package com.ajb.merchants.model;

import android.view.View;
import android.widget.ImageView;

public class HomeImage {
	private View view;
	private ImageView image;
	private String url;
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public ImageView getImage() {
		return image;
	}
	public void setImage(ImageView image) {
		this.image = image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
