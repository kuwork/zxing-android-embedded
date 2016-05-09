package com.ajb.merchants.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.task.BlurBitmapTask;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

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
        BaseListAdapter<BalanceLimitInfo> balanceLimitInfoAdapter = new BaseListAdapter<BalanceLimitInfo>(getActivity(), balanceLimitInfoList, R.layout.balance_limit_item, null);
        balanceGridView.setAdapter(balanceLimitInfoAdapter);
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
        initLeftMenu();
        return v;
    }

    /**
     * 初始化侧滑菜单
     */
    private void initLeftMenu() {
        ModularMenu modularMenu;
        String menuJson = CommonUtils.getFromAssets(getActivity(), "main_menu.json");
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
            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            };
            int size = modularMenuList.size();
            for (int i = 0; i < size; i++) {
                modularMenu = modularMenuList.get(i);
                if (ModularMenu.CODE_COUPON.equals(modularMenu.getModularCode())) {
                    initCouponMenuList(modularMenu, onItemClickListener);
                } else if (ModularMenu.CODE_MAIN_MENU.equals(modularMenu.getModularCode())) {
                    initMainMenuList(modularMenu, onItemClickListener);
                }
            }
//            menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    MenuItemAdapter adapter = (MenuItemAdapter) parent.getAdapter();
//                    Object item = adapter.getItem(position);
//                    if (item instanceof MenuInfo) {
//                        MenuInfo menuInfo = ((MenuInfo) item);
//                        if (menuInfo.isNeedLogin()) {
//                            if (!isLogin()) {
//                                showToast("请先登陆");
//                                startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
//                                return;
//                            }
//                            if (MenuInfo.TO_SOCIETYSHARE.equals(menuInfo.getMenuCode()) && MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
//                                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                                    drawer.closeDrawer(GravityCompat.START);
//                                }
//                                openSharePopWindow();
//                            } else {
//                                menuInfo.click(HomePageActivity.this);
//                            }
//                        } else {
//                            switch (menuInfo.getMenuCode()) {
//                                case MenuInfo.TO_EXIT://退出掌停宝
//                                    finish();
//                                    break;
//                                default:
//                                    menuInfo.click(HomePageActivity.this);
//                                    break;
//
//                            }
//                        }
//                    }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initCouponMenuList(ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (couponMenuListAdapter == null) {
            couponMenuListAdapter = new MenuItemAdapter<MenuInfo>(getActivity(), mm.getMenuList(), mm.getModularCode());
            couponGridView.setAdapter(couponMenuListAdapter);
        } else {
            couponMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
    }

    protected void initMainMenuList(ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (mainMenuListAdapter == null) {
            mainMenuListAdapter = new MenuItemAdapter<MenuInfo>(getActivity(), mm.getMenuList(), mm.getModularCode());
            menuGridView.setAdapter(mainMenuListAdapter);
        } else {
            mainMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
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

}
