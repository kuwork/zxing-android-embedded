package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;

	public OrderListAdapter(Context context, List<Map<String, Object>> list) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		if (list != null) {
			this.mData = list;
		} else {
			this.mData = new ArrayList<Map<String, Object>>();
		}
	}

	// 更新列表状态
	public void changeStatue(List<Map<String, Object>> list) {
		if (list != null) {
			this.mData = list;
		} else {
			this.mData = new ArrayList<Map<String, Object>>();
		}
		notifyDataSetChanged();
	}

	/**
	 * 根据id删除某条数据
	 */
	public void deleteData(int arg) {
		if(0<=arg&&arg<mData.size()){
			mData.get(arg).put("state", "1");
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = mInflater.inflate(R.layout.order_item, null);
			holder.number = (TextView) arg1.findViewById(R.id.line_number);
			holder.parkingName = (TextView) arg1
					.findViewById(R.id.order_item_parking_name);
			holder.carport = (TextView) arg1
					.findViewById(R.id.order_item_carport);
			holder.carNo = (TextView) arg1.findViewById(R.id.order_item_car_no);
			holder.delete = (TextView) arg1
					.findViewById(R.id.order_item_parking_delete);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		holder.number.setText((arg0 + 1) + "");
		holder.parkingName.setText((String) mData.get(arg0).get("parkingName"));
		holder.carport.setText((String) mData.get(arg0).get("carport"));
		holder.carNo.setText((String) mData.get(arg0).get("carNo"));
		holder.delete.setTag(R.id.order_value, mData.get(arg0));
		holder.delete.setTag(R.id.order_index, arg0);
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoMyOrder((Map<String, Object>) v.getTag(R.id.order_value));
			}
		});

		if (mData.get(arg0).get("state").equals("6")) {// 已离场显示完成
			holder.delete.setText("已离场");
		} else if (mData.get(arg0).get("state").equals("5")) {// 已入场
			holder.delete.setText("已入场");
		} else if (mData.get(arg0).get("state").equals("4")) {// 已预约,入场前
			if (mData.get(arg0).get("canClose").equals("1")) {// 可以取消
				holder.delete.setEnabled(true);
				holder.delete.setOnClickListener(new OnClickListener() {// 取消

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								deleteOrderClickListener
										.onDeleteClick((Integer) v
												.getTag(R.id.order_index));
							}
						});
				holder.delete.setText("取消");

			} else {// 不能取消
				holder.delete.setText("预约中");
				holder.delete.setEnabled(true);
			}

		} else if (mData.get(arg0).get("state").equals("0")) {
			holder.delete.setText("系统已取消");

		} else if (mData.get(arg0).get("state").equals("1")) {
			holder.delete.setText("用户已取消");

		}
		return arg1;
	}

	class ViewHolder {

		TextView parkingName;
		TextView carport;
		TextView carNo;
		TextView delete;
		TextView number;

	}

	public void clearData() {
		if (mData != null && mData.size() > 0) {
			mData.clear();
		}
		notifyDataSetChanged();
	}

	public interface onDeleteOrderClickListener {
		public void onDeleteClick(int arg);
	}

	private onDeleteOrderClickListener deleteOrderClickListener;

	public void setOnDeleteClickListener(
			onDeleteOrderClickListener deleteOrderClickListener) {
		this.deleteOrderClickListener = deleteOrderClickListener;
	}

	public void gotoMyOrder(Map<String, Object> map) {
		boolean flag = false;
		// if (map.get("state").equals("6")) {// 已离场显示完成
		// flag = true;
		// } else if (map.get("state").equals("5")) {// 已入场
		// flag = true;
		// } else if (map.get("state").equals("4")) {// 已预约,入场前
		//
		// if (map.get("canClose").equals("1")) {// 可以取消
		//
		// flag = false;
		// } else {// 不能取消
		// flag = true;
		// }
		//
		// } else if (map.get("state").equals("0")) {
		// flag = false;
		//
		// } else if (map.get("state").equals("1")) {
		// flag = false;
		// }
//		flag = true;
//		if (flag) {
//			Intent intent = new Intent();
//			intent.setClass(context, MyOrderActivity.class);
//			intent.putExtra("订单ID", (String) map.get("orderId"));
//			intent.putExtra("tag", "0");
//			context.startActivity(intent);
//		}
	}

}
