package home.tetris;
import android.content.Context;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity
        implements Updater.Callback, GameListener, View.OnTouchListener
{
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static final String DEFAULT_LANG = "en";
    private static final String DIALOG_SETTINGS = "DialogSettings";
    private static int sceneWidth, sceneHeight;

    private MainActivity activity;
    private Scene sceneView;
    private Menu mMenu;
    private LinearLayout canvasLayout;
    private Statistic statisticView;
    private boolean pause = false;
    private boolean moving = false;
    private static long backPressed = 0;
    private int oldX = 0, oldY = 0;

    /**
     * Здесь  ViewTreeObserver используется для определения момента когда станут известны
     * размеры View элемента canvasLayout. Далее мы сможем определть размеры нашего поля
     * и начать игру.
     */

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);

        EXECUTOR.execute(new Updater(this, false));
        new Sound(this);

        activity = this;

        canvasLayout = (LinearLayout) findViewById(R.id.canvas);

        ViewTreeObserver observer = canvasLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override public void onGlobalLayout()
            {
                if(sceneView == null)
                {
                    sceneWidth = canvasLayout.getWidth();
                    sceneHeight = canvasLayout.getHeight();
                    sceneView = new Scene(activity);
                    sceneView.setOnTouchListener(activity);
                    sceneView.setGameListener(activity);
                    canvasLayout.addView(sceneView);
                    sceneView.start();
                }
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if(!pause) togglePause();
                SettingsDialog dialog = new SettingsDialog();
                dialog.setSettingDialogListener(new SettingsDialog.SettingsDialogListener()
                {
                    @Override public void onCloseSettingsDialog()
                    {
                        if(statisticView != null) return;
                        if(pause) togglePause();
                    }

                    @Override public void onChangeLanguage(String newLanguage)
                    {
                        sceneView.free();
                        canvasLayout.removeView(sceneView);
                        canvasLayout.removeView(statisticView);
                        recreate();
                        Settings.setStringSetting(Settings.APP_LANGUAGE, newLanguage);
                    }
                });
                dialog.show(getSupportFragmentManager(), DIALOG_SETTINGS);
            }
        });

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);
    }

    static Future<Integer> submit(Callable<Integer> callable){return EXECUTOR.submit(callable);}
    static void execute(Runnable runnable){EXECUTOR.execute(runnable);}
    static int getSceneWidth(){return sceneWidth;}
    static int getSceneHeight(){return sceneHeight;}

    @Override protected void attachBaseContext(Context newBase)
    {
        new Settings(newBase);
        super.attachBaseContext(MyContextWrapper.wrap(newBase,
                Settings.getStringSetting(Settings.APP_LANGUAGE, DEFAULT_LANG)));
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        Statistic.clearStatistic();
        sceneView.free();
    }

    private void togglePause()
    {
        MenuItem itemPause = mMenu.findItem(R.id.item_pause_game);
        onOptionsItemSelected(itemPause);
    }

    @Override public void onUpdateDialogClose()
    {
        if(pause) togglePause();
    }

    @Override public void onGotUpdate() {if(!pause) togglePause();}

    @Override public boolean onTouch(View v, MotionEvent event)
    {
        if(pause) return true;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                oldX = x; oldY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if(x - oldX < -Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    sceneView.moveCurrentLeft();
                    return true;
                }
                else if(x - oldX > Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    sceneView.moveCurrentRight();
                    return true;
                }
                else if(y - oldY > Block.SIZE){
                    moving = true;
                    oldX = x; oldY = y;
                    sceneView.moveCurrentDown(Scene.FALL_INCREMENT);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(!moving) {
                    sceneView.rotateCurrent();
                }
                moving = false;
        }
        return true;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.tetris_menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_pause_game:
                pause = !pause;
                if(pause)
                {
                    item.setTitle(R.string.play_game);
                    item.setIcon(R.drawable.ic_action_play);
                    sceneView.pause();
                    showHiScore(sceneView.getHi_score());
                } else {
                    item.setTitle(R.string.pause_game);
                    item.setIcon(R.drawable.ic_action_pause);
                    hideStatistic();
                    sceneView.start();
                }
                return true;

            case R.id.item_stop_game:
                if(!pause) togglePause();
                sceneView.stop();
                showHiScore(sceneView.getHi_score());
                showStatistic();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    private void hideStatistic()
    {
        if(statisticView == null) return;
        Statistic.clearStatistic();
        canvasLayout.removeView(statisticView);
        canvasLayout.addView(sceneView);
        statisticView = null;
    }

    private void showStatistic()
    {
        if(statisticView == null)
        {
            canvasLayout.removeView(sceneView);
            statisticView = new Statistic(activity);
            canvasLayout.addView(statisticView);
        }
    }

    private void showHiScore(int hi_score)
    {
        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(
                    getString(R.string.hi_score,
                            String.format(Locale.getDefault(), "%04d", hi_score)));
    }

    @Override public void onLevelUp(int level)
    {Toast.makeText(this, getString(
                R.string.level_up, level), Toast.LENGTH_SHORT).show();}

    @Override public void onGameOver()
    {Toast.makeText(this, getString(R.string.game_over), Toast.LENGTH_SHORT).show();}

    @Override public void onScoreChange(final int score)
    {
        if(getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(
                    String.format(Locale.getDefault(), "%04d", score));
    }

    @Override public void onBackPressed()
    {
        if(backPressed + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();

        } else {
            Toast.makeText(this, getString(R.string.push_again_to_exit), Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}