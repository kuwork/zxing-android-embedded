package com.ajb.merchants.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.util.Constant;


public class UpdateDialog extends Activity implements OnClickListener {

    private TextView dirText;
    private LinearLayout sure_btn;
    private LinearLayout cancel_btn;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_update_download_complete);
        initData();
    }

    /**
     * 初始化数据
     *
     * @Title initData
     * @Description
     * @author 李庆育
     * @date 2015-12-21 下午3:15:34
     */
    private void initData() {
        dirText = (TextView) this.findViewById(R.id.download_package_dir);
        sure_btn = (LinearLayout) this.findViewById(R.id.sure_btn);
        cancel_btn = (LinearLayout) this.findViewById(R.id.cancel_btn);
        sure_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String dirStr = bundle.getString(Constant.KEY_UPDATE_APK_DIR);
            pendingIntent = (PendingIntent) bundle
                    .get(Constant.KEY_UPDATE_PENDING_INTENT);
            dirText.setText("保存目录:" + dirStr);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.sure_btn:
                if (pendingIntent != null) {
                    try {
                        pendingIntent.send();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.cancel_btn:
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

}
