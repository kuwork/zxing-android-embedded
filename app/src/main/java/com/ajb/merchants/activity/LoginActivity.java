package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.ed_account)
    private EditText edAccount;
    @ViewInject(R.id.ed_password)
    private EditText edPassword;
    @ViewInject(R.id.tvAgreement)
    private TextView tvAgreement;
    @ViewInject(R.id.checkbox_consent_agreement)
    private CheckBox checkBoxConsentAgreement;

    private Dialog mDialog;

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
        if (!"".equals(sharedFileUtils.getString(SharedFileUtils.LOGIN_NAME))) {
            edAccount.setText(sharedFileUtils.getString(SharedFileUtils.LOGIN_NAME));
        }
    }

    @OnClick(R.id.btn_sure)
    public void onLoginClick(View v) {
        if (checkBoxConsentAgreement != null) {
            if (!checkBoxConsentAgreement.isChecked()) {
                showToast("请仔细阅读商户服务协议并同意");
                return;
            }
        }
        requestLogin(edAccount.getText().toString().trim(), edPassword.getText()
                .toString().trim());
    }

    private void requestLogin(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            showToast("请输入商户账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("请输入商户密码");
            return;
        }
        String channelId = JPushInterface.getRegistrationID(getBaseContext());
        if (TextUtils.isEmpty(channelId)) {
            showToast("注册设备失败");
            return;
        }
        // 起线程去登录
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, userName);
        params.addQueryStringParameter(Constant.InterfaceParam.PASSWORD, password);
        params.addQueryStringParameter(Constant.InterfaceParam.CHANNELID, channelId);
        send(Constant.PK_LOGIN, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        mDialog = MyProgressDialog.createLoadingDialog(
                                LoginActivity.this, "请稍后...");
                        mDialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (responseInfo.statusCode == 200) {
                            BaseResult<String> result = null;
                            try {
                                result = gson.fromJson(
                                        responseInfo.result,
                                        new TypeToken<BaseResult<String>>() {
                                        }.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (result == null) {
                                showToast(getString(R.string.error_network_short));
                                return;
                            }
                            if ("0000".equals(result.code)) {
                                setResult(RESULT_OK);
                                sharedFileUtils.putString(SharedFileUtils.KEY_TOKEN, result.data);
                                sharedFileUtils.putBoolean(SharedFileUtils.IS_LOGIN, true);
                                Bundle bundle = getIntent().getExtras();
                                String pageStr = "";
                                if (bundle != null && bundle.containsKey(Constant.KEY_FROM)) {
                                    pageStr = bundle.getString(Constant.KEY_FROM);
                                    if (SplashActivity.class.getName().equals(pageStr)) {
                                        Intent intent = new Intent(getBaseContext(), HomePageActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                                finish();
                            } else {
                                showToast(result.msg);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
