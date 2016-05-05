package com.ajb.merchants.model;

import java.io.Serializable;

public class AdInfo implements Serializable {

    private String link;
    private String name;
    private int developStatus;
    private String productName;
    private String url;
    private int type;//0为网络数据1为本地资源
    private int res;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDevelopStatus() {
        return developStatus;
    }

    public void setDevelopStatus(int developStatus) {
        this.developStatus = developStatus;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AdInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AdInfo(String link, String name, int developStatus, String productName, String url) {
        super();
        this.link = link;
        this.name = name;
        this.developStatus = developStatus;
        this.productName = productName;
        this.url = url;
        this.type = 0;
    }

    public AdInfo(String link, String name, int developStatus, String productName, int res) {
        super();
        this.link = link;
        this.name = name;
        this.developStatus = developStatus;
        this.productName = productName;
        this.type = 1;
        this.res = res;
    }


}
