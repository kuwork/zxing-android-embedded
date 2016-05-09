package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.LoginInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.Constant.InterfaceParam;
import com.ajb.merchants.util.MyProgressDialog;
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

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.ed_account)
    private EditText edAccount;
    @ViewInject(R.id.ed_password)
    private EditText edPassword;
    @ViewInject(R.id.tvAgreement)
    private TextView tvAgreement;

    private Context context;
    private Dialog mDialog;
    private final int LOGIN = 1, NEWLOGIN = 2, CODE = 3, BIND_RETRY = 4;
    private TelephonyManager tm = null;
    private LoginInfo loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        initView();
    }

    /**
     * 视图初始化
     */
    private void initView() {
        context = LoginActivity.this;
        initTitle("登录");
        initBackClick(R.id.NO_RES, new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        tvAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvAgreement.getPaint().setAntiAlias(true);//抗锯齿
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if ("".equals(sharedFileUtils.getString("LoginName"))) {
            edAccount.setText(sharedFileUtils.getString("LoginName"));
        }
    }

    @OnClick(R.id.btn_sure)
    public void onLoginClick(View v) {
        if (TextUtils.isEmpty(edAccount.getText().toString().trim())) {
            showToast("请输入商户账号");
            return;
        }
        if (TextUtils.isEmpty(edPassword.getText().toString().trim())) {
            showToast("请输入商户密码");
            return;
        }
        newLogin(edAccount.getText().toString().trim(), edPassword.getText()
                .toString().trim());
    }

    /**
     * 新版登录
     */
    public void newLogin(String userName, String password) {
        // 起线程去登录
        RequestParams params = new RequestParams();
//        params.addQueryStringParameter("moblie", userName);
//        params.addQueryStringParameter("status", status);
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
                                        InterfaceParam.USERNAME, edAccount.getText()
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
                            sharedFileUtils.putString("LoginName", edAccount
                                    .getText().toString().trim());
                            sharedFileUtils.putString("deviceId", tm.getDeviceId());
                            if (jsonObject2.has("invite")) {
                                sharedFileUtils.putString("invite",
                                        jsonObject2.getString("invite"));
                            }
                            // 设备绑定
                            bindChannelIDParams = new RequestParams();
                            bindChannelIDParams.addQueryStringParameter(
                                    InterfaceParam.USERNAME, edAccount.getText()
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
                                sharedFileUtils.putString(SharedFileUtils.LOGIN_NAME, edAccount
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
