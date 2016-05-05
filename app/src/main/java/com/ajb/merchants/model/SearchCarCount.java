package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:(新)查询计费接口实体类
 * @author: 李庆育
 * @date: 2016/3/9 14:47
 */
public class SearchCarCount implements Serializable {

    /**
     * cardId : 10930861 position : carInTime : 2016-03-01 16:15:18 chargeMsg :
     * 月租卡无需缴费 parkFeeValue :
     * {"hasFee":"0","effectiveFee":"0","oldshouldPayFee":
     * "0","partyMoney":"0","nextChargeMoney"
     * :"0","isMember":"false","allFeeTime"
     * :"0时0分","allParkTime":"190时16分","nextChargeTime"
     * :"","shouldPayFee":"0.0","allFee"
     * :"0.0","chargeEndTime":"","subParkCode":""
     * ,"memberTimeFreeTime":"0","memberTimeFreeValue"
     * :"0","visitTime":"","nextResidueTime"
     * :"","lastPetTime":"","nightAllFee":"0"
     * ,"currFavMoney":"0","currFavTime":"0"
     * ,"overTime":"0","deduction":"","chargingCar"
     * :"通用","freeTime":"0","favMoney":"0","favTime":"0","balance":""} cardType
     * : 月租卡 info :
     * [{"value":"190时16分","key":"车辆已停时长"},{"value":"2016-03-01 16:15:18"
     * ,"key":"车辆进场时间"
     * },{"value":"粤A12345","key":"车    牌"},{"value":"","key":"车辆停车位置"}]
     * nextResidueTime : 0 carNo : 粤A12345 parkName : 安居宝展厅停车场 leaveTime :
     * 2016-03-09 14:31:35 payType : 1 payWay : 121 chargeCode : 4444
     */

    private String cardId;
    private String carSN;
    private String position;
    private String carInTime;
    private String chargeMsg;
    private String nextPayTime; // 多少分钟后费用更新
    private String nextPayMsg; // 多少分钟后费用提示信息
    private String nowDate;// 产生费用的时间

    /**
     * hasFee : 0 effectiveFee : 0 oldshouldPayFee : 0 partyMoney : 0
     * nextChargeMoney : 0 isMember : false allFeeTime : 0时0分 allParkTime :
     * 190时16分 nextChargeTime : shouldPayFee : 0.0 allFee : 0.0 chargeEndTime :
     * subParkCode : memberTimeFreeTime : 0 memberTimeFreeValue : 0 visitTime :
     * nextResidueTime : lastPetTime : nightAllFee : 0 currFavMoney : 0
     * currFavTime : 0 overTime : 0 deduction : chargingCar : 通用 freeTime : 0
     * favMoney : 0 favTime : 0 balance :
     */

    private ParkFeeValue parkFeeValue;
    private String cardType;
    private int nextResidueTime;
    private String carNo;
    private String parkName;
    private String leaveTime;
    private String payType;
    private String payWay;
    private String chargeCode;
    private String ltdCode;
    private String parkCode;
    /**
     * value : 190时16分 key : 车辆已停时长
     */
    private List<Info> info;
    private List<Coupon> couponsList;
    private PaySuccess orderSuccess;

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCarInTime(String carInTime) {
        this.carInTime = carInTime;
    }

    public void setChargeMsg(String chargeMsg) {
        this.chargeMsg = chargeMsg;
    }

    public void setParkFeeValue(ParkFeeValue parkFeeValue) {
        this.parkFeeValue = parkFeeValue;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setNextResidueTime(int nextResidueTime) {
        this.nextResidueTime = nextResidueTime;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public void setInfo(List<Info> info) {
        this.info = info;
    }

    public String getCardId() {
        return cardId;
    }

    public String getPosition() {
        return position;
    }

    public String getCarInTime() {
        return carInTime;
    }

    public String getChargeMsg() {
        return chargeMsg;
    }

    public ParkFeeValue getParkFeeValue() {
        return parkFeeValue;
    }

    public String getCardType() {
        return cardType;
    }

    public int getNextResidueTime() {
        return nextResidueTime;
    }

    public String getCarNo() {
        return carNo;
    }

    public String getParkName() {
        return parkName;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public String getPayType() {
        return payType;
    }

    public String getPayWay() {
        return payWay;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public List<Info> getInfo() {
        return info;
    }

    public List<Coupon> getCouponsList() {
        return couponsList;
    }

    public void setCouponsList(List<Coupon> couponsList) {
        this.couponsList = couponsList;
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

    public String getCarSN() {
        return carSN;
    }

    public void setCarSN(String carSN) {
        this.carSN = carSN;
    }

    public String getNextPayTime() {
        return nextPayTime;
    }

    public void setNextPayTime(String nextPayTime) {
        this.nextPayTime = nextPayTime;
    }

    public String getNextPayMsg() {
        return nextPayMsg;
    }

    public void setNextPayMsg(String nextPayMsg) {
        this.nextPayMsg = nextPayMsg;
    }

    public String getNowDate() {
        return nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }

    public PaySuccess getOrderSuccess() {
        return orderSuccess;
    }

    public void setOrderSuccess(PaySuccess orderSuccess) {
        this.orderSuccess = orderSuccess;
    }

    @Override
    public String toString() {
        return "SearchCarCount [cardId=" + cardId + ", carSN=" + carSN
                + ", position=" + position + ", carInTime=" + carInTime
                + ", chargeMsg=" + chargeMsg + ", nextPayTime=" + nextPayTime
                + ", nextPayMsg=" + nextPayMsg + ", nowDate=" + nowDate
                + ", parkFeeValue=" + parkFeeValue + ", cardType=" + cardType
                + ", nextResidueTime=" + nextResidueTime + ", carNo=" + carNo
                + ", parkName=" + parkName + ", leaveTime=" + leaveTime
                + ", payType=" + payType + ", payWay=" + payWay
                + ", chargeCode=" + chargeCode + ", ltdCode=" + ltdCode
                + ", parkCode=" + parkCode + ", info=" + info
                + ", couponsList=" + couponsList + ", orderSuccess="
                + orderSuccess + "]";
    }

}
