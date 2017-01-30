package home.opengl;

import android.app.Activity;
import android.graphics.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by Дима on 17.01.2017.
 */

/*
Соответственно вычислить местоположения пиксела на экране можно таким способом:
XPixelSize = 2.0f/XScreen;
YPixelSize = 2.0f/YScreen;
Где XScreen и YScreen - количество пикселов по оси на экране.
соответственно:
перевести экранные( 150, 100 ) в систему координат OpenGL:
Хposition = XPixelSize*150 - 1.0f;
Yposition = YPixelSize*(YScreen - 100) - 1.0f;
 */

class Square
{
    static final int VERTEX_SIZE = 8;
    static final int SQ_SIZE = 40;
    private float[] mVertices;
  //  private FloatBuffer mVertexData;
  //  private float[] mColor = {0.0f, 0.0f, 1.0f, 1.0f};

    private float xps;
    private float yps;

    private int width, height;

    private int left, top, right, bottom;

    Square(Display display)
    {
        width = display.getWidth();
        height = display.getHeight();
        xps = 2.0f / width;             // XPixelSize
        yps = 2.0f / height;            // YPixelSize

        mVertices = new float[VERTEX_SIZE];
     //   mVertexData = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

     //   int l = leftPos;//random.nextInt(width);
     //   int t = 1;
     //   int r = l + SQ_SIZE;
     //   int b = t + SQ_SIZE;

     //   Calculate(l,t,r,b);
    }

    private void Calculate(int aLeft, int aTop, int aRight, int aBottom)
    {
        left = aLeft;
        if(left > width) left -= SQ_SIZE;
        top = aTop;
        right = aRight;
        bottom = aBottom;

        mVertices[0] = xps * left - 1.0f;           // left - top
        mVertices[1] = yps * (height - top) - 1.0f;
        mVertices[2] = mVertices[0];                // left - bottom
        mVertices[3] = yps * (height - bottom) - 1.0f;
        mVertices[4] = xps * right - 1.0f;          // right - top
        mVertices[5] = mVertices[1];
        mVertices[6] = mVertices[4];                // right - bottom
        mVertices[7] = mVertices[3];
      //  mVertices[8] = mColor[0];
      //  mVertices[9] = mColor[1];
      //  mVertices[10] = mColor[2];
    }

    float[] getVertices()
    {return mVertices;}

    void incTop(int value)
    {
        top = top + value;
        bottom = top + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    void decLeft(int value)
    {
        left = left - value;
        right = left + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    void incLeft(int value)
    {
        left = left + value;
        right = left + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    void setLeft(int value)
    {
        left = value;
        right = left + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    int getLeft(){return left;}

    void setTop(int value)
    {
        top = value;
        bottom = top + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    int getTop(){return top;}

    int getRight(){return right;}

    int getBottom(){return bottom;}

    void setBounds(int aLeft, int aTop)
    {
        left = aLeft;
        right = left + SQ_SIZE;
        top = aTop;
        bottom = top + SQ_SIZE;
        Calculate(left, top, right, bottom);
    }

    float glLeft(){return mVertices[0];}
    float glTop(){return mVertices[1];}
    float glRight(){return mVertices[4];}
    float glBottom(){return mVertices[3];}
}
