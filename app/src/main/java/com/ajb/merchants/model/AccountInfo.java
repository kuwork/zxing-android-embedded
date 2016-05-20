package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jerry on 16/1/18.
 */
public class AccountInfo implements Serializable {

    private String id;  //子账户id
    private String accountId;
    private String storeId;
    private String account;
    private String accountName;
    private String storeName;
    private String headimgUrl;
    private String phone;
    private String coverimgUrl;
    private String remark;  //备注
    private String password;//子账户密码
    private List<String> rightList; //权限列表

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getRightList() {
        return rightList;
    }

    public void setRightList(List<String> rightList) {
        this.rightList = rightList;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
