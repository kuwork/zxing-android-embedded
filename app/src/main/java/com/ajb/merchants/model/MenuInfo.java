package com.ajb.merchants.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.ajb.merchants.activity.AboutActivity;
import com.ajb.merchants.activity.AccountManagementActivity;
import com.ajb.merchants.activity.EditorActivity;
import com.ajb.merchants.activity.ModifyPhoneActivity;
import com.ajb.merchants.activity.ResetPasswordActivity;
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
    public static final String TYPE_OPERATE_NATIVE = "NATIVE";  //本地页面
    public static final String TYPE_OPERATE_WEB = "WEB";    //网页
    public static final String TYPE_OPERATE_NONE = "NONE";  //不能打开
    /**
     * 侧滑菜单code
     */
    public static final String TO_RECORD_GIVING = "200021";
    public static final String TO_RECORD_PURCHASE = "200022";
    public static final String TO_RECORD_RECYLE = "200023";
    public static final String TO_SETTING = "200024";
    public static final String TO_EXIT = "200025";
    /**
     * 主页功能菜单
     */
    public static final String TO_COUPON_PUBLISH = "200011";
    public static final String TO_COUPON_PURCHASE = "200012";
    public static final String TO_COUPON_RECYLE = "200013";
    public static final String TO_ACCOUNT_SETTING = "200014";
    public static final String TO_MESSAGE = "200015";
    public static final String TO_PWD_RESET = "200016";
    /*主页派送优惠*/
    public static final String TO_COUPON_SCAN = "200001";
    public static final String TO_COUPON_CAR_NUM = "200002";
    public static final String TO_COUPON_CARD = "200003";

    /**
     * 设置页面code
     */
    public static final String TO_CHECKUPDATE = "200031";
    public static final String TO_CLEARCACHE = "200032";
    public static final String TO_ABOUTUS = "200033";
    public static final String TO_PAGESETTING = "200034";
    public static final String TO_LOGIN_OUT = "200035";

    /**
     * 商家详情页面code
     */
    public static final String TO_STORE_DETAIL = "300001";  //商铺名称
    public static final String TO_STORE_ADDRESS = "300003"; //商铺地址
    public static final String TO_STORE_SCOPE = "300004";  //商铺经营范围
    public static final String TO_CONTACT = "300005";   //商铺联系人
    public static final String TO_PHONE = "300006";    //商铺电话

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
                if (TO_ACCOUNT_SETTING.equals(getMenuCode())) {
                    //账号管理
                    context.startActivity(new Intent(context, AccountManagementActivity.class));
                } else if (TO_ABOUTUS.equals(getMenuCode())) {
                    //关于我们
                    context.startActivity(new Intent(context, AboutActivity.class));
                } else if (TO_SETTING.equals(getMenuCode())) {
                    //系统设置
                    intent = new Intent(context, SettingActivity.class);
                    dealExtras(intent);
                    context.startActivity(intent);
                } else if (TO_CONTACT.equals(getMenuCode())
                        || TO_STORE_DETAIL.equals(getMenuCode()) ||
                        TO_STORE_ADDRESS.equals(getMenuCode()) ||
                        TO_STORE_SCOPE.equals(getMenuCode())
                        ) {
                    intent = new Intent(context, EditorActivity.class);
                    dealExtras(intent);
                    intent.putExtra(Constant.KEY_TITLE, getTitle());
                    intent.putExtra(Constant.KEY_DESC, getDesc());
                    context.startActivity(intent);
                } else if (TO_PHONE.equals(getMenuCode())) {
                    intent = new Intent(context, ModifyPhoneActivity.class);
                    dealExtras(intent);
                    context.startActivity(intent);
                } else if (TO_PWD_RESET.equals(getMenuCode())) {
                    intent = new Intent(context, ResetPasswordActivity.class);
                    dealExtras(intent);
                    context.startActivity(intent);
                }else if (TO_CLEARCACHE.equals(getMenuCode())) {
                    //清除缓存
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
                } else {

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