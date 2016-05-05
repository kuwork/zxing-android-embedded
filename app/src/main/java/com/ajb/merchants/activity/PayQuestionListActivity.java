package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
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
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class PayQuestionListActivity extends BaseActivity {

    @ViewInject(R.id.pay_question_list)
    ListView questionListView;
    @ViewInject(R.id.question_list_pay_time)
    TextView payTime;
    private Dialog mDialog;
    private BaseListAdapter<BaseModel> adapter;
    private SearchCarCount searchCarCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_question_list);
        ViewUtils.inject(this);
        initView();
        getFeedBackList();
    }

    private void initView() {
        initTitle("账单疑问反馈");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        questionListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        BaseModel baseModel = (BaseModel) parent
                                .getItemAtPosition(position);
                        if (baseModel == null) {
                            return;
                        }
                        if (searchCarCount == null) {
                            return;
                        }
                        Intent intent = new Intent(getBaseContext(),
                                PayQuestionDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.KEY_BASEMODEL,
                                baseModel);
                        bundle.putSerializable(Constant.APPSEARCHCARCOUNT,
                                searchCarCount);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey(Constant.APPSEARCHCARCOUNT)) {
            searchCarCount = (SearchCarCount) bundle
                    .getSerializable(Constant.APPSEARCHCARCOUNT);
        }
        if (searchCarCount != null) {
            payTime.setText(searchCarCount.getOrderSuccess().getPayTime());
        } else {
            payTime.setText("");
        }
    }

    private void getFeedBackList() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE,
                Constant.InterfaceParam.PAY_TYPE_ORDER_DOUBT);
        send(Constant.APPPAYFEEDBACK, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog == null) {
                    mDialog = MyProgressDialog.createLoadingDialog(
                            PayQuestionListActivity.this, "请稍后...");
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
                    BaseResult<List<BaseModel>> r = gson.fromJson(
                            responseInfo.result,
                            new TypeToken<BaseResult<List<BaseModel>>>() {
                            }.getType());
                    if (null != r && r.data != null) {
                        adapter = new BaseListAdapter<BaseModel>(
                                getBaseContext(), r.data,
                                R.layout.pay_question_list_item, null);
                        questionListView.setAdapter(adapter);
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
}
