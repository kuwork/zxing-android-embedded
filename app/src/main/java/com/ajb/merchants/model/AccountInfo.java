package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 16/1/18.
 */
public class AccountInfo implements Serializable {

    private String accountName;


    private String storeName;
    private String headimgUrl;
    private String phone;
    private String coverimgUrl;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoverimgUrl() {
        return coverimgUrl;
    }

    public void setCoverimgUrl(String coverimgUrl) {
        this.coverimgUrl = coverimgUrl;
    }
}
