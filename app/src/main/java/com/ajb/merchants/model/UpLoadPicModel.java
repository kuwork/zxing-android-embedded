package com.ajb.merchants.model;

public class UpLoadPicModel {
	/**
	 * 秘钥
	 */
	private String openKey;
	/**
	 * 卡片ID
	 */
	private String cardId;
	/**
	 * 机器码
	 */
	private String macCode;
	/**
	 *企业号 
	 */
	private String ltdCode;
	/**
	 * 停车场代号
	 */
	private String parkCode;
	/**
	 * 进场还是离场
	 */
	private String status;
	/**
	 * 当前终端请求时间，格式是 yyyy-MM-dd HH:mm:ss
	 */
	private String actionTime;
	/**
	 * 车牌号
	 */
	private String carNo;
	/**
	 * 刷卡时间（优先使用手持机进出接口返回的accTime）
	 */
	private String accTime;
	/**
	 * 图片路径
	 */
	private String file;
	
	public String getOpenKey() {
		return openKey;
	}
	public void setOpenKey(String openKey) {
		this.openKey = openKey;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getMacCode() {
		return macCode;
	}
	public void setMacCode(String macCode) {
		this.macCode = macCode;
	}
	public String getLtdCode() {
		return ltdCode;
	}
	public void setLtdCode(String ltdCode) {
		this.ltdCode = ltdCode;
	}
	public String getParkCode() {
		return parkCode;
	}
	public void setParkCode(String parkCode) {
		this.parkCode = parkCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getActionTime() {
		return actionTime;
	}
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
	public String getCarNo() {
		return carNo;
	}
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getAccTime() {
		return accTime;
	}
	public void setAccTime(String accTime) {
		this.accTime = accTime;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public UpLoadPicModel(String macCode, String ltdCode, String parkCode,
			String status, String actionTime, String carNo, String accTime,
			String file) {
		super();
		this.macCode = macCode;
		this.ltdCode = ltdCode;
		this.parkCode = parkCode;
		this.status = status;
		this.actionTime = actionTime;
		this.carNo = carNo;
		this.accTime = accTime;
		this.file = file;
	}
	@Override
	public String toString() {
		return "UpLoadPicModel [macCode=" + macCode + ", ltdCode=" + ltdCode
				+ ", parkCode=" + parkCode + ", status=" + status
				+ ", actionTime=" + actionTime + ", carNo=" + carNo
				+ ", accTime=" + accTime + ", file=" + file + "]";
	}
	public UpLoadPicModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
