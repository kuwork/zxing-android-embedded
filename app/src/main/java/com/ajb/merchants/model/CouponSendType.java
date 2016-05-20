package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

public class CouponSendType implements Serializable {

    public static final double TYPE_SERVER = 1;
    public static final double TYPE_NATIVE = 2;

    public static final double COUPON_MONEY = 1;
    public static final double COUPON_TIME = 2;


    private String title;
    private String tips;//favList空列表提示
    private String hasTimeFavMsg;//已享有时间优惠的中文说明，如2小时
    private String hasValueFavMsg;//已享有金额优惠的中文说明，如2元
    private double hasTimeFav;//已享有时间优惠的值，以分钟为单位，如60，为60分钟
    private double hasValueFav;//已享有金额优惠的值
    private double valueTop;
    private int sendModel;//1:非自定义,2:自定义
    private double timeTop;
    private String allowToSend;//00:可以派发,01:不能再派发
    private double couponType;//派发优惠券类型，1为金额优惠，2为时间优惠
    private double time;
    private double value;
    private String unit;

    private List<Coupon> favList;//非自定义模式

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getHasTimeFavMsg() {
        return hasTimeFavMsg;
    }

    public void setHasTimeFavMsg(String hasTimeFavMsg) {
        this.hasTimeFavMsg = hasTimeFavMsg;
    }

    public String getHasValueFavMsg() {
        return hasValueFavMsg;
    }

    public void setHasValueFavMsg(String hasValueFavMsg) {
        this.hasValueFavMsg = hasValueFavMsg;
    }

    public double getHasTimeFav() {
        return hasTimeFav;
    }

    public void setHasTimeFav(double hasTimeFav) {
        this.hasTimeFav = hasTimeFav;
    }

    public double getHasValueFav() {
        return hasValueFav;
    }

    public void setHasValueFav(double hasValueFav) {
        this.hasValueFav = hasValueFav;
    }

    public double getValueTop() {
        return valueTop;
    }

    public void setValueTop(double valueTop) {
        this.valueTop = valueTop;
    }

    public int getSendModel() {
        return sendModel;
    }

    public void setSendModel(int sendModel) {
        this.sendModel = sendModel;
    }

    public double getTimeTop() {
        return timeTop;
    }

    public void setTimeTop(double timeTop) {
        this.timeTop = timeTop;
    }

    public String getAllowToSend() {
        return allowToSend;
    }

    public void setAllowToSend(String allowToSend) {
        this.allowToSend = allowToSend;
    }

    public double getCouponType() {
        return couponType;
    }

    public void setCouponType(double couponType) {
        this.couponType = couponType;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Coupon> getFavList() {
        return favList;
    }

    public void setFavList(List<Coupon> favList) {
        this.favList = favList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}