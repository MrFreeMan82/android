package home.opengl;

import android.app.Activity;
import android.graphics.Point;

/**
 * Created by Дима on 17.01.2017.
 */

public class Display
{
    private int mWidth, mHeight;

    public Display(Activity activity)
    {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        mWidth = size.x;
        mHeight = size.y;
    }

    public int getWidth()
    {return mWidth;}

    public int getHeight()
    {return mHeight;}

}
