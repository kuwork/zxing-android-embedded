package com.ajb.merchants.fragment;


import android.app.Activity;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.merchants.R;
import com.ajb.merchants.interfaces.OnViewErrorListener;
import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.util.PathManager;

import org.apache.http.NameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements BaiduNaviManager.RoutePlanListener, OnViewErrorListener {

    protected Gson gson;
    private Toolbar toolbar;
    private TextView headerMenu1;
    protected SharedFileUtils sharedFileUtils;
    private String mSDCardPath;
    private BNRoutePlanNode mBNRoutePlanNode;
    private View errorView;
    protected Map<Integer, Runnable> runnableMap;//延时定位处理
    protected String from;//上一级页面的类名

    /**
     * @return 定位任务列表
     */
    protected Map<Integer, Runnable> getRunnableMap() {
        if (runnableMap == null) {
            runnableMap = new HashMap<>();
        }
        return runnableMap;
    }

    /**
     * 处理定位请求
     */
    protected void dealRunnableMap() {
        if (!getRunnableMap().isEmpty()) {
            for (Runnable r :
                    getRunnableMap().values()) {
                r.run();
            }
        }
    }


    public BaseFragment() {
        gson = new Gson();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sharedFileUtils = new SharedFileUtils(activity);
    }

    /**
     * @param str 标题
     * @Title initTitle
     * @Description 设置标题文字
     * @author jerry
     * @date 2015年7月1日 上午9:39:46
     */
    public void initTitle(View v, String str) {
        try {
            TextView title = (TextView) v.findViewById(R.id.tv_title);
            toolbar = (Toolbar) v.findViewById(R.id.toolbar);
            if (title != null) {
                title.setText(str);
            }
            if (toolbar != null) {
                toolbar.setTitle("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void showTip(String str, String action, View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(str)) {
            Snackbar.make(getActivity().getWindow().getDecorView(), str, Snackbar.LENGTH_LONG)
                    .setAction("Action", onClickListener).show();
        }
    }

    public String getLoginName() {
        return sharedFileUtils.getString(SharedFileUtils.LOGIN_NAME);
    }


    protected <T> HttpHandler<T> send(String uri, RequestParams params,
                                      RequestCallBack<T> callBack) {
        String url = Constant.SERVER_URL + uri;
        LogUtils.v("requestUrl:" + url);
        HttpUtils http = MyApplication.getNoCacheHttpUtils();
        if (params != null) {
            if (LogUtils.allowD) {
                List<NameValuePair> list = (List<org.apache.http.NameValuePair>) params.getQueryStringParams();
                int size = list.size();
                StringBuilder sb = new StringBuilder();
                org.apache.http.NameValuePair nameValuePair;
                for (int i = 0; i < size; i++) {
                    nameValuePair = list.get(i);
                    sb.append(nameValuePair.getName() + "=" + nameValuePair.getValue() + "&");
                }
                if (sb.toString().endsWith("&")) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                LogUtils.d(sb.toString());
            }
            return http.send(HttpRequest.HttpMethod.POST, url, params, callBack);
        } else {
            return http.send(HttpRequest.HttpMethod.POST, url, callBack);
        }
    }

    protected void navToCarpark(double latitude, double longitude, CarPark carPark) {
        boolean isInitSuccess = BaiduNaviManager.isNaviInited();
        boolean isInitSoSuccess = BaiduNaviManager.isNaviSoLoadSuccess();

        if (!isInitSuccess || !isInitSoSuccess) {
            //版本判断
            if (Build.VERSION.SDK_INT >= 23) {
                showToast("当前系统版本暂不支持使用导航功能");
                return;
            } else {
                showToast("百度地图初始化引擎失败");
            }
            return;
        }
        if (carPark == null) {
            showToast("缺少车场信息，无法开启导航");
            return;
        }
        launchNavigator(
                latitude,
                longitude,
                "我的位置",
                Double.valueOf(carPark.getLatitude()),
                Double.valueOf(carPark.getLongitude()),
                carPark.getAddress());
    }

    protected void initNavi() {
        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, Constant.APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String authinfo;
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        LogUtils.d(authinfo);
                    }

                    public void initSuccess() {
                        LogUtils.d("百度导航引擎初始化成功");
                    }

                    public void initStart() {
                        LogUtils.d("百度导航引擎初始化开始");
                    }

                    public void initFailed() {
                        LogUtils.d("百度导航引擎初始化失败");
                    }
                }, null);
    }

    protected boolean initDirs() {
        mSDCardPath = PathManager.getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, Constant.APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                return f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 启动GPS导航. 前置条件：导航引擎初始化成功
     */
    protected void launchNavigator(double fromlatitude, double fromlongitude,
                                   String fromAddress, double tolatitude,
                                   double tolongitude, String topoiaddress) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(fromlongitude, fromlatitude,
                fromAddress, null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(tolongitude,
                tolatitude, topoiaddress, null,
                BNRoutePlanNode.CoordinateType.BD09LL);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BNRouteGuideManager.getInstance().setVoiceModeInNavi(BNRouteGuideManager.VoiceMode.Novice);
            this.mBNRoutePlanNode = sNode;
            showToast("正在启动导航，请稍后");
            LogUtils.d("正在启动导航，请稍后:" + fromlatitude + "," + fromlongitude + "->" + tolatitude + "," + tolongitude);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list,
                    BaiduNaviManager.RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND,
                    true, this);
        }
    }


    @Override
    public void onJumpToNavigator() {
//        Intent intent = new Intent(getActivity(), BaiduNavGuideActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(BaiduNavGuideActivity.KEY_ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {
        showToast("导航规划失败，请重试");
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }


    /**
     * @param v     列表布局（内容）
     * @param strId 提示语
     * @param imgId 图标
     */
    @Override
    public void showErrorPage(View v, int strId, int imgId) {
        ViewGroup parent = (ViewGroup) v.getParent();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        errorView = getActivity().getLayoutInflater().inflate(R.layout.error_layout, null);
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            parent.addView(errorView, params);
        } else {
            FrameLayout frameLayout = new FrameLayout(getActivity());
            parent.removeView(v);
            FrameLayout.LayoutParams paramsMargin = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            paramsMargin.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            frameLayout.addView(v, paramsMargin);
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
            parent.addView(frameLayout, layoutParams);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.addView(errorView, params);
        }

        TextView tvError = (TextView) errorView.findViewById(R.id.tvError);
        ImageView imgError = (ImageView) errorView.findViewById(R.id.imgError);
        if (tvError != null) {
            tvError.setText(getResources().getString(strId));
        }
        if (imgError != null) {
            imgError.setImageResource(imgId);
            imgError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshErrorPage();
                }
            });
        }
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * 点击刷新，调用此方法
     */
    @Override
    public void refreshErrorPage() {
        if (errorView != null) {
            ((ViewGroup) errorView.getParent()).removeView(errorView);
        }
    }

    public boolean isLogin() {
        return getSharedFileUtils().getBoolean(SharedFileUtils.IS_LOGIN);
    }


    public SharedFileUtils getSharedFileUtils() {
        if (sharedFileUtils == null) {
            sharedFileUtils = new SharedFileUtils(getActivity());
        }
        return sharedFileUtils;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}
