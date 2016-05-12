package com.ajb.merchants.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ajb.merchants.R;
import com.ajb.merchants.util.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditorActivity extends BaseActivity {

    @ViewInject(R.id.edText)
    EditText edText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String titleStr = "";
        String descStr = "";
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_TITLE)) {
                titleStr = bundle.getString(Constant.KEY_TITLE);
            }
            if (bundle.containsKey(Constant.KEY_DESC)) {
                descStr = bundle.getString(Constant.KEY_DESC);
            }
        }
        initTitle(titleStr);
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
        if (edText != null && !TextUtils.isEmpty(descStr)) {
            edText.setText(descStr);
        }
    }
}
