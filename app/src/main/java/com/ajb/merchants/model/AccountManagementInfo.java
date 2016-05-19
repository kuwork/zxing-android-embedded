package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: Yuven
 * @date: 2016/5/19 15:38
 */
public class AccountManagementInfo implements Serializable {

    private String account;
    private String password;
    private List<PermissionInfo> rightList;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PermissionInfo> getRightList() {
        return rightList;
    }

    public void setRightList(List<PermissionInfo> rightList) {
        this.rightList = rightList;
    }
}
