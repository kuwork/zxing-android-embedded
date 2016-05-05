package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 16/1/18.
 */
public class AccountInfo implements Serializable {

    /**
     * invite : 00000000
     * isSign : 1
     * balance : 0
     * totalIncome : 0.0
     * isRenew : 1
     * integral : 100
     */

    private String invite;
    private String isSign;
    private String balance;
    private String totalIncome;
    private String isRenew;
    private String integral;

    public AccountInfo() {
    }

    public AccountInfo(String invite, String isSign, String balance, String totalIncome, String isRenew, String integral) {
        this.invite = invite;
        this.isSign = isSign;
        this.balance = balance;
        this.totalIncome = totalIncome;
        this.isRenew = isRenew;
        this.integral = integral;
    }

    public void setInvite(String invite) {
        this.invite = invite;
    }

    public void setIsSign(String isSign) {
        this.isSign = isSign;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setIsRenew(String isRenew) {
        this.isRenew = isRenew;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getInvite() {
        return invite;
    }

    public String getIsSign() {
        return isSign;
    }

    public String getBalance() {
        return balance;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public String getIsRenew() {
        return isRenew;
    }

    public String getIntegral() {
        return integral;
    }
}
