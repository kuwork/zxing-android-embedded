package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.LoginInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.Constant.InterfaceParam;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SMSBroadcastReceiver;
import com.ajb.merchants.util.SharedFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.ed_phone)
    private EditText ed_phone;
    @ViewInject(R.id.ed_code)
    private EditText ed_code;
    //    @ViewInject(R.id.tv_agreement)
//    private TextView tv_agreement;
    @ViewInject(R.id.btn_sure)
    private Button btn_login;
    @ViewInject(R.id.btn_code)
    private TextView btn_code;
    //    @ViewInject(R.id.checkBox)
//    private CheckBox checkBox;
    @ViewInject(R.id.lv_code)
    private LinearLayout lv_code;

    private Context context;
    private Dialog mDialog;
    private final int LOGIN = 1, NEWLOGIN = 2, CODE = 3, BIND_RETRY = 4;
    private TelephonyManager tm = null;
    private String deviString;
    private TimeCount timeCount;
    private SMSBroadcastReceiver mSMSBroadcastReceiver;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private LoginInfo loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSMSBroadcastReceiver = new SMSBroadcastReceiver();

        // 实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        // 注册广播
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);

        mSMSBroadcastReceiver
                .setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
                    @Override
                    public void onReceived(String message) {

                        ed_code.setText(getDynamicPassword(message));

                    }
                });
    }

    /**
     * 视图初始化
     */
    private void initView() {
        context = LoginActivity.this;

        initTitle("用户登录");
        initBackClick(R.id.NO_RES, new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviString = sharedFileUtils.getString("deviceId");
//        checkBox.setOnClickListener(this);
//        tv_agreement.setOnClickListener(this);
//        checkBox.setChecked(true);

        if ("".equals(sharedFileUtils.getString("LoginName"))) {
            ed_phone.setText(sharedFileUtils.getString("LoginName"));
        }
    }

    @OnClick(R.id.btn_sure)
    public void onLoginClick(View v) {
        if (ed_phone.getText().toString().trim().length() != 11) {
            showToast("请输入11位的手机号码");
            return;
        }
        if (TextUtils.isEmpty(ed_code.getText())) {
            showToast("请输入验证码");
            return;
        }
        newLogin(ed_phone.getText().toString().trim(), ed_code.getText()
                .toString().trim(), "2");
    }

    @OnClick(R.id.btn_code)
    public void onCodeClick(View v) {
        // 获取短信验证码
        if (ed_phone.getText().toString().trim().length() == 11) {
            mDialog = MyProgressDialog.createLoadingDialog(context, "获取中...");
            mDialog.setCancelable(true);
            mDialog.show();
            getCode(ed_phone.getText().toString().trim());
        } else {
            showToast(
                    getResources().getString(R.string.error_phone));
        }
    }

    // 获取短信
    public void getCode(String phoneNo) {
        /*SendRequestThread st = new SendRequestThread(mHandler,
                AppConfig.PK_APPCODE, CODE, 0);
        String param = AppConfig.buildCodeParamString(phoneNo);
        st.setParamString(param);
        st.start();*/

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("phoneNo", phoneNo);

        send(Constant.PK_APPCODE, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        mDialog.cancel();
                        if (responseInfo.statusCode == 200) {
                            Gson gson = new Gson();
                            BaseResult<String> result = gson.fromJson(
                                    responseInfo.result,
                                    new TypeToken<BaseResult<String>>() {
                                    }.getType());
                            if ("0000".equals(result.code)) {
                                timeCount = new TimeCount(60 * 1000, 1000);
                                timeCount.start();
                            } else {
                                showToast(result.data);
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        showToast(msg);
                    }
                });


    }

   /* *//**
     * 登录
     *//*
    public void login(String userName, String password) {
        // 起线程去登录
        SendRequestThread st = new SendRequestThread(mHandler,
                AppConfig.PK_LOGIN, LOGIN, 0);
        String param = AppConfig.buildLoginParamString(userName, password);
        st.setParamString(param);
        st.start();
    }*/

    /**
     * 新版登录
     */
    public void newLogin(String userName, String code, String status) {
        // 起线程去登录
        /*SendRequestThread st = new SendRequestThread(mHandler,
                AppConfig.PK_LOGIN, NEWLOGIN, 0);
        String param = AppConfig.buildNewLoginParamString(userName, code,
                status);
        st.setParamString(param);
        st.start();*/

        RequestParams params = new RequestParams();
        params.addQueryStringParameter("moblie", userName);
        params.addQueryStringParameter("code", code);
        params.addQueryStringParameter("status", status);
        send(Constant.PK_LOGIN, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        mDialog = MyProgressDialog.createLoadingDialog(
                                LoginActivity.this, "正在登陆,请稍后...");
                        mDialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (responseInfo.statusCode == 200) {
                            Gson gson = new Gson();
                            BaseResult<LoginInfo> result = gson.fromJson(
                                    responseInfo.result,
                                    new TypeToken<BaseResult<LoginInfo>>() {
                                    }.getType());
                            if ("0000".equals(result.code)) {
                                loginInfo = result.getData();
                                // 设备绑定
                                bindChannelIDParams = new RequestParams();
                                bindChannelIDParams.addQueryStringParameter(
                                        InterfaceParam.USERNAME, ed_phone.getText()
                                                .toString().trim());
                                bindChannelIDParams.addQueryStringParameter(
                                        InterfaceParam.CHANNELID,
                                        JPushInterface.getRegistrationID(context));
                                bindChannelIDParams.addQueryStringParameter(
                                        InterfaceParam.PHONETYPE, "3");
                                LogUtils.e(InterfaceParam.CHANNELID + ":"
                                        + JPushInterface.getRegistrationID(context));
                                bindChannelID();
                                return;
                            } else {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                                showToast(result.msg);
                            }
                        } else {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        showToast(getString(R.string.error_network_short));
                    }

                }
        );


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (msg.obj == null) {
                showToast(
                        getResources().getString(R.string.error_network_short));
                return;
            }
            switch (msg.what) {
                case NEWLOGIN:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());

                        String code = jsonObject.getString("code");
                        if (code.equals("0000")) {

                            JSONObject jsonObject2 = (JSONObject) jsonObject
                                    .get("data");
                            sharedFileUtils.putString("balance",
                                    jsonObject2.getString("balance"));
                            sharedFileUtils.putBoolean("isLogin", true);
                            sharedFileUtils.putString("LoginName", ed_phone
                                    .getText().toString().trim());
                            sharedFileUtils.putString("deviceId", tm.getDeviceId());
                            if (jsonObject2.has("invite")) {
                                sharedFileUtils.putString("invite",
                                        jsonObject2.getString("invite"));
                            }
                            // 设备绑定
                            bindChannelIDParams = new RequestParams();
                            bindChannelIDParams.addQueryStringParameter(
                                    InterfaceParam.USERNAME, ed_phone.getText()
                                            .toString().trim());
                            bindChannelIDParams.addQueryStringParameter(
                                    InterfaceParam.CHANNELID,
                                    JPushInterface.getRegistrationID(context));
                            bindChannelIDParams.addQueryStringParameter(
                                    InterfaceParam.PHONETYPE, "3");
                            LogUtils.e(InterfaceParam.CHANNELID + ":"
                                    + JPushInterface.getRegistrationID(context));
                            mHandler.sendMessage(mHandler.obtainMessage(BIND_RETRY,
                                    ""));
                            return;
                        }
                        showToast(jsonObject.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case CODE:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String code = jsonObject.getString("code");
                        if (code.equals("0000")) {
                            timeCount = new TimeCount(60 * 1000, 1000);
                            timeCount.start();
                        }
                        showToast(jsonObject.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case BIND_RETRY:

                    bindChannelID();
                    break;

            }

        }

    };
    private RequestParams bindChannelIDParams;

    private void bindChannelID() {
        if (bindChannelIDParams == null) {
            return;
        }
        send(Constant.APPSAVECHANNELID, bindChannelIDParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (responseInfo.statusCode == 200) {
                            Gson gson = new Gson();
                            BaseResult<String> result = gson.fromJson(
                                    responseInfo.result,
                                    new TypeToken<BaseResult<String>>() {
                                    }.getType());
                            if ("0000".equals(result.code)) {
                                LogUtils.d("设备注册成功");
                                sharedFileUtils.putString(SharedFileUtils.BALANCE,
                                        loginInfo.getBalance());
                                sharedFileUtils.putBoolean(SharedFileUtils.IS_LOGIN, true);
                                sharedFileUtils.putString(SharedFileUtils.LOGIN_NAME, ed_phone
                                        .getText().toString().trim());
                                sharedFileUtils.putString(SharedFileUtils.DEVICE_ID, tm.getDeviceId());
                                if (loginInfo.getInvite() != null) {
                                    sharedFileUtils.putString(SharedFileUtils.INVITE,
                                            loginInfo.getInvite());
                                }
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                LogUtils.e("设备注册失败:" + result.msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        LogUtils.e("设备注册失败:" + error.getExceptionCode() + ","
                                + msg);
                        // showToast("设备注册失败");
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(BIND_RETRY, ""),
                                15 * 1000);
                    }
                });

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            btn_code.setTextColor(getResources().getColor(R.color.orange));
        }

        @Override
        public void onFinish() {
            btn_code.setEnabled(true);
            btn_code.setText(getResources().getString(R.string.error_try_again));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_code.setEnabled(false);
            btn_code.setText("" + millisUntilFinished / 1000);
        }

    }


    ;

    public static String getDynamicPassword(String str) {
        // 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 4 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.print(m.group());
            dynamicPassword = m.group();
        }

        return dynamicPassword;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销短信监听广播
        this.unregisterReceiver(mSMSBroadcastReceiver);
    }
}
