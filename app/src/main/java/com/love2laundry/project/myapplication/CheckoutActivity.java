package com.love2laundry.project.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class CheckoutActivity extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView, prefrencesListView;
    private RecyclerView.Adapter cartAdapter, preferencesAdapter;
    private String TAG = CreditCardsActivity.class.getSimpleName();

    String franchise_id, pickUpTimeSelected, pickUpDate, pickUpTime, deliveryDate, deliveryTimeSelected, deliveryTime, cardId;
    Button pickUpDateButton, pickUpTimeButton, deliveryDateButton, deliveryTimeButton, checkout_button;
    EditText postCodeField, streetField, buildingField, townField, accountNotes, additionalInstruction;

    private Cart cartDb;
    String countryCode, currencyCode;
    ArrayList<HashMap<String, String>> preferencesList;
    HashMap<String, String> selectedPreference = new HashMap<>();
    public SharedPreferences sharedPreferencesCountry;

    TextView creditCardField;

    private DrawerLayout mDrawerLayout;
    public ArrayList<HashMap<String, String>>  services = new ArrayList<>();

    double servicesTotal = 0, preferenceTotal = 0, subTotal = 0, grandTotal = 0;
    Boolean showPreferences=false;
    String selectedP = "";
    String memberPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        super.Navigation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);
        memberPreferences = sharedpreferences.getString("member_preferences", null);

        final String member_id = sharedpreferences.getString("member_id", null);
        String firstName = sharedpreferences.getString("firstName", null);
        String lastName = sharedpreferences.getString("lastName", null);
        String phone = sharedpreferences.getString("phone", null);
        String street = sharedpreferences.getString("streetName", null);
        String building = sharedpreferences.getString("buildingName", null);
        String town = sharedpreferences.getString("town", null);
        final String member = sharedpreferences.getString("member_data", null);

        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);

        currencyCode = sharedPreferencesCountry.getString("currencyCode", null);
        countryCode = sharedPreferencesCountry.getString("country", null);
        franchise_id = sharedPreferencesCountry.getString("franchise_id", null);
        String postCode = sharedPreferencesCountry.getString("postCode", null);

        if (postCode == null) {
            postCode = sharedpreferences.getString("postCode", null);
            franchise_id = sharedpreferences.getString("franchise_id", null);
        }

        postCodeField = (EditText) findViewById(R.id.post_code);
        postCodeField.setText(postCode);

        streetField = (EditText) findViewById(R.id.street);
        streetField.setText(street);

        buildingField = (EditText) findViewById(R.id.building);
        buildingField.setText(building);

        townField = (EditText) findViewById(R.id.town);
        townField.setText(town);
        cartDb = new Cart(this);
        pickUpDateButton = (Button) findViewById(R.id.pick_up_date);

        accountNotes = (EditText) findViewById(R.id.account_notes);


        additionalInstruction = (EditText) findViewById(R.id.additional_instruction);

        showPreferences = cartDb.showPreferences(androidId, countryCode);
        if(showPreferences==true) {
            new GetPreferences().execute();
        }


        pickUpDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CheckoutActivity.this, DatesActivity.class);
                intent.putExtra("franchise_id", franchise_id);
                intent.putExtra("type", "Pickup");
                intent.putExtra("title", "Pick Up Date");
                intent.putExtra("action", sharedPreferencesCountry.getString("apiPickUpDate", null) + "/" + franchise_id);
                startActivityForResult(intent, 10);
            }
        });


        pickUpTimeButton = (Button) findViewById(R.id.pick_up_time);

        pickUpTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (pickUpDate == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please choose pick up date first!",
                            Toast.LENGTH_LONG).show();

                } else {

                    Intent intent = new Intent(CheckoutActivity.this, TimeActivity.class);

                    intent.putExtra("franchise_id", franchise_id);
                    intent.putExtra("type", "Pickup");
                    intent.putExtra("title", "Pick Up Time");
                    intent.putExtra("action", sharedPreferencesCountry.getString("apiPickUpTime", null) + "?id=" + franchise_id + "&date=" + pickUpDate + "&type=Pickup&device_id=" + androidId);
                    startActivityForResult(intent, 10);

                }
            }
        });


        deliveryDateButton = (Button) findViewById(R.id.delivery_date);
        deliveryDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pickUpTime == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please choose pick up time first!",
                            Toast.LENGTH_LONG).show();

                } else {

                    Intent intent = new Intent(CheckoutActivity.this, DatesActivity.class);
                    // Log.e("apiDeliveryDate",sharedPreferencesCountry.getString("apiDeliveryDate",null));
                    intent.putExtra("franchise_id", franchise_id);
                    intent.putExtra("type", "delivery");
                    intent.putExtra("title", "Delivery Date");
                    intent.putExtra("action", sharedPreferencesCountry.getString("apiPickUpDate", null) + "/" + franchise_id);
                    startActivityForResult(intent, 10);
                }
            }
        });


        deliveryTimeButton = (Button) findViewById(R.id.delivery_time);

        deliveryTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deliveryDate == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please choose delivery date first!",
                            Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(CheckoutActivity.this, TimeActivity.class);

                    intent.putExtra("franchise_id", franchise_id);
                    intent.putExtra("type", "delivery");
                    intent.putExtra("title", "Delivery Time");
                    intent.putExtra("action", sharedPreferencesCountry.getString("apiDeliveryTime", null) + "?id=" + franchise_id +
                            "&date=" + deliveryDate + "&type=Delivery&pick_date=" + pickUpDate + "&device_id=" + androidId + "&pick_time_hour=" + pickUpTime);
                    startActivityForResult(intent, 10);

                }
            }
        });



        services = cartDb.getServices(androidId, countryCode);
        cartAdapter = new MyCartAdapter(services);

        JSONArray servicesData = null;

        try {
            servicesData = ((MyCartAdapter) cartAdapter).getServices();
            servicesTotal = ((MyCartAdapter) cartAdapter).getServicesTotal();
        } catch (JSONException e) {
            Log.e("Log e ", e.getMessage());
            e.printStackTrace();
        }

        if (services.size() > 0) {

            int i = 0;

            recyclerView = findViewById(R.id.list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(cartAdapter);
        } else {
            Log.e("Item", "Empty");
        }

        //preferencesListingView = (RecyclerView) findViewById(R.id.preferences_listing);


        creditCardField = (TextView) findViewById(R.id.credit_card);
        creditCardField.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                Intent intent = new Intent(CheckoutActivity.this, CreditCardsActivity.class);
                intent.putExtra("type", "credit_card");
                intent.putExtra("title", "Credit Card");
                intent.putExtra("action", sharedPreferencesCountry.getString("apiCreditCards", null) + "/" + member_id);
                startActivityForResult(intent, 10);
                return false;
            }
        });

        Button checkoutButton = (Button) findViewById(R.id.checkout_button);

        final ArrayList<HashMap<String, String>> finalServices = services;
        final JSONArray finalServicesData = servicesData;
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String franchise = sharedPreferencesCountry.getString("franchise", null);
                String device_id = sharedPreferencesCountry.getString("deviceId", null);
                final Map<String, String> postDataParams = new HashMap<String, String>();


                if (pickUpDate == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please choose pick up date first!",
                            Toast.LENGTH_LONG).show();

                }else if (pickUpTime == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please choose pick up time first!",
                            Toast.LENGTH_LONG).show();

                }else if(deliveryDate==null){
                    Toast.makeText(getApplicationContext(),
                            "Please choose delivery date first!",
                            Toast.LENGTH_LONG).show();

                }else if(cardId==null){
                    Toast.makeText(getApplicationContext(),
                            "Please choose credit card first!",
                            Toast.LENGTH_LONG).show();

                }else {

                    if(showPreferences==true) {


                        try {
                            selectedP = ((MyPreferencesAdapter) preferencesAdapter).getSelected();
                            preferenceTotal = ((MyPreferencesAdapter) preferencesAdapter).getTotal();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        selectedP = (selectedP == null || selectedP.length() == 0) ? "" : (selectedP.substring(0, selectedP.length() - 1));
                    }


                    if(finalServicesData.length()>0) {
                        subTotal = servicesTotal + preferenceTotal;
                        grandTotal = subTotal;
                    }else{
                        grandTotal =15.00;
                    }


                    String ip = Utils.getIPAddress(true);

                    String notes = accountNotes.getText().toString();
                    String additional = additionalInstruction.getText().toString();

                    try {
                        postDataParams.put("id", "");
                        postDataParams.put("preferences_data", selectedP);
                        postDataParams.put("preference_total", "" + preferenceTotal);
                        postDataParams.put("service_total", "" + servicesTotal);
                        postDataParams.put("grand_total", "" + grandTotal);
                        postDataParams.put("sub_total", "" + subTotal);
                        postDataParams.put("card_id", cardId);
                        postDataParams.put("post_code", postCodeField.getText().toString());
                        postDataParams.put("building_name", buildingField.getText().toString());
                        postDataParams.put("street_name", streetField.getText().toString());
                        postDataParams.put("town", townField.getText().toString());
                        postDataParams.put("pick_date", pickUpDate);
                        postDataParams.put("pick_time", pickUpTimeSelected);
                        postDataParams.put("delivery_date", deliveryDate);
                        postDataParams.put("delivery_time", deliveryTimeSelected);
                        postDataParams.put("account_notes", notes);
                        postDataParams.put("additional_instruction", additional);
                        postDataParams.put("ip_address", ip);
                        postDataParams.put("member_data", member);
                        postDataParams.put("franchise_data", franchise);
                        postDataParams.put("services_data", finalServicesData.toString());
                        postDataParams.put("device_id", device_id);

                        String server = sharedPreferencesCountry.getString("server", null);
                        String url = server + sharedPreferencesCountry.getString("apiCheckout", null);
                        RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        JSONObject jsonObj = null;
                                        Log.e(TAG, response);
                                        try {

                                            jsonObj = new JSONObject(response);
                                            Log.e(TAG+" jsonObj ", ""+jsonObj);
                                            String result= jsonObj.getString("result");
                                            if(result.equals("Success")) {
                                                String invoiceId= jsonObj.getString("InvoiceID");
                                                String message= jsonObj.getString("message");
                                                String type= jsonObj.getString("type");

                                                Intent intent = new Intent(CheckoutActivity.this, InvoiceActivity.class);
                                                intent.putExtra("result", result);
                                                intent.putExtra("message", message);
                                                intent.putExtra("type", type);
                                                intent.putExtra("invoiceId", invoiceId);
                                                startActivityForResult(intent, 10);
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
                        Log.e(TAG + " JSONException ", e.getMessage());
                        //e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            String returnType = data.getExtras().getString("returnType");

            if (returnType.equals("PickupDate")) {
                pickUpDate = data.getExtras().getString("pickUpDate");
                String dateSelected = data.getExtras().getString("dateSelected");
                pickUpDateButton.setText(dateSelected);
            } else if (returnType.equals("PickupTime")) {
                pickUpTime = data.getExtras().getString("hour");
                pickUpTimeSelected = data.getExtras().getString("timeSelected");
                pickUpTimeButton.setText(pickUpTimeSelected);
            } else if (returnType.equals("deliveryDate")) {
                deliveryDate = data.getExtras().getString("pickUpDate");
                String deliveryDateSelected = data.getExtras().getString("dateSelected");
                deliveryDateButton.setText(deliveryDateSelected);
            } else if (returnType.equals("deliveryTime")) {
                deliveryTime = data.getExtras().getString("hour");
                deliveryTimeSelected = data.getExtras().getString("timeSelected");
                deliveryTimeButton.setText(deliveryTimeSelected);

            } else if (returnType.equals("credit_card")) {
                cardId = data.getExtras().getString("selectedCardId");
                creditCardField.setText(data.getExtras().getString("selectedCardTitle"));
            }
        }
    }

    public class GetPreferences extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server", null);
            String url = server + sharedpreferences.getString("apiPreferences", null);
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    preferencesList = new ArrayList<>();
                    JSONObject preferences = jsonObj.getJSONObject("preferences");
                    JSONArray data = preferences.getJSONArray("data");
                    JSONArray memberArray = new JSONArray(memberPreferences);
                    String memberSelected=null;
                    int d=0;
                    for (int i = 0; i < data.length(); i++) {


                        memberSelected=null;
                        if(memberArray.get(i).toString()!=null) {
                            JSONObject m = new JSONObject(memberArray.get(i).toString());
                            memberSelected=m.getString("ID");
                            d++;
                        }

                        JSONObject c = data.getJSONObject(i);
                        String id = c.getString("ID");
                        String title = c.getString("Title");
                        JSONArray children = c.getJSONArray("child_records");
                        //Log.e(TAG,"+ children "+children.get(0));
                        JSONObject defaultP=new JSONObject(children.get(0).toString());


                            Double price=0.0;

                            if(defaultP.get("PriceForPackage").equals("Yes")){
                                price=Double.parseDouble(defaultP.get("PriceForPackage").toString());
                            }
                            selectedP+=defaultP.get("ID").toString()+","+title+","+defaultP.get("Title").toString()+","+price+","+price+"|";


                        HashMap<String, String> card = new HashMap<>();
                        card.put("id", id);
                        card.put("title", title);
                        card.put("children", children.toString());
                        selectedPreference.put(id, memberSelected);
                        preferencesList.add(card);
                    }


                } catch (final JSONException e) {
                    // Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                // Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            prefrencesListView = findViewById(R.id.preferences_listing);

            preferencesAdapter = new MyPreferencesAdapter(preferencesList, selectedPreference);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            prefrencesListView.setLayoutManager(mLayoutManager);
            prefrencesListView.setItemAnimator(new DefaultItemAnimator());
            prefrencesListView.setAdapter(preferencesAdapter);


        }
    }





}