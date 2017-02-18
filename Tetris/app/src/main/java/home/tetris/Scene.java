package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static home.tetris.Globals.SQ_SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Служит для описания игрового поля (положение всех тетрамино на поле)
 * Описывает их движение и повороты.
 * Удаляет заполненые линии ведет учет текущих уровеня и очков.
 * Также выполняет отрисовку сцены.
 *
 */

class Scene extends View implements DeleteLineAnimation.Callback
{
    private static final int TIMER_INTERVAL = 30;

    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private Sound sound;
    private boolean gameOver = false;
    private boolean running = false;
    private boolean enableDeleteLine = true;
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
        invalidate();
    }

    Scene(Context context)
    {
        super(context);
        callback = (Callback) context;
        sound = new Sound(context);
        sceneList = new ArrayList<>();
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
        int type = (int) (Math.random() * 19);

        switch (type)
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
            sound.play(Globals.ROTATE);
            sceneList.remove(currentMino);
            sceneList.add(tetramino);
            currentMino = tetramino;
        }
    }

    private boolean lineIsFull(int bottom)
    {
        int counter = 0;
        for(Tetramino current: sceneList)
        {
            for(Rect block: current.getBlocks())
            {
                if(block == null) continue;
                if(block.bottom == bottom){
                    counter++;
                    if(counter == Globals.BLOCKS_PER_WIDTH){
                        return true;
                    }
                }
            }
        }
        return false;
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
                    block.bottom += SQ_SIZE;
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
    public void onDeleteBlock(Tetramino tetramino){

        int counter = 0;
        for(Rect block: tetramino.getBlocks()){
            if(block == null) counter++;
        }

        if(counter == Globals.MAX_BLOCK_CNT) sceneList.remove(tetramino);
        invalidate();
    }

    @Override
    public void onDeleteComplete(int line){
        fallSquares(line);
        enableDeleteLine = true;
    }

    @Override
    public void onBeginDelete(){
        enableDeleteLine = false;
    }

    private void deleteFullLines()
    {
        int bottom = Globals.HEIGHT;
        boolean enablePlay = true;

        while(bottom > 0)
        {
            if(lineIsEmpty(bottom)) return;

            if(lineIsFull(bottom))
            {
               if(enablePlay){
                   sound.play(Globals.DELETE_LINE);
                   enablePlay = false;
               }


               new DeleteLineAnimation(sceneList, this, bottom);
               score++;
               callback.onScoreChange(score);

               if((score % Globals.SCORE_PER_LEVEL) == 0){
                   level++;
                   sound.play(Globals.LEVEL_UP);
                   callback.onLevelUp(level);
               }
            }
            bottom -= SQ_SIZE;
        }
    }

    void moveCurrentDown(int speedInc)
    {
        if(gameOver) return;
        if(speedInc != 0) sound.play(Globals.MOVE_MINO);
        for (int k = level + 2 + speedInc; k > 0; k--) {

            if (collisionBottom(currentMino)) {
                if (collisionUp(currentMino)) {
                    gameOver = true;
                    callback.onGameOver();
                    return;
                }
                sound.play(Globals.IMPACT);
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
        sound.play(Globals.MOVE_MINO);
        currentMino.moveLeft();
    }

    void moveCurrentRight()
    {
        if(gameOver) return;
        if(collisionRight(currentMino)) return;
        sound.play(Globals.MOVE_MINO);
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
            if(block.bottom == Globals.HEIGHT) return true;
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
            if(block.right >= Globals.WIDTH) return true;

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
                    (newBlock.right > Globals.WIDTH) ||
                        (newBlock.bottom > Globals.HEIGHT)) return true;

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
