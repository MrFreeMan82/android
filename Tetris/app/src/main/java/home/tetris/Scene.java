package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.View;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Дима on 23.01.2017.
 * Служит для описания игрового поля (положение всех тетрамино на поле)
 * Описывает их движение и повороты.
 * Удаляет заполненые линии ведет учет текущих уровеня и очков.
 * Также выполняет отрисовку сцены.
 *
 */

interface GameListener{
    void onScoreChange(int score);
    void onLevelUp(int level);
    void onGameOver();
}

final class Scene extends View
{
    static final int SCREEN_DELTA = 500;
    static final int BLOCKS_PER_WIDTH = 12;                         // Определяет колл-во блоков по ширине
    static final int WIDTH = MainActivity.getSceneWidth();          // Доступная ширина
    static final int HEIGHT = MainActivity.getSceneHeight();       // Доступная высота
    static final int BLOCKS_PER_HEIGHT = HEIGHT / (WIDTH / BLOCKS_PER_WIDTH);
    static final int FALL_INCREMENT = HEIGHT / SCREEN_DELTA * 50;

    private static final int SCORE_PER_LEVEL = 25;   // Через сколько очков переходим на уровень выше.
    private static final int TIMER_INTERVAL = 20;    // Интервал таймер.

    private Block[][] field;
    private Tetramino currentMino;
    private Paint paint;
    private Background background;
    private DeleteAnimation deleteAnimation;
    private boolean gameOver = false;
    private boolean running = false;
    private boolean cancel = false;
    private int score = 0;
    private int hi_score = 0;
    private int level = 1;
    private GameListener listener;
    private Future<Integer> totalDeleted;

    void setGameListener(GameListener aListener){listener = aListener;}

    void start(){
        if(running) return;
        listener.onLevelUp(level);
        listener.onScoreChange(score);
        running = true;
        background.setPause(false);
    }

    void pause(){
        running = false;
        background.setPause(true);
    }

    void stop(){
        running = false;
        background.setPause(true);
        clear();
        invalidate();
    }

    void free()
    {
        running = false;
        clear();
        cancel = true;
        background.cancel();
    }

    Scene(Context context)
    {
        super(context);
        hi_score = Settings.getIntSetting(Settings.APP_SETTING_HISCORE, 0);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        field = new Block[BLOCKS_PER_WIDTH][BLOCKS_PER_HEIGHT];
        deleteAnimation = new DeleteAnimation(this);

        new CountDownTimer(Long.MAX_VALUE, TIMER_INTERVAL)
        {
            @Override public void onFinish() {}

            @Override public void onTick(long millisUntilFinished)
            {
                if(cancel) this.cancel();

                if(running)
                {
                    if(hasDeletedLines()) updateScoreAndLevel(getTotalDeletedLines());
                    moveCurrentDown(0);
                    invalidate();
                }
            }
        }.start();

        background = new Background();
        MainActivity.execute(background);
    }

    @Override public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        background.draw(canvas, paint);

        if(currentMino != null) currentMino.draw(canvas, paint);

