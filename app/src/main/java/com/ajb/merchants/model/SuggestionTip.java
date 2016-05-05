package com.ajb.merchants.model;

import java.io.Serializable;

import com.baidu.mapapi.search.sug.SuggestionResult;

public class SuggestionTip implements Serializable {
	private String city;
	private String district;
	private String key;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SuggestionTip(SuggestionResult.SuggestionInfo info) {
		this.city = info.city;
		this.district = info.district;
		this.key = info.key;
	}

	public SuggestionTip(String city, String district, String key) {
		super();
		this.city = city;
		this.key = key;
	}

	@Override
	public String toString() {
		return key;
	}

}
