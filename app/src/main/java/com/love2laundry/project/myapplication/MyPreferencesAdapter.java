package com.love2laundry.project.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MyPreferencesAdapter extends RecyclerView.Adapter<MyPreferencesAdapter.MyViewHolder> {

    Context p;
    ArrayList<HashMap<String, String>> preferencesList = new ArrayList<>();
    HashMap<String, String> selectedPreference = new HashMap<>();
    Double total = 0.0;

    public MyPreferencesAdapter(ArrayList<HashMap<String, String>> preferences, HashMap<String, String> selected) {
        preferencesList = preferences;
        selectedPreference = selected;
        Log.e("Preferences selected",""+selectedPreference);
        // Log.e("Preferences preferences",""+preferences);

    }

    public Double getTotal() {
        return total;
    }


    public String getMemberSelected() throws JSONException {

        int size = selectedPreference.size();
        Iterator it = selectedPreference.entrySet().iterator();
        String preferences_data = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
           // Log.e(" pair.getKey() -> ",pair.getKey() + " = " + pair.getValue());
            String string = (String) pair.getValue();

            JSONObject json = new JSONObject(string);
            preferences_data += json.getString("ID") + "||";

            it.remove(); // avoids a ConcurrentModificationException
        }

        return preferences_data;
    }

    public String getSelected() throws JSONException {

        int size = selectedPreference.size();
        Iterator it = selectedPreference.entrySet().iterator();
        String preferences_data = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            //Log.e(" pair.getKey() -> ",pair.getKey() + " = " + pair.getValue());
            String string = (String) pair.getValue();

            JSONObject json = new JSONObject(string);
            String priceForPackage = json.getString("PriceForPackage");
            Double price = 0.0;
            if (priceForPackage.equals("Yes")) {
                price = Double.parseDouble(json.getString("Price"));
                total += price;
            }

            preferences_data += json.getString("ID") + "," + json.getString("categoryTitle")
                    + "," + json.getString("Title") + "," + price + "," + price + "|";

            it.remove(); // avoids a ConcurrentModificationException
        }
        // String rtrim = preferences_data.replaceAll("\\s+|","");
        //Log.e("preferences_data -> ","sss -> "+preferences_data.substring(preferences_data.lastIndexOf('|')));

        return preferences_data;
    }

    @NonNull
    @Override
    public MyPreferencesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Log.e("cc preference",""+preferencesList.toString());
        p = parent.getContext();
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_preferences, parent, false);

        return new MyPreferencesAdapter.MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPreferencesAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.title.setText(preferencesList.get(i).get("title"));

        String list = preferencesList.get(i).get("children");


        String id = preferencesList.get(i).get("id");
        String title = preferencesList.get(i).get("title");

        String selectedTitle = "";


        try {
            JSONArray jsonArray = new JSONArray(list);
            ArrayList<String> stringArray = new ArrayList<String>();
            final JSONArray stringArrayValues = new JSONArray();
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject json = jsonArray.getJSONObject(j);
                json.put("categoryTitle", title);
                stringArray.add(json.getString("Title"));
                String pid=json.getString("ID");
                stringArrayValues.put(j, json);

                int l=Integer.parseInt(pid);
                int r=Integer.parseInt(selectedPreference.get(id).toString());

                if(l==r){
                    selectedTitle=json.getString("Title");
                }
            }
            ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(p, android.R.layout.simple_list_item_1, stringArray);
            myViewHolder.spinner.setAdapter(spinnerMenu);
            myViewHolder.spinner.setId(Integer.parseInt(id));
            myViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);

                    if (item != null) {
                        String catId = "" + adapterView.getId();
                        try {
                            selectedPreference.put(catId.toString(), stringArrayValues.getString(position));
                        //    Log.e("selectedPreference ",""+stringArrayValues.getString(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // TODO Auto-generated method stub
                }
            });
            int spinnerPosition = 0;
            String myString = selectedTitle;

            spinnerPosition = spinnerMenu.getPosition(myString);
            myViewHolder.spinner.setSelection(spinnerPosition);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return preferencesList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Spinner spinner;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            spinner = (Spinner) view.findViewById(R.id.spinner);

        }
    }


}
