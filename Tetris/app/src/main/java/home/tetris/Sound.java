package home.tetris;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Дима on 11.02.2017.
 * Класс менеджер звуков игры
 */

class Sound {
    static final int IMPACT = 1;
    static final int LEVEL_UP = 2;
    static final int MOVE_MINO = 3;
    static final int ROTATE = 4;
    static final int DELETE_LINE = 5;

    private static final String SOUND_FOLDER = "tetris_sounds";
    private static final int MAX_SOUNDS = 10;

    private AssetManager assetManager;
    private static SoundPool soundPool;

    Sound(Context context){
        assetManager = context.getAssets();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(MAX_SOUNDS)
                    .build();
        } else {
            soundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }

        loadSounds();
    }

    private void loadSounds(){
        try{
            String[] files = assetManager.list(SOUND_FOLDER);
            Arrays.sort(files);

            for(String filename: files){
                String assetPath = SOUND_FOLDER + "/";
                AssetFileDescriptor afd = assetManager.openFd(assetPath + filename);
                soundPool.load(afd, 1);
            }
        } catch (IOException ioe) {
            Log.e("Sound", "Could not list assets", ioe);
        }
    }

    static void play(int soundId){
        if(Settings.getBooleanSetting(
                Settings.APP_SOUND_ENABLED, true))
                        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
