package com.ajb.merchants.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.FilterConditionAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.adapter.SortAdapter;
import com.ajb.merchants.adapter.SortDistrictAdapter;
import com.ajb.merchants.fragment.BaseFragment;
import com.ajb.merchants.fragment.HomeFragment;
import com.ajb.merchants.fragment.MainFragment;
import com.ajb.merchants.interfaces.OnCameraListener;
import com.ajb.merchants.interfaces.OnLocateListener;
import com.ajb.merchants.interfaces.OnSearchListener;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.model.BaiduShortUrlUtils;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.model.ProvinceInfo;
import com.ajb.merchants.model.SortModel;
import com.ajb.merchants.model.filter.Condition;
import com.ajb.merchants.model.filter.ConditionValue;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.task.CityUpdateTask;
import com.ajb.merchants.util.CarLocation;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.ScaleImageView;
import com.ajb.merchants.view.SideBar;
import com.ajb.merchants.view.SyncHorizontalScrollView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.App;
import com.util.DensityUtil;
import com.util.ObjectUtil;
import com.util.PathManager;
import com.zxing.activity.CaptureActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class HomePageActivity extends BaseActivity implements View.OnClickListener, OnSearchListener, PlatformActionListener, OnCameraListener, OnLocateListener {
    //  @ViewInject(R.id.fab)
    //  FloatingActionButton fab;
    @ViewInject(R.id.drawer_layout)
    DrawerLayout drawer;
    //  @ViewInject(R.id.nav_view)
    //  NavigationView navigationView;
    @ViewInject(R.id.lv_left_menu)
    ListView menuListView;
    @ViewInject(R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(R.id.imgAvator)
    ImageView imgAvatar;
    @ViewInject(R.id.tvAccountName)
    TextView tvAccountName;
    @ViewInject(R.id.home_slide_menu_banner_img)
    ScaleImageView home_slide_menu_banner_img;
    View menuPay;
    private PopupWindow share_popWindow;
    private Fragment mContent;
    private FragmentManager manager;
    private boolean hasPayNews = true;
    private int currentPic;
    private List<AdInfo> dataList;
    private FrameLayout imageLinear;
    private File photoFile, file;
    private View contentView;   //拍照popwindow的view
    private PopupWindow carLocationPop;
    private HomeFragment home;
    private MyLocationData locData;//定位结果
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private String homeName;
    private LinearLayout rg_nav_content;
    private ImageView iv_nav_indicator;
    private int indicatorWidth = 0;
    private FrameLayout mFirstListViewLayout, mSecondListViewLayout;
    private MainFragment main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ViewUtils.inject(this);
        initBroadcast();//账户信息监听
        manager = getSupportFragmentManager();
        homeName = sharedFileUtils.getString(SharedFileUtils.HOME_NAME);
        if (TextUtils.isEmpty(homeName)) {
            homeName = MainFragment.class.getSimpleName();
            sharedFileUtils.putString(SharedFileUtils.HOME_NAME, homeName);
        }
        initFirst();
//        if (initDirs()) {
//            initNavi();
//        }
//        initSharePopWindow();
//        createPhotoFileDir();
        getLocalSlideBanner();
        initLeftMenu();
        updateAccountInfo(getAccountInfo());
        ShareSDK.initSDK(this);
        requestPermission(Constant.PM_LOCATION,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, new Runnable() {
                    @Override
                    public void run() {
                        initLocation();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        showToast("相关权限被禁止，可能无法使用部分功能");
                    }
                });
        checkUpdate();//检查更新
    }

    @Override
    protected void updateAccountInfo(AccountInfo info) {
        super.updateAccountInfo(info);
        if (info == null) {
            return;
        }
    }

    @Override
    protected void showNoUpdateMsg() {

    }

    @Override
    protected void warnUser() {
        if (menuListView == null || menuListView.getAdapter() == null || menuListView.getAdapter().getCount() == 0) {
            return;
        }
        MenuItemAdapter<MenuInfo> adapter = (MenuItemAdapter<MenuInfo>) menuListView.getAdapter();
        adapter.removeAllDots();//清除原有状态
        if (App.getVersionCode(getBaseContext()) < sharedFileUtils
                .getInt(SharedFileUtils.LAST_CHECK_NEWEST_VERSION_CODE)) {
            adapter.addDots(MenuInfo.TO_SETTING);
        }
    }

    /**
     * 主页设置
     */
    private void initFirst() {
        if (homeName.equals(HomeFragment.class.getSimpleName())) {
            initHome();
        } else if (homeName.equals(MainFragment.class.getSimpleName())) {
            initMain();
        }
    }

    private void initLocation() {
        // 定位初始化
        mLocClient = new LocationClient(getBaseContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        // option.setPriority(LocationClientOption.NetWorkFirst);//
        // 设置网络优先(不设置，默认是gps优先)
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setScanSpan(30000);// 设置发起定位请求的间隔时间为30s(小于1秒则一次定位)
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void createPhotoFileDir() {
        // 存储至DCIM文件夹
        photoFile = new File(PathManager.getDiskFileDir(getBaseContext()) + File.separator + CarLocation.CAPTURE_FOLDER);
        if (!photoFile.exists()) {
            photoFile.mkdirs();
        }
    }

    /**
     * 初始化图片预览LocationPopupWindow
     *
     * @param bitmap
     * @return
     * @Title initCarLocationPopWindow
     * @Description
     * @author 李庆育
     * @date 2015-10-22 下午4:32:07
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initCarLocationPopWindow(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.cancel_btn:
                        carLocationPop.dismiss();
                        bitmap.recycle();
                        break;
                    case R.id.sure_btn:
                        onStartCaptrue();
                        carLocationPop.dismiss();
                        bitmap.recycle();
                        break;

                    case R.id.car_location_pic_del:
                        boolean isDelete = CarLocation.deleteCarLocationBitmap(getBaseContext());
                        if (isDelete) {
                            showToast("删除成功");
//                            if (mContent instanceof MapViewFragment) {
//                                ((MapViewFragment) mContent).isCarLocationPicExist();
//                            }
                        }
                        carLocationPop.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        if (contentView == null || carLocationPop == null) {
            contentView = getLayoutInflater().inflate(
                    R.layout.popup_car_location_pic, null);
            carLocationPop = new PopupWindow(contentView);
            carLocationPop.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(
                onClickListener);
        contentView.findViewById(R.id.sure_btn).setOnClickListener(
                onClickListener);
        contentView.findViewById(R.id.car_location_pic_del).setOnClickListener(onClickListener);
        final ImageView imageView = (ImageView) contentView
                .findViewById(R.id.car_location_pic);
        imageView.setImageBitmap(bitmap);
        imageLinear = (FrameLayout) imageView.getParent();
        ViewTreeObserver viewTreeObserver = imageLinear.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                int w = imageLinear.getWidth();
                int bmpW = bitmap.getWidth();
                int bmpH = bitmap.getHeight();
                int h = w * bmpH / bmpW;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageLinear
                        .getLayoutParams();
                params.width = w;
                params.height = h;
                imageLinear.setLayoutParams(params);
                return true;
            }
        });
        carLocationPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 显示图片预览LocationPopupWindow
     *
     * @Title showLocationPopWindow
     * @Description
     * @author 李庆育
     * @date 2015-10-22 下午4:56:48
     */
    public void showLocationPopWindow() {
        if (!carLocationPop.isShowing()) {
            carLocationPop.showAtLocation(getWindow().getDecorView(),
                    Gravity.CENTER, 0, 0);
        } else {
            showToast("请稍等");
        }
    }

    public void SaveImageFromDiff(File file) {
        if (file == null) {
            return;
        }
        String filePath = file.getAbsolutePath();
        LogUtils.d("filePath=" + file.getAbsolutePath() + ";");
        if (!file.exists()) {
            showToast("照片保存失败");
            return;
        }
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 不去真的解析图片 获取图片头部信息
            BitmapFactory.decodeFile(filePath, opts);
            int height = opts.outHeight;
            int width = opts.outWidth;
            LogUtils.i("height=" + height + ";width=" + width);
            int w, h;
            if (width > height) {// 横向的
                w = 500 * width / height;
                h = 500;
            } else {
                w = 500;
                h = 500 * height / width;
            }
            // 计算采样率
            int scaleX = height / h;
            int scaleY = width / w;
            int scale = Math.min(scaleX, scaleY);
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);
            File imageFile = new File(photoFile.getAbsolutePath(), CarLocation.ZTB_CARNO);
            clearImageCache(photoFile);
            //图片接近(0~0.01)是16:9的比例的时候，强行旋转图片
            double sixteen = 16;
            double nine = 9;
            double sixteenToNineValue = sixteen / nine; //代表16除以9
            double imgW = Double.valueOf(width);
            double imgH = Double.valueOf(height);
            double imgHToimgW = imgH / imgW;  //代表高除以宽
            if (Math.abs(sixteenToNineValue - imgHToimgW) >= 0 && Math.abs(sixteenToNineValue - imgHToimgW) < 0.02) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);  // 旋转90度
                //宽高都缩小4倍
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width / 4, height / 4, matrix, true);
            }
            saveImage(imageFile, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImage(File imageFile, Bitmap bm) {
        try {
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(imageFile));
            /* 采用压缩转档方法 */
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            /* 调用flush()方法，更新BufferStream */
            bos.flush();
            /* 结束OutputStream */
            bos.close();
            showToast("照片保存成功");
//            if (mContent instanceof MapViewFragment) {
//                if (((MapViewFragment) mContent).isCarLocationPicExist()) {
//                    onShowPhoto();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("照片保存失败");
        }
    }

    /**
     * @Title clearImageCache
     * @Description 清空缓存图片
     * @author jerry
     * @date 2015年8月21日 上午11:35:18
     */
    public void clearImageCache(File photoFile) {
        if (photoFile != null) {
            File f1 = new File(photoFile, CarLocation.ZTB_CARNO);
            if (f1 != null && f1.exists()) {
                f1.delete();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initAccountInfo();
        if (mLocClient != null) {
            mLocClient.start();
        }
    }

    /**
     * 初始化侧滑菜单
     */
    private void initLeftMenu() {
        ModularMenu modularMenu;
        String menuJson = CommonUtils.getFromAssets(getBaseContext(), "menu.json");
        if (menuJson.equals("")) {
            return;
        }
        try {
            Gson gson = new Gson();
            List<ModularMenu> modularMenuList = gson.fromJson(menuJson, new TypeToken<List<ModularMenu>>() {
            }.getType());
            if (modularMenuList == null) {
                return;
            }
            int size = modularMenuList.size();
            for (int i = 0; i < size; i++) {
                modularMenu = modularMenuList.get(i);
                if (ModularMenu.CODE_LEFTMENU.equals(modularMenu.getModularCode())) {
                    MenuItemAdapter<MenuInfo> adapter = new MenuItemAdapter<>(getBaseContext(), modularMenu.getMenuList(), modularMenu.getModularCode());
                    menuListView.setAdapter(adapter);
                }
            }
            menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MenuItemAdapter adapter = (MenuItemAdapter) parent.getAdapter();
                    Object item = adapter.getItem(position);
                    if (item instanceof MenuInfo) {
                        MenuInfo menuInfo = ((MenuInfo) item);
                        if (menuInfo.isNeedLogin()) {
                            if (!isLogin()) {
                                showToast("请先登陆");
                                startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
                                return;
                            }
                            if (MenuInfo.TO_SOCIETYSHARE.equals(menuInfo.getMenuCode()) && MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
                                if (drawer.isDrawerOpen(GravityCompat.START)) {
                                    drawer.closeDrawer(GravityCompat.START);
                                }
                                openSharePopWindow();
                            } else {
                                menuInfo.click(HomePageActivity.this);
                            }
                        } else {
                            switch (menuInfo.getMenuCode()) {
                                case MenuInfo.TO_EXIT://退出掌停宝
                                    finish();
                                    break;
                                default:
                                    menuInfo.click(HomePageActivity.this);
                                    break;

                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHome() {
        if (home == null) {
            home = HomeFragment.newInstance();
        }
        switchContent(mContent, home);
        initTitle(getString(R.string.app_name));
        initHeaderDivider(false);
        if (hasPayNews) {
            initMenuClick(R.drawable.actionbar_scan, R.string.action_shoot, this,
                    R.drawable.actionbar_pay_news, R.string.action_pay, this);
        } else {
            initMenuClick(R.drawable.actionbar_scan, R.string.action_shoot, this,
                    R.drawable.actionbar_pay_news, R.string.action_pay, this);
        }
        if (homeName.equals(MainFragment.class.getSimpleName())) {
            initDrawer();
            initBackClick(R.drawable.actionbar_menu, this);
        } else {
            initBackClick(R.drawable.actionbar_goback, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initFirst();
                }
            });
        }
    }

    private void initMain() {
        if (main == null) {
            main = MainFragment.newInstance();
        }
        switchContent(mContent, main);
        initTitle("");
        initHeaderDivider(false);
        if (homeName.equals(MainFragment.class.getSimpleName())) {
            initDrawer();
            initBackClick(R.drawable.actionbar_menu, this);
        } else {
            initBackClick(R.drawable.actionbar_goback, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initFirst();
                }
            });
        }
    }


    /**
     * 预约分享
     */
    public void goParkingSearch() {

    }


    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void setToolbarNavigationClickListener(View.OnClickListener onToolbarNavigationClickListener) {
                super.setToolbarNavigationClickListener(onToolbarNavigationClickListener);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (dataList == null || dataList.isEmpty()) {
                    if (home_slide_menu_banner_img != null) {
                        home_slide_menu_banner_img.setVisibility(View.GONE);
                    }
                    return;
                }
                switchBannerPic();
                currentPic = (currentPic + 1) % dataList.size();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else {
                if (((BaseFragment) mContent).onKeyDown(keyCode, event)) {
                    return true;
                }
                if (mContent.getClass().getSimpleName().equals(homeName)) {
                    showOkCancelAlertDialog(false, "提示", "是否退出程序?", "确认", "取消",
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dimissOkCancelAlertDialog();
                                    clearBeforeExit(HomePageActivity.this);
                                    finish();
                                    overridePendingTransition(R.anim.push_right_in,
                                            R.anim.push_left_out);
                                }
                            }, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dimissOkCancelAlertDialog();
                                }
                            });
                } else {

                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.headerMenu1:
                LogUtils.e("打开");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;

            case R.id.headerMenu2:
                if (!isLogin()) {
                    showToast("请先登陆");
                    startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
                    return;
                }
                scan();
                break;

            case R.id.headerMenu3:
                showToast("显示动态筛选菜单");
                break;
        }
    }


    public void scan() {
        if (isLogin()) {
            requestPermission(Constant.PM_CAMERA, Manifest.permission.CAMERA, new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(getBaseContext(), CaptureActivity.class);
                    startActivityForResult(intent, Constant.REQ_CODE_SCAN);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    showToast("启动失败");
                }
            });
        } else {
            showToast("请先登陆");
            startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
        }
    }

    @Override
    public void onListClick(MyLocationData locData) {

    }

    @Override
    public void onSearchBarClick() {
        if (toolbar != null) {
//            initMapSearch();
        }
    }

    @Override
    public void onSearchResultShow(GeoCodeResult result) {
        if (result == null) {
            return;
        }
//        initMap();
//        if (mContent instanceof MapViewFragment) {
//            ((MapViewFragment) mContent).showSearchResult(result);
//        }
    }

    @OnClick(R.id.account_header)
    public void onAccountHeaderClick(View v) {
        if (isLogin()) {
            Intent intent = new Intent(getBaseContext(), AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String inviteCode = getInviteCode();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQ_CODE_WECHAT:
                    shareToWechat(inviteCode);
                    break;

                case Constant.REQ_CODE_WECHAT_MOMENT:
                    shareToWechatMoment(inviteCode);
                    break;

                case Constant.REQ_CODE_SMS:
                    shareToSMS(inviteCode);
                    break;

                case Constant.REQ_CODE_CAPTURE:
                    SaveImageFromDiff(file);
                    break;

                case Constant.REQ_CODE_LOGIN:
                    initAccountInfo();
                    break;
                default:
                    if (mContent != null) {
                        mContent.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }
        }
    }

    private void initAccountInfo() {
        if (isLogin()) {
            //用户名不要全局变量
            String accountName = getSharedFileUtils().getString(SharedFileUtils.LOGIN_NAME);
            if (TextUtils.isEmpty(accountName)) {
                tvAccountName.setText(R.string.error_hava_not_login);
                tvAccountName.setTag(null);
            } else {
                tvAccountName.setText(CommonUtils.omittText(accountName, 3, 3));
                tvAccountName.setTag(accountName);
                getAccoutInfo(accountName);
            }
        } else {
            tvAccountName.setText(R.string.error_hava_not_login);
            tvAccountName.setTag(null);
        }
    }

    // 获取用户详细信息
    public void getAccoutInfo(String userName) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.USERNAME, userName);
        send(Constant.APPGETRENEWINFO, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                if (responseInfo.statusCode == 200) {
                    try {
                        BaseResult<AccountInfo> r = gson.fromJson(responseInfo.result, new TypeToken<BaseResult<AccountInfo>>() {
                        }.getType());
                        if ("0000".equals(r.code) && r.data != null) {
                            saveAndNoticeAccountInfoChange(r.data);
                        } else {
                            showToast(getString(R.string.error_network_short));
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.error_network_short));
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                LogUtils.d(msg);
                showToast(getString(R.string.error_network_short));
            }
        });
    }

    /**
     * 初始化分享popWindow
     */
    private void initSharePopWindow() {
        View contentView = getLayoutInflater().inflate(R.layout.popwindow_social_share, null);
        LinearLayout share_wechat = (LinearLayout) contentView.findViewById(R.id.share_wechat);
        LinearLayout share_wechat_moment = (LinearLayout) contentView.findViewById(R.id.share_wechat_moment);
        LinearLayout share_sms = (LinearLayout) contentView.findViewById(R.id.share_sms);
        LinearLayout space = (LinearLayout) contentView.findViewById(R.id.space);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.share_wechat:
                        String inviteCode = getInviteCode();
                        if (!TextUtils.isEmpty(inviteCode)) {
                            shareToWechat(inviteCode);
                        } else {
                            goLogin(Constant.REQ_CODE_WECHAT);
                        }
                        break;

                    case R.id.share_wechat_moment:
                        String mInviteCode = getInviteCode();
                        if (!TextUtils.isEmpty(mInviteCode)) {
                            shareToWechatMoment(mInviteCode);
                        } else {
                            goLogin(Constant.REQ_CODE_WECHAT_MOMENT);
                        }
                        break;

                    case R.id.share_sms:
                        String mInviteCode2 = getInviteCode();
                        if (!TextUtils.isEmpty(mInviteCode2)) {
                            shareToSMS(mInviteCode2);
                        } else {
                            goLogin(Constant.REQ_CODE_SMS);
                        }
                        break;

                    case R.id.space:
                        share_popWindow.dismiss();
                        break;
                }
            }
        };
        share_wechat.setOnClickListener(onClickListener);
        share_wechat_moment.setOnClickListener(onClickListener);
        share_sms.setOnClickListener(onClickListener);
        space.setOnClickListener(onClickListener);
        share_popWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * 打开分享popWindow
     */
    private void openSharePopWindow() {
        if (!share_popWindow.isShowing()) {
            share_popWindow.showAtLocation(getWindow().getDecorView(),
                    Gravity.CENTER_HORIZONTAL, 0, 0);
        } else {
            showToast("请稍后");
        }
    }

    /**
     * 分享到微信
     */
    private void shareToWechat(String inviteCode) {
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle("掌停宝");
        wechat.setText("下载掌停宝APP送优惠卷礼包,停车缴费轻松搞定,请输入我的优惠码" + inviteCode);
        wechat.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.share_logo));
        wechat.setUrl("http://ajbwechat.eanpa-gz-manager.com/weChat/app/appUser/toInvitationPage?code="
                + inviteCode);
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(HomePageActivity.this, Wechat.NAME);
        weixin.setPlatformActionListener(HomePageActivity.this);
        weixin.share(wechat);
    }

    /**
     * 分享到微信朋友圈
     */
    private void shareToWechatMoment(String inviteCode) {
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle("下载掌停宝APP送优惠卷礼包,停车缴费轻松搞定,请输入我的优惠码" + inviteCode);
        wechat.setImageData(BitmapFactory.decodeResource(getResources(), R.mipmap.share_logo));
        wechat.setUrl("http://ajbwechat.eanpa-gz-manager.com/weChat/app/appUser/toInvitationPage?code="
                + inviteCode);
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(HomePageActivity.this, WechatMoments.NAME);
        weixin.setPlatformActionListener(HomePageActivity.this);
        weixin.share(wechat);
    }

    /**
     * 短信分享
     */
    private void shareToSMS(final String inviteCode) {
        new BaiduShortUrlUtils(
                "http://ajbwechat.eanpa-gz-manager.com/weChat/app/appUser/toInvitationPage?code="
                        + inviteCode,
                new BaiduShortUrlUtils.OnGetShortUrlListener() {
                    @Override
                    public void onResult(String shortUrl) {
                        ShortMessage.ShareParams shareParams = new ShortMessage.ShareParams();
                        shareParams.setTitle("掌停宝");
                        shareParams.setText("下载掌停宝APP送优惠卷礼包,停车缴费轻松搞定,请输入我的优惠码 "
                                + shortUrl);
                        Platform sms = ShareSDK.getPlatform(ShortMessage.NAME);
                        sms.share(shareParams);
                    }
                }).start();
    }

    /**
     * 获取邀请码
     *
     * @return
     */
    private String getInviteCode() {
        String inviteCode = getSharedFileUtils().getString(SharedFileUtils.INVITE);
        if (!TextUtils.isEmpty(inviteCode)) {
            return inviteCode;
        } else {
            return null;
        }
    }

    /**
     * 跳转到登陆界面
     */
    private void goLogin(int mRequestCode) {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(loginIntent, mRequestCode);
    }

    private void getLocalSlideBanner() {
        String bannerStr = sharedFileUtils.getString(SharedFileUtils.BANNER_LIST_LOCAL_HOME_SLIDE_MENU);
        if (bannerStr == null || bannerStr.equals("")) {
        } else {
            dataList = (List<AdInfo>) ObjectUtil.getObject(bannerStr);
        }

    }

    private void switchBannerPic() {
        if (dataList == null || dataList.size() < currentPic) {
            return;
        }
        AdInfo adInfo = dataList.get(currentPic);
        home_slide_menu_banner_img.setVisibility(View.VISIBLE);
        if (adInfo.getType() == 0 && !TextUtils.isEmpty(adInfo.getUrl())) {
            Bitmap bitmap = BitmapFactory.decodeFile(adInfo.getUrl());
            home_slide_menu_banner_img.setImageBitmap(bitmap);
        } else if (adInfo.getType() == 1) {
            home_slide_menu_banner_img.setImageResource(adInfo.getRes());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    /**
     * shareSDK分享的三个回调
     *
     * @param platform
     * @param i
     * @param hashMap
     */

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(getApplicationContext(), "分享取消", Toast.LENGTH_SHORT).show();
    }

    /**
     * 开始拍照
     */
    @Override
    public void onStartCaptrue() {
        if (photoFile.exists()) {
            file = new File(photoFile, CarLocation.ZTB_CARNO);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, Constant.REQ_CODE_CAPTURE);
    }

    @Override
    public void onShowPhoto() {
        Bitmap bitmap = CarLocation.getCarLocationBitmap(getBaseContext());
        initCarLocationPopWindow(bitmap);
    }

    public void launchApp(Context context, String packageName) {
        // 启动目标应用
        if (App.isPackageExist(context, packageName)) {
            // 获取目标应用安装包的Intent
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    packageName);
            startActivity(intent);
        }
        // 安装目标应用
        else {
            showOkCancelAlertDialog(false, "提示", "您还未安装周边优惠，即刻前往下载", "确定",
                    "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            dimissOkCancelAlertDialog();

                            Uri uri = Uri
                                    .parse("http://www.doyao.cn/wx/youhui/index.html?src=ztb");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            Intent chooser = Intent.createChooser(intent,
                                    "选择浏览器打开");
                            startActivity(chooser);

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            dimissOkCancelAlertDialog();
                        }
                    });

        }
    }


    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.setCustomAnimations(X
//                    android.R.anim.fade_in, R.anim.fade_out);
            if (from != null) {
                transaction.hide(from);
            }
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.add(R.id.contentLayout, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                showTip("服务端网络定位失败", null, null);
                return;
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                showTip("网络不通导致定位失败，请检查网络是否通畅", null, null);
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                showTip("定位失败，可能是处于飞行模式下，若不是可以试着重启手机", null, null);
                return;
            }
            LogUtils.v(location.getLatitude() + "---"
                    + location.getLongitude() + "---"
                    + location.getDirection());
            sharedFileUtils.putString(SharedFileUtils.LAST_LOC, location.getLatitude() + "," + location.getLongitude());
            sharedFileUtils.putString(SharedFileUtils.LAST_CITY, location.getCity());
            onLocate(location);
            dealRunnableMap();
        }

    }

    @Override
    protected void onPause() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
    }


    @Override
    public void onLocate(BDLocation location) {
        if (location == null) {
            return;
        }
        LogUtils.d("定位成功");
        locData = new MyLocationData.Builder()
                .satellitesNum(location.getSatelliteNumber())
                .speed(location.getSpeed())
                .direction(location.getDirection())
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        if (mContent != null && mContent instanceof OnLocateListener) {
            ((OnLocateListener) mContent).onLocate(location);
        }
    }

    @Override
    public MyLocationData getLocation() {
        return locData;
    }


    //    @ViewInject(R.id.rl_nav)
