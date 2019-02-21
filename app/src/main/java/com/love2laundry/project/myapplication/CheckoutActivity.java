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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class CheckoutActivity extends Navigation
        implements MyCartAdapter.PersonModifier,MyPreferencesAdapter.Modifier {

    private RecyclerView recyclerView, prefrencesListView;
    private RecyclerView.Adapter cartAdapter, preferencesAdapter;
    private String TAG = CreditCardsActivity.class.getSimpleName();

    String franchise_id, cardId;
    Button pickUpDateButton, pickUpTimeButton, deliveryDateButton, deliveryTimeButton, checkout_button;
    EditText streetField, buildingField, townField, accountNotes, additionalInstruction;

    TextView postCodeField;
    TextView preferencesHeadView;

    TextView preferenceTotalView,preferenceTotalTextView,grandTotalView;

    private Cart cartDb;
    String countryCode, currencyCode,currencySymbol;
    ArrayList<HashMap<String, String>> preferencesList;
    HashMap<String, String> selectedPreference = new HashMap<>();
    public SharedPreferences sharedPreferencesCountry,sharedPreferencesDiscount;

    TextView creditCardField,discountAmountText;

    RelativeLayout discountsView,discountForm,discountButtons;

    public ArrayList<HashMap<String, String>>  services = new ArrayList<>();


    Button discountCodeButton,referralCodeButton,applyButton;
    EditText codeField;
    String discountCodeType;
    boolean validDiscount;


    double servicesTotal = 0.00, preferenceTotal = 0.00, subTotal = 0.00, grandTotal = 0.00;
    Boolean showPreferences=false;
    String selectedP = "";
    String memberPreferences;
    String pickUpDate,pickUpTime,deliveryDate,deliveryTime,deliveryDateSelected,dateSelected,
            pickUpTimeSelected,deliveryTimeSelected;
    public HashMap<String, String> checkoutPreferences,checkoutPreferencesCopy;
    TextView cartAmount;

    TextView minimumOrderMessage;

    Double discountAmount = 0.00;
    String member_id;
    String member = null;
    Double minimumOrderAmount;
    Double discountedPrice;
    Config config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        super.Navigation();
        config= new Config();
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        member_id = sharedpreferences.getString("member_id", null);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Navigation navigation = new Navigation();
        navigation.initView(navigationView, member_id,sharedpreferences);
        sharedPreferencesDiscount = getSharedPreferences("discount", MODE_PRIVATE);


        if (!config.isConnected(this)) {
            config.buildDialog(this).show();

            Intent i = null;
            i = new Intent(this, DashboardActivity.class);
            startActivityForResult(i, 10);
        }else {


            final String firstName = sharedpreferences.getString("firstName", null);
            String lastName = sharedpreferences.getString("lastName", null);
            String phone = sharedpreferences.getString("phone", null);
            String street = sharedpreferences.getString("streetName", null);
            String building = sharedpreferences.getString("buildingName", null);
            String town = sharedpreferences.getString("town", null);
            member = sharedpreferences.getString("member_data", null);

            discountForm = (RelativeLayout) findViewById(R.id.discounts_form);
            discountsView = (RelativeLayout) findViewById(R.id.discounts);

            preferenceTotalView = findViewById(R.id.preferences_total);
            preferenceTotalTextView = findViewById(R.id.preferences_total_text);
            minimumOrderMessage = findViewById(R.id.minimum_order_message);
            preferencesHeadView = findViewById(R.id.preferences_heading);


            sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);

            currencyCode = sharedPreferencesCountry.getString("currencyCode", null);
            countryCode = sharedPreferencesCountry.getString("country", null);
            franchise_id = sharedPreferencesCountry.getString("franchise_id", null);
            String postCode = sharedPreferencesCountry.getString("postCode", null);

            currencySymbol = config.getCurrencySymbol(currencyCode);

            String franchiseData = null;

            if (postCode == null) {
                postCode = sharedpreferences.getString("postCode", null);
            }

            String minimum = sharedPreferencesCountry.getString("minimumOrderAmount", null);

            minimumOrderAmount=Double.parseDouble(minimum);

            postCodeField = (TextView) findViewById(R.id.post_code);
            postCodeField.setText(postCode);
            postCodeField.setEnabled(false);
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

            services = cartDb.getServices(androidId, countryCode);
            cartAdapter = new MyCartAdapter(services, this, country, androidId);
            ((MyCartAdapter) cartAdapter).setPersonModifier(this);


            JSONArray servicesData = null;

            try {
                servicesData = ((MyCartAdapter) cartAdapter).getServices();
                servicesTotal = cartDb.getServicesTotal(androidId, country);
            } catch (JSONException e) {
                Log.e("Log e ", e.getMessage());
            }

            if (services.size() > 0) {

                recyclerView = findViewById(R.id.list);
                LinearLayoutManager manager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(cartAdapter);
                //cartAdapter.notifyDataSetChanged();
            }

            discountAmountText = (TextView) findViewById(R.id.discount_amount);
            cartAmount = (TextView) findViewById(R.id.cart_amount);

            if (servicesData.length() > 0) {

                cartAmount.setText(currencySymbol + displayPrice(servicesTotal));

                subTotal = servicesTotal + preferenceTotal;
                grandTotal = subTotal;

            } else {
                grandTotal = minimumOrderAmount;
                preferenceTotalView.setVisibility(View.GONE);
                preferenceTotalTextView.setVisibility(View.GONE);
                cartAmount.setText(currencySymbol + "00:00");
            }

            minimumOrderMessage.setText(minimumOrderMessage.getText().toString() + " " + currencySymbol + config.displayPrice(minimumOrderAmount) + ".");

            showPreferences = cartDb.showPreferences(androidId, countryCode);


            if (showPreferences == true) {
                new GetPreferences().execute();
            } else {
                preferenceTotalTextView.setVisibility(View.GONE);
                preferenceTotalView.setVisibility(View.GONE);
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
                        Toast.makeText(getApplicationContext(), "Please choose pick up date first!", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(getApplicationContext(), "Please choose pick up time first!", Toast.LENGTH_LONG).show();

                    } else {

                        Intent intent = new Intent(CheckoutActivity.this, DatesActivity.class);
                        intent.putExtra("franchise_id", franchise_id);
                        intent.putExtra("type", "delivery");
                        intent.putExtra("title", "Delivery Date");
                        intent.putExtra("action", sharedPreferencesCountry.getString("apiDeliveryDate", null) + "/" + franchise_id);
                        startActivityForResult(intent, 10);
                    }
                }
            });


            deliveryTimeButton = (Button) findViewById(R.id.delivery_time);

            deliveryTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (deliveryDate == null) {
                        Toast.makeText(getApplicationContext(), "Please choose delivery date first!", Toast.LENGTH_LONG).show();
                    } else {

                        Intent intent = new Intent(CheckoutActivity.this, TimeActivity.class);

                        intent.putExtra("franchise_id", franchise_id);
                        intent.putExtra("type", "delivery");
                        intent.putExtra("title", "Delivery Time");
                        intent.putExtra("action", sharedPreferencesCountry.getString("apiDeliveryTime", null) + "?id=" + franchise_id + "&date=" + deliveryDate + "&type=Delivery&pick_date=" + pickUpDate + "&device_id=" + androidId + "&pick_time_hour=" + pickUpTime);
                        startActivityForResult(intent, 10);

                    }
                }
            });

            grandTotalView = (TextView) findViewById(R.id.grand_total_amount);

            grandTotalView.setText(currencySymbol + grandTotal);


            discountCodeButton = (Button) findViewById(R.id.discount_code);

            codeField = findViewById(R.id.code);
            applyButton = findViewById(R.id.apply);

            discountCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    discountCodeType = "discount";
                    codeField.setHint("Discount Code");
                    codeField.setText("");
                    discountForm.setVisibility(View.VISIBLE);
                }
            });

            referralCodeButton = (Button) findViewById(R.id.referral_code);

            referralCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    discountCodeType = "referral";
                    codeField.setHint("Referral Code");
                    codeField.setText("");
                    discountForm.setVisibility(View.VISIBLE);
                    // applyButton.setVisibility(View.VISIBLE);
                }
            });


            applyButton = (Button) findViewById(R.id.apply);

            applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (codeField.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter valid code!", Toast.LENGTH_LONG).show();

                    } else {
                        String device_id = sharedPreferencesCountry.getString("deviceId", null);
                        postCode(codeField.getText().toString(), member_id, device_id);
                    }

                }
            });


            Button addMoreServices;
            addMoreServices = (Button) findViewById(R.id.add_more_services);

            addMoreServices.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    SharedPreferences.Editor mp = sharedpreferences.edit();

                    mp.putString("streetName", streetField.getText().toString());
                    mp.putString("buildingName", buildingField.getText().toString());
                    mp.putString("town", townField.getText().toString());
                    mp.commit();


                    moveToListingActivity();
                }
            });

            creditCardField = (TextView) findViewById(R.id.credit_card);
            creditCardField.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {

                    Intent intent = new Intent(CheckoutActivity.this, CreditCardsActivity.class);

                    startActivityForResult(intent, 10);
                    return false;
                }
            });

            final JSONArray finalServicesData = servicesData;
            final Button checkoutButton = (Button) findViewById(R.id.checkout_button);
            checkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final ProgressBar loading= (ProgressBar) findViewById(R.id.loading);


                    String franchise = sharedPreferencesCountry.getString("franchise", null);
                    String device_id = sharedPreferencesCountry.getString("deviceId", null);

                    final Map<String, String> postDataParams = new HashMap<String, String>();

                    if (!config.isConnected(CheckoutActivity.this)) {
                        config.buildDialog(CheckoutActivity.this).show();

                    } else if (servicesTotal < minimumOrderAmount && grandTotal < minimumOrderAmount) {

                        Toast.makeText(getApplicationContext(), getString(R.string.minimum_order_message) + "  " + currencySymbol + minimumOrderAmount, Toast.LENGTH_LONG).show();

                    } else if (pickUpDate == null) {
                        Toast.makeText(getApplicationContext(), "Please choose pick up date first!", Toast.LENGTH_LONG).show();

                    } else if (pickUpTime == null) {
                        Toast.makeText(getApplicationContext(), "Please choose pick up time first!", Toast.LENGTH_LONG).show();

                    } else if (deliveryDate == null) {
                        Toast.makeText(getApplicationContext(), "Please choose delivery date first!", Toast.LENGTH_LONG).show();

                    } else if (cardId == null) {
                        Toast.makeText(getApplicationContext(), "Please choose credit card first!", Toast.LENGTH_LONG).show();

                    } else {

                        String ip = Utils.getIPAddress(true);

                        String notes = accountNotes.getText().toString();
                        String additional = additionalInstruction.getText().toString();

                        loading.setVisibility(View.VISIBLE);
                        checkoutButton.setVisibility(View.GONE);
                        checkoutButton.setEnabled(false);

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


                            if (sharedPreferencesDiscount.getBoolean("validDiscount", false) == true) {
                                postDataParams.put("discount_referral_data", sharedPreferencesDiscount.getString("codeString", null));
                                postDataParams.put("discount_total", discountAmount.toString());
                            }

                            String server = sharedPreferencesCountry.getString("server", null);
                            String url = server + sharedPreferencesCountry.getString("apiCheckout", null);
                            RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    JSONObject jsonObj = null;
                                    try {

                                        jsonObj = new JSONObject(response);
                                        Log.e(TAG + " jsonObj ", "" + jsonObj);
                                        String result = jsonObj.getString("result");
                                        String message = jsonObj.getString("message");
                                        if (result.equals("Success")) {
                                            String invoiceId = jsonObj.getString("InvoiceID");

                                            String type = jsonObj.getString("type");

                                            Intent intent = new Intent(CheckoutActivity.this, InvoiceActivity.class);
                                            intent.putExtra("result", result);
                                            intent.putExtra("message", message);
                                            intent.putExtra("type", type);
                                            intent.putExtra("invoiceId", invoiceId);
                                            startActivityForResult(intent, 10);

                                        } else {

                                            Toast.makeText(getApplicationContext(),
                                                    message,
                                                    Toast.LENGTH_LONG).show();

                                            loading.setVisibility(View.GONE);
                                            checkoutButton.setVisibility(View.VISIBLE);
                                            checkoutButton.setEnabled(true);

                                        }
                                    } catch (JSONException e) {
                                        loading.setVisibility(View.GONE);
                                        checkoutButton.setVisibility(View.VISIBLE);
                                        checkoutButton.setEnabled(true);
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loading.setVisibility(View.GONE);
                                    checkoutButton.setVisibility(View.VISIBLE);
                                    checkoutButton.setEnabled(true);
                                    Log.e("Error.Response ", error.toString());
                                }
                            }) {
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
                            postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                            queue.add(postRequest);

                        } catch (Exception e) {
                            loading.setVisibility(View.GONE);
                            checkoutButton.setVisibility(View.VISIBLE);
                            checkoutButton.setEnabled(true);
                            Log.e(TAG + " JSONException ", e.getMessage());
                        }
                    }
                }
            });


            discountedPrice = cartDb.getTotalForDiscount(androidId, country);


            applyDiscount();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            String returnType = data.getExtras().getString("returnType");

            if (returnType.equals("PickupDate")) {


                if(data.getExtras().getString("pickUpDate") != null && !data.getExtras().getString("pickUpDate").equals("")) {


                    pickUpTime=null;
                    pickUpTimeSelected=null;
                    pickUpTimeButton.setText("Select Pick Up Time");

                    deliveryDate=null;
                    deliveryDateSelected=null;
                    deliveryDateButton.setText("Select Delivery Date");

                    deliveryTime=null;
                    deliveryTimeSelected=null;
                    deliveryTimeButton.setText("Select Delivery Time");

                    pickUpDate = data.getExtras().getString("pickUpDate");
                    dateSelected = data.getExtras().getString("dateSelected");

                    SharedPreferences.Editor sd= sharedpreferences.edit();
                    sd.putString("pickUpDate",pickUpDate);
                    sd.putString("dateSelected",dateSelected);
                    sd.commit();

                    pickUpDateButton.setText(dateSelected);
                }


            } else if (returnType.equals("PickupTime")) {



                if(data.getExtras().getString("hour") != null && !data.getExtras().getString("hour").equals("")) {

                    deliveryDate=null;
                    deliveryDateSelected=null;
                    deliveryDateButton.setText("Select Delivery Date");

                    pickUpTime = data.getExtras().getString("hour");
                    pickUpTimeSelected = data.getExtras().getString("timeSelected");
                    pickUpTimeButton.setText(pickUpTimeSelected);
                }
            } else if (returnType.equals("deliveryDate")) {


                if(data.getExtras().getString("pickUpDate") != null && !data.getExtras().getString("pickUpDate").equals("")) {

                    deliveryTime=null;
                    deliveryTimeSelected=null;
                    deliveryTimeButton.setText("Select Delivery Time");

                    deliveryDate = data.getExtras().getString("pickUpDate");
                    deliveryDateSelected = data.getExtras().getString("dateSelected");

                    SharedPreferences.Editor sd= sharedpreferences.edit();
                    sd.putString("deliveryDate",deliveryDate);
                    sd.putString("deliveryDateSelected",deliveryDateSelected);
                    sd.commit();

                    deliveryDateButton.setText(deliveryDateSelected);
                }
            } else if (returnType.equals("deliveryTime")) {


                if(data.getExtras().getString("hour") != null && !data.getExtras().getString("hour").equals("")) {
                    deliveryTime = data.getExtras().getString("hour");
                    deliveryTimeSelected = data.getExtras().getString("timeSelected");
                    deliveryTimeButton.setText(deliveryTimeSelected);
                }

            } else if (returnType.equals("credit_card")) {


                String selectCardName=data.getExtras().getString("selectedCardName");
                Log.e("selectCardName "," <---> "+selectCardName);
                if(selectCardName != null && !selectCardName.equals("")) {
                    cardId = data.getExtras().getString("selectedCardId");
                    creditCardField.setText(data.getExtras().getString("selectedCardName"));
                }

            }
        }

        //getDiscount();

    }

    @Override
    public void onPersonSelected() {

        servicesTotal = cartDb.getServicesTotal(androidId, country);

        cartAmount.setText(currencySymbol+servicesTotal);
        applyDiscount();
        showPreferences = cartDb.showPreferences(androidId, countryCode);

        if(showPreferences==false) {

            preferenceTotal=0.00;
            preferenceTotalView.setVisibility(View.GONE);
            preferencesHeadView.setVisibility(View.GONE);
            prefrencesListView.setVisibility(View.GONE);
            preferenceTotalTextView.setVisibility(View.GONE);
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

            String jsonStr = sh.makeServiceCall(url+"?member_id="+member_id);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    preferencesList = new ArrayList<>();
                    JSONObject preferences = jsonObj.getJSONObject("preferences");
                    JSONArray data = preferences.getJSONArray("data");

                    JSONObject memberPreferences = preferences.getJSONObject("member_preferences");
                    preferenceTotal=preferences.getDouble("total");
                    String memberSelected=null;
                    int d=0;
                    for (int i = 0; i < data.length(); i++) {

                        memberSelected=null;

                        JSONObject c = data.getJSONObject(i);
                        String id = c.getString("ID");
                        String title = c.getString("Title");
                        JSONArray children = c.getJSONArray("child_records");

                        JSONObject defaultP;

                        Double price=0.00;

                        if(memberPreferences.get(id)!=null) {
                            defaultP = new JSONObject(memberPreferences.get(id).toString());
                            memberSelected=defaultP.getString("Title");
                        }else{
                            defaultP = new JSONObject(children.get(0).toString());
                            memberSelected=defaultP.getString("Title");
                        }

                        if(defaultP.get("PriceForPackage").equals("Yes")){
                            price=Double.parseDouble(defaultP.get("Price").toString());
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
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
            ((MyPreferencesAdapter) preferencesAdapter).setModifier(CheckoutActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            prefrencesListView.setLayoutManager(mLayoutManager);
            prefrencesListView.setItemAnimator(new DefaultItemAnimator());
            prefrencesListView.setAdapter(preferencesAdapter);
            preferenceTotalView.setText(currencySymbol+displayPrice(preferenceTotal));
            grandTotal=servicesTotal+preferenceTotal;
            grandTotalView.setText(currencySymbol+displayPrice(grandTotal));

        }
    }


    public void applyDiscount()
    {

        boolean validDiscount = sharedPreferencesDiscount.getBoolean("validDiscount",false);
        String codeString = sharedPreferencesDiscount.getString("codeString", null);
        discountedPrice=cartDb.getTotalForDiscount(androidId,country);
        String[] tempArray;
        String delimiter = "\\|";
        //Double minimumAmount=15.00;

        if(validDiscount==true && servicesTotal>minimumOrderAmount) {
            tempArray = codeString.split(delimiter);
            Double discount = 0.00;


            Boolean isPercentage = false;
            if (tempArray[0].equals("Discount")) {

                if (tempArray[4].equals("Percentage")) {
                    discount = (Double.parseDouble(tempArray[3]) / 100);
                    discountAmount = discountedPrice * discount;

                    isPercentage = true;
                } else {
                    discount = Double.parseDouble(tempArray[3]);
                    if (discount < discountedPrice) {
                        discountAmount = discountedPrice - discount;
                    }
                }
            } else {

                discount = Double.parseDouble(tempArray[3]);
                if (discount < discountedPrice) {
                    discountAmount = discountedPrice - discount;
                }
            }


            if (isPercentage == true) {
                grandTotal = servicesTotal - discountAmount + preferenceTotal;
                discountAmountText.setText("-" + currencySymbol + displayPrice(discountAmount));

            } else {
                grandTotal = servicesTotal - discount + preferenceTotal;
                discountAmountText.setText("-" + currencySymbol + displayPrice(discount));
            }
        }else if (servicesTotal<=0){
            grandTotal=minimumOrderAmount;
            discountAmount=0.00;
            discountAmountText.setText("-" + currencySymbol + "0.00" );
        }else if(validDiscount==true && servicesTotal<minimumOrderAmount) {
            grandTotal = servicesTotal + preferenceTotal;
            discountAmount=0.00;
            discountAmountText.setText("-" + currencySymbol + "0.00" );

        }



        grandTotalView.setText(currencySymbol+displayPrice(grandTotal));
        discountsView.setVisibility(View.VISIBLE);
        //codeField.setVisibility(View.GONE);
        //applyButton.setVisibility(View.GONE);
        //discountCodeButton.setVisibility(View.GONE);
        //referralCodeButton.setVisibility(View.GONE);

    }

    public void postCode(String code,String member_id,String device_id)
    {

        final Map<String, String> params = new HashMap<String, String>();
        try{
            params.put("member_id", member_id);
            params.put("is_bunddle", "0");
            params.put("code", code);
            params.put("device_id", device_id);

            String server = sharedPreferencesCountry.getString("server", null);
            String url;
            if(discountCodeType=="discount") {
                url = server + sharedPreferencesCountry.getString("apiPostDiscount", null);
            }else{
                url = server + sharedPreferencesCountry.getString("apiPostReferral", null);
                params.put("member_data", member);
            }
            RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            JSONObject jsonObj = null;
                            try {

                                jsonObj = new JSONObject(response);
                                String result= jsonObj.getString("result");
                                if(result.equals("Success")) {

                                    final SharedPreferences.Editor sp = sharedPreferencesDiscount.edit();
                                    sp.putString("codeString", jsonObj.getString("data").toString());
                                    sp.putBoolean("validDiscount", true);
                                    sp.commit();
                                    applyDiscount();

                                }else{

                                    Toast.makeText(getApplicationContext(),
                                            jsonObj.getString("message"),
                                            Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                Log.e(TAG+" JSONException ", e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
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
        }
    }

    public void managePreferencesTotal(){

        try {
            checkoutPreferences = ((MyPreferencesAdapter) preferencesAdapter).getSelected();

            checkoutPreferencesCopy=checkoutPreferences;
            int size = checkoutPreferencesCopy.size();
            Iterator it = checkoutPreferencesCopy.entrySet().iterator();
            //Log.e("iterator();",checkoutPreferencesCopy.entrySet().iterator().toString());
            String preferences_data ="" ;
            Double total=0.00;
            for (Map.Entry me : checkoutPreferencesCopy.entrySet()) {
                System.out.println("Key: "+me.getKey() + " & Value: " + me.getValue());
                String string = (String) me.getValue();

                JSONObject json = new JSONObject(string);
                String priceForPackage = json.getString("PriceForPackage");
                Double price = 0.00;
                if (priceForPackage.equals("Yes")) {
                    Log.e("json.toString --> ",json.toString());
                    price = Double.parseDouble(json.getString("Price"));
                    total += price;

                }

                selectedP += json.getString("ID") + "," + json.getString("categoryTitle")
                        + "," + json.getString("Title") + "," + price + "," + price + "|";


            }
            selectedP = (selectedP == null || selectedP.length() == 0) ? "" : (selectedP.substring(0, selectedP.length() - 1));

            preferenceTotal = total;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        preferenceTotalView.setText(currencySymbol+displayPrice(preferenceTotal));

        applyDiscount();
    }

    public String displayPrice(Double price){
        return String.format("%.2f", price);
    }

    public void managePrices(){


    }

    @Override
    public void onBackPressed() {
        moveToListingActivity();
    }


    private void moveToListingActivity(){
        Intent intent = new Intent(CheckoutActivity.this, ListingActivity.class);
        startActivityForResult(intent, 10);
        String server = sharedPreferencesCountry.getString("server", null);
        String action = sharedPreferencesCountry.getString("loadData", null);

        String q = "?type=load&device_id=" + androidId + "&post_code=" + postCodeField.getText().toString() + "&versions=1|2|3&more_info=1";
        String url = server + action + q;
        intent.putExtra("request", url);
        startActivity(intent);
    }



}