package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

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
    @ViewInject(R.id.edAccountLayout)
    private LinearLayout edAccountLayout;
    private Dialog mDialog;
    private BaseListAdapter<PermissionInfo> adapter;
    private AccountInfo editAccountInfo;
    public static final int MODE_ADD = 0;   //添加账户
    public static final int MODE_EDIT = 1;  //编辑账户
    private int currentMode = MODE_ADD; //默认是添加账户页面
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    checkAccount();
                    break;
            }
        }
    };

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
                if (currentMode == MODE_ADD) {
                    addAccount();
                } else {
                    modifyAccount();
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.KEY_MODE)) {
                currentMode = bundle.getInt(Constant.KEY_MODE);
            }
            if (bundle.containsKey(Constant.KEY_ACCOUNT_SETTING_INFO)) {
                editAccountInfo = (AccountInfo) bundle.getSerializable(Constant.KEY_ACCOUNT_SETTING_INFO);
            }
        }
        if (currentMode == MODE_EDIT) {
            if (edAccountLayout != null) {
                edAccountLayout.setVisibility(View.GONE);
            }
            if (edPassword != null) {
                if (editAccountInfo != null) {
                    edPassword.setText(editAccountInfo.getPassword());
                }
                edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            edPassword.setText("");
                        }
                    }
                });
            }
        } else {
            if (edAccountLayout != null) {
                edAccountLayout.setVisibility(View.VISIBLE);
            }
            edAccount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 0) {
                        imgPasswordPass.setVisibility(View.INVISIBLE);
                    } else {
                        imgPasswordPass.setVisibility(View.INVISIBLE);
                        handler.removeMessages(0);
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                }
            });
            edAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        checkAccount();
                    }
                }
            });
        }
    }

    @OnClick(R.id.imgPasswordPass)
    public void onImgClick(View v) {
        String msg = (String) v.getTag();
        if (!TextUtils.isEmpty(msg)) {
            showToast(msg);
        }
    }

    /**
     * 获取账户权限
     */
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
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        if (currentMode == MODE_EDIT) {
            if (editAccountInfo != null) {
                params.addQueryStringParameter(Constant.InterfaceParam.EDITACCOUNT, editAccountInfo.getAccount());
            }
        }
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

    /**
     * 新增账号
     */
    private void addAccount() {
        String newAccountStr = edAccount.getText().toString().trim();
        String newAccountPwd = edPassword.getText().toString().trim();
        String remarkStr = edRemark.getText().toString().trim();
        if (TextUtils.isEmpty(newAccountStr)) {
            showToast(getString(R.string.tip_enter_sub_name));
            return;
        }
        if (TextUtils.isEmpty(newAccountPwd)) {
            showToast(getString(R.string.tip_enter_sub_password));
            return;
        }
        List<PermissionInfo> dataList = null;
        if (adapter != null) {
            dataList = adapter.getDataList();
        }
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
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        params.addQueryStringParameter(Constant.InterfaceParam.NEWACCOUNT, newAccountStr);
        params.addQueryStringParameter(Constant.InterfaceParam.PASSWORD, newAccountPwd);
        params.addQueryStringParameter(Constant.InterfaceParam.REMARK, remarkStr);
        params.addQueryStringParameter(Constant.InterfaceParam.IDS, gson.toJson(dataList));
        send(Constant.PK_ADDACCOUNT, params, new RequestCallBack<String>() {

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
                    BaseResult result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    showToast(result.getMsg());
                    if ("0000".equals(result.code)) {
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

    /**
     * 校验用户是否存在
     */
    private void checkAccount() {
        String account = edAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        send(Constant.PK_CHECKACCOUNT, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    BaseResult result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    if ("0000".equals(result.code)) {
                        imgPasswordPass.setVisibility(View.VISIBLE);
                        imgPasswordPass.setImageResource(R.mipmap.ic_account_pass);
                    } else {
                        imgPasswordPass.setVisibility(View.VISIBLE);
                        imgPasswordPass.setImageResource(R.mipmap.no_need_pay);
                        imgPasswordPass.setTag(result.getMsg());
                        showToast(result.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                fail(error, msg);
            }
        });
    }

    /**
     * 修改账户信息
     */
    private void modifyAccount() {
        if (editAccountInfo == null) {
            return;
        }
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
        String passwordStr = edPassword.getText().toString().trim();
        String remarkStr = edRemark.getText().toString().trim();
        if (TextUtils.isEmpty(passwordStr)) {
            showToast(getString(R.string.tip_enter_sub_password));
            return;
        }
        List<PermissionInfo> dataList = null;
        if (adapter != null) {
            dataList = adapter.getDataList();
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        params.addQueryStringParameter(Constant.InterfaceParam.PASSWORD, passwordStr);
        params.addQueryStringParameter(Constant.InterfaceParam.ID, editAccountInfo.getId());
        params.addQueryStringParameter(Constant.InterfaceParam.IDS, gson.toJson(dataList));
        params.addQueryStringParameter(Constant.InterfaceParam.REMARK, remarkStr);
        send(Constant.PK_UPDATEACCOUNT, params, new RequestCallBack<String>() {

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
                    BaseResult result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    showToast(result.getMsg());
                    if ("0000".equals(result.code)) {
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
