package home.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 12.01.2017.
 */

public class BeatBox
{
    private static final String TAG = "BeatBox";
    private static final String SOUND_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager assetManager;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context)
    {
        assetManager = context.getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    private void loadSounds()
    {
        String[] soundNames;

        try{
            soundNames = assetManager.list(SOUND_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
        }
        catch (IOException ioe){
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }

        for(String filename:soundNames)
        {
            try {
                String assetsPath = SOUND_FOLDER + "/" + filename;
                Sound sound = new Sound(assetsPath);
                load(sound);
                mSounds.add(sound);
            }catch (IOException ioe){
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException
    {
        AssetFileDescriptor afd = assetManager.openFd(sound.getAssetsPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundID(soundId);
    }

    public List<Sound> getSounds()
    {return mSounds;}

    public void play(Sound sound)
    {
        Integer soundId = sound.getSoundID();
        if(soundId == null) return;
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release()
    {mSoundPool.release();}
}
