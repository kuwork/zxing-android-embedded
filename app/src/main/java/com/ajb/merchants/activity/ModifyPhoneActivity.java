package com.ajb.merchants.activity;

import android.os.Bundle;
import android.view.View;

import com.ajb.merchants.R;

public class ModifyPhoneActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        initData();
    }

    private void initData() {
        initTitle("更换手机号");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
