package com.ajb.merchants.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.ShareInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.view.CustomWebView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class WebViewActivity extends BaseActivity {

    @ViewInject(R.id.webview)
    CustomWebView webView;
    @ViewInject(R.id.rl_network)
    RelativeLayout rlNetwork;
    @ViewInject(R.id.swipeLayout)
    MySwipeRefreshLayout swipeLayout;
    @ViewInject(R.id.imgError)
    ImageView imgError;
    @ViewInject(R.id.tvError)
    TextView tvErrorTip;
    private String url = "";
    private String title = "";
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ERRORCODE.NO_NETWORK: {
                    imgError.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(url)) {
                                if (url.startsWith("http://") || url.startsWith("https://")) {
                                    webView.loadUrl(url);
                                } else {
                                    webView.loadUrl("http://" + url);
                                }
                            } else {
                                webView.loadUrl("http://portal.eanpa-gz-manager.com/static/products.html");
                            }
                            swipeLayout.setVisibility(View.VISIBLE);
                            rlNetwork.setVisibility(View.GONE);
                        }
                    });
                    swipeLayout.setVisibility(View.GONE);
                    rlNetwork.setVisibility(View.VISIBLE);
                    if (msg.arg1 != 0) {
                        imgError.setImageResource(msg.arg1);
                    }
                    if (msg.arg2 != 0) {
                        tvErrorTip.setText(getResources().getString(msg.arg2));
                    }
                    break;
                }
                case Constant.REQ_CODE_SHARE: {
                    ShareInfo shareInfo = (ShareInfo) msg.obj;
                    if (shareInfo != null) {
                        if (ShareInfo.WEIXIN.equals(shareInfo.getType())) {
                            shareToWechat(shareInfo);
                        } else if (ShareInfo.PENGYOUQUAN.equals(shareInfo.getType())) {
                            shareToWechatMoment(shareInfo);
                        } else if (ShareInfo.SMS.equals(shareInfo.getType())) {
                            shareToSMS(shareInfo);
                        }
                    }
                    break;
                }
            }
        }
    };

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                showTip("服务端网络定位失败", null, null);
                return;
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                showTip("网络不通导致定位失败，请检查网络是否通畅", null, null);
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                showTip("定位失败，可能是处于飞行模式下，若不是可以试着重启手机", null, null);
                return;
            }
            LogUtils.v(location.getLatitude() + "---"
                    + location.getLongitude() + "---"
                    + location.getDirection());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ViewUtils.inject(this);
        initView();
//        if (initDirs()) {
//            initNavi();
//        }
        // 定位初始化
        mLocClient = new LocationClient(getBaseContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        // option.setPriority(LocationClientOption.NetWorkFirst);//
        // 设置网络优先(不设置，默认是gps优先)
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setScanSpan(10000);// 设置发起定位请求的间隔时间为10s(小于1秒则一次定位)
        mLocClient.setLocOption(option);
    }

    private void initView() {
        webView.setHandler(handler);
        swipeLayout.setCanRefresh(false);
        swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(webView.getUrl());
            }
        });
        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.holo_blue_bright),
                getResources().getColor(R.color.holo_green_light),
                getResources().getColor(R.color.holo_orange_light),
                getResources().getColor(R.color.holo_red_light));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("URL")) {
                url = bundle.getString("URL");
            }
            if (bundle.containsKey("TITLE")) {
                title = bundle.getString("TITLE");
            }
        }
        if (TextUtils.isEmpty(title)) {
            initTitle("");
        } else {
            initTitle(title);
        }
        //左侧关闭按钮
        initBackClick(NO_RES, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        initCloseMenuClick(R.drawable.actionbar_finish, getString(R.string.action_close), new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                webView.loadUrl(url);
            } else {
                webView.loadUrl("http://" + url);
            }
        } else {
            webView.loadUrl("http://portal.eanpa-gz-manager.com/static/products.html");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        this.webView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constant.KEY_URL, url);
        outState.putString(Constant.KEY_TITLE, title);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        url = (String) savedInstanceState.get(Constant.KEY_URL);
        title = (String) savedInstanceState.get(Constant.KEY_TITLE);
    }

}
