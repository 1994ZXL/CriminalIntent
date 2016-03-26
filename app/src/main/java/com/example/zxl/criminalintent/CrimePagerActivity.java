package com.example.zxl.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ZXL on 2015/10/10.
 */
public class CrimePagerActivity extends Activity {
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getFragmentManager();
        mViewPager.setAdapter(new  FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                CrimeFragment fragment = CrimeFragment.newInstance(crime.getId());
                return fragment;
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //解决初始显示屏位置的问题
        UUID cirmeId = (UUID)getIntent().getSerializableExtra("CRIME_ID");
        int position = 0;
        for(Crime m : mCrimes){
            if(m.getId().equals(cirmeId)){
                mViewPager.setCurrentItem(position);
                break;
            } else {
                position ++;
            }
        }
    }
}
