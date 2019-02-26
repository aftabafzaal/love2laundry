package com.love2laundry.project.source;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Navigation extends Config implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences loginMember;


    public void Navigation() {
        super.Config();
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

    public void initView(NavigationView navigationView,String member_id,SharedPreferences loginMember) {


        //
        View headerLayout = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();
        ImageView navbarCenterLogo = (ImageView) headerLayout.findViewById(R.id.CenterLogo);
        LinearLayout navHeaderTxt = (LinearLayout) headerLayout.findViewById(R.id.navHeaderTxt);
        ImageView navbarSideLogo = (ImageView) headerLayout.findViewById(R.id.SideLogo);

        if(member_id==null) {
            // if not login
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
            navigationView.getMenu().findItem(R.id.register).setVisible(true);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.dashboard).setVisible(false);
            navigationView.getMenu().findItem(R.id.place_order).setVisible(false);
            navigationView.getMenu().findItem(R.id.account).setVisible(false);
            navigationView.getMenu().findItem(R.id.laundry_settings).setVisible(false);
            navigationView.getMenu().findItem(R.id.loyalties).setVisible(false);
            navigationView.getMenu().findItem(R.id.payment_cards).setVisible(false);
            navigationView.getMenu().findItem(R.id.discount_codes).setVisible(false);
            navigationView.getMenu().findItem(R.id.referral).setVisible(false);


            navbarCenterLogo.setVisibility(View.VISIBLE);
            navbarSideLogo.setVisibility(View.GONE);
            navHeaderTxt.setVisibility(View.GONE);


        }else{
            /// if login


            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            navigationView.getMenu().findItem(R.id.register).setVisible(false);
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);

            navigationView.getMenu().findItem(R.id.dashboard).setVisible(true);
            navigationView.getMenu().findItem(R.id.place_order).setVisible(true);
            navigationView.getMenu().findItem(R.id.account).setVisible(true);
            navigationView.getMenu().findItem(R.id.laundry_settings).setVisible(true);
            navigationView.getMenu().findItem(R.id.loyalties).setVisible(true);
            navigationView.getMenu().findItem(R.id.payment_cards).setVisible(true);
            navigationView.getMenu().findItem(R.id.discount_codes).setVisible(true);
            navigationView.getMenu().findItem(R.id.referral).setVisible(true);

            navbarCenterLogo.setVisibility(View.GONE);
            navbarSideLogo.setVisibility(View.VISIBLE);
            navHeaderTxt.setVisibility(View.VISIBLE);
            //loginMember = getSharedPreferences("member", MODE_PRIVATE);
            String firstName = loginMember.getString("firstName", null);
            String lastName = loginMember.getString("lastName", null);
            String email = loginMember.getString("email", null);
            TextView txtName = (TextView) headerLayout.findViewById(R.id.nav_header_name);
            TextView txtEmail = (TextView) headerLayout.findViewById(R.id.nav_header_email);

            txtName.setText(firstName+" "+lastName);
            txtEmail.setText(email);

        }

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        ///getMenuInflater().inflate(R.menu.navigation, menu);
        ///Log.e("Das","onCreateOptionsMenu");
        return true;
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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


        Intent intent = null;
        Boolean activityForResult=true;

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
        }else if (id == R.id.logout) {
            intent = new Intent(this, LogoutActivity.class);
        }else if (id == R.id.login) {
            intent = new Intent(this, LoginActivity.class);
        }else if (id == R.id.register) {
            intent = new Intent(this, RegisterActivity.class);
        }else if (id == R.id.referral) {
            intent = new Intent(this, ReferralActivity.class);
        }else if (id == R.id.faqs) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("https://lov2laundry.freshdesk.com/support/solutions"));

        }else if (id == R.id.terms) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.love2laundry.com/t-c.html"));

        }else if (id == R.id.pick_country) {

            SharedPreferences.Editor editor = getSharedPreferences("country", MODE_PRIVATE).edit();

            editor.putString("country",null);
            editor.commit();

            intent = new Intent(this, PickCountryActivity.class);
        }else if (id == R.id.contact) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL,new String[] { "info@love2laundry.com" } );
            intent.putExtra(Intent.EXTRA_SUBJECT,"Contact Us Message for Love2Laundry");
            intent.setData(Uri.parse("mailto:"));
            startActivity(intent);
            //startActivity(Intent.createChooser(intent,"Choose an email client:"));
            activityForResult=false;

        }else if (id == R.id.rate_app) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("http://market.android.com/details?id=com.love2laundry.www"));

        }

        if(activityForResult==true) {
            startActivityForResult(intent, 10);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}