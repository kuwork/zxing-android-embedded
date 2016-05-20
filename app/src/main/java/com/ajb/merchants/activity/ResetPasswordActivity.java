package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AccountSettingInfo;
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

public class ResetPasswordActivity extends BaseActivity {

    @ViewInject(R.id.tvName)
    private TextView tvName;
    @ViewInject(R.id.tvPhone)
    private TextView tvPhone;
    @ViewInject(R.id.edPassword)
    private EditText edPassword;
    @ViewInject(R.id.edConfirmPassword)
    private EditText edConfirmPassword;
    @ViewInject(R.id.edVerification)
    private EditText edVerification;
    @ViewInject(R.id.btnVerification)
    private Button btnVerification;
    @ViewInject(R.id.btnResetPassword)
    private Button btnResetPassword;

    private AccountSettingInfo accountSettingInfo;
    private AccountInfo accountInfo;
    private TimeCount timeCount;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ViewUtils.inject(this);
        initData();
        onTextChange();
    }

    private void initData() {
        initTitle("密码重置");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        accountSettingInfo = getAccountSettingInfo();
        accountInfo = accountSettingInfo.getAccountInfo();
        if (accountSettingInfo == null || accountInfo == null) {
            return;
        }
        if (tvName != null) {
            if (!TextUtils.isEmpty(accountInfo.getAccount())) {
                tvName.setText(accountSettingInfo.getAccountInfo().getAccount());
            }
        }
        if (tvPhone != null) {
            String phoneStr = "";
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey(Constant.InterfaceParam.PHONE)) {
                    phoneStr = bundle.getString(Constant.InterfaceParam.PHONE);
                }
            }
            tvPhone.setText(phoneStr);
            if (!TextUtils.isEmpty(phoneStr)) {
                btnVerification.setEnabled(true);
            } else {
                btnVerification.setEnabled(false);
            }
        }
    }

    /**
     * 添加edittext文本框监听
     */
    private void onTextChange() {
        if (edPassword == null) {
            return;
        }
        if (edConfirmPassword == null) {
            return;
        }
        if (edVerification == null) {
            return;
        }
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnResetPassword.setEnabled(isAllFill());
            }
        };
        edPassword.addTextChangedListener(textWatcher);
        edConfirmPassword.addTextChangedListener(textWatcher);
        edVerification.addTextChangedListener(textWatcher);
    }

    @OnClick(value = {R.id.btnVerification, R.id.btnResetPassword})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerification:
                getVerificationCode();
                break;

            case R.id.btnResetPassword:
                String account = accountInfo.getAccount();
                String phone = accountInfo.getPhone();
                String password = edPassword.getText().toString().trim();
                String confirmPassword = edConfirmPassword.getText().toString().trim();
                String msgCode = edVerification.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    showToast(getString(R.string.tip_reset_pwd));
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    showToast(getString(R.string.tip_reset_pwd_confirm));
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    showToast(getString(R.string.tip_password_not_match));
                    return;
                }
                if (TextUtils.isEmpty(msgCode)) {
                    showToast(getString(R.string.tip_reset_phone_verification));
                    return;
                }
                if (TextUtils.isEmpty(account)) {
                    showToast(getString(R.string.tip_empty_account_name));
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    showToast(getString(R.string.tip_empty_phone));
                    return;
                }
                requestResetPassword(account, password, msgCode, phone);
                break;
        }
    }

    /**
     * 获取手机验证码
     */
    private void getVerificationCode() {
        String account = accountInfo.getAccount();
        String phone = accountInfo.getPhone();
        if (TextUtils.isEmpty(account)) {
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter(Constant.InterfaceParam.ACCOUNT, account);
        params.addBodyParameter(Constant.InterfaceParam.PHONE, phone);
        send(Constant.PK_MSG_PUSH, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        ResetPasswordActivity.this, "请稍后...");
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
                        timeCount = new TimeCount(60 * 1000, 200); //构造CountDownTimer对象(60s)
                        timeCount.begin();
                    } else {
                        showToast(result.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                fail(error, msg);
            }
        });
    }

    /**
     * 重置密码
     */
    private void requestResetPassword(String account, String password, String msgCode, String phone) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        params.addQueryStringParameter(Constant.InterfaceParam.PASSWORD, password);
        params.addQueryStringParameter(Constant.InterfaceParam.CODE, msgCode);
        params.addQueryStringParameter(Constant.InterfaceParam.PHONE, phone);
        send(Constant.PK_RESET_PASSWORD, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        ResetPasswordActivity.this, "请稍后...");
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
                        sharedFileUtils.remove(SharedFileUtils.TOKEN);
                        sharedFileUtils.remove(SharedFileUtils.IS_LOGIN);
                        showToast(result.getMsg());
                        finish();
                    } else {
                        setResult(RESULT_CANCELED);
                        showToast(result.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                fail(error, msg);
                setResult(RESULT_CANCELED);
            }
        });
    }

    /**
     * 倒计时
     */
    private class TimeCount extends CountDownTimer {

        /**
         * 参数依次为总时长,和计时的时间间隔
         *
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void begin() {
            btnVerification.setEnabled(false);
            btnVerification.setText("60s");
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {  //计时过程显示
            btnVerification.setText((int) Math.ceil(millisUntilFinished / 1000f) + "s");
        }

        @Override
        public void onFinish() { //计时完毕时触发
            btnVerification.setText("获取验证码");
            if (!TextUtils.isEmpty(accountInfo.getPhone())) {
                btnVerification.setEnabled(true);
            } else {
                btnVerification.setEnabled(false);
            }
            cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    private boolean isAllFill() {
        if (TextUtils.isEmpty(tvName.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(edPassword.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(edConfirmPassword.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(tvPhone.getText())) {
            return false;
        }
        if (TextUtils.isEmpty(edVerification.getText())) {
            return false;
        }
        return true;
    }

}
