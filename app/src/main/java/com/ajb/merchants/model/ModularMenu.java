package com.ajb.merchants.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/1/5 16:06
 */
public class ModularMenu implements Serializable {

    public final static String CODE_LEFTMENU = "190000";
    public final static String CODE_SETTING = "180000";
    public final static String CODE_ACCOUNT = "170000";
    public final static String CODE_MENU = "1000";
    public final static String CODE_COUPON = "100001";
    public final static String CODE_MAIN_MENU = "100002";
    /**
     * modularId : 1
     * modularCode : 180000
     * modularEcode : advertisingCode
     * modularValue : 左侧菜单
     * modularUrl :
     * pageCode : HomePage
     * menuList : [{"menuCode":"190001","menuValue":"我的车辆","operateType":"NATIVE","title":"我的车场","type":"NORMAL","linkUrl":"http://www.baidu.com","menuPictureUrl":"http://120.25.164.74:28560/yuesaoClient/resources/ico/yuesaoLevel/sx.png"}]
     */

    private String modularId;
    private String modularCode;
    private String modularEcode;
    private String modularValue;
    private String modularUrl;
    private String pageCode;
    /**
     * menuCode : 190001
     * menuValue : 我的车辆
     * operateType : NATIVE
     * title : 我的车场
     * type : NORMAL
     * linkUrl : http://www.baidu.com
     * menuPictureUrl : http://120.25.164.74:28560/yuesaoClient/resources/ico/yuesaoLevel/sx.png
     */

    private List<MenuInfo> menuList;

    public ModularMenu() {
    }

    public ModularMenu(String modularId, String modularCode, String modularEcode, String modularValue, String modularUrl, String pageCode, List<MenuInfo> menuList) {
        this.modularId = modularId;
        this.modularCode = modularCode;
        this.modularEcode = modularEcode;
        this.modularValue = modularValue;
        this.modularUrl = modularUrl;
        this.pageCode = pageCode;
        this.menuList = menuList;
    }

    public void setModularId(String modularId) {
        this.modularId = modularId;
    }

    public void setModularCode(String modularCode) {
        this.modularCode = modularCode;
    }

    public void setModularEcode(String modularEcode) {
        this.modularEcode = modularEcode;
    }

    public void setModularValue(String modularValue) {
        this.modularValue = modularValue;
    }

    public void setModularUrl(String modularUrl) {
        this.modularUrl = modularUrl;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public void setMenuList(List<MenuInfo> menuList) {
        this.menuList = menuList;
    }

    public String getModularId() {
        return modularId;
    }

    public String getModularCode() {
        return modularCode;
    }

    public String getModularEcode() {
        return modularEcode;
    }

    public String getModularValue() {
        return modularValue;
    }

    public String getModularUrl() {
        return modularUrl;
    }

    public String getPageCode() {
        return pageCode;
    }

    public List<MenuInfo> getMenuList() {
        return menuList;
    }

    @Override
    public String toString() {
        return "ModularMenu{" +
                "modularId='" + modularId + '\'' +
                ", modularCode='" + modularCode + '\'' +
                ", modularEcode='" + modularEcode + '\'' +
                ", modularValue='" + modularValue + '\'' +
                ", modularUrl='" + modularUrl + '\'' +
                ", pageCode='" + pageCode + '\'' +
                ", menuList=" + menuList +
                '}';
    }
}
