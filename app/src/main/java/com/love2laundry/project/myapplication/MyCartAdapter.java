package com.love2laundry.project.myapplication;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {



    public interface PersonModifier{
        public void onPersonSelected();
    }

    private PersonModifier personModifier;
    ArrayList<HashMap<String, String>> services = new ArrayList<>();
    Context p;
    private Cart cartDb;
    public double servicesTotal = 0.00;
    private String  country;
    private String deviceId;


    public MyCartAdapter(ArrayList<HashMap<String, String>> items, Context c,String location,String phoneId ) {
        services = items;
        p=c;
        country=location;
        deviceId=phoneId;

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
        int size = services.size();
        JSONArray jsonArray = new JSONArray();
        servicesTotal=0.00;

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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.serviceName.setText(services.get(i).get("serviceName"));
        myViewHolder.quantity.setText(services.get(i).get("quantity"));
        Double unitPrice = Double.parseDouble(services.get(i).get("unitPrice"));
        Config config = new Config();
        myViewHolder.unitPrice.setText(config.getCurrencySymbol(country)+config.displayPrice(unitPrice));

        if(services.get(i).get("image")!=null && !services.get(i).get("image").equals("") ){
            Picasso.with(p.getApplicationContext()).load(services.get(i).get("image")).resize(50,50).into(myViewHolder.mobileImagePath);
        }

        myViewHolder.removeCart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Config config=new Config();
                cartDb = new Cart(p);
                Integer service_id = null;
                try {
                    service_id = Integer.parseInt(services.get(i).get("service_id"));
                    String total = myViewHolder.quantity.getText().toString();
                    Integer quantity = Integer.parseInt(total);
                    HashMap<String, String> item = new HashMap<>();
                    item = cartDb.getService(deviceId, service_id, country);

                    quantity--;
                    Double unitPrice = Double.parseDouble(item.get("unitPrice"));
                    Double discount = Double.parseDouble(item.get("discount"));

                    if (quantity == 0) {

                        cartDb.deleteCartItem(deviceId, service_id, country);
                        services.remove(i);
                        //notifyDataSetChanged();
                    } else {
                        services.get(i).put("quantity",quantity.toString());
                        cartDb.updateCart(deviceId, service_id, quantity, unitPrice, discount, country);
                        myViewHolder.quantity.setText(quantity.toString());
                    }
                    notifyDataSetChanged();

                    JSONArray services = getServices();

                    if(personModifier!=null){
                        personModifier.onPersonSelected();
                    }



                } catch (Exception e) {
                    Log.e("My Cart ",e.getMessage());
                }
                //return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
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