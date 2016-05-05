package com.ajb.merchants.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.ProvinceInfo;

import java.util.ArrayList;
import java.util.List;

public class SortDistrictAdapter extends BaseAdapter {
    private List<ProvinceInfo> list = null;
    private Context mContext;

    public SortDistrictAdapter(Context mContext, List<ProvinceInfo> list) {
        this.mContext = mContext;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ProvinceInfo> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        ProvinceInfo mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvTitle.setText(this.list.get(position).getDistrictName());
        //选中
        if (position == selectItem) {
            viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.filter_sel_color));
        } else {
            viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_tv_color_02));
        }

        return view;
    }


    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
    }


    private int selectItem = -1;

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }
}