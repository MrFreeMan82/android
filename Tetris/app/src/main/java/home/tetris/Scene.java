package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 23.01.2017.
 * Служит для описания игрового поля (положение всех тетрамино на поле)
 * Описывает их движение и повороты.
 * Удаляет заполненые линии ведет учет текущих уровеня и очков.
 * Также выполняет отрисовку сцены.
 *
 */

class Scene extends View implements TetrisAnimator.Callback
{
    public static final int BLOCKS_PER_WIDTH = 12;
    public static int WIDTH;
    public static int HEIGHT;

    private static final int SCORE_PER_LEVEL = 25;
    private static final int TIMER_INTERVAL = 30;
    private static final int TETRAMINO_TOTAL = 19;

    private List<Tetramino> sceneList;
    private TetrisAnimator tetrisAnimator;
    private Tetramino currentMino;
    private Paint paint;
    private Sound sound;
    private boolean gameOver = false;
    private boolean running = false;
    private int score = 0;
    private int level = 1;
    private Callback callback;

    interface Callback{
        void onScoreChange(int score);
        void onLevelUp(int level);
        void onGameOver();
    }

    void start(){
        if(running) return;
        if(currentMino == null) newMino();
        running = true;
    }

    void pause(){
        running = false;
    }

    void stop(){
        running = false;
        clear();
        tetrisAnimator.clear();
        invalidate();
    }

    Scene(Context context)
    {
        super(context);
        callback = (Callback) context;
        sound = new Sound(context);
        sceneList = new ArrayList<>();
        tetrisAnimator = new TetrisAnimator(this);
        paint = new Paint();
        paint.setStrokeWidth(1);
        new CountDownTimer(Long.MAX_VALUE, TIMER_INTERVAL){
            @Override
            public void onFinish() {}

            @Override
            public void onTick(long millisUntilFinished){

                if(running) {
                    moveCurrentDown(0);
                    invalidate();
                }
            }
        }.start();
    }

// Создает новое тетрамино за пределами экрана, все параметры выбираются случайно
    private void newMino()
    {
        switch ((int) (Math.random() * TETRAMINO_TOTAL))
        {
            case 0: sceneList.add(new LineHorizontal()); break;
            case 1: sceneList.add(new LineVertical()); break;
            case 2: sceneList.add(new Square()); break;
            case 3: sceneList.add(new L0());   break;
            case 4: sceneList.add(new L90());  break;
            case 5: sceneList.add(new L180()); break;
            case 6: sceneList.add(new L270()); break;
            case 7: sceneList.add(new LR0()); break;
            case 8: sceneList.add(new LR90()); break;
            case 9: sceneList.add(new LR180()); break;
            case 10: sceneList.add(new LR270()); break;
            case 11: sceneList.add(new T0()); break;
            case 12: sceneList.add(new T90()); break;
            case 13: sceneList.add(new T180()); break;
            case 14: sceneList.add(new T270()); break;
            case 15: sceneList.add(new Z0()); break;
            case 16: sceneList.add(new Z180()); break;
            case 17: sceneList.add(new RZ0()); break;
            case 18: sceneList.add(new RZ180());
        }
        currentMino = sceneList.get(sceneList.size() - 1);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        for(Tetramino tetramino:sceneList)
        {
            paint.setColor(tetramino.getColor());

            for(Rect block:tetramino.getBlocks())
                if(block != null) canvas.drawRect(block, paint);
        }
    }

    void rotateCurrent()
    {
        if(gameOver) return;

        Tetramino tetramino = currentMino.rotate();
        if(tetramino == null) return;

        if(!collisionRotate(tetramino, currentMino)){
            sound.play(Sound.ROTATE);
            sceneList.remove(currentMino);
            sceneList.add(tetramino);
            currentMino = tetramino;
        }
    }

    private Tetramino[] getNextFullLine(int bottom)
    {
        int counter = 0;
        Tetramino[] result = new Tetramino[BLOCKS_PER_WIDTH];
        for(Tetramino current: sceneList)
        {
            for(Rect block: current.getBlocks())
            {
                if(block == null) continue;
                if(block.bottom == bottom)
                {
                    result[counter] = current;
                    counter++;
                    if(counter == BLOCKS_PER_WIDTH) return result;
                }
            }
        }
        return null;
    }

    private void fallSquares(int bottom)
    {
        for(Tetramino tetramino: sceneList)
        {
            for (Rect block: tetramino.getBlocks())
            {
                if(block == null) continue;
                if(block.bottom < bottom)
                {
                    block.top = block.bottom;
                    block.bottom += Tetramino.SQ_SIZE;
                }
            }
        }
    }

