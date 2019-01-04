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
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutActivity extends Navigation
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView,prefrencesListView;
    private RecyclerView.Adapter mAdapter,preferencesAdapter;
    private String TAG = CreditCardsActivity.class.getSimpleName();

    String franchise_id,pickUpDate,pickUpTime,deliveryDate,deliveryTime,cardId;
    Button pickUpDateButton,pickUpTimeButton,deliveryDateButton,deliveryTimeButton;
    EditText postCodeField,streetField,buildingField,townField;

    private Cart cartDb;
    String countryCode,currencyCode;
    ArrayList<HashMap<String, String>> preferencesList;

    public SharedPreferences sharedPreferencesCountry;

    TextView creditCardField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        super.Navigation();
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        final String member_id = sharedpreferences.getString("member_id",null);
        String firstName = sharedpreferences.getString("firstName",null);
        String lastName = sharedpreferences.getString("lastName",null);
        String phone = sharedpreferences.getString("phone",null);
        String street = sharedpreferences.getString("streetName",null);
        String building = sharedpreferences.getString("buildingName",null);
        String town = sharedpreferences.getString("town",null);

        sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);

        currencyCode = sharedPreferencesCountry.getString("currencyCode",null);
        countryCode = sharedPreferencesCountry.getString("country",null);
        franchise_id = sharedPreferencesCountry.getString("franchise_id",null);
        String postCode = sharedPreferencesCountry.getString("postCode",null);

        if(postCode==null){
            postCode = sharedpreferences.getString("postCode",null);
            franchise_id = sharedpreferences.getString("franchise_id",null);

        }
        // Log.e("Das",member_id+"-"+firstName+"-"+lastName);

        postCodeField = (EditText) findViewById(R.id.post_code);
        postCodeField.setText(postCode);

        streetField = (EditText) findViewById(R.id.street);
        streetField.setText(street);

        buildingField = (EditText) findViewById(R.id.building);
        buildingField.setText(building);

        townField = (EditText) findViewById(R.id.town);
        townField.setText(town);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Log.e("Api Pick Up Date",sharedPreferencesCountry.getString("apiPickUpDate", null));

        pickUpDateButton = (Button) findViewById(R.id.pick_up_date);

        pickUpDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CheckoutActivity.this, DatesActivity.class);
                intent.putExtra("franchise_id",franchise_id);
                intent.putExtra("type","Pickup");
                intent.putExtra("title","Pick Up Date");
                intent.putExtra("action", sharedPreferencesCountry.getString("apiPickUpDate", null) + "/" + franchise_id );
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
                    Log.e("apiDeliveryDate",sharedPreferencesCountry.getString("apiDeliveryDate",null));
                    intent.putExtra("franchise_id",franchise_id);
                    intent.putExtra("type","delivery");
                    intent.putExtra("title","Delivery Date");
                    intent.putExtra("action", sharedPreferencesCountry.getString("apiPickUpDate", null) + "/" + franchise_id );
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
                            "&date=" + deliveryDate + "&type=Delivery&pick_date="+pickUpDate+"&device_id=" + androidId + "&pick_time_hour="+pickUpTime);
                    startActivityForResult(intent, 10);

                }
            }
        });

        cartDb = new Cart(this);
        ArrayList<HashMap<String, String>>  services = new ArrayList<>();
        services = cartDb.getServices(androidId,countryCode);

        if(services.size()>0){

            int i=0;

            recyclerView = findViewById(R.id.list);
            mAdapter = new MyCartAdapter(services);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        }else{
            Log.e("Item","Empty");
        }

        //preferencesListingView = (RecyclerView) findViewById(R.id.preferences_listing);

        Boolean showPreferences = cartDb.showPreferences(androidId,countryCode);

        new GetPreferences().execute();

        creditCardField= (TextView)findViewById(R.id.credit_card);
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



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {

            String returnType = data.getExtras().getString("returnType");


            if (returnType.equals("PickupDate")) {
                pickUpDate = data.getExtras().getString("pickUpDate");
                String dateSelected = data.getExtras().getString("dateSelected");
                pickUpDateButton.setText(dateSelected);
            }else if(returnType.equals("PickupTime")){
                pickUpTime = data.getExtras().getString("hour");
                String pickUpTimeSelected = data.getExtras().getString("timeSelected");
                pickUpTimeButton.setText(pickUpTimeSelected);
            } else if(returnType.equals("deliveryDate")){
                deliveryDate = data.getExtras().getString("pickUpDate");
                String deliveryDateSelected = data.getExtras().getString("dateSelected");
                deliveryDateButton.setText(deliveryDateSelected);
            }else if(returnType.equals("deliveryTime")){
                deliveryTime = data.getExtras().getString("hour");
                String deliveryTimeSelected = data.getExtras().getString("timeSelected");
                deliveryTimeButton.setText(deliveryTimeSelected);
            }else if(returnType.equals("credit_card")){
                cardId = data.getExtras().getString("selectedCardId");
                creditCardField.setText(data.getExtras().getString("selectedCardTitle"));
            }
        }
    }

    private class GetPreferences extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server",null);
            String url = server+"/apiv5/get_preferences_records";
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    preferencesList = new ArrayList<>();
                    //String result = jsonObj.getString("preferences");
                    JSONObject preferences = jsonObj.getJSONObject("preferences");
                    JSONArray data = preferences.getJSONArray("data");



                    for(int i=0;i<data.length();i++){
                        JSONObject c = data.getJSONObject(i);
                        String id = c.getString("ID");
                        String title = c.getString("Title");
                        // JSONObject childs = c.getJSONObject("child_records");
                        // Log.e(TAG," c.childs ->"+id);
                        HashMap<String, String> card = new HashMap<>();
                        card.put("id", id);
                        card.put("title", title);
                        //card.put("childs", childs.toString());
                        preferencesList.add(card);
                    }
                }catch (final JSONException e) {
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
            }else {
                Log.e(TAG, "Couldn't get json from server.");
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

            prefrencesListView =  findViewById(R.id.preferences_listing);
            preferencesAdapter = new MyPreferencesAdapter(preferencesList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            prefrencesListView.setLayoutManager(mLayoutManager);
            prefrencesListView.setItemAnimator(new DefaultItemAnimator());
            prefrencesListView.setAdapter(preferencesAdapter);

        }
    }
}