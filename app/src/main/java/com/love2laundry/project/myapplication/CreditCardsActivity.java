package com.love2laundry.project.myapplication;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CreditCardsActivity extends Config {

    public static interface ResultCallbackIF {

        public void resultOk(String resultString, Bundle resultMap);
        public void resultCancel(String resultString, Bundle resultMap);

    }

    private String TAG = CreditCardsActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> cardList;
    private ListView lv;
    String action,title,type,selectedCardId,selectedCardName,selectedCardTitle,selectedCardMaskedNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        action = getIntent().getStringExtra("action");
        cardList = new ArrayList<>();

        new GetDates().execute();
    }

    private class GetDates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server",null);
            String url = server+action;
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG,url);
            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray cards;
                    cards = jsonObj.getJSONArray("cards");


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
                        cardList.add(card);

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

            lv=(ListView)findViewById(R.id.list);
            Log.e("cardList ",""+cardList);
            ListAdapter adapter = new SimpleAdapter(CreditCardsActivity.this, cardList,
                    R.layout.list_card, new String[]{ "id","title","name","MaskedNumber"},
                    new int[]{R.id.title,R.id.name,R.id.maskedNumber});

            int s = cardList.size();
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    selectedCardId = cardList.get(i).get("id");
                    selectedCardName = cardList.get(i).get("name");
                    selectedCardTitle = cardList.get(i).get("title");
                    selectedCardMaskedNumber = cardList.get(i).get("MaskedNumber");
                    finish();
                }
            });

        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("selectedCardId", selectedCardId);
        data.putExtra("selectedCardName", selectedCardName);
        data.putExtra("selectedCardTitle", selectedCardTitle);
        data.putExtra("selectedCardMaskedNumber", selectedCardMaskedNumber);
        data.putExtra("returnType", type);
        setResult(RESULT_OK, data);
        super.finish();
    }
}