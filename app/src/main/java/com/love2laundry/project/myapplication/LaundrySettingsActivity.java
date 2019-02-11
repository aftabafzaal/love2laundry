package com.love2laundry.project.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LaundrySettingsActivity extends Navigation {

    EditText accountNotes;
    private String TAG = LaundrySettingsActivity.class.getSimpleName();
    public SharedPreferences sharedPreferencesCountry;
    ArrayList<HashMap<String, String>> preferencesList;
    HashMap<String, String> selectedPreference = new HashMap<>();
    private RecyclerView recyclerView, prefrencesListView;
    private RecyclerView.Adapter cartAdapter, preferencesAdapter;
    String memberPreferences;
    String selectedP = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_laundry_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        final String member_id = sharedpreferences.getString("member_id", null);

        String memberJsonString = sharedpreferences.getString("member_data", null);

        memberPreferences = sharedpreferences.getString("member_preferences", null);

        accountNotes = (EditText) findViewById(R.id.account_notes);
        String notes="";
        try {
            JSONObject memberObj= new JSONObject(memberJsonString);
            notes=memberObj.getString("AccountNotes");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        accountNotes.setText(notes);

        //Log.e(TAG,memberJson);

        final Button updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButton.setEnabled(false);

                try {
                    selectedP = ((MyPreferencesAdapter) preferencesAdapter).getMemberSelected();
                    selectedP = (selectedP == null || selectedP.length() == 0) ? "" : (selectedP.substring(0, selectedP.length() - 2));


                    try{

                        final Map<String, String> postDataParams = new HashMap<String, String>();

                        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);

                        String device_id = sharedPreferencesCountry.getString("deviceId", null);
                        postDataParams.put("member_id", member_id);

                        postDataParams.put("device_id", device_id);
                        String notes=accountNotes.getText().toString();
                        postDataParams.put("preferences_data",notes+"||"+selectedP);

                        String server = sharedPreferencesCountry.getString("server", null);
                        String url = server + sharedPreferencesCountry.getString("apiPreferencesUpdate", "/apiv5/post_preferences_update");
                        RequestQueue queue = Volley.newRequestQueue(LaundrySettingsActivity.this);
                        Log.e(TAG,url);

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        JSONObject jsonObj = null;
                                        Log.e(TAG+"response->", response);
                                        try {

                                            jsonObj = new JSONObject(response);

                                            String result= jsonObj.getString("result");
                                            if(result.equals("Success")) {
                                                String message= jsonObj.getString("message");
                                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                                String preferenceData = jsonObj.getString("data");
                                                Log.e(TAG+" preferenceData -> ", ""+preferenceData);

                                                String memberData = jsonObj.getString("member_data");
                                                Log.e(TAG+" memberData -> ", ""+memberData);

                                                sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

                                                SharedPreferences.Editor member = sharedpreferences.edit();
                                                member.remove("member_data");
                                                member.remove("member_preferences");
                                                member.putString("member_data", memberData.toString());
                                                member.putString("member_preferences", preferenceData.toString());
                                                member.commit();
                                                finish();
                                                startActivity(getIntent());

                                            }

                                        } catch (JSONException e) {
                                            Log.e(TAG+"response->", response);
                                            Log.e(TAG+" JSONException ", e.getMessage());
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        finish();
                                        startActivity(getIntent());
                                        // error
                                        Log.e("Error.Response ", error.toString());
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                return postDataParams;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/x-www-form-urlencoded");
                                return headers;
                            }
                        };
                        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        queue.add(postRequest);

                    } catch (Exception e) {
                        finish();
                        startActivity(getIntent());
                        Log.e( " JSONException ", e.getMessage());

                        //e.printStackTrace();
                    }


                } catch (JSONException e) {
                    Log.e("TAG","sdasdasd"+e.getMessage());
                    e.printStackTrace();
                }

            }
        });


        new GetPreferences().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Navigation navigation =new Navigation();
        navigation.initView(navigationView,member_id);

    }

    public class GetPreferences extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server", null);
            String url = server + sharedpreferences.getString("apiPreferences", null);
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    preferencesList = new ArrayList<>();
                    JSONObject preferences = jsonObj.getJSONObject("preferences");
                    JSONArray data = preferences.getJSONArray("data");

                    JSONArray memberArray = new JSONArray(memberPreferences);
                    String memberSelected=null;
                    int d=0;
                    for (int i = 0; i < data.length(); i++) {

                        memberSelected=null;
                        if(memberArray.get(i).toString()!=null) {
                            JSONObject m = new JSONObject(memberArray.get(i).toString());
                            memberSelected=m.getString("ID");
                            d++;
                        }

                        JSONObject c = data.getJSONObject(i);
                        String id = c.getString("ID");
                        String title = c.getString("Title");
                        JSONArray children = c.getJSONArray("child_records");
                        //Log.e(TAG,"+ children "+children.get(0));
                        JSONObject defaultP=new JSONObject(children.get(0).toString());



                        selectedP+=defaultP.get("ID").toString()+"||";

                        HashMap<String, String> card = new HashMap<>();
                        card.put("id", id);
                        card.put("title", title);
                        card.put("children", children.toString());
                        selectedPreference.put(id,memberSelected);
                        preferencesList.add(card);
                    }


                } catch (final JSONException e) {
                    // Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                // Log.e(TAG, "Couldn't get json from server.");
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

            prefrencesListView = findViewById(R.id.preferences_listing);

            preferencesAdapter = new MyPreferencesAdapter(preferencesList, selectedPreference);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            prefrencesListView.setLayoutManager(mLayoutManager);
            prefrencesListView.setItemAnimator(new DefaultItemAnimator());
            prefrencesListView.setAdapter(preferencesAdapter);


        }
    }
}