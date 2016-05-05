package com.ajb.merchants.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ajb.merchants.R;

public class PopupWindowAdapter {
	/** 返回网格布局的适配器 */
	public static ArrayAdapter<String> getAdapter(Context context) {
		String[] list = new String[] { "京", "津", "冀", "晋", "蒙", "辽", "吉", "黑",
				"沪", "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
				"琼", "渝", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新" };
		ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(context,
				R.layout.grid_text_item, R.id.carno_item_title, list);
		return simpleAdapter;
	}
}
