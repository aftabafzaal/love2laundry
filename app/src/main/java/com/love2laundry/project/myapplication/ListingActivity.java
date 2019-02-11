package com.love2laundry.project.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListingActivity extends Navigation {

    private ViewPager mViewPager;

    private String TAG = MainActivity.class.getSimpleName();
    private Context context;

    ArrayList<HashMap<String, String>> services;
    private ListView lv;
    String[] cats;
    JSONArray[] service_records;

    TabHost host;
    private android.R.id viewHolder;
    List<Map<String, String>> data = new ArrayList<>();
    Integer quantity;
    TextView quantityText;
    Map<Integer, Integer> totalServices = new HashMap<Integer, Integer>();
    final Activity activity = this;
    private Cart cartDb;
    String countryCode, currencyCode, request;
    SharedPreferences spMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cartDb = new Cart(this);

        super.onCreate(savedInstanceState);


        Config config = new Config();

        if (!config.isConnected(this)) {
            config.buildDialog(this).show();
        }else{


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


        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        currencySymbol = sharedpreferences.getString("currencySymbol", null);
        countryCode = sharedpreferences.getString("country", null);
        request = getIntent().getStringExtra("request");
        RequestQueue queue = Volley.newRequestQueue(this);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.checkout);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = cartDb.getCount(androidId, countryCode);

                sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

                String member_id = sharedpreferences.getString("member_id", null);
                startActivity(new Intent(ListingActivity.this, LoginActivity.class));

            }
        });

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
                    sp.putString("franchise_id", franchise.getString("ID"));
                    sp.putString("minimumOrderAmount", franchise.getString("MinimumOrderAmount"));
                    sp.putString("settings", settings.toString());
                    sp.commit();
                    Log.e(franchise.getString("MinimumOrderAmount") + "franchise --> ", franchise.toString());

                    cats = new String[data.length()];
                    service_records = new JSONArray[data.length()];

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String MTitle = c.getString("MTitle");
                        JSONArray sr = c.getJSONArray("service_records");
                        cats[i] = MTitle;
                        service_records[i] = sr;
                    }

                    host = findViewById(R.id.tabHost);
                    host.setup();
                    host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

                        public void onTabChanged(String tabId) {

                            int number = host.getCurrentTab();
                            activity.setTitle(tabId);
                            getServicesView(number, service_records[number], tabId);
                        }
                    });

                    Resources res = getResources();
                    for (int i = 0; i < cats.length; i++) {

                        TabHost.TabSpec spec = host.newTabSpec(cats[i]);
                        spec.setIndicator(cats[i]);
                        spec.setContent(R.id.tab1);
                        host.addTab(spec);
                    }

                    host.setCurrentTab(1);
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

    private RecyclerView listing;
    private RecyclerView.Adapter myServicesAdapter;

    protected void getServicesView(int number, final JSONArray service_records, String categoryName) {


        listing = findViewById(R.id.listing);
        currencySymbol=sharedpreferences.getString("currencySymbol",null);
        myServicesAdapter = new MyServicesAdapter(ListingActivity.this,number,service_records,categoryName,currencySymbol,countryCode,androidId);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listing.setLayoutManager(mLayoutManager);
        listing.setItemAnimator(new DefaultItemAnimator());
        listing.setAdapter(myServicesAdapter);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListingActivity.this, UKActivity.class);
        startActivityForResult(intent, 10);
        startActivity(intent);
    }

}