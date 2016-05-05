package com.ajb.merchants.others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.widget.Toast;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.ajb.merchants.R;
import com.ajb.merchants.model.CityCharModle;
import com.ajb.merchants.model.ProvinceInfo;
import com.baidu.mapapi.SDKInitializer;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.util.App;

public class MyApplication extends Application {
	public static Set<String> citySet = new HashSet<String>();
	public static List<Map<String, Object>> cityList;
	public static List<Map<String, String>> listHashMaps;
	private static String[] citySort;// 排序
	private static List<CityCharModle> list2;// 获取当前区域
	private static String[] currentCity;// 获取当前城市和区域
	private static List<String> CarNumberList; // 车牌列表
	private static HttpUtils httpUtilsNoCache, httpUtils;
	private static DbUtils dbCity;

	public static List<String> getCarNumberList() {

		if (null == CarNumberList) {
			return new ArrayList<String>();
		}

		return CarNumberList;
	}

	public static void setCarNumberList(List<String> carNumberList) {
		CarNumberList = carNumberList;
	}

	public static String[] getCurrentCity() {
		return currentCity;
	}

	public static void setCurrentCity(String[] currentCity) {
		MyApplication.currentCity = currentCity;
	}

	public static List<CityCharModle> getList2() {
		return list2;
	}

	public static void setList2(List<CityCharModle> list2) {
		MyApplication.list2 = list2;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		if (!"official".equals(App.getMetaData(this, "update_type"))) {
			JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		}
		JPushInterface.init(this); // 初始化 JPush
		setStyleCustom();
	}

	public static void setListCity(Set<String> citySet) {
		MyApplication.citySort = citySet.toArray(new String[] {});
	}

	public static String[] getCitySort() {
		return citySort;
	}

	// 读取.xls文件中的城市分类列表
	public static void initCityData(Context context) {
		if (cityList != null && cityList.size() > 0) {
			return;
		}
		String cityed = "";
		String citid = "";
		try {
			DbUtils db = getDbCity(context);
			List<ProvinceInfo> list = db.findAll(Selector
					.from(ProvinceInfo.class).where(WhereBuilder.b())
					.orderBy("cityPinyin", true));
			cityList = new ArrayList<Map<String, Object>>();
			List<Map<String, String>> childList = null;
			listHashMaps = new ArrayList<Map<String, String>>();
			HashMap<String, Object> hm = new HashMap<String, Object>();
			for (int i = 0; i < list.size(); ++i) {
				ProvinceInfo p = list.get(i);

				String id = p.getDistrictCode();
				String area = p.getDistrictName();
				String cityid = p.getCityCode();
				String city = p.getCityName();

				citySet.add(city);

				if (!citid.endsWith(cityid)) {
					citid = cityid;
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put(city, cityid);
					listHashMaps.add(hashMap);
				}

				if (!cityed.equals(city)) {
					if (childList != null && childList.size() != 0) {
						hm.put(cityed, childList);
					}
					childList = new ArrayList<Map<String, String>>();
					cityed = city;
				}
				Map<String, String> childMap = new HashMap<String, String>();
				childMap.put("AreaName", area);
				childMap.put("AreaId", id);
				childList.add(childMap);
				if (i == list.size() - 1) {
					hm.put(cityed, childList);
				}
			}
			cityList.add(hm);
			setListCity(citySet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean flag = false;

	private static String productPay = "1";// 1为停车缴费，2为会员激活，3为会员续期，4用户充值

	public static String getProductPay() {
		return productPay;
	}

	public static void setProductPay(String productPay) {
		MyApplication.productPay = productPay;
	}

	public static boolean isFlag() {
		return flag;
	}

	public static void setFlag(boolean flag) {
		MyApplication.flag = flag;
	}

	public static boolean getFlag() {
		return flag;
	}

	private static Activity payActivity;

//	public static void exit(Context context) {
//		ActivityManager activityManager = (ActivityManager) context
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		// Intent startMain = new Intent(Intent.ACTION_MAIN);
//		// startMain.addCategory(Intent.CATEGORY_HOME);
//		// startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		// context.startActivity(startMain);
//		// Intent startMain = new Intent(context, HomePage2Activity.class);
//		// startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		// startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		// startMain.putExtra("isExit", true);
//		// context.startActivity(startMain);
//		android.os.Process.killProcess(android.os.Process.myPid());
//	}

	public static void setActivity(Activity payActivity) {
		MyApplication.payActivity = payActivity;
	}

	public static Activity getActivity() {
		return MyApplication.payActivity;
	}

	public static DbUtils getDbCity(Context context) {
		if (dbCity == null) {
			dbCity = DbUtils.create(context, "citys.db", 3,
					new DbUpgradeListener() {
						@Override
						public void onUpgrade(DbUtils db, int oldVersion,
								int newVersion) {
							try {
								if (oldVersion != newVersion) {
									db.dropTable(ProvinceInfo.class);
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
					});
		}
		return dbCity;
	}

	public static HttpUtils getNoCacheHttpUtils() {
		if (httpUtilsNoCache == null) {
			httpUtilsNoCache = new HttpUtils();
			// 设置当前请求的缓存时间
			httpUtilsNoCache.configCurrentHttpCacheExpiry(0 * 1000);
			// 设置默认请求的缓存时间
			httpUtilsNoCache.configDefaultHttpCacheExpiry(0);
			// 设置线程数
			httpUtilsNoCache.configRequestThreadPoolSize(5);
			httpUtilsNoCache.configResponseTextCharset("utf-8");
		}
		return httpUtilsNoCache;
	}

	public static HttpUtils getHttpUtils() {
		if (httpUtils == null) {
			httpUtils = new HttpUtils();
			// 设置线程数
			httpUtils.configRequestThreadPoolSize(5);
			httpUtils.configResponseTextCharset("utf-8");
		}
		return httpUtils;
	}

	/**
	 * 设置通知栏样式 - 定义通知栏Layout
	 */
	private void setStyleCustom() {
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(
				getApplicationContext(), R.layout.notification_msg_builder,
				R.id.icon, R.id.title, R.id.content);
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
																	// Notification.DEFAULT_SOUND）或者震动（
																	// Notification.DEFAULT_VIBRATE）
		builder.developerArg0 = "developerArg";
		JPushInterface.setDefaultPushNotificationBuilder(builder);

		builder = new CustomPushNotificationBuilder(getApplicationContext(),
				R.layout.notification_msg_builder_second, R.id.icon,
				R.id.title, R.id.content);
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
																	// Notification.DEFAULT_SOUND）或者震动（
																	// Notification.DEFAULT_VIBRATE）
		builder.developerArg0 = "developerArg1";
		JPushInterface.setPushNotificationBuilder(1, builder);

		builder = new CustomPushNotificationBuilder(getApplicationContext(),
				R.layout.notification_msg_builder_third, R.id.icon, R.id.title,
				R.id.content);
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
		// Notification.DEFAULT_SOUND）或者震动（
		// Notification.DEFAULT_VIBRATE）
		builder.developerArg0 = "developerArg2";
		JPushInterface.setPushNotificationBuilder(3, builder);
	}
}