package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AccountManagementInfo;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.PermissionInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.view.MyGridView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AccountManagementActivity extends BaseActivity {

    @ViewInject(R.id.gridViewAccountPermission)
    private MyGridView gridViewAccountPermission;
    @ViewInject(R.id.edAccount)
    private EditText edAccount;
    @ViewInject(R.id.edPassword)
    private EditText edPassword;
    @ViewInject(R.id.edRemark)
    private EditText edRemark;
    @ViewInject(R.id.imgPasswordPass)
    private ImageView imgPasswordPass;

    private Dialog mDialog;
    private BaseListAdapter<PermissionInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);
        ViewUtils.inject(this);
        initView();
        getAccountPermission();
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
        AccountSettingInfo accountSettingInfo = getAccountSettingInfo();
        if (accountSettingInfo == null) {
            return;
        }
        AccountInfo accountInfo = accountSettingInfo.getAccountInfo();
        if (accountInfo == null) {
            return;
        }
        String account = accountInfo.getAccount();
        if (TextUtils.isEmpty(account)) {
            showToast(getString(R.string.tip_empty_account_name));
            return;
        }
        final RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        send(Constant.PK_QUERYACCOUNT_RIGHT, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        AccountManagementActivity.this, "请稍后...");
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (responseInfo.statusCode == 200) {
                    BaseResult<AccountManagementInfo> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<AccountManagementInfo>>() {
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
                            if (result.data.getRightList() != null) {
                                if (result.data.getRightList().isEmpty()) {
                                    return;
                                }
                                if (adapter == null) {
                                    adapter = new BaseListAdapter<PermissionInfo>(getBaseContext(), result.data.getRightList(), R.layout.grid_item_account_permission, null);
                                    gridViewAccountPermission.setAdapter(adapter);
                                    gridViewAccountPermission.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            BaseListAdapter<PermissionInfo> adapter = (BaseListAdapter<PermissionInfo>) parent.getAdapter();
                                            PermissionInfo permissionInfo = adapter.getItem(position);
                                            if (permissionInfo == null) {
                                                return;
                                            }
                                            if (permissionInfo.getIsEdit().equals("0")) {
                                                showToast(getString(R.string.tip_no_permission));
                                            } else {
                                                if (permissionInfo.getIsOpen().equals("1")) {
                                                    permissionInfo.setIsOpen("0");
                                                    adapter.notifyDataSetChanged();
                                                } else if (permissionInfo.getIsOpen().equals("0")) {
                                                    permissionInfo.setIsOpen("1");
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    adapter.update(result.data.getRightList());
                                }
                            }
                        }
                    } else {
                        showToast(result.getMsg());
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
