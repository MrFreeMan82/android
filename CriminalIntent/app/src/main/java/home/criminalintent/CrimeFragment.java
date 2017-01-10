package home.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


/**
 * Created by Дима on 15.12.2016.
 */

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PERMISSION = "DialogPermission";
    private static final String DIALOG_PICTURE_DETAIL = "PictureDetail";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static  final int REQUEST_CONTACT = 2;
    private static final int REQUEST_CALL = 3;
    private static final int REQUEST_READ_CONTACT = 4;
    private static final int REQUEST_PHOTO = 5;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private boolean readContactEnabled = false;

    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitle;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String FormatDate(String dateFormat, Date date)
    {
        Locale loc = new Locale("ru");

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, loc);
        return sdf.format(date);
    }

    private String FormatTime(String timeFormat, Date time)
    {
        Locale loc = new Locale("ru");

        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, loc);
        return sdf.format(time);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Uri contactUri;
            switch(requestCode)
            {
                case REQUEST_DATE:
                    Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                    mCrime.setDate(date);
                    updateDate();
                    break;

                case REQUEST_TIME:
                    Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                    mCrime.setTime(time);
                    updateTime();
                    break;

                case REQUEST_CONTACT: {
                    if (data == null) break;
                    contactUri = data.getData();
                    // определение полей, значения которых должны быть возвращены запросом
                    String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

                    // Выполнение запроса - contactUri здесь выполняет функции условия where
                    Cursor c = getActivity().getContentResolver().
                            query(contactUri, queryFields, null, null, null);

                    try {
                        if (c.getCount() == 0) return;
                        c.moveToFirst();
                        String suspect = c.getString(0);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                    } finally {
                        c.close();
                    }
                    break;
                }

                case REQUEST_CALL: {
                    if (data == null) break;

                    contactUri = data.getData();
                    Cursor cContact = getActivity().getContentResolver().
                            query(contactUri, null, null, null, null);
                    try{
                        cContact.moveToFirst();
                        String id = cContact.getString(
                                cContact.getColumnIndex(ContactsContract.Contacts._ID)
                        );

                        Cursor cPhones = getActivity().getContentResolver().
                                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                      null,
                                      ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                                      null, null
                                );
                        try {
                            cPhones.moveToFirst();
                            String number = cPhones.getString(
                                    cPhones.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                            );
                            Uri num = Uri.parse("tel:" + number);
                            final Intent iDial = new Intent(Intent.ACTION_DIAL, num);
                            startActivity(iDial);

                        }finally {
                            cPhones.close();
                        }

                    }finally {
                        cContact.close();
                    }
                    break;
                }
                case REQUEST_PHOTO:{
                    updatePhotoView();
                }

            }
        }
    }

    private void updateTime() {
        String timeFormat = "hh:mm";
        mTimeButton.setText(FormatTime(timeFormat, mCrime.getTime()));
    }

    private void updateDate() {
        String dateFormat = "EEEE, dd MMMM yyyy";
        mDateButton.setText(FormatDate(dateFormat, mCrime.getDate()));
    }

    private void updateCallButton()
    {
        mCallButton.setEnabled(readContactEnabled);
    }

    private void updatePhotoView()
    {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.crime_fragment, container, false);

        mTitle = (EditText) v.findViewById(R.id.crime_title);
        mTitle.setText(mCrime.getTitle());
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getTime());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(getCrimeReport())
                            .setSubject(getString(R.string.crime_report_subject))
                            .setChooserTitle(getString(R.string.send_report))
                            .createChooserIntent();
                startActivity(i);

              /*  Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/
            }
        });

        final Intent pickContact =
                new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

       // pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null)
            mSuspectButton.setText(mCrime.getSuspect());

        mCallButton = (Button) v.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CALL);
            }
        });

        getPermissionToReadUserContacts();
        updateCallButton();

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(
                pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
        {
            mSuspectButton.setEnabled(false);
            mCallButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);
        if(canTakePhoto)
        {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                PictureDetail dialog = PictureDetail.newInstance(mPhotoFile);
                dialog.show(manager, DIALOG_PICTURE_DETAIL);
            }
        });
        updatePhotoView();
        return v;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport()
    {
        String solvedString;
        if(mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MMM dd";
        String dateString = FormatDate(dateFormat, mCrime.getDate());
        String suspect = mCrime.getSuspect();
        if(suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect, suspect);

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void getPermissionToReadUserContacts()
    {
        readContactEnabled = false;
        if(ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

                FragmentManager manager = getFragmentManager();
                PermissionDialog dialog = PermissionDialog.newInstance("READ_CONTACT");
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_READ_CONTACT);
                dialog.show(manager, DIALOG_PERMISSION);
            } else
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else readContactEnabled = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if(grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this.getContext(), "Read contact permission granted", Toast.LENGTH_SHORT).show();
                readContactEnabled = true;
            }else{
                Toast.makeText(this.getContext(), "Read contact permission denied", Toast.LENGTH_SHORT).show();
                readContactEnabled = false;
            }

        }
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updateCallButton();
    }
}
