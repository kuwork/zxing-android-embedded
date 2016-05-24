package com.ajb.merchants.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.HomePageActivity;
import com.ajb.merchants.activity.LoginActivity;
import com.ajb.merchants.activity.MerchantDetailActivity;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.task.BlurBitmapTask;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.ajb.merchants.view.RoundedImageView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
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
    @ViewInject(R.id.imgHeaderBg)
    public ImageView imgHeaderBg;
    @ViewInject(R.id.imgAvatar)
    public RoundedImageView imgAvatar;
    @ViewInject(R.id.tvStoreName)
    public TextView tvStoreName;
    private View picPickView;
    private PopupWindow picPickPopwindow;


    //优惠方式
    private MenuItemAdapter<MenuInfo> couponMenuListAdapter;
    //功能菜单
    private MenuItemAdapter<MenuInfo> mainMenuListAdapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private BlurBitmapTask blurBitmapTask;
    private BaseListAdapter<BalanceLimitInfo> balanceLimitInfoAdapter;

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
                if (getActivity() instanceof HomePageActivity) {
                    ((HomePageActivity) getActivity()).getAccoutInfo();
                } else {
                    swipeLayout.setRefreshing(false);
                }
            }
        });
        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.holo_blue_bright),
                ContextCompat.getColor(getActivity(), R.color.holo_green_light),
                ContextCompat.getColor(getActivity(), R.color.holo_orange_light),
                ContextCompat.getColor(getActivity(), R.color.holo_red_light));

        List<BalanceLimitInfo> balanceLimitInfoList = Arrays.asList(
                new BalanceLimitInfo("", "元", "可赠金额"),
                new BalanceLimitInfo("0.0", "", "可赠时间"),
                new BalanceLimitInfo("0.0", "元", "当月已赠金额"),
                new BalanceLimitInfo("0.0", "小时", "当月已赠时间")
        );

        initBalance(balanceLimitInfoList);
        return v;
    }

    public void initHeaderBg(Context context, String url) {
        if (imgHeaderBg == null) {
            return;
        }
        BitmapUtils bitmapUtils = new BitmapUtils(context);
        String s = (String) imgHeaderBg.getTag();
        if (s != null && s.equals(url)) {
            return;
        }
        bitmapUtils.display(imgHeaderBg, url, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                container.setTag(uri);
                if (blurBitmapTask != null && !blurBitmapTask.isCancelled()) {
                    blurBitmapTask.cancel(true);
                }
                blurBitmapTask = new BlurBitmapTask(getActivity()) {
                    @Override
                    protected void onPostExecute(List<Bitmap> list) {
                        super.onPostExecute(list);
                        if (list.size() > 0) {
                            imgHeaderBg.setImageBitmap(list.get(0));
                        }
                    }
                };
                blurBitmapTask.execute(bitmap);
            }

            @Override
            public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
                container.setImageResource(R.mipmap.header_bg);
            }
        });
    }

    public void initBalance(List<BalanceLimitInfo> balanceLimitInfoList) {
        if (balanceLimitInfoAdapter == null) {
            balanceLimitInfoAdapter = new BaseListAdapter<BalanceLimitInfo>(getActivity(), balanceLimitInfoList, R.layout.balance_limit_item, null);
            balanceGridView.setAdapter(balanceLimitInfoAdapter);
        } else {
            balanceLimitInfoAdapter.update(balanceLimitInfoList);
        }
    }

    public void initCouponMenuList(Context context, ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (couponGridView == null) {
            return;
        }
        if (couponMenuListAdapter == null) {
            couponMenuListAdapter = new MenuItemAdapter<MenuInfo>(context, mm.getMenuList(), mm.getModularCode());
            couponGridView.setAdapter(couponMenuListAdapter);
        }
        if (mm != null) {
            couponMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
        couponGridView.setOnItemClickListener(listener);
    }

    public void initMainMenuList(Context context, ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (menuGridView == null) {
            return;
        }
        if (mainMenuListAdapter == null) {
            mainMenuListAdapter = new MenuItemAdapter<MenuInfo>(context, mm.getMenuList(), mm.getModularCode());
            menuGridView.setAdapter(mainMenuListAdapter);
        }
        if (mm != null) {
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

    @OnClick(R.id.imgHeaderBg)
    public void onImgHeaderBgClick(View v) {
        if (!isLogin()) {
            showToast("请先登陆");
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
            return;
        }
        initPicPickPopWindow();
    }

    /**
     * 更换相册封面Popwindow
     */
    private void initPicPickPopWindow() {
        if (picPickView == null || picPickPopwindow == null) {
            picPickView = getActivity().getLayoutInflater().inflate(R.layout.popup_pic_pick, null);
            picPickPopwindow = new PopupWindow(picPickView);
            picPickPopwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            picPickPopwindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            picPickPopwindow.setAnimationStyle(R.style.AnimationFade);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvPick:
                        break;
                    default:
                        picPickPopwindow.dismiss();
                        break;
                }
            }
        };
        LinearLayout emptyLayout = (LinearLayout) picPickView.findViewById(R.id.emptyLayout);
        TextView tvPick = (TextView) picPickView.findViewById(R.id.tvPick);
        TextView tvCancel = (TextView) picPickView.findViewById(R.id.tvCancel);
        emptyLayout.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
        tvPick.setOnClickListener(onClickListener);
        tvPick.setText("更换相册封面");
        picPickPopwindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
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

    public void setRefreshing(boolean refreshing) {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(refreshing);
        }
    }
}
