package com.ajb.merchants.model;

import com.google.gson.annotations.SerializedName;

public class ParkingInfo {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String centerLon;
    private String centerLat;
    private String ltdCode;// 企业ID
    private String parkCode;// 车场ID
    private String parkName;// 车场名
    private String parkingPrice;// 车位价格
    private String carportKeepCount;// 空车位数量
    private String inTime;
    @SerializedName("leaveTime")
    private String outTime;
    private String carNo;
    private String feeseason;
    private String editFee;
    private String cardType;

    public String getFeeseason() {
        return feeseason;
    }

    public void setFeeseason(String feeseason) {
        this.feeseason = feeseason;
    }

    public String getEditFee() {
        return editFee;
    }

    public void setEditFee(String editFee) {
        this.editFee = editFee;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    private boolean isCheck = false;
    private int status;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public String getCenterLon() {
        return centerLon;
    }

    public void setCenterLon(String centerLon) {
        this.centerLon = centerLon;
    }

    public String getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(String centerLat) {
        this.centerLat = centerLat;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getLtdCode() {
        return ltdCode;
    }

    public void setLtdCode(String ltdCode) {
        this.ltdCode = ltdCode;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkingPrice() {
        return parkingPrice;
    }

    public void setParkingPrice(String parkingPrice) {
        this.parkingPrice = parkingPrice;
    }

    public String getCarportKeepCount() {
        return carportKeepCount;
    }

    public void setCarportKeepCount(String carportKeepCount) {
        this.carportKeepCount = carportKeepCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
