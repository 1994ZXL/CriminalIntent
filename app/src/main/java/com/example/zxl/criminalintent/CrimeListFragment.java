package com.example.zxl.criminalintent;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ZXL on 2015/9/24.
 */
public class CrimeListFragment extends ListFragment{
    private ArrayList<Crime> mCrimes;
    private static final String TAG = "CrimeListFragment";

    @Override
    public void onResume(){
        super.onResume();
        new LoadCrimeByServerTask().execute();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Crime c= ((CrimeAdapter)getListAdapter()).getItem(position);
        Log.d(TAG, c.getTitle()+"was clicked");

        //Intent i =new Intent(getActivity(), CrimePagerActivity.class);
        //i.putExtra("CRIME_ID", c.getId());
        //startActivity(i);

        CrimeFragment f = CrimeFragment.newInstance(c.getId());
        FragmentManager fm = getActivity().getFragmentManager();
        fm.beginTransaction().replace(R.id.detailfragmentContainer, f).commit();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime>{

        public CrimeAdapter(ArrayList<Crime> crimes){
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if( null == convertView){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }

            Crime c = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());

            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(android.text.format.DateFormat.format("EEE yyyy年M月dd日 kk:mm", c.getDate()));

            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }

    /*public void initAdapter() {
        Log.e("0", "mCrimes:  " + mCrimes);
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }*/

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setHasOptionsMenu(true);

        PollService.setServiceAlarm(getActivity(), true);

        getActivity().setTitle(R.string.crime_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        Log.e(TAG,"onCreate"+mCrimes);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime c = new Crime();
                CrimeLab.get(getActivity()).addCrime(c);
                Log.d(TAG, c.getTitle() + "was clicked");

                //Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                //i.putExtra("CRIME_ID", c.getId());
                //startActivity(i);
                CrimeFragment f = CrimeFragment.newInstance(c.getId());
                FragmentManager fm = getActivity().getFragmentManager();
                fm.beginTransaction().replace(R.id.detailfragmentContainer, f).commit();
                //CrimePagerActivity

                return true;
            case R.id.menu_item_show_subtitle:
                return true;
            case R.id.menu_item_delete_crime:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class LoadCrimeByServerTask extends AsyncTask<Void, Void, ArrayList<Crime>>{

        @Override
        protected ArrayList<Crime> doInBackground(Void... params) {
            return new NetUtil().getCrimes();
        }

        @Override
        protected void onPostExecute(ArrayList<Crime> crimes){
            CrimeLab.get(getActivity()).setCrimes(crimes);
            mCrimes = crimes;
            CrimeAdapter adapter = new CrimeAdapter(mCrimes);
            Log.e(TAG, "LoadCrimeByServerTask mCrimes: " + mCrimes);
            setListAdapter(adapter);
            ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    public void updateUI(){
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }
}
