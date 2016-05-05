package com.ajb.merchants.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.PayWay;

import java.util.ArrayList;
import java.util.List;

public class PayTypeAdapter extends BaseAdapter {

	private Context context;
	private String types;
	private LayoutInflater mInflater;
	private List<PayWay> list;
	private PayWay payWay = new PayWay();
	private Drawable alipay, wechatpay;

	public PayTypeAdapter(Context context, String types) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.types = types;

		list = new ArrayList<PayWay>();
		Resources res = context.getResources();
		alipay = res.getDrawable(R.mipmap.alipay);
		wechatpay = res.getDrawable(R.mipmap.wechatpay);
		
		if (types.contains("1")) {// 1:微信支付,2：支付宝,3银联
			payWay.setLogo(wechatpay);
			payWay.setType("微信支付");
			payWay.setWay("1");
			list.add(payWay);
		} 
		if (types.contains("2")) {
			payWay = new PayWay();
			payWay.setLogo(alipay);
			payWay.setType("支付宝支付");
			payWay.setWay("2");
			list.add(payWay);
		} 
		if (types.contains("3")) {
			payWay = new PayWay();

		} 
	}

	// 更新列表状态
	public void setPayTypes(String types) {
		this.types = types;
		list.clear();
		if (types.contains("1")) {// 1:微信支付,2：支付宝,3银联
			payWay.setLogo(wechatpay);
			payWay.setType("微信支付");
			payWay.setWay("1");
			list.add(payWay);
		} 
		if (types.contains("2")) {
			payWay = new PayWay();
			payWay.setLogo(alipay);
			payWay.setType("支付宝支付");
			payWay.setWay("2");
			list.add(payWay);
		} 
		if (types.contains("3")) {

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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = mInflater.inflate(R.layout.pay_type_item, null);
			holder.iv = (ImageView) arg1.findViewById(R.id.iv);
			holder.tv = (TextView) arg1.findViewById(R.id.tv);

			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		holder.iv.setImageDrawable(list.get(arg0).getLogo());
		holder.tv.setText(list.get(arg0).getType());

		return arg1;
	}

	class ViewHolder {
		ImageView iv;
		TextView tv;

	}

}
