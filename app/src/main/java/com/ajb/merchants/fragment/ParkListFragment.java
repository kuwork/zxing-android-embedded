package com.ajb.merchants.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.WebViewActivity;
import com.ajb.merchants.adapter.ParkListAdapter;
import com.ajb.merchants.model.AroundPager;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.model.Pager;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MapMathUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class ParkListFragment extends BaseFragment {

    @ViewInject(R.id.listview)
    PullToRefreshListView listView;
    @ViewInject(R.id.null_data)
    RelativeLayout nullData;
    private ParkListAdapter adaper;
    private double mLon;
    private double mLat;
    private Pager pager;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLat = bundle.getDouble(Constant.KEY_LATITUDE);
            mLon = bundle.getDouble(Constant.KEY_LONGITUDE);
        }
    }


    public ParkListFragment() {
        // Required empty public constructor
    }

    public static ParkListFragment newInstance() {
        ParkListFragment fragment = new ParkListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ViewUtils.inject(this, v);
        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listView.isRefreshing()) {
                    listView.onRefreshComplete();
                }
                listView.setRefreshing(true);
            }
        }, 1000);
        return v;
    }

    private void initView() {
        // TODO Auto-generated method stub

        List<CarPark> list = new ArrayList<CarPark>();
        adaper = new ParkListAdapter(getActivity(), list, new OnClickListener() {
            @Override
            public void onClick(View v) {
                CarPark carPark = (CarPark) v.getTag();
                if (carPark != null) {
                    navToCarpark(mLat, mLon, carPark);
                }
            }
        });
        listView.setAdapter(adaper);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) parent
                        .getAdapter();
                ParkListAdapter listAdapter = (ParkListAdapter) headerViewListAdapter
                        .getWrappedAdapter();
                CarPark carPark = (CarPark) listAdapter.getItem(position - headerViewListAdapter.getHeadersCount());
                goCarPark(carPark);
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                pager = null;
                getNumberMsg(mLon,
                        mLat, 10000,
                        "", "1");
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                if (pager == null) {
                    getNumberMsg(mLon,
                            mLat, 10000,
                            "", "1");
                } else {
                    getNumberMsg(mLon,
                            mLat, 10000,
                            "", "" + (pager.page + 1));
                }
            }
        });
    }

    /**
     * 查询停车场
     */
    public void getNumberMsg(double centerLon, double centerLat, double range,
                             String type, String page) {
        if ("0".equals(type)) {
            type = "";
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.CENTERLON, String.valueOf(centerLon));
        params.addQueryStringParameter(Constant.InterfaceParam.CENTERLAT, String.valueOf(centerLat));
        params.addQueryStringParameter(Constant.InterfaceParam.DISTANCE, String.valueOf(range));
        params.addQueryStringParameter(Constant.InterfaceParam.PAGE, page);
        params.addQueryStringParameter(Constant.InterfaceParam.MEMBER, type);

        send(Constant.APPNEWSEARCH, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                try {
                    BaseResult<AroundPager<CarPark>> rr = gson.fromJson(
                            responseInfo.result,
                            new TypeToken<BaseResult<AroundPager<CarPark>>>() {
                            }.getType());
                    if ("0000".equals(rr.code)) {
                        if (rr.data != null) {
                            if (rr.data.list != null && rr.data.list.size() > 0) {
                                if (rr.data.page <= 1) {
                                    adaper.update(rr.data.list);
                                    adaper.notifyDataSetChanged();
                                } else {
                                    adaper.append(rr.data.list);
                                    adaper.notifyDataSetChanged();
                                }
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            } else {
                                showToast("没有更多数据");
                                if (adaper.getCount() == 0) {
                                    listView.setEmptyView(nullData);
                                    showErrorPage(listView, R.string.error_no_carpark_nearby, R.mipmap.norecord);
                                }
                            }
                            pager = rr.data;
                        } else {
                            showToast("刷新列表失败");
                        }
                    } else {
                        showToast(rr.msg);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    showToast("刷新列表失败");
                } finally {
                    listView.onRefreshComplete();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.e(error.getExceptionCode() + ":" + msg);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        showErrorPage(listView, R.string.error_network, R.mipmap.network);
                    }
                }, 500);

            }
        });
    }

    @Override
    public void refreshErrorPage() {
        super.refreshErrorPage();
        if (listView.isRefreshing()) {
            listView.onRefreshComplete();
        }
        listView.setRefreshing(true);
    }

    public void goCarPark(CarPark cp) {
        if (cp == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_TITLE, "车场详情");
        bundle.putString(Constant.KEY_URL, Constant.SERVER_URL + "/jsp/parkDetails?id=" + cp.getId() + "&distance=" + getDistanceStr(mLat, mLon, cp));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @NonNull
    private String getDistanceStr(double latitude, double longitude, CarPark carPark) {
        String disStr;
        if (TextUtils.isEmpty(carPark.getMyDistance())) {
            disStr = "未知";
        } else {
            try {
                Double d = Double.parseDouble(carPark.getMyDistance());
                d = MapMathUtil.calDistance(longitude,
                        Double.parseDouble(carPark.getLongitude()),
                        latitude,
                        Double.parseDouble(carPark.getLatitude()));

                if (d < 1000) {
                    disStr = carPark.getMyDistance() + "m";
                } else {
                    disStr = String.format("%.2f", d / 1000) + "km";
                }
            } catch (Exception e) {
                e.printStackTrace();
                disStr = "0km";
            }
        }
        return disStr;
    }

}
