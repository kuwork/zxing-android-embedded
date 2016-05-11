package com.ajb.merchants.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by chenlongjie on 2015/11/5.
 */
public class CarInParkingBuilder implements Serializable {
    private String park;
    private String carNo;//车牌
    private String cardId;//卡芯片号
    private String carSN;//卡编号
    private String parkCode;
    private String ltdCode;
    private String parkName;
    private String accTime;
    private String isPay;
    private String imgPath;
    private String payType;

    public CarInParkingBuilder() {
        super();
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getLtdCode() {
        return ltdCode;
    }

    public void setLtdCode(String ltdCode) {
        this.ltdCode = ltdCode;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getAccTime() {
        return accTime;
    }

    public void setAccTime(String accTime) {
        this.accTime = accTime;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String cardType) {
        this.payType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCarSN() {
        return carSN;
    }

    public void setCarSN(String carSN) {
        this.carSN = carSN;
    }

    public void build() {
        //2000001,02000001,安居宝展厅停车场,粤AN982C,2015-07-07 16:23:51,http://devupload.eanpa-gz-manager.com/images/20150707/16/20150707162351011000103.jpg,6fe42edc-e6da-4f4c-beaa-ead272e0d34b,8nluDrWefH6RpgcrZxxDZ5ku1NYo8c7Pr2jTSE4uSw6Ry3u7jcBQr7Xfxo5u7BbYuyfF1FRF11RO2Sf3pSUSwE9yA9fNLwBkY1pomr68VgDlalmLItQ7VfewkvrhJhSE\r\n,b2d42260-441a-49af-9b75-876d65030e13,临时卡,1
        if (TextUtils.isEmpty(park)) {
            return;
        }
        String[] str = park.split(",");
        setLtdCode(str[0]);
        setParkCode(str[1]);
        setParkName(str[2]);
        setCarNo(str[3]);


    }

}
