package com.ajb.merchants.model;

/**
 * 车场信息
 * @author chenming
 *
 */
public class CarParkModel {

	/**
	 * 车场编号
	 */
	private  String c_parkCodeString;
	
	/**
	 * 企业编号
	 */
	private  String c_itCodeString;

	/**
	 * 车场名称
	 */
	private String c_nameString;
	
	/**
	 * 车场会员车位数
	 */
	private String c_memberNumString;
	/**
	 * 车场车位数
	 */
	
	private String c_carNumString;
	
	/**
	 * 车场免费时间
	 */
	
	private String c_freeTimeString;
	
	/**
	 * 车场收费规则
	 */
	private String c_chargeString;
	
	/**
	 * 经度
	 */
	private String c_longitudeString;
	
	/**
	 *  纬度
	 */
	private  String c_latitudeString;
	
	/**
	 * 车场地址
	 */
	private String c_addressString;
	
	/**
	 * 车场电话
	 */
	private String c_carphoneString;
	
	/**
	 * 车场图片
	 */
	private String c_parkPicString;
	
	public String getC_parkPicString() {
		return c_parkPicString;
	}

	public void setC_parkPicString(String c_parkPicString) {
		this.c_parkPicString = c_parkPicString;
	}

	public String getC_parkCodeString() {
		return c_parkCodeString;
	}

	public void setC_parkCodeString(String c_parkCodeString) {
		this.c_parkCodeString = c_parkCodeString;
	}

	public String getC_itCodeString() {
		return c_itCodeString;
	}

	public void setC_itCodeString(String c_itCodeString) {
		this.c_itCodeString = c_itCodeString;
	}

	public String getC_nameString() {
		return c_nameString;
	}

	public void setC_nameString(String c_nameString) {
		this.c_nameString = c_nameString;
	}

	public String getC_memberNumString() {
		return c_memberNumString;
	}

	public void setC_memberNumString(String c_memberNumString) {
		this.c_memberNumString = c_memberNumString;
	}

	public String getC_carNumString() {
		return c_carNumString;
	}

	public void setC_carNumString(String c_carNumString) {
		this.c_carNumString = c_carNumString;
	}

	public String getC_freeTimeString() {
		return c_freeTimeString;
	}

	public void setC_freeTimeString(String c_freeTimeString) {
		this.c_freeTimeString = c_freeTimeString;
	}

	public String getC_chargeString() {
		return c_chargeString;
	}

	public void setC_chargeString(String c_chargeString) {
		this.c_chargeString = c_chargeString;
	}

	public String getC_longitudeString() {
		return c_longitudeString;
	}

	public void setC_longitudeString(String c_longitudeString) {
		this.c_longitudeString = c_longitudeString;
	}

	public String getC_latitudeString() {
		return c_latitudeString;
	}

	public void setC_latitudeString(String c_latitudeString) {
		this.c_latitudeString = c_latitudeString;
	}

	public String getC_addressString() {
		return c_addressString;
	}

	public void setC_addressString(String c_addressString) {
		this.c_addressString = c_addressString;
	}

	public String getC_carphoneString() {
		return c_carphoneString;
	}

	public void setC_carphoneString(String c_carphoneString) {
		this.c_carphoneString = c_carphoneString;
	}

	@Override
	public String toString() {
		return "CarParkModel [c_parkCodeString=" + c_parkCodeString
				+ ", c_itCodeString=" + c_itCodeString + ", c_nameString="
				+ c_nameString + ", c_memberNumString=" + c_memberNumString
				+ ", c_carNumString=" + c_carNumString + ", c_freeTimeString="
				+ c_freeTimeString + ", c_chargeString=" + c_chargeString
				+ ", c_longitudeString=" + c_longitudeString
				+ ", c_latitudeString=" + c_latitudeString
				+ ", c_addressString=" + c_addressString
				+ ", c_carphoneString=" + c_carphoneString + "]";
	}

	public CarParkModel() {
		super();
		// TODO Auto-generated constructor stub
	}


	public CarParkModel(String c_parkCodeString, String c_itCodeString,
			String c_nameString, String c_memberNumString,
			String c_carNumString, String c_freeTimeString,
			String c_chargeString, String c_longitudeString,
			String c_latitudeString, String c_addressString,
			String c_carphoneString, String c_parkPicString) {
		super();
		this.c_parkCodeString = c_parkCodeString;
		this.c_itCodeString = c_itCodeString;
		this.c_nameString = c_nameString;
		this.c_memberNumString = c_memberNumString;
		this.c_carNumString = c_carNumString;
		this.c_freeTimeString = c_freeTimeString;
		this.c_chargeString = c_chargeString;
		this.c_longitudeString = c_longitudeString;
		this.c_latitudeString = c_latitudeString;
		this.c_addressString = c_addressString;
		this.c_carphoneString = c_carphoneString;
		this.c_parkPicString = c_parkPicString;
	}
	

}
