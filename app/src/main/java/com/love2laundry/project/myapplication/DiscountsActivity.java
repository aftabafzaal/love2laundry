package com.love2laundry.project.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscountsActivity extends Navigation {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    JSONArray loyaltyList = new JSONArray();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    ArrayList<HashMap<String, String>> preferencesList;
    HashMap<String, String> selectedPreference = new HashMap<>();
    public SharedPreferences sharedPreferencesCountry;

    private String TAG = DiscountsActivity.class.getSimpleName();

    String member_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpreferences = getSharedPreferences("member", MODE_PRIVATE);
        member_id = sharedpreferences.getString("member_id", null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Navigation navigation =new Navigation();
        navigation.initView(navigationView,member_id);


        new GetDiscountCodes().execute();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        JSONArray loyalties = new JSONArray();
        ListView lv;
        /**
         * The {@link ViewPager} that will host the section contents.
         */
        private ViewPager mViewPager;




        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        private static JSONArray l= new JSONArray();

        ArrayList<HashMap<String, String>> listingLoyalties;


        public static PlaceholderFragment newInstance(int sectionNumber,JSONArray loyaltyList)  {
            PlaceholderFragment fragment = new PlaceholderFragment();
            //loyalties=loyaltyList;
            l=loyaltyList;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber-1);
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            RecyclerView recyclerView;
            RecyclerView.Adapter loyaltiesAdapter;

            View rootView = inflater.inflate(R.layout.fragment_loyalty, container, false);
            ListView lv = (ListView) rootView.findViewById(R.id.list);

            int num=getArguments().getInt(ARG_SECTION_NUMBER);

            if(num==1){
                LinearLayout lp = (LinearLayout) rootView.findViewById(R.id.tab_header);
                lp.setVisibility(View.GONE);
            }
            try {
                JSONArray obj = (JSONArray) l.get(num);
                recyclerView = rootView.findViewById(R.id.list);
                loyaltiesAdapter = new DiscountsAdapter(obj,num);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(loyaltiesAdapter);
            } catch (JSONException e) {
                Log.e("onCreateView E ",e.getMessage());
                e.printStackTrace();
            }

            return rootView;

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        JSONArray loyalties = new JSONArray();

        public SectionsPagerAdapter(FragmentManager fm,JSONArray loyaltyList) {
            super(fm);
            loyalties=loyaltyList;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1,loyalties);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    private class GetDiscountCodes extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            sharedPreferencesCountry = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedPreferencesCountry.getString("server", null);
            String url = server + sharedpreferences.getString("apiDiscounts", "/apiv5/get_discounts")+"/"+member_id;
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {

                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    preferencesList = new ArrayList<>();
                    String result = jsonObj.getString("result");
                    JSONObject data = jsonObj.getJSONObject("data");
                    ///Log.e("data",data.toString());
                    JSONArray loyaltyActive = data.getJSONArray("discount_active");
                    JSONArray history = data.getJSONArray("discount_history");

                    Log.e(TAG,history.toString());
                    Log.e(TAG,loyaltyActive.toString());


                    loyaltyList.put(0,loyaltyActive);
                    loyaltyList.put(1,history);

                } catch (final JSONException e) {
                    // Log.e(TAG, "Json parsing error: " + e.getMessage());
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
                // Log.e(TAG, "Couldn't get json from server.");
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

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),loyaltyList);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

            super.onPostExecute(result);

        }
    }
}
