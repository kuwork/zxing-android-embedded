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
