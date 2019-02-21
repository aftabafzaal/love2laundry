package com.love2laundry.project.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    JSONArray servicesRecords = new JSONArray();
    String country,currencySymbol,androidId;
    ServicesFragment.UpdateCart updateCart;

    TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment, JSONArray servicesRecords, int number, String countryCode, String currencySymbol, String category, String icon, String androidId ) throws JSONException {


        Bundle args = new Bundle();
        args.putInt("number", number);
        args.putString("services",servicesRecords.toString());
        args.putString("country",countryCode);
        args.putString("currencySymbol",currencySymbol);
        args.putString("androidId",androidId);
        args.putString("category",category);
        args.putString("icon",icon);

        fragment.setArguments(args);


        mFragmentList.add(fragment);
        mFragmentTitleList.add(category);
        //servicesRecords.put(number,servicesRecords);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}