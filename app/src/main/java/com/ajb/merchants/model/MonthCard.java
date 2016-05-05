package com.ajb.merchants.model;

import java.io.Serializable;

public class MonthCard implements Serializable {
	private String carNo;
	private MonthCardInfo monthCardInfo;
	private String isMonthCard;
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	
	public String getIsMonthCard() {
		return isMonthCard;
	}
	public void setIsMonthCard(String isMonthCard) {
		this.isMonthCard = isMonthCard;
	}
	public MonthCardInfo getMonthCardInfo() {
		return monthCardInfo;
	}
	public void setMonthCardInfo(MonthCardInfo monthCardInfo) {
		this.monthCardInfo = monthCardInfo;
	}
	
	

}
