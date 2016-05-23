package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: Yuven
 * @date: 2016/5/20 16:52
 */
public class MerchantsDetailInfo implements Serializable {

    private List<Info> balanceList;
    private ModularMenu menu;
    private Info topList;

    public List<Info> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(List<Info> balanceList) {
        this.balanceList = balanceList;
    }

    public ModularMenu getMenu() {
        return menu;
    }

    public void setMenu(ModularMenu menu) {
        this.menu = menu;
    }

    public Info getTopList() {
        return topList;
    }

    public void setTopList(Info topList) {
        this.topList = topList;
    }
}
