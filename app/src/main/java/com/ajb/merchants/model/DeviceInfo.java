package com.ajb.merchants.model;

import android.os.Build;

import java.io.Serializable;

/**
 * 设备信息
 */
public class DeviceInfo implements Serializable {
    /**
     * type : Android
     * model : PLK-UL00
     * versionRelease : 5.0.2
     * versionSDK : 22
     */
    private String type;
    private String model;
    private String versionRelease;
    private String versionSDK;

    public DeviceInfo() {
        this.type = "Android";
        this.model = Build.MODEL;
        this.versionRelease = Build.VERSION.RELEASE;
        this.versionSDK = Build.VERSION.SDK_INT + "";
    }

    public DeviceInfo(String type, String model, String versionRelease, String versionSDK) {
        this.type = type;
        this.model = model;
        this.versionRelease = versionRelease;
        this.versionSDK = versionSDK;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVersionRelease(String versionRelease) {
        this.versionRelease = versionRelease;
    }

    public void setVersionSDK(String versionSDK) {
        this.versionSDK = versionSDK;
    }

    public String getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    public String getVersionRelease() {
        return versionRelease;
    }

    public String getVersionSDK() {
        return versionSDK;
    }
}