package home.tetris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Дима on 23.02.2017.
 */

public class Updater extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "Updater";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/MrFreeMan82/android/master/Tetris/version.txt";
    private static final String VERSION_APK_URL = "https://github.com/MrFreeMan82/android/blob/master/Tetris/release/";
    private static final String APP_SETTING_IGNORED_VERSION = "ignoredVersion";

    private MainActivity mainActivity;

    Updater(MainActivity activity)
    {
        mainActivity = activity;
    }

    protected Void doInBackground(Void... params)
    {
        checkUpdates();
        return null;
    }

    private String getURLString(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(connection.getResponseMessage() + ": with " + urlString);
            }

            int bytesRead;
            byte[] buffer = new byte[8];
            while((bytesRead = in.read(buffer)) > 0) out.write(buffer, 0, bytesRead);
            out.close();
            return out.toString();
        } finally {
            connection.disconnect();
        }
    }

    private int getLastAppVersion()
    {
        try {
            String str = getURLString(VERSION_URL);
            return (str.equals("")) ? 0 : Integer.parseInt(str);
        } catch (IOException ioe){
            Log.e(TAG, "Error while getLastAppVersion");
            ioe.printStackTrace();
        }
        return 0;
    }

    private void checkUpdates()
    {
        final int lastAppVersion = getLastAppVersion();
        if(lastAppVersion == 0) return;
        if(lastAppVersion <= BuildConfig.VERSION_CODE) return; // Last version Ok skipping

        int ignoredInt = Settings.getIntSetting(APP_SETTING_IGNORED_VERSION, 0);
        if((ignoredInt == 0) || (ignoredInt >= lastAppVersion)) return;

        doUpdate(lastAppVersion);
    }

    private void doUpdate(final int lastAppVersion)
    {
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage(mainActivity.getString(R.string.update_available, lastAppVersion))
                .setMessage(mainActivity.getString(R.string.update_info))
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String apkURL = VERSION_APK_URL + lastAppVersion +
                                        mainActivity.getString(R.string.app_name) + ".apk";

                                intent.setData(Uri.parse(apkURL));
                                mainActivity.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Settings.setIntSetting(APP_SETTING_IGNORED_VERSION, lastAppVersion);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}
