package com.ajb.merchants.interfaces;

import android.view.View;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/1/8 14:15
 */
public interface OnViewErrorListener {

    public void showErrorPage(View v, int strId, int imgId);

    public void showErrorPage(View v, String tip, int imgId);

    public void onErrorPageClick();

}
