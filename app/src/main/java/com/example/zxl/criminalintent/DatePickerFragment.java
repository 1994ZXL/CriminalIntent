package com.example.zxl.criminalintent;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private Date mDate;
    public static final String EXTRA_DATE = "crimeDate";

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStates){
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year,month,day);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d){
        Log.d("TEST", String.format("用户选择了%d年%d月%d日", y, m + 1, d));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);

        if(null == getTargetFragment()){
            return;
        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, cal.getTime());

            getTargetFragment().onActivityResult(11, Activity.RESULT_OK, intent);
        }
    }

}
