package com.example.zxl.criminalintent;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;

/**
 * Created by ZXL on 2015/10/15.
 */
public class CriminalIntentJSONSerializer {
    private Context mContext;
    private String mFilename;

    private static final String TAG = "JSONSerializer";

    public CriminalIntentJSONSerializer(Context c, String f){
        mContext = c;
        mFilename = f;
    }

    public void saveCrime(ArrayList<Crime> crimes) throws JSONException, IOException{
        JSONArray array = new JSONArray();
        for (Crime c: crimes){
            array.put(c.toJSON());
        }
        Writer writer = null;
        try{
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try{
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while(null != (line = reader.readLine())){
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i<array.length(); i++){
                crimes.add(new Crime(array.getJSONObject(i)));
                Log.e(TAG, "Crimes.add:  " +  crimes);
            }
        } catch (FileNotFoundException e){

        } finally {
            if (null != reader)
                reader.close();
        }

        return crimes;
    }
}
