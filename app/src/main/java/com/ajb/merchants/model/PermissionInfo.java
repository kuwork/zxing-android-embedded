package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * @Description: 权限实体类
 * @author: Yuven
 * @date: 2016/5/19 14:26
 */
public class PermissionInfo implements Serializable {

    /**
     * menuId : 200022
     * isOpen : 1
     * isEdit : 1
     * name : 购买记录
     * id : 10
     */

    private String id;
    private String name;    //权限名称
    private String menuId;  //菜单id
    private String isOpen;  //是否打开该权限;1-打开;0-关闭
    private  String isEdit;  //是否允许编辑;1-允许;0-不允许;默认允许
    private  String isVisible;  //是否可见;1-可见;0-不可见;默认可见

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }
}
