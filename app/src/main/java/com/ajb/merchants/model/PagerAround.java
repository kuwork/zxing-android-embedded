package com.ajb.merchants.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PagerAround<T> extends Pager {
    public String colorUrl;
    @SerializedName("around")
    public List<T> list;
}
