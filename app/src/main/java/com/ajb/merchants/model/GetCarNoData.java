package com.ajb.merchants.model;

import java.util.List;

/**
 * Created by jerry on 15/11/4.
 */
public class GetCarNoData {
    public List<CarModel> list;

    public GetCarNoData() {
    }

    public List<CarModel> getList() {
        return list;
    }

    public void setList(List<CarModel> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GetCarNoData{" +
                "list=" + list +
                '}';
    }
}
