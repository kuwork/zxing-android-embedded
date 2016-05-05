package com.ajb.merchants.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.WebViewActivity;
import com.ajb.merchants.interfaces.OnCameraListener;
import com.ajb.merchants.interfaces.OnLocateListener;
import com.ajb.merchants.interfaces.OnSearchListener;
import com.ajb.merchants.model.AppSearchData;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.util.CarLocation;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MapMathUtil;
import com.ajb.merchants.util.SharedFileUtils;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.util.BitmapUtil;
import com.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnLocateListener {

    private final static int CARPARK_MAX_SIZE = 100;
    @ViewInject(R.id.search_bar)
    View searchBar;
    @ViewInject(R.id.bmapView)
    MapView mMapView;
    @ViewInject(R.id.img_camera)
    ImageView img_camera;
    @ViewInject(R.id.imgColorInfo)
    ImageView imgColorInfo;
    @ViewInject(R.id.example_layout)
    LinearLayout exampleLayout;
    @ViewInject(R.id.mapView_layout)
    RelativeLayout mapView_layout;
    private OnSearchListener listener;
    private OnCameraListener cameraListener;
    private OnLocateListener locateListener;
    private BaiduMap mBaiduMap;
    private Marker mMakerCenter;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private String cityName;
    public MyLocationData locData;// 最新的位置
    private List<CarPark> lisCarRarkt = null;
    private View root;
    private PopupWindow popup, carLocationPop;
    private float zoomLevel = 13.5f;// 当前地图缩放级别
    boolean isFirstLoc = true;// 是否首次定位
    private HashMap<String, Drawable> bitmapMap;
    private Map<String, Marker> markersMap;
    private ArrayList<Marker> markers;
    private View viewMarker, viewMarkerBigger;
    private CarPark popup_carPark;
    private Dialog dialog;
    private boolean isImgColorInfoVisible = true;
    private ValueAnimator animZoomIn;

    private ValueAnimator animZoomOut;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (isImgColorInfoVisible) {
                    exampleLayout.performClick();
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSearchListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        try {
            cameraListener = (OnCameraListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();

        }
        try {
            locateListener = (OnLocateListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();

        }
    }


    public MapViewFragment() {
        if (markersMap == null) {
            markersMap = new HashMap<String, Marker>();
        }
        if (markers == null) {
            markers = new ArrayList<Marker>();
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        isImgColorInfoVisible = false;
        cancelAnimate();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (imgColorInfo != null) {
            isImgColorInfoVisible = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exampleLayout.performClick();
                }
            }, 1000);
        }
    }

    public static MapViewFragment newInstance() {
        MapViewFragment fragment = new MapViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        ViewUtils.inject(this, v);
        initMap();
        isCarLocationPicExist();
        return v;
    }


    @Override
    public void onPause() {
//        if (mapView_layout != null) {
//            mMapView.setVisibility(View.GONE);
//            mapView_layout.invalidate();
//            mapView_layout.setVisibility(View.GONE);
//        }
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }


    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    private void initMap() {
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        Rect rect = new Rect();
        mBaiduMap.setCompassPosition(new Point(100, 100));
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.isBuildingsEnabled();// 获取是否允许楼块效果
        mMapView.showZoomControls(false);
        mBaiduMap.setMaxAndMinZoomLevel(20, 9);
        mBaiduMap
                .setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
                    @Override
                    public boolean onMyLocationClick() {
                        if (mBaiduMap.getMaxZoomLevel() - 1 > mBaiduMap
                                .getMapStatus().zoom) {
                            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                                    .zoomBy(1);
                            mBaiduMap.animateMapStatus(mapStatusUpdate);
                        }
                        if (MapViewFragment.this.locData != null && lisCarRarkt != null
                                && lisCarRarkt.size() > 0) {
                            CarPark carPark = null;
                            CarPark carParkNearby = null;
                            double m = Double.MAX_VALUE;
                            for (int i = 0; i < lisCarRarkt.size(); i++) {
                                carPark = lisCarRarkt.get(i);
                                double dis = Double.MAX_VALUE;
                                try {
                                    dis = MapMathUtil.calDistance(
                                            MapViewFragment.this.locData.longitude, Double
                                                    .parseDouble(carPark
                                                            .getLongitude()),
                                            MapViewFragment.this.locData.latitude, Double
                                                    .parseDouble(carPark
                                                            .getLatitude()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dis = Double.MAX_VALUE;
                                }
                                if (m > dis) {
                                    carParkNearby = carPark;
                                    m = dis;
                                }
                            }
                            if (m != Double.MAX_VALUE && m < 100
                                    && carParkNearby != null) {
                                initPopupWindows(carParkNearby);
                            }
                        }
                        return false;
                    }
                });
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.navi_map_gps_locked);
        MyLocationConfiguration config = new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfigeration(config);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {

                if (mBaiduMap != null) {
                    LogUtils.e("ZOOM=" + arg0.zoom);
                    if (Math.floor(arg0.zoom) <= mBaiduMap.getMinZoomLevel()) {
                        MapStatus mapStatus = new MapStatus.Builder().zoom(
                                mBaiduMap.getMinZoomLevel()).build();
                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                                .newMapStatus(mapStatus);
                        // 改变地图状态
                        mBaiduMap.setMapStatus(mMapStatusUpdate);
                    } else if (Math.floor(arg0.zoom) >= mBaiduMap
                            .getMaxZoomLevel()) {
                        MapStatus mapStatus = new MapStatus.Builder().zoom(
                                mBaiduMap.getMaxZoomLevel()).build();
                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                                .newMapStatus(mapStatus);
                        // 改变地图状态
                        mBaiduMap.setMapStatus(mMapStatusUpdate);
                    }
                    if (mMakerCenter != null) {
                        mMakerCenter.remove();
                    }
                    LatLng target = mBaiduMap.getMapStatus().target;
                    LogUtils.d(target.toString());
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.mipmap.loc_04);
                    OverlayOptions ooA = new MarkerOptions().position(target)
                            .icon(bitmapDescriptor).zIndex(5).draggable(true);
                    if (ooA == null) {
                        return;
                    }
                    mMakerCenter = (Marker) (mBaiduMap.addOverlay(ooA));
                    searchParkingAround(target.longitude, target.latitude, 10000,
                            "0");
                }
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                long start = System.currentTimeMillis();
                if (marker != null && marker.getExtraInfo() != null
                        && marker.getExtraInfo().containsKey("CarPark")) {
                    CarPark cp = (CarPark) marker.getExtraInfo()
                            .getSerializable("CarPark");
                    if (cp == null) {
                        return false;
                    }
                    initPopupWindows(cp);
                    BitmapDescriptor descriptor = BitmapDescriptorFactory
                            .fromView(getMarkerView(cp, true));
                    marker.setIcon(descriptor);
                    marker.setZIndex(9);
                    LogUtils.d("耗时：" + (System.currentTimeMillis() - start) + "ms");
                }
                return false;
            }
        });
        initLastLoction();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(zoomLevel);// 设置地图的缩放比例
        mBaiduMap.setMapStatus(msu);// 将前面的参数交给BaiduMap类


    }

    /**
     * @param v 搜索输入框点击事件
     */
    @OnClick(R.id.search_bar)
    public void onSearchBarClick(View v) {
        if (listener != null) {
            listener.onSearchBarClick();
        }
    }

    @OnClick(R.id.docenter_layout)
    public void onCenterLocationClick(View v) {
        if (locData != null) {
            LatLng ll = new LatLng(locData.latitude, locData.longitude);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        } else {
            showToast("定位中,请稍后..");
            getRunnableMap().put(Constant.REQ_CENTER_LOCATION, new Runnable() {
                @Override
                public void run() {
                    getRunnableMap().remove(Constant.REQ_CENTER_LOCATION);
                    LatLng ll = new LatLng(locData.latitude, locData.longitude);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);
                }
            });
        }
    }

    @OnClick(R.id.list_layout)
    public void onListLayoutClick(View v) {
        if (locData != null) {
            if (listener != null) {
                listener.onListClick(locData);
            }
        } else {
            showToast("定位中,请稍后..");
            getRunnableMap().put(Constant.REQ_CENTER_LOCATION, new Runnable() {
                @Override
                public void run() {
                    getRunnableMap().remove(Constant.REQ_CENTER_LOCATION);
                    if (listener != null) {
                        listener.onListClick(locData);
                    }
                }
            });
        }

//        if (locData != null) {
//            Intent intent = new Intent(getActivity(),
//                    ParkListActivity.class);
//            intent.putExtra(Constant.KEY_LATITUDE, locData.latitude);
//            intent.putExtra(Constant.KEY_LONGITUDE, locData.longitude);
//            getActivity().startActivity(intent);
//        }
    }

    @OnClick(R.id.capture_layout)
    public void onCameraClick(View v) {
        if (cameraListener == null) {
            return;
        }
        if (isCarLocationPicExist()) {
            cameraListener.onShowPhoto();
        } else {
            cameraListener.onStartCaptrue();
        }
    }

    @OnClick(R.id.gift_layout)
    public void onGiftLayoutClick(View v) {
        Intent intent = new Intent(Constant.BANNER_ACTION_HOME);
        getActivity().sendBroadcast(intent);
    }

    /**
     * @param v 地图标识按钮事件
     */
    @OnClick(R.id.example_layout)
    public void onExampleClick(View v) {
        if (imgColorInfo == null) {
            return;
        }
        handler.removeMessages(0);
        imgColorInfo.setVisibility(View.VISIBLE);
        if (isImgColorInfoVisible) {
            cancelAnimate();
            zoomOutAnimate();
        } else {
            cancelAnimate();
            zoomInAnimate();
            handler.sendEmptyMessageDelayed(0, 3000);
        }
        isImgColorInfoVisible = !isImgColorInfoVisible;
    }

    private void cancelAnimate() {
        handler.removeMessages(0);
        if (animZoomIn != null) {
            animZoomIn.cancel();
        }
        if (animZoomOut != null) {
            animZoomOut.cancel();
        }
    }

    private void zoomInAnimate() {
        int h = sharedFileUtils.getInt(SharedFileUtils.COLORINFO_HEIGHT);
        int w = sharedFileUtils.getInt(SharedFileUtils.COLORINFO_WIDTH);
        if (h == 0 || w == 0) {
            return;
        }
        int w_base = DensityUtil.dp2px(getActivity(), 100);
        int h_base = w_base * h / w;
        PropertyValuesHolder pvh_width = PropertyValuesHolder.ofInt("layout_width", 0, w_base);
        PropertyValuesHolder pvh_height = PropertyValuesHolder.ofInt("layout_height", 0, h_base);

        animZoomIn = ValueAnimator
                .ofPropertyValuesHolder(pvh_width, pvh_height)
                .setDuration(500);

        animZoomIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int pvh_width = (int) valueAnimator.getAnimatedValue("layout_width");
                int pvh_height = (int) valueAnimator.getAnimatedValue("layout_height");

                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) imgColorInfo.getLayoutParams();
                p.height = pvh_height;
                p.width = pvh_width;
                imgColorInfo.setLayoutParams(p);
                exampleLayout.destroyDrawingCache();
                imgColorInfo.destroyDrawingCache();
                exampleLayout.invalidate();
                imgColorInfo.invalidate();
            }
        });

        animZoomIn.start();


    }

    private void zoomOutAnimate() {
        int h = sharedFileUtils.getInt(SharedFileUtils.COLORINFO_HEIGHT);
        int w = sharedFileUtils.getInt(SharedFileUtils.COLORINFO_WIDTH);
        if (h == 0 || w == 0) {
            return;
        }
        int w_base = DensityUtil.dp2px(getActivity(), 100);
        int h_base = w_base * h / w;
        PropertyValuesHolder pvh_width = PropertyValuesHolder.ofInt("layout_width", w_base, 0);
        PropertyValuesHolder pvh_height = PropertyValuesHolder.ofInt("layout_height", h_base, 0);

        animZoomOut = ValueAnimator
                .ofPropertyValuesHolder(pvh_width, pvh_height)
                .setDuration(500);

        animZoomOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int pvh_width = (int) valueAnimator.getAnimatedValue("layout_width");
                int pvh_height = (int) valueAnimator.getAnimatedValue("layout_height");

                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) imgColorInfo.getLayoutParams();
                p.height = pvh_height;
                p.width = pvh_width;
                imgColorInfo.setLayoutParams(p);
                exampleLayout.destroyDrawingCache();
                imgColorInfo.destroyDrawingCache();
                exampleLayout.invalidate();
                imgColorInfo.invalidate();
            }
        });

        animZoomOut.start();
    }

    public void showSearchResult(GeoCodeResult result) {
        MapStatus mapStatus = new MapStatus.Builder().target(result
                .getLocation()).zoom(18f)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }


    @Override
    public void onLocate(BDLocation location) {
        if (location == null) {
            return;
        }
        LogUtils.d("定位成功");
        if (!TextUtils.isEmpty(location.getCity())) {
            cityName = location.getCity();
        }
        locData = new MyLocationData.Builder()
                .satellitesNum(location.getSatelliteNumber())
                .speed(location.getSpeed())
                .direction(location.getDirection())
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        showLocationOnMap();
        dealRunnableMap();
    }

    private void showLocationOnMap() {
        if (locData == null) {
            return;
        }
        searchParkingAround(locData.longitude, locData.latitude, 10000f, "0");
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(locData.latitude,
                    locData.longitude);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
    }

    @Override
    public MyLocationData getLocation() {
        return locData;
    }

    /**
     * 初始化按钮背景
     *
     * @return
     */
    public boolean isCarLocationPicExist() {
        Bitmap bitmap = CarLocation.getCarLocationBitmap(getActivity());
        if (bitmap != null) {
            if (img_camera != null) {
                img_camera.setImageResource(R.mipmap.camera_after);
                bitmap.recycle();
            }
            return true;
        } else {
            if (img_camera != null) {
                img_camera.setImageResource(R.mipmap.camera_before);
            }
            return false;
        }
    }


    protected void initPopupWindows(final CarPark carPark) {

        if (carPark == null) {
            return;
        }
        LogUtils.d("打开了" + carPark.getParkName());
        // 装载R.layout.popup对应的界面布局
        if (root == null || popup == null) {
            root = getActivity().getLayoutInflater()
                    .inflate(R.layout.popup_around, null);
            // 创建PopupWindow对象
            popup = new PopupWindow(root);
            popup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            // popup.setAnimationStyle(R.anim.in);
            popup.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
            popup.setOutsideTouchable(true);// 以及下一句 同时写才会有效
            popup.setFocusable(true);// 获取焦点
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    CarPark popup_carPark = null;
                    if (root != null) {
                        popup_carPark = (CarPark) root.getTag();
                    }
                    if (popup_carPark != null) {
                        if (markersMap.containsKey(popup_carPark.getId())) {
                            Marker marker = markersMap.get(popup_carPark
                                    .getId());
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                                    .fromView(getMarkerView(popup_carPark,
                                            false));
                            marker.setIcon(bitmapDescriptor);
                        }
                        if (root != null) {
                            root.setTag(null);
                        }
                    }
                }
            });

        }
        TextView popup_header_line = (TextView) root
                .findViewById(R.id.popup_header_line);
        popup_header_line.setBackgroundColor(Color.parseColor(carPark.getColor()));
        View popup_around_header = root
                .findViewById(R.id.popup_around_header);
        ImageView packMember = (ImageView) root.findViewById(R.id.packMember);
        TextView packname_tv = (TextView) root.findViewById(R.id.packname_tv);
        TextView packDis_tv = (TextView) root.findViewById(R.id.packDis_tv);
        LinearLayout space_ly = (LinearLayout) root.findViewById(R.id.space);
        TextView navigation_btn = (TextView) root
                .findViewById(R.id.navigation_btn);
        LinearLayout popListView = (LinearLayout) root
                .findViewById(R.id.popup_around_listview);
        root.setTag(carPark);
        packname_tv.setText(carPark.getParkName());
        String disStr = getDistanceStr(locData, carPark);
        packDis_tv.setText(disStr);
        List<Map<String, String>> info = carPark.getInfo();
        if (info != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view;
            popListView.removeAllViews();
            for (int i = 0; i < info.size(); i++) {
                view = inflater.inflate(R.layout.popup_around_listview_item,
                        null);
                popListView.addView(initItem(view, info.get(i)));
            }
        }

        // freeTimeListLayout.setVisibility(View.VISIBLE);
        // layout_comm.setVisibility(View.VISIBLE);
        // layout_member.setVisibility(View.VISIBLE);
        if (carPark.getOnlinePay().equals("1")) {
            packMember.setVisibility(ViewGroup.VISIBLE);
        } else {
            packMember.setVisibility(ViewGroup.GONE);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarPark popup_carPark = (CarPark) v.getTag();
                switch (v.getId()) {
                    case R.id.space:
                        popup.dismiss();
                        break;
                    case R.id.popup_around_header:
                    case R.id.popup_around_listview:
                        popup.dismiss();
                        goCarPark(popup_carPark);
                        break;
                    case R.id.navigation_btn:

                        //                        try {
                        //                            LatLng start = new LatLng(locData.latitude, locData.longitude);
                        //                            LatLng end = new LatLng(Double.parseDouble(popup_carPark.getLatitude()), Double.parseDouble(popup_carPark.getLongitude()));
                        //                            // 构建 导航参数
                        //                            NaviParaOption para = new NaviParaOption()
                        //                                    .startPoint(start).endPoint(end)
                        //                                    .startName("我的位置").endName(popup_carPark.getParkName());
                        //                            BaiduMapNavigation.openBaiduMapNavi(para, getActivity());
                        //                        } catch (BaiduMapAppNotSupportNaviException e) {
                        //                            e.printStackTrace();
                        //                            showTip("百度地图初始化引擎失败", null, null);
                        //                        } catch (Exception e) {
                        //                            e.printStackTrace();
                        //                            showTip("导航参数设置失败", null, null);
                        //                        }

                        double lat = 0;
                        double lng = 0;
                        if (locData != null) {
                            try {
                                lat = locData.latitude;
                                lng = locData.longitude;
                            } catch (Exception e) {
                                e.printStackTrace();
                                lat = 0;
                                lng = 0;
                            }
                        }
                        if (lat == 0 && lng == 0) {
                            // TODO 先定位再导航
//                            if (mLocClient != null) {
//                                if (dialog != null && dialog.isShowing()) {
//                                    dialog.dismiss();
//                                    dialog = null;
//                                }
//                                mLocClient.start();
//                                showToast("定位中，请稍后重试");
//                            }
                        } else {
                            popup.dismiss();
                            navToCarpark(lat, lng, popup_carPark);
                        }
                        break;

                    default:
                        break;
                }
            }
        };
        popListView.setOnClickListener(onClickListener);
        space_ly.setOnClickListener(onClickListener);
        popup_around_header.setOnClickListener(onClickListener);
        navigation_btn.setOnClickListener(onClickListener);
        popListView.setTag(carPark);
        popup_around_header.setTag(carPark);
        navigation_btn.setTag(carPark);
        if (!popup.isShowing())

        {
            popup.showAtLocation(getActivity().getWindow().getDecorView(),
                    Gravity.CENTER_HORIZONTAL, 0, 0);
        }

    }


    @NonNull
    private String getDistanceStr(MyLocationData locData, CarPark carPark) {
        String disStr;
        if (TextUtils.isEmpty(carPark.getMyDistance())) {
            disStr = "未知";
        } else {
            try {
                Double d = Double.parseDouble(carPark.getMyDistance());
                if (locData != null) {
                    d = MapMathUtil.calDistance(locData.longitude,
                            Double.parseDouble(carPark.getLongitude()),
                            locData.latitude,
                            Double.parseDouble(carPark.getLatitude()));
                }

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


    /**
     * 创建一行车场信息
     *
     * @param view
     * @param map
     * @return
     */
    private View initItem(View view, Map<String, String> map) {
        TextView item_name = (TextView) view
                .findViewById(R.id.pop_around_lv_text);
        TextView item_number = (TextView) view
                .findViewById(R.id.pop_around_lv_number);
        item_name.setText(map.get("key") + "");
        item_number.setText(map.get("value") + "");
        String keyColor = map.get("keyColor");
        String valueColor = map.get("valueColor");
        if (!TextUtils.isEmpty(keyColor)) {
            item_name.setTextColor(Color.parseColor(keyColor));
        } else {
            item_name.setTextColor(getResources().getColor(
                    R.color.item_tv_color_02));
        }

        if (!TextUtils.isEmpty(valueColor)) {
            item_number.setTextColor(Color.parseColor(valueColor));
        } else {
            item_number.setTextColor(getResources().getColor(R.color.black));
        }
        return view;
    }

    public View getMarkerView(CarPark carPark, boolean flag) {
        View view;
        if (flag) {
            //            if (viewMarkerBigger == null) {
            viewMarkerBigger = getActivity().getLayoutInflater().inflate(R.layout.popu_map_bigger, null);
            //            }
            view = viewMarkerBigger;
        } else {
            //            if (viewMarker == null) {
            viewMarker = getActivity().getLayoutInflater().inflate(R.layout.popu_map, null);
            //            }
            view = viewMarker;
        }
        TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
        ImageView imageView = (ImageView) view.findViewById(R.id.im_car);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) v.getTag()).setVisibility(View.VISIBLE);
            }
        });
        if (bitmapMap == null) {
            bitmapMap = new HashMap<String, Drawable>();
        }

        String colorStr = carPark.getColor();
        Drawable d = bitmapMap.get(colorStr);
        if (d == null) {
            Bitmap bmp = CommonUtils.getMarkerColored(getActivity(),
                    R.mipmap.loc_front, Color.parseColor("#24a7eb"),
                    Color.parseColor(colorStr), carPark.getUnit());
            d = BitmapUtil.toDrawable(getActivity()
                    , bmp);
            bitmapMap.put(colorStr, d);
        }
        if (TextUtils.isEmpty(carPark.getParkPrice())) {
            tv_price.setText("0");
        } else {
            tv_price.setText(carPark.getParkPrice());
        }
        tv_price.setTextColor(Color.parseColor(colorStr));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(d);
        } else {
            imageView.setBackgroundDrawable(d);
        }
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view;
    }

    public void goCarPark(CarPark cp) {
        if (cp == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_TITLE, "车场详情");
        bundle.putString(Constant.KEY_URL, Constant.SERVER_URL + "/jsp/parkDetails?id=" + cp.getId() + "&distance=" + getDistanceStr(locData, cp));
        intent.putExtras(bundle);
        startActivity(intent);
//        Intent intent = new Intent(getActivity(), AroundActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constant.KEY_CARPARK, cp);
//        bundle.putSerializable(Constant.KEY_LATITUDE, locData.latitude);
//        bundle.putSerializable(Constant.KEY_LONGITUDE, locData.longitude);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }


    protected void addPointOverlay(List<CarPark> listCarPark) {
        if (listCarPark == null || listCarPark.size() == 0) {
            return;
        }
        try {
            Marker mMaker;
            CarPark carPark;
            BitmapDescriptor bitmapDescriptor;
            if (markersMap == null) {
                markersMap = new HashMap<String, Marker>();
            }
            for (int i = 0; i < listCarPark.size(); i++) {
                carPark = listCarPark.get(i);
                if (markersMap.containsKey(carPark.getId())) {
                    mMaker = markersMap.get(carPark.getId());
                    mMaker.setZIndex(1);
                    Bundle bundle = mMaker.getExtraInfo();
                    bundle.putSerializable("CarPark", carPark);
                    mMaker.setExtraInfo(bundle);
                    markers.remove(mMaker);
                    markers.add(mMaker);
                } else {
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromView(getMarkerView(carPark, false));
                    LatLng ll = new LatLng(
                            Double.parseDouble(carPark.getLatitude()),
                            Double.parseDouble(carPark.getLongitude()));
                    OverlayOptions ooA = new MarkerOptions().position(ll)
                            .icon(bitmapDescriptor).zIndex(5).draggable(true);
                    if (ooA == null) {
                        continue;
                    }
                    mMaker = (Marker) (mBaiduMap.addOverlay(ooA));
                    mMaker.setZIndex(1);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CarPark", carPark);
                    mMaker.setExtraInfo(bundle);
                    markers.add(mMaker);
                    markersMap.put(carPark.getId(), mMaker);
                }
            }
            while (true) {
                LogUtils.d("markers.size()=" + markers.size());
                if (markers.size() <= CARPARK_MAX_SIZE) {
                    break;
                }
                Marker marker = markers.remove(0);
                carPark = (CarPark) marker.getExtraInfo().get("CarPark");
                markersMap.remove(carPark.getId());
                marker.remove();
            }
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

    }


    /**
     * 查询停车场
     */

    public void searchParkingAround(double centerLon, double centerLat, double range,
                                    String type) {
        if ("0".equals(type)) {
            type = "";
        }

        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.CENTERLON, centerLon + "");
        params.addQueryStringParameter(Constant.InterfaceParam.CENTERLAT, centerLat + "");
        params.addQueryStringParameter(Constant.InterfaceParam.DISTANCE, range + "");
        params.addQueryStringParameter(Constant.InterfaceParam.MEMBER, type + "");
        params.addQueryStringParameter(Constant.InterfaceParam.PAGE, "1");
        params.addQueryStringParameter(Constant.InterfaceParam.ROWS, "50");
        //        HttpUtils http = MyApplication.getNoCacheHttpUtils();
        //        http.send(HttpRequest.HttpMethod.POST,"http://172.16.35.5:8081/getParkMongo", params, new RequestCallBack<String>() {
        send(Constant.APPNEWSEARCH, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //                LogUtils.d(responseInfo.result);
                if (responseInfo.statusCode == 200) {
                    try {
                        BaseResult<AppSearchData> r = gson.fromJson(responseInfo.result,
                                new TypeToken<BaseResult<AppSearchData>>() {
                                }.getType());
                        if ("0000".equals(r.code)) {
                            if (r.data != null) {
                                addPointOverlay(r.data.around);
                                if (!TextUtils.isEmpty(r.data.colorUrl)) {
                                    imgColorInfo.setVisibility(View.GONE);
                                    isImgColorInfoVisible = false;
                                    sharedFileUtils.putString(SharedFileUtils.COLORINFO_URL_NEW, r.data.colorUrl);
                                    new BitmapUtils(getActivity()).display(imgColorInfo, r.data.colorUrl, new BitmapLoadCallBack<ImageView>() {
                                        @Override
                                        public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                                            container.setImageBitmap(bitmap);
                                            String url_new = sharedFileUtils.getString(SharedFileUtils.COLORINFO_URL_NEW);
                                            String url = sharedFileUtils.getString(SharedFileUtils.COLORINFO_URL);
                                            if (!TextUtils.isEmpty(url_new) && !url_new.equals(url)) {
                                                sharedFileUtils.putString(SharedFileUtils.COLORINFO_URL, url_new);
                                                sharedFileUtils.putInt(SharedFileUtils.COLORINFO_WIDTH, bitmap.getWidth());
                                                sharedFileUtils.putInt(SharedFileUtils.COLORINFO_HEIGHT, bitmap.getHeight());
                                                exampleLayout.performClick();
                                            }

                                        }

                                        @Override
                                        public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
                                            sharedFileUtils.remove(SharedFileUtils.COLORINFO_URL_NEW);
                                        }
                                    });
                                }
                            } else {
                                showToast(getString(R.string.error_network_short));
                            }
                        } else {
                            showToast(r.msg);
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_network_short));
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showToast(getString(R.string.error_network_short));
                LogUtils.d(msg);
            }
        });

    }

    /**
     * @Title initLastLoction
     * @Description 获取上次定位的GPS位置，或者默认广州
     * @author jerry
     * @date 2015年11月16日 下午1:46:03
     */
    protected void initLastLoction() {
        LatLng ll = null;
        if (locateListener != null) {
            locData = locateListener.getLocation();
            showLocationOnMap();
            if (locData != null) {
                double lat = Math.abs(locData.latitude);//纬度
                double lng = Math.abs(locData.longitude);//经度
                if (lat > 0 && lat <= 90 && lng > 0 && lng <= 180) {
                    ll = new LatLng(locData.latitude, locData.longitude);
                }
            }
        }
        if (ll == null) {//没有定位先显示历史位置
            try {
                String[] locStr = sharedFileUtils.getString(
                        SharedFileUtils.LAST_LOC).split(",");
                if (locStr.length < 2) {
                    ll = new LatLng(23.139761, 113.328249);//体育西
                } else {
                    ll = new LatLng(Double.parseDouble(locStr[0]),
                            Double.parseDouble(locStr[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
                ll = new LatLng(23.139761, 113.328249);
            }
        }
        MapStatus mapStatus = new MapStatus.Builder().target(ll).zoom(zoomLevel)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mapStatus);
        // 改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    public void setLocData(MyLocationData locData) {
        this.locData = locData;
    }
}
