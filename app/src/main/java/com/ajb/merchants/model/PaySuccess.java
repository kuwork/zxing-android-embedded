package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/3/15 14:52
 */
public class PaySuccess implements Serializable {

    /**
     * tradeState : 0
     * orderId : 249b2b01-3988-494f-aa7f-a41e1bab2fca
     * tradeMsg : 已成功缴费0.01元,+5积分
     * info : [{"value":"","key":"车辆已停时长"},{"value":"2016-03-14 15:00:30","key":"车辆进场时间"},{"value":"粤ajb777","key":"车    牌"},{"value":"","key":"车辆停车位置"}]
     */

    private String tradeState;
    private String orderId;
    private String tradeMsg;
    private String payTime;
    private String nextPayMsg;
    private String nextPayTime;
    private String nowDate;
    private String nextChargeTime;
    private String carInTime;
    private String startAd; //是否要显示广告。0显示1不显示
    private ShareInfo netShare;
    /**
     * value :
     * key : 车辆已停时长
     */

    private List<Info> info;

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setTradeMsg(String tradeMsg) {
        this.tradeMsg = tradeMsg;
    }

    public void setInfo(List<Info> info) {
        this.info = info;
    }

    public String getTradeState() {
        return tradeState;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTradeMsg() {
        return tradeMsg;
    }

    public List<Info> getInfo() {
        return info;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getNextPayMsg() {
        return nextPayMsg;
    }

    public void setNextPayMsg(String nextPayMsg) {
        this.nextPayMsg = nextPayMsg;
    }

    public String getNextPayTime() {
        return nextPayTime;
    }

    public void setNextPayTime(String nextPayTime) {
        this.nextPayTime = nextPayTime;
    }

    public String getNowDate() {
        return nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }

    public String getNextChargeTime() {
        return nextChargeTime;
    }

    public void setNextChargeTime(String nextChargeTime) {
        this.nextChargeTime = nextChargeTime;
    }

    public String getCarInTime() {
        return carInTime;
    }

    public void setCarInTime(String carInTime) {
        this.carInTime = carInTime;
    }

    public String getStartAd() {
        return startAd;
    }

    public void setStartAd(String startAd) {
        this.startAd = startAd;
    }

    public ShareInfo getNetShare() {
        return netShare;
    }

    public void setNetShare(ShareInfo netShare) {
        this.netShare = netShare;
    }

    @Override
    public String toString() {
        return "PaySuccess{" +
                "tradeState='" + tradeState + '\'' +
                ", orderId='" + orderId + '\'' +
                ", tradeMsg='" + tradeMsg + '\'' +
                ", payTime='" + payTime + '\'' +
                ", nextPayMsg='" + nextPayMsg + '\'' +
                ", nextPayTime='" + nextPayTime + '\'' +
                ", nowDate='" + nowDate + '\'' +
                ", nextChargeTime='" + nextChargeTime + '\'' +
                ", carInTime='" + carInTime + '\'' +
                ", startAd='" + startAd + '\'' +
                ", netShare=" + netShare +
                ", info=" + info +
                '}';
    }
}
