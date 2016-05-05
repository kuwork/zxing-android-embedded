package com.ajb.merchants.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BillInfo;
import com.ajb.merchants.util.DateConvertor;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jerry
 * @ClassName BillInfoListAdtpter
 * @Description 优惠券列表
 * @date 2015年9月1日 下午4:44:25
 */
public class BillListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<BillInfo> list;// 优惠券列表
    private HashMap<String, Integer> map;

    public BillListAdapter(Context context, List<BillInfo> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<BillInfo>();
        } else {
            this.list = list;
        }
    }

    // 更新列表数据
    public void update(List<BillInfo> list) {
        if (list == null) {
            this.list = new ArrayList<BillInfo>();
        } else {
            this.list = list;
        }
        this.notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (list != null && list.size() > 0) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
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
        holder.init(position, list.get(position));
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

    String getDateString(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return "未知";
        }
        try {
            Date sqlDate = DateConvertor.getSqlDate(dateString);
            return DateConvertor.getSqlDateString(sqlDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }


    public void append(List<BillInfo> list2) {
        if (list2 == null) {
            return;
        }
        list.addAll(list2);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


}
