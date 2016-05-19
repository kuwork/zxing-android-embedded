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

    private String menuId;
    private String isOpen;
    private String isEdit;
    private String name;
    private String id;

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
}
