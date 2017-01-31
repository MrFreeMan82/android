package home.tetris;

import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements ViewTreeObserver.OnGlobalLayoutListener, Renderer.Callback{

    private LinearLayout canvasLayout;
    private Renderer mRenderer;
    private Menu mMenu;
    private boolean pause = false;

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
        if(!pause) mRenderer.start();
        Log.d("Activity", "onGlobalLayout");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.tetris_menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.item_pause_game:
                pause = !pause;
                if(pause) {
                    item.setTitle(R.string.new_game);
                    item.setIcon(R.drawable.ic_action_play);
                    mRenderer.pause();
                } else {
                    item.setTitle(R.string.pause_game);
                    item.setIcon(R.drawable.ic_action_pause);
                    mRenderer.start();
                }

                return true;
            case R.id.item_stop_game:
                MenuItem itemPause = mMenu.findItem(R.id.item_pause_game);
                if(!pause) onOptionsItemSelected(itemPause);
                mRenderer.stop();

                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScoreChange(int score)
    {
        getSupportActionBar().setSubtitle(String.format(Locale.US, "%04d", score));
    }
}
