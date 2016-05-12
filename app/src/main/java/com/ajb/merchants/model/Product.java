package com.ajb.merchants.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by jerry on 16/5/3.
 */
public class Product implements Serializable {
    String value;// 值
    String unit;// 单位

    public Product(String value, String unit) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        if (TextUtils.isEmpty(value)) {
            return "0.0";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(value)) {
            sb.append(value);
        }
        if (!TextUtils.isEmpty(unit)) {
            sb.append(unit);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (value != null ? !value.equals(product.value) : product.value != null) return false;
        return unit != null ? unit.equals(product.unit) : product.unit == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
