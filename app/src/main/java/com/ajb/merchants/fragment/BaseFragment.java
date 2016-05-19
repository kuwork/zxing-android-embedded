package com.ajb.merchants.fragment;


import android.app.Activity;
import android.content.Intent;
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
import com.ajb.merchants.activity.LoginActivity;
import com.ajb.merchants.interfaces.OnViewErrorListener;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.util.ObjectUtil;
import com.util.PathManager;

import org.apache.http.NameValuePair;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements OnViewErrorListener {

    protected Gson gson;
    private Toolbar toolbar;
    private TextView headerMenu1;
    protected SharedFileUtils sharedFileUtils;
    private String mSDCardPath;
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
        if (params == null) {
            params = new RequestParams();
        }
        if (!Constant.PK_LOGIN.equals(uri)) {
            String tokenId = sharedFileUtils.getString(SharedFileUtils.TOKEN);
            if (TextUtils.isEmpty(tokenId)) {
                callBack.onFailure(new HttpException(403), "请重新登录");
                return null;
            }
            params.addQueryStringParameter(Constant.InterfaceParam.TOKEN, tokenId);
        }
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
                    onErrorPageClick();
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
    public void onErrorPageClick() {
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

    protected void fail(HttpException error, String msg) {
        if (error.getExceptionCode() == 0) {
            showToast(getString(R.string.error_network_short));
        } else if (error.getExceptionCode() == 403) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(intent, Constant.REQ_CODE_LOGIN);
        } else {
            showToast(msg);
        }
    }

    /**
     * @return 本地缓存的用户信息
     */
    protected AccountSettingInfo getAccountSettingInfo() {
        return (AccountSettingInfo) ObjectUtil.getObject(sharedFileUtils.getString(SharedFileUtils.ACCOUNT_SETING_INFO));
    }
}
