package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.PayTypeAdapter;
import com.ajb.merchants.adapter.PopupBannerAdapter;
import com.ajb.merchants.alipay.PayResult;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.model.AliPayInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.CarInParkingBuilder;
import com.ajb.merchants.model.Coupon;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.PaySuccess;
import com.ajb.merchants.model.PayWay;
import com.ajb.merchants.model.SearchCarCount;
import com.ajb.merchants.model.ShareInfo;
import com.ajb.merchants.model.WeChatInfo;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.DateConvertor;
import com.ajb.merchants.util.MD5;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.util.WeXinUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MyListView;
import com.ajb.merchants.view.ScaleFrameLayout;
import com.ajb.merchants.wxapi.WXPayEntryActivity;
import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.util.DensityUtil;
import com.util.ObjectUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cn.sharesdk.framework.ShareSDK;

public class PayActivity extends BaseActivity implements View.OnClickListener,
        OnPageChangeListener {

    @ViewInject(R.id.newmoney_tv)
    TextView TvMoney;
    @ViewInject(R.id.time_money_tip)
    TextView TvTimeMoneyTip;
    @ViewInject(R.id.tv_no_need_pay)
    TextView TvNoNeedPay;
    @ViewInject(R.id.in_need_pay)
    LinearLayout needPayLinear;
    @ViewInject(R.id.in_no_need_pay)
    LinearLayout noNeedPayLinear;
    @ViewInject(R.id.in_coupon_list)
    LinearLayout couponListLinear;
    @ViewInject(R.id.coupon_tip_ll)
    LinearLayout couponTipLinear;
    @ViewInject(R.id.pay_btn_layout)
    LinearLayout payBtnLayout;
    @ViewInject(R.id.pay_banner_layout)
    LinearLayout payBannerLayout;
    @ViewInject(R.id.couponListView)
    MyListView couponListView;
    @ViewInject(R.id.pay_detail_lv)
    MyListView payDetailListView;
    @ViewInject(R.id.tip_img)
    ImageView tipImg;
    @ViewInject(R.id.feedback_btn)
    TextView feedBackBtn;
    @ViewInject(R.id.btn_onclick_pay)
    Button payBtn;
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

    public static final String REQ_LIST_RESULT = "COUPON_LIST";// 选择优惠券
    private static final int PAY_WECHAT = 0;
    private static final int PAY_ALIPAY = 1;
    private static final int SDK_PAY_FLAG = 2;
    private CarInParkingBuilder carInParkingBuilder;
    private SearchCarCount searchCarCount;
    private BaseListAdapter<Info> infoAdapter;
    private BaseListAdapter<Coupon> couponAdapter;
    private List<Info> infoList;
    private IWXAPI api = WXAPIFactory.createWXAPI(this, null);
    private PayTypeAdapter payTypeAdapter;
    private PayReq req;
    private Dialog mDialog;
    private MyGridView gridView;
    private View root;
    private PopupWindow pay_popWindow;
    private TimeCount timeCount;
    private AliPayInfo aliPayInfo;
    private View contentView;
    private long interfaceBackTime;
    private long allParkTime; // 车辆已停时长
    private long nextPayTime;
    private Timestamp carInDate; // 车辆进场时间
    private Timestamp carCalcDate;// 车辆计费时间
    private Timestamp carNextPayDate; // 车辆下次缴费时间
    private List<AdInfo> dataList;
    private ImageView[] circlePoints;
    private PopupBannerAdapter adapter;
    private PopupWindow sharePopWindow;
    private ShareInfo shareInfo;
    private PaySuccess paySuccess;
    private boolean isLoading;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (msg.obj == null) {
                showToast(getResources().getString(R.string.error_network_short));
                return;
            }
            switch (msg.what) {
                case PAY_WECHAT:
                    try {
                        BaseResult<WeChatInfo> r = gson.fromJson(
                                msg.obj.toString(),
                                new TypeToken<BaseResult<WeChatInfo>>() {
                                }.getType());
                        if (null != r && null != r.data) {
                            if ("0000".equals(r.getCode())) {
                                MyApplication.setProductPay("1");
                                WXPayEntryActivity.PREPAYID = r.data.getPrepayid();
                                genAPPPayReq(r.data.getPrepayid(),
                                        r.data.getPartnerid(), r.data.getAppKey());
                            } else if ("0013".equals(r.getCode())) {
                                showOkAlertDialog(false, "提示",
                                        "抵扣后金额为零，请切换支付方式‘仅优惠券抵扣’后，继续进行支付。", "确定",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dimissOkAlertDialog();
                                            }
                                        });
                            } else {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                                showToast(r.getMsg());
                            }
                        } else {
                            showToast(r.getMsg());
                        }
                    } catch (Exception e) {
                        showToast(getString(R.string.error_network_short));
                        e.printStackTrace();
                    }
                    break;

                case PAY_ALIPAY:
                    try {
                        BaseResult<AliPayInfo> r = gson.fromJson(
                                msg.obj.toString(),
                                new TypeToken<BaseResult<AliPayInfo>>() {
                                }.getType());
                        if (null != r && null != r.data) {
                            if ("0000".equals(r.getCode())) {
                                aliPayInfo = r.data;
                                MyApplication.setProductPay("1");
                                genAPPPayReq(r.data.getSign(),
                                        r.data.getOrderInfo());
                            }
                        } else if ("0013".equals(r.getCode())) {
                            showOkAlertDialog(false, "提示",
                                    "抵扣后金额为零，请切换支付方式‘仅优惠券抵扣’后，继续进行支付。", "确定",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dimissOkAlertDialog();
                                        }
                                    });
                        } else {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            showToast(r.getMsg());
                        }
                    } catch (Exception e) {
                        showToast(getString(R.string.error_network_short));
                        e.printStackTrace();
                    }
                    break;
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (aliPayInfo == null) {
                            return;
                        }
                    /*
					 * Intent intent = new Intent(getBaseContext(),
					 * PaySuccessActivity.class); Bundle bundle = new Bundle();
					 * bundle.putSerializable(Constant.InterfaceParam.ORDERNO,
					 * aliPayInfo.getOrderId()); intent.putExtras(bundle);
					 * startActivity(intent);
					 */
                        // getMessage(carInParkingBuilder);
                        showToast("支付成功");
                    } else {
                        showToast(payResult.getMemo());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ViewUtils.inject(this);
        ShareSDK.initSDK(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constant.KEY_CARINPARKING)) {
            carInParkingBuilder = (CarInParkingBuilder) bundle
                    .getSerializable(Constant.KEY_CARINPARKING);
        }
        initView();
        initPayPopWindow();
    }

    private void initView() {
        initTitle("缴停车费");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_refresh, "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessage(carInParkingBuilder);
            }
        });
        api.registerApp(Constant.APP_ID);
        req = new PayReq();
    }

    @OnClick(value = {R.id.btn_onclick_pay, R.id.feedback_btn,
            R.id.close_layout, R.id.share_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onclick_pay:
                String payWay = searchCarCount.getPayWay();
                if (!TextUtils.isEmpty(payWay)) {
                    if ("4".equals(payWay)) { // 仅优惠券抵扣
                        showUseConponPay();
                    } else {
                        if (payWay.contains("1") || payWay.contains("2")) {
                            if (payTypeAdapter == null) {
                                payTypeAdapter = new PayTypeAdapter(
                                        getBaseContext(), payWay);
                            } else {
                                payTypeAdapter.setPayTypes(payWay);
                            }
                            gridView.setAdapter(payTypeAdapter);
                            openPayPopWindow();
                        } else {
                            showOkAlertDialog(false, "提示",
                                    "当前版本不支持该车场的在线支付方式，请使用现金支付。", "确定",
                                    new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                            // TODO Auto-generated method stub
                                            dimissOkAlertDialog();
                                        }
                                    });
                        }
                    }
                } else {
                    showOkAlertDialog(false, "提示", "该车场暂不支持在线支付！", "确定",
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    // TODO Auto-generated method stub
                                    dimissOkAlertDialog();
                                }
                            });
                }
                break;

            case R.id.feedback_btn:
                if (searchCarCount == null) {
                    return;
                }
                Intent intent = null;
                if (Constant.InterfaceParam.PAY_SUCCESS.equals(searchCarCount
                        .getChargeCode())) {
                    Intent i = new Intent(getBaseContext(),
                            PayQuestionListActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(Constant.APPSEARCHCARCOUNT, searchCarCount);
                    i.putExtras(b);
                    startActivity(i);
                } else {
                    intent = new Intent(getBaseContext(), PayFeedBackActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable(Constant.APPSEARCHCARCOUNT, searchCarCount);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                break;

            case R.id.close_layout:
                if (bannerLayout != null
                        && bannerLayout.getVisibility() == View.VISIBLE) {
                    bannerLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.share_btn:
                if (null != sharePopWindow) {
                    openSharePopWindow();
                }
        }

    }

    private void showUseConponPay() {
        showOkCancelAlertDialog(false, "提示", "是否使用优惠券抵扣停车费?", "确定", "取消",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                        sendUseCouponPay();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                    }
                });
    }

    /**
     * 仅优惠券抵扣
     */
    private void sendUseCouponPay() {
        if (searchCarCount == null) {
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.IDS,
                gson.toJson(searchCarCount.getCouponsList()));
        params.addQueryStringParameter(Constant.InterfaceParam.CARINTIME,
                searchCarCount.getCarInTime());
        params.addQueryStringParameter(Constant.InterfaceParam.ITDCODE,
                searchCarCount.getLtdCode());
        params.addQueryStringParameter(Constant.InterfaceParam.PARKCODE,
                searchCarCount.getParkCode());
        params.addQueryStringParameter(Constant.InterfaceParam.CARNO,
                searchCarCount.getCarNo());
        params.addQueryStringParameter(Constant.InterfaceParam.CARDID,
                searchCarCount.getCardId());
        params.addQueryStringParameter(Constant.InterfaceParam.USERNAME,
                getLoginName());
        send(Constant.APPUSERCOUPONSV2, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mDialog = MyProgressDialog.createLoadingDialog(
                        PayActivity.this, "请稍后...");
                mDialog.setCancelable(false);
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                try {
                    BaseResult<PaySuccess> r = gson.fromJson(
                            responseInfo.result,
                            new TypeToken<BaseResult<PaySuccess>>() {
                            }.getType());
                    if (r != null) {
                        if ("0000".equals(r.code)) {
                            getMessage(carInParkingBuilder);
                        } else {
                            showToast(r.msg);
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
                fail(error, msg);
            }
        });
    }

    /**
     * 初始化分享popWindow
     */
    private void initPayPopWindow() {
        if (root == null || pay_popWindow == null) {
            root = this.getLayoutInflater().inflate(
                    R.layout.popwindow_pay_type, null);
            // 创建PopupWindow对象
            pay_popWindow = new PopupWindow(root);
            pay_popWindow.setWindowLayoutMode(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            // popup.setAnimationStyle(R.anim.in);
            pay_popWindow.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
            pay_popWindow.setOutsideTouchable(true);// 以及下一句 同时写才会有效
            pay_popWindow.setFocusable(true);// 获取焦点
        }
        LinearLayout space = (LinearLayout) root.findViewById(R.id.space);
        gridView = (MyGridView) root.findViewById(R.id.gv);
        // wechat_pay.setOnClickListener(this);
        space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (pay_popWindow.isShowing()) {
                    pay_popWindow.dismiss();
                }
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if (null == searchCarCount) {
                    return;
                }
                PayWay payWay = (PayWay) gridView.getAdapter().getItem(arg2);
                if (payWay.getWay().equals("1")) {
                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                    if (!isPaySupported) {
                        showToast("不支持微信支付，请升级到微信5.0至以上的版本");
                    } else {
                        mDialog = MyProgressDialog.createLoadingDialog(
                                PayActivity.this, "订单提交中...");
                        mDialog.show();
                        String body2 = "停车缴费";
                        String tradeType2 = "APP";
                        replay(Constant.APPPAYV2, body2, tradeType2,
                                searchCarCount.getParkFeeValue()
                                        .getShouldPayFee(), searchCarCount
                                        .getLtdCode(), searchCarCount
                                        .getParkCode(), searchCarCount
                                        .getCarNo(), searchCarCount.getCarSN(),
                                searchCarCount.getPayType(),
                                gson.toJson(searchCarCount.getCouponsList()),
                                PAY_WECHAT);
                    }

                } else if (payWay.getWay().equals("2")) {
                    mDialog = MyProgressDialog.createLoadingDialog(
                            PayActivity.this, "订单提交中.....");
                    mDialog.show();
                    String body = "停车缴费";
                    String tradeType = "APP";
                    replay(Constant.APPPAYFORALIV2, body, tradeType,
                            searchCarCount.getParkFeeValue().getShouldPayFee(),
                            searchCarCount.getLtdCode(),
                            searchCarCount.getParkCode(),
                            searchCarCount.getCarNo(),
                            searchCarCount.getCarSN(),
                            searchCarCount.getPayType(),
                            gson.toJson(searchCarCount.getCouponsList()),
                            PAY_ALIPAY);
                }
                if (pay_popWindow.isShowing()) {
                    pay_popWindow.dismiss();
                }
            }

        });
    }

    /**
     * 打开分享popWindow
     */
    private void openPayPopWindow() {
        if (!pay_popWindow.isShowing()) {
            pay_popWindow.showAtLocation(getWindow().getDecorView(),
                    Gravity.CENTER, 0, 0);
        } else {
            showToast("请稍后");
        }
    }

    // // 组装
    private void genAPPPayReq(String prepay_id, String partnerid, String appKey) {
        req.appId = Constant.APP_ID;
        req.partnerId = partnerid;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXpay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = WeXinUtils.genAppSign(signParams, appKey);
        api.registerApp(Constant.APP_ID);
        api.sendReq(req);
    }

    // 组装 支付宝
    private void genAPPPayReq(String sign, String orderInfo) {
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + "sign_type=\"RSA\"";
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * @param URL
     * @param body
     * @param tradeType
     * @param shouldPayFee
     * @param ltdCode
     * @param parkCode
     * @param carNo
     * @param carSN
     * @param payTypeString
     * @param ids
     * @param payType
     * @Title replay
     * @Description 获取订单信息
     * @date 2016-3-28 下午1:17:35
     */
    public void replay(String URL, String body, String tradeType,
                       String shouldPayFee, String ltdCode, String parkCode, String carNo,
                       String carSN, String payTypeString, String ids, final int payType) {
        // 获取订单信息
        TelephonyManager tm = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.DEVIDEINFO,
                tm.getDeviceId());
        params.addQueryStringParameter(Constant.InterfaceParam.BODY, body);
        params.addQueryStringParameter(Constant.InterfaceParam.TRADETYPE,
                tradeType);
        params.addQueryStringParameter(Constant.InterfaceParam.TOTALFRR,
                shouldPayFee);
        params.addQueryStringParameter(Constant.InterfaceParam.ITDCODE, ltdCode);
        params.addQueryStringParameter(Constant.InterfaceParam.PARKCODE,
                parkCode);
        params.addQueryStringParameter(Constant.InterfaceParam.CARNO, carNo);
        params.addQueryStringParameter(Constant.InterfaceParam.CARSN, carSN);// 卡编号
        params.addQueryStringParameter(Constant.InterfaceParam.IDS, ids);
        params.addQueryStringParameter(Constant.InterfaceParam.PAYTYPE,
                payTypeString);
        params.addQueryStringParameter(Constant.InterfaceParam.USERNAME,
                getLoginName());
        send(URL, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    mHandler.sendMessage(mHandler.obtainMessage(payType,
                            responseInfo.result));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(payType, null));
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mHandler.sendMessage(mHandler.obtainMessage(payType, null));
            }
        });

    }

    private void getMessage(final CarInParkingBuilder carInParkingBuilder) {
        if (isLoading) {
            return;
        }
        if (timeCount != null) {
            timeCount.cancel();
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (carInParkingBuilder == null) {
            return;
        }
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(carInParkingBuilder.getLtdCode())) {
            params.addQueryStringParameter(Constant.InterfaceParam.LTDCODE,
                    carInParkingBuilder.getLtdCode());
        }
        if (!TextUtils.isEmpty(carInParkingBuilder.getParkCode())) {
            params.addQueryStringParameter(Constant.InterfaceParam.PARKCODE,
                    carInParkingBuilder.getParkCode());
        }
        if (!TextUtils.isEmpty(carInParkingBuilder.getCarSN())) {
            params.addQueryStringParameter(Constant.InterfaceParam.CARSN,
                    carInParkingBuilder.getCarSN());
        }
        if (!TextUtils.isEmpty(carInParkingBuilder.getCarNo())) {
            params.addQueryStringParameter(Constant.InterfaceParam.CARNO,
                    carInParkingBuilder.getCarNo());
        }
        if (!TextUtils.isEmpty(carInParkingBuilder.getPayType())) {
            params.addQueryStringParameter(Constant.InterfaceParam.PAYTYPE,
                    carInParkingBuilder.getPayType());
        }
        if (TextUtils.isEmpty(getLoginName())) {
            showToast("用户信息为空");
            return;
        } else {
            params.addQueryStringParameter(Constant.InterfaceParam.MOBILEPHONE,
                    getLoginName());
        }
        send(Constant.APPSEARCHCARCOUNT, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                isLoading = true;
                mDialog = MyProgressDialog.createLoadingDialog(
                        PayActivity.this, "请稍后...");
                mDialog.setCancelable(true);
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (responseInfo.statusCode == 200) {
                    interfaceBackTime = System.currentTimeMillis();
                    try {
                        BaseResult<SearchCarCount> r = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<SearchCarCount>>() {
                                }.getType());
                        if (null != r) {
                            if ("0000".equals(r.code) && r.data != null) {
                                searchCarCount = r.data;
                                String chargeCode = searchCarCount
                                        .getChargeCode();
                                if (Constant.InterfaceParam.NO_NEED_PAY
                                        .equals(chargeCode)
                                        || Constant.InterfaceParam.PAY_SUCCESS
                                        .equals(chargeCode)) {
                                    noNeedPayLinear.setVisibility(View.VISIBLE);
                                    payBannerLayout.setVisibility(View.GONE);
                                    payBtnLayout.setVisibility(View.VISIBLE);
                                    needPayLinear.setVisibility(View.GONE);
                                    TvNoNeedPay.setText(searchCarCount
                                            .getChargeMsg());
                                    if (Constant.InterfaceParam.PAY_SUCCESS
                                            .equals(chargeCode)) {
                                        payBannerLayout
                                                .setVisibility(View.VISIBLE);
                                        payBtnLayout.setVisibility(View.GONE);
                                        tipImg.setImageResource(R.mipmap.pay_success);
                                        paySuccess = searchCarCount
                                                .getOrderSuccess();
                                        if (null != paySuccess) {
                                            shareInfo = paySuccess
                                                    .getNetShare();
                                            if (null != shareInfo) {
                                                initSharePopWindow();
                                                shareBtn.setVisibility(View.VISIBLE);
                                            } else {
                                                shareBtn.setVisibility(View.GONE);
                                            }
                                            if ("0".equals(paySuccess // 需要启动广告
                                                    .getStartAd())) {
                                                getLocalBannerData();
                                                initCirclePoint();
                                            } else {
                                                if (bannerLayout != null) {
                                                    bannerLayout
                                                            .setVisibility(View.GONE);
                                                }
                                            }
                                            feedBackBtn.setText("对此账单有疑问？");
                                        }
                                    } else {
                                        tipImg.setImageResource(R.mipmap.no_need_pay);
                                    }
                                    payBtn.setText("去缴费");
                                    payBtn.setEnabled(false);
                                } else {
                                    noNeedPayLinear.setVisibility(View.GONE);
                                    payBannerLayout.setVisibility(View.GONE);
                                    payBtnLayout.setVisibility(View.VISIBLE);
                                    needPayLinear.setVisibility(View.VISIBLE);
                                    String money = searchCarCount
                                            .getParkFeeValue()
                                            .getShouldPayFee();
                                    TvMoney.setText(money);
                                    feedBackBtn.setText("错误反馈");
                                    if ("4".equals(searchCarCount.getPayWay())) {
                                        payBtn.setText("确定");
                                    } else {
                                        payBtn.setText("去缴费");
                                    }
                                    payBtn.setEnabled(true);
                                }
                                infoList = searchCarCount.getInfo();
                                List<Coupon> couponList = searchCarCount
                                        .getCouponsList();
                                if (null != couponList && !couponList.isEmpty()) {
                                    couponListLinear
                                            .setVisibility(View.VISIBLE);
                                    if (null == couponAdapter) {
                                        couponAdapter = new BaseListAdapter<Coupon>(
                                                getBaseContext(),
                                                couponList,
                                                R.layout.pay_coupon_listview_item,
                                                null);
                                        couponListView
                                                .setAdapter(couponAdapter);
                                    } else {
                                        couponAdapter.update(couponList);
                                    }
                                } else {
                                    couponListLinear.setVisibility(View.GONE);
                                }
                                if (null != infoList) {
                                    if (null == infoAdapter) {
                                        infoAdapter = new BaseListAdapter<Info>(
                                                getBaseContext(),
                                                infoList,
                                                R.layout.pay_listview_detail_item,
                                                null);
                                        payDetailListView
                                                .setAdapter(infoAdapter);
                                    } else {
                                        infoAdapter.update(infoList);
                                    }
                                }
                                TvTimeMoneyTip.setText(searchCarCount
                                        .getNextPayMsg());
                                try {
                                    carInDate = DateConvertor
                                            .getTimestamp(searchCarCount
                                                    .getCarInTime()); // 车辆进场时间
                                    carCalcDate = DateConvertor
                                            .getTimestamp(searchCarCount
                                                    .getNowDate()); // 车辆计费时间
                                    carNextPayDate = DateConvertor
                                            .getTimestamp(searchCarCount
                                                    .getParkFeeValue()
                                                    .getNextChargeTime()); // 车辆下次缴费时间
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (null == carInDate || null == carCalcDate) {
                                    return;
                                }
                                if (carInDate.after(carCalcDate)) {
                                    return;
                                }
                                if (carNextPayDate != null
                                        && carCalcDate.after(carNextPayDate)) {
                                    return;
                                }
                                allParkTime = carCalcDate.getTime()
                                        - carInDate.getTime();
                                if (carNextPayDate != null) {
                                    nextPayTime = carNextPayDate.getTime()
                                            - carCalcDate.getTime();
                                } else {
                                    nextPayTime = -1;
                                }
                                if (nextPayTime == 0) {
                                    nextPayTime += 60;
                                }
                                timeCount = new TimeCount(Long.MAX_VALUE, 200); // 构造CountDownTimer对象
                                timeCount.begin();
                            } else {
                                showOkAlertDialog(false, "提示", r.getMsg(),
                                        "确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        });
                            }
                        } else {
                            showOkCancelAlertDialog(false, "提示",
                                    "网络连接出错，是否重试？", "确定", "取消",
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dimissOkCancelAlertDialog();
                                            getMessage(carInParkingBuilder);
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dimissOkCancelAlertDialog();
                                            finish();
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_network_short));
                    }
                }
                isLoading = false;
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                error.printStackTrace();
                showOkCancelAlertDialog(false, "提示", "网络连接出错，是否重试？", "确定",
                        "取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dimissOkCancelAlertDialog();
                                getMessage(carInParkingBuilder);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dimissOkCancelAlertDialog();
                                finish();
                            }
                        });
                isLoading = false;
            }
        });

    }

    /**
     * 替换提示
     *
     * @param time
     */
    private void replaceTvTimeMoneyTip(long time) {
        String nextPayMsg = searchCarCount.getNextPayMsg();
        if (nextPayMsg.contains("*")) {
            if (nextPayTime == -1) {// 下次缴费为空
                String newTip = nextPayMsg.replace("*", "*未知时间");
                TvTimeMoneyTip.setText(newTip);
                return;
            }
            if (nextPayTime - time < 0) {
                getMessage(carInParkingBuilder);
                return;
            } else if (nextPayTime - time == 0) {
                getMessage(carInParkingBuilder);
            }
            String timeStr = DateConvertor
                    .secToTime((nextPayTime - time) / 1000);
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
         * @param millisInFuture    The number of millis in the future from the call to
         *                          {@link #start()} until the countdown is done and
         *                          {@link #onFinish()} is called.
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
        public void onTick(long millisUntilFinished) { // 计时过程显示
            // 逝去时间
            long passTime = System.currentTimeMillis() - interfaceBackTime;
            replaceTvTimeMoneyTip(passTime);// 剩余时间显示
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

        @Override
        public void onFinish() { // 计时完毕时触发
            // cancel();
            // getMessage(carInParkingBuilder);
        }

    }

    /**
     * 获取本地shareFileUtils的广告数据
     */
    private void getLocalBannerData() {
        String dataString = sharedFileUtils
                .getString(SharedFileUtils.BANNER_LIST_LOCAL_PAY);
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
                adapter = new PopupBannerAdapter(dataList,
                        getApplicationContext(), new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        PopupBannerAdapter.ViewHolder viewHolder = (PopupBannerAdapter.ViewHolder) v
                                .getTag();
                        if (viewHolder != null
                                && viewHolder.adInfo != null
                                && !TextUtils.isEmpty(viewHolder.adInfo
                                .getLink())) {
                            Intent intent = new Intent(
                                    getBaseContext(),
                                    WebViewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constant.KEY_URL,
                                    viewHolder.adInfo.getLink());
                            bundle.putString(Constant.KEY_TITLE,
                                    viewHolder.adInfo.getName());
                            Log.i("Splash", "clickItem+"
                                    + viewHolder.adInfo.getLink());
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
                viewPager.setAdapter(adapter);
                viewPager.setOnPageChangeListener(this);
                if (payBannerLayout != null) {
                    payBannerLayout.setVisibility(View.VISIBLE);
                }
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
                params.setMargins(
                        DensityUtil.dp2px(getApplicationContext(), 3), 0, 0, 0);
                imageView.setLayoutParams(params);
                circlePoints[i] = imageView;
                // 初始值, 默认第0个选中
                if (i == 0) {
                    circlePoints[i]
                            .setBackgroundResource(R.drawable.circle_choose);
                } else {
                    circlePoints[i]
                            .setBackgroundResource(R.drawable.circle_unchoose);
                }
                // 将小圆点放入到布局中
                popup_banner_circle_groups.addView(circlePoints[i]);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < circlePoints.length; i++) {
            if (i == position) {
                circlePoints[i].setBackgroundResource(R.drawable.circle_choose);
            } else {
                circlePoints[i]
                        .setBackgroundResource(R.drawable.circle_unchoose);
            }
        }
    }

    // 有三种状态（0，1，2）。state== 1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做。
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 初始化分享popWindow
     */
    private void initSharePopWindow() {
        if (contentView == null || sharePopWindow == null) {
            contentView = getLayoutInflater().inflate(
                    R.layout.popwindow_social_share, null);
            sharePopWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        LinearLayout share_wechat = (LinearLayout) contentView
                .findViewById(R.id.share_wechat);
        LinearLayout share_wechat_moment = (LinearLayout) contentView
                .findViewById(R.id.share_wechat_moment);
        LinearLayout share_sms = (LinearLayout) contentView
                .findViewById(R.id.share_sms);
        LinearLayout space = (LinearLayout) contentView
                .findViewById(R.id.space);
        OnClickListener onClickListener = new OnClickListener() {

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
    }

    /**
     * 打开分享popWindow
     */
    private void openSharePopWindow() {
        if (!sharePopWindow.isShowing()) {
            sharePopWindow.showAtLocation(getWindow().getDecorView(),
                    Gravity.CENTER, 0, 0);
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
        if (mDialog != null && mDialog.isShowing()) {
            isLoading = false;
            mDialog.dismiss();
        }
        dimissOkCancelAlertDialog();
        dimissOkAlertDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessage(carInParkingBuilder);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        // TODO Auto-generated method stub
        bundle.putSerializable(Constant.KEY_CARINPARKING, carInParkingBuilder);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        carInParkingBuilder = (CarInParkingBuilder) savedInstanceState
                .get(Constant.KEY_CARINPARKING);
    }
}
