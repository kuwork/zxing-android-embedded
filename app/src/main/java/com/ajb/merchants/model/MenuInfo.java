package com.ajb.merchants.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.ajb.merchants.activity.AboutActivity;
import com.ajb.merchants.activity.SettingActivity;
import com.ajb.merchants.activity.WebViewActivity;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.DataCleanManager;
import com.util.PathManager;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

public class MenuInfo implements Serializable {

    private String menuCode;
    private String menuValue;
    //
    private String operateType;
    private String title;
    private String type;
    private String linkUrl;
    private String menuPictureUrl;
    private String desc;
    private int groupId;
    private Map<String, String> extras;
    private boolean needLogin;
    //识别布局类型
    public static final String TYPE_NORMAL = "NORMAL";
    public static final String TYPE_SEPARATOR = "SEPARATOR";
    //大的分割view
    public static final String TYPE_DIVIDE = "DIVIDE";
    //操作类型
    public static final String TYPE_OPERATE_NATIVE = "NATIVE";
    public static final String TYPE_OPERATE_WEB = "WEB";
    /**
     * 侧滑菜单code
     */
    public static final String TO_CARLIST = "190001";
    public static final String TO_COUPONLIST = "190002";
    public static final String TO_MYORDERLIST = "190003";
    public static final String TO_PRAKINGRECORD = "190004";
    public static final String TO_MYBILLLIST = "190005";
    public static final String TO_COUPONCODE = "190007";
    public static final String TO_SOCIETYSHARE = "190008";
    public static final String TO_SETTING = "190009";
    public static final String TO_EXIT = "190010";
    public static final String TO_SAFEGUARD = "190011";
    public static final String TO_PRESENTEXP = "190012";
    public static final String TO_ACCOUNT = "190013";
    public static final String TO_MYBURSE = "190014";
    /**
     * 设置页面code
     */
    public static final String TO_CHECKUPDATE = "180001";
    public static final String TO_CLEARCACHE = "180002";
    public static final String TO_FEEDBACK = "180003";
    public static final String TO_ABOUTUS = "180004";
    public static final String TO_PAGESETTING = "180005";
    //测试ye
    public static final String TO_INDOOR_NAV = "200001";


    public MenuInfo() {
    }


    public MenuInfo(int groupId, String menuCode, String menuValue, String operateType, String title, String type, String linkUrl, String menuPictureUrl, String desc, Map<String, String> extras, boolean needLogin) {
        this.menuCode = menuCode;
        this.menuValue = menuValue;
        this.operateType = operateType;
        this.title = title;
        this.type = type;
        this.linkUrl = linkUrl;
        this.menuPictureUrl = menuPictureUrl;
        this.desc = desc;
        this.groupId = groupId;
        this.extras = extras;
        this.needLogin = needLogin;
    }

    //ActionBar菜单
    public MenuInfo(String title, String type) {
        this(0, "0", null, TYPE_OPERATE_NATIVE, title, type, null, null, null, null, false);
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public void setMenuValue(String menuValue) {
        this.menuValue = menuValue;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setMenuPictureUrl(String menuPictureUrl) {
        this.menuPictureUrl = menuPictureUrl;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public String getMenuValue() {
        return menuValue;
    }

    public String getOperateType() {
        return operateType;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getMenuPictureUrl() {
        return menuPictureUrl;
    }

    @Override
    public String toString() {
        return "MenuInfo{" +
                "needLogin=" + needLogin +
                ", menuCode='" + menuCode + '\'' +
                ", menuValue='" + menuValue + '\'' +
                ", operateType='" + operateType + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", menuPictureUrl='" + menuPictureUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", groupId=" + groupId +
                ", extras=" + extras +
                '}';
    }

    public void click(Context context) {
        if (MenuInfo.TYPE_SEPARATOR.equals(getType()) || MenuInfo.TYPE_DIVIDE.equals(getType())) {
            return;
        }
        Intent intent;
        switch (getOperateType()) {
            case MenuInfo.TYPE_OPERATE_NATIVE:
                //系统设置
                if (TO_SETTING.equals(getMenuCode())) {
                    intent = new Intent(context, SettingActivity.class);
                    dealExtras(intent);
                    context.startActivity(intent);
                } else
                    //关于我们
                    if (TO_ABOUTUS.equals(getMenuCode())) {
                        context.startActivity(new Intent(context, AboutActivity.class));
                    } else
                        //清除缓存
                        if (TO_CLEARCACHE.equals(getMenuCode())) {
                            String cacheDirPath = PathManager.getDiskCacheDir(context) + File.separator + "WebCache";
                            String cacheDirPath2 = PathManager.getDiskCacheDir(context) + "/database/webview.db";
                            String cacheDirPath3 = PathManager.getDiskCacheDir(context) + "/database/webviewCache.db";
                            // 清除缓存
                            DataCleanManager.cleanApplicationCache(context, cacheDirPath, cacheDirPath2, cacheDirPath3);
//                AlarmManager manager = (AlarmManager) getBaseContext()
//                        .getSystemService(Context.ALARM_SERVICE);
//                Intent i = getBaseContext().getPackageManager()
//                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                PendingIntent pendingIntent = PendingIntent.getActivity(
//                        getBaseContext(), 0, i, Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                long triggerAtTime = SystemClock.elapsedRealtime();
//                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);
//
//                System.exit(0);
                        }
                break;

            case MenuInfo.TYPE_OPERATE_WEB:
                if (TextUtils.isEmpty(getLinkUrl())) {
                    return;
                }
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(Constant.KEY_TITLE, getTitle());
                if (getLinkUrl().startsWith("/")) {
                    intent.putExtra(Constant.KEY_URL, Constant.SERVER_URL + getLinkUrl().substring(1));
                } else {
                    intent.putExtra(Constant.KEY_URL, getLinkUrl() + "?t=" + System.currentTimeMillis());
                }
                dealExtras(intent);
                context.startActivity(intent);
                break;
        }

    }


    /**
     * @param str
     * @Title showToast
     * @Description Toast显示提示信息
     * @author jerry
     * @date 2015年7月8日 下午12:45:29
     */
    protected void showToast(String str, Context c) {
        Toast.makeText(c, str, Toast.LENGTH_SHORT).show();
    }

    public void dealExtras(Intent intent) {
        if (intent == null) {
            return;
        }
        if (getExtras() != null) {
            for (Map.Entry<String, String> entry : getExtras().entrySet()) {
                if ("false".equals(entry.getValue().toLowerCase()) || "true".equals(entry.getValue().toLowerCase())) {
                    intent.putExtra(entry.getKey(), Boolean.valueOf(entry.getValue()).booleanValue());
                } else {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }
            }
        }
    }

}