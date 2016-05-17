package com.ajb.merchants.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jerry on 16/5/13.
 */
public class AccountSettingInfo implements Serializable {
    @SerializedName("menu")
    List<ModularMenu> modularMenus;
    List<BalanceLimitInfo> balanceList;
    @SerializedName("homeAccount")
    AccountInfo accountInfo;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public List<BalanceLimitInfo> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(List<BalanceLimitInfo> balanceList) {
        this.balanceList = balanceList;
    }

    public List<ModularMenu> getModularMenus() {
        return modularMenus;
    }

    public void setModularMenus(List<ModularMenu> modularMenus) {
        this.modularMenus = modularMenus;
    }
}
