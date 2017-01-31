package home.tetris;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    private int x = 0, oldX = 0;
    private boolean enableMove = false;
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
            if(enableMove && x < oldX) scene.moveCurrentLeft(x);
            else if(enableMove && x > oldX) scene.moveCurrentRight(x);
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
                oldX = x;
                x = round(event.getX());

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                        enableRotate = false;
                        enableMove = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        enableRotate = !enableMove;
                        enableMove = false;
                }

                return true;
            }
        });
    }

    void start()
    {
        if(running) return;
        timer = new MyTimer(Long.MAX_VALUE, 20);
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
