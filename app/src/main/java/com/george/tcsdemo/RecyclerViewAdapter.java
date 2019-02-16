package com.george.tcsdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.tcsdemo.data.Lista;
import com.george.tcsdemo.utils.TcsUtils;

import java.util.ArrayList;
import java.util.Locale;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NavigationAdapterViewHolder> {

    private Context mContext;
    private ArrayList<Lista> hitsList;

    public RecyclerViewAdapter(Context context, ArrayList<Lista> list) {
        mContext = context;
        hitsList = list;

    }

    @NonNull
    @Override
    public NavigationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new NavigationAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationAdapterViewHolder holder, int position) {

        Lista hit = hitsList.get(position);

        //Set date
        holder.dateView.setText(hit.getDt_txt());

        //Set image
        int weatherId = Integer.parseInt(hit.getWeather()[0].getId());
        holder.iconView.setImageResource(TcsUtils.getSmallArtResourceIdForWeatherCondition(weatherId));

        //Set weather description
        String description = TcsUtils.getStringForWeatherCondition(mContext, weatherId);
        holder.descriptionView.setText(description);

        //Set Max temp
        Locale currentLocale = mContext.getResources().getConfiguration().locale;
        holder.highTempView.setText(String.format(currentLocale, "%.1f", Double.parseDouble(hit.getMain().getTemp_max())) + "\u00B0");

        //Set Min temp
        holder.lowTempView.setText(String.format(currentLocale, "%.1f", Double.parseDouble(hit.getMain().getTemp_min())) + "\u00B0");


    }

    @Override
    public int getItemCount() {
        if (hitsList != null && hitsList.size() > 0) {
            return hitsList.size();
        } else {
            return 0;
        }
    }

    class NavigationAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        NavigationAdapterViewHolder(View view) {
            super(view);

            iconView = view.findViewById(R.id.weather_icon);
            dateView = view.findViewById(R.id.date);
            descriptionView = view.findViewById(R.id.weather_description);
            highTempView = view.findViewById(R.id.high_temperature);
            lowTempView = view.findViewById(R.id.low_temperature);

        }
    }

    public void setHitsData(ArrayList<Lista> list) {
        hitsList = list;
        /*notifyDataSetChanged();*/
    }


}

