package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
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

class Scene extends View
{
    public static final int BLOCKS_PER_WIDTH = 12;
    public static int WIDTH;
    public static int HEIGHT;

    //private static final String TAG = "Scene";
    private static final int SCORE_PER_LEVEL = 25;
    private static final int TIMER_INTERVAL = 30;
    private static final int TETRAMINO_TOTAL = 19;

    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private Sound sound;
    private DeletingAnimation deletingAnimation;
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
        callback.onScoreChange(score);
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
        Handler responseHandler = new Handler();
        deletingAnimation = new DeletingAnimation(responseHandler, this);
        deletingAnimation.setBarDeleteListener(new DeletingAnimation.BarDeleteListener()
        {
            @Override
            public void onBarDelete()
            {
                score ++;
                callback.onScoreChange(score);

                if ((score % SCORE_PER_LEVEL) == 0) {
                    level++;
                    sound.play(Sound.LEVEL_UP);
                    callback.onLevelUp(level);
                }
            }

            @Override
            public void onDeleteComplete()
            {
                clearEmptyTetraminos();
            }

            @Override
            public void onRepaint(){invalidate();}
        });
        deletingAnimation.start();
        deletingAnimation.getLooper();

        paint.setStrokeWidth(1);
        new CountDownTimer(Long.MAX_VALUE, TIMER_INTERVAL){
            @Override
            public void onFinish() {}

            @Override
            public void onTick(long millisUntilFinished)
            {
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
        int type = (int) (Math.random() * TETRAMINO_TOTAL);
        sceneList.add(Tetramino.intToTetramino(type));
        currentMino = sceneList.get(sceneList.size() - 1);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        for(Tetramino tetramino:sceneList)
        {
            paint.setColor(tetramino.getColor());

            for(Block block: tetramino.getBlocks())
                if(block.active){
                    canvas.drawRect(block.subRect, paint);
                }
        }
    }

    void rotateCurrent()
    {
        if(gameOver) return;

        Tetramino tetramino = currentMino.rotate();
        if(tetramino == null) return;

        if(!collisionRotate(tetramino, currentMino))
        {
            sound.play(Sound.ROTATE);
            sceneList.remove(currentMino);
            sceneList.add(tetramino);
            currentMino = tetramino;
        }
    }

    private void clearEmptyTetraminos()
    {
        int i = 0;
        while(i < sceneList.size())
        {
            int counter = 0;
            Tetramino next = sceneList.get(i);
            for(Block block: next.getBlocks()) if(!block.active) counter++;

            if(counter == Tetramino.MAX_BLOCK_CNT) sceneList.remove(next); else i++;
        }
    }

    void moveCurrentDown(int speedInc)
    {
        if(gameOver) return;
        if(speedInc != 0) sound.play(Sound.MOVE_MINO);
        for (int k = level + 2 + speedInc; k > 0; k--)
        {
            if (collisionBottom(currentMino))
            {
                if (collisionUp(currentMino))
                {
                    gameOver = true;
                    callback.onGameOver();
                    return;
                }
                sound.play(Sound.IMPACT);
                deletingAnimation.deleteFullLines();
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
        for(Block current: mino.getBlocks())
            if (current.active && current.rect.top < 0) return true;

        return false;
    }

    boolean collisionBottom(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            if(block.active && block.rect.bottom == HEIGHT) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.active &&
                            block.rect.bottom == prev.rect.top &&
                                block.rect.left == prev.rect.left) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionLeft(Tetramino current)
    {
       for(Block block: current.getBlocks())
       {
           if(block.active && block.rect.left <= 0) return true;

           for(Tetramino tetramino: sceneList)
           {
               if(tetramino == current) continue;

               for(Block prev: tetramino.getBlocks())
               {
                   if(prev.active &&
                           block.rect.left == prev.rect.right &&
                                block.rect.bottom >= prev.rect.top &&
                                    block.rect.bottom <= prev.rect.bottom) return true;
               }
           }
       }
       return false;
    }

    private boolean collisionRight(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            if(block.active && block.rect.right >= WIDTH) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.active &&
                            block.rect.right == prev.rect.left &&
                                block.rect.bottom >= prev.rect.top &&
                                    block.rect.bottom <= prev.rect.bottom) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionRotate(Tetramino newMino, Tetramino current)
    {
        for(Block newBlock: newMino.getBlocks())
        {
            if((newBlock.rect.left < 0) ||
                    (newBlock.rect.right > WIDTH) ||
                        (newBlock.rect.bottom > HEIGHT)) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;

                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.active)
                    {
                        if ((newBlock.rect.top >= prev.rect.top &&
                                    newBlock.rect.top <= prev.rect.bottom) ||
                                (newBlock.rect.bottom >= prev.rect.top &&
                                        newBlock.rect.bottom <= prev.rect.bottom))
                        {
                            if ((newBlock.rect.left == prev.rect.left) ||
                                    (newBlock.rect.right == prev.rect.right)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    int getLevel(){return level;}

    Tetramino getCurrentMino(){return currentMino;}

    List<Tetramino> getSceneList(){return sceneList;}

    Sound getSound(){return sound;}

    void clear()
    {
        sceneList.clear();
        gameOver = false;
        score = 0;
        callback.onScoreChange(score);
        level = 1;
        currentMino = null;
    }

    void free()
    {
        deletingAnimation.quit();
    }
}
