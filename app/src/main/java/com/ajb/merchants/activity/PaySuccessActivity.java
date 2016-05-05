package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.PopupBannerAdapter;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.PaySuccess;
import com.ajb.merchants.model.ShareInfo;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.DateConvertor;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyListView;
import com.ajb.merchants.view.ScaleFrameLayout;
import com.ajb.merchants.wxapi.WXPayEntryActivity;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.DensityUtil;
import com.util.ObjectUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

public class PaySuccessActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.tv_pay_success)
    TextView TvPaySuccess;
    @ViewInject(R.id.time_money_tip)
    TextView TvTimeMoneyTip;
    @ViewInject(R.id.pay_detail_lv)
    MyListView payDetailListView;
    @ViewInject(R.id.pay_success_pic)
    ImageView paySuccessPic;
    @ViewInject(R.id.pop_banner_viewpager)
    ViewPager viewPager;
    @ViewInject(R.id.popup_banner_circle_groups)
    LinearLayout popup_banner_circle_groups;
    @ViewInject(R.id.close_layout)
    LinearLayout closeLayout;
    @ViewInject(R.id.banner_layout)
    ScaleFrameLayout bannerLayout;
    @ViewInject(R.id.share_btn)
    LinearLayout shareBtn;
    private Dialog mDialog;
    private String orderNo; //订单id或者微信prepayId
    private PaySuccess paySuccess;
    private TimeCount timeCount;
    private long interfaceBackTime;
    private long allParkTime;
    private List<Info> infoList;
    private BaseListAdapter<Info> infoAdapter;
    private List<AdInfo> dataList;
    private ImageView[] circlePoints;
    private PopupBannerAdapter adapter;
    private PopupWindow sharePopWindow;
    private ShareInfo shareInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ViewUtils.inject(this);
        ShareSDK.initSDK(this);
        initTitle("缴费成功");
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_refresh, "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quaryOrderInfo();
            }
        });
    }

    /**
     * 查询订单信息
     */
    private void quaryOrderInfo() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey(Constant.InterfaceParam.ORDERNO)) {
            orderNo = bundle.getString(Constant.InterfaceParam.ORDERNO);
        } else {
            orderNo = WXPayEntryActivity.PREPAYID;
        }
        if (TextUtils.isEmpty(orderNo)) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ORDERNO, orderNo);
        send(Constant.APPORDERDETAIL, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mDialog = MyProgressDialog.createLoadingDialog(PaySuccessActivity.this, "请稍后...");
                mDialog.setCancelable(false);
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                try {
                    BaseResult<PaySuccess> r = gson.fromJson(responseInfo.result,
                            new TypeToken<BaseResult<PaySuccess>>() {
                            }.getType());
                    if (r != null && r.data != null) {
                        interfaceBackTime = System.currentTimeMillis();
                        paySuccess = r.data;
                        shareInfo = r.data.getNetShare();
                        if (null != shareInfo) {
                            shareBtn.setVisibility(View.VISIBLE);
                            initSharePopWindow();
                        } else {
                            shareBtn.setVisibility(View.GONE);
                        }
                        String tradeState = paySuccess.getTradeState();
                        TvPaySuccess.setText(paySuccess.getTradeMsg());
                        TvTimeMoneyTip.setText(paySuccess.getNextPayMsg());
                        if ("0".equals(tradeState)) {
                            paySuccessPic.setImageResource(R.mipmap.pay_success);
                            infoList = paySuccess.getInfo();
                            if (null != infoList) {
                                infoAdapter = new BaseListAdapter<Info>(getBaseContext(), infoList, R.layout.pay_listview_detail_item, null);
                                payDetailListView.setAdapter(infoAdapter);
                            }
                            try {
                                Timestamp carInDate = DateConvertor.getTimestamp(paySuccess.getCarInTime());   //车辆进场时间
                                Timestamp carCalcDate = DateConvertor.getTimestamp(paySuccess.getNowDate());   //车辆计费时间
                                allParkTime = carCalcDate.getTime() - carInDate.getTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!TextUtils.isEmpty(paySuccess.getNextPayTime())) {
                                long nextPayTime = Long.valueOf(paySuccess.getNextPayTime());
                                if (nextPayTime == 0) {
                                    nextPayTime += 60;
                                }
                                if (timeCount != null) {
                                    timeCount.cancel();
                                }
                                timeCount = new TimeCount(nextPayTime * 1000, 200); //构造CountDownTimer对象(3s)
                                timeCount.begin();
                            }
                        } else {
                            paySuccessPic.setImageResource(R.mipmap.no_need_pay);
                        }
                        if ("0".equals(r.data.getStartAd())) {
                            getLocalBannerData();
                            initCirclePoint();
                        } else {
                            if (bannerLayout != null) {
                                bannerLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getString(R.string.error_network_short));
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                error.printStackTrace();
                showOkCancelAlertDialog(false, "提示", "网络连接出错，是否重试？", "确定", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                        quaryOrderInfo();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                        finish();
                    }
                });
            }
        });
    }

    @OnClick(value = {R.id.feedback_btn, R.id.close_layout, R.id.share_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback_btn:
                if (paySuccess == null) {
                    return;
                }
                Intent i = new Intent(getBaseContext(), PayQuestionListActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(Constant.KEY_PAYSUCCESS, paySuccess);
                i.putExtras(b);
                startActivity(i);
                break;

            case R.id.close_layout:
                if (bannerLayout != null && bannerLayout.getVisibility() == View.VISIBLE) {
                    bannerLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.share_btn:
                if (null != sharePopWindow) {
                    openSharePopWindow();
                }
                break;
        }
    }

    /**
     * 替换提示
     *
     * @param time
     */
    private void replaceTvTimeMoneyTip(long time) {
        String timeStr = DateConvertor.secToTime(Long.valueOf(paySuccess.getNextPayTime()) - time / 1000);
        String nextPayMsg = paySuccess.getNextPayMsg();
        if (nextPayMsg.contains("*")) {
            String newTip = nextPayMsg.replace("*", "*" + timeStr);
            TvTimeMoneyTip.setText(newTip);
        }
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
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {  //计时过程显示
            int time = (int) Math.ceil(millisUntilFinished / 1000f);
            if (time > 0) {
                long passTime = System.currentTimeMillis() - interfaceBackTime;
                replaceTvTimeMoneyTip(passTime);
                long localRealTime = allParkTime + passTime;
                String timeStr = DateConvertor.millToTime(localRealTime);
                if (infoList != null && !infoList.isEmpty()) {
                    int size = infoList.size();
                    Info info = null;
                    for (int i = 0; i < size; i++) {
                        info = infoList.get(i);
                        if ("车辆已停时长".equals(info.getKey())) {
                            info.setValue(timeStr);
                            infoAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void onFinish() { //计时完毕时触发
            cancel();
            finish();
        }

    }

    /**
     * 获取本地shareFileUtils的数据
     */
    private void getLocalBannerData() {
        String dataString = sharedFileUtils.getString(SharedFileUtils.BANNER_LIST_LOCAL_PAY);
        LogUtils.d("--dataString->>" + dataString);
        if (TextUtils.isEmpty(dataString)) {
            if (bannerLayout != null) {
                bannerLayout.setVisibility(View.GONE);
            }
        } else {
            dataList = (List<AdInfo>) ObjectUtil.getObject(dataString);
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            if (!dataList.isEmpty()) {
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
                viewPager.setOnPageChangeListener(this);
                if (bannerLayout != null) {
                    bannerLayout.setVisibility(View.VISIBLE);
                }
            } else {
                if (bannerLayout != null) {
                    bannerLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 初始化banner圆点
     */
    private void initCirclePoint() {
        if (dataList.size() > 1) {
            popup_banner_circle_groups.removeAllViews();
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

    /**
     * 初始化分享popWindow
     */
    private void initSharePopWindow() {
        View contentView = getLayoutInflater().inflate(
                R.layout.popwindow_social_share, null);
        LinearLayout share_wechat = (LinearLayout) contentView
                .findViewById(R.id.share_wechat);
        LinearLayout share_wechat_moment = (LinearLayout) contentView
                .findViewById(R.id.share_wechat_moment);
        LinearLayout share_sms = (LinearLayout) contentView
                .findViewById(R.id.share_sms);
        LinearLayout space = (LinearLayout) contentView
                .findViewById(R.id.space);
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.share_wechat:
                        shareToWechat(shareInfo);
                        break;

                    case R.id.share_wechat_moment:
                        shareToWechatMoment(shareInfo);
                        break;

                    case R.id.share_sms:
                        shareToSMS(shareInfo);
                        break;

                    case R.id.space:
                        sharePopWindow.dismiss();
                        break;
                }
                sharePopWindow.dismiss();
            }
        };
        share_wechat.setOnClickListener(onClickListener);
        share_wechat_moment.setOnClickListener(onClickListener);
        share_sms.setOnClickListener(onClickListener);
        space.setOnClickListener(onClickListener);
        sharePopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        sharePopWindow.setOutsideTouchable(true);
        sharePopWindow.setBackgroundDrawable(new BitmapDrawable());
        sharePopWindow
                .setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        // TODO Auto-generated method stub
                        backgroundgAlpha(1f);
                    }
                });
    }

    /**
     * 打开分享popWindow
     */
    private void openSharePopWindow() {
        if (!sharePopWindow.isShowing()) {
            sharePopWindow.showAtLocation(getWindow().getDecorView(),
                    Gravity.BOTTOM, 0, 0);
            backgroundgAlpha(0.7f);
        } else {
            showToast("请稍后");
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
        quaryOrderInfo();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }
}
