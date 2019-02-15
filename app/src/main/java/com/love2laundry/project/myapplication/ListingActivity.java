package com.love2laundry.project.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListingActivity extends Navigation implements ServicesFragment.UpdateCart  {

    private ViewPager mViewPager;

    private String TAG = MainActivity.class.getSimpleName();
    private Context context;

    ArrayList<HashMap<String, String>> services;

    JSONArray[] service_records;

    Map<Integer, Integer> totalServices = new HashMap<Integer, Integer>();
    private Cart cartDb;
    String countryCode, currencyCode, request;
    SharedPreferences spMember;
    TextView itemMessage,servicesTotalView;
    Config config;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    String[] cats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cartDb = new Cart(this);

        super.onCreate(savedInstanceState);
        config = new Config();

        if (!config.isConnected(this)) {
            config.buildDialog(this).show();
        }else {


            setContentView(R.layout.activity_listing);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            Navigation navigation = new Navigation();

            spMember = getSharedPreferences("member", MODE_PRIVATE);

            navigation.initView(navigationView, spMember.getString("member_id", null));

            super.Navigation();

            RelativeLayout floatingActionButton = (RelativeLayout) findViewById(R.id.sticky_cart);

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long count = cartDb.getCount(androidId, countryCode);

                    sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

                    String member_id = sharedpreferences.getString("member_id", null);
                    startActivity(new Intent(ListingActivity.this, LoginActivity.class));

                }
            });


            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            currencySymbol = sharedpreferences.getString("currencySymbol", null);
            countryCode = sharedpreferences.getString("country", null);
            request = getIntent().getStringExtra("request");
            RequestQueue queue = Volley.newRequestQueue(this);

            if (!config.isConnected(this)) {
                config.buildDialog(this).show();
            }else{

                StringRequest stringRequest = new StringRequest(Request.Method.GET, request, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {

                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");
                            JSONObject categories = jsonObj.getJSONObject("categories");
                            JSONArray data = categories.getJSONArray("data");

                            JSONObject franchise = jsonObj.getJSONObject("franchise_detail");
                            JSONObject settings = jsonObj.getJSONObject("settings");

                            SharedPreferences.Editor sp = sharedpreferences.edit();
                            sp.putString("franchise", franchise.toString());
                            sp.putString("franchise_id", franchise.getString("ID"));
                            sp.putString("minimumOrderAmount", franchise.getString("MinimumOrderAmount"));
                            sp.putString("settings", settings.toString());
                            sp.commit();
                            Log.e(franchise.getString("MinimumOrderAmount") + "franchise --> ", franchise.toString());

                            service_records = new JSONArray[data.length()];


                            viewPager = (ViewPager) findViewById(R.id.viewPager);
                            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                            adapter = new TabAdapter(getSupportFragmentManager());

                            cats = new String[data.length()];

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                String MTitle = c.getString("MTitle");
                                JSONArray sr = c.getJSONArray("service_records");
                                service_records[i] = sr;
                                cats[i] = MTitle;
                                ServicesFragment servicesFragment=new ServicesFragment();
                                adapter.addFragment(servicesFragment,service_records[i],i,country,currencySymbol,
                                        MTitle,androidId);
                            }
                            viewPager.setAdapter(adapter);
                            viewPager.setCurrentItem(0);
                            tabLayout.setupWithViewPager(viewPager);

                            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                //TabLayout.Tab tab = tabLayout.getTabAt(i);

                                tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab);
                                TextView tab_name = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab);
                                tab_name.setText("" + cats[i]);

                                ImageView tabImage = (ImageView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tabImage);

                                Picasso.with(getApplicationContext()).load("https://www.love2laundry.com/uploads/categories/"+cats[i].toLowerCase()+"-icon.png").into(tabImage);

                                //tabImage.setImageURI(Uri.parse("https://www.love2laundry.com/uploads/categories/trousers-icon.png"));



                            }




                        } catch (JSONException e) {
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

        }

        updateCart();
    }


    @Override
    public void updateCart() {


        Double servicesTotal=cartDb.getServicesTotal(androidId,country);

        //Log.e("servicesTotal",servicesTotal.toString());

        TextView itemMessage = findViewById(R.id.item_message);
        TextView servicesTotalView = findViewById(R.id.services_total);

        if(servicesTotal==null || servicesTotal==0.0 ){
            itemMessage.setText("Skip Item Selection");
            servicesTotalView.setText("Order");
        }else{
            itemMessage.setText("Your Basket");
            servicesTotalView.setText(currencySymbol+config.displayPrice(servicesTotal)+"");
        }
    }

    @Override
    public void onBackPressed() {
       // finishAffinity();
        Intent intent = new Intent(this, UKActivity.class);
        startActivityForResult(intent, 10);
        startActivity(intent);
    }
}