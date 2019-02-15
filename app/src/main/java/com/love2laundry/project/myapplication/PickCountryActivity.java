package com.love2laundry.project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PickCountryActivity extends Config {
    public PickCountryActivity() {
    }

    public interface ServerCallback {
        void onSuccess(JSONObject result);
    }


    private String TAG = PickCountryActivity.class.getSimpleName();
    RadioButton radioUk;
    RadioButton radioUae;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        String c=sharedpreferences.getString("country",null);

        if(c!=null) {
            if (c.equals("uk")) {
                startActivity(new Intent(PickCountryActivity.this, UKActivity.class));
                finish();

            }
        }
        Config config =new Config();
        if(!config.isConnected(this)) {
            config.buildDialog(this).show();
        }
        else {

            setContentView(R.layout.pick_country);
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("country", "uk");
            editor.commit();

            radioUk = findViewById(R.id.radioUk);
            radioUae = findViewById(R.id.radioUae);
        }
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("country");
        switch (view.getId()) {
            case R.id.radioUk:
                ((RadioButton) findViewById(R.id.radioUk)).setChecked(true);
                ((RadioButton) findViewById(R.id.radioUae)).setChecked(false);
                editor.putString("country", "uk");

                break;
            case R.id.radioUae:
                ((RadioButton) findViewById(R.id.radioUae)).setChecked(true);
                ((RadioButton) findViewById(R.id.radioUk)).setChecked(false);
                editor.putString("country", "uae");
                break;
        }
        editor.commit();
        // Toast.makeText(getBaseContext(), currencyCode , Toast.LENGTH_SHORT).show();
    }

    public void send(View v) {
        super.Config();
        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        country = sharedpreferences.getString("country", null);
        super.setCountry(country);
        // String currencyCode = sharedpreferences.getString("currencyCode",null);
        // Toast.makeText(getBaseContext(), currencyCode , Toast.LENGTH_SHORT).show();
        Map<String, String> postDataParams = new HashMap<String, String>();

        String server = sharedpreferences.getString("server", null);
        String url = server + sharedpreferences.getString("apiRegisterDevice", null);
        // Log.e(TAG, " " + url);


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {

                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");
                            Log.e(TAG, jsonObj.toString());
                            JSONObject device_detail = jsonObj.getJSONObject("device_detail");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            String deviceId = device_detail.getString("PKDeviceID");
                            editor.putString("deviceId", deviceId);
                            editor.commit();

                            //Log.e(TAG, country + " -> " + deviceId);

                            switch (country) {

                                case "uk":
                                    startActivity(new Intent(PickCountryActivity.this, UKActivity.class));
                                    break;
                                case "uae":
                                    //startActivity(new Intent(PickCountryActivity.this,UAEActivity.class));
                                    break;
                            }


                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException --> " + e.getMessage());
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("model", Build.MODEL);
                params.put("platform", "Android");
                params.put("version", Build.VERSION.RELEASE);
                params.put("id", androidId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(postRequest);


    }

    @Override
    public void onBackPressed() {

        finishAffinity();

    }
}