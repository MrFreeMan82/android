package home.tetris;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * Created by Дима on 24.02.2017.
 * Класс используется для смены языков
 *
 */

public class MyContextWrapper extends ContextWrapper
{
    MyContextWrapper(Context base){super(base);}

    public static ContextWrapper wrap(Context context, String language) {
        Configuration config = context.getResources().getConfiguration();
        Locale sysLocale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        if (!language.equals("") && !sysLocale.getLanguage().equals(language))
        {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                context = context.createConfigurationContext(config);
            } else {
                context.getResources().updateConfiguration(config,
                        context.getResources().getDisplayMetrics());
            }
        }
        return new MyContextWrapper(context);
    }
}
