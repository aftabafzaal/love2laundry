package com.love2laundry.project.myapplication;

import android.content.Context;
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

class DiscountsAdapter extends RecyclerView.Adapter<DiscountsAdapter.MyViewHolder> {

    Context p;
    JSONArray listingLoyalties= new JSONArray();
    int total = 0;
    int num = 0;
    public DiscountsAdapter(JSONArray l, int n) {
        listingLoyalties = l;
        total =  l.length();
        num=n;
        Log.e("listingLoyalties -> "+ num,listingLoyalties.toString());
    }

    @NonNull
    @Override
    public DiscountsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Log.e("cc preference",""+preferencesList.toString());
        p = parent.getContext();
        int layout=R.layout.list_discounts_active;

        if(num==1){
            layout=R.layout.list_discounts_history;
        }


        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new DiscountsAdapter.MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountsAdapter.MyViewHolder myViewHolder, int i) {

        //JSONObject contact = listingLoyalties.get(i);
        try {
            JSONObject o = listingLoyalties.getJSONObject(i);
            if(num==1) {
                Log.e("toString -> "+num ,o.toString());
                myViewHolder.discount_worth.setText(o.getString("DiscountWorthValue"));
                myViewHolder.invoiceNumber.setText("#"+o.getString("InvoiceNumber"));
                myViewHolder.date.setText(o.getString("CreatedDateTimeFormatted"));
                myViewHolder.price.setText(o.getString("CurrencyAmount"));
                myViewHolder.code.setText(o.getString("DiscountCode"));
            }else{

                myViewHolder.code.setText(o.getString("Code"));
                myViewHolder.value.setText(o.getString("WorthValue"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return total;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id,code,value;
        public TextView discount_worth,invoiceNumber,date,price;

        public MyViewHolder(View view) {
            super(view);

            code = (TextView) view.findViewById(R.id.code);
            if(num==1) {
                discount_worth = (TextView) view.findViewById(R.id.discount_worth);
                invoiceNumber = (TextView) view.findViewById(R.id.invoice_no);
                date = (TextView) view.findViewById(R.id.date);
                price = (TextView) view.findViewById(R.id.price);
            }else{
                value = (TextView) view.findViewById(R.id.value);
            }

        }
    }


}
