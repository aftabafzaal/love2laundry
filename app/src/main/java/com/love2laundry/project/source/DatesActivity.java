package com.love2laundry.project.source;


import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DatesActivity extends Config {

    public static interface ResultCallbackIF {

        public void resultOk(String resultString, Bundle resultMap);

        public void resultCancel(String resultString, Bundle resultMap);

    }

    private String TAG = DatesActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> dateList;
    private ListView lv;
    String franchise_id, action, title, type;
    String pickUpDate;
    String dateSelected;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);
        franchise_id = getIntent().getStringExtra("franchise_id");
        action = getIntent().getStringExtra("action");
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");


        activity.setTitle(title);
        dateList = new ArrayList<>();

        new GetDates().execute();
    }


    private class GetDates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server", null);
            String url = server + action;

            Log.e(TAG,url);
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray dates;
                    if (type.equals("delivery")) {
                        dates = jsonObj.getJSONArray("delivery");
                    } else {
                        dates = jsonObj.getJSONArray("pick");
                    }

                    for (int i = 0; i < dates.length(); i++) {
                        JSONObject c = dates.getJSONObject(i);
                        String date = c.getString("Date_List");
                        String dateNumber = c.getString("Date_Number");
                        String dateClass = c.getString("Date_Class");
                        String dateSelected = c.getString("Date_Selected");

                            HashMap<String, String> contact = new HashMap<>();
                            contact.put("date", date);
                            contact.put("disable", date);
                            contact.put("dateNumber", dateNumber);
                            contact.put("dateClass", dateClass);
                            contact.put("dateSelected", dateSelected);
                            contact.put("Available", dateClass);
                            dateList.add(contact);
                    }

                } catch (final JSONException e) {
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
            } else {
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

            lv = (ListView) findViewById(R.id.list);
            ListAdapter adapter = new SimpleAdapter(DatesActivity.this, dateList,
                    R.layout.date_list, new String[]{"date","disable", "dateNumber", "dateClass", "dateSelected"},
                    new int[]{R.id.date,R.id.disable}){

                //@SuppressLint("WrongConstant")
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    final View v = super.getView(position, convertView, parent);

                    TextView disable = v.findViewById(R.id.disable);
                    TextView date = v.findViewById(R.id.date);
                    if (dateList.get(position).get("dateClass").equals("Available")) {


                        date.setVisibility(View.VISIBLE);
                        disable.setVisibility(View.GONE);
                    }else{

                        disable.setEnabled(false);
                        disable.setClickable(false);
                        disable.setVisibility(View.VISIBLE);
                        date.setVisibility(View.GONE);
                    }
                    return v;
                }
            };
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (dateList.get(i).get("dateClass").equals("Available")) {
                        pickUpDate = dateList.get(i).get("dateNumber");
                        dateSelected = dateList.get(i).get("dateSelected");
                        finish();
                    }



                }
            });
        }
    }

    @Override
    public void finish() {

        Intent data = new Intent();
        data.putExtra("pickUpDate", pickUpDate);
        data.putExtra("dateSelected", dateSelected);
        data.putExtra("returnType", type + "Date");
        setResult(RESULT_OK, data);
        super.finish();
    }

}