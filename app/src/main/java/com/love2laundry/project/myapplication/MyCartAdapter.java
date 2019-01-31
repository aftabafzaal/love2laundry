package com.love2laundry.project.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> services = new ArrayList<>();

    public MyCartAdapter(ArrayList<HashMap<String, String>> items) {
        services = items;
    }

    public double servicesTotal = 0;

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Log.e("onCreateViewHolder view","view");
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cart_checkout, parent, false);

        return new MyViewHolder(viewHolder);
    }

    public double getServicesTotal() {
        return servicesTotal;
    }

    public JSONArray getServices() throws JSONException {
        int size = getItemCount();
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < size; i++) {

            JSONObject service = new JSONObject();
            service.put("ID", services.get(i).get("service_id"));
            service.put("Title", services.get(i).get("serviceName"));
            service.put("TitleUpper", services.get(i).get("serviceName"));
            service.put("DesktopImage", services.get(i).get("desktopImage"));
            service.put("DesktopImagePath", services.get(i).get("desktopImage"));

            service.put("MobileImage", services.get(i).get("image"));
            service.put("MobileImagePath", services.get(i).get("image"));

            service.put("Content", services.get(i).get("Content"));
            service.put("CategoryID", services.get(i).get("category_id"));
            service.put("CategoryName", services.get(i).get("categoryName"));
            service.put("IsPackage", services.get(i).get("isPackage"));

            service.put("PreferencesShow", services.get(i).get("preferencesShow"));

            service.put("Quantity", services.get(i).get("quantity"));
            service.put("Price", services.get(i).get("unitPrice"));
            service.put("Total", services.get(i).get("price"));
            service.put("OfferPrice", services.get(i).get("unitPrice"));

            Double price = Double.parseDouble(services.get(i).get("price"));
            servicesTotal += price;

            Double CurrencyAmount = Double.parseDouble(services.get(i).get("price")) + Double.parseDouble(services.get(i).get("discount"));
            Double amount = Math.floor(price);
            service.put("CurrencyAmount", amount.toString());
            jsonArray.put(service);

        }

        return jsonArray;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        Log.e(" ->--- ",services.get(i).get("serviceName"));


        for (int j=0;i<=services.size();j++) {
            myViewHolder.serviceName.setText(services.get(j).get("serviceName"));
            myViewHolder.quantity.setText(services.get(j).get("quantity"));
            myViewHolder.unitPrice.setText(services.get(j).get("unitPrice"));
        }


        //myViewHolder.price.setText(services.get(i).get("price"));
    }

    @Override
    public int getItemCount() {

        Log.e("services.size() -> ",""+services.size());

        return services.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceName, quantity, unitPrice, price;

        public MyViewHolder(View view) {
            super(view);
            serviceName = (TextView) view.findViewById(R.id.serviceName);
            quantity = (TextView) view.findViewById(R.id.quantity);
            unitPrice = (TextView) view.findViewById(R.id.unitPrice);
        }
    }
}