//    RelativeLayout rl_nav;
//    @ViewInject(R.id.mHsv)
//    SyncHorizontalScrollView mHsv;
//    @ViewInject(R.id.rg_nav_content)
//    RadioGroup rg_nav_content;
//    @ViewInject(R.id.iv_nav_indicator)
//    ImageView iv_nav_indicator;
//    @ViewInject(R.id.iv_nav_left)
//    ImageView iv_nav_left;
//    @ViewInject(R.id.iv_nav_right)
//    ImageView iv_nav_right;

    private View root;
    private PopupWindow popup;
    private int currentIndicatorLeft = 0;
    List<Condition> conditionList;
    Map<String, BaseAdapter> adapterList;


    protected void initPopupWindows(View v) {

        // 装载R.layout.popup对应的界面布局
        if (root == null || popup == null) {
            root = getLayoutInflater()
                    .inflate(R.layout.filter_layout, null);
            // 创建PopupWindow对象
            popup = new PopupWindow(root);
            popup.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // popup.setAnimationStyle(R.anim.in);
            popup.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
            popup.setOutsideTouchable(true);// 以及下一句 同时写才会有效
            popup.setFocusable(true);// 获取焦点
        }

        RelativeLayout rl_nav = (RelativeLayout) root.findViewById(R.id.rl_nav);
        mFirstListViewLayout = (FrameLayout) root.findViewById(R.id.mFirstListViewLayout);
        mSecondListViewLayout = (FrameLayout) root.findViewById(R.id.mSecondListViewLayout);
        final SyncHorizontalScrollView mHsv = (SyncHorizontalScrollView) root.findViewById(R.id.mHsv);
        rg_nav_content = (LinearLayout) root.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) root.findViewById(R.id.iv_nav_indicator);
        ImageView iv_nav_left = (ImageView) root.findViewById(R.id.iv_nav_left);
        ImageView iv_nav_right = (ImageView) root.findViewById(R.id.iv_nav_right);
        ListView mListView = (ListView) root.findViewById(R.id.mListView);

        indicatorWidth = DensityUtil.getScreenWidth(getBaseContext()) / 4;//一行3个
        RelativeLayout.LayoutParams cursor_Params = (RelativeLayout.LayoutParams) iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        iv_nav_indicator.setLayoutParams(cursor_Params);
        mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);

        SideBar sideBar = (SideBar) root.findViewById(R.id.sidebar);
        TextView dialog = (TextView) root.findViewById(R.id.dialog);
        TextView hoverItemText = (TextView) root
                .findViewById(R.id.hover_listview_item);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                if (mFirstListViewLayout == null) {
                    return;
                }
                ListView firstLV = (ListView) mFirstListViewLayout.findViewById(R.id.mListView);
                if (firstLV == null) {
                    return;
                }
                SortAdapter adapter = (SortAdapter) firstLV.getAdapter();
                if (adapter == null) {
                    return;
                }
                int position = adapter.getPositionForSection(s
                        .charAt(0));
                if (position != -1) {
                    firstLV.setSelection(position);
                }
            }
        });
        //获取布局填充器
        LayoutInflater mInflater = (LayoutInflater) LayoutInflater.from(this);
        rg_nav_content.removeAllViews();
        if (conditionList == null || conditionList.isEmpty()) {
            //读取数据
            String conditionStr = CommonUtils.getFromAssets(getBaseContext(), "condition.json");
            if (TextUtils.isEmpty(conditionStr)) {
                return;
            }
            conditionList = gson.fromJson(conditionStr, new TypeToken<List<Condition>>() {
            }.getType());
        }
        if (conditionList == null || conditionList.size() == 0) {
            return;
        }
        if (adapterList == null) {
            adapterList = new LinkedHashMap<>();
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < rg_nav_content.getChildCount(); i++) {
                    ((TextView) rg_nav_content.getChildAt(i)).setSelected(false);
                }
                v.setSelected(true);
                int checkedId = v.getId();
                TranslateAnimation animation = new TranslateAnimation(
                        currentIndicatorLeft, v.getLeft(), 0f, 0f);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);

                //执行位移动画
                ImageView iv_nav_indicator = (ImageView) v.getTag(R.id.iv_nav_indicator);
                iv_nav_indicator.setVisibility(View.VISIBLE);
                iv_nav_indicator.startAnimation(animation);

                //记录当前 下标的距最左侧的 距离
                int lastIndicatorLeft = currentIndicatorLeft;
                currentIndicatorLeft = ((TextView) v).getLeft();
                LogUtils.d("lastIndicatorLeft=" + lastIndicatorLeft + ";currentIndicatorLeft=" + currentIndicatorLeft);
                LogUtils.d("ScrollX=" + mHsv.getScrollX() + ";ScrollY=" + mHsv.getScrollY());

                mHsv.smoothScrollTo(currentIndicatorLeft - indicatorWidth, 0);
                //数据显示
                Condition c = (Condition) v.getTag();
                if (c.getConditionList() != null) {//单列表处理
                    if (c.getConditionList().size() > 0) {
                        mFirstListViewLayout.setVisibility(View.VISIBLE);
                        mSecondListViewLayout.setVisibility(View.GONE);
                        ListView lv = (ListView) v.getTag(R.id.mListView);
                        if (lv != null && c != null) {
                            FilterConditionAdapter adapter = new FilterConditionAdapter(getBaseContext(), c);
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    FilterConditionAdapter adapter = (FilterConditionAdapter) parent.getAdapter();
                                    ConditionValue cv = (ConditionValue) adapter.getItem(position);
                                    adapter.check(cv);
                                }
                            });
                        }
                    } else {
                        mFirstListViewLayout.setVisibility(View.GONE);
                        mSecondListViewLayout.setVisibility(View.GONE);
                    }
                } else {//区域列表处理
                    mFirstListViewLayout.setVisibility(View.GONE);
                    mSecondListViewLayout.setVisibility(View.GONE);
                    DbUtils dbCity = MyApplication.getDbCity(getBaseContext());
                    try {
                        if (dbCity.count(ProvinceInfo.class) == 0) {
                            //读取城市列表数据
                            CityUpdateTask cityUpdateTask = new CityUpdateTask(getBaseContext()) {
                                @Override
                                protected void onPostExecute(Boolean aBoolean) {
                                    if (!aBoolean) {
                                        showToast("读取城市区域失败");
                                    } else {
                                        initCityListView();
                                    }
                                }

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                }
                            };
                            cityUpdateTask.execute();
                        } else {
                            initCityListView();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }


                }


            }
        };
        Condition c;
        for (int i = 0; i < conditionList.size(); i++) {
            c = conditionList.get(i);
            TextView rb = (TextView) mInflater.inflate(R.layout.filter_item, null);
            rb.setId(i);
            rb.setText(c.getConditionName());
            rb.setLayoutParams(new LinearLayout.LayoutParams(indicatorWidth,
                    RadioGroup.LayoutParams.MATCH_PARENT));
            rb.setTag(c);
            rg_nav_content.addView(rb);
            rb.setTag(R.id.iv_nav_indicator, iv_nav_indicator);
            rb.setTag(R.id.mHsv, mHsv);
            rb.setTag(R.id.mListView, mListView);
            rb.setOnClickListener(onClickListener);
        }
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rg_nav_content.removeAllViews();
                mHsv.smoothScrollTo(0, 0);
                iv_nav_indicator.setVisibility(View.GONE);
                mFirstListViewLayout.setVisibility(View.GONE);
                mSecondListViewLayout.setVisibility(View.GONE);

            }
        });
        if (!popup.isShowing()) {
            popup.showAsDropDown(v);
        }

    }

    //显示区域列表
    private void initCityListView() {
        DbUtils dbUtils = MyApplication.getDbCity(getBaseContext());
        // 实例化汉字转拼音类
        ArrayList<SortModel> SourceDateList = new ArrayList<SortModel>();
        Set<String> set = new HashSet<String>();
        try {
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setSql("select distinct cityCode,cityPinyin,cityName from " + TableUtils.getTableName(ProvinceInfo.class) + " order By cityPinyin asc;");
            List<DbModel> list = dbUtils.findDbModelAll(sqlInfo);

            if (list != null && list.size() > 0) {
                mFirstListViewLayout.setVisibility(View.VISIBLE);
                mSecondListViewLayout.setVisibility(View.VISIBLE);
                SortAdapter sortAdapter = new SortAdapter(getBaseContext(), SortModel.getCityList(list));
                String currentCity = sharedFileUtils.getString(SharedFileUtils.LAST_CITY);
                sortAdapter.setCurrentCity(currentCity);
                sortAdapter.setSelectItem(0);
                ListView lv = ((ListView) mFirstListViewLayout.findViewById(R.id.mListView));
                lv.setAdapter(sortAdapter);
                initSecondDistrict(currentCity, sortAdapter);//初始化当前城市区域
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SortAdapter adapter = (SortAdapter) parent.getAdapter();
                        adapter.setSelectItem(position);
                        SortModel item = (SortModel) adapter.getItem(position);
                        if (item == null) {
                            return;
                        }
                        String selectCity = item.getName();
                        initSecondDistrict(selectCity, adapter);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSecondDistrict(String selectCity, SortAdapter adapter) {
        try {
            DbUtils dbUtils = MyApplication.getDbCity(getBaseContext());
            List<ProvinceInfo> districtList = dbUtils
                    .findAll(Selector
                            .from(ProvinceInfo.class)
                            .where(WhereBuilder.b().and(
                                    "cityName", "=",
                                    selectCity))
                            .orderBy("districtPinyin", false));
            ProvinceInfo pi = new ProvinceInfo();
            pi.setDistrictName("全区域");
            districtList.add(0, pi);
            if (selectCity.equals(adapter.getCurrentCity())) {
                pi = new ProvinceInfo();
                pi.setDistrictName("附近");
                districtList.add(0, pi);
            }
            GridView districtGridView = (GridView) mSecondListViewLayout.findViewById(R.id.pop_city_gridview);
            SortDistrictAdapter sortDistrictAdapter = new SortDistrictAdapter(getBaseContext(), districtList);
            districtGridView.setAdapter(sortDistrictAdapter);
            sortDistrictAdapter.setSelectItem(0);
            districtGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SortDistrictAdapter adapter = (SortDistrictAdapter) parent.getAdapter();
                    adapter.setSelectItem(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
