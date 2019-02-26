package com.love2laundry.project.source;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class MyServicesAdapter extends RecyclerView.Adapter<MyServicesAdapter.MyViewHolder> {

    Context p;


    HashMap<String, String> selectedPreference = new HashMap<>();
    JSONArray services_record;
    int number;
    String category;
    String currency;
    Cart cartDb;
    Map<Integer, Integer> totalServices = new HashMap<Integer, Integer>();
    String deviceId,  country;

    public interface UpdateListing{
        public void getServicesTotal();
    }

    private UpdateListing updateServices;


    public void setUpdateServices(UpdateListing updateServices){
        this.updateServices = updateServices;
    }


    public MyServicesAdapter(Activity activity, int n, JSONArray servicesJson, String c, String currencyCode, String countryCode, String androidId) {

        cartDb = new Cart(activity);
        services_record = servicesJson;
        category = c;
        number = n;
        currency=currencyCode;
        deviceId=androidId;
        country=countryCode;
    }

    @NonNull
    @Override
    public MyServicesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        p = parent.getContext();
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tabs_data, parent, false);

        return new MyServicesAdapter.MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {


        try {
            final JSONObject json=new JSONObject(services_record.get(i).toString());

            myViewHolder.title.setText(json.getString("Title"));
            myViewHolder.content.setText(json.getString("Content"));

            if(!json.getString("MobileImagePath").equals("")) {
                Picasso.with(p).load(json.getString("MobileImagePath")).into(myViewHolder.mobileImagePath);
            }

            final String discount = json.getString("DiscountPercentage");
            String price = json.getString("Price");
            Double offerPrice;
            Integer serviceId = Integer.parseInt(json.getString("ID"));
            Config config = new Config();

            if (Double.parseDouble(discount) > 0) {
                offerPrice = Double.parseDouble(price) - (Double.parseDouble(price) / 100) * Double.parseDouble(discount);
                offerPrice = Math.floor(offerPrice);

                myViewHolder.discount.setText(discount + "%");
                myViewHolder.offerPrice.setText(currency+config.displayPrice(offerPrice));


            } else {
                offerPrice = Double.parseDouble(price);

                // myViewHolder.price.getText();
                myViewHolder.discount.setVisibility(View.GONE);
                myViewHolder.price.setVisibility(View.GONE);
                myViewHolder.offerPrice.setText(currency+config.displayPrice(offerPrice));
                //myViewHolder.offerPrice.setVisibility(View.GONE);
                //myViewHolder.offerPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                //myViewHolder.offerPrice.setTextColor(R.color.colorDarkGray);
            }

            final Double  discountAmount = Double.parseDouble(price) - offerPrice;


            HashMap<String, String> item = new HashMap<>();
            Log.e("aaa ",deviceId+" "+serviceId+" "+country);
            item = cartDb.getService(deviceId, serviceId, country);



            if (item.isEmpty() == true) {
                myViewHolder.quantity.setText("0");
            } else {
                myViewHolder.quantity.setText(item.get("quantity"));
            }

            myViewHolder.price.setText(currency+""+json.getString("Price"));
            myViewHolder.price.setPaintFlags(myViewHolder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            final Double finalOfferPrice = offerPrice;
            myViewHolder.add.setOnClickListener(new View.OnClickListener() {
                final Set<String> set = new HashSet<String>();
                @Override
                public void onClick(View arg0) {

                    Integer quantity;
                    String total = (String) myViewHolder.quantity.getText();
                    quantity = Integer.parseInt(total);
                    quantity++;
                    try {


                        Log.e("--",json.toString());
                        totalServices.put(Integer.parseInt(json.getString("ID")), quantity);
                        set.add(json.getString("Title"));
                        set.add(quantity.toString());

                        Integer service_id = Integer.parseInt(json.getString("ID"));
                        String serviceName = json.getString("Title");
                        String content = json.getString("Content");
                        Double unitPrice = finalOfferPrice;
                        Integer category_id = Integer.parseInt(json.getString("CategoryID"));
                        String categoryName = category;

                        String mobileImage = json.getString("MobileImagePath");
                        String desktopImage = json.getString("DesktopImagePath");
                        String isPackage = json.getString("IsPackage");
                        String preferencesShow = json.getString("PreferencesShow");
                        HashMap<String, String> item = new HashMap<>();

                        item = cartDb.getService(deviceId, service_id, country);

                        if (item.isEmpty() == true) {

                            cartDb.insertCart(deviceId, service_id, content, serviceName, unitPrice, unitPrice * quantity, quantity,
                                    category_id, categoryName, country, json.getString("MobileImagePath"), desktopImage, isPackage, preferencesShow, discountAmount * quantity);

                        } else {
                            cartDb.updateCart(deviceId, service_id, quantity, unitPrice, discountAmount, country);
                        }
                        if(updateServices!=null){
                            updateServices.getServicesTotal();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    myViewHolder.quantity.setText(quantity.toString());

                }
            });


            myViewHolder.minus.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Integer service_id = null;
                    try {
                        service_id = Integer.parseInt(json.getString("ID"));
                        Double unitPrice = finalOfferPrice;
                        String total = myViewHolder.quantity.getText().toString();
                        Integer quantity = Integer.parseInt(total);
                        if (quantity>0) {
                            HashMap<String, String> item = new HashMap<>();
                            item = cartDb.getService(deviceId, service_id, country);
                            quantity--;
                            Log.e("quantity", "==" + quantity);
                            if (quantity == 0) {
                                cartDb.deleteCartItem(deviceId, service_id, country);
                                myViewHolder.quantity.setText("0");
                            } else {
                                cartDb.updateCart(deviceId, service_id, quantity, unitPrice, discountAmount, country);
                                myViewHolder.quantity.setText(quantity.toString());
                            }

                            if (updateServices != null) {
                                updateServices.getServicesTotal();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return services_record.length();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,quantity,price,discount,offerPrice,content;
        public ImageView mobileImagePath;
        public ImageButton add,minus;


        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            quantity = (TextView) view.findViewById(R.id.quantity);
            price = (TextView) view.findViewById(R.id.price);
            discount = (TextView) view.findViewById(R.id.discount);
            offerPrice = (TextView) view.findViewById(R.id.offerPrice);
            content = (TextView) view.findViewById(R.id.content);
            mobileImagePath = (ImageView) view.findViewById(R.id.mobileImagePath);
            add = (ImageButton) view.findViewById(R.id.add);
            minus = (ImageButton) view.findViewById(R.id.minus);

        }
    }


}
