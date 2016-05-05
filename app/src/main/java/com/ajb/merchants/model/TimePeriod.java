package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 15/11/5.
 */
public class TimePeriod implements Serializable {
    private String memberFreeStartTime;
    private String memberFreeEndTime;

    public TimePeriod(String memberFreeStartTime, String memberFreeEndTime) {
        this.memberFreeStartTime = memberFreeStartTime;
        this.memberFreeEndTime = memberFreeEndTime;
    }

    public TimePeriod() {

    }

    public String getMemberFreeStartTime() {
        return memberFreeStartTime;
    }

    public void setMemberFreeStartTime(String memberFreeStartTime) {
        this.memberFreeStartTime = memberFreeStartTime;
    }

    public String getMemberFreeEndTime() {
        return memberFreeEndTime;
    }

    public void setMemberFreeEndTime(String memberFreeEndTime) {
        this.memberFreeEndTime = memberFreeEndTime;
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
                "memberFreeStartTime='" + memberFreeStartTime + '\'' +
                ", memberFreeEndTime='" + memberFreeEndTime + '\'' +
                '}';
    }
}
