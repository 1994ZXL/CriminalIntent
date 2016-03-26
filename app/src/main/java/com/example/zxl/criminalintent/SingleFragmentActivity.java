package com.example.zxl.criminalintent;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by ZXL on 2015/9/24.
 */
public abstract class SingleFragmentActivity extends Activity{
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_twopane);

        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(null == fragment){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        } else {
            //什么都不做
        }
    }
}
