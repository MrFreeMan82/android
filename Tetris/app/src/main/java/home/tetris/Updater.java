package home.tetris;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Created by Дима on 23.02.2017.
 */

public class Updater extends AsyncTask<MainActivity, Void, Void>{

    private static final String TAG = "Updater";

    protected Void doInBackground(MainActivity... activity)
    {
        return null;
    }

    Integer getLastAppVersion()
    {
        try{
            URL url = new URL("");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
