package home.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Дима on 23.02.2017.
 *
 */

class Updater extends AsyncTask<Void, Void, Void>{

    private static final String TAG = "Updater";
    private static final String RELEASE_NAME = "Tetris";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/MrFreeMan82/android/master/Tetris/version.txt";
    private static final String VERSION_RELEASE_URL = "https://github.com/MrFreeMan82/android/releases/download/";

    private Activity mainActivity;
    private Callback callback;
    private boolean quiet;
    static int LAST_APP_VERSION;

    interface Callback
    {
        void onGotUpdate();
        void onUpdateDialogClose();
    }

    Updater(Activity activity, boolean quietDownload)
    {
        quiet = quietDownload;
        mainActivity = activity;
        callback = (Callback) activity;
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
            byte[] buffer = new byte[1024];
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
        LAST_APP_VERSION = getLastAppVersion();
        if(LAST_APP_VERSION == 0) return;
        if(LAST_APP_VERSION <= BuildConfig.VERSION_CODE) return; // Last version Ok skipping

        doUpdate(LAST_APP_VERSION);
    }

    private void downloadUpdate(int lastAppVersion)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String apkURL = VERSION_RELEASE_URL + lastAppVersion + '/' + RELEASE_NAME + ".apk";

        intent.setData(Uri.parse(apkURL));
        mainActivity.startActivity(intent);
    }

    private void doUpdate(final int lastAppVersion)
    {
        mainActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                if(quiet)
                {
                    downloadUpdate(lastAppVersion);
                    return;
                }

                callback.onGotUpdate();

                int ignoredInt = Settings.getIntSetting(Settings.APP_SETTING_IGNORED_VERSION, 0);
                if(ignoredInt >= LAST_APP_VERSION) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder
                        .setMessage(mainActivity.getString(R.string.update_available, lastAppVersion))
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                downloadUpdate(lastAppVersion);
                                callback.onUpdateDialogClose();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Settings.setIntSetting(Settings.APP_SETTING_IGNORED_VERSION, lastAppVersion);
                                callback.onUpdateDialogClose();
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
