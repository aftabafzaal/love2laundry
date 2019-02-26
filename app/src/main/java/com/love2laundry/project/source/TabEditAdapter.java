package com.love2laundry.project.source;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabEditAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    JSONArray servicesRecords = new JSONArray();
    String country,currencySymbol,androidId;
    ServicesEditFragment.UpdateCart updateCart;

    TabEditAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment, JSONArray servicesRecords, int number, String countryCode, String currencySymbol, String category, String icon, String androidId, HashMap<String, String> services ) throws JSONException {


        Bundle args = new Bundle();
        args.putInt("number", number);
        args.putString("services",servicesRecords.toString());
        args.putString("country",countryCode);
        args.putString("currencySymbol",currencySymbol);
        args.putString("androidId",androidId);
        args.putString("category",category);
        args.putString("icon",icon);
        args.putSerializable("selectedServices", services);

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