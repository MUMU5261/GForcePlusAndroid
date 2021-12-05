package com.oymotion.gforceprofiledemo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 02/12/2021
 * Time: 00:55
 * Description:
 */
public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.ViewHolder> {
    private static final String TAG = "PropertyListAdapter";

    private List<Property> properList;

    public PropertyListAdapter(ArrayList<Property> data) {
        this.properList = data;
    }

    public void updateData(ArrayList<Property> data) {
        this.properList.clear();
        this.properList = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_property_list_item, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: "+PropertyListAdapter.this.properList.get(position).getProperty());
        // it is failed when setText with int, in this case with p_id rather than String.valueOf(..)
        String property_name = String.valueOf(PropertyListAdapter.this.properList.get(position).getProperty());
        String polar_low = String.valueOf(PropertyListAdapter.this.properList.get(position).getPolarLow());
        String polar_high = String.valueOf(PropertyListAdapter.this.properList.get(position).getPolarHigh());
        holder.tv_property.setText(property_name);
        holder.tv_polar_low.setText(polar_low);
        holder.tv_polar_high.setText(polar_high);
    }


    @Override
    public int getItemCount() {
        return PropertyListAdapter.this.properList == null ? 0 : PropertyListAdapter.this.properList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_property;
        TextView tv_polar_low;
        TextView tv_polar_high;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_property = (TextView) itemView.findViewById(R.id.tv_item_property);
            tv_polar_low = (TextView) itemView.findViewById(R.id.tv_item_polar_low);
            tv_polar_high = (TextView) itemView.findViewById(R.id.tv_item_polar_high);
        }
    }

}
