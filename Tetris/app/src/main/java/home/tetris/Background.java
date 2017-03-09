package home.tetris;

import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Дима on 22.02.2017.
 *
 */

class Background extends AsyncTask<Void, Void, Void>
{
    static final int MOON_RADIUS = 50 * (Scene.HEIGHT / Scene.SCREEN_DELTA);
    static final int STAR_COLOR = 0xffa9e4f4;
    static final int MOON_COLOR = 0xfff5f5c8;

    private static final String TAG = "Background";
    private static final int STARS_COUNT = 50;

    private boolean pause;

    Point[] stars;
    Point moon;

    Background()
    {
        moon = new Point();
        stars = new Point[STARS_COUNT];

        moon.x = (int) (Math.random() * Scene.WIDTH);
        moon.y = (int) (Math.random() * Scene.HEIGHT);

        for(int i = 0; i < STARS_COUNT; i++)
        {
            stars[i] = new Point();
            stars[i].x = (int) (Math.random() * Scene.WIDTH);
            stars[i].y = (int) (Math.random() * Scene.HEIGHT);
        }
    }

    private void moveMoon()
    {
        moon.x += 1;
        moon.y -= 1;

        if(moon.x - MOON_RADIUS * 2 > Scene.WIDTH) moon.x = -MOON_RADIUS;
        if(moon.y + MOON_RADIUS < -MOON_RADIUS * 2) moon.y = Scene.HEIGHT + MOON_RADIUS;
    }

    private void moveStars()
    {
        for(Point star : stars)
        {
            star.y -= 1;
            if(star.y < 0) star.y = Scene.HEIGHT;
        }
    }

    void setPause(boolean value){pause = value;}

    @Override
    protected Void doInBackground(Void... params)
    {
        while (!isCancelled())
        {
            if(!pause)
            {
                moveMoon();
                moveStars();
                try {
                    Thread.sleep(60);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    Log.e(TAG, "Background Animation Error");
                }
            }
        }
        return null;
    }
}
