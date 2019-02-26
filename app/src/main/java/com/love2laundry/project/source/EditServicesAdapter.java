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

class EditServicesAdapter extends RecyclerView.Adapter<EditServicesAdapter.MyViewHolder> {

    Context p;

    HashMap<String, String> selectedPreference = new HashMap<>();
    JSONArray services_record;
    int number;
    String category;
    String currency;
    Cart cartDb;
    Map<Integer, Integer> totalServices = new HashMap<Integer, Integer>();
    String deviceId,  country;
    HashMap<String, String> selectedServices = new HashMap<>();

    public interface UpdateListing{
        public void getServicesTotal(HashMap<String, String> selectedServices);
    }

    private UpdateListing updateServices;


    public void setUpdateServices(UpdateListing updateServices){
        this.updateServices = updateServices;
    }


    public EditServicesAdapter(Activity activity, int n, JSONArray servicesJson, String c, String currencyCode, String countryCode, String androidId,HashMap<String,String> services) {

        services_record = servicesJson;
        category = c;
        number = n;
        currency=currencyCode;
        deviceId=androidId;
        country=countryCode;
        selectedServices=services;
    }

    @NonNull
    @Override
    public EditServicesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        p = parent.getContext();
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tabs_data, parent, false);

        return new EditServicesAdapter.MyViewHolder(viewHolder);
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

            }

            final Double  discountAmount = Double.parseDouble(price) - offerPrice;

            if (selectedServices.containsKey(json.getString("ID"))==true) {
                JSONObject item=new JSONObject(selectedServices.get(json.getString("ID")));
                myViewHolder.quantity.setText(item.getString("quantity"));

            } else {
                myViewHolder.quantity.setText("0");
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

                        Integer service_id = Integer.parseInt(json.getString("ID"));
                        String serviceName = json.getString("Title");
                        Double unitPrice = finalOfferPrice;
                        Integer category_id = Integer.parseInt(json.getString("CategoryID"));
                        String categoryName = category;
                        String mobileImage = json.getString("MobileImagePath");

                        JSONObject params = new JSONObject();
                        params.put("service_id",json.getString("ID"));
                        params.put("title",serviceName);
                        params.put("MobileImageName",mobileImage);
                        params.put("price",unitPrice);

                        params.put("categoryId",json.getString("CategoryID"));
                        params.put("desktopImage",json.getString("DesktopImage"));
                        params.put("isPackage",json.getString("IsPackage"));
                        params.put("preferencesShow",json.getString("PreferencesShow"));
                        params.put("total",unitPrice*quantity);
                        params.put("content",json.getString("Content"));
                        params.put("titleUpper",json.getString("TitleUpper"));
                        params.put("currencyAmount",json.getString("CurrencyAmount"));
                        params.put("desktopImagePath",json.getString("DesktopImagePath"));
                        params.put("mobileImagePath",json.getString("MobileImagePath"));


                        if (selectedServices.containsKey(json.getString("ID"))==true) {
                            JSONObject j = new JSONObject(selectedServices.get(json.getString("ID")));
                            Log.e("update",j.getString("allowed"));
                            params.put("allowed",j.getString("allowed"));
                            params.put("quantity",quantity);
                        } else {
                            Log.e("add","add");
                            params.put("allowed",0);
                            params.put("quantity",1);
                        }

                        selectedServices.put(json.getString("ID"),params.toString());
                        if(updateServices!=null){
                           updateServices.getServicesTotal(selectedServices);
                        }
                    } catch (JSONException e) {
                        Log.e("s",e.getMessage());
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
                        String serviceName = json.getString("Title");
                        Integer category_id = Integer.parseInt(json.getString("CategoryID"));
                        String categoryName = category;
                        String mobileImage = json.getString("MobileImagePath");


                        if (quantity>0) {

                            JSONObject params = new JSONObject();
                            params.put("service_id",json.getString("ID"));
                            params.put("title",serviceName);
                            params.put("MobileImageName",mobileImage);
                            params.put("price",unitPrice);



                            if (selectedServices.containsKey(json.getString("ID"))==true) {

                                JSONObject j = new JSONObject(selectedServices.get(json.getString("ID")));
                                Log.e("j",j.toString());
                                int allowed= Integer.parseInt(j.getString("allowed"));

                                if(quantity>allowed){
                                    quantity--;
                                    params.put("quantity",quantity);
                                    params.put("allowed",allowed);
                                    selectedServices.put(json.getString("ID"),params.toString());
                                }

                            } else {
                                quantity--;
                                if (quantity == 0) {
                                    selectedServices.remove(json.getString("ID"));
                                }else{
                                    params.put("quantity",quantity);
                                    params.put("allowed",0);
                                    selectedServices.put(json.getString("ID"),params.toString());
                                }
                            }

                            myViewHolder.quantity.setText(quantity.toString());

                            if (updateServices != null) {
                                updateServices.getServicesTotal(selectedServices);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("EditServicesAdapter",e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(" EditServicesAdapter",e.getMessage());
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
