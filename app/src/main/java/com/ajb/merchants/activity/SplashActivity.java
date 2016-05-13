package com.ajb.merchants.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.SplashBannerAdapter;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.model.AdResult;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.util.CarLocation;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.util.download.DownloadManager;
import com.ajb.merchants.util.download.DownloadService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.util.App;
import com.util.DensityUtil;
import com.util.ObjectUtil;
import com.util.PathManager;
import com.util.SHA1Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Splash界面
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private List<AdInfo> dataList;//装载viewpager数据的对象
    private SplashBannerAdapter adapter;
    // banner圆点
    private ImageView[] circlePoints;
    private TimeCount timeCount;
    private DownloadManager manager;
    private File bannerSplashFolder;    //保存欢迎页广告的图片的文件夹
    private File bannerHomeFolder;      //保存首页广告图片的文件夹
    private File bannerHomeSlideFolder;      //保存首页广告图片的文件夹
    private boolean canStart;
    @ViewInject(R.id.footLayout)
    LinearLayout footLayout;
    @ViewInject(R.id.splash_viewpager)
    ViewPager splash_viewpager;
    @ViewInject(R.id.splash_circle_groups)
    LinearLayout splash_circle_groups;
    @ViewInject(R.id.splash_skip_btn)
    TextView splash_skip_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewUtils.inject(this);
        manager = DownloadService.getDownloadManager(SplashActivity.this);
        initWidget();
        createPhotoFileDir();
        initCirclePoint();
        getSplashBannerInfo();
        getHomeBannerInfo();
        getHomeSlideBannerInfo();
        getHomeActionBannerInfo();
        getPayBannerInfo();
    }

    private void initWidget() {
        String dataString = sharedFileUtils.getString(SharedFileUtils.BANNER_LIST_LOCAL_SPLASH);
        boolean isFirstIn = sharedFileUtils.getBoolean(SharedFileUtils.IS_FIRST_IN);//第一次为false
        if (!isFirstIn) {//首次进入
            canStart = false;
            footLayout.setVisibility(View.GONE);
            sharedFileUtils.putString(SharedFileUtils.BANNER_LIST_SPLASH, null);
            dataList = new ArrayList<AdInfo>();
            AdInfo adInfo = new AdInfo("", "", 1, "2", R.mipmap.splash_pic);
            dataList.add(adInfo);

            adInfo = new AdInfo("", "", 1, "2", R.mipmap.intro_1);
            dataList.add(adInfo);

            adInfo = new AdInfo("", "", 1, "2", R.mipmap.intro_2);
            dataList.add(adInfo);
            splash_skip_btn.setVisibility(View.GONE);
        } else {
            dataList = (List<AdInfo>) ObjectUtil.getObject(dataString);
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            if (dataList.isEmpty()) {      //当远程没有设置广告的时候就显示默认的
                AdInfo adInfo = new AdInfo("", "", 1, "2", R.mipmap.splash_pic);
                dataList.add(adInfo);
            }
            canStart = true;
        }
        adapter = new SplashBannerAdapter(dataList, getBaseContext(), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashBannerAdapter.ViewHolder viewHolder = (SplashBannerAdapter.ViewHolder) v.getTag();
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
        if (splash_viewpager != null) {
            splash_viewpager.setAdapter(adapter);
            splash_viewpager.setOnPageChangeListener(this);
            if (canStart) {
                timeCount = new TimeCount(3000, 200); //构造CountDownTimer对象(3s)
                timeCount.begin();
            }
        }

    }

    /**
     * 创建文件夹
     */
    private void createPhotoFileDir() {
        // 存储至DCIM文件夹
        bannerSplashFolder = new File(
                PathManager.getDiskFileDir(getBaseContext()) + File.separator + CarLocation.ZTB_BANNER_SPLASH);
        bannerHomeFolder = new File(
                PathManager.getDiskFileDir(getBaseContext()) + File.separator + CarLocation.ZTB_BANNER_HOME);
        bannerHomeSlideFolder = new File(
                PathManager.getDiskFileDir(getBaseContext()) + File.separator + CarLocation.ZTB_BANNER_HOME_SLIDE);

        if (!bannerSplashFolder.exists()) {
            bannerSplashFolder.mkdirs();
        }
        if (!bannerHomeFolder.exists()) {
            bannerHomeFolder.mkdirs();
        }
        if (!bannerHomeSlideFolder.exists()) {
            bannerHomeSlideFolder.mkdirs();
        }
    }

    /**
     * 初始化banner圆点
     */
    private void initCirclePoint() {
        if (dataList.size() > 1) {
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
                splash_circle_groups.addView(circlePoints[i]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.splash_skip_btn:
                toHome();
                break;

            default:
                break;
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
        //前提是新版本第一次进来
        if (!canStart) {
            if (position == dataList.size() - 1) {
                splash_skip_btn.setVisibility(View.VISIBLE);
                splash_skip_btn.setOnClickListener(this);
                splash_skip_btn.setText("进入");
            } else {
                splash_skip_btn.setVisibility(View.GONE);
            }
        }
    }

    //有三种状态（0，1，2）。state== 1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做。
    @Override
    public void onPageScrollStateChanged(int state) {
        if (timeCount == null) {
            return;
        }
        if (state == 2) {
            timeCount.begin();
        } else if (state == 1) {
            timeCount.cancel();
            splash_skip_btn.setText("3s");
        } else if (state == 0) {
            timeCount.begin();
        }
    }

    /**
     * 到主页
     */
    private void toHome() {
        sharedFileUtils.putBoolean(SharedFileUtils.IS_FIRST_IN, true);
        Intent intent = null;
        if (isLogin()) {
            intent = new Intent(getBaseContext(), HomePageActivity.class);
        } else {
            intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.putExtra(Constant.KEY_FROM, SplashActivity.class.getName());//标明是splash页面跳转
        }
        startActivity(intent);
        finish();
    }

    /**
     * 获取欢迎页面广告图片
     */
    private void getSplashBannerInfo() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.PRODUCTID, App.getMetaData(getBaseContext(), "product_app_id"));
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.AD_TYPE_SPLASH);
        send(Constant.APPQUREYAD, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    String remote = SharedFileUtils.BANNER_LIST_SPLASH;
                    String local = SharedFileUtils.BANNER_LIST_LOCAL_SPLASH;
                    dealWithDownload(responseInfo, remote, local);
                } else {
                    Log.i("Splash", "network error");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d(msg);
            }
        });
    }

    private void dealWithDownload(ResponseInfo<String> responseInfo, String remote, String local) {
        try {
            Gson gson = new Gson();
            BaseResult<AdResult> r = gson.fromJson(responseInfo.result, new TypeToken<BaseResult<AdResult>>() {
            }.getType());
            if (r == null || r.data == null || r.data.list == null) {
                return;
            }
            List<AdInfo> dataList = r.data.list;
            List<AdInfo> dataListLocal = new ArrayList<AdInfo>();
            String nowStr = gson.toJson(dataList);
            String nowTag = SHA1Util.sum(nowStr);
            String lastTag = SHA1Util.sum(sharedFileUtils.getString(remote));
            Log.i("Splash", "Splash_nowTag:" + nowTag);
            Log.i("Splash", "Splash_lastTag:" + lastTag);
            if (lastTag.equals(nowTag)) {
                return;
            }

            for (int i = 0; i < dataList.size(); i++) {
                AdInfo adInfo = dataList.get(i);
                String picUrl = adInfo.getUrl();
                String picName;
                if (TextUtils.isEmpty(picUrl) || picUrl.lastIndexOf(File.separator) == -1) {
                    continue;
                } else {
                    int index = picUrl.lastIndexOf(File.separator);
                    picName = picUrl.substring(index == picUrl.length() - 1 ? index : index + 1);
                }
                String picPath = bannerSplashFolder.getAbsolutePath() + (bannerSplashFolder.getAbsolutePath().endsWith(File.separator) ? "" : File.separator) + picName;
                adInfo.setUrl(picPath);
                dataListLocal.add(adInfo);
                manager.addNewDownload(picUrl, picName, picPath, true, false, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Log.i("Splash", "Splash:" + responseInfo.result.getAbsolutePath());

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Log.i("Splash", "Splash:" + msg);
                    }
                });
            }
            sharedFileUtils.putString(remote, nowStr);
            sharedFileUtils.putString(local, ObjectUtil.getBASE64String(dataListLocal));
        } catch (Exception e) {
            e.printStackTrace();
            sharedFileUtils.putString(remote, "");
            sharedFileUtils.putString(local, "");
        }
    }

    /**
     * 获取首页广告
     */
    private void getHomeBannerInfo() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.PRODUCTID, App.getMetaData(getBaseContext(), "product_app_id"));
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.AD_TYPE_HOME);
        send(Constant.APPQUREYAD, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    String remote = SharedFileUtils.BANNER_LIST_HOME;
                    String local = SharedFileUtils.BANNER_LIST_LOCAL_HOME;
                    dealWithDownload(responseInfo, remote, local);
                } else {
                    Log.i("Splash", "network error");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d(msg);
            }
        });

    }

    /**
     * 获取首页侧滑广告
     */
    private void getHomeSlideBannerInfo() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.PRODUCTID, App.getMetaData(getBaseContext(), "product_app_id"));
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.AD_TYPE_HOME_SLIDE_MENU);
        send(Constant.APPQUREYAD, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    String remote = SharedFileUtils.BANNER_LIST_HOME_SLIDE_MENU;
                    String local = SharedFileUtils.BANNER_LIST_LOCAL_HOME_SLIDE_MENU;
                    dealWithDownload(responseInfo, remote, local);
                } else {
                    Log.i("Splash", "network error");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d("failure->>" + msg);
            }
        });

    }

    /**
     * 获取首页活动弹窗广告
     */
    private void getHomeActionBannerInfo() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.PRODUCTID, App.getMetaData(getBaseContext(), "product_app_id"));
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.AD_TYPE_ACTION);
        send(Constant.APPQUREYAD, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    String remote = SharedFileUtils.BANNER_LIST_HOME_ACTION;
                    String local = SharedFileUtils.BANNER_LIST_LOCAL_HOME_ACTION;
                    dealWithDownload(responseInfo, remote, local);
                } else {
                    Log.i("Splash", "network error");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d("failure->>" + msg);
            }
        });

    }

    /**
     * 获取首页活动弹窗广告
     */
    private void getPayBannerInfo() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.PRODUCTID, App.getMetaData(getBaseContext(), "product_app_id"));
        params.addQueryStringParameter(Constant.InterfaceParam.TYPE, Constant.AD_TYPE_PAY);
        send(Constant.APPQUREYAD, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    String remote = SharedFileUtils.BANNER_LIST_PAY;
                    String local = SharedFileUtils.BANNER_LIST_LOCAL_PAY;
                    dealWithDownload(responseInfo, remote, local);
                } else {
                    Log.i("Splash", "network error");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d("failure->>" + msg);
            }
        });

    }

    /**
     * 倒计时
     */
    private class TimeCount extends CountDownTimer {

        /**
         * 参数依次为总时长,和计时的时间间隔
         *
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void begin() {
            splash_skip_btn.setText("3s");
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {  //计时过程显示
            splash_skip_btn.setText((int) Math.ceil(millisUntilFinished / 1000f) + "s");
        }

        @Override
        public void onFinish() { //计时完毕时触发
            splash_skip_btn.setText("进入");
            cancel();
            toHome();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeCount != null) {
            timeCount.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timeCount != null) {
            timeCount.begin();
        }
    }

}
