package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends Navigation {

    EditText firstName,lastName,email,phone,referral,building,street,town,postCode,password,confirmPassword;
    private String TAG = AccountActivity.class.getSimpleName();
    public SharedPreferences sharedPreferencesCountry;
    String passwordText,confirmPasswordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        final String member_id = sharedpreferences.getString("member_id", null);
        //String firstName = sharedpreferences.getString("firstName", null);
        //String lastName = sharedpreferences.getString("lastName", null);




        String memberJson = sharedpreferences.getString("member_data", null);



        JSONObject member= null;
        try {
            member = new JSONObject(memberJson);

            firstName = (EditText) findViewById(R.id.first_name);
            firstName.setText(member.getString("FirstName"));

            lastName = (EditText) findViewById(R.id.last_name);
            lastName.setText(member.getString("LastName"));

            email = (EditText) findViewById(R.id.email);
            email.setText(member.getString("EmailAddress"));

            phone = (EditText) findViewById(R.id.phone);
            phone.setText(member.getString("Phone"));

            referral = (EditText) findViewById(R.id.referral_code);
            referral.setText(member.getString("ReferralCode"));

            postCode = (EditText) findViewById(R.id.post_code);
            postCode.setText(member.getString("PostalCode"));

            building = (EditText) findViewById(R.id.building);
            building.setText(member.getString("BuildingName"));


            street = (EditText) findViewById(R.id.street);
            street.setText(member.getString("StreetName"));

            town = (EditText) findViewById(R.id.town);
            town.setText(member.getString("Town"));

            password = (EditText) findViewById(R.id.password);



            confirmPassword = (EditText) findViewById(R.id.confirm_password);



            //town.setText(member.getString("Town"));



        } catch (JSONException e) {
            Log.e(TAG,"JSONException");
            e.printStackTrace();
        }



        Button updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                passwordText=password.getText().toString();
                confirmPasswordText=confirmPassword.getText().toString();

                final Map<String, String> postDataParams = new HashMap<String, String>();

                if(firstName.length() == 0 || firstName.equals("") || firstName == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter first name.",
                            Toast.LENGTH_LONG).show();
                    //EditText is empty
                }else if(lastName.length() == 0 || lastName.equals("") || lastName == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter first name.",
                            Toast.LENGTH_LONG).show();
                }else if(email.length() == 0 || email.equals("") || email == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter email.",
                            Toast.LENGTH_LONG).show();
                }else if(phone.length() == 0 || phone.equals("") || phone == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter phone number.",
                            Toast.LENGTH_LONG).show();
                }else if(referral.length() <=4 || referral.equals("") || referral == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid referral code.",
                            Toast.LENGTH_LONG).show();
                }else if(postCode.length() == 0 || postCode.equals("") || postCode == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter postal code.",
                            Toast.LENGTH_LONG).show();
                }else if(building.length() == 0 || building.equals("") || building == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter building name.",
                            Toast.LENGTH_LONG).show();
                }else if(street.length() == 0 || street.equals("") || street == null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Please enter street name.",
                            Toast.LENGTH_LONG).show();

                }else if(!passwordText.equals(confirmPasswordText)){
                    Toast.makeText(getApplicationContext(),
                            "You passwords do not match.",
                            Toast.LENGTH_LONG).show();

                } else{



                    try {
                        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
                        postDataParams.put("id", member_id);
                        // postDataParams.put("discount_referral_data","");
                        String device_id = sharedPreferencesCountry.getString("deviceId", null);
                        postDataParams.put("id", member_id);
                        postDataParams.put("first_name", firstName.getText().toString());
                        postDataParams.put("last_name", lastName.getText().toString());
                        postDataParams.put("phone", phone.getText().toString());
                        postDataParams.put("post_code", postCode.getText().toString());
                        postDataParams.put("email_address", email.getText().toString());
                        postDataParams.put("referral_code", referral.getText().toString());

                        postDataParams.put("building_name", building.getText().toString());
                        postDataParams.put("street_name", street.getText().toString());
                        postDataParams.put("town", town.getText().toString());
                        postDataParams.put("device_id", device_id);
                        postDataParams.put("password", passwordText );

                        String server = sharedPreferencesCountry.getString("server", null);
                        String url = server + sharedPreferencesCountry.getString("apiAccountUpdate", "/apiv5/post_account_update");
                        RequestQueue queue = Volley.newRequestQueue(AccountActivity.this);
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
                                                //String invoiceId= jsonObj.getString("InvoiceID");
                                                String message= jsonObj.getString("message");
                                                //String type= jsonObj.getString("type");


                                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                                                String memberData = jsonObj.getString("data");
                                                Log.e(TAG+" memberData -> ", ""+memberData);

                                                SharedPreferences.Editor member = sharedpreferences.edit();

                                                member.putString("member_data", memberData);
                                                member.putString("firstName",firstName.getText().toString());
                                                member.putString("lastName",lastName.getText().toString());
                                                member.putString("email",email.getText().toString());
                                                member.putString("phone",phone.getText().toString());
                                                member.putString("postCode",postCode.getText().toString());
                                                member.putString("buildName",building.getText().toString());
                                                member.putString("streetName",street.getText().toString());
                                                member.putString("town",town.getText().toString());
                                                member.commit();


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

        Log.e("member",""+member);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
}