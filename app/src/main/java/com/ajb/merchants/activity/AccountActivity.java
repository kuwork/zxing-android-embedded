package com.ajb.merchants.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyListView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends BaseActivity {
    @ViewInject(R.id.tvAccountName)
    TextView tvAccountName;
    @ViewInject(R.id.scoreLayout)
    LinearLayout scoreLayout;
    @ViewInject(R.id.tvIntegration)
    TextView tvIntegration;
    @ViewInject(R.id.imgAvator)
    ImageView imgAvator;
    @ViewInject(R.id.tvRegistration)
    TextView tvRegistration;
    @ViewInject(R.id.listview)
    MyListView menuListView;
    private String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        initTitle("我的账户");
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenu();//初始化菜单,显示余额
        initAccountInfo();
        initBroadcast();//监听账户信息变化
        updateAccountInfo(getAccountInfo());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initAccountInfo() {
        if (isLogin()) {
            //用户名不要全局变量
            accountName = getSharedFileUtils().getString(SharedFileUtils.LOGIN_NAME);
            if (TextUtils.isEmpty(accountName)) {
                tvAccountName.setText(R.string.error_hava_not_login);
                tvAccountName.setTag(null);
            } else {
                tvAccountName.setText(CommonUtils.omittText(accountName, 3, 3));
                tvAccountName.setTag(accountName);
                getAccoutInfo(accountName);
            }
        } else {
            tvAccountName.setText(R.string.error_hava_not_login);
            tvAccountName.setTag(null);
        }
    }

    @OnClick(R.id.tvRegistration)
    public void onRegistrationClick(View v) {//签到
        AccountInfo accountInfo = getAccountInfo();
        if (accountInfo == null) {
            return;
        }
        if ("0".equals(accountInfo.getIsSign())) {// 可以签到
            checkIn();
        }
    }


    // 获取用户详细信息
    public void getAccoutInfo(String userName) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.USERNAME, userName);
        send(Constant.APPGETRENEWINFO, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                if (responseInfo.statusCode == 200) {
                    try {
                        BaseResult<AccountInfo> r = gson.fromJson(responseInfo.result, new TypeToken<BaseResult<AccountInfo>>() {
                        }.getType());
                        if ("0000".equals(r.code) && r.data != null) {
                            sharedFileUtils.putString(SharedFileUtils.ACCOUNT_INFO, ObjectUtil.getBASE64String(r.data));
                            Intent intent = new Intent(Constant.BROADCAST.ACCOUNT_INFO);
                            intent.putExtra(Constant.KEY_ACCOUNT_INFO, r.data);
                            sendBroadcast(intent);
                        } else {
                            showToast(getString(R.string.error_network_short));
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_network_short));
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d(msg);
                showToast(getString(R.string.error_network_short));
            }
        });
    }

    private void updateBalance(String balance) {
        if (menuListView != null && menuListView.getAdapter() != null && menuListView.getCount() > 0) {
//            MenuItemAdapter adapter = (MenuItemAdapter) menuListView.getAdapter();
//            adapter.setMenuDesc(MenuInfo.TO_MYBURSE, TextUtils.isEmpty(balance) ? "" : balance + "元");
        }
    }

    // 签到
    public void checkIn() {
        String accountName = null;
        if (isLogin()) {
            //用户名不要全局变量
            accountName = getSharedFileUtils().getString(SharedFileUtils.LOGIN_NAME);
        }
        if (TextUtils.isEmpty(accountName)) {
            showToast("账户信息无效，签到失败");
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.USERNAME, accountName);
        send(Constant.RENEWSINGNIN, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                if (responseInfo.statusCode == 200) {
                    try {
                        BaseResult<AccountInfo> r = gson.fromJson(responseInfo.result, new TypeToken<BaseResult<AccountInfo>>() {
                        }.getType());
                        if (r != null) {
                            if ("0000".equals(r.code) && r.data != null) {
                                AccountInfo accountInfo = getAccountInfo();
                                if (!TextUtils.isEmpty(r.data.getIntegral()) && accountInfo != null) {
                                    accountInfo.setIntegral(r.data.getIntegral());
                                    accountInfo.setIsSign("1");
                                    saveAndNoticeAccountInfoChange(accountInfo);
                                }
                            } else {
                                showToast(r.msg);
                            }
                        } else {
                            showToast(getString(R.string.error_network_short));
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_network_short));
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d(msg);
                showToast(getString(R.string.error_network_short));
            }
        });
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        ModularMenu modularMenu;
        String menuJson = CommonUtils.getFromAssets(getBaseContext(), "account.json");
        if (menuJson.equals("")) {
            return;
        }
        try {
            Gson gson = new Gson();
            List<ModularMenu> modularMenuList = gson.fromJson(menuJson, new TypeToken<List<ModularMenu>>() {
            }.getType());
            if (modularMenuList == null) {
                return;
            }
            int size = modularMenuList.size();
            for (int i = 0; i < size; i++) {
                modularMenu = modularMenuList.get(i);
                if (ModularMenu.CODE_ACCOUNT.equals(modularMenu.getModularCode())) {
                    MenuItemAdapter<MenuInfo> adapter = new MenuItemAdapter<>(getBaseContext(), dealListData(modularMenu.getMenuList()), modularMenu.getModularCode());
                    menuListView.setAdapter(adapter);
                }
            }
            menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MenuItemAdapter adapter = (MenuItemAdapter) parent.getAdapter();
                    Object item = adapter.getItem(position);
                    if (item instanceof MenuInfo) {
                        MenuInfo menuInfo = ((MenuInfo) item);
                        menuInfo.click(AccountActivity.this);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<MenuInfo> dealListData(List<MenuInfo> menuList) {
        List<MenuInfo> list = new ArrayList<>();
        int groupId = 0;
        MenuInfo menu = null;
        for (int i = 0; i < menuList.size(); i++) {
            menu = menuList.get(i);
            if (MenuInfo.TYPE_NORMAL.equals(menu.getType())) {

                if (groupId != menu.getGroupId()) {
                    MenuInfo menuInfo = new MenuInfo();
                    menuInfo.setType(MenuInfo.TYPE_DIVIDE);
                    list.add(menuInfo);
                    groupId = menuList.get(i).getGroupId();
                }
                list.add(menuList.get(i));
            } else if (MenuInfo.TYPE_SEPARATOR.equals(menuList.get(i).getType())) {
                list.add(menuList.get(i));
            }

        }
        if (list.size() > 0) {
            MenuInfo menuInfo = new MenuInfo();
            menuInfo.setType(MenuInfo.TYPE_DIVIDE);
            list.add(list.size(), menuInfo);
        }
        return list;
    }

    @Override
    protected void updateAccountInfo(AccountInfo info) {
        super.updateAccountInfo(info);
        if (info == null) {
            return;
        }
        if (tvIntegration != null) {
            tvIntegration.setText(TextUtils.isEmpty(info.getIntegral()) ? "0.0" : info.getIntegral());
        }
        if (tvRegistration != null) {
            if ("1".equals(info.getIsSign())) {
                tvRegistration.setText(R.string.tip_already_checkin);
            }
            tvRegistration.setVisibility(View.VISIBLE);
        }
        updateBalance(info.getBalance());
    }
}
