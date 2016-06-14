package com.util;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class App {

	/**
	 * 获取app版本名
	 *
	 * @return 返回当前版本名
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "1.0";
		}
	}

	/**
	 * 获取app版本号
	 *
	 * @return 返回当前版本号
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			return 1;
		}
	}

	/**
	 * 获取app包名
	 *
	 * @return 返回包名
	 */
	public static String getPackageName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.packageName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * @Title getAppIcon
	 * @Description 获取应用图标
	 * @author 陈国宏
	 * @date 2014年1月3日 下午8:45:09
	 * @param context
	 * @return Drawable對象
	 */
	public static Drawable getAppIcon(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			Drawable drawable = info.applicationInfo.loadIcon(manager);
			return drawable;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * @Title getAppIconBitmap
	 * @Description 获取应用图标
	 * @author 陈国宏
	 * @date 2014年1月3日 下午8:45:52
	 * @param context
	 * @return Bitmap對象
	 */
	public static Bitmap getAppIconBitmap(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			Drawable drawable = info.applicationInfo.loadIcon(manager);
			return BitmapUtil.fromDrawable(drawable);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * @Title getApILevel
	 * @Description 返回API等级,由此判断系统版本号
	 * @author 陈国宏
	 * @date 2013年12月31日 上午9:06:37
	 * @return 系统版本号
	 */
	public static int getApILevel() {
		int sysVersion;
		try {
			sysVersion = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (Exception e) {
			sysVersion = 0;
		}
		return sysVersion;
	}

	/**
	 * @Title isPackageExist
	 * @Description .判断package是否存在
	 * @author 陈国宏
	 * @date 2013年12月31日 上午9:49:59
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isPackageExist(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		Intent intent = new Intent().setPackage(packageName);
		List<ResolveInfo> infos = manager.queryIntentActivities(intent,
				PackageManager.GET_INTENT_FILTERS);
		if (infos == null || infos.size() < 1) {
			return false;
		} else {
			return true;
		}
	}

	public static String getApplicationName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext()
					.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 64);
		} catch (NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	private String getSign(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					App.getPackageName(context), 64);
			android.content.pm.Signature[] sigs = info.signatures;
			// XLog.i("ANDROID_LAB", "sigs.len=" + sigs.length);
			// XLog.i("ANDROID_LAB", sigs[0].toCharsString());
			// XLog.i("ANDROID_LAB",SHA1Util.sum(sigs[0].toCharsString()));
			return sigs[0].toCharsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getMetaData(Context context, String key) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			String msg = appInfo.metaData.getString(key);
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getSha1SumString(Context ctx) {
		try {
			File file = new File(ctx.getPackageManager().getApplicationInfo(
					getPackageName(ctx), 0).sourceDir);
			return SHA1Util.sumFile(ctx,file);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}