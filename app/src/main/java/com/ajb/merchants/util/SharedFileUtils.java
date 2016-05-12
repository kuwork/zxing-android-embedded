package com.ajb.merchants.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 数据缓存
 *
 * @author chenming
 */
public class SharedFileUtils {
    public final static String LAST_CHECK_NEWEST_VERSION_CODE = "com.ajb.anjubao.intelligent.versioncode";
    public final static String IS_FIRST_IN = "isFirstIn";
    public static final String FILE_NAME = "pk_file";
    public static final String BANNER_LIST_SPLASH = "banner_list_splash";
    public static final String BANNER_LIST_LOCAL_SPLASH = "banner_list_local_splash_1";
    public static final String BANNER_LIST_HOME = "banner_list_home";
    public static final String BANNER_LIST_LOCAL_HOME = "banner_list_local_home_1";
    public static final String BANNER_LIST_HOME_SLIDE_MENU = "banner_list_home_slide_menu";
    public static final String BANNER_LIST_LOCAL_HOME_SLIDE_MENU = "banner_list_local_home_slide_menu_1";
    public static final String BANNER_LIST_HOME_ACTION = "banner_list_home_action";
    public static final String BANNER_LIST_LOCAL_HOME_ACTION = "banner_list_local_home_action_1";
    public static final String BANNER_LIST_PAY = "banner_list_pay";
    public static final String BANNER_LIST_LOCAL_PAY = "banner_list_local_pay";
    public static final String IS_LOGIN = "isLogin";
    public static final String LOGIN_NAME = "LoginName";
    public static final String DEVICE_ID = "deviceId";
    public static final String INVITE = "invite";
    public static final String BALANCE = "balance";
    public static final String LAST_LOC = "com.ajb.anjubao.intelligent.LAST_LOC";
    public static final String LAST_CITY = "com.ajb.anjubao.intelligent.LAST_CITY";
    //车牌历史
    public static final String LAST_CARNO_AREA = "Last_CarNo_Area";
    public static final String LAST_CARNO = "Last_CarNo";
    public static final String LAST_CARNO_HISTORY = "Last_CarNo_History";
    //地图搜索历史
    public static final String LAST_ADDRESS_HISTORY = "Last_Address_History";
    public static final String HOME_NAME = "HOME_NAME";
    public static final String COUPON_SETTING = "com.ajb.anjubao.intelligent.COUPON_SETTING";

    public static final String ACCOUNT_INFO = "com.ajb.anjubao.intelligent.ACCOUNT_INFO";
    //优惠历史
    public static final String COUPON_MONEY_LIST = "com.ajb.merchants.COUPON_MONEY_LIST";
    public static final String COUPON_TIME_LIST = "com.ajb.merchants.COUPON_TIME_LIST";

    private SharedPreferences sp;

    public SharedFileUtils(Context context) {
        sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        if (key == null || key.equals(""))
            throw new IllegalArgumentException(
                    "Key can't be null or empty string");
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        if (key == null || key.equals(""))
            throw new IllegalArgumentException(
                    "Key can't be null or empty string");
        return sp.getString(key, "");
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        Editor e = sp.edit();
        e.putInt(key, value);
        e.commit();
    }

    public void putBoolean(String key, boolean value) {
        if (key == null || key.equals(""))
            throw new IllegalArgumentException(
                    "Key can't be null or empty string");
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void remove(String key) {
        if (key == null || key.equals(""))
            throw new IllegalArgumentException(
                    "Key can't be null or empty string");
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean isContainsKey(String key) {
        return sp.contains(key);
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public void putLong(String key, long value) {
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
