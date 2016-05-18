package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AccountSettingInfo;
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

import java.util.List;

public class AccountListActivity extends BaseActivity {

    @ViewInject(R.id.accountListView)
    private ListView accountListView;//账户列表
    private BaseListAdapter<AccountInfo> adapter;
    private AccountInfo clickItem;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        ViewUtils.inject(this);
        initView();
        getAccountList();
    }

    private void initView() {
        initTitle("账号管理");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_add, "新增", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AccountManagementActivity.class);
                startActivity(intent);
            }
        });
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout lv_detail = (LinearLayout) view.findViewById(R.id.lv_detail);
                if (lv_detail.getVisibility() == View.VISIBLE) {
                    lv_detail.setVisibility(View.GONE);
                } else {
                    lv_detail.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    /**
     * 获取账户列表
     */
    private void getAccountList() {
        AccountSettingInfo accountSettingInfo = getAccountSettingInfo();
        if (accountSettingInfo == null) {
            return;
        }
        AccountInfo accountInfo = accountSettingInfo.getAccountInfo();
        if (accountInfo == null) {
            return;
        }
        String account = accountInfo.getAccountName();
        if (TextUtils.isEmpty(account)) {
            showToast(getString(R.string.tip_empty_account_name));
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter(Constant.InterfaceParam.ACCOUNT, account);
        send(Constant.PK_QUERYACCOUNT, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        AccountListActivity.this, "请稍后...");
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (responseInfo.statusCode == 200) {
                    BaseResult<List<AccountInfo>> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<List<AccountInfo>>>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    if ("0000".equals(result.code)) {
                        if (result.data != null) {
                            if (adapter == null) {
                                adapter = new BaseListAdapter<AccountInfo>(getBaseContext(), result.data, R.layout.listview_item_account, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        switch (v.getId()) {
                                            case R.id.btn_edit:
                                                clickItem = (AccountInfo) v.getTag();
                                                break;

                                            case R.id.btn_delete:
                                                clickItem = (AccountInfo) v.getTag();
                                                showOkCancelAlertDialog(false, "确定删除账户？",
                                                        "",
                                                        "确定", "取消",
                                                        new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(
                                                                    View arg0) {
                                                                // TODO: 2016/5/18 delete account
                                                            }
                                                        }, new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(
                                                                    View arg0) {
                                                                dimissOkCancelAlertDialog();
                                                            }
                                                        });

                                                break;
                                        }
                                    }
                                });
                                accountListView.setAdapter(adapter);
                            } else {
                                adapter.update(result.data);
                            }
                        }
                    } else {
                        showOkAlertDialog(false, "提示", result.getMsg(), "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
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
