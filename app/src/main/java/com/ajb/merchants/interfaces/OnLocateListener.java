package com.ajb.merchants.interfaces;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;

/**
 * Created by jerry on 15/10/27.
 */
public interface OnLocateListener {
    //获取定位结果
    public void onLocate(BDLocation location);

    //获取定位结果
    public MyLocationData getLocation();

    //启动定位

}
