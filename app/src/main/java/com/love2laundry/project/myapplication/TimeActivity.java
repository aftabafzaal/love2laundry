package com.love2laundry.project.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class TimeActivity extends Config {

    public static interface ResultCallbackIF {

        public void resultOk(String resultString, Bundle resultMap);
        public void resultCancel(String resultString, Bundle resultMap);

    }

    private String TAG = TimeActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> timeList;
    private ListView lv;
    String franchise_id,action,title,type;
    String time;
    String timeSelected,hour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);
        franchise_id = getIntent().getStringExtra("franchise_id");
        action = getIntent().getStringExtra("action");
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");

        timeList = new ArrayList<>();
        Log.e(TAG,action);
        new GetDates().execute();
    }

    private class GetDates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(DatesActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response

            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server",null);

            String url = server+action;
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG,jsonStr);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray timeArray = jsonObj.getJSONArray("data");
                    Log.e(TAG,"Time Array - > "+timeArray.length());

                    // looping through All Contacts

                    for (int i = 0; i < timeArray.length(); i++) {
                        JSONObject c = timeArray.getJSONObject(i);
                        String status = c.getString("Class");
                        String availableTime = c.getString("Time");
                        Integer hour = c.getInt("Hour");

                        if(status.equals("Available")){
                            HashMap<String, String> contact = new HashMap<>();
                            contact.put("status", status);
                            contact.put("availableTime", availableTime);
                            contact.put("hour", hour.toString());
                            timeList.add(contact);
                        }
                    }


                }catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }



            }else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            lv=(ListView)findViewById(R.id.list);
            Log.e(TAG,timeList.toString());


            ListAdapter adapter = new SimpleAdapter(TimeActivity.this, timeList,
                    R.layout.time_list, new String[]{ "availableTime","status","hour"},
                    new int[]{R.id.time});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    timeSelected = timeList.get(i).get("availableTime");
                    hour = timeList.get(i).get("hour");
                    finish();
                }
            });

        }
    }

    @Override
    public void finish() {

        Intent data = new Intent();
        data.putExtra("timeSelected", timeSelected);
        data.putExtra("hour", hour);
        data.putExtra("returnType", type+"Time");
        setResult(RESULT_OK, data);
        super.finish();
    }
}