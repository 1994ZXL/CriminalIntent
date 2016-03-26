package com.example.zxl.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ZXL on 2016/2/24.
 */
public class CrimeCameraActivty extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }

    @Override
    protected Fragment createFragment(){
        return new CrimeCameraFragment();
    }
}
