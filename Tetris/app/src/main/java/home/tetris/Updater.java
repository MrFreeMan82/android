package home.tetris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Дима on 23.02.2017.
 */

public class Updater extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "Updater";
    private static final String VERSION_URL = "https://github.com/MrFreeMan82/android/blob/master/Tetris/version.txt";
    private static final String APP_SETTING_IGNORED_VERSION = "ignoredVersion";

    private MainActivity mainActivity;
    private SharedPreferences preferences;

    Updater(SharedPreferences aPreferences, MainActivity activity)
    {
        mainActivity = activity;
        preferences = aPreferences;
    }

    protected Void doInBackground(Void... params)
    {
        checkUpdates();
        return null;
    }

    Integer getLastAppVersion()
    {
        try{
            URL url = new URL(VERSION_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = reader.readLine();
            reader.close();
            return Integer.parseInt(str);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    void checkUpdates()
    {
        final Integer lastAppVersion = getLastAppVersion();
        if(lastAppVersion == null) return;
        if(lastAppVersion <= BuildConfig.VERSION_CODE) return; // Last version Ok skipping

        String ignoredStr = preferences.getString(APP_SETTING_IGNORED_VERSION, null);
        if(ignoredStr != null)
        {
            int ignoredInt = Integer.parseInt(ignoredStr);
            if(ignoredInt >= lastAppVersion) return;
        }

        doUpdate(lastAppVersion);
    }

    void doUpdate(final int lastAppVersion)
    {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage(mainActivity.getString(R.string.update_available, lastAppVersion))
                .setMessage(mainActivity.getString(R.string.update_info))
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                            }
                        });


            }
        });
    }

}
