package home.tetris;

import android.graphics.Point;

/**
 * Created by Дима on 22.02.2017.
 *
 */

class Background
{
    static final int MOON_RADIUS = 50 * (Scene.getHEIGHT() / Scene.SCREEN_DELTA);
    static final int STAR_COLOR = 0xffa9e4f4;
    private static final int STARS_COUNT = 50;
    Point[] stars;
    Point moon;


    Background(){
        moon = new Point();
        stars = new Point[STARS_COUNT];
    }

    void createBackground()
    {
        moon.x = (int) (Math.random() * Scene.getWIDTH());
        moon.y = (int) (Math.random() * Scene.getHEIGHT());

        for(int i = 0; i < STARS_COUNT; i++)
        {
            stars[i] = new Point();
            stars[i].x = (int) (Math.random() * Scene.getWIDTH());
            stars[i].y = (int) (Math.random() * Scene.getHEIGHT());
        }
    }

    void moveMoon()
    {
        moon.x += 1;
        moon.y -= 1;

        if(moon.x - MOON_RADIUS * 2 > Scene.getWIDTH()) moon.x = -MOON_RADIUS;
        if(moon.y + MOON_RADIUS < -MOON_RADIUS * 2) moon.y = Scene.getHEIGHT() + MOON_RADIUS;
    }

    void moveStars()
    {
       for(Point star : stars)
       {
           star.y -= 1;
           if(star.y < 0) star.y = Scene.getHEIGHT();
       }
    }
}
