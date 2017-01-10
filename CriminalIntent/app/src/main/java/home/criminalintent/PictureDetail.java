package home.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Дима on 10.01.2017.
 */

public class PictureDetail extends DialogFragment
{
    private static final String ARG_PICTURE = "picture";
    private ImageView mImageView;

    public static PictureDetail newInstance(File picture)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE, picture);
        PictureDetail pictureDetail = new PictureDetail();
        pictureDetail.setArguments(args);
        return pictureDetail;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        File picture = (File) getArguments().getSerializable(ARG_PICTURE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picture_detail, null);
        mImageView = (ImageView) v.findViewById(R.id.picture_detail);

        if(picture == null && !picture.exists()){
            mImageView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(picture.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity())
                   .setView(v)
                   .setTitle("Детали преступления")
                   .setPositiveButton("OK", null)
                   .create();
    }
}
