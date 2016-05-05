package com.ajb.merchants.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.PopupBannerAdapter;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.util.DensityUtil;
import com.util.ObjectUtil;

import java.util.List;

/**
 * 广告页
 */
public class PopupWindowBannerActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ImageView close_icon;
    private SharedFileUtils sharedFileUtils;
    private List<AdInfo> dataList;
    private ViewPager viewPager;
    private ImageView[] circlePoints;
    private LinearLayout popup_banner_circle_groups;
    private PopupBannerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_banner);
        initData();
        getLocalShareFileData();
        initCirclePoint();
    }

    private void initData() {
        close_icon = (ImageView) this.findViewById(R.id.pop_home_banner_close_icon);
        viewPager = (ViewPager) this.findViewById(R.id.pop_banner_viewpager);
        popup_banner_circle_groups = (LinearLayout) this.findViewById(R.id.popup_banner_circle_groups);
        sharedFileUtils = new SharedFileUtils(getApplicationContext());
        close_icon.setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * 获取本地shareFileUtils的数据
     */
    private void getLocalShareFileData() {
        String dataString = sharedFileUtils.getString(SharedFileUtils.BANNER_LIST_LOCAL_HOME_ACTION);
        LogUtils.d("--dataString->>" + dataString);
        if (dataString == null || dataString.equals("")) {
            //默认图片链接
            AdInfo adInfo = new AdInfo();
            adInfo.setType(1);
            adInfo.setLink("http://www.ajbcloud.com/static/products.html");
            adInfo.setRes(R.mipmap.splash_pic);
            dataList.add(adInfo);
        } else {
            dataList = (List<AdInfo>) ObjectUtil.getObject(dataString);
            if (dataList == null || dataList.isEmpty()) {
                //默认图片链接
                AdInfo adInfo = new AdInfo();
                adInfo.setType(1);
                adInfo.setLink("http://www.ajbcloud.com/static/products.html");
                adInfo.setRes(R.mipmap.splash_pic);
                dataList.add(adInfo);
            }
        }
        adapter = new PopupBannerAdapter(dataList, getApplicationContext(), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupBannerAdapter.ViewHolder viewHolder = (PopupBannerAdapter.ViewHolder) v.getTag();
                if (viewHolder != null && viewHolder.adInfo != null && !TextUtils.isEmpty(viewHolder.adInfo.getLink())) {
                    Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
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
    }

    /**
     * 初始化banner圆点
     */
    private void initCirclePoint() {
        circlePoints = new ImageView[dataList.size()];
        // 广告栏的小圆点图标
        for (int i = 0; i < dataList.size(); i++) {
            // 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(DensityUtil.dp2px(getApplicationContext(), 3), 0,
                    0, 0);
            imageView.setLayoutParams(params);
            circlePoints[i] = imageView;
            // 初始值, 默认第0个选中
            if (i == 0) {
                circlePoints[i].setBackgroundResource(R.drawable.circle_choose);
            } else {
                circlePoints[i].setBackgroundResource(R.drawable.circle_unchoose);
            }
            // 将小圆点放入到布局中
            popup_banner_circle_groups.addView(circlePoints[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < circlePoints.length; i++) {
            if (i == position) {
                circlePoints[i].setBackgroundResource(R.drawable.circle_choose);
            } else {
                circlePoints[i].setBackgroundResource(R.drawable.circle_unchoose);
            }
        }
    }

    //有三种状态（0，1，2）。state== 1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做。
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_home_banner_close_icon:
                finish();
                overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}

