package com.flo.upnpdevicedetector;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static String TAG = MainActivity.class.getName();

    private Button mSearchBtn;
    private ListView mDeviceListView;

    private ArrayAdapter<String> mListHTTMPMsgAdapter;
    private ArrayAdapter<String> mListIPsAdapter;

    private ArrayList<String> mDeviceAnswerList;
    private ArrayList<String> mDeviceIPsList;

    private UPnPDeviceFinder mDevfinder  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBtn = (Button) findViewById(R.id.search_device_btn);
        mDeviceListView = (ListView) findViewById(R.id.device_list_view);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUPnPdevices();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_show_ips: showIPs();
                                        break;
            case R.id.action_show_http_msg: showHTTPMsg();
                                        break;
        }

        return true;
    }

    private void showHTTPMsg(){
        if(mDeviceAnswerList != null && mDeviceAnswerList != null){
            if(mListHTTMPMsgAdapter == null){
                mListHTTMPMsgAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDeviceAnswerList);
            }
            mDeviceListView.setAdapter(mListHTTMPMsgAdapter);
            mListHTTMPMsgAdapter.notifyDataSetChanged();
        }
    }

    private void showIPs(){
        if(mDeviceIPsList != null){
            if(mListIPsAdapter == null){
                mListIPsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDeviceIPsList);
            }
            mDeviceListView.setAdapter(mListIPsAdapter);
            mListIPsAdapter.notifyDataSetChanged();
        }
    }

    private void parseIPs(){
        if(mDeviceAnswerList != null){
            for(String msg : mDeviceAnswerList){
                String ip = SSDPUtils.parseIP(msg);

                if(!mDeviceIPsList.contains(ip)){
                    mDeviceIPsList.add(ip);
                }
            }
        }
    }

    private void searchUPnPdevices(){
        Log.e(TAG, "searchUPnPdevices");

        if(mDevfinder == null){
            mDevfinder = new UPnPDeviceFinder(true);
        }

        new SearchUPnPdevicesTask().execute();
    }

    private void updateUI(ArrayList<String> list){
        if(list != null){
            Log.e(TAG, "updateUI");

            if(mDeviceAnswerList == null){
                mDeviceAnswerList = new ArrayList<String>();
            }
            if(mDeviceIPsList == null){
                mDeviceIPsList = new ArrayList<String>();
            }

            mDeviceAnswerList.clear();
            mDeviceIPsList.clear();

            mDeviceAnswerList = list;
            parseIPs();

            showHTTPMsg();
        }
    }

    class SearchUPnPdevicesTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private Exception exception;

        protected ArrayList<String> doInBackground(Void...v) {
            return mDevfinder.getUPnPDevicesList();
        }

        protected void onPostExecute(ArrayList<String> devList) {
            Log.e(TAG, "SearchUPnPdevicesTask -- onPostExecute");
            updateUI(devList);
        }
    }
}
