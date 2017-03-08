package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
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

interface GameListener{
    void onScoreChange(int score);
    void onLevelUp(int level);
    void onGameOver();
}

class Scene extends View
{
    static final int SCREEN_DELTA = 500;
    static final int BLOCKS_PER_WIDTH = 12;          // Определяет колл-во блоков по ширине
    private static int width;                        // Доступная ширина
    private static int height;                       // Доступная высота
    private int fallSpeedIncrement;         // Определяет скорость падения тетрамино
                                            // когда пальцем проводим вниз,
                                            // эта веречина меняется в зависимости от разрешения экрана

    private static final int SCORE_PER_LEVEL = 25;   // Через сколько очков переходим на уровень выше.
    private static final int TIMER_INTERVAL = 30;    // Интервал таймер выбран методом подбора.

    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private Sound sound;
    private Background background;
    private DeleteAnimation deleteAnimation;
    private boolean gameOver = false;
    private boolean running = false;
    private boolean cancel = false;
    private int score = 0;
    private int hi_score = 0;
    private int level = 1;
    private GameListener listener;

    void setGameListener(GameListener aListener){listener = aListener;}
    void setSound(Sound aSound){sound = aSound;}

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
        background.setCancel(true);
    }

    Scene(Context context)
    {
        super(context);
        hi_score = Settings.getIntSetting(Settings.APP_SETTING_HISCORE, 0);
        paint = new Paint();
        sceneList = new ArrayList<>();
        deleteAnimation = new DeleteAnimation(this);
        deleteAnimation.setBarDeleteListener(new BarDeleteListener()
        {
            @Override
            public void onDeleteComplete(int total)
            {
                score += total;
                listener.onScoreChange(score);

                if(score > hi_score){
                    hi_score = score;
                    Settings.setIntSetting(Settings.APP_SETTING_HISCORE, hi_score);
                }

                if (score >= level * SCORE_PER_LEVEL) {
                    level++;
                    sound.play(Sound.LEVEL_UP);
                    listener.onLevelUp(level);
                }
            }
        });

        new CountDownTimer(Long.MAX_VALUE, TIMER_INTERVAL)
        {
            @Override
            public void onFinish() {}

            @Override
            public void onTick(long millisUntilFinished)
            {
                if(cancel) this.cancel();

                if(running)
                {
                    moveCurrentDown(0);
                    invalidate();
                }
            }
        }.start();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        paint.setColor(Background.MOON_COLOR);
        canvas.drawCircle(background.moon.x, background.moon.y, Background.MOON_RADIUS, paint);

        for(Tetramino tetramino:sceneList)
        {
            for(Block block: tetramino.getBlocks())
                if(block.isVisible())
                {
                    paint.setColor(tetramino.getColor());
                    canvas.drawRect(block.getRect(), paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(block.getMid(), paint);
                    paint.setColor(tetramino.getColor());
                    canvas.drawRect(block.getSubRect(), paint);

                    paint.setStrokeWidth(2);
                    paint.setColor(Color.WHITE);
                    Point p1 = block.getP1();
                    Point p2 = block.getP2();
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                }
        }

        for(Point star : background.stars)
        {
            paint.setColor(Background.STAR_COLOR);
            paint.setStrokeWidth(3);
            canvas.drawPoint(star.x, star.y, paint);
        }
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
        fallSpeedIncrement = 50 * (height / SCREEN_DELTA);

        background = new Background();
        background.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        start();
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
            for(Block block: next.getBlocks()) if(!block.isVisible()) counter++;

            if(counter == Tetramino.MAX_BLOCK_CNT) sceneList.remove(next); else i++;
        }
    }

    private void newMino()
    {
        sceneList.add(Tetramino.next());
        currentMino = sceneList.get(sceneList.size() - 1);
    }

    void moveCurrentDown(int speedInc)
    {
        if(gameOver) return;
        if(currentMino == null) newMino();

        if(speedInc != 0) sound.play(Sound.MOVE_MINO);

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
                sound.play(Sound.IMPACT);
                clearEmptyTetraminos();
                deleteAnimation.deleteFullLines();
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
            if (current.isVisible() && current.getRect().top < 0) return true;

        return false;
    }

   private boolean collisionBottom(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            if(block.isVisible() && block.getRect().bottom == height) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.isVisible() &&
                            block.getRect().bottom == prev.getRect().top &&
                            block.getRect().left == prev.getRect().left) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionLeft(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            if(block.isVisible() && block.getRect().left <= 0) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;

                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.isVisible() &&
                            block.getRect().left == prev.getRect().right &&
                            block.getRect().bottom >= prev.getRect().top &&
                            block.getRect().bottom <= prev.getRect().bottom) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionRight(Tetramino current)
    {
        for(Block block: current.getBlocks())
        {
            if(block.isVisible() && (width - block.getRect().right) < Block.SQ_SIZE) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;
                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.isVisible() &&
                            block.getRect().right == prev.getRect().left &&
                            block.getRect().bottom >= prev.getRect().top &&
                            block.getRect().bottom <= prev.getRect().bottom) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionRotate(Tetramino newTetramino, Tetramino current)
    {
        for(Block newBlock: newTetramino.getBlocks())
        {
            if((newBlock.getRect().left < 0) ||
                    (newBlock.getRect().right > width) ||
                    (newBlock.getRect().bottom > height)) return true;

            for(Tetramino tetramino: sceneList)
            {
                if(tetramino == current) continue;

                for(Block prev: tetramino.getBlocks())
                {
                    if(prev.isVisible())
                    {
                        if ((newBlock.getRect().top >= prev.getRect().top &&
                                newBlock.getRect().top <= prev.getRect().bottom) ||
                                (newBlock.getRect().bottom >= prev.getRect().top &&
                                        newBlock.getRect().bottom <= prev.getRect().bottom))
                        {
                            if ((newBlock.getRect().left == prev.getRect().left) ||
                                    (newBlock.getRect().right == prev.getRect().right)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static int getWIDTH(){return width;}
    static int getHEIGHT(){return height;}
    int getFallSpeedIncrement(){return fallSpeedIncrement;}
    int getHi_score(){return hi_score;}
    Tetramino getCurrentMino(){return currentMino;}
    List<Tetramino> getSceneList(){return sceneList;}
    Sound getSound(){return sound;}

    private void clear()
    {
        sceneList.clear();
        gameOver = false;
        score = 0;
        listener.onScoreChange(score);
        level = 1;
        currentMino = null;
    }
}
