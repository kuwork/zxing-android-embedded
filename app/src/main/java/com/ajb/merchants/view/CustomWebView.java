package com.ajb.merchants.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ajb.merchants.R;
import com.ajb.merchants.interfaces.UserInterface;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.lidroid.xutils.util.LogUtils;
import com.util.App;
import com.util.PathManager;

import java.io.File;
import java.util.Map;

public class CustomWebView extends WebView {

    protected static final String TAG = CustomWebView.class.getSimpleName();
    private Context context;
    private MySwipeRefreshLayout swipeLayout;
    private Dialog mDialog;
    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public CustomWebView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        initSetting();
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            initSwipeLayout(url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO Auto-generated method stub
            LogUtils.e("无网络信息监控");
            switch (errorCode) {
                case ERROR_UNKNOWN:
                    break;
                case ERROR_HOST_LOOKUP:     //无网络或者网络不通的情况下
                    Message message = handler.obtainMessage();
                    message.what = Constant.ERRORCODE.NO_NETWORK;
                    message.arg1 = R.mipmap.network;
                    message.arg2 = R.string.error_network;
                    handler.sendMessage(message);
                    break;
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    break;
                case ERROR_AUTHENTICATION:
                    break;
                case ERROR_PROXY_AUTHENTICATION:
                    break;
                case ERROR_CONNECT:
                    break;
                case ERROR_IO:
                    break;
                case ERROR_TIMEOUT:
                    break;
                case ERROR_REDIRECT_LOOP:
                    break;
                case ERROR_UNSUPPORTED_SCHEME:
                    break;
                case ERROR_FAILED_SSL_HANDSHAKE:
                    break;
                case ERROR_BAD_URL:
                    break;
                case ERROR_FILE:
                    break;
                case ERROR_FILE_NOT_FOUND:
                    break;
                case ERROR_TOO_MANY_REQUESTS:
                    break;
            }
        }

    }

    public void initSwipeLayout(String url) {
        // if (url.equals(Constant.SERVER_INDEX)) {
//		swipeLayout.setCanRefresh(false);
        // } else {
        if (swipeLayout != null) {
            swipeLayout.setCanRefresh(true);
        }
        // }
    }

    private void initSetting() {
        WebSettings settings = this.getSettings();
        String ua = settings.getUserAgentString();
        LogUtils.v("ua=" + ua);
        settings.setUserAgentString(ua + "; AndroidWeb/"
                + App.getVersionName(context) + "_"
                + App.getVersionCode(context));
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//不适用缓存
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);

        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        String cacheDirPath = PathManager.getDiskCacheDir(context) + File.separator + "WebCache";
        File file = new File(cacheDirPath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        if (!file.exists()) {
            file.mkdir();
        }
//      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        settings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        settings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        this.addJavascriptInterface(new UserInterface(context, this),
                "ztb");
        // this.addJavascriptInterface(new FunctionInterface(context),
        // "xw_func");
        this.setWebViewClient(new MyWebViewClient());
        this.setWebChromeClient(new WebChromeClient() {

                                    @Override
                                    public void onProgressChanged(WebView view, int newProgress) {
                                        LogUtils.v(newProgress + "");
                                        super.onProgressChanged(view, newProgress);
                                        if (swipeLayout != null) {
                                            if (newProgress == 100) {
                                                // 隐藏进度条
                                                swipeLayout.setRefreshing(false);
                                            } else {
                                                if (!swipeLayout.isRefreshing()) {
                                                    swipeLayout.setRefreshing(true);
                                                }
                                            }
                                        } else {
                                            try {
                                                if (newProgress == 100) {
                                                    if (mDialog != null && mDialog.isShowing()) {
                                                        mDialog.dismiss();
                                                        mDialog = null;
                                                    }
                                                } else {
                                                    if (mDialog == null) {
                                                        mDialog = MyProgressDialog.createLoadingDialog(getContext(), "请稍后...");
                                                        mDialog.show();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onGeolocationPermissionsHidePrompt() {
                                        super.onGeolocationPermissionsHidePrompt();
                                        Log.i(TAG, "onGeolocationPermissionsHidePrompt");
                                    }

                                    @Override
                                    public void onGeolocationPermissionsShowPrompt(final String origin,
                                                                                   final GeolocationPermissions.Callback callback) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("是否允许获取您的位置？");
                                        DialogInterface.OnClickListener dialogButtonOnClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int clickedButton) {
                                                if (DialogInterface.BUTTON_POSITIVE == clickedButton) {
                                                    callback.invoke(origin, true, true);
                                                } else if (DialogInterface.BUTTON_NEGATIVE == clickedButton) {
                                                    callback.invoke(origin, false, false);
                                                }
                                            }

                                        };
                                        builder.setPositiveButton("允许", dialogButtonOnClickListener);
                                        builder.setNegativeButton("拒绝", dialogButtonOnClickListener);
                                        builder.show();
                                        super.onGeolocationPermissionsShowPrompt(origin, callback);
                                        Log.i(TAG, "onGeolocationPermissionsShowPrompt");
                                    }

                                }

        );
    }

    @Override
    public void loadUrl(String url) {
        initSwipeLayout(url);
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        initSwipeLayout(url);
        super.loadUrl(url, additionalHttpHeaders);
    }

    public MySwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    public void setSwipeLayout(MySwipeRefreshLayout swipeLayout) {
        this.swipeLayout = swipeLayout;
    }
}
