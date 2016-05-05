package com.ajb.merchants.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.util.Constant;
import com.util.UpdateManager;

/**
 * 关于我们
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout layout_soft_guide, service_term, comprehend_ajb;
    private TextView currentVer;
    private TextView phoneTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        initTitle("关于");
        initBackClick(R.id.NO_RES, this);
        currentVer = (TextView) findViewById(R.id.currentVer);
        layout_soft_guide = (RelativeLayout) findViewById(R.id.layout_soft_guide);
        service_term = (RelativeLayout) findViewById(R.id.service_term);
        comprehend_ajb = (RelativeLayout) findViewById(R.id.comprehend_ajb);
        currentVer = (TextView) findViewById(R.id.currentVer);
        phoneTv = (TextView) findViewById(R.id.callno_tv);
        String type = new UpdateManager(getBaseContext(), null).getUpdateType();
        if (!"official".equals(type)) {
            currentVer.setText("内测 V" + getVersionName());
        } else {
            currentVer.setText("V" + getVersionName());
        }
        layout_soft_guide.setOnClickListener(this);
        service_term.setOnClickListener(this);
        comprehend_ajb.setOnClickListener(this);
        phoneTv.setOnClickListener(this);
    }

    private String getVersionName() {
        String version = "";
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.comprehend_ajb:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("URL", "http://www.ajbparking.com");
                intent.putExtra(Constant.KEY_TITLE, "了解安居宝");
                startActivity(intent);
                break;
            case R.id.service_term:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.KEY_URL,
                        "http://app.eanpa-gz-manager.com/static/register_agreement.html");
                intent.putExtra(Constant.KEY_TITLE, "停车服务协议");
                startActivity(intent);
                break;
            case R.id.layout_soft_guide:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.KEY_URL,
                        "http://portal.eanpa-gz-manager.com/static/products.html");
                intent.putExtra(Constant.KEY_TITLE, "功能介绍");
                startActivity(intent);
                break;
            case R.id.headerMenu1:
                finish();
                // 设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_left_out);
                break;
            case R.id.callno_tv:
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phoneTv.getText()));
                this.startActivity(intent);
                break;
        }
    }
}
