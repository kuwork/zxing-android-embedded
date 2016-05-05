package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BillInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class MyBillListAdapter extends BaseAdapter {
    private Context context;
    private List<BillInfo> mData;
    private LayoutInflater mInflater;

    public MyBillListAdapter(Context context, List<BillInfo> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
        if (list != null) {
            this.mData = list;
        } else {
            this.mData = new ArrayList<BillInfo>();
        }
    }

    // 更新列表状态
    public void changeStatue(List<BillInfo> list) {
        if (list != null) {
            this.mData = list;
        } else {
            this.mData = new ArrayList<BillInfo>();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.my_bill_item, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.init(position, mData.get(position));

        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.line_number)
        TextView lineNumber;
        @ViewInject(R.id.my_bill_item_createTime)
        TextView createTime;
        @ViewInject(R.id.my_bill_item_title)
        TextView title;
        @ViewInject(R.id.my_bill_item_tradeState)
        TextView tradeState;
        @ViewInject(R.id.my_bill_item_totalFee)
        TextView totalFee;

        public void init(int position, BillInfo billInfo) {
            this.lineNumber.setText((position + 1) + "");
            this.createTime.setText(billInfo.getCreateTime());
            this.title.setText(billInfo.getTitle());
            this.tradeState.setText(billInfo.getTradeState());
            this.totalFee.setText(billInfo.getTotalFee());
        }

    }
}
