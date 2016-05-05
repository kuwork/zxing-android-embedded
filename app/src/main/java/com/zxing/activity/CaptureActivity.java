package com.zxing.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.merchants.R;
import com.ajb.merchants.activity.BaseActivity;
import com.ajb.merchants.model.CarInParkingBuilder;
import com.ajb.merchants.util.Constant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback, OnClickListener {
    private CaptureActivityHandler handler;
    @ViewInject(R.id.viewfinder_view)
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    public static final int CANNOT_SCAN = 800;
    private boolean vibrate;
    private String action = null;
    private int mode = 0;
    @ViewInject(R.id.btnFlash)
    private CheckBox btnFlash;
    @ViewInject(R.id.tv_flash)
    private TextView tv_flash;
    @ViewInject(R.id.action_menu_view)
    private ActionMenuView actionMenuView;
    @ViewInject(R.id.preview_view)
    SurfaceView previewView;
    @ViewInject(R.id.empty_view)
    View emptyView;
    @ViewInject(R.id.tv_scan_tip)
    private TextView tv_scan_tip;
    private String from;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ViewUtils.inject(this);
        initTitle("二维码扫描");
        initBackClick(NO_RES, new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        initMenuClick(NO_ICON, "菜单一", this,
                NO_ICON, "菜单二", this);
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
//        actionMenuView.setOnMenuItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        LogUtils.v("hasSurface=" + hasSurface);
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
        btnFlash.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CameraManager.get().turnOn();
                    tv_flash.setText("关灯");
                } else {
                    tv_flash.setText("开灯");
                    CameraManager.get().turnOff();
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constant.KEY_MODE)) {
            from = bundle.getString(Constant.KEY_MODE);
            tv_scan_tip.setText("请扫描停车场内柱子上的二维码");
        }
    }

    @Override
    protected void onPause() {
        LogUtils.v("OnPause");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LogUtils.v("onDestroy");
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        try {
            inactivityTimer.onActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "扫描失败,返回", Toast.LENGTH_SHORT)
                    .show();
            finish();
        } else {
            LogUtils.i("barcode= " + resultString);
            if (TextUtils.isEmpty(from)) {
                Pattern pattern = Pattern.compile("cardNumber=[\\da-zA-Z]+");
                Matcher matcher = pattern.matcher(resultString);
                if (resultString.contains("eanpa-gz-manager") && matcher.find()) {
                    String str = matcher.group();
                    Intent i = new Intent();
                    String[] strList = str.split("=");
                    Bundle bundle = new Bundle();
                    CarInParkingBuilder carInParkingBuilder = new CarInParkingBuilder();
                    carInParkingBuilder.setCarSN(strList[1]);
                    bundle.putSerializable(Constant.KEY_CARINPARKING, carInParkingBuilder);
                    i.putExtras(bundle);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                } else {
                    Vibrator vib = (Vibrator) CaptureActivity.this
                            .getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(1000);
                    Toast.makeText(CaptureActivity.this, "请使用安居宝停车卡",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                if (resultString.startsWith("ID-")) {
                    Intent i = new Intent();
                    i.putExtra(Constant.KEY_CARDID, resultString);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                } else {
                    Vibrator vib = (Vibrator) CaptureActivity.this
                            .getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(1000);
                    Toast.makeText(CaptureActivity.this, "请扫描掌停宝二维码",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void initCamera() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorPage(emptyView, R.string.tip_error_camera, R.mipmap.norecord);
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headerMenu1:
                break;
            case R.id.headerMenu2:
                break;
            case R.id.headerMenu3:
                break;
        }
    }
}
