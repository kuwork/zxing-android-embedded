package com.ajb.merchants.model;

import java.io.Serializable;

public class CardInfo implements Serializable{
    private String userName;
    private String ltdCode;
    private String parkCode;
    private String cardType;
    private String carNo;
    private int enable;
    private String createDate;
    private String phoneNo;
    private String cardId;
    private String updateDate;
    private String id;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLtdCode(String ltdCode) {
        this.ltdCode = ltdCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getLtdCode() {
        return ltdCode;
    }

    public String getParkCode() {
        return parkCode;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCarNo() {
        return carNo;
    }

    public int getEnable() {
        return enable;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getCardId() {
        return cardId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getId() {
        return id;
    }
}