    private boolean lineIsEmpty(int bottom)
    {
        for(Tetramino tetramino: sceneList){
            for(Rect block: tetramino.getBlocks()){
                if(block == null)continue;
                if(block.bottom == bottom) return false;
            }
        }
        return true;
    }

    @Override
    public void onDeleteBlock(Tetramino tetramino, int column){

        int counter = 0;
        for(Rect block: tetramino.getBlocks()){
            if(block == null) counter++;
        }

        if(counter == Tetramino.MAX_BLOCK_CNT) sceneList.remove(tetramino);
        invalidate();
    }

    @Override
    public void onDeleteComplete(){
      //  fallSquares(line);
    }

    private void deleteFullLines()
    {
        int bottom = HEIGHT;
        boolean enablePlay = true;

        while(bottom > 0)
        {
            if(lineIsEmpty(bottom)) return;

            Tetramino[] line = getNextFullLine(bottom);
            if(line != null)
            {
               if(enablePlay){
                   sound.play(Sound.DELETE_LINE);
                   enablePlay = false;
               }

               tetrisAnimator.addDeleteLineAnimation(line, bottom);
               score++;
               callback.onScoreChange(score);

               if((score % SCORE_PER_LEVEL) == 0){
                   level++;
                   sound.play(Sound.LEVEL_UP);
                   callback.onLevelUp(level);
               }
            }
            bottom -= Tetramino.SQ_SIZE;
        }
    }

    void moveCurrentDown(int speedInc)
    {
        if(gameOver) return;
        if(speedInc != 0) sound.play(Sound.MOVE_MINO);
        for (int k = level + 2 + speedInc; k > 0; k--) {

            if (collisionBottom(currentMino)) {
                if (collisionUp(currentMino)) {
                    gameOver = true;
                    callback.onGameOver();
                    return;
                }
                sound.play(Sound.IMPACT);
                deleteFullLines();
                newMino();
            }
            currentMino.moveDown();
        }
    }

    void moveCurrentLeft()
    {
        if(gameOver) return;
        if(collisionLeft(currentMino)) return;
        sound.play(Sound.MOVE_MINO);
        currentMino.moveLeft();
    }

    void moveCurrentRight()
    {
        if(gameOver) return;
        if(collisionRight(currentMino)) return;
        sound.play(Sound.MOVE_MINO);
        currentMino.moveRight();
    }

    private boolean collisionUp(Tetramino mino)
    {
        for(Rect current: mino.getBlocks()) {
            if(current == null) continue;
            if (current.top < 0) return true;
        }
        return false;
    }

    private boolean collisionBottom(Tetramino current){
        for(Rect block: current.getBlocks())
        {
            if(block == null) continue;
            if(block.bottom == HEIGHT) return true;
            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Rect prev: tetramino.getBlocks())
                {
                    if(prev == null) continue;
                    if(block.bottom == prev.top && block.left == prev.left) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionLeft(Tetramino current){
       for(Rect block: current.getBlocks())
       {
           if(block == null) continue;
           if(block.left <= 0) return true;

           for(Tetramino tetramino: sceneList)
           {
               if(tetramino == current) continue;

               for(Rect prev: tetramino.getBlocks())
               {
                   if(prev == null) continue;
                   if(block.left == prev.right &&
                           block.bottom >= prev.top &&
                                block.bottom <= prev.bottom) return true;
               }
           }
       }
       return false;
    }

    private boolean collisionRight(Tetramino current)
    {
        for(Rect block: current.getBlocks())
        {
            if(block == null) continue;
            if(block.right >= WIDTH) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Rect prev: tetramino.getBlocks())
                {
                    if(prev == null) continue;
                    if(block.right == prev.left &&
                            block.bottom >= prev.top &&
                                block.bottom <= prev.bottom) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionRotate(Tetramino newMino, Tetramino current){

        for(Rect newBlock: newMino.getBlocks())
        {
            if((newBlock.left < 0) ||
                    (newBlock.right > WIDTH) ||
                        (newBlock.bottom > HEIGHT)) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;

                for(Rect prev: tetramino.getBlocks())
                {
                    if(prev == null) continue;
                    if((newBlock.top >= prev.top && newBlock.top <= prev.bottom) ||
                            (newBlock.bottom >= prev.top && newBlock.bottom <= prev.bottom))
                    {
                        if ((newBlock.left == prev.left) || (newBlock.right == prev.right)) return true;
                    }
                }
            }
        }
        return false;
    }

    void clear()
    {
        sceneList.clear();
        gameOver = false;
        score = 0;
        callback.onScoreChange(score);
        level = 1;
        currentMino = null;
    }
}