        for(Block[] blocks: field)
        {
            for (Block block: blocks)
                if(block != null) block.draw(canvas, paint);
        }
    }

    private int getTotalDeletedLines()
    {
        int total = 0;
        try {
            total = totalDeleted.get();
            totalDeleted = null;
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        return total;
    }

    private boolean hasDeletedLines(){return (totalDeleted != null && totalDeleted.isDone());}

    private void updateScoreAndLevel(int totalDeleted)
    {
        score += totalDeleted;
        listener.onScoreChange(score);

        if(score > hi_score){
            hi_score = score;
            Settings.setIntSetting(Settings.APP_SETTING_HISCORE, hi_score);
        }

        if (score >= level * SCORE_PER_LEVEL) {
            level++;
            Sound.play(Sound.LEVEL_UP);
            listener.onLevelUp(level);
        }
    }

    void rotateCurrent()
    {
        if(gameOver) return;

        Tetramino tetramino = currentMino.rotate();
        if(tetramino == null) return;

        if(!collisionRotate(tetramino))
        {
            Sound.play(Sound.ROTATE);
            currentMino = tetramino;
        }
    }

    void putTetramino(Tetramino tetramino)
    {
        for (Block block: tetramino.getBlocks())
        {
            int x = block.rect.left / Block.SIZE;
            int y = block.rect.top / Block.SIZE;

            if(y >= 0 && block.visible) field[x][y] = block;
        }
    }

    void moveCurrentDown(int speedInc)
    {
        if(gameOver) return;
        if(currentMino == null) currentMino = Tetramino.Type.newInstance();

        if(speedInc != 0) Sound.play(Sound.MOVE_MINO);

        for (int k = level + 2 + speedInc; k > 0; k--)
        {
            if (collisionBottom(currentMino))
            {
                if (collisionUp(currentMino))
                {
                    gameOver = true;
                    listener.onGameOver();
                    return;
                }
                Sound.play(Sound.IMPACT);
                putTetramino(currentMino);
                totalDeleted = deleteAnimation.deleteFullLines();
                currentMino = Tetramino.Type.newInstance();
            }

            currentMino.moveDown();
        }
    }

    void moveCurrentLeft()
    {
        if(gameOver) return;
        if(collisionLeft(currentMino)) return;
        Sound.play(Sound.MOVE_MINO);
        currentMino.moveLeft();
    }

    void moveCurrentRight()
    {
        if(gameOver) return;
        if(collisionRight(currentMino)) return;
        Sound.play(Sound.MOVE_MINO);
        currentMino.moveRight();
    }

    private boolean collisionUp(Tetramino mino)
    {
        for(Block current: mino.getBlocks())
            if (current.visible && current.rect.top < 0) return true;

        return false;
    }

    private boolean collisionBottom(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            int x = block.rect.left / Block.SIZE;
            int y = block.rect.top / Block.SIZE;

            if(HEIGHT - block.rect.bottom < 1) return true;
            if(y < 0) continue;
            while (y < BLOCKS_PER_HEIGHT)
            {
                if(field[x][y] != null && field[x][y].visible &&
                        field[x][y].rect.top == block.rect.bottom) return true;
                y++;
            }
        }
        return false;
    }

    private boolean collisionLeft(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            int x = block.rect.left / Block.SIZE;
            int y = block.rect.top / Block.SIZE;

            if(x == 0) return true;
            if((y < 0) || (y == BLOCKS_PER_HEIGHT)) continue;
            while (x >= 0)
            {
                if (field[x][y] != null && field[x][y].visible &&
                        field[x][y].rect.right == block.rect.left) return true;
                x--;
            }
        }
        return false;
    }

    private boolean collisionRight(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            int x = block.rect.left / Block.SIZE;
            int y = block.rect.top / Block.SIZE;

            if(WIDTH - block.rect.right < 1) return true;
            if((y < 0) || (y == BLOCKS_PER_HEIGHT)) continue;
            while (x < BLOCKS_PER_WIDTH)
            {
                if (field[x][y] != null && field[x][y].visible &&
                        field[x][y].rect.left == block.rect.right) return true;

                x++;
            }
        }
        return false;
    }

    private boolean collisionRotate(Tetramino newTetramino)
    {
        for(Block block: newTetramino.getBlocks())
        {
            int x = block.rect.left / Block.SIZE;
            int y = block.rect.top / Block.SIZE;

            if((x < 0) || (x >= BLOCKS_PER_WIDTH) ||
                    (y >= BLOCKS_PER_HEIGHT)) return true;

            if((y < 0) || (field[x][y] == null)) continue;

            if((block.rect.left >= field[x][y].rect.left &&
                    block.rect.left <= field[x][y].rect.right) ||
                    (block.rect.top >= field[x][y].rect.top &&
                            block.rect.top <= field[x][y].rect.bottom)) return true;
        }
        return false;
    }

    Block[][] getField(){return field;}
    int getHi_score(){return hi_score;}

    private void clear()
    {
        gameOver = false;
        score = 0;
        listener.onScoreChange(score);
        level = 1;
        currentMino = null;
        field = new Block[BLOCKS_PER_WIDTH][BLOCKS_PER_HEIGHT];
    }
}
