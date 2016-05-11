package com.ajb.merchants.model;

import android.widget.ImageView;

import java.util.List;

/**
 * Created by chenming on 2016/5/11.商户账户信息
 */
public class MerchantsAccoutInfo {

    /**
     * 账户名称
     */
    private String ma_accoutname;
    /**
     * 账户所在商场位置
     */
    private  String ma_address;

    /**
     * 账户备注
     */
    private String ma_remark;


    /**
     * 账户权限列表
     */
    private List<String>  ma_listCompetence;


    /**
     * 账户图像
     */
    private ImageView ma_image;

    public String getMa_accoutname() {
        return ma_accoutname;
    }

    public void setMa_accoutname(String ma_accoutname) {
        this.ma_accoutname = ma_accoutname;
    }

    public String getMa_address() {
        return ma_address;
    }

    public void setMa_address(String ma_address) {
        this.ma_address = ma_address;
    }

    public String getMa_remark() {
        return ma_remark;
    }

    public void setMa_remark(String ma_remark) {
        this.ma_remark = ma_remark;
    }

    public List<String> getMa_listCompetence() {
        return ma_listCompetence;
    }

    public void setMa_listCompetence(List<String> ma_listCompetence) {
        this.ma_listCompetence = ma_listCompetence;
    }

    public ImageView getMa_image() {
        return ma_image;
    }

    public void setMa_image(ImageView ma_image) {
        this.ma_image = ma_image;
    }
}
