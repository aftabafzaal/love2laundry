package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class PaymentCardsActivity extends Navigation {

    EditText firstName,lastName,email,phone,referral,building,street,town,postCode,password,confirmPassword;
    private String TAG = PaymentCardsActivity.class.getSimpleName();
    public SharedPreferences sharedPreferencesCountry;
    String member_id;
    ArrayList<HashMap<String, String>> cardList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        member_id = sharedpreferences.getString("member_id", null);
        String memberJson = sharedpreferences.getString("member_data", null);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Navigation navigation =new Navigation();
        navigation.initView(navigationView, member_id,sharedpreferences);

        cardList = new ArrayList<>();


        Button addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PaymentCardsActivity.this, AddPaymentCardActivity.class);

                startActivityForResult(intent, 10);
                finish();


            }
        });




        new GetCards().execute();
    }


    private class GetCards extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedPreferencesCountry.getString("server", null);
            String url = server + sharedPreferencesCountry.getString("apiCreditCards", null) + "/" + member_id;
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, url);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray cards;
                    cards = jsonObj.getJSONArray("cards");
                    Log.e(TAG," -> "+cards);

                    for (int i = 0; i < cards.length(); i++) {
                        JSONObject c = cards.getJSONObject(i);
                        String id = c.getString("ID");
                        String title = c.getString("Title");
                        String name = c.getString("Name");
                        String expireYear = c.getString("ExpireYear");
                        String expireMonth = c.getString("ExpireMonth");
                        String maskedNumber = c.getString("MaskedNumber");
                        String maskedCode = c.getString("MaskedCode");

                        HashMap<String, String> card = new HashMap<>();
                        card.put("id", id);
                        card.put("title", title);
                        card.put("name", name);
                        card.put("expireYear", expireYear);
                        card.put("expireMonth", expireMonth);
                        card.put("maskedNumber", maskedNumber);
                        card.put("maskedCode", maskedCode);
                        Log.e(TAG," -->-- "+card);
                        cardList.add(card);


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


            lv = (ListView) findViewById(R.id.list);

            ListAdapter adapter = new SimpleAdapter(PaymentCardsActivity.this, cardList,
                    R.layout.list_card, new String[]{"name"},
                    new int[]{R.id.name});

            int s = cardList.size();
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    Log.e("cardList ", "" + cardList.get(i));


                    Intent intent = new Intent(PaymentCardsActivity.this, EditPaymentCardActivity.class);

                    intent.putExtra("id", cardList.get(i).get("id"));
                    intent.putExtra("name", cardList.get(i).get("name"));
                    intent.putExtra("title", cardList.get(i).get("title"));
                    intent.putExtra("maskedNumber", cardList.get(i).get("maskedNumber"));
                    intent.putExtra("expireMonth", cardList.get(i).get("expireMonth"));
                    intent.putExtra("maskedCode", cardList.get(i).get("maskedCode"));
                    intent.putExtra("expireYear", cardList.get(i).get("expireYear"));
                    startActivityForResult(intent, 10);
                    finish();



                    //selectedCardId = cardList.get(i).get("id");
                    //selectedCardName = cardList.get(i).get("name");
                    //selectedCardTitle = cardList.get(i).get("title");
                    //selectedCardMaskedNumber = cardList.get(i).get("MaskedNumber");
                    //finish();
                }
            });


        }
    }
}