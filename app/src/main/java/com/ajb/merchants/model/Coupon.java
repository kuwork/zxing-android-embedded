package com.ajb.merchants.model;

import java.io.Serializable;

public class Coupon extends Product implements Serializable {

    String couponType;//优惠券的分类方向，1为金额券2为时间券
    String typeName;//种类的名称，如2元券、3小时
    String nums;//指该种类下所含余额的数量，以张为单位
    String desc;//单条描述信息

    public Coupon(String value, String unit, String couponType, String desc, String nums, String typeName) {
        super(value, unit);
        this.couponType = couponType;
        this.desc = desc;
        this.nums = nums;
        this.typeName = typeName;
    }

    public Coupon(String value, String unit) {
        super(value, unit);
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
