package com.love2laundry.project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DecimalFormat;

public class Navigation extends Config implements NavigationView.OnNavigationItemSelectedListener  {

    public void Navigation() {
        super.Config();
        Log.e("Config","Navigation");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        Log.e("Das","onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.e("onOptionsItemSelected","onOptionsItemSelected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.e("NavigationItemSelected",id+"-"+id+"-"+id+"");

        Intent intent = null;
        if (id == R.id.dashboard) {
            intent = new Intent(this, DashboardActivity.class);
        } else if (id == R.id.place_order) {

            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String country =sharedpreferences.getString("country",null);
            if(country.equals("uk")){
                intent = new Intent(this, UKActivity.class);
            }else{
                intent = new Intent(this, UAEActivity.class);
            }
        } else if (id == R.id.loyalties) {
            intent = new Intent(this, LoyaltyActivity.class);
        } else if (id == R.id.account) {
            intent = new Intent(this, AccountActivity.class);
        } else if (id == R.id.laundry_settings) {
            intent = new Intent(this, LaundrySettingsActivity.class);
        } else if (id == R.id.discount_codes) {
            intent = new Intent(this, DiscountsActivity.class);
        } else if (id == R.id.payment_cards) {
            intent = new Intent(this, PaymentCardsActivity.class);
        }
        startActivityForResult(intent, 10);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}