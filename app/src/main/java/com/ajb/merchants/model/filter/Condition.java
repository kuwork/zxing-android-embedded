package com.ajb.merchants.model.filter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jerry on 16/4/19.
 */
public class Condition implements Serializable {

    /**
     * conditionCode : parkingPrice
     * conditionName : 车位价格
     * orderBy : dataCode
     * groupBy : null
     * conditionListUrl : null
     * conditionList : [{"dataCode":"00","dataValue":"normal","dataName":"不限"},{"dataCode":"01","dataValue":"more","dataName":"价格递增"},{"dataCode":"02","dataValue":"less","dataName":"价格递减"}]
     */

    private String conditionCode;
    private String conditionName;
    private String orderBy;
    private String groupBy;
    private String conditionListUrl;
    /**
     * dataCode : 00
     * dataValue : normal
     * dataName : 不限
     */

    private List<ConditionValue> conditionList;

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getConditionListUrl() {
        return conditionListUrl;
    }

    public void setConditionListUrl(String conditionListUrl) {
        this.conditionListUrl = conditionListUrl;
    }

    public List<ConditionValue> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<ConditionValue> conditionList) {
        this.conditionList = conditionList;
    }


}
