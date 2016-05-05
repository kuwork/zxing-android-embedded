package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.CarPark;

import java.util.List;

public class CarNumberPayListAdapter extends BaseAdapter {

    private Context context;
    private List<CarPark> list;
    private String selectedId;

    public CarNumberPayListAdapter(Context context, List<CarPark> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public List<CarPark> getList() {
        return list;
    }

    public void setList(List<CarPark> list) {
        this.list = list;
    }

    @Override
    public View getView(int postion, View v, ViewGroup arg2) {
        ViewHolder holder = null;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.activity_carnumberpay_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) v.findViewById(R.id.text_tv);
            holder.select = (ImageView) v.findViewById(R.id.select_ib);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        CarPark carPark = list.get(postion);
        holder.text.setText(carPark.getParkName());
        if (selectedId != null && selectedId.equals(carPark.getId())) {
            holder.select.setImageResource(R.drawable.right_click);
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
        }
        return v;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void append(List<CarPark> carParkName) {
        if (list != null) {
            list.addAll(carParkName);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder {
        public TextView text;
        public ImageView select;
    }

}
