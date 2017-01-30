package home.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity
{
    private GLSurfaceView mGLSurfaceView;
    private NewRenderer renderer;
    private float x = 0, y = 0, oldX = 0, oldY = 0;
    private boolean enableMove = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = manager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if(!supportsEs2)
        {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        renderer = new NewRenderer(this);
        mGLSurfaceView.setRenderer(renderer);
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                oldX = x; oldY = y;
                x = event.getX();
                y = event.getY();
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                        if(enableMove) enableMove = false;
                        if(y < oldY){
                            renderer.enableRotate = true;
                        }
                        else if(x > oldX && abs(x - oldX) > 10) {
                            renderer.enableRight = true;
                            renderer.enableLeft = false;
                        }else if (x < oldX && abs(x - oldX) > 10){
                            renderer.enableRight = false;
                            renderer.enableLeft = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        enableMove = true;
                        renderer.enableLeft = false;
                        renderer.enableRight = false;
                        renderer.enableRotate = false;
                        break;
                }
                return true;
            }
        });
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}

