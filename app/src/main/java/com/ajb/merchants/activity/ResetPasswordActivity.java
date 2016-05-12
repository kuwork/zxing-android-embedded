package com.ajb.merchants.activity;

import android.os.Bundle;
import android.view.View;

import com.ajb.merchants.R;
import com.lidroid.xutils.ViewUtils;

public class ResetPasswordActivity extends BaseActivity {

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
    }
}
