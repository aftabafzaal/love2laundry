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

class MyPreferencesAdapter extends RecyclerView.Adapter<MyPreferencesAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>>  preferencesList = new ArrayList<>();
    public MyPreferencesAdapter(ArrayList<HashMap<String, String>> preferences) {
        preferencesList=preferences;
    }

    @NonNull
    @Override
    public MyPreferencesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.e("onCreateViewHolder view","view");
        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_preferences, parent, false);
        return new MyPreferencesAdapter.MyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPreferencesAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.title.setText(preferencesList.get(i).get("title"));
    }

    @Override
    public int getItemCount() {
        return preferencesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

}
