package com.ajb.merchants.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.Coupon;
import com.ajb.merchants.util.DateConvertor;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName CouponListAdtpter
 * @Description 优惠券列表
 * @author jerry
 * @date 2015年9月1日 下午4:44:25
 */
public class CouponListPayAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Coupon> list;// 优惠券列表
	private HashMap<String, Integer> map;
	private Gson gson;

	public CouponListPayAdapter(Context context, List<Coupon> list) {
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
			convertView = mInflater.inflate(R.layout.couponpay_item, null);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.init(context, list.get(position), position);
		holder.hideLine(getCount(), position);
		return convertView;
	}

	class ViewHolder {
		@ViewInject(R.id.title)
		private TextView title;
		@ViewInject(R.id.line)
		private TextView line;
		@ViewInject(R.id.img)
		private TextView img;

		public ViewHolder() {
			super();
		}

		public void init(Context context, Coupon coupon, int position) {
			// 1代表金额优惠券；2代表时间优惠券;3代表微信活动折扣优惠券
			int color = context.getResources().getColor(R.color.coupon_blue);
			SpannableString str;
			title.setText(coupon.getValue() + coupon.getUnit());
			title.append(coupon.getName());
		}

		public void hideLine(int size, int position) {
			if (size - 1 == position) {
				this.line.setVisibility(View.GONE);
			} else {
				this.line.setVisibility(View.VISIBLE);
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

	
	
	public void setSelected(int position) {
		Coupon coupon = list.get(position);
		coupon.setSelected(!coupon.isSelected());
		notifyDataSetChanged();
	}

	public void append(List<Coupon> list2) {
		if (list2 == null) {
			return;
		}
		list.addAll(list2);
		notifyDataSetChanged();
	}

	public void del(Coupon coupon) {
		if (coupon == null) {
			return;
		}
		list.remove(coupon);
		notifyDataSetChanged();
	}

	public List<Coupon> getList() {
		return list;
	}

	public void setList(List<Coupon> list) {
		this.list = list;
	}
}
