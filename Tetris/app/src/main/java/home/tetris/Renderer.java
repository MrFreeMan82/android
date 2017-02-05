package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static home.tetris.Globals.SQ_SIZE;
import static java.lang.Math.round;

/**
 * Created by Дима on 23.01.2017.
 */

public class Renderer extends View
{
    private Context mContext;
    private Scene scene = null;
    private MyTimer timer;
    private boolean running = false;
    private int x = 0, oldX = 0, y =0, oldY = 0;
    private boolean enableMoveLeft = false, enableMoveRight = false, enableMoveDown = false;
    private boolean enableRotate = false;
    private int oldScore = 0;
    private Callback callback;

    interface Callback
    {
        void onScoreChange(int score);
    }

    private class MyTimer extends CountDownTimer
    {
        MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish()
        {}

        @Override
        public void onTick(long millisUntilFinished)
        {
            if(!running) return;

            if(scene.getGameOver())
            {
                Toast.makeText(mContext, "Game Over", Toast.LENGTH_SHORT).show();
                running = false;
                cancel();
                return;
            }

            int score = scene.getScore();
            if(oldScore != score)
            {
                oldScore = score;
                callback.onScoreChange(score);
            }

            if(enableRotate)
            {
                scene.rotateCurrent();
                enableRotate = false;
            }

            if(enableMoveLeft) scene.moveCurrentLeft(x);
            else if(enableMoveRight) scene.moveCurrentRight(x);
            if(enableMoveDown) scene.moveCurrentDown(y);

            scene.moveCurrentDown();
            invalidate();
        }
    }

    public Renderer(Context context) {
        super(context);
        mContext = context;
        callback = (Callback) context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                x = round(event.getX());
                y = round(event.getY());

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        oldX = x;         // При касании запоминаем старые значения х, у
                        oldY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        enableRotate = false;
                        // Для того чтоб отделить движение от касания нужно проверить расстояние между
                        // старыми значениями х,у и новыми. Если расстояние больше одного квадрата, то это движение
                        // иначе будем тетрамино поворачивать
                        enableMoveLeft = oldX - x > SQ_SIZE;
                        enableMoveRight = x - oldX > SQ_SIZE;
                        enableMoveDown = y - oldY > SQ_SIZE;

                        // Сбрасываем старые значения для того например, если будем водить вправо влево
                        // и х = oldx то будет задержка.
                        if(enableMoveLeft || enableMoveRight) oldX = -SQ_SIZE;
                        break;

                    case MotionEvent.ACTION_UP:
                        enableRotate = !(enableMoveRight || enableMoveLeft || enableMoveDown);
                        enableMoveLeft = false;
                        enableMoveRight = false;
                        enableMoveDown = false;
                }

                return true;
            }
        });
    }

    void start()
    {
        if(running) return;
        timer = new MyTimer(Long.MAX_VALUE, 15);
        scene = Scene.get();
        callback.onScoreChange(scene.getScore());
        timer.start();
        running = true;
    }

    void pause()
    {
        if(!running) return;
        running = false;
        timer.cancel();
    }

    void stop()
    {
        running = false;
        scene.clear();
        timer.cancel();
        callback.onScoreChange(scene.getScore());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // canvas.drawColor(Color.GREEN);
        scene.Draw(canvas);
    }
}
