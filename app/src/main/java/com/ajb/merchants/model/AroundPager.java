package com.ajb.merchants.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/1/15 16:00
 */
public class AroundPager<T> extends Pager<T> {

    public String colorUrl;
    @SerializedName("around")
    public List<T> list;

}
