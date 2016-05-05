package com.ajb.merchants.model;

import android.text.TextUtils;

import java.io.Serializable;

public class Coupon implements Serializable {

    String id;// 优惠券列表的主键
    String name;//
    String type;// 优惠券的类型优惠券类型：1代表金额优惠券；2代表时间优惠券;3代表微信活动折扣优惠券
    String startTime;// 优惠券使用开始时间
    String endTime;// 优惠券使用结束时间
    String distributeTime;// 优惠券派发时间
    String value;// 优惠的金额，单位元 对应type为1金额优惠券时使用
    String unit;// 单位
    String parkingName;
    String ltdCode;
    String parkCode;
    boolean isSelected;
    String color;
    String deadline;//提示七天内有效
    String company;//哪个公司赠送


    public Coupon() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Coupon(String id, String name, String type, String startTime,
                  String endTime, String distributeTime, String value, String unit,
                  String parkingName, String ltdCode, String parkCode,
                  boolean isSelected, String color) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distributeTime = distributeTime;
        this.value = value;
        this.unit = unit;
        this.parkingName = parkingName;
        this.ltdCode = ltdCode;
        this.parkCode = parkCode;
        this.isSelected = isSelected;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDistributeTime() {
        return distributeTime;
    }

    public void setDistributeTime(String distributeTime) {
        this.distributeTime = distributeTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getColor() {
        if (TextUtils.isEmpty(color)) {
            return "#26C183";
        }
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Coupon [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", endTime=");
        builder.append(endTime);
        builder.append(", distributeTime=");
        builder.append(distributeTime);
        builder.append(", value=");
        builder.append(value);
        builder.append(", unit=");
        builder.append(unit);
        builder.append(", parkingName=");
        builder.append(parkingName);
        builder.append(", ltdCode=");
        builder.append(ltdCode);
        builder.append(", parkCode=");
        builder.append(parkCode);
        builder.append(", isSelected=");
        builder.append(isSelected);
        builder.append(", color=");
        builder.append(color);
        builder.append("]");
        return builder.toString();
    }

}
