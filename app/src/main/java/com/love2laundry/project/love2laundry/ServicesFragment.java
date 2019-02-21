package com.love2laundry.project.love2laundry;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

public class ServicesFragment extends Fragment implements MyServicesAdapter.UpdateListing  {

    public static JSONArray service_records;
    public int number;
    public  Activity activity;
    private RecyclerView listing;
    private RecyclerView.Adapter myServicesAdapter;
    public String currencySymbol,country,category,androidId;



    public interface UpdateCart{
        public void updateCart();
    }

    private UpdateCart updateCart;


    public void setUpdateCart(UpdateCart updateCart){
        this.updateCart = updateCart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_services, container, false);

        listing = rootView.findViewById(R.id.listing);
        Bundle bundle=getArguments();
        currencySymbol=bundle.getString("currencySymbol");
        country=bundle.getString("country");
        androidId=bundle.getString("androidId");
        category=bundle.getString("category");
        try {
            JSONArray service_records= new JSONArray(bundle.getString("services"));
            Log.e("service_records ",country+" "+androidId);
            myServicesAdapter = new MyServicesAdapter(getActivity(),number,service_records,category,currencySymbol,country,androidId);
            ((MyServicesAdapter) myServicesAdapter).setUpdateServices(this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            listing.setLayoutManager(mLayoutManager);
            listing.setItemAnimator(new DefaultItemAnimator());
            listing.setAdapter(myServicesAdapter);

        } catch (JSONException e) {
            Log.e(" --> ",e.getMessage());
            e.printStackTrace();
        }


        return rootView;
    }


    @Override
    public void getServicesTotal(){

        ((ListingActivity) getActivity()).updateCart();

    }
}
