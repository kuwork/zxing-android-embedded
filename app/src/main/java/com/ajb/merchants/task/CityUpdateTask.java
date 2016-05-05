package com.ajb.merchants.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ajb.merchants.model.ProvinceInfo;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.WordToSpell;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 16/4/25.
 */
public class CityUpdateTask extends AsyncTask<String, Integer, Boolean> {

    private Context context;

    public CityUpdateTask(Context c) {
        this.context = c;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        DbUtils dbUtils = MyApplication.getDbCity(context);
        try {
            InputStream inStream = context.getResources().getAssets()
                    .open("city.json");
            InputStreamReader inputReader = new InputStreamReader(inStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            Gson gson = new Gson();
            List<ProvinceInfo> list = new ArrayList<ProvinceInfo>();
            while ((line = bufReader.readLine()) != null) {
                ProvinceInfo p = gson.fromJson(line, new TypeToken<ProvinceInfo>() {
                }.getType());
                if (p != null) {
                    String cityNameSpell = WordToSpell.converterToFirstSpell(p
                            .getCityName());
                    String districtNameSpell = WordToSpell.converterToFirstSpell(p
                            .getDistrictName());
                    p.setCityPinyin(cityNameSpell);
                    p.setDistrictPinyin(districtNameSpell);
                    list.add(p);
                }
            }
            LogUtils.d("list:" + list.size());
            dbUtils.saveAll(list);
            LogUtils.e("处理结束list:" + list.size());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbUtils.deleteAll(ProvinceInfo.class);
            } catch (DbException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.onCancelled(aBoolean);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
