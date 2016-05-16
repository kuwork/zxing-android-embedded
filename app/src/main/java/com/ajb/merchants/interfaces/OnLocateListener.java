package com.ajb.merchants.interfaces;

import com.baidu.location.BDLocation;

/**
 * Created by jerry on 15/10/27.
 */
public interface OnLocateListener {
    //获取定位结果
    public void onLocate(BDLocation location);

    //启动定位

}
