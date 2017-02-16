package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static home.tetris.Globals.SQ_SIZE;

/**
 * Created by Дима on 23.01.2017.
 * Служит для описания игрового поля (положение всех тетрамино на поле)
 * Описывает их движение и повороты.
 * Удаляет заполненые линии ведет учет текущих уровеня и очков.
 *
 */

class Scene extends View
{
    private static final int TIMER_INTERVAL = 30;

    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private Random random;
    private Sound sound;
    private boolean gameOver = false;
    private boolean running = false;
    private boolean waitUntilFall = false;
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
        random = new Random();
        paint = new Paint();
        paint.setStrokeWidth(1);
        new CountDownTimer(Long.MAX_VALUE, TIMER_INTERVAL){
            @Override
            public void onFinish() {}

            @Override
            public void onTick(long millisUntilFinished){
                if(waitUntilFall) return;

                if(running) {
                    moveCurrentDown(0);
                    invalidate();
                }
            }
        }.start();
    }

    private int getColor()
    {
        int k = Integer.MAX_VALUE;
        return Color.argb(random.nextInt(k), random.nextInt(k), random.nextInt(k), random.nextInt(k));
    }
// Создает новое тетрамино за пределами экрана, все параметры выбираются случайно
    private void newMino()
    {
        Type type = Tetramino.intToType(random.nextInt(19));
        int leftPos = 0;
        int topPos = 0;
        int color = getColor();
        switch (type)
        {
            case tLineHorizontal:
                    topPos = -SQ_SIZE;
                    leftPos = random.nextInt(Globals.WIDTH - (Globals.MAX_BLOCK_CNT * SQ_SIZE));
                    break;
            case tLineVertical:
                    topPos = -(Globals.MAX_BLOCK_CNT * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - SQ_SIZE);
                    break;
            case tSquare:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                break;
            case tL0:  case tL180: case tLR0: case tLR180:
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                break;
            case tL90: case tL270: case tLR90: case tLR270:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
                break;
            case tT0: case tT180:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
                break;
            case tT90: case tT270:
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                break;
            case tZ0: case tRZ0:
                    topPos = -(3 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (2 * SQ_SIZE));
                break;
            case tZ180: case tRZ180:
                    topPos = -(2 * SQ_SIZE);
                    leftPos = random.nextInt(Globals.WIDTH - (3 * SQ_SIZE));
        }
        leftPos = (leftPos >= SQ_SIZE)? (leftPos / SQ_SIZE) * SQ_SIZE: 0;
        sceneList.add(new Tetramino(type, leftPos, topPos, color));
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
        Type type = currentMino.getType();
        switch(type)
        {
            case tLineHorizontal: type = Type.tLineVertical; break;
            case tLineVertical: type = Type.tLineHorizontal; break;
            case tSquare: return;
            case tL0: type = Type.tL90; break;
            case tL90: type = Type.tL180; break;
            case tL180: type = Type.tL270; break;
            case tL270: type = Type.tL0; break;
            case tLR0: type = Type.tLR90; break;
            case tLR90: type = Type.tLR180; break;
            case tLR180: type = Type.tLR270; break;
            case tLR270: type = Type.tLR0; break;
            case tT0: type = Type.tT90; break;
            case tT90: type = Type.tT180; break;
            case tT180: type = Type.tT270; break;
            case tT270: type = Type.tT0; break;
            case tZ0: type = Type.tZ180; break;
            case tZ180: type = Type.tZ0; break;
            case tRZ0: type = Type.tRZ180;break;
            case tRZ180: type = Type.tRZ0;
        }

        sound.play(Globals.ROTATE);
        Tetramino mino = new Tetramino(type, currentMino.getMinLeft(), currentMino.getMinTop(), currentMino.getColor());

        if(!collisionRotate(mino, currentMino))
        {
            sceneList.remove(currentMino);
            sceneList.add(mino);
            currentMino = mino;
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
                        Log.d("Scene", "block count " + counter);
                        return true;
                    }
                }
            }
        }
        Log.d("Scene", "block count " + counter);
        return false;
    }

    private void deleteLine(int bottom)
    {
        if(bottom == 0) return;
        int i = 0;
        while(i <= sceneList.size() - 1)
        {
            Tetramino tetramino = sceneList.get(i);
            int counter = 0;
            for(Rect block: tetramino.getBlocks())
            {
                if(block == null) {
                    counter++;
                    continue;
                }

                if (block.bottom == bottom) {
                        tetramino.replaceBlock(null, block);
                }
            }
            if(counter == Globals.MAX_BLOCK_CNT) {
                sceneList.remove(tetramino);
            } else i++;
        }
    }

    private void fallSquares(int bottom)
    {
        waitUntilFall = true;
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
        waitUntilFall = false;
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

    private void deleteFullLines()
    {
        int bottom = Globals.HEIGHT;
        boolean enablePlay = true;

        while(bottom > 0)
        {
            if(lineIsEmpty(bottom)) return;

            while(lineIsFull(bottom))
            {
               if(enablePlay){
                   sound.play(Globals.DELETE_LINE);
                   enablePlay = false;
               }
               deleteLine(bottom);
               fallSquares(bottom);
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
        for (int k = level + 2 + speedInc; k > 0; k--) {
            if(!waitUntilFall) {
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
