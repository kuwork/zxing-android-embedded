package com.ajb.merchants.model;

import java.util.List;

public class Carport {
	private List<CarportInfo> carportList;
	private String imageUrl;
	private String address;
	private String carportKeepCount;
	private String telephone;
	public List<CarportInfo> getCarportList() {
		return carportList;
	}
	public void setCarportList(List<CarportInfo> carportList) {
		this.carportList = carportList;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCarportKeepCount() {
		return carportKeepCount;
	}
	public void setCarportKeepCount(String carportKeepCount) {
		this.carportKeepCount = carportKeepCount;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	

}
