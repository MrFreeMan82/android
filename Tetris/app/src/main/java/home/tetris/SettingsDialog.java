package home.tetris;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Дима on 24.02.2017.
 *
 */

public class SettingsDialog extends DialogFragment
{
    private static final String ABOUT = "http://website.byethost24.com/2017/02/28/%d1%82%d0%b5%d1%82%d1%80%d0%b8%d1%81-%d0%b4%d0%bb%d1%8f-android/#more-59";
    private RadioButton english;
    private RadioButton russian;
    private CheckBox sound;
    private String oldLang;
    private SettingsDialogListener listener;

    interface SettingsDialogListener{
        void onCloseSettingsDialog();
        void onChangeLanguage(String newLanguage);
    }

    void setSettingDialogListener(SettingsDialogListener settingDialogListener){listener = settingDialogListener;}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final ViewGroup nullParent = null;
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.settings_dialog, nullParent, false);

        TextView currentVersionText = (TextView) v.findViewById(R.id.curr_version);
        currentVersionText.setText(getString(R.string.version, BuildConfig.VERSION_CODE));

        Button newVersionButton = (Button) v.findViewById(R.id.new_version);
        String version = (Updater.LAST_APP_VERSION > 0) ? Integer.toString(Updater.LAST_APP_VERSION): "--";
        newVersionButton.setText(getString(R.string.new_version, version));
        newVersionButton.setEnabled(Updater.LAST_APP_VERSION > BuildConfig.VERSION_CODE);
        newVersionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MainActivity.execute(new Updater(getActivity(), true));
                dismiss();
            }
        });

        Button about = (Button) v.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ABOUT));
                startActivity(intent);
            }
        });

        english = (RadioButton) v.findViewById(R.id.english_radio);
        russian = (RadioButton) v.findViewById(R.id.russian_radio);
        sound = (CheckBox) v.findViewById(R.id.sound_check_box);

        oldLang = Settings.getStringSetting(Settings.APP_LANGUAGE, "");
        switch (oldLang)
        {
            case "en": english.setChecked(true); break;
            case "ru": russian.setChecked(true); break;
            default:
                oldLang = "en";
                english.setChecked(true);
        }
        sound.setChecked(Settings.getBooleanSetting(Settings.APP_SOUND_ENABLED, true));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.settings)
                .setPositiveButton(R.string.save,
                                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Settings.setBooleanSetting(Settings.APP_SOUND_ENABLED, sound.isChecked());

                        if(english.isChecked()) {
                            if(!oldLang.equals("en")) listener.onChangeLanguage("en");
                        }
                        else if(russian.isChecked()) {
                            if(!oldLang.equals("ru")) listener.onChangeLanguage("ru");
                        }

                        listener.onCloseSettingsDialog();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onCloseSettingsDialog();
                        dismiss();
                    }
                })
                .create();
    }

}
