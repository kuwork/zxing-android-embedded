package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jerry on 16/5/18.
 */
public class CarParkingInInfo implements Serializable {


    private String account;
    private String cardId;//卡内编号
    private String accTime;
    private String channelId;
    private String carNo;
    private String cardSnId;//卡编号
    private String token;

    private List<Info> infoList;

    /**
     * tips :
     * hasTimeFavMsg : 已享有时间优惠：0分钟
     * hasValueFavMsg : 已享有金额优惠：13.0元
     * hasTimeFav : 0
     * hasValueFav : 0
     * valueTop : 2000
     * favList : [] 非自定义模式
     * sendModel : 1 ()
     * timeTop : 360
     * allowToSend : ()
     * couponType : 1
     * time : 0
     * value : 0
     */

    private List<CouponSendType> topList;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getAccTime() {
        return accTime;
    }

    public void setAccTime(String accTime) {
        this.accTime = accTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getCardSnId() {
        return cardSnId;
    }

    public void setCardSnId(String cardSnId) {
        this.cardSnId = cardSnId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Info> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<Info> infoList) {
        this.infoList = infoList;
    }

    public List<CouponSendType> getTopList() {
        return topList;
    }

    public void setTopList(List<CouponSendType> topList) {
        this.topList = topList;
    }


}
