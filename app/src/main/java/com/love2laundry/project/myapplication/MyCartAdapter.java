package com.love2laundry.project.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>>  services = new ArrayList<>();
    public MyCartAdapter(ArrayList<HashMap<String, String>> items) {
        services=items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.e("onCreateViewHolder view","view");
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cart_checkout, parent, false);
        Log.e("onCreateViewHolder view","view");
        return new MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.serviceName.setText(services.get(i).get("serviceName"));
        myViewHolder.quantity.setText(services.get(i).get("quantity"));
        myViewHolder.unitPrice.setText(services.get(i).get("unitPrice"));
        //myViewHolder.price.setText(services.get(i).get("price"));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceName,quantity,unitPrice,price;

        public MyViewHolder(View view) {
            super(view);
            serviceName = (TextView) view.findViewById(R.id.serviceName);
            quantity = (TextView) view.findViewById(R.id.quantity);
            unitPrice = (TextView) view.findViewById(R.id.unitPrice);
           // price = (TextView) view.findViewById(R.id.price);
        }
    }
}