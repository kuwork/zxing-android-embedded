package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * @author chenming
 *         基础实体
 */
public class BalanceLimitInfo extends Product implements Serializable {

    private String desc;
    private int serial;

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public BalanceLimitInfo(String value, String unit, String desc) {
        super(value, unit);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BalanceLimitInfo{" +
                "desc='" + desc + '\'' +
                "} " + super.toString();
    }
}
