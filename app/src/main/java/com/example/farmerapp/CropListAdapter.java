package com.example.farmerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CropListAdapter extends ArrayAdapter<Crop> {

    private Context mContext;
    int mResource;

    public CropListAdapter(@NonNull Context context, int resource, ArrayList<Crop> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String commodity = getItem(position).getCommodity();
        String variety = getItem(position).getVariety();
        String arrival_date = getItem(position).getArrival_date();
        String min_price = getItem(position).getMin_price();
        String max_price = getItem(position).getMax_price();
        String modal_price = getItem(position).getModal_price();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView commodityText = convertView.findViewById(R.id.commodity);
        TextView varietyText = convertView.findViewById(R.id.variety);
        TextView arrival_dateText = convertView.findViewById(R.id.arrival_date);
        TextView min_priceText = convertView.findViewById(R.id.min_price);
        TextView max_priceText = convertView.findViewById(R.id.max_price);
        TextView modal_priceText = convertView.findViewById(R.id.modal_price);

        commodityText.setText(commodity);
        varietyText.setText("Variety: "+variety);
        arrival_dateText.setText("Arrival Date: "+arrival_date);
        min_priceText.setText("Min Price: "+min_price);
        max_priceText.setText("Max Price: "+max_price);
        modal_priceText.setText("Modal Price: "+modal_price);

        return convertView;
    }
}
