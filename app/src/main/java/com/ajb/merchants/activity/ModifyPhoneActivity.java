package com.ajb.merchants.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.util.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ModifyPhoneActivity extends BaseActivity {

    @ViewInject(R.id.tvPhone)
    private TextView tvPhone;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        String titleStr = "";
        String descStr = "";
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_TITLE)) {
                titleStr = bundle.getString(Constant.KEY_TITLE);
            }
            if (bundle.containsKey(Constant.InterfaceParam.VALUE)) {
                descStr = bundle.getString(Constant.InterfaceParam.VALUE);
            }
        }
        initTitle(titleStr);
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (tvPhone != null && !TextUtils.isEmpty(descStr)) {
            tvPhone.setText(descStr);
        }
    }

}
