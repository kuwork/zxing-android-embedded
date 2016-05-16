package com.ajb.merchants.wxapi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.ajb.merchants.activity.BaseActivity;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.Constant;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    public static String PREPAYID = "";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);

    }

    @Override
    public void onReq(BaseReq req) {
        new AlertDialog.Builder(WXPayEntryActivity.this).setTitle("提示标题2")
                .setMessage("这是提示内容").show();

    }

    @Override
    public void onResp(BaseResp resp) {
        if (null != MyApplication.getActivity()) {
            MyApplication.getActivity().finish();
        }
        switch (resp.errCode) {
            // 支付成功
            case 0:
                showToast("支付成功");
                Intent intent = null;
                if (MyApplication.getProductPay().equals("1")) {// 停车缴费
                    /*intent = new Intent(getBaseContext(), PaySuccessActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.InterfaceParam.PREPAYID, PREPAYID);
                    startActivity(intent);*/
                } else {// 充值
                    /*intent = new Intent(WXPayEntryActivity.this,
                            AccountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);*/
                }
                break;

            // 表示支付失败D
            case -1:
                showToast("支付失败");
                break;

            // 表示取消支付
            case -2:
                showToast("支付取消");
                break;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }
}