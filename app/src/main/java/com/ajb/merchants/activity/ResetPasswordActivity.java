package com.ajb.merchants.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.lidroid.xutils.ViewUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        initTitle("密码重置");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (tvName != null) {
            if (!TextUtils.isEmpty(getLoginName())) {
                tvName.setText(getLoginName());
            }
        }
        if (tvPhone != null) {
            if (!TextUtils.isEmpty(tvPhone.getText().toString().trim())) {
                btnVerification.setEnabled(true);
            } else {
                btnVerification.setEnabled(false);
            }
        }
    }

    @OnClick(value = {R.id.btnVerification, R.id.btnResetPassword})
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerification:
                //getVerificationCode();
                break;

            case R.id.btnResetPassword:
                break;
        }
    }
}
