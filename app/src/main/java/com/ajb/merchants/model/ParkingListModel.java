package com.ajb.merchants.model;

import java.util.List;

public class ParkingListModel extends Base{
	private List<ParkingInfo> data;

	public List<ParkingInfo> getData() {
		return data;
	}

	public void setData(List<ParkingInfo> data) {
		this.data = data;
	}

}
