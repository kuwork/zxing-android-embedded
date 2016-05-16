package com.ajb.merchants.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.HomePageActivity;
import com.ajb.merchants.activity.WebViewActivity;
import com.ajb.merchants.adapter.BannerAdapter;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.viewpager)
    ViewPager viewPager;
    private List<AdInfo> dataList;
    private boolean canStart;
    private BannerAdapter adapter;
    private static final long SPLASH_DELAY_MILLIS = 5000;
    private final static int PAGE_START = 0;
    private final static int PAGE_NEXT = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAGE_START:
                    if (viewPager != null && adapter != null && adapter.getCount() > 0) {
                        viewPager.setCurrentItem(0);
                        LogUtils.d("回到首页");
                        handler.sendEmptyMessageDelayed(PAGE_NEXT, SPLASH_DELAY_MILLIS);
                    }
                    break;
                case PAGE_NEXT:
                    if (viewPager != null && adapter != null && adapter.getCount() > 0) {
                        int page = viewPager.getCurrentItem();
                        if (page == Integer.MAX_VALUE) {
                            handler.sendEmptyMessageDelayed(PAGE_START, SPLASH_DELAY_MILLIS);
                        } else {
                            LogUtils.d("前往页码:" + (page + 1));
                            viewPager.setCurrentItem(page + 1);
                            handler.sendEmptyMessageDelayed(PAGE_NEXT, SPLASH_DELAY_MILLIS);
                        }
                    }
                    break;
            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        handler.removeMessages(PAGE_START);
        handler.removeMessages(PAGE_NEXT);
        if (!hidden) {
            handler.sendEmptyMessageDelayed(PAGE_NEXT, SPLASH_DELAY_MILLIS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ViewUtils.inject(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");
        initWidget();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("onPause");
        handler.removeMessages(PAGE_START);
        handler.removeMessages(PAGE_NEXT);
    }

    private void initWidget() {
        String dataString = sharedFileUtils.getString(SharedFileUtils.BANNER_LIST_LOCAL_HOME);
        if (!TextUtils.isEmpty(dataString)) {
            dataList = (List<AdInfo>) ObjectUtil.getObject(dataString);
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            if (dataList.isEmpty()) {      //当远程没有设置广告的时候就显示默认的

                AdInfo adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc01);
                dataList.add(adInfo);
                adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc02);
                dataList.add(adInfo);
                adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc03);
                dataList.add(adInfo);
                adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc04);
                dataList.add(adInfo);
                adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc05);
                dataList.add(adInfo);
            }
        } else {
//            footLayout.setVisibility(View.GONE);
            sharedFileUtils.putString(SharedFileUtils.BANNER_LIST_SPLASH, null);
            dataList = new ArrayList<AdInfo>();
            AdInfo adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc01);
            dataList.add(adInfo);
            adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc02);
            dataList.add(adInfo);
            adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc03);
            dataList.add(adInfo);
            adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc04);
            dataList.add(adInfo);
            adInfo = new AdInfo("", "", 1, "2", R.mipmap.pc05);
            dataList.add(adInfo);

//            splash_skip_btn.setVisibility(View.GONE);
        }
        adapter = new BannerAdapter(dataList, getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAdapter.ViewHolder viewHolder = (BannerAdapter.ViewHolder) v.getTag();
                if (viewHolder != null && viewHolder.adInfo != null && !TextUtils.isEmpty(viewHolder.adInfo.getLink())) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_URL, viewHolder.adInfo.getLink());
                    bundle.putString(Constant.KEY_TITLE, viewHolder.adInfo.getName());
                    Log.i("Splash", "clickItem+" + viewHolder.adInfo.getLink());
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        if (dataList != null && dataList.size() > 1) {
            handler.removeMessages(PAGE_START);
            handler.removeMessages(PAGE_NEXT);
            handler.sendEmptyMessageDelayed(PAGE_NEXT, SPLASH_DELAY_MILLIS);
        }
    }


    @OnClick(value = {R.id.homeBtn01, R.id.homeBtn02, R.id.homeBtn03, R.id.homeBtn04, R.id.homeBtn05})
    public void onHomeBtnClick(View v) {
        switch (v.getId()) {
            case R.id.homeBtn01:
                ((HomePageActivity) getActivity()).launchApp(getActivity(), "com.doyao.discount");
                break;
            case R.id.homeBtn03:
                break;
            case R.id.homeBtn04:
                break;
            case R.id.homeBtn05:
                ((HomePageActivity) getActivity()).scan();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }


    //有三种状态（0，1，2）。state== 1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做。
    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
