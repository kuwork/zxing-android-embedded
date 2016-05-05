package com.ajb.merchants.model;

import java.util.List;

public class MonthCardEx extends MonthCard {
	private List<MonthCardInfo> carNoList;

	public List<MonthCardInfo> getCarNoList() {
		return carNoList;
	}

	public void setCarNoList(List<MonthCardInfo> carNoList) {
		this.carNoList = carNoList;
	}

}
