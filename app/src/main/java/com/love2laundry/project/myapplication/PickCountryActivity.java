package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class PickCountryActivity extends Config {

    private String TAG = PickCountryActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> contactList;
    private ListView lv;
    RadioButton radioUk;
    RadioButton radioUae;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("country", "uk");
        editor.commit();

        radioUk = findViewById(R.id.radioUk);
        radioUae = findViewById(R.id.radioUae);

        setContentView(R.layout.pick_country);
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("country");
        switch(view.getId()) {
            case R.id.radioUk:
                    ((RadioButton) findViewById(R.id.radioUk)).setChecked(true);
                    ((RadioButton) findViewById(R.id.radioUae)).setChecked(false);
                    editor.putString("country", "uk");

                    break;
            case R.id.radioUae:
                    ((RadioButton) findViewById(R.id.radioUae)).setChecked(true);
                    ((RadioButton) findViewById(R.id.radioUk)).setChecked(false);
                    editor.putString("country", "uae");
                    break;
        }
       editor.commit();
        Toast.makeText(getBaseContext(), currencyCode , Toast.LENGTH_SHORT).show();
    }

    public void send(View v) {
        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        String country = sharedpreferences.getString("country",null);
        super.setCountry(country);
        String currencyCode = sharedpreferences.getString("currencyCode",null);
        Toast.makeText(getBaseContext(), currencyCode , Toast.LENGTH_SHORT).show();


        switch(country) {

            case "uk":
                startActivity(new Intent(PickCountryActivity.this,
                        UKActivity.class));
                break;
            case "uae":
                startActivity(new Intent(PickCountryActivity.this,
                        UAEActivity.class));
                break;
        }



    }
}