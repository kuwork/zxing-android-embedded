package com.ajb.merchants.model;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

public class ProvinceInfo implements Serializable {
	@Id
	@NoAutoIncrement
	String districtCode;
	String districtName;
	String cityCode;
	String cityName;
	String provinceName;
	String districtPinyin;
	String cityPinyin;

	public ProvinceInfo(String districtCode, String districtName,
			String cityCode, String cityName, String provinceName) {
		super();
		this.districtCode = districtCode;
		this.districtName = districtName;
		this.cityCode = cityCode;
		this.cityName = cityName;
		this.provinceName = provinceName;
	}

	public ProvinceInfo() {
		super();
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getDistrictPinyin() {
		return districtPinyin;
	}

	public void setDistrictPinyin(String districtPinyin) {
		this.districtPinyin = districtPinyin;
	}

	public String getCityPinyin() {
		return cityPinyin;
	}

	public void setCityPinyin(String cityPinyin) {
		this.cityPinyin = cityPinyin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProvinceInfo [districtCode=");
		builder.append(districtCode);
		builder.append(", districtName=");
		builder.append(districtName);
		builder.append(", cityCode=");
		builder.append(cityCode);
		builder.append(", cityName=");
		builder.append(cityName);
		builder.append(", provinceName=");
		builder.append(provinceName);
		builder.append("]");
		return builder.toString();
	}

}
