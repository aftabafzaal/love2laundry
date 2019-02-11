package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoutActivity extends Config {

    SharedPreferences member;
    Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.Config();
        SharedPreferences member;
        SharedPreferences.Editor editor = getSharedPreferences("member", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
        String country =sharedpreferences.getString("country",null);
        if(country.equals("uk")){
            intent = new Intent(this, UKActivity.class);
        }else{
            intent = new Intent(this, UAEActivity.class);
        }
        startActivityForResult(intent, 10);

    }
}
