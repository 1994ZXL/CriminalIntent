package com.example.zxl.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.text.format.DateFormat;

import org.json.JSONException;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ZXL on 2015/9/21.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private Crime mCrime;
    private EditText mTitleField;
    private UUID mId;
    private Button mDateButton;
    private Button mTimeButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTANCT = 2;
    private static final String DIALOG_IMAGE = "image";

    public static CrimeFragment newInstance(UUID id) {
        CrimeFragment result = new CrimeFragment();
        result.setId(id);
        return result;
    }

    public void setId(UUID id){
        this.mId = id;
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建JavaBean
        //mCrime = new Crime();
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra("CRIME_ID");
        mCrime = CrimeLab.get(mId);
        setHasOptionsMenu(true);
    }

    private void showPhoto() {
        Photo p = mCrime.getmPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                new ChangeCrimeByServerTask().execute(mCrime);
                upDateList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateDate();
        updateTime();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this, 11);
                fragment.show(getFragmentManager(), "crimedatePicker");
                new ChangeCrimeByServerTask().execute(mCrime);
                upDateList();
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment fragment = TimePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this, 12);
                fragment.show(getFragmentManager(), "crimetimePicker");
                new ChangeCrimeByServerTask().execute(mCrime);
                upDateList();
            }
        });



        CheckBox solvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        solvedCheckbox.setChecked(mCrime.isSolved());
        solvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                new ChangeCrimeByServerTask().execute(mCrime);
            }
        });

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(), CrimeCameraActivty.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Photo p = mCrime.getmPhoto();
                if (p == null)
                    return;
                FragmentManager fm = getActivity().getFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;
        if (!hasCamera) {
            mPhotoButton.setEnabled(false);
        }

        Button reportButton = (Button)v.findViewById(R.id.crime_reprotButton);
        reportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTANCT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }

    @Override
    public void onActivityResult(int requsetCode, int resultCode, Intent i){
        if(resultCode != Activity.RESULT_OK){
            return;
        }else {
            if(requsetCode == 11){
                Date date = (Date)i.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
            }
            if(requsetCode == 12){
                Date time = (Date)i.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setDate(time);
                updateTime();

            }
            if (requsetCode == REQUEST_PHOTO) {
                String filename = i.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                if (filename != null) {
                    Photo p = new Photo(filename);
                    mCrime.setPhoto(p);
                    showPhoto();
                }
            }
            if (requsetCode == REQUEST_CONTANCT) {
                Uri contactUri = i.getData();
                String[] queryField = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };
                Cursor c = getActivity().getContentResolver().query(contactUri, queryField, null, null, null);
                if (c.getCount() == 0) {
                    c.close();
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                c.close();
            }
        }
    }

    private void updateDate(){
        mDateButton.setText(android.text.format.DateFormat.format("EEE yyyy年M月dd日", mCrime.getDate()));
    }


    private void updateTime(){
        mTimeButton.setText(android.text.format.DateFormat.format("kk:mm", mCrime.getDate()));
    }


    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
        new SaveCrimeByServerTask().execute(mCrime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                deleteCrime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCrime(){
        CrimeLab.get(getActivity()).deleteCrime(mCrime);
        new DeleteCrimeByServerTask().execute(mCrime);
        getActivity().finish();
    }

    //DELETE
    private class DeleteCrimeByServerTask extends AsyncTask<Crime, Void, String>{
        @Override
        protected String doInBackground(Crime... params){
            return new NetUtil().deleteCrime(params[0]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
        }
    }

    //PUT
    private class ChangeCrimeByServerTask extends AsyncTask<Crime, Void, String>{
        @Override
        protected String doInBackground(Crime... params){
            return new NetUtil().changeCrime(params[0]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
        }
    }

    //POST
    private class SaveCrimeByServerTask extends AsyncTask<Crime, Void, String>{
        @Override
        protected String doInBackground(Crime... params){
            return new NetUtil().saveCrime(params[0]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
        }
    }

    public void upDateList(){
        FragmentManager fm = getFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }


}
