package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.BaseModel;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.SearchCarCount;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.view.MyListView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

public class PayFeedBackActivity extends BaseActivity {

    @ViewInject(R.id.pay_feedback_listview)
    MyListView feedBackListView;
    @ViewInject(R.id.ed_another_words)
    EditText otherWords;
    private Dialog mDialog;
    private BaseListAdapter<BaseModel> adapter;
    private SearchCarCount searchCarCount;
    private BaseModel currentItem;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_feedback);
        ViewUtils.inject(this);
        initView();
        getFeedBackList();
    }

    private void initView() {
        initTitle("错误反馈");
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedBackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentItem = (BaseModel) parent.getItemAtPosition(position);
                adapter.selectItem(position);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey(Constant.APPSEARCHCARCOUNT)) {
            searchCarCount = (SearchCarCount) bundle.getSerializable(Constant.APPSEARCHCARCOUNT);
        }
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

    private void getFeedBackList() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.InterfaceParam.PAY_TYPE_FEEDBACK);
        send(Constant.APPPAYFEEDBACK, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog == null) {
                    mDialog = MyProgressDialog.createLoadingDialog(PayFeedBackActivity.this, "请稍后...");
                    mDialog.setCancelable(true);
                }
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                try {
                    BaseResult<List<BaseModel>> r = gson.fromJson(responseInfo.result, new TypeToken<BaseResult<List<BaseModel>>>() {
                    }.getType());
                    if (null != r && r.data != null) {
                        adapter = new BaseListAdapter<BaseModel>(getBaseContext(), r.data, R.layout.pay_feedback_item, null);
                        feedBackListView.setAdapter(adapter);
                    } else {
                        showToast(r.getMsg());
                    }
                } catch (Exception e) {
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
                showOkCancelAlertDialog(false, "提示", "网络连接出错，是否重试？", "确定",
                        "取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dimissOkCancelAlertDialog();
                                getFeedBackList();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dimissOkCancelAlertDialog();
                                finish();
                            }
                        });
            }
        });
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitBtn(View v) {
        String msg = otherWords.getText().toString().trim();
        if (currentItem == null && TextUtils.isEmpty(msg)) {
            showToast("请至少选择一项或者填写您的问题");
            return;
        }
        if (searchCarCount == null) {
            return;
        }
        RequestParams params = new RequestParams();
        if (currentItem != null) {
            params.addQueryStringParameter(Constant.InterfaceParam.APPPAYFEEDBACKID, currentItem.getId());
        }
        if (!TextUtils.isEmpty(msg)) {
            params.addQueryStringParameter(Constant.InterfaceParam.CONTENT, msg);
        }
        params.addQueryStringParameter(Constant.InterfaceParam.DATA, gson.toJson(searchCarCount));
        send(Constant.APPPAYFEEDBACKSAVE, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mDialog = MyProgressDialog.createLoadingDialog(PayFeedBackActivity.this, "请稍后...");
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
                }
        );

    }

}
