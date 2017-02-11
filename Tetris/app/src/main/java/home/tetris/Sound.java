package home.tetris;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Дима on 11.02.2017.
 */

class Sound {
    private static final String SOUND_FOLDER = "tetris_sounds";
    private static final int MAX_SOUNDS = 1;

    private AssetManager assetManager;
    private SoundPool soundPool;

    Sound(Context context){
        assetManager = context.getAssets();
        soundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

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

    void play(int soundId){
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
