package com.ajb.merchants.activity;

import android.os.Bundle;
import android.view.View;

import com.ajb.merchants.R;
import com.ajb.merchants.util.Constant;
import com.lidroid.xutils.ViewUtils;

/**
 * 设置界面
 */
public class CouponGivingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupongiving);
        ViewUtils.inject(this);
        String title = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(Constant.KEY_TITLE)) {
                title = bundle.getString(Constant.KEY_TITLE);
            }
        }
        initTitle(title);
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
