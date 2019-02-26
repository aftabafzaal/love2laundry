package com.love2laundry.project.source;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ListingActivity extends Navigation implements ServicesFragment.UpdateCart  {

    private ViewPager mViewPager;

    private String TAG = MainActivity.class.getSimpleName();
    JSONArray[] service_records;
    private Cart cartDb;
    String countryCode, request;
    SharedPreferences spMember;
    Config config;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    String[] cats,mobileIcons;
    String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cartDb = new Cart(this);

        super.onCreate(savedInstanceState);
        config = new Config();

        if (!config.isConnected(this)) {
            config.buildDialog(ListingActivity.this,"home",country).show();
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
            navigation.initView(navigationView, spMember.getString("member_id", null),spMember);
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
            server =sharedpreferences.getString("server",null);
            currencySymbol = sharedpreferences.getString("currencySymbol", null);
            countryCode = sharedpreferences.getString("country", null);
            request = getIntent().getStringExtra("request");
            RequestQueue queue = Volley.newRequestQueue(this);

            if (!config.isConnected(this)) {
                config.buildDialog(ListingActivity.this,"home",country).show();
            }else{

                StringRequest stringRequest = new StringRequest(Request.Method.GET, request, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {

                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");


                            if ((jsonObj.isNull("categories")==false)) {
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
                                //Log.e(franchise.getString("MinimumOrderAmount") + "franchise --> ", franchise.toString());

                                service_records = new JSONArray[data.length()];


                                viewPager = (ViewPager) findViewById(R.id.viewPager);
                                tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                                adapter = new TabAdapter(getSupportFragmentManager());

                                cats = new String[data.length()];

                                mobileIcons = new String[data.length()];

                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject c = data.getJSONObject(i);
                                    String MTitle = c.getString("MTitle");
                                    ListingActivity.this.setTitle(cats[0]);
                                    String icon = c.getString("MobileIcon");
                                    JSONArray sr = c.getJSONArray("service_records");
                                    service_records[i] = sr;
                                    cats[i] = MTitle;
                                    mobileIcons[i] = icon;
                                    //Log.e("c.toString ",c.toString());
                                    ServicesFragment servicesFragment = new ServicesFragment();
                                    adapter.addFragment(servicesFragment, service_records[i], i, country, currencySymbol, MTitle, mobileIcons[i], androidId);
                                }

                                viewPager.setAdapter(adapter);
                                viewPager.setCurrentItem(0);
                                tabLayout.setupWithViewPager(viewPager);

                                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        int numTab = tab.getPosition();

                                        ListingActivity.this.setTitle(cats[numTab]);
                                    }
                                });

                                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                    //TabLayout.Tab tab = tabLayout.getTabAt(i);

                                    tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab);
                                    TextView tab_name = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab);
                                    tab_name.setText("" + cats[i]);

                                    ImageView tabImage = (ImageView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tabImage);

                                    Picasso.with(getApplicationContext()).load(server + "/uploads/categories/" + mobileIcons[i]).into(tabImage);

                                }

                            }else{

                                Toast.makeText(getApplicationContext(),
                                        "No franchise found please try again!",
                                        Toast.LENGTH_LONG).show();

                                onBackPressed();
                            }


                            } catch(JSONException e){
                                Log.e(TAG, "JSONException " + e.getMessage());
                            }
                        }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "VolleyError " + error.getMessage());
                    }
                });
                stringRequest.setShouldCache(false);
                queue.getCache().clear();
                queue.add(stringRequest);


            }
            updateCart();
        }


    }


    @Override
    public void updateCart() {


        Double servicesTotal=cartDb.getServicesTotal(androidId,country);


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
        Intent intent=null;
        if(country.equals("uk")) {
            intent = new Intent(this, UKActivity.class);
        }else{
            intent = new Intent(this, UAEActivity.class);
        }
        startActivityForResult(intent, 10);
        startActivity(intent);
    }
}