package home.opengl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Дима on 15.01.2017.
 */


class NewRenderer implements GLSurfaceView.Renderer
{
    private Display mDisplay;
    private Context mContext;
    private Scene scene;
    private int mProgramId;
    private int mColorLocation;
    private int mPositionLoacation;

    boolean enableLeft = false;
    boolean enableRight = false;
    boolean enableRotate = false;

        NewRenderer(Context context)
    {
        mContext = context;
        Activity activity = (Activity) mContext;
        mDisplay = new Display(activity);
        prepareData();
    }

    private void prepareData()
    {
        scene = new Scene(mDisplay);
    }

    private void bindData()
    {
        mColorLocation = glGetUniformLocation(mProgramId, "u_Color");

        mPositionLoacation = glGetAttribLocation(mProgramId, "a_Position");
        glEnableVertexAttribArray(mPositionLoacation);
    }

    @Override
    public void onDrawFrame(GL10 arg0)
    {
        glClear(GL_COLOR_BUFFER_BIT);
        boolean enableDown = enableLeft == false && enableRight == false;
        scene.Draw(mColorLocation);
        if(enableRotate) scene.Rotate();
        if(enableLeft) scene.moveLeft();
        if(enableRight) scene.moveRight();
        if(enableDown)scene.moveDown();
        glVertexAttribPointer(mPositionLoacation, 2, GL_FLOAT, false, 0, scene.getVertexData());
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1)
    {
        glClearColor(0f, 0f, 0f, 1f);
        int vertexShaderId = ShaderUtil.createShader(mContext, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtil.createShader(mContext, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        mProgramId = ShaderUtil.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(mProgramId);
        bindData();
    }
}
