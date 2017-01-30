package home.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private LinearLayout canvasLayout;
    private Renderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        mRenderer = new Renderer(this);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        canvasLayout = (LinearLayout) findViewById(R.id.canvas);
        canvasLayout.addView(mRenderer, lParams);

        ViewTreeObserver observer = canvasLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout()
    {
        Display.Width = canvasLayout.getWidth();
        Display.Height = canvasLayout.getHeight();
        Display.SQ_SIZE = Display.Width / 12;
        mRenderer.start();
      //  Log.d("MainActivity", "onGlobalLayout");
    }

}
