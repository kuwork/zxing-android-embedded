package com.ajb.merchants.interfaces;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.model.DeviceInfo;
import com.ajb.merchants.model.ShareInfo;
import com.ajb.merchants.model.UserAppInfo;
import com.ajb.merchants.model.VersionInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.CustomWebView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserInterface {
    private final Gson gson;
    private Context context;
    private CustomWebView customWebView;
    private MyLocationListener mMyLocationListener;
    private LocationClient locationClient;
    private static final Gson GSON = new Gson();
    protected static final String TAG = UserInterface.class.getSimpleName();
    private SharedFileUtils sharedFileUtils;

    public UserInterface(Context context, CustomWebView customWebView) {
        this.context = context;
        this.customWebView = customWebView;
        this.gson = new Gson();
        sharedFileUtils = new SharedFileUtils(context);
    }

    @JavascriptInterface
    public void getUserInfo(String funcStr) {
        final String funcName = funcStr;
        String nameStr = sharedFileUtils.getString(SharedFileUtils.LOGIN_NAME);
        UserAppInfo userAppInfo = new UserAppInfo(nameStr, new DeviceInfo(), new VersionInfo(context));
        final String str = gson.toJson(userAppInfo);
        if (customWebView != null) {
            customWebView.post(new Runnable() {
                @Override
                public void run() {
                    String jsStr = "javascript:" + funcName + "('" + str
                            + "')";
                    customWebView.loadUrl(jsStr);
                }
            });

        }
    }

    @JavascriptInterface
    public void share(String str) {
        try {
            ShareInfo shareInfo = gson.fromJson(str, new TypeToken<ShareInfo>() {
            }.getType());
            if (shareInfo != null && customWebView.getHandler() != null) {
                customWebView.getHandler().sendMessage(customWebView.getHandler().obtainMessage(Constant.REQ_CODE_SHARE, shareInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void navToCarpark(String str) {
        try {
            CarPark info = gson.fromJson(str, new TypeToken<CarPark>() {
            }.getType());
            if (info != null && customWebView.getHandler() != null) {
                customWebView.getHandler().sendMessage(customWebView.getHandler().obtainMessage(Constant.REQ_CODE_NAV, info));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void call(String str) {
        if (!TextUtils.isEmpty(str)) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
            context.startActivity(intent);
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            String time = location.getTime();
            sb.append("time : ");
            sb.append(time);
            sb.append("\nerror code : ");
            sb.append(location.getLocType());

            double latitude = location.getLatitude();
            double longltude = location.getLongitude();
            float radius = location.getRadius();
            sb.append("\nlatitude : ");
            sb.append(latitude);
            sb.append("\nlontitude : ");
            sb.append(longltude);
            sb.append("\nradius : ");
            sb.append(radius);

            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                float speed = location.getSpeed();
                int satelliteNum = location.getSatelliteNumber();
                sb.append("\nspeed : ");
                sb.append(speed);
                sb.append("\nsatellite : ");
                sb.append(satelliteNum);
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(context, "定位失败,服务器错误", Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(context, "定位失败,请检查是否禁用定位", Toast.LENGTH_LONG)
                        .show();
            }

            if (location.getCity() != null) {
                if (customWebView != null) {
                    // Toast.makeText(
                    // context,
                    // "javascript:setCurrPosition('{\"lat\":"
                    // + location.getLatitude() + ",\"lng\":"
                    // + location.getLongitude() + "}')",
                    // Toast.LENGTH_LONG).show();
                    customWebView
                            .loadUrl("javascript:setCurrPosition('{\"lat\":"
                                    + location.getLatitude() + ",\"lng\":"
                                    + location.getLongitude() + "}')");
                    customWebView.initSwipeLayout(customWebView.getUrl());
                }
                locationClient.stop();
            }

        }
    }
}
