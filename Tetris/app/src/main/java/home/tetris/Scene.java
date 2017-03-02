package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
    static final int BLOCKS_PER_WIDTH = 12;          // Определяет колл-во блоков по ширине
    static int WIDTH;                                       // Доступная ширина
    static int HEIGHT;                                      // Доступная высота
    static int FALL_SPEED_INCREMENT = 100;                  // Определяет скорость падения тетрамино
                                                            // когда пальцем проводим вниз
                                                            // эта веречина меняется в зависимости от разрешения экрана
    private static final int SCORE_PER_LEVEL = 25;          // Через сколько очков переходим на уровень выше.
    private static final int TIMER_INTERVAL = 30;           // Интервал таймер выбран методом подбора.
    private static final int TETRAMINO_TOTAL = 19;          // Кол-во тетрамоно(сюда входят и повороты)

    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private Paint paint;
    private Sound sound;
    private Background background;
    private DeletingAnimation deletingAnimation;
    private boolean gameOver = false;
    private boolean running = false;
    private boolean cancel = false;
    private int score = 0;
    private int hi_score = 0;
    private int level = 1;
    private Callback callback;

    interface Callback{
        void onScoreChange(int score);
        void onLevelUp(int level);
        void onGameOver();
    }

    void start(){
        if(running) return;
        callback.onLevelUp(level);
        callback.onScoreChange(score);
        if(currentMino == null) newMino();
        running = true;
    }

    void pause(){running = false;}

    void stop(){
        running = false;
        clear();
        invalidate();
    }

    Scene(Context context)
    {
        super(context);
        hi_score = Settings.getIntSetting(Settings.APP_SETTING_HISCORE, 0);
        callback = (Callback) context;
        sound = new Sound(context);
        sceneList = new ArrayList<>();
        paint = new Paint();
        background = new Background();
        Handler responseHandler = new Handler();
        deletingAnimation = new DeletingAnimation(responseHandler, this);
        deletingAnimation.setBarDeleteListener(new DeletingAnimation.BarDeleteListener()
        {
            @Override
            public void onDeleteComplete(int total)
            {
                score += total;
                callback.onScoreChange(score);

                if(score > hi_score){
                    hi_score = score;
                    Settings.setIntSetting(Settings.APP_SETTING_HISCORE, hi_score);
                }

                if (score >= level * SCORE_PER_LEVEL) {
                    level++;
                    sound.play(Sound.LEVEL_UP);
                    callback.onLevelUp(level);
                }
                deletingAnimation.falling(total);
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
                if(cancel) this.cancel();

                if(running)
                {
                    if(Background.INTERVAL > 50) {
                        background.moveMoon();
                        background.moveStars();
                        Background.INTERVAL = 0;
                    }
                    Background.INTERVAL += TIMER_INTERVAL;
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
    public void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        if((w != oldw) || (h != oldh)) {
            Scene.WIDTH = w;
            Scene.HEIGHT = h;
            Tetramino.SQ_SIZE = Scene.WIDTH / Scene.BLOCKS_PER_WIDTH;

            FALL_SPEED_INCREMENT = 50 * (Scene.HEIGHT / 500);
            Block.DELTA = 5 * (Scene.HEIGHT / 500);
            Background.MOON_RADIUS = 50 * (Scene.HEIGHT / 500);
            background.createBackground();
            start();
        }
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawARGB(255, 0, 0, 0);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(background.moon.x, background.moon.y, Background.MOON_RADIUS, paint);

        for(Tetramino tetramino:sceneList)
        {
            for(Block block: tetramino.getBlocks())
                if(block.active)
                {
                    paint.setColor(tetramino.getColor());
                    canvas.drawRect(block.rect, paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawRect(block.mid, paint);
                    paint.setColor(tetramino.getColor());
                    canvas.drawRect(block.subRect, paint);

                    if(block.p1 != null && block.p2 != null)
                    {
                        paint.setStrokeWidth(2);
                        paint.setColor(Color.WHITE);
                        canvas.drawLine(block.p1.x, block.p1.y, block.p2.x, block.p2.y, paint);
                    }
                }
        }

        for(Point star : background.stars)
        {
            paint.setColor(Background.STAR_COLOR);
            paint.setStrokeWidth(3);
            canvas.drawPoint(star.x, star.y, paint);
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
                clearEmptyTetraminos();
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
            if(block.active && block.rect.right >= Scene.WIDTH) return true;

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
                    (newBlock.rect.right > Scene.WIDTH) ||
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

    int getHi_score(){return hi_score;}

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
        cancel = true;
        stop();
        deletingAnimation.quit();
    }
}
