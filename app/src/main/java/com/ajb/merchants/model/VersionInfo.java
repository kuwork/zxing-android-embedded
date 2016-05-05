package com.ajb.merchants.model;

import android.content.Context;

import com.util.App;

import java.io.Serializable;

/**
 * APP版本信息
 */
public class VersionInfo implements Serializable {
    /**
     * type : Android
     * versionCode : 17
     * versionName : 1.0
     */
    private String type;
    private String versionCode;
    private String versionName;

    public VersionInfo(Context context) {
        this.type = "Android";
        this.versionCode = App.getVersionCode(context) + "";
        this.versionName = App.getVersionName(context);
    }

    public VersionInfo() {
    }

    public VersionInfo(String type, String versionCode, String versionName) {
        this.type = type;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getType() {
        return type;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "type='" + type + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}