package com.ajb.merchants.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class MySwipeRefreshLayout extends SwipeRefreshLayout {

	private boolean canRefresh = true;

	public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MySwipeRefreshLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canChildScrollUp() {
		if (!canRefresh) {
			return true;
		}
		return super.canChildScrollUp();
	}

	public boolean isCanRefresh() {
		return canRefresh;
	}

	public void setCanRefresh(boolean canRefresh) {
		this.canRefresh = canRefresh;
	}
}
