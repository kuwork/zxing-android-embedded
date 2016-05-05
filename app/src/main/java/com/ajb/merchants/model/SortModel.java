package com.ajb.merchants.model;

import com.lidroid.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

public class SortModel {

    private DbModel data;
    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public DbModel getData() {
        return data;
    }

    public void setData(DbModel data) {
        this.data = data;
    }

    public SortModel(String sortLetters, String name) {
        this.name = name;
        this.sortLetters = sortLetters;
    }

    public SortModel(DbModel model) {
        this.sortLetters = model.getString("cityPinyin").substring(0, 1).toUpperCase();
        this.name = model.getString("cityName");
        this.data = model;
    }

    public static List<SortModel> getCityList(List<DbModel> list) {
        ArrayList<SortModel> sortModels = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return sortModels;
        }

        // 在列表上添加热门城市
        SortModel sortModebj = new SortModel("热门", "北京市");
        sortModels.add(sortModebj);
        SortModel sortModelsh = new SortModel("热门", "上海市");
        sortModels.add(sortModelsh);
        SortModel sortModelgz = new SortModel("热门", "广州市");
        sortModels.add(sortModelgz);
        SortModel sortModelcd = new SortModel("热门", "成都市");
        sortModels.add(sortModelcd);
        SortModel sortModelcs = new SortModel("热门", "长沙市");
        sortModels.add(sortModelcs);
        SortModel sortModeltj = new SortModel("热门", "天津市");
        sortModels.add(sortModeltj);
        SortModel sortModelxa = new SortModel("热门", "西安市");
        sortModels.add(sortModelxa);
        SortModel sortModelnj = new SortModel("热门", "南京市");
        sortModels.add(sortModelnj);

        int size = list.size();
        DbModel model;
        for (int i = 0; i < size; i++) {
            model = list.get(i);
            sortModels.add(new SortModel(model));
        }

        return sortModels;
    }

}
