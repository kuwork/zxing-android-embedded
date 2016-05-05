package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 16/1/23.
 */
public class HomePageInfo implements Serializable{
    String name;
    String className;

    public HomePageInfo(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "HomePageInfo{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
