package com.ajb.merchants.model;

public class MonthCardInfo {
	private String id;
	private String charge;
	private String parkName;
	private String startDateString;
	private String endDateString;
	private String ltdCode;
	private String parkCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getStartDateString() {
		return startDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
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

	@Override
	public String toString() {
		return "MonthCardInfo [id=" + id + ", charge=" + charge + ", parkName="
				+ parkName + ", startDateString=" + startDateString
				+ ", endDateString=" + endDateString + ", ltdCode=" + ltdCode
				+ ", parkCode=" + parkCode + "]";
	}

	public MonthCardInfo(String id, String charge, String parkName,
			String startDateString, String endDateString, String ltdCode,
			String parkCode) {
		super();
		this.id = id;
		this.charge = charge;
		this.parkName = parkName;
		this.startDateString = startDateString;
		this.endDateString = endDateString;
		this.ltdCode = ltdCode;
		this.parkCode = parkCode;
	}

}
