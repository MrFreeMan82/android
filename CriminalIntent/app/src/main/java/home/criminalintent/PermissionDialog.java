package home.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Дима on 09.01.2017.
 */

public class PermissionDialog extends DialogFragment
{
    private static final String ARG_PERMISSION = "permission";

    public static PermissionDialog newInstance(String permission)
    {
        Bundle args = new Bundle();
        args.putCharSequence(ARG_PERMISSION, permission);
        PermissionDialog permissionDialog = new PermissionDialog();
        permissionDialog.setArguments(args);
        return permissionDialog;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String permission = getArguments().getString(ARG_PERMISSION);

        return new AlertDialog.Builder(getActivity())
                    .setTitle("Разрешить доступ к " + permission)
                    .setPositiveButton(R.string.permission_allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_OK);
                        }
                    })
                    .setNegativeButton(R.string.permission_deny, null)
                    .create();
    }

    private void sendResult(int resultCode)
    {
        if (getTargetFragment() == null) return;
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
    }
}
