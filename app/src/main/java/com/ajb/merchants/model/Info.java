package com.ajb.merchants.model;

import java.io.Serializable;

public class Info implements Serializable {
    private String value;
    private String key;

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Info{" +
                "value='" + value + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}