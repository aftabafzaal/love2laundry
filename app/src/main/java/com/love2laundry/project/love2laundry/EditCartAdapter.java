package com.love2laundry.project.love2laundry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class EditCartAdapter extends RecyclerView.Adapter<EditCartAdapter.MyViewHolder> {



    public interface PersonModifier{
        public void onPersonSelected(JSONArray services);
    }

    private PersonModifier personModifier;
    JSONArray services = new JSONArray();
    Context p;
    private Cart cartDb;
    public double servicesTotal = 0.00;


    public EditCartAdapter(JSONArray items, Context c) {
        services = items;
        p=c;
    }

    public void setPersonModifier(PersonModifier personModifier){
        this.personModifier = personModifier;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Log.e("onCreateViewHolder view","view");
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cart_checkout, parent, false);
        return new MyViewHolder(viewHolder);

    }

    public JSONArray getServices() throws JSONException {
        int size = services.length();
        JSONArray jsonArray = new JSONArray();
        servicesTotal=0.00;

        for (int i = 0; i < size; i++) {



        }
        return jsonArray;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        Config config = new Config();
        try {

            final JSONObject json= new JSONObject(services.get(i).toString());
            Double unitPrice = Double.parseDouble(json.getString("price"));
            myViewHolder.serviceName.setText(json.getString("title"));
            int allowedQuantity=Integer.parseInt(json.getString("allowed"));
            myViewHolder.quantity.setText(json.getString("quantity"));

            myViewHolder.unitPrice.setText(config.getCurrencySymbol("uk")+config.displayPrice(unitPrice));
            if(json.getString("MobileImageName")!=null && !json.getString("MobileImageName").equals("") ){
                Picasso.with(p.getApplicationContext()).load(json.getString("MobileImageName")).resize(50,50).into(myViewHolder.mobileImagePath);
            }

            myViewHolder.removeCart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    Integer service_id = null;

                    String total = myViewHolder.quantity.getText().toString();
                    Integer quantity = Integer.parseInt(total);

                    JSONObject params = new JSONObject();

                    try {
                        params.put("service_id",json.getString("service_id"));
                        params.put("title",json.getString("title"));
                        params.put("MobileImageName",json.getString("MobileImageName"));
                        params.put("price",json.getString("price"));
                        params.put("categoryId",json.getString("categoryId"));
                        params.put("desktopImage",json.getString("desktopImage"));
                        params.put("isPackage",json.getString("isPackage"));
                        params.put("preferencesShow",json.getString("preferencesShow"));
                        params.put("total",json.getString("total"));
                        params.put("content",json.getString("content"));
                        params.put("titleUpper",json.getString("titleUpper"));
                        params.put("currencyAmount",json.getString("currencyAmount"));
                        params.put("desktopImagePath",json.getString("desktopImagePath"));
                        params.put("mobileImagePath",json.getString("mobileImagePath"));


                        Integer allowed = Integer.parseInt(json.getString("allowed"));;
                        if(allowed>0){
                            if(quantity>allowed) {
                                quantity--;
                                params.put("quantity", quantity);
                                params.put("allowed", allowed);
                                services.put(i, params);
                            }
                        }else{
                            quantity--;
                            params.put("allowed",0);
                            if(quantity==0){
                                Log.e("--->",quantity+"");
                                services.remove(i);
                                notifyDataSetChanged();
                            }else{

                                json.put("quantity",quantity);
                                services.put(i,params);
                            }
                        }
                        myViewHolder.quantity.setText(quantity.toString());
                        if(personModifier!=null){
                            personModifier.onPersonSelected(services);
                        }

                    } catch (JSONException e) {
                        Log.e("JSONException ---> ",e.getMessage());
                    }
                    //return true;
                }
            });



        } catch (JSONException e) {
           Log.e("aa",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return services.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceName, quantity, unitPrice, price, delete;
        public ImageView mobileImagePath;
        public Button removeCart;
        public LinearLayout linearLayout;
        public MyViewHolder(View view) {
            super(view);
            serviceName = (TextView) view.findViewById(R.id.serviceName);
            quantity = (TextView) view.findViewById(R.id.quantity);
            unitPrice = (TextView) view.findViewById(R.id.unitPrice);
            mobileImagePath = (ImageView) view.findViewById(R.id.mobileImagePath);
            removeCart = (Button) view.findViewById(R.id.remove_cart);
            linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        }
    }

}