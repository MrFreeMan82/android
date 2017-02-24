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
    static final String APP_SETTING_HISCORE = "hiscore";
    static final String APP_SETTING_IGNORED_VERSION = "ignoredVersion";
    static final String APP_LANGUAGE = "language";
    static final String APP_SOUND_ENABLED = "sound_enabled";

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

    static void setStringSetting(String key, String value)
    {
        preferences.edit().putString(key, value).apply();
    }

    static String getStringSetting(String key, String def)
    {
        return preferences.getString(key, def);
    }

    static void setBooleanSetting(String key, boolean value)
    {
        preferences.edit().putBoolean(key, value).apply();
    }

    static boolean getBooleanSetting(String key, boolean def)
    {
        return preferences.getBoolean(key, def);
    }

    static boolean exists(String key)
    {
        return preferences.contains(key);
    }

}
