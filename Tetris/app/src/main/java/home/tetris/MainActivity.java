package home.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

import static home.tetris.Globals.SQ_SIZE;
import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity
        implements ViewTreeObserver.OnGlobalLayoutListener, Scene.Callback, View.OnTouchListener{

    private static final int SPEED_INCREMENT = 50;

    private LinearLayout canvasLayout;
    private Scene scene;
    private Menu mMenu;
    private boolean pause = false;
    private boolean moving = false;
    private int oldX = 0, oldY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        scene = new Scene(this);
        scene.setOnTouchListener(this);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        canvasLayout = (LinearLayout) findViewById(R.id.canvas);
        canvasLayout.addView(scene, lParams);

        ViewTreeObserver observer = canvasLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(pause) return true;
        int x = round(event.getX());
        int y = round(event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                oldX = x; oldY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if(x - oldX < -SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentLeft();
                    return true;
                }
                else if(x - oldX > SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentRight();
                    return true;
                }
                else if(y - oldY > SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentDown(SPEED_INCREMENT);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(!moving) {
                    scene.rotateCurrent();
                }
                moving = false;
        }
        return true;
    }

    @Override
    public void onGlobalLayout()
    {
        Globals.WIDTH = canvasLayout.getWidth();
        Globals.HEIGHT = canvasLayout.getHeight();
        Globals.SQ_SIZE = Globals.WIDTH / Globals.BLOCKS_PER_WIDTH;
        if(!pause) scene.start();
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
                    scene.pause();
                } else {
                    item.setTitle(R.string.pause_game);
                    item.setIcon(R.drawable.ic_action_pause);
                    scene.start();
                }

                return true;
            case R.id.item_stop_game:
                MenuItem itemPause = mMenu.findItem(R.id.item_pause_game);
                if(!pause) onOptionsItemSelected(itemPause);
                scene.stop();

                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLevelUp(int level)
    {
        Toast.makeText(this, "Level up to " + level, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameOver()
    {
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScoreChange(int score)
    {
        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(String.format(Locale.US, "%04d", score));
    }
}
