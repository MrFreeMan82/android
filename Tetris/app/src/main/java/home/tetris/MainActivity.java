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

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity
        implements  Scene.Callback, View.OnTouchListener{

    private Scene scene;
    private Menu mMenu;
    private boolean pause = false;
    private boolean moving = false;
    private static long backPressed = 0;
    private int oldX = 0, oldY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        scene = new Scene(this);
        scene.setOnTouchListener(this);
       // LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
      //          LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout canvasLayout = (LinearLayout) findViewById(R.id.canvas);
        canvasLayout.addView(scene);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        scene.free();
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
                if(x - oldX < -Tetramino.SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentLeft();
                    return true;
                }
                else if(x - oldX > Tetramino.SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentRight();
                    return true;
                }
                else if(y - oldY > Tetramino.SQ_SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentDown(Scene.FALL_SPEED_INCREMENT);
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
                    item.setTitle(R.string.play_game);
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
        Toast.makeText(this, getString(R.string.level_up, level), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameOver()
    {
        Toast.makeText(this, R.string.game_over, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScoreChange(int score)
    {
        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(String.format(Locale.US, "%04d", score));
    }

    @Override
    public void onBackPressed()
    {
        if(backPressed + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();
        }
        {
            Toast.makeText(this, R.string.push_again_to_exit, Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}
