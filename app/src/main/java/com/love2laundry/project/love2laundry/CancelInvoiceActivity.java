package com.love2laundry.project.love2laundry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class CancelInvoiceActivity extends Config {

    private String TAG = CancelInvoiceActivity.class.getSimpleName();

    SharedPreferences member;
    Intent intent = null;
    SharedPreferences sharedPreferencesCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.Config();

        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
        String country =sharedPreferencesCountry.getString("country",null);

        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);
        String member_id =sharedpreferences.getString("member_id",null);


        final Map<String, String> postDataParams = new HashMap<String, String>();
        try{
            String invoiceId = getIntent().getStringExtra("invoiceId");
            postDataParams.put("id", member_id);

            String device_id = sharedPreferencesCountry.getString("deviceId", null);
            postDataParams.put("member_id", member_id);
            postDataParams.put("id", invoiceId);
            postDataParams.put("type", "Cancel");
            postDataParams.put("device_id", androidId);

            String server = sharedPreferencesCountry.getString("server", null);
            String url = server + sharedPreferencesCountry.getString("apiCancelOrder", "/apiv5/post_invoice_re_order_copy");
            RequestQueue queue = Volley.newRequestQueue(CancelInvoiceActivity.this);
            Log.e(TAG,url);

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONObject jsonObj = null;
                            Log.e(TAG, response);
                            try {

                                jsonObj = new JSONObject(response);

                                String result= jsonObj.getString("result");
                                if(result.equals("Success")) {
                                    String message= jsonObj.getString("message");
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(CancelInvoiceActivity.this, DashboardActivity.class);
                                    startActivityForResult(intent, 10);
                                    //finish();
                                    //return false;

                                }

                            } catch (JSONException e) {
                                Log.e(TAG+" JSONException ", e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
            Log.e( " JSONException ", e.getMessage());
            //e.printStackTrace();
        }






        /*
            if(country.equals("uk")){
                intent = new Intent(this, UKActivity.class);
            }else{
                intent = new Intent(this, UAEActivity.class);
            }
            startActivityForResult(intent, 10);
        */
    }
}
