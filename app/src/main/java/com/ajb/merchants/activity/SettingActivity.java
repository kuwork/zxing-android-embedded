package com.ajb.merchants.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.fragment.FindCarPayFragment;
import com.ajb.merchants.fragment.HomeFragment;
import com.ajb.merchants.fragment.MapViewFragment;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.HomePageInfo;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 设置界面
 */
public class SettingActivity extends BaseActivity {

    @ViewInject(R.id.setting_listview)
    MyListView menuListView;
    @ViewInject(R.id.change_account_btn)
    LinearLayout change_account_btn;
    private RequestParams unBindChannelIDParams;
    private Dialog mDialog;
    private View contentView;
    private PopupWindow popup;
    private BaseListAdapter<HomePageInfo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ViewUtils.inject(this);
        initView();
        initMenu();
    }

    @Override
    public void onBackPressed() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        ModularMenu modularMenu;
        final String menuJson = CommonUtils.getFromAssets(getBaseContext(), "setting.json");
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
                if (ModularMenu.CODE_SETTING.equals(modularMenu.getModularCode())) {
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
                        if (MenuInfo.TO_CHECKUPDATE.equals(menuInfo.getMenuCode()) && MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
                            checkUpdate();
                        } else if (MenuInfo.TO_CLEARCACHE.equals(menuInfo.getMenuCode()) && MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
                            showOkCancelAlertDialog(true, "提示", "将清除本地缓存文件,是否继续?", "继续", "关闭", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    clearCacheFolder(getBaseContext().getCacheDir(), System.currentTimeMillis());
                                    clearBeforeExit(getBaseContext());
                                    dimissOkCancelAlertDialog();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dimissOkCancelAlertDialog();
                                }
                            });
                        } else if (MenuInfo.TO_PAGESETTING.equals(menuInfo.getMenuCode()) && MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
                            initPageSetting();
                        } else {
                            menuInfo.click(SettingActivity.this);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPageSetting() {
        if (contentView == null || popup == null) {
            contentView = getLayoutInflater().inflate(
                    R.layout.popup_carno_list, null);
            popup = new PopupWindow(contentView);
            popup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel_btn:
                        popup.dismiss();
                        break;
                    case R.id.sure_btn:
                        HomePageInfo homePageInfo = (HomePageInfo) v.getTag();
                        if (homePageInfo != null) {
                            sharedFileUtils.putString(SharedFileUtils.HOME_NAME, homePageInfo.getClassName());
                            showToast("设置成功，重启才会生效");
                        }
                        popup.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(
                onClickListener);
        contentView.findViewById(R.id.sure_btn).setOnClickListener(
                onClickListener);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.title);
        tvTitle.setText("请选择主页");
        ListView listView = (ListView) contentView.findViewById(R.id.listView);
        List<HomePageInfo> list = Arrays.asList(
                new HomePageInfo("主页菜单", HomeFragment.class.getSimpleName()),
                new HomePageInfo("附近车场", MapViewFragment.class.getSimpleName()),
                new HomePageInfo("找车缴费", FindCarPayFragment.class.getSimpleName())
        );
        if (adapter == null) {
            adapter = new BaseListAdapter<HomePageInfo>(getBaseContext(), list, R.layout.activity_carnumberpay_item, null);
            listView.setAdapter(adapter);
        } else {
            adapter.update(list);
        }
        String className = sharedFileUtils.getString(SharedFileUtils.HOME_NAME);
        adapter.setChecked(className);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getClassName().equals(className)) {
                contentView.findViewById(R.id.sure_btn).setTag(list.get(i));
                break;
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BaseListAdapter<HomePageInfo> adapter = (BaseListAdapter<HomePageInfo>) parent.getAdapter();
                HomePageInfo page = (HomePageInfo) adapter.getItem(position);
                if (!TextUtils.isEmpty(page.getClassName())) {
                    adapter.setChecked(page.getClassName());
                    contentView.findViewById(R.id.sure_btn).setTag(page);
                }
            }
        });
        if (!popup.isShowing()) {
            popup.showAtLocation(getWindow().getDecorView(),
                    Gravity.CENTER, 0, 0);
        }
    }

    private List<MenuInfo> dealListData(List<MenuInfo> menuList) {
        List<MenuInfo> list = new ArrayList<>();
        int groupId = 0;

        for (int i = 0; i < menuList.size(); i++) {
            if (MenuInfo.TYPE_NORMAL.equals(menuList.get(i).getType())) {
                if (groupId != menuList.get(i).getGroupId()) {
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
    protected void warnUser() {
        if (menuListView == null || menuListView.getAdapter() == null || menuListView.getAdapter().getCount() == 0) {
            return;
        }
        MenuItemAdapter<MenuInfo> adapter = (MenuItemAdapter<MenuInfo>) menuListView.getAdapter();
        adapter.removeAllDots();//清除原有状态
        if (App.getVersionCode(getBaseContext()) < sharedFileUtils
                .getInt(SharedFileUtils.LAST_CHECK_NEWEST_VERSION_CODE)) {
            adapter.addDots(MenuInfo.TO_CHECKUPDATE);
        }
    }

    private void initView() {
        initTitle("设置");
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isLogin()) {
            change_account_btn.setVisibility(View.VISIBLE);
        } else {
            change_account_btn.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.change_account_btn)
    public void onLoginOutClick(View v) {
//        // 退出登陆
//        sharedFileUtils.putBoolean("isLogin", false);
//        finish();
//        // 设置切换动画，从右边进入，左边退出
//        overridePendingTransition(R.anim.push_right_in,
//                R.anim.push_left_out);
        // 设备绑定
        unBindChannelIDParams = new RequestParams();
        unBindChannelIDParams.addQueryStringParameter(
                Constant.InterfaceParam.USERNAME,
                sharedFileUtils.getString("LoginName"));
        unBindChannelIDParams.addQueryStringParameter(
                Constant.InterfaceParam.CHANNELID,
                JPushInterface.getRegistrationID(getBaseContext()));
        unBindChannelID();
    }


    private void unBindChannelID() {
        if (unBindChannelIDParams == null) {
            return;
        }
        send(Constant.APPDELCHANNELID, unBindChannelIDParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        mDialog = MyProgressDialog.createLoadingDialog(
                                SettingActivity.this, "正在注销,请稍后...");
                        mDialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (responseInfo.statusCode == 200) {
                            Gson gson = new Gson();
                            BaseResult<String> result = gson.fromJson(
                                    responseInfo.result,
                                    new TypeToken<BaseResult<String>>() {
                                    }.getType());
                            if ("0000".equals(result.code)) {
                                LogUtils.d("设备解绑成功");
                                sharedFileUtils.putBoolean("isLogin", false);
                                finish();
                                // 设置切换动画，从右边进入，左边退出
                                overridePendingTransition(R.anim.push_right_in,
                                        R.anim.push_left_out);
                            } else {
                                LogUtils.e("设备解绑失败:" + result.msg);
                                showToast(getString(R.string.error_network_short));
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        showToast(getString(R.string.error_network_short));
                        LogUtils.e("设备解绑失败:" + error.getExceptionCode() + ","
                                + msg);
                    }
                });

    }


}
