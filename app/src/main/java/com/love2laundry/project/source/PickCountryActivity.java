package com.love2laundry.project.source;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

            }else if (c.equals("uae")) {
                startActivity(new Intent(PickCountryActivity.this, UAEActivity.class));
                finish();

            }
        }else {
            Config config = new Config();
            if (!config.isConnected(this)) {
                config.buildDialog(PickCountryActivity.this, "", country).show();
            } else {

                setContentView(R.layout.pick_country);
                sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("country", "uk");
                editor.putString("addressType", getString(R.string.post_code));
                editor.commit();

                radioUk = findViewById(R.id.radioUk);
                radioUae = findViewById(R.id.radioUae);
            }
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
                editor.putString("addressType", getString(R.string.post_code));

                break;
            case R.id.radioUae:
                ((RadioButton) findViewById(R.id.radioUae)).setChecked(true);
                ((RadioButton) findViewById(R.id.radioUk)).setChecked(false);
                editor.putString("country", "uae");
                editor.putString("addressType", getString(R.string.area_name));
                break;
        }
        editor.commit();
        // Toast.makeText(getBaseContext(), currencyCode , Toast.LENGTH_SHORT).show();
    }

    public void send(View v) {

        SharedPreferences member,discount;
        SharedPreferences.Editor editor = getSharedPreferences("member", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        editor = getSharedPreferences("discount", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();


        super.Config();
        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        country = sharedpreferences.getString("country", null);
        super.setCountry(country);
        Map<String, String> postDataParams = new HashMap<String, String>();

        String server = sharedpreferences.getString("server", null);
        String url = server + sharedpreferences.getString("apiRegisterDevice", null);


        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {

                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");
                            JSONObject device_detail = jsonObj.getJSONObject("device_detail");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            String deviceId = device_detail.getString("PKDeviceID");
                            editor.putString("deviceId", deviceId);
                            editor.commit();

                            switch (country) {

                                case "uk":
                                    startActivity(new Intent(PickCountryActivity.this, UKActivity.class));
                                    break;
                                case "uae":
                                    startActivity(new Intent(PickCountryActivity.this,UAEActivity.class));
                                    break;
                            }


                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException --> " + e.getMessage());
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