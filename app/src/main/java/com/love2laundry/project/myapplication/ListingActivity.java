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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cartDb = new Cart(this);

        super.onCreate(savedInstanceState);
        super.Config();

        setContentView(R.layout.activity_listing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        currencySymbol= sharedpreferences.getString("currencySymbol", null);
        countryCode = sharedpreferences.getString("country", null);
        request = getIntent().getStringExtra("request");
        RequestQueue queue = Volley.newRequestQueue(this);


        //cartDb.deleteAll(androidId,countryCode);
        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.checkout);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long count = cartDb.getCount(androidId, countryCode);


                sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

                String member_id = sharedpreferences.getString("member_id", null);
                startActivity(new Intent(ListingActivity.this,
                        LoginActivity.class));

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
                            sp.putString("franchise", franchise.toString());
                            sp.commit();


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




        //services = new ArrayList<>();
        //lv = findViewById(R.id.list);
        //Log.e(TAG, "service_records " + service_records);


        /*
        ListAdapter adapter = new SimpleAdapter(ListingActivity.this, services,
                R.layout.tabs_data, new String[]{"title","content", "total", "id", "price", "discount", "offerPrice"},
                new int[]{R.id.title,R.id.content, R.id.quantity, R.id.id, R.id.price, R.id.discount, R.id.offerPrice}) {
            @SuppressLint("WrongConstant")
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View v = super.getView(position, convertView, parent);


                TextView discount = v.findViewById(R.id.discount);
                TextView price = v.findViewById(R.id.price);
                TextView offerPrice = v.findViewById(R.id.offerPrice);
                Double discountAmount=Double.parseDouble(discount.getText().toString());

                price.setText(currencySymbol+price.getText().toString());
                offerPrice.setText(currencySymbol+offerPrice.getText().toString());


                if(discountAmount>0){
                    discount.setText(discount.getText().toString() + "%");


                }else {
                    discount.setVisibility(View.GONE);
                    price.setVisibility(View.GONE);
                }

                ImageView ivBasicImage = (ImageView) findViewById(R.id.mobileImagePath);
                //new RetrieveFeedTask(ivBasicImage).execute(services.get(position).get("mobileImagePath"));

                ImageButton add = v.findViewById(R.id.add);

                final Set<String> set = new HashSet<String>();
                add.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        TextView tv = v.findViewById(R.id.quantity);
                        String total = tv.getText().toString();
                        quantity = Integer.parseInt(total);
                        quantity++;
                        totalServices.put(Integer.parseInt(services.get(position).get("id")), quantity);
                        set.add(services.get(position).get("title"));
                        set.add(quantity.toString());

                        tv.setText(quantity.toString());
                        Set<String> fetch = sharedpreferences.getStringSet(services.get(position).get("id"), null);
                        Integer service_id = Integer.parseInt(services.get(position).get("id"));
                        String serviceName = services.get(position).get("title");
                        String content = services.get(position).get("content");

                        String unitPrice = services.get(position).get("offerPrice");
                        Integer category_id = Integer.parseInt(services.get(position).get("category_id"));
                        String categoryName = services.get(position).get("categoryName");

                        String mobileImage = services.get(position).get("mobileImagePath");
                        String desktopImage = services.get(position).get("desktopImage");
                        String isPackage = services.get(position).get("isPackage");
                        String preferencesShow = services.get(position).get("preferencesShow");
                        String discountAmount = services.get(position).get("discountAmount");


                        HashMap<String, String> item = new HashMap<>();
                        item = cartDb.getService(androidId, service_id, countryCode);

                        if (item.isEmpty() == true) {

                            cartDb.insertCart(androidId, service_id, content, serviceName, Double.parseDouble(unitPrice), Double.parseDouble(unitPrice) * quantity, quantity,
                                    category_id, categoryName, countryCode, services.get(position).get("mobileImagePath"), desktopImage, isPackage, preferencesShow, Double.parseDouble(discountAmount) * quantity);

                        } else {
                            cartDb.updateCart(androidId, service_id, quantity, Double.parseDouble(unitPrice), Double.parseDouble(discountAmount), countryCode);
                        }
                    }
                });
                ImageButton minus = v.findViewById(R.id.minus);
                minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Integer service_id = Integer.parseInt(services.get(position).get("id"));
                        String unitPrice = services.get(position).get("offerPrice");
                        String discountAmount = services.get(position).get("discountAmount");
                        TextView tv = v.findViewById(R.id.quantity);
                        String total = tv.getText().toString();
                        quantity = Integer.parseInt(total);

                        HashMap<String, String> item = new HashMap<>();
                        item = cartDb.getService(androidId, service_id, countryCode);

                        if (quantity > 0) {
                            quantity--;
                            set.add(services.get(position).get("title"));
                            set.add(quantity.toString());
                            cartDb.updateCart(androidId, service_id, quantity, Double.parseDouble(unitPrice), Double.parseDouble(discountAmount), countryCode);
                        } else {
                            cartDb.deleteCartItem(androidId, service_id, countryCode);
                        }
                        tv.setText(quantity.toString());
                    }
                });

                return v;
            }


        };
        lv.setAdapter(adapter);
        */
    }


    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int MINIMO_TAM = 10;
    public static final int MAXIMO_TAM = 640;

    public static Bitmap loadRemoteImage(String urlImagen) {
        if (null == urlImagen) {
            return null;
        }

        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;

        DefaultHttpClient httpclient = new DefaultHttpClient();
        //httpclient.addRequestInterceptor(new GzipHttpRequestInterceptor());
        //httpclient.addResponseInterceptor(new GzipHttpResponseInterceptor());

        try {
            String urlSinEspacios = urlImagen.toString().replace(" ", "+");

            HttpGet httpget = new HttpGet(urlSinEspacios);
            HttpEntity entity = httpclient.execute(httpget).getEntity();

            is = entity.getContent();
            bis = new BufferedInputStream(is, IO_BUFFER_SIZE);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bis, null, o);
            try {
                bis.close();
                is.close();
            } catch (Exception e) {
            }

            //Calcular mejor escala
            int scale = 1;
            if (o.outHeight > MAXIMO_TAM || o.outWidth > MAXIMO_TAM) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(MAXIMO_TAM / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Descargar el real
            entity = httpclient.execute(httpget).getEntity();
            is = entity.getContent();
            bis = new BufferedInputStream(is, IO_BUFFER_SIZE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inTempStorage = new byte[16 * 1024];
            options.inSampleSize = scale;
            bm = BitmapFactory.decodeStream(bis, null, options);
            httpclient.getConnectionManager().shutdown();
        } catch (Exception e) {

            bm = null;
        } finally {
            try {
                bis.close();
                is.close();
                httpclient.getConnectionManager().shutdown();
            } catch (Exception e) {
            }
        }

        return bm;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;


        private ImageView ivBasicImage;
        public RetrieveFeedTask(ImageView i ){
            ivBasicImage=i;
        }

        protected String doInBackground(String... urls) {



            try {
                URL url = null;
                try {
                    url = new URL(urls[0]);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ivBasicImage.setImageBitmap(bmp);
                } catch (MalformedURLException e) {
                    Log.e(TAG,"MalformedURLException + "+e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG,"IOException + "+e.getMessage());
                }
            } catch (Exception e) {
                this.exception = e;
                Log.e(TAG,"Exception "+ e.getMessage());
                //return null;
            } finally {
                //is.close();
            }

            return null;
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}