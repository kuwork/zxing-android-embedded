package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2015/12/28 18:30
 */
public class CarNoSociety implements Serializable {


    /**
     * carNoLimit : 5
     * carNoList : [{"userName":"","ltdCode":"2000001","parkCode":"02000001","cardType":"月租卡","carNo":"粤A12398","enable":1,"createDate":"1451289316000","phoneNo":"","cardId":"9335309","updateDate":"null","id":"391a4f8c-d234-439f-afbc-f7e55fdc4a95"}]
     */

    private int carNoLimit;
    private String carOpenTip;
    private String carLimitTip;
    private String carCloseTip;
    /**
     * userName :
     * ltdCode : 2000001
     * parkCode : 02000001
     * cardType : 月租卡
     * carNo : 粤A12398
     * enable : 1
     * createDate : 1451289316000
     * phoneNo :
     * cardId : 9335309
     * updateDate : null
     * id : 391a4f8c-d234-439f-afbc-f7e55fdc4a95
     */

    private List<CardInfo> carNoList;

    public void setCarNoLimit(int carNoLimit) {
        this.carNoLimit = carNoLimit;
    }

    public void setCarNoList(List<CardInfo> carNoList) {
        this.carNoList = carNoList;
    }

    public int getCarNoLimit() {
        return carNoLimit;
    }

    public List<CardInfo> getCarNoList() {
        return carNoList;
    }

    public String getCarOpenTip() {
        return carOpenTip;
    }

    public void setCarOpenTip(String carOpenTip) {
        this.carOpenTip = carOpenTip;
    }

    public String getCarLimitTip() {
        return carLimitTip;
    }

    public void setCarLimitTip(String carLimitTip) {
        this.carLimitTip = carLimitTip;
    }

    public String getCarCloseTip() {
        return carCloseTip;
    }

    public void setCarCloseTip(String carCloseTip) {
        this.carCloseTip = carCloseTip;
    }
}


