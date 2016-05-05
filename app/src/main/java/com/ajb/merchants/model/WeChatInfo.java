package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/3/10 16:00
 */
public class WeChatInfo implements Serializable {

    /**
     * partnerid : 1230274101
     * totalFee : 0.3
     * appId : wx494442457975748a
     * timeStamp : 1450061393
     * prepayid : wx20151214104832cffa26dec10178977130
     * nonceStr : 62e7f2e090fe150ef8deb4466fdc81b3
     * appSign : 09346847FC35FC0C982C12A351FD30EB
     * appKey : 5799fba4d7c2df619ef75cd3181a4550
     */

    private String partnerid;
    private String totalFee;
    private String appId;
    private String timeStamp;
    private String prepayid;
    private String nonceStr;
    private String appSign;
    private String appKey;

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public String getAppId() {
        return appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getAppSign() {
        return appSign;
    }

    public String getAppKey() {
        return appKey;
    }
}
