package com.ajb.merchants.model;

public class CarModelRecord {

	/**
	 * 停车场名称
	 */
	private  String carName;
	/**
	 * 停车场费
	 */
	private String carMoney;
	
	/**
	 * 停车时间
	 */
	private  String carTime;
	/**
	 * 车牌
	 */
	private  String  carNum;
	/**
	 * 订单号 
	 */
	private  String  orderNum;
	
	/**
	 * 进场时间
	 */
	private  String carInTime;
	/**
	 * 出场时间
	 */
	private  String carOutTime;
	/**
	 * 车牌图片
	 */
	private String carPic;
	
	private  String itcode;
	
	private  String parkCode;
	
	public String getCarPic() {
		return carPic;
	}
	public void setCarPic(String carPic) {
		this.carPic = carPic;
	}
	public String getItcode() {
		return itcode;
	}
	public void setItcode(String itcode) {
		this.itcode = itcode;
	}
	public String getParkCode() {
		return parkCode;
	}
	public void setParkCode(String parkCode) {
		this.parkCode = parkCode;
	}
	public String getCarName() {
		return carName;
	}
	public void setCarName(String carName) {
		this.carName = carName;
	}
	public String getCarMoney() {
		return carMoney;
	}
	public void setCarMoney(String carMoney) {
		this.carMoney = carMoney;
	}
	public String getCarTime() {
		return carTime;
	}
	public void setCarTime(String carTime) {
		this.carTime = carTime;
	}
	public String getCarNum() {
		return carNum;
	}
	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getCarInTime() {
		return carInTime;
	}
	public void setCarInTime(String carInTime) {
		this.carInTime = carInTime;
	}
	public String getCarOutTime() {
		return carOutTime;
	}
	public void setCarOutTime(String carOutTime) {
		this.carOutTime = carOutTime;
	}
	public CarModelRecord(String carName, String carMoney, String carTime,
			String carNum, String orderNum, String carInTime, String carOutTime) {
		super();
		this.carName = carName;
		this.carMoney = carMoney;
		this.carTime = carTime;
		this.carNum = carNum;
		this.orderNum = orderNum;
		this.carInTime = carInTime;
		this.carOutTime = carOutTime;
	}
	public CarModelRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	

	
}
