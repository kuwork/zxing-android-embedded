package com.ajb.merchants.activity;

import android.os.Bundle;
import android.view.View;

import com.ajb.merchants.R;

public class AccountManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        initView();
    }

    private void initView() {
        initTitle("账号管理");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_done, "完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void getAccountPermission() {

    }
}
