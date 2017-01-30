package home.opengl;

import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform4f;
import static home.opengl.Square.SQ_SIZE;
import static home.opengl.Square.VERTEX_SIZE;
import static home.opengl.Tetramino.DOWN_DELTA;
import static home.opengl.Tetramino.MOVE_DELTA;
import static home.opengl.Tetramino.Type.tLineHorizontal;
import static home.opengl.Tetramino.Type.tLineVertical;
import static home.opengl.Tetramino.Type.tSquare;
import static home.opengl.Tetramino.Type.tT;
import static java.lang.Math.abs;

/**
 * Created by Дима on 18.01.2017.
 */

class Scene
{
    private  Display mDisplay;
    private List<Tetramino> sceneList;
    private Tetramino currentMino;
    private float xps, yps;


    Scene(Display display)
    {
        mDisplay = display;
        sceneList = new ArrayList<>();
        xps = 2.0f / mDisplay.getWidth();             // XPixelSize
        yps = 2.0f / mDisplay.getHeight();            // YPixelSize
      //  move_delta = abs(xps * MOVE_DELTA - 1.0f);

        newMino();
    }

    private void newMino() {
        //if(sceneList.size() > 1) return;
        sceneList.add(new Tetramino(mDisplay, tT));
        currentMino = sceneList.get(sceneList.size() - 1);
    }

    void Rotate()
    {
        Log.d("Rotate", "Rotating", new Exception());
    }

    void moveDown()
    {
        if(currentMino == null) return;
        if(!collisionBottom())
        {
            currentMino.moveDown();
        }else{
            newMino();
        }
    }

    void moveLeft()
    {
        if(currentMino == null) return;
        currentMino.moveLeft();
        if(collisionLeft(1)) currentMino.moveRight();
    }

    void moveRight()
    {
        if(currentMino == null) return;
        currentMino.moveRight();
        if(collisionRight()) currentMino.moveLeft();
    }

    private boolean collisionLeft(int left)
    {
        for(Square current: currentMino.getSquareList())
        {
            int t = current.getTop();
            int b = current.getBottom();
            int l = current.getLeft();
            int r = current.getRight();
            if(l <= 1) return true;
            for(Tetramino tetramino:sceneList)
            {
                if(tetramino == currentMino) continue;

                for(Square prev:tetramino.getSquareList())
                {
                    int p_b = prev.getBottom();
                    int p_l = prev.getLeft();
                    int p_r = prev.getRight();
                    int p_t = prev.getTop();
                    if (b >= p_t && t <= p_b &&
                            (
                                    (l >= p_l && l <= p_r) ||
                                            (r <= p_r && r >= p_l)
                            )
                            ) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionRight()
    {
        int right = mDisplay.getWidth();
        for(Square current: currentMino.getSquareList())
        {
            int t = current.getTop();
            int b = current.getBottom();
            int l = current.getLeft();
            int r = current.getRight();
            if(r >= right) return true;
            for(Tetramino tetramino:sceneList)
            {
                if(tetramino == currentMino) continue;

                for(Square prev:tetramino.getSquareList())
                {
                    int p_b = prev.getBottom();
                    int p_l = prev.getLeft();
                    int p_r = prev.getRight();
                    int p_t = prev.getTop();
                    if (b >= p_t && t <= p_b &&
                            (
                                    (l >= p_l && l <= p_r) ||
                                            (r <= p_r && r >= p_l)
                            )
                            ) return true;
                }
            }
        }
        return false;
    }

    private boolean collisionBottom()
    {
        int bottom = mDisplay.getHeight();
        for(Square current: currentMino.getSquareList())
        {
            int t = current.getTop();
            int b = current.getBottom();
            int l = current.getLeft();
            int r = current.getRight();
            if(b >= bottom) return true;
            for(Tetramino tetramino:sceneList)
            {
                if(tetramino == currentMino) continue;

                for(Square prev:tetramino.getSquareList())
                {
                    int p_b = prev.getBottom();
                    int p_l = prev.getLeft();
                    int p_r = prev.getRight();
                    int p_t = prev.getTop();
                    if (b >= p_t && t <= p_b &&
                       (
                            (l >= p_l && l <= p_r) ||
                            (r <= p_r && r >= p_l)
                       )
                    ) return true;
                }
            }
        }
        return false;
    }

    FloatBuffer getVertexData()
    {
        int sq_count = 0;
        for(int i = 0; i < sceneList.size(); i++)
            sq_count += sceneList.get(i).getSquare_count();

        FloatBuffer mVertexData = ByteBuffer.allocateDirect(
                        sceneList.size() * VERTEX_SIZE * sq_count * 4).
                                order(ByteOrder.nativeOrder()).asFloatBuffer();

        for(int i = 0; i < sceneList.size(); i++)
            mVertexData.put(sceneList.get(i).getVertices());

        mVertexData.position(0);

        return mVertexData;
    }

    void Draw(int coloLocation)
    {
        int k = 0;
        for(int i = 0; i < sceneList.size(); i++)
        {
            float[] color = sceneList.get(i).getColor();
            glUniform4f(coloLocation,  color[0], color[1], color[2], 1.0f);
            glDrawArrays(GL_TRIANGLE_STRIP, k, 16);

            k += 16;  // inc vertex
        }
    }
}
