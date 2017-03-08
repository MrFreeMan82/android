package home.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by Дима on 08.03.2017.
 *
 */

public class SceneView extends View
{
    static final int SCREEN_DELTA = 500;
    static final int BLOCKS_PER_WIDTH = 12;
    private static int width;
    private static int heght;

    private Block[][] field;

    SceneView(Context context){
        super(context);
    }

    void setBounds(int aWidth, int aHeight)
    {
        width = aWidth;
        heght = aHeight;
        field = new Block[BLOCKS_PER_WIDTH][];
    }

    @Override
    public void onDraw(Canvas canvas)
    {

    }

    static int getWIDTH(){return width;}
    static int getHEIGHT(){return heght;}
}
