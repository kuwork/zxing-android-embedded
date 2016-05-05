package com.ajb.merchants.model;

public class AddShareModle 
{
	private String Id;
	private String parkingName;
	private String parkingPrice;
	private String ltd_code;
	private String park_code;
	public String getId() {
		return Id;
	}
	public void setId(String parkingId) {
		this.Id = parkingId;
	}
	public String getParkingName() {
		return parkingName;
	}
	public void setParkingName(String parkingName) {
		this.parkingName = parkingName;
	}
	public String getParkingPrice() {
		return parkingPrice;
	}
	public void setParkingPrice(String parkingPrice) {
		this.parkingPrice = parkingPrice;
	}
	public String getLtd_code() {
		return ltd_code;
	}
	public void setLtd_code(String ltd_code) {
		this.ltd_code = ltd_code;
	}
	public String getPark_code() {
		return park_code;
	}
	public void setPark_code(String park_code) {
		this.park_code = park_code;
	}
	@Override
	public String toString() {
		return "AddShareModle [Id=" + Id + ", parkingName=" + parkingName
				+ ", parkingPrice=" + parkingPrice + ", ltd_code=" + ltd_code
				+ ", park_code=" + park_code + "]";
	}
	
	
}
