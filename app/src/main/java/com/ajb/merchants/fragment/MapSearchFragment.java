package com.ajb.merchants.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.HistoryAdapter;
import com.ajb.merchants.interfaces.OnSearchListener;
import com.ajb.merchants.model.SuggestionTip;
import com.ajb.merchants.util.SharedFileUtils;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapSearchFragment extends BaseFragment implements OnGetGeoCoderResultListener, OnGetSuggestionResultListener {
    @ViewInject(R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(R.id.key)
    AutoCompleteTextView keyWorldsView;
    @ViewInject(R.id.lvSearchHistory)
    ListView lvSearchHistory;

    private boolean isClickATV = false;// 是否为详情Marker
    private SuggestionSearch mSuggestionSearch;
    private GeoCoder mSearch;
    private String cityName;
    private String test;
    private ArrayAdapter<SuggestionTip> sugAdapter;
    private OnSearchListener listener;
    private HistoryAdapter historyAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("CITY_NAME")) {
            cityName = bundle.getString("CITY_NAME");
        } else {
            cityName = "广州";
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSearchListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    public MapSearchFragment() {
        // Required empty public constructor
    }

    public static MapSearchFragment newInstance() {
        MapSearchFragment fragment = new MapSearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);
        ViewUtils.inject(this, v);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(cityName));
            }
        });
        keyWorldsView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    return true;
//                }
                return false;
            }
        });
        keyWorldsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ArrayAdapter<SuggestionTip> adapter = (ArrayAdapter<SuggestionTip>) arg0
                        .getAdapter();
                SuggestionTip st = adapter.getItem(arg2);
                test = st.getKey();
                // Geo搜索
                String address = toAddress(st.getCity(), st.getDistrict(), st.getKey());
                mSearch.geocode(new GeoCodeOption().city(cityName).address(address));
                if (historyAdapter != null) {
                    historyAdapter.save(address);
                }
                isClickATV = true;
            }
        });

        List<String> listHistory = (List<String>) ObjectUtil.getObject(sharedFileUtils
                .getString(SharedFileUtils.LAST_ADDRESS_HISTORY));

        if (listHistory == null) {
            listHistory = new ArrayList<String>();
        }

        historyAdapter = new HistoryAdapter(getActivity(), listHistory, HistoryAdapter.ADDRESS_HISTORY);

        lvSearchHistory.setAdapter(historyAdapter);

        lvSearchHistory.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        HistoryAdapter adapter = (HistoryAdapter) arg0.getAdapter();
                        keyWorldsView.setText((String) adapter.getItem(arg2));
                    }
                }

        );
        return v;
    }

    private String toAddress(String... s) {
        StringBuilder sb = new StringBuilder();
        for (String temp : s) {
            if (!TextUtils.isEmpty(temp)) {
                sb.append(temp);
            }
        }
        return sb.toString();

    }

    @OnClick(R.id.btnSearch)
    public void onSearchClick(View v) {

        if (keyWorldsView != null) {
            if (TextUtils.isEmpty(keyWorldsView.getText())) {
                return;
            }
            /**
             * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
             */
            // mSuggestionSearch
            // .requestSuggestion((new SuggestionSearchOption())
            // .keyword(keyWorldsView.getText().toString())
            // .city(cityName));
            String address = keyWorldsView.getText().toString();
            mSearch.geocode(new GeoCodeOption().city(cityName).address(
                    address));
            if (historyAdapter != null) {
                historyAdapter.save(address);
            }
            isClickATV = true;
        }
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            showToast("抱歉，未能找到结果");
            return;
        }
        if (listener != null) {
            listener.onSearchResultShow(result);
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        // sugAdapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_dropdown_item_1line);
        List<SuggestionTip> list = new ArrayList<SuggestionTip>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null)
                list.add(new SuggestionTip(info));
            // sugAdapter.add(info.key);
        }
        sugAdapter = new ArrayAdapter<SuggestionTip>(getActivity(),
                R.layout.search_tip_item_layout, R.id.search_tip_title, list);
        keyWorldsView.setAdapter(sugAdapter);
        if (!keyWorldsView.getText().toString().equals(test)) {
            test = keyWorldsView.getText().toString();
            keyWorldsView.setText(test);
            keyWorldsView.setSelection(test.length());
        }
    }

}
