package home.tetris;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

import static java.lang.Math.round;


public class MainActivity extends AppCompatActivity
        implements Updater.Callback, GameListener, View.OnTouchListener
{
    private static final String DEFAULT_LANG = "en";
    private static final String DIALOG_SETTINGS = "DialogSettings";
    private static int sceneWidth, sceneHeight;
    private MainActivity activity;
    private Scene scene;
    private Menu mMenu;
    private LinearLayout canvasLayout;
    private boolean pause = false;
    private boolean moving = false;
    private static long backPressed = 0;
    private int oldX = 0, oldY = 0;
    private int fallIncrement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        new Updater(this, false).execute();

        activity = this;
        canvasLayout = (LinearLayout) findViewById(R.id.canvas);
        canvasLayout.setOnTouchListener(this);

        ViewTreeObserver observer = canvasLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if(scene == null)
                {
                    sceneWidth = canvasLayout.getWidth();
                    sceneHeight = canvasLayout.getHeight();
                    fallIncrement = 50 * (sceneHeight / Scene.SCREEN_DELTA);
                    scene = new Scene(activity);
                    scene.setGameListener(activity);
                    scene.setSound(new Sound(activity));

                    canvasLayout.addView(scene);
                    scene.start();
                }
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!pause) togglePause();
                FragmentManager manager = getSupportFragmentManager();
                SettingsDialog dialog = new SettingsDialog();// SettingsDialog.get();
                dialog.setSettingDialogListener(new SettingsDialog.SettingsDialogListener() {
                    @Override
                    public void onCloseSettingsDialog() {
                        if(pause) togglePause();
                    }

                    @Override
                    public void onChangeLanguage(String newLanguage) {
                        scene.free();
                        canvasLayout.removeView(scene);
                        recreate();
                    }
                });
                dialog.show(manager, DIALOG_SETTINGS);
            }
        });

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);
    }

    static int getSceneWidth(){return sceneWidth;}
    static int getSceneHeight(){return sceneHeight;}

    @Override
    protected void attachBaseContext(Context newBase) {
        new Settings(newBase);
        super.attachBaseContext(MyContextWrapper.wrap(newBase,
                Settings.getStringSetting(Settings.APP_LANGUAGE, DEFAULT_LANG)));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //scene.free();
    }

    private void togglePause()
    {
        MenuItem itemPause = mMenu.findItem(R.id.item_pause_game);
        onOptionsItemSelected(itemPause);
    }

    @Override
    public void onUpdateDialogClose()
    {
        if(pause) togglePause();
    }

    @Override
    public void onGotUpdate() {if(!pause) togglePause();}

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
                if(x - oldX < -Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentLeft();
                    return true;
                }
                else if(x - oldX > Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentRight();
                    return true;
                }
                else if(y - oldY > Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    scene.moveCurrentDown(fallIncrement);
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
                    showHiScore(scene.getHi_score());
                } else {
                    item.setTitle(R.string.pause_game);
                    item.setIcon(R.drawable.ic_action_pause);
                    scene.start();
                }
                return true;

            case R.id.item_stop_game:
                if(!pause) togglePause();
                scene.stop();
                showHiScore(scene.getHi_score());
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    void showHiScore(int hi_score){
        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(
                    getString(R.string.hi_score,
                            String.format(Locale.US, "%04d", hi_score)));
    }

    @Override
    public void onLevelUp(int level)
    {
        Toast.makeText(this, getString(
                R.string.level_up, level), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGameOver()
    {
        Toast.makeText(this, getString(R.string.game_over), Toast.LENGTH_SHORT).show();
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
            scene.free();
            super.onBackPressed();
        } else {
            Toast.makeText(this, getString(R.string.push_again_to_exit), Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}
