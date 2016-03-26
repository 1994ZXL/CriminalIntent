package com.example.zxl.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ZXL on 2015/9/24.
 */
public class CrimeLab {
    private static ArrayList<Crime> mCrimes;

    private static final String TAG = "CrimeLab";

    private static final String FILENAME = "crimes.json";
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext){
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        /*
        mCrimes = new ArrayList<Crime>();
        for(int i = 0;i < 3;i++){
            Crime c = new Crime();
            c.setTitle("Crime #" + i);;
            c.setSolved(i % 2 == 0);
            mCrimes.add(c);
        }
        */

        try {
            mCrimes = mSerializer.loadCrimes();
            Log.e(TAG, "loadCrimes" + mCrimes);
        } catch (Exception e){
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "crimes加载错误: ", e);
        }

    }

    public boolean saveCrimes(){
        try{
            mSerializer.saveCrime(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e){
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

    public static CrimeLab get(Context c){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for(Crime c : mCrimes){
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }

    public static Crime get(UUID id){
        Crime result = null;
        for(Crime m : mCrimes){
            if(m.getId().equals(id)){
                result = m;
                break;
            }
        }
        return result;
    }

    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    public void setCrimes(List<Crime> serverCrimes) {
        mCrimes = (ArrayList<Crime>)serverCrimes;
    }

    public boolean deleteCrime(Crime c) {
        return mCrimes.remove(c);
    }
}
