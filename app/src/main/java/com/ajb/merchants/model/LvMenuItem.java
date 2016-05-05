package com.ajb.merchants.model;

import android.text.TextUtils;

public class LvMenuItem {
    // 操作类型
    public static final String OP_NATIVE = "NATIVE";
    public static final String OP_WEB = "WEB";
    public static final String OP_NONE = "NONE";
    // 布局类型
    private static final int NO_ICON = 0;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_NO_ICON = 1;
    public static final int TYPE_SEPARATOR = 2;
    // 属性
    public int type;//类型
    public String name;//名字
    public int actionCode;//动作指标
    public boolean needLogin;
    public int icon;//图标资源ID
    public String iconUrl;//图标链接
    public String link;//网页链接


    public LvMenuItem(int icon, String name, int actionCode, boolean needLogin) {
        this.icon = icon;
        this.name = name;
        this.actionCode = actionCode;
        this.needLogin = needLogin;
        if (icon == NO_ICON && TextUtils.isEmpty(name)) {//分割线
            type = TYPE_SEPARATOR;
        } else if (icon == NO_ICON) {
            type = TYPE_NO_ICON;
        } else {
            type = TYPE_NORMAL;
        }
        if (type != TYPE_SEPARATOR && TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("you need set a name for a non-SEPARATOR item");
        }

    }

    public LvMenuItem(String name) {
        this(NO_ICON, name, -1, false);
    }

    public LvMenuItem() {
        this(null);
    }


}