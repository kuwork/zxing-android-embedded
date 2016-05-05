package com.ajb.merchants.interfaces;

import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.search.geocode.GeoCodeResult;

/**
 * Created by jerry on 15/10/27.
 */
public interface OnSearchListener {
    public void onSearchBarClick();
    public void onListClick(MyLocationData locData);
    public void onSearchResultShow(GeoCodeResult result);

}
