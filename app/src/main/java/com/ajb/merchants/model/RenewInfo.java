package com.ajb.merchants.model;

import java.io.Serializable;

public class RenewInfo implements Serializable {
	String isSign;
	Double balance;
	String renewGrowthValue;
	int level;
	Double totalIncome;
	String renewLevel;
	String carprotNo;
	String carNoList;
	int isRenew;
	String vaildDate;
	Double integral;

	public RenewInfo() {
		super();
	}

	public RenewInfo(String isSign, Double balance, String renewGrowthValue,
			int level, Double totalIncome, String renewLevel, String carprotNo,
			String carNoList, int isRenew, String vaildDate, Double integral) {
		super();
		this.isSign = isSign;
		this.balance = balance;
		this.renewGrowthValue = renewGrowthValue;
		this.level = level;
		this.totalIncome = totalIncome;
		this.renewLevel = renewLevel;
		this.carprotNo = carprotNo;
		this.carNoList = carNoList;
		this.isRenew = isRenew;
		this.vaildDate = vaildDate;
		this.integral = integral;
	}

	public String getIsSign() {
		return isSign;
	}

	public void setIsSign(String isSign) {
		this.isSign = isSign;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getRenewGrowthValue() {
		return renewGrowthValue;
	}

	public void setRenewGrowthValue(String renewGrowthValue) {
		this.renewGrowthValue = renewGrowthValue;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Double getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}

	public String getRenewLevel() {
		return renewLevel;
	}

	public void setRenewLevel(String renewLevel) {
		this.renewLevel = renewLevel;
	}

	public String getCarprotNo() {
		return carprotNo;
	}

	public void setCarprotNo(String carprotNo) {
		this.carprotNo = carprotNo;
	}

	public String getCarNoList() {
		return carNoList;
	}

	public void setCarNoList(String carNoList) {
		this.carNoList = carNoList;
	}

	public int getIsRenew() {
		return isRenew;
	}

	public void setIsRenew(int isRenew) {
		this.isRenew = isRenew;
	}

	public String getVaildDate() {
		return vaildDate;
	}

	public void setVaildDate(String vaildDate) {
		this.vaildDate = vaildDate;
	}

	public Double getIntegral() {
		return integral;
	}

	public void setIntegral(Double integral) {
		this.integral = integral;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RenewInfo [isSign=");
		builder.append(isSign);
		builder.append(", balance=");
		builder.append(balance);
		builder.append(", renewGrowthValue=");
		builder.append(renewGrowthValue);
		builder.append(", level=");
		builder.append(level);
		builder.append(", totalIncome=");
		builder.append(totalIncome);
		builder.append(", renewLevel=");
		builder.append(renewLevel);
		builder.append(", carprotNo=");
		builder.append(carprotNo);
		builder.append(", carNoList=");
		builder.append(carNoList);
		builder.append(", isRenew=");
		builder.append(isRenew);
		builder.append(", vaildDate=");
		builder.append(vaildDate);
		builder.append(", integral=");
		builder.append(integral);
		builder.append("]");
		return builder.toString();
	}

}
