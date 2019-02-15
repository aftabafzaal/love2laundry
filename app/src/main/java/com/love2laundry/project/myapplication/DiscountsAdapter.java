package com.love2laundry.project.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
            final JSONObject o = listingLoyalties.getJSONObject(i);
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
                myViewHolder.copy.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {

                        try {
                            Log.e("toString -> ",o.getString("Code"));
                            ClipboardManager clipboard = (ClipboardManager) p.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("copy", o.getString("Code"));
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(p,
                                    "Discount code copied.",
                                    Toast.LENGTH_LONG).show();
                            //Toast toast;
                           // toast = (Toast) Toast.makeText(this,
                            //        "Discount code copied.", Toast.LENGTH_SHORT).show();;
                            //toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            //toast.show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
        public Button copy;

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
                copy = (Button) view.findViewById(R.id.copy);
            }

        }
    }


}
