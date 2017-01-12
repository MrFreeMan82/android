package home.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
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
    private AssetManager assetManager;
    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context)
    {
        assetManager = context.getAssets();
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
            String assetsPath = SOUND_FOLDER + "/" + filename;
            Sound sound = new Sound(assetsPath);
            mSounds.add(sound);
        }
    }

    public List<Sound> getSounds()
    {return mSounds;}
}
