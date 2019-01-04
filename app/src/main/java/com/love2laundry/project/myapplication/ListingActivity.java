package com.love2laundry.project.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListingActivity extends Config  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    /**
     * The {@link ViewPager} that will host the section contents.
     */
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
    String countryCode,currencyCode,request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cartDb = new Cart(this);

        super.onCreate(savedInstanceState);
        super.Config();

        setContentView(R.layout.activity_listing);

        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        currencyCode = sharedpreferences.getString("currencyCode",null);
        countryCode = sharedpreferences.getString("country",null);
        request = getIntent().getStringExtra("request");
        RequestQueue queue = Volley.newRequestQueue(this);



        //cartDb.deleteAll(androidId,countryCode);
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.checkout);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = cartDb.getCount(androidId,countryCode);



                sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

                String member_id = sharedpreferences.getString("member_id",null);
                if(member_id==null){
                    startActivity(new Intent(ListingActivity.this,
                            LoginActivity.class));
                }else{
                    startActivity(new Intent(ListingActivity.this,
                            CheckoutActivity.class));
                }
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObj = null;

                        try {

                            jsonObj = new JSONObject(response);
                            String result = jsonObj.getString("result");
                            JSONObject categories = jsonObj.getJSONObject("categories");
                            JSONArray data = categories.getJSONArray("data");

                            JSONObject franchise = jsonObj.getJSONObject("franchise_detail");

                            SharedPreferences.Editor sp = sharedpreferences.edit();
                            sp.putString("franchise_id", franchise.getString("ID"));
                            sp.commit();


                            cats = new String[data.length()];
                            service_records =new JSONArray[data.length()];

                            for(int i=0;i<data.length();i++){
                                JSONObject c = data.getJSONObject(i);
                                String MTitle= c.getString("MTitle");
                                JSONArray sr= c.getJSONArray("service_records");
                                cats[i]=MTitle;
                                service_records[i]=sr;
                            }

                            host = findViewById(R.id.tabHost);
                            host.setup();
                            host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

                                public void onTabChanged(String tabId) {

                                    int number =host.getCurrentTab();
                                    activity.setTitle(tabId);
                                    getServicesView(number,service_records[number],tabId);
                                }
                            });

                            for (int i = 0; i < cats.length; i++)
                            {
                                TabHost.TabSpec spec = host.newTabSpec(cats[i]);
                                spec.setIndicator(cats[i]);
                                spec.setContent(R.id.tab1);
                                host.addTab(spec);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG,"JSONException "+e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");

                Log.e(TAG,"VolleyError "+error.getMessage());
            }
        });
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);
    }

    protected void getServicesView(int number,JSONArray service_records,String categoryName){

        services = new ArrayList<>();
        lv = findViewById(R.id.list);
        Log.e(TAG,"service_records "+service_records);
        for (int j = 0; j < service_records.length(); j++) {
            try {
                // Log.e(TAG,"service_records "+service_records.getJSONObject(j));
                JSONObject obj=service_records.getJSONObject(j);
                String title = obj.getString("Title");
                String id = obj.getString("PKServiceID");
                String mobileImagePath = obj.getString("MobileImagePath");
                String desktopImage = obj.getString("DesktopImagePath");

                String isPackage = obj.getString("IsPackage");
                String preferencesShow = obj.getString("PreferencesShow");

                String category_id = obj.getString("CategoryID");
                String price = obj.getString("Price");
                String discount = obj.getString("DiscountPercentage");
                Double offerPrice,discountAmount;

                /// Log.e("Images -> ",desktopImage+" <--> "+service_records);
                if(Double.parseDouble(discount)>0) {
                    offerPrice = Double.parseDouble(price) - (Double.parseDouble(price) / 100) * Double.parseDouble(discount);
                }else{
                    offerPrice = Double.parseDouble(price);
                }

                offerPrice=Math.floor(offerPrice);
                discountAmount = Double.parseDouble(price) - offerPrice;
                HashMap<String, String> service = new HashMap<>();
                service.put("title", title);
                service.put("id", id);
                service.put("mobileImagePath", mobileImagePath);
                service.put("desktopImage", desktopImage);
                service.put("isPackage", isPackage);
                service.put("preferencesShow", preferencesShow);
                service.put("price", price);
                service.put("discount", discount);
                service.put("discountAmount",discountAmount.toString());
                service.put("offerPrice", offerPrice.toString());
                service.put("categoryName", categoryName);
                service.put("category_id", category_id);

                int serviceId = Integer.parseInt(id);

                if(totalServices.get(serviceId)!=null) {
                    service.put("total", totalServices.get(serviceId).toString());
                }else{
                    service.put("total", "0");
                }

                HashMap<String, String> item = new HashMap<>();
                item=cartDb.getService(androidId,serviceId,countryCode);
                // Log.e(TAG,serviceId+" "+item.isEmpty()+" "+item.get("id"));

                if(item.isEmpty()==true) {
                    service.put("total", "0");
                }else{
                    service.put("total", item.get("quantity"));
                }

                services.add(service);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ListAdapter adapter = new SimpleAdapter(ListingActivity.this, services,
                R.layout.tabs_data, new String[]{ "title","mobileImagePath","total","id","price","discount","offerPrice"},
                new int[]{R.id.title,R.id.mobileImagePath,R.id.quantity,R.id.id,R.id.price,R.id.discount,R.id.offerPrice})
        {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent)
            {
                final View  v = super.getView(position, convertView, parent);
                Button add= v.findViewById(R.id.add);

                ImageView ivBasicImage = (ImageView) findViewById(R.id.mobileImagePath);

                final Set<String> set = new HashSet<String>();
                add.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        TextView tv = v.findViewById(R.id.quantity);
                        String total=tv.getText().toString();
                        quantity=Integer.parseInt(total);
                        quantity++;
                        totalServices.put(Integer.parseInt(services.get(position).get("id")), quantity);
                        set.add(services.get(position).get("title"));
                        set.add(quantity.toString());

                        tv.setText(quantity.toString());
                        Set<String> fetch = sharedpreferences.getStringSet(services.get(position).get("id"), null);

                        Integer service_id=Integer.parseInt(services.get(position).get("id"));
                        String serviceName=services.get(position).get("title");
                        String unitPrice=services.get(position).get("offerPrice");
                        Integer category_id=Integer.parseInt(services.get(position).get("category_id"));
                        String categoryName=services.get(position).get("categoryName");

                        String mobileImage=services.get(position).get("mobileImagePath");
                        String desktopImage=services.get(position).get("desktopImage");
                        String isPackage=services.get(position).get("isPackage");
                        String preferencesShow=services.get(position).get("preferencesShow");
                        String discountAmount=services.get(position).get("discountAmount");

                        HashMap<String, String> item = new HashMap<>();
                        item=cartDb.getService(androidId,service_id,countryCode);

                        if(item.isEmpty()==true) {

                            cartDb.insertCart(androidId, service_id, serviceName, Double.parseDouble(unitPrice), Double.parseDouble(unitPrice) * quantity, quantity,
                                    category_id, categoryName, countryCode, services.get(position).get("mobileImagePath"),desktopImage,isPackage,preferencesShow,Double.parseDouble(discountAmount)* quantity);

                        }else{
                            cartDb.updateCart(androidId,service_id,quantity,Double.parseDouble(unitPrice),Double.parseDouble(discountAmount),countryCode);
                        }
                    }
                });
                Button minus= v.findViewById(R.id.minus);
                minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Integer service_id=Integer.parseInt(services.get(position).get("id"));
                        String unitPrice=services.get(position).get("offerPrice");
                        String discountAmount=services.get(position).get("discountAmount");
                        TextView tv = v.findViewById(R.id.quantity);
                        String total=tv.getText().toString();
                        quantity=Integer.parseInt(total);

                        HashMap<String, String> item = new HashMap<>();
                        item=cartDb.getService(androidId,service_id,countryCode);

                        if(quantity>0){
                            quantity--;
                            set.add(services.get(position).get("title"));
                            set.add(quantity.toString());
                            cartDb.updateCart(androidId,service_id,quantity,Double.parseDouble(unitPrice),Double.parseDouble(discountAmount),countryCode);
                        }else{
                            cartDb.deleteCartItem(androidId,service_id,countryCode);
                        }
                        tv.setText(quantity.toString());
                    }
                });

                return v;
            }


        };
        lv.setAdapter(adapter);
    }
}