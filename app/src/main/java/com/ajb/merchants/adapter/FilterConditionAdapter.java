package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.filter.Condition;
import com.ajb.merchants.model.filter.ConditionValue;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by jerry on 16/4/22.
 */
public class FilterConditionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private Condition condition;

    public FilterConditionAdapter(Context context, Condition condition) {
        this.condition = condition;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (condition != null && condition.getConditionList() != null) {
            return condition.getConditionList().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (condition != null && condition.getConditionList() != null) {
            return condition.getConditionList().get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.filter_value_item, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.init(context, condition.getConditionList().get(position), position);
        return convertView;
    }

    public void check(ConditionValue cv) {
        int size = getCount();
        ConditionValue conditionValue;
        for (int i = 0; i < size; i++) {
            conditionValue = (ConditionValue) getItem(i);
            if (cv.equals(conditionValue)) {
                conditionValue.setCheck(true);
            } else {
                conditionValue.setCheck(false);
            }
        }
        notifyDataSetChanged();
    }


    class ViewHolder {
        @ViewInject(R.id.title)
        TextView title;
        @ViewInject(R.id.desc)
        TextView value;
        @ViewInject(R.id.select_ib)
        ImageView img;
        @ViewInject(R.id.item_bg)
        RelativeLayout bg;


        public ViewHolder() {
            super();
        }

        public void init(Context context, ConditionValue info, int position) {
            if (title != null) {
                title.setText(info.getDataName());
            }
            if (img != null) {
                if (info.isCheck()) {
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.GONE);
                }
            }
        }
    }
}
