package home.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Дима on 11.03.2017.
 *
 */

final class Block
{
    static final int SIZE = Scene.WIDTH / Scene.BLOCKS_PER_WIDTH;
    private static final int DELTA = 5 * (Scene.HEIGHT / Scene.SCREEN_DELTA);

    boolean visible = true;
    Tetramino tetramino;
    Rect rect;
    private Rect subRect;
    private Rect mid;
    private Point p1;
    private Point p2;

    Block(int left, int top, int right, int bottom)
    {
        rect = new Rect(left, top, right, bottom);
        subRect = new Rect(rect);
        mid = new Rect(rect);
        p1 = new Point();
        p2 = new Point();
        updateSubRect();
    }

    void moveDown(int value)
    {
        rect.top += value;
        rect.bottom = rect.top + SIZE;
        updateSubRect();
    }

    void moveLeft()
    {
        rect.left -= SIZE;
        rect.right = rect.left + SIZE;
        updateSubRect();
    }

    void moveRight()
    {
        rect.left = rect.right;
        rect.right = rect.left + SIZE;
        updateSubRect();
    }

    void decrease()
    {
        rect.bottom -= 1;
        updateSubRect();
    }

    private void updateSubRect()
    {
        subRect.left = rect.left + DELTA;
        subRect.top = rect.top + DELTA;
        subRect.right = rect.right - DELTA;
        subRect.bottom = rect.bottom - DELTA;

        if(subRect.bottom > subRect.top) {
            int h = subRect.bottom - subRect.top;
            int w = subRect.right - subRect.left;

            p1.x = subRect.right;
            p1.y = subRect.top + (h / 2) + ((h / 2) / 2);

            p2.x = subRect.left + ((w / 2) + ((w / 2) / 2));
            p2.y = subRect.bottom;
        }

        mid.left = rect.left + (DELTA / 2);
        mid.top = rect.top + (DELTA / 2);
        mid.right = rect.right - (DELTA / 2);
        mid.bottom = rect.bottom - (DELTA / 2);
    }

    void draw(Canvas canvas, Paint paint)
    {
        if(!visible) return;
        int color = tetramino.getColor();
        paint.setColor(color);
        canvas.drawRect(rect, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(mid, paint);
        paint.setColor(color);
        canvas.drawRect(subRect, paint);

        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);

        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
    }
}
