package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 15/11/4.
 */
public class CarModel implements Serializable {
    private String carNo;//车牌号

    public CarModel(String carNo) {
        this.carNo = carNo;
    }

    public CarModel() {
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    @Override
    public String toString() {
        return "CarModel{" +
                "carNo='" + carNo + '\'' +
                "} " + super.toString();
    }
}
