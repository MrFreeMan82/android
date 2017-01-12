package home.beatbox;

/**
 * Created by Дима on 12.01.2017.
 */

public class Sound
{
    private String mAssetsPath;
    private String mName;

    public Sound(String assetPath)
    {
        mAssetsPath = assetPath;
        String[] components = assetPath.split("/");
        String filename = components[components.length - 1];
        mName = filename.replace(".wav", "");
    }

    public String getAssetsPath()
    {return mAssetsPath;}

    public String getName()
    {return mName;}
}
