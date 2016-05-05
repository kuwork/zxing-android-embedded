package com.ajb.merchants.model;

import java.io.Serializable;

public class ProvinceVersion implements Serializable {
	String md5;
	String url;

	public ProvinceVersion(String md5, String url) {
		super();
		this.md5 = md5;
		this.url = url;
	}

	public ProvinceVersion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
