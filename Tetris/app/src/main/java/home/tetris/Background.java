package home.tetris;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Дима on 22.02.2017.
 *
 */

class Background
{
    static final int STAR_COLOR = 0xffa9e4f4;
    private static final int STARS_COUNT = 50;
    static long INTERVAL = 0;
    Point[] stars;
    Point moon;
    static int MOON_RADIUS = 50;

    Background(){
        moon = new Point();
        moon.x = (int) (Math.random() * Scene.WIDTH);
        moon.y = (int) (Math.random() * Scene.HEIGHT);

        stars = new Point[STARS_COUNT];

        for(int i = 0; i < STARS_COUNT; i++)
        {
            stars[i] = new Point();
            stars[i].x = (int) (Math.random() * Scene.WIDTH);
            stars[i].y = (int) (Math.random() * Scene.HEIGHT);
        }
    }

    void moveMoon()
    {
        moon.x += 1;
        moon.y -= 1;

        if(moon.x - MOON_RADIUS * 2 > Scene.WIDTH) moon.x = -MOON_RADIUS;
        if(moon.y + MOON_RADIUS < -MOON_RADIUS * 2) moon.y = Scene.HEIGHT + MOON_RADIUS;
    }

    void moveStars()
    {
       for(Point star : stars)
       {
           star.y -= 1;
           if(star.y < 0) star.y = Scene.HEIGHT;
       }
    }
}
