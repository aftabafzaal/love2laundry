package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditPaymentCardActivity extends Navigation {

    EditText name,title,number,cvvCode;
    private String TAG = EditPaymentCardActivity.class.getSimpleName();
    public SharedPreferences sharedPreferencesCountry;
    String id,cardName,cardTitle,expireMonth,expireYear,maskedCode,maskedNumber;
    static final String[] Months = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

    Spinner year,month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_payment_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        final String member_id = sharedpreferences.getString("member_id", null);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= thisYear+5; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        Spinner spinYear = (Spinner)findViewById(R.id.year);
        spinYear.setAdapter(adapter);


        final Spinner spinner = (Spinner) findViewById(R.id.month);
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Months);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterMonth);


        //String memberJson = sharedpreferences.getString("member_data", null);

        id = getIntent().getStringExtra("id");
        cardName = getIntent().getStringExtra("name");
        cardTitle = getIntent().getStringExtra("title");
        expireMonth = getIntent().getStringExtra("expireMonth");
        expireYear = getIntent().getStringExtra("expireYear");
        maskedCode = getIntent().getStringExtra("maskedCode");
        maskedNumber = getIntent().getStringExtra("maskedNumber");

        name = (EditText) findViewById(R.id.name);
        name.setText(cardName);

        title = (EditText) findViewById(R.id.title);
        title.setText(cardTitle);

        number = (EditText) findViewById(R.id.number);
        number.setHint(maskedNumber);

        cvvCode = (EditText) findViewById(R.id.ccv);
        cvvCode.setHint(maskedCode);

        year = (Spinner) findViewById(R.id.year);


        month = (Spinner) findViewById(R.id.month);

        //int monthAsInt = Calendar.getInstance().g;


        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    final Map<String, String> deleteDataParams = new HashMap<String, String>();
                    {

                        try {
                            sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
                            deleteDataParams.put("id", member_id);

                            String device_id = sharedPreferencesCountry.getString("deviceId", null);
                            deleteDataParams.put("member_id", member_id);
                            deleteDataParams.put("id", id);
                            deleteDataParams.put("type", "remove");
                            deleteDataParams.put("device_id", device_id);

                            String server = sharedPreferencesCountry.getString("server", null);
                            String url = server + sharedPreferencesCountry.getString("apiPaymentCard", "/apiv5/post_credit_card");
                            RequestQueue queue = Volley.newRequestQueue(EditPaymentCardActivity.this);
                            Log.e(TAG,url);

                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            JSONObject jsonObj = null;
                                            // Log.e(TAG, response);
                                            try {

                                                jsonObj = new JSONObject(response);

                                                String result= jsonObj.getString("result");
                                                if(result.equals("Success")) {
                                                    String message= jsonObj.getString("message");
                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(EditPaymentCardActivity.this, PaymentCardsActivity.class);
                                                    startActivityForResult(intent, 10);
                                                    finish();
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
                                    return deleteDataParams;
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
                    }
                }
                }
            });


        Button updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final Map<String, String> postDataParams = new HashMap<String, String>();

                if(name.length() == 0 || name.equals("") || name == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter name.",
                            Toast.LENGTH_LONG).show();
                    //EditText is empty
                }else if(title.length() == 0 || title.equals("") || title == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter title.",
                            Toast.LENGTH_LONG).show();
                }else if(number.length() == 0 || number.equals("") || number == null || number.length() != 16){

                    Toast.makeText(getApplicationContext(),
                            "Please enter valid credit card.",
                            Toast.LENGTH_LONG).show();

                } else if(cvvCode.length() < 3  )
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter valid cvv code.",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    try {
                        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
                        postDataParams.put("id", member_id);

                        String device_id = sharedPreferencesCountry.getString("deviceId", null);
                        postDataParams.put("member_id", member_id);
                        postDataParams.put("id", id);
                        postDataParams.put("type", "update");
                        postDataParams.put("title", title.getText().toString());
                        postDataParams.put("name", name.getText().toString());
                        postDataParams.put("number", number.getText().toString());
                        postDataParams.put("year", year.getSelectedItem().toString());
                        int monthAsInt = month.getSelectedItemPosition()+1;
                        postDataParams.put("year", year.getSelectedItem().toString());
                        postDataParams.put("device_id", device_id);
                        postDataParams.put("code", cvvCode.getText().toString());
                        postDataParams.put("month", String.format("%02d", monthAsInt));
                        String server = sharedPreferencesCountry.getString("server", null);
                        String url = server + sharedPreferencesCountry.getString("apiPaymentCard", "/apiv5/post_credit_card");
                        RequestQueue queue = Volley.newRequestQueue(EditPaymentCardActivity.this);
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
                                                Intent intent = new Intent(EditPaymentCardActivity.this, PaymentCardsActivity.class);
                                                startActivityForResult(intent, 10);
                                                finish();



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
                }
            }
        });

        //Log.e("member",""+member);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Navigation navigation =new Navigation();
        navigation.initView(navigationView,member_id);
    }
}