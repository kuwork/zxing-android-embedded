package com.ajb.merchants.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.SortModel;
import com.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class SortAdapter extends BaseAdapter {
    private Drawable locDrawable;
    private List<SortModel> list = null;
    private Context mContext;
    private String currentCity;

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
        SortModel sortModel = new SortModel("定位", currentCity);
        if (list.size() > 0 &&
                "定位".equals(list.get(0).getSortLetters())) {
            list.set(0, sortModel);
        } else {
            list.add(0, sortModel);
        }
    }

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        locDrawable = mContext.getResources().getDrawable(R.mipmap.address);
        locDrawable.setBounds(0, 0, 40, 40);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SortModel> list) {
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
        SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        viewHolder.tvTitle.setText(this.list.get(position).getName());
        //选中
        if (position == selectItem) {
            viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.filter_sel_color));
        } else {
            viewHolder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.item_tv_color_02));
        }
        //当前城市
        if (position == 0 && list.get(position).getName().equals(currentCity)) {
            viewHolder.tvTitle.setCompoundDrawables(null, null, locDrawable, null);
            viewHolder.tvTitle.setPadding(DensityUtil.dp2px(mContext, 10f), 0,
                    DensityUtil.dp2px(mContext, 10), 0);
        } else {
            viewHolder.tvTitle.setCompoundDrawables(null, null, null, null);
            viewHolder.tvTitle.setPadding(DensityUtil.dp2px(mContext, 10f), 0,
                    DensityUtil.dp2px(mContext, 10), 0);
        }

        return view;

    }


    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    private int selectItem = -1;

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }
}