package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

public class DashboardActivity extends Navigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);

        String member_id = sharedpreferences.getString("member_id", null);
        String firstName = sharedpreferences.getString("firstName", null);
        String lastName = sharedpreferences.getString("lastName", null);
        Log.e("Das", member_id + "-" + firstName + "-" + lastName + "");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Navigation navigation =new Navigation();
        navigation.initView(navigationView,member_id);

    }

    public void getInvoices(View v) {

        Log.e("View"," -> "+v.getId());
        String type="pending_invoices";
        switch (v.getId()) {
            case R.id.pending_invoices:
                type="Pending";

                break;
            case R.id.processed_invoices:
                type="Processed";
                break;

            case R.id.canceled_invoices:
                type="Canceled";
                break;

            case R.id.completed_invoices:
                type="Completed";
                break;
            default:
                type="Pending";
                break;
        }
        Intent intent = new Intent(DashboardActivity.this, InvoicesActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, 10);
    }
}