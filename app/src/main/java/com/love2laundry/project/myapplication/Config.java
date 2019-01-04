package com.love2laundry.project.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.text.DecimalFormat;

public class Config extends AppCompatActivity {

    public SharedPreferences sharedpreferences;
    public String currencyCode;
    public String currencySymbol;
    public String currency;
    String androidId;
    private Context context;

    public void Config(){
        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    protected void setCountry(String country){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(country.equals("uk")){
            UK location = new UK();
            editor.putString("currencyCode", UK.CURRENCY_CODE);
            editor.putString("currencySymbol", UK.CURRENCY_SYMBOL);
            editor.putString("currency", UK.CURRENCY);
            editor.putString("server", UK.SERVER);
            editor.putString("loadData", UK.API_LOAD_DATA);
            editor.putString("apiLogin", UK.API_LOGIN);
            editor.putString("apiRegister", UK.API_REGISTER);
            editor.putString("apiPickUpDate", UK.API_PICK_UP_DATE);
            editor.putString("apiPickUpTime", UK.API_PICK_UP_TIME);
            editor.putString("apiDeliveryDate", UK.API_DELIVERY_DATE);
            editor.putString("apiDeliveryTime", UK.API_DELIVERY_TIME);
            editor.putString("apiCreditCards", UK.API_CREDIT_CARDS);


        }else if(country.equals("uae")){
            UAE location = new UAE();
            editor.putString("currencyCode", UAE.CURRENCY_CODE);
            editor.putString("currencySymbol", UAE.CURRENCY_SYMBOL);
            editor.putString("currency", UAE.CURRENCY);
            editor.putString("server", UAE.SERVER);
            editor.putString("loadData", UAE.API_LOAD_DATA);
            editor.putString("apiLogin", UK.API_LOGIN);
            editor.putString("apiRegister", UK.API_REGISTER);
            editor.putString("apiPickUpDate", UK.API_PICK_UP_DATE);
        }else if(country==null){
            Log.e("Pick Country","Error in country");
        }
        editor.commit();
        // return location;
    }

    static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("###.###");
        return Double.valueOf(twoDForm.format(d));
    }


}