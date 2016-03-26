package com.example.zxl.criminalintent;

import android.app.Fragment;

/**
 * Created by ZXL on 2015/9/24.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
