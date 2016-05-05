package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * 用户设备信息
 * Created by jerry on 16/1/8.
 */
public class UserAppInfo implements Serializable {

    /**
     * phone : 13416333417
     * device : {"type":"Android","model":"PLK-UL00","versionRelease":"5.0.2","versionSDK":"22"}
     * version : {"type":"Android","versionCode":"17","versionName":"1.0"}
     */

    private String phone;

    private DeviceInfo device;

    public UserAppInfo() {
    }

    public UserAppInfo(String phone, DeviceInfo device, VersionInfo version) {
        this.phone = phone;
        this.device = device;
        this.version = version;
    }


    private VersionInfo version;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDevice(DeviceInfo device) {
        this.device = device;
    }

    public void setVersion(VersionInfo version) {
        this.version = version;
    }

    public String getPhone() {
        return phone;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public VersionInfo getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "UserAppInfo{" +
                "phone='" + phone + '\'' +
                ", device=" + device +
                ", version=" + version +
                '}';
    }
}
