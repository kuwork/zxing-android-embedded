package com.ajb.merchants.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.Coupon;
import com.ajb.merchants.util.DateConvertor;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author jerry
 * @ClassName CouponListAdtpter
 * @Description 优惠券列表
 * @date 2015年9月1日 下午4:44:25
 */
public class CouponListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<Coupon> list;// 优惠券列表
    private HashMap<String, Integer> map;
    private boolean isHistory;

    public CouponListAdapter(Context context, List<Coupon> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<Coupon>();
        } else {
            this.list = list;
        }
    }

    // 更新列表数据
    public void update(List<Coupon> list) {
        if (list == null) {
            this.list = new ArrayList<Coupon>();
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
            convertView = mInflater.inflate(R.layout.coupon_item, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.init(context, list.get(position), position);
        return convertView;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    class ViewHolder {
        @ViewInject(R.id.couponHeader)
        private View couponHeader;
        @ViewInject(R.id.selectedLayout)
        private RelativeLayout selectedLayout;
        @ViewInject(R.id.couponDesc)
        private TextView couponDesc;
        @ViewInject(R.id.couponParkingType)
        private TextView couponParkingType;
        @ViewInject(R.id.couponTimeLimit)
        private TextView couponTimeLimit;
        @ViewInject(R.id.couponTimeLeft)
        private TextView couponTimeLeft;
        @ViewInject(R.id.couponTitle)
        private TextView couponTitle;

        public ViewHolder() {
            super();
        }

        public void init(Context context, Coupon coupon, int position) {
            // 1代表金额优惠券；2代表时间优惠券;3代表微信活动折扣优惠券
            SpannableString str;
            int color = Color.parseColor("#FCB636");
            String colorStr = coupon.getColor();
            if (!TextUtils.isEmpty(colorStr)) {
                color = Color.parseColor(colorStr);
            }
            GradientDrawable gd = (GradientDrawable) context.getResources()
                    .getDrawable(R.drawable.coupon_bg_bottom);
            gd.setColor(color);
            couponHeader.setBackgroundDrawable(gd);
            str = new SpannableString(coupon.getValue() + coupon.getUnit() + "");
            if (!TextUtils.isEmpty(str)) {
                int dotIndex = str.toString().indexOf('.');
                if (dotIndex != -1) {
                    str.setSpan(new RelativeSizeSpan(0.7f), dotIndex, str.length() - coupon.getUnit().length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                str.setSpan(new RelativeSizeSpan(0.5f), str.length()
                                - coupon.getUnit().length(), str.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                    str.setSpan(new ForegroundColorSpan(color), 0, str.length(),
//                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            couponTitle.setText(str);
            couponDesc.setText(TextUtils.isEmpty(coupon.getName()) ? ""
                    : coupon.getName());
            couponTimeLeft.setText(TextUtils.isEmpty(coupon.getDeadline()) ? ""
                    : coupon.getDeadline());
            couponParkingType.setText(
                    (TextUtils.isEmpty(coupon.getCompany()) ? ""
                            : coupon.getCompany()));
            couponTimeLimit.setText(getDateString(coupon.getEndTime()));
            if (coupon.isSelected()) {
                selectedLayout.setVisibility(View.VISIBLE);
            } else {
                selectedLayout.setVisibility(View.GONE);
            }

            if (isHistory()) {
                couponTimeLimit.setTextColor(Color.parseColor("#999999"));
                couponTitle.setTextColor(Color.parseColor("#999999"));
                couponDesc.setTextColor(Color.parseColor("#999999"));
                couponParkingType.setTextColor(Color.parseColor("#999999"));
                couponTimeLeft.setTextColor(Color.parseColor("#999999"));
            } else {
                couponTitle.setTextColor(Color.parseColor("#FCB636"));
                couponTimeLimit.setTextColor(Color.parseColor("#666666"));
                couponDesc.setTextColor(Color.parseColor("#333333"));
                couponParkingType.setTextColor(Color.parseColor("#999999"));
                couponTimeLeft.setTextColor(Color.parseColor("#FC6455"));
            }

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

    public boolean setSelected(int position, String ltdCode, String parkCode) {
        if (ltdCode == null || parkCode == null) {
            return false;
        }
        Coupon coupon = list.get(position);
        if ("-1".equals(coupon.getLtdCode())
                || (!TextUtils.isEmpty(coupon.getLtdCode()) && ltdCode
                .equals(coupon.getLtdCode()))) {
            coupon.setSelected(!coupon.isSelected());
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public List<Coupon> getSelected() {
        List<Coupon> tmp = new ArrayList<Coupon>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                tmp.add(list.get(i));
            }
        }
        return tmp;
    }

    public void append(List<Coupon> list2) {
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

    public void remove(Coupon couponWillUse) {
        list.remove(couponWillUse);
        notifyDataSetChanged();
    }

}
