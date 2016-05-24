package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditorActivity extends BaseActivity {

    @ViewInject(R.id.edText)
    EditText edText;
    private Bundle bundle;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        bundle = getIntent().getExtras();
        String titleStr = "";
        String descStr = "";
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
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_done, "完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        if (edText != null && !TextUtils.isEmpty(descStr)) {
            edText.setText(descStr);
        }
    }

    private void submitData() {
        RequestParams params = new RequestParams();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if (key.equals(Constant.KEY_TITLE)) {
                    continue;
                }
                if (key.equals(Constant.InterfaceParam.VALUE)) {
                    params.addQueryStringParameter(key, edText.getText().toString().trim());
                    continue;
                }
                params.addQueryStringParameter(key, bundle.getString(key));
            }
        }
        send(Constant.PK_UPDATESTOREDETAIL, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        EditorActivity.this, "请稍后...");
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
                    showToast(result.msg);
                    if ("0000".equals(result.code)) {
                        setResult(RESULT_OK);
                        finish();
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
}
