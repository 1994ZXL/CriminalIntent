package com.example.zxl.criminalintent;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private Date mTime;
    public static final String EXTRA_TIME = "crimeTime";

    public TimePickerFragment() {
        // Required empty public constructor
    }

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStates){
        mTime = (Date) getArguments().getSerializable(EXTRA_TIME);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mTime);
        int time = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(),this,time,min,true);
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        Log.d("TEST", String.format("用户选择了%d时%d分", hourOfDay, minute));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
        cal.set(Calendar.MINUTE,minute);

        if(null == getTargetFragment()){
            return;
        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TIME, cal.getTime());

            getTargetFragment().onActivityResult(12, Activity.RESULT_OK, intent);
        }
    }
}
