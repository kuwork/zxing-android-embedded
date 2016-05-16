package com.ajb.merchants.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.LoginActivity;
import com.ajb.merchants.activity.MerchantDetailActivity;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.task.BlurBitmapTask;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Arrays;
import java.util.List;

public class MainFragment extends BaseFragment {

    @ViewInject(R.id.swipeLayout)
    MySwipeRefreshLayout swipeLayout;
    @ViewInject(R.id.balanceGridView)
    MyGridView balanceGridView;
    @ViewInject(R.id.couponGridView)
    MyGridView couponGridView;
    @ViewInject(R.id.menuGridView)
    MyGridView menuGridView;
    @ViewInject(R.id.imgHeaderBanner)
    ImageView imgHeaderBanner;
    private boolean isFirst = true;

    BlurBitmapTask blurBitmapTask = new BlurBitmapTask(getActivity()) {
        @Override
        protected void onPostExecute(List<Bitmap> list) {
            super.onPostExecute(list);
            if (list.size() > 0) {
                imgHeaderBanner.setImageBitmap(list.get(0));
            }
        }
    };
    //优惠方式
    private MenuItemAdapter<MenuInfo> couponMenuListAdapter;
    //功能菜单
    private MenuItemAdapter<MenuInfo> mainMenuListAdapter;
    private AdapterView.OnItemClickListener onItemClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemClickListener = (AdapterView.OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnItemClickListener");
        }
    }

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d("hidden=" + hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ViewUtils.inject(this, v);
        swipeLayout.setCanRefresh(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMainSetting();
            }
        });
        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.holo_blue_bright),
                getResources().getColor(R.color.holo_green_light),
                getResources().getColor(R.color.holo_orange_light),
                getResources().getColor(R.color.holo_red_light));

        List<BalanceLimitInfo> balanceLimitInfoList = Arrays.asList(
                new BalanceLimitInfo("", "元", "可赠金额"),
                new BalanceLimitInfo("0.0", "", "可赠时间"),
                new BalanceLimitInfo("0.0", "元", "当月已赠金额"),
                new BalanceLimitInfo("0.0", "小时", "当月已赠时间")
        );

        initBalance(balanceLimitInfoList);

        ViewTreeObserver viewTreeObserver = imgHeaderBanner.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isFirst) {
                    blurBitmapTask.execute(BitmapFactory.decodeResource(getResources(), R.mipmap.header_bg_01));
                    isFirst = false;
                }
                return true;
            }
        });
        String menuJson = CommonUtils.getFromAssets(getActivity(), "main_menu.json");
        String modelMenuJson = sharedFileUtils.getString(SharedFileUtils.ACCOUNT_SETING_INFO);
        if (!TextUtils.isEmpty(menuJson)) {
            try {
                AccountSettingInfo asi = gson.fromJson(modelMenuJson, new TypeToken<AccountSettingInfo>() {
                }.getType());
                if (asi != null) {
                    initBalance(asi.getBalanceList());
//                initLeftMenu(asi.getModularMenus());
                }
                List<ModularMenu> modularMenu = gson.fromJson(menuJson, new TypeToken<List<ModularMenu>>() {
                }.getType());

                initLeftMenu(modularMenu);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        requestMainSetting();
        return v;
    }

    private void initBalance(List<BalanceLimitInfo> balanceLimitInfoList) {
        BaseListAdapter<BalanceLimitInfo> balanceLimitInfoAdapter = new BaseListAdapter<BalanceLimitInfo>(getActivity(), balanceLimitInfoList, R.layout.balance_limit_item, null);
        balanceGridView.setAdapter(balanceLimitInfoAdapter);
    }

    private void requestMainSetting() {
        send(Constant.PK_MAIN_SETTING, null, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                swipeLayout.setRefreshing(false);
                if (responseInfo.statusCode == 200) {
                    BaseResult<AccountSettingInfo> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<AccountSettingInfo>>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    if ("0000".equals(result.code)) {
                        sharedFileUtils.putString(SharedFileUtils.ACCOUNT_SETING_INFO, gson.toJson(result.data));
//                        initLeftMenu(result.data.getModularMenus());
                        initBalance(result.data.getBalanceList());
                    } else {
                        showToast(result.msg);
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                swipeLayout.setRefreshing(false);
                    fail(error, msg);
            }

        });
    }

    /**
     * 初始化侧滑菜单
     */
    private void initLeftMenu(List<ModularMenu> modularMenuList) {
        ModularMenu modularMenu;
        if (modularMenuList == null) {
            return;
        }
        int size = modularMenuList.size();
        for (int i = 0; i < size; i++) {
            modularMenu = modularMenuList.get(i);
            if (ModularMenu.CODE_COUPON.equals(modularMenu.getModularCode())) {
                initCouponMenuList(modularMenu, onItemClickListener);
            } else if (ModularMenu.CODE_MAIN_MENU.equals(modularMenu.getModularCode())) {
                initMainMenuList(modularMenu, onItemClickListener);
            }
        }
    }

    protected void initCouponMenuList(ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (couponMenuListAdapter == null) {
            couponMenuListAdapter = new MenuItemAdapter<MenuInfo>(getActivity(), mm.getMenuList(), mm.getModularCode());
            couponGridView.setAdapter(couponMenuListAdapter);
        } else {
            couponMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
        couponGridView.setOnItemClickListener(listener);
    }

    protected void initMainMenuList(ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (mainMenuListAdapter == null) {
            mainMenuListAdapter = new MenuItemAdapter<MenuInfo>(getActivity(), mm.getMenuList(), mm.getModularCode());
            menuGridView.setAdapter(mainMenuListAdapter);
        } else {
            mainMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
        menuGridView.setOnItemClickListener(listener);
    }

    @OnClick(R.id.imgAvatar)
    public void onAvatarClick(View v) {
        if (!isLogin()) {
            showToast("请先登陆");
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
            return;
        }
        Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("onPause");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQ_CODE_LOGIN) {
            swipeLayout.setRefreshing(true);
            requestMainSetting();
        }
    }
}
