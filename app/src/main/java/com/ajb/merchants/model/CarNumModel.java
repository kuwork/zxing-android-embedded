package com.ajb.merchants.model;

public class CarNumModel {

	/**
	 * 车牌ID
	 */
	private String c_Id;

	/**
	 * 车牌号
	 */
	private String c_Name;
	/**
	 * 车牌对应的手机号
	 */
	private String c_phone;
	public CarNumModel(String c_Id, String c_Name, String c_phone) {
		super();
		this.c_Id = c_Id;
		this.c_Name = c_Name;
		this.c_phone = c_phone;
	}
	@Override
	public String toString() {
		return "CarNumModel [c_Id=" + c_Id + ", c_Name=" + c_Name
				+ ", c_phone=" + c_phone + "]";
	}
	public String getC_Id() {
		return c_Id;
	}
	public void setC_Id(String c_Id) {
		this.c_Id = c_Id;
	}
	public String getC_Name() {
		return c_Name;
	}
	public void setC_Name(String c_Name) {
		this.c_Name = c_Name;
	}
	public String getC_phone() {
		return c_phone;
	}
	public void setC_phone(String c_phone) {
		this.c_phone = c_phone;
	}
	
	
	
	
}
