package com.ajb.merchants.model.filter;

import java.io.Serializable;

public class ConditionValue implements Serializable {
    private String dataCode;//序号
    private String dataValue;//隐含值 kp987777
    private String dataName;//显示值
    private boolean check;

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}