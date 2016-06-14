package com.util;

import android.content.Context;

/**
 * File: DensityUtil.java </p> Copyright: Useease Copyright (c) 2012</p>
 * 
 * @author Fantouch
 * @version 1.0
 * @since 2012-12-13上午10:02:13
 * @modify
 * @description dp px 转换工具
 */
public class DensityUtil {
	private final static String TAG = "DensityUtil";
	private static float density = 0f;
	private static float defaultDensity = 1.5f;// 高分辨率的手机density普遍接近1.5

	private DensityUtil() {
	}

	public static void setDensity(float density) {
		DensityUtil.density = density;
	}

	public static float getDensity(Context context) {
		// DisplayMetrics metrics = new DisplayMetrics();
		// Display display = ((Activity) context).getWindowManager()
		// .getDefaultDisplay();
		// display.getMetrics(metrics);
		// return metrics.density;
		return context.getResources().getDisplayMetrics().density;
	}

	public static int getScreenWidth(Context context) {
		// DisplayMetrics metrics = new DisplayMetrics();
		// Display display = ((Activity) context).getWindowManager()
		// .getDefaultDisplay();
		// display.getMetrics(metrics);
		// return metrics.widthPixels;
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight(Context context) {
		// DisplayMetrics metrics = new DisplayMetrics();
		// Display display = ((Activity) context).getWindowManager()
		// .getDefaultDisplay();
		// display.getMetrics(metrics);
		// return metrics.heightPixels;
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * @Title dp2px
	 * @Description 将dip或dp值转换为px值，保证尺寸大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午1:48:55
	 * @param dpValue
	 * @return
	 */
	public static int dp2px(Context context, float dpValue) {
		int px;
		if (density == 0) {
			if (context != null) {
				density = getDensity(context);
			}
			if (density == 0) {
				density = defaultDensity;
			}
		}
		px = (int) (dpValue * density + 0.5f);
//		XLog.i(TAG, "px = " + px);
		return px;
	}

	/**
	 * @Title px2dp
	 * @Description 将px值转换为dip或dp值，保证尺寸大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午1:48:30
	 * @param pxValue
	 * @return dp
	 */
	public static int px2dp(Context context, float pxValue) {
		int dp;
		if (density == 0) {
			if (context != null) {
				density = getDensity(context);
			}
			if (density == 0) {
				density = defaultDensity;
			}
		}
		dp = (int) (pxValue / density + 0.5f);
//		XLog.i(TAG, "dp = " + dp);
		return dp;
	}

	/**
	 * @Title px2sp
	 * @Description 将px值转换为sp值，保证文字大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午2:06:26
	 * @param context
	 * @param pxValue
	 * @return sp
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * @Title sp2px
	 * @Description 将sp值转换为px值，保证文字大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午2:07:00
	 * @param context
	 * @param spValue
	 * @return px
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * @Title dp2sp
	 * @Description 将dp值转换为sp值，保证文字大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午2:07:09
	 * @param context
	 * @param dpValue
	 * @return sp
	 */
	public static int dp2sp(Context context, float dpValue) {
		if (density == 0) {
			density = getDensity(context);
		}
		return px2sp(context, dp2px(context, dpValue));
	}

	/**
	 * @Title sp2dp
	 * @Description 将sp值转换为px值，保证文字大小不变
	 * @author 陈国宏
	 * @date 2014年1月20日 下午2:07:45
	 * @param context
	 * @param spValue
	 * @return dp
	 */
	public static int sp2dp(Context context, float spValue) {
		if (density == 0) {
			density = getDensity(context);
		}
		return px2dp(context, sp2px(context, spValue));
	}
}