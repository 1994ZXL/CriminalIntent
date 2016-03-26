package com.example.zxl.criminalintent;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZXL on 2015/10/19.
 */
public class NetUtil {
    private final static String IP1 = "http://192.168.56.1:8080/";
    private final static String IP2 = "http://192.168.1.125";
    private final static String ENDPOINT = IP1;
    private final static String CRIME = "crime/";
    private final static String TAG = "NetUtil";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection conn = null;
        ByteArrayOutputStream out = null;
        try{
            conn = (HttpURLConnection) url.openConnection();
            out = new ByteArrayOutputStream();
            InputStream in = conn.getInputStream();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[10240];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();

        } finally {
            if(out != null){
                out.close();
            }
            if(conn != null){
                conn.disconnect();
            }
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(ENDPOINT+urlSpec));
    }

    public String changeCrime(Crime crime) {
        String urlString = ENDPOINT + CRIME + crime.getId().toString();
        return put(urlString, crime);
    }

    public String deleteCrime(Crime crime) {
        String urlString = ENDPOINT + CRIME+ crime.getId().toString();
        return delete(urlString, crime);
    }

    public String saveCrime(Crime crime) {
        String urlString = ENDPOINT + CRIME+ crime.getId().toString();
        return post(urlString, crime);
    }

    public List<Crime> getObject(){
        List<Crime> result = null;
        try {
            String url = Uri.parse(ENDPOINT+CRIME).buildUpon().build().toString();
            String response = getUrl(url);
            Log.i(TAG, "接收到原始数据是: " + response);
            result = new ArrayList<>();
            parseItems(result, response);
            Log.e(TAG, "转换后的对象是：" + result);
        } catch (IOException e){
            Log.e(TAG, "读取网络是失败", e);
        }
        return result;
    }

    public void parseItems(List<Crime> items, String source){
        try {
            JSONArray array = (JSONArray) new JSONArray(source);
            for(int i = 0; i< array.length(); i++){
                items.add(new Crime(array.getJSONObject(i)));
            }
        } catch (JSONException e){
            Log.e(TAG, "解析JSON时出错", e);
        }
    }

    public String post(String urlString, Crime crime){
        String rs = "post请求错误";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            osw.write(crime.toJSON().toString());
            Log.e(TAG, "POST" + crime.toJSON().toString());
            osw.flush();
            osw.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[10240];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } catch (Exception e) {
            Log.e(TAG, "POST ex: " + e.getLocalizedMessage());
            return rs;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public String put(String urlString, Crime crime){
        String rs = "put请求错误";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            osw.write(crime.toJSON().toString());
            Log.e(TAG, "PUT" + crime.toJSON().toString());
            osw.flush();
            osw.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[10240];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } catch (Exception e) {
            Log.e(TAG, "PUT ex: " + e.getLocalizedMessage());
            return rs;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public String delete(String urlString, Crime crime){
        String rs = "delete请求错误";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            osw.write(crime.toJSON().toString());
            Log.e(TAG, "DELETE" + crime.toJSON().toString());
            osw.flush();
            osw.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[10240];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        } catch (Exception e) {
            Log.e(TAG, "DETELE ex: " + e.getLocalizedMessage());
            return rs;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public ArrayList<Crime> getCrimes()  {
        String crimeJSON = null;
        try {
            crimeJSON = getUrl(CRIME);
        } catch (IOException e) {
            Log.e(TAG, "ee: "+e.getLocalizedMessage());
        }
        if(crimeJSON != null) {
            ArrayList<Crime> crimes = new ArrayList<Crime>();
            try {
                JSONArray array = (JSONArray) new JSONTokener(crimeJSON.toString()).nextValue();
                for (int i = 0; i < array.length();i++) {
                    crimes.add(new Crime(array.getJSONObject(i)));
                }
                Log.e(TAG, "crimes: " + crimes);
                return crimes;
            } catch (Exception e){
                Log.e(TAG, "ee: "+e.getLocalizedMessage());
            }
        }
        return null;
    }
}
