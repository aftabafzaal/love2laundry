package com.love2laundry.project.source;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;

public class SubListingActivity extends Navigation implements ServicesEditFragment.UpdateCart  {

    private ViewPager mViewPager;

    private String TAG = MainActivity.class.getSimpleName();
    JSONArray[] service_records;
    String countryCode, request;
    SharedPreferences spMember;
    Config config;

    private TabEditAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    HashMap<String, String> services = new HashMap<>();
    String[] cats,mobileIcons;
    String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        config = new Config();

        if (!config.isConnected(this)) {
            config.buildDialog(SubListingActivity.this,"home",country).show();
        }else {


            setContentView(R.layout.activity_sub_listing);
            services = (HashMap<String, String>) getIntent().getSerializableExtra("services");

            spMember = getSharedPreferences("member", MODE_PRIVATE);
            super.Navigation();

            RelativeLayout floatingActionButton = (RelativeLayout) findViewById(R.id.sticky_cart);

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finish();

                }
            });


            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            server =sharedpreferences.getString("server",null);
            currencySymbol = sharedpreferences.getString("currencySymbol", null);
            countryCode = sharedpreferences.getString("country", null);
            request = getIntent().getStringExtra("request");
            RequestQueue queue = Volley.newRequestQueue(this);

            if (!config.isConnected(this)) {
                config.buildDialog(SubListingActivity.this,"home",country).show();
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

                            service_records = new JSONArray[data.length()];


                            viewPager = (ViewPager) findViewById(R.id.viewPager);
                            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                            adapter = new TabEditAdapter(getSupportFragmentManager());

                            cats = new String[data.length()];

                            mobileIcons = new String[data.length()];

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject c = data.getJSONObject(i);
                                String MTitle = c.getString("MTitle");
                                SubListingActivity.this.setTitle(cats[0]);
                                String icon = c.getString("MobileIcon");
                                JSONArray sr = c.getJSONArray("service_records");
                                service_records[i] = sr;
                                cats[i] = MTitle;
                                mobileIcons[i] = icon;
                                ServicesEditFragment servicesFragment=new ServicesEditFragment();
                                adapter.addFragment(servicesFragment,service_records[i],i,country,currencySymbol,
                                        MTitle,mobileIcons[i],androidId,services);
                            }

                            viewPager.setAdapter(adapter);
                            viewPager.setCurrentItem(0);
                            tabLayout.setupWithViewPager(viewPager);

                            tabLayout.addOnTabSelectedListener(
                                    new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            //super.onTabSelected(tab);
                                            int numTab = tab.getPosition();

                                            SubListingActivity.this.setTitle(cats[numTab]);
                                            //prefs.edit().putInt("numTab", numTab).apply();
                                        }
                                    });

                            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                //TabLayout.Tab tab = tabLayout.getTabAt(i);

                                tabLayout.getTabAt(i).setCustomView(R.layout.custom_tab);
                                TextView tab_name = (TextView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tab);
                                tab_name.setText("" + cats[i]);

                                ImageView tabImage = (ImageView) tabLayout.getTabAt(i).getCustomView().findViewById(R.id.tabImage);

                                Picasso.with(getApplicationContext()).load(server+"/uploads/categories/"+mobileIcons[i]).into(tabImage);

                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "VolleyError " + error.getMessage());
                    }
                });
                stringRequest.setShouldCache(true);
                //queue.getCache().clear();
                queue.add(stringRequest);
            }

        }

        updateCart(services);
    }


    @Override
    public void updateCart(HashMap<String, String> selectedServices) {


        services=selectedServices;

        TextView itemMessage = findViewById(R.id.item_message);
        TextView servicesTotalView = findViewById(R.id.services_total);
        Double servicesTotal=0.0;
        for (Map.Entry me : selectedServices.entrySet()) {
            String string = (String) me.getValue();
            try {
                JSONObject json = new JSONObject(string);
                Double  price= Double.parseDouble(json.getString("price"));
                Integer quantity= Integer.parseInt(json.getString("quantity"));
                servicesTotal+=price*quantity;

            } catch (JSONException e) {
               Log.e(TAG,e.getMessage());
            }
        }

        if(servicesTotal==null || servicesTotal==0.0 ){
            itemMessage.setText("Skip Item Selection");
            servicesTotalView.setText("Order");
        }else{
            itemMessage.setText("Your Basket");
            servicesTotalView.setText(currencySymbol+config.displayPrice(servicesTotal)+"");
        }

    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("services",services);
        data.putExtra("returnType",  "services");
        setResult(RESULT_OK, data);
        super.finish();
    }


    @Override
    public void onBackPressed() {
        finish();
    }

}