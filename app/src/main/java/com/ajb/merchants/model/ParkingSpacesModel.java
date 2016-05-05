package com.ajb.merchants.model;

/**
 * 车位信息
 * @author chenming
 *
 */
public class ParkingSpacesModel {

	/**
	 * 车位号
	 */
	private String carNum;
	
	/**
	 * 车位分享在那个场
	 */
	private String carParkString;
	
	/**
	 * 车位所对车场的编号
	 */
	private  String carParkNumString;
	
	/**
	 * 车位的有效期
	 */
	private  String carValidity;

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getCarParkString() {
		return carParkString;
	}

	public void setCarParkString(String carParkString) {
		this.carParkString = carParkString;
	}

	public String getCarParkNumString() {
		return carParkNumString;
	}

	public void setCarParkNumString(String carParkNumString) {
		this.carParkNumString = carParkNumString;
	}

	public String getCarValidity() {
		return carValidity;
	}

	public void setCarValidity(String carValidity) {
		this.carValidity = carValidity;
	}
	
	
	
	
	
	

}
