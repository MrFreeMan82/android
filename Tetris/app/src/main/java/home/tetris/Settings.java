package home.tetris;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Дима on 23.02.2017.
 *
 */

class Settings
{
    private static final String APP_SETTINGS = "settings";

    private static SharedPreferences preferences = null;

    Settings(Context context)
    {
        preferences = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    static void setIntSetting(String key, int value)
    {
        preferences.edit().putInt(key, value).apply();
    }

    static int getIntSetting(String key, int def)
    {
        return preferences.getInt(key, def);
    }
}
