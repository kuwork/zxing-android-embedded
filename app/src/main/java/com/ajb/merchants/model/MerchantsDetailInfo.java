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
    private ModularMenu infoList;
    private Info topList;

    public List<Info> getBalanceList() {
        return balanceList;
    }

    public void setBalanceList(List<Info> balanceList) {
        this.balanceList = balanceList;
    }

    public ModularMenu getInfoList() {
        return infoList;
    }

    public void setInfoList(ModularMenu infoList) {
        this.infoList = infoList;
    }

    public Info getTopList() {
        return topList;
    }

    public void setTopList(Info topList) {
        this.topList = topList;
    }
}
