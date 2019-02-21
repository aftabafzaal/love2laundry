package com.love2laundry.project.love2laundry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends Navigation {
    private String TAG = MainActivity.class.getSimpleName();
    public SharedPreferences sharedPreferencesCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        String member_id = sharedpreferences.getString("member_id", null);
        String firstName = sharedpreferences.getString("firstName", null);
        String lastName = sharedpreferences.getString("lastName", null);
        Log.e("Das", member_id + "-" + firstName + "-" + lastName + "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Navigation navigation =new Navigation();
        navigation.initView(navigationView, member_id,sharedpreferences);

        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);

        String server = sharedPreferencesCountry.getString("server", null);
        String request = server + sharedPreferencesCountry.getString("apiDashboard", "/apiv5/dashboard");
        request = request+"/"+member_id;
        Log.e(TAG,request);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {
                            //String currency=sharedPreferencesCountry.getString("currencyCode",null);
                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");
                            JSONObject data = jsonObj.getJSONObject("data");
                            Log.e("bb",result);
                            TextView name = (TextView) findViewById(R.id.name);
                            TextView email = (TextView) findViewById(R.id.email);
                            TextView address = (TextView) findViewById(R.id.address);
                            TextView loyalty_point = (TextView) findViewById(R.id.loyalty_point);
                            TextView pending = (TextView) findViewById(R.id.pending);
                            TextView processed = (TextView) findViewById(R.id.processed);
                            TextView canceled = (TextView) findViewById(R.id.canceled);
                            TextView completed = (TextView) findViewById(R.id.completed);

                            if(result.equals("Success")){
                                name.setText(data.getString("FirstName") + " " + data.getString("LastName"));
                                email.setText(data.getString("EmailAddress"));
                                address.setText(data.getString("BuildingName") + " " + data.getString("StreetName") + " " + data.getString("Town"));
                                loyalty_point.setText(data.getString("RemainingLoyaltyPoints"));
                                pending.setText(data.getString("Pending"));
                                processed.setText(data.getString("Processed"));
                                canceled.setText(data.getString("Cancel"));
                                completed.setText(data.getString("Completed"));
                            }
                        }
                        catch (JSONException e) {
                            Log.e(TAG, "JSONException " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");

                Log.e(TAG, "VolleyError " + error.getMessage());
            }
        });
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);

    }

    public void getInvoices(View v) {

        Log.e("View"," -> "+v.getId());
        String type="pending_invoices";
        switch (v.getId()) {
            case R.id.pending_invoices:
                type="Pending";

                break;
            case R.id.processed_invoices:
                type="Processed";
                break;

            case R.id.canceled_invoices:
                type="Cancel";
                break;

            case R.id.completed_invoices:
                type="Completed";
                break;
            default:
                type="Pending";
                break;
        }
        Intent intent = new Intent(DashboardActivity.this, InvoicesActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, 10);
    }
}