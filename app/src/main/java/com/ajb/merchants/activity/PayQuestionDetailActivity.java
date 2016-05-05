package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BaseModel;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.SearchCarCount;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PayQuestionDetailActivity extends BaseActivity {

    @ViewInject(R.id.ed_another_words)
    EditText otherWords;
    @ViewInject(R.id.question_list_detail_name)
    TextView detailName;
    @ViewInject(R.id.question_list_detail_content)
    TextView detailContent;
    private BaseModel baseModel;
    private SearchCarCount searchCarCount;
    private Dialog mDialog;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_question_detail);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Constant.APPSEARCHCARCOUNT)) {
                searchCarCount = (SearchCarCount) bundle
                        .getSerializable(Constant.APPSEARCHCARCOUNT);
            }
            if (bundle.containsKey(Constant.KEY_BASEMODEL)) {
                baseModel = (BaseModel) bundle
                        .getSerializable(Constant.KEY_BASEMODEL);
            }
        }
        if (baseModel != null) {
            initTitle(baseModel.getTitle());
            if (detailName != null) {
                detailName.setText(baseModel.getTitle());
            }
            if (detailContent != null) {
                detailContent.setText(baseModel.getDesc());
            }
        }
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String editable = otherWords.getText().toString();
                LogUtils.e("afterTextChanged:" + editable.length() + "");
                // // 如果包含特殊字符，则将特殊字符替换成""
                // String str =
                // CommonUtils.removeSpecialChar(editable.toString());
                String str = editable
                        .replaceAll(
                                "[^[\\u4e00-\\u9fa5|\\w\\s|^,.?~!@#$%^&*()_+=\"'\\[\\]{}，。？~！@#￥%……&*（）—“‘：•；【】{}<>《》、＂／＊＆＼＃＄％︿＿＋－＝＜「」\\\\/°‵′﹃≧≦▽￣╯□╰⊙﹏⊙︶︿︶╭∩╮＊◎‖\\∣-]]",
                                "");
                if (!editable.equals(str)) {
                    otherWords.setText(str);
                    otherWords.setSelection(str.length()); // 光标置后
                }
            }
        };
        otherWords.addTextChangedListener(textWatcher);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitBtn(View v) {
        if (searchCarCount == null) {
            return;
        }
        String msg = otherWords.getText().toString().trim();
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(
                Constant.InterfaceParam.APPPAYFEEDBACKID, baseModel.getId());
        if (!TextUtils.isEmpty(msg)) {
            params.addQueryStringParameter(Constant.InterfaceParam.CONTENT, msg);
        }
        params.addQueryStringParameter(Constant.InterfaceParam.APPORDERID,
                searchCarCount.getOrderSuccess().getOrderId());
        params.addQueryStringParameter(Constant.InterfaceParam.DATA,
                gson.toJson(searchCarCount));
        send(Constant.APPPAYFEEDBACKSAVE, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mDialog = MyProgressDialog.createLoadingDialog(
                                PayQuestionDetailActivity.this, "请稍后...");
                        mDialog.setCancelable(false);
                        mDialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        try {
                            BaseResult r = gson.fromJson(responseInfo.result,
                                    new TypeToken<BaseResult>() {
                                    }.getType());
                            if (r != null) {
                                showToast(r.msg);
                                if ("0000".equals(r.getCode())) {
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                            showToast(getString(R.string.error_network_short));
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        error.printStackTrace();
                        showToast(msg);
                    }
                });
    }

}
