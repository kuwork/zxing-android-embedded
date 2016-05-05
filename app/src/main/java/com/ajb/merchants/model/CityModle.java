package com.ajb.merchants.model;

public class CityModle 
{
	private String parentId;
	private String cityname;
	private String cityCode;
	public String getParentId()
	{
		return parentId;
	}
	public void setParentId(String parentId) 
	{
		this.parentId = parentId;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) 
	{
		this.cityname = cityname;
	}
	public String getCityCode() 
	{
		return cityCode;
	}
	public void setCityCode(String cityCode) 
	{
		this.cityCode = cityCode;
	}	
}
