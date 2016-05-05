package com.ajb.merchants.model;

import android.text.TextUtils;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CarPark implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String parkName;
    private String carportMemberCount;//会员、优惠剩余
    private String carportUnmemberCount;//普通车位数
    private String longitude;
    private String latitude;
    private String phone;
    private String address;
    private String discount;
    @Id
    @NoAutoIncrement
    private String id;
    private List<TimePeriod> freeTimeList;
    private String parkPrice;
    private String member;//类型
    private String myDistance;//距离
    private String chargeImgUrl;
    private String imageUrl;
    private String carportCount;// 总车位数
    private String carportSurplusCount;// 剩余车位数
    private String onlinePay;// 是否支持在线微信支付
    private String color;
    private String ltdCode;
    private String hourPrice;
    private String parkCode;
    private String unit;//单位
    private List<Map<String, String>> info;

    public CarPark() {
    }

    public CarPark(String parkName, String carportMemberCount, String carportUnmemberCount, String longitude, String latitude, String phone, String address, String discount, String id, List<TimePeriod> freeTimeList, String parkPrice, String member, String myDistance, String chargeImgUrl, String carportCount, String carportSurplusCount, String onlinePay, String color, String ltdCode, String hourPrice, String parkCode, String unit, List<Map<String, String>> info) {
        this.parkName = parkName;
        this.carportMemberCount = carportMemberCount;
        this.carportUnmemberCount = carportUnmemberCount;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.address = address;
        this.discount = discount;
        this.id = id;
        this.freeTimeList = freeTimeList;
        this.parkPrice = parkPrice;
        this.member = member;
        this.myDistance = myDistance;
        this.chargeImgUrl = chargeImgUrl;
        this.carportCount = carportCount;
        this.carportSurplusCount = carportSurplusCount;
        this.onlinePay = onlinePay;
        this.color = color;
        this.ltdCode = ltdCode;
        this.hourPrice = hourPrice;
        this.parkCode = parkCode;
        this.unit = unit;
        this.info = info;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getCarportMemberCount() {
        return carportMemberCount;
    }

    public void setCarportMemberCount(String carportMemberCount) {
        this.carportMemberCount = carportMemberCount;
    }

    public String getCarportUnmemberCount() {
        return carportUnmemberCount;
    }

    public void setCarportUnmemberCount(String carportUnmemberCount) {
        this.carportUnmemberCount = carportUnmemberCount;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TimePeriod> getFreeTimeList() {
        return freeTimeList;
    }

    public void setFreeTimeList(List<TimePeriod> freeTimeList) {
        this.freeTimeList = freeTimeList;
    }

    public String getParkPrice() {
        return parkPrice;
    }

    public void setParkPrice(String parkPrice) {
        this.parkPrice = parkPrice;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMyDistance() {
        return myDistance;
    }

    public void setMyDistance(String myDistance) {
        this.myDistance = myDistance;
    }

    public String getChargeImgUrl() {
        return chargeImgUrl;
    }

    public void setChargeImgUrl(String chargeImgUrl) {
        this.chargeImgUrl = chargeImgUrl;
    }

    public String getCarportCount() {
        return carportCount;
    }

    public void setCarportCount(String carportCount) {
        this.carportCount = carportCount;
    }

    public String getCarportSurplusCount() {
        return carportSurplusCount;
    }

    public void setCarportSurplusCount(String carportSurplusCount) {
        this.carportSurplusCount = carportSurplusCount;
    }

    public String getOnlinePay() {
        return onlinePay;
    }

    public void setOnlinePay(String onlinePay) {
        this.onlinePay = onlinePay;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLtdCode() {
        return ltdCode;
    }

    public void setLtdCode(String ltdCode) {
        this.ltdCode = ltdCode;
    }

    public String getHourPrice() {
        return hourPrice;
    }

    public void setHourPrice(String hourPrice) {
        this.hourPrice = hourPrice;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getUnit() {
        if (TextUtils.isEmpty(unit)) {
            return "元";
        }
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<Map<String, String>> getInfo() {
        return info;
    }

    public void setInfo(List<Map<String, String>> info) {
        this.info = info;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "CarPark{" +
                "parkName='" + parkName + '\'' +
                ", carportMemberCount='" + carportMemberCount + '\'' +
                ", carportUnmemberCount='" + carportUnmemberCount + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", discount='" + discount + '\'' +
                ", id='" + id + '\'' +
                ", freeTimeList=" + freeTimeList +
                ", parkPrice='" + parkPrice + '\'' +
                ", member='" + member + '\'' +
                ", myDistance='" + myDistance + '\'' +
                ", chargeImgUrl='" + chargeImgUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", carportCount='" + carportCount + '\'' +
                ", carportSurplusCount='" + carportSurplusCount + '\'' +
                ", onlinePay='" + onlinePay + '\'' +
                ", color='" + color + '\'' +
                ", ltdCode='" + ltdCode + '\'' +
                ", hourPrice='" + hourPrice + '\'' +
                ", parkCode='" + parkCode + '\'' +
                ", unit='" + unit + '\'' +
                ", info=" + info +
                '}';
    }
}
