package com.ajb.merchants.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.FilterConditionAdapter;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.adapter.PopupWindowAdapter;
import com.ajb.merchants.adapter.SortAdapter;
import com.ajb.merchants.adapter.SortDistrictAdapter;
import com.ajb.merchants.fragment.BaseFragment;
import com.ajb.merchants.fragment.HomeFragment;
import com.ajb.merchants.fragment.MainFragment;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.model.AdInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.CarInParkingBuilder;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.model.ProvinceInfo;
import com.ajb.merchants.model.SortModel;
import com.ajb.merchants.model.filter.Condition;
import com.ajb.merchants.model.filter.ConditionValue;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.task.CityUpdateTask;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.RoundedImageView;
import com.ajb.merchants.view.ScaleImageView;
import com.ajb.merchants.view.SideBar;
import com.ajb.merchants.view.SyncHorizontalScrollView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.App;
import com.util.DensityUtil;
import com.util.ObjectUtil;
import com.zxing.activity.CaptureActivity;

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

public class HomePageActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener, OnItemClickListener {
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
    @ViewInject(R.id.imgAvatar)
    RoundedImageView imgAvatar;
    @ViewInject(R.id.tvAccountName)
    TextView tvAccountName;
    @ViewInject(R.id.tvAccountDesc)
    TextView tvAccountDesc;
    @ViewInject(R.id.home_slide_menu_banner_img)
    ScaleImageView home_slide_menu_banner_img;
    View menuPay;
    private Fragment mContent;
    private FragmentManager manager;
    private boolean hasPayNews = true;
    private int currentPic;
    private List<AdInfo> dataList;
    private HomeFragment home;
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private String homeName;
    private LinearLayout rg_nav_content;
    private ImageView iv_nav_indicator;
    private int indicatorWidth = 0;
    private FrameLayout mFirstListViewLayout, mSecondListViewLayout;
    private MainFragment main;
    private Dialog carNumInputDialog, cardInputDialog;
    private View carNoPopupView;
    private PopupWindow carNoPopupWindow;
    private MenuItemAdapter<MenuInfo> leftMenuListAdapter;

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
        getLocalSlideBanner();
        updateAccountSettingInfo(getAccountSettingInfo());
//        ShareSDK.initSDK(this);
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
        initAccountSettingInfo();
    }

    @Override
    protected void updateAccountSettingInfo(AccountSettingInfo info) {
        super.updateAccountSettingInfo(info);
        if (info == null) {
            return;
        }
        initAccountInfo(imgAvatar, tvAccountName, null, info.getAccountInfo());
        if (tvAccountDesc != null) {
            tvAccountDesc.setText(TextUtils.isEmpty(info.getAccountInfo().getPhone()) ? "" : info.getAccountInfo().getPhone());
        }
        initMenu(info.getModularMenus());
        if (mContent != null && mContent instanceof MainFragment) {
            MainFragment fragment = (MainFragment) mContent;
            initAccountInfo(fragment.imgAvatar, null, fragment.tvStoreName, info.getAccountInfo());
            BitmapUtils bitmapUtils = new BitmapUtils(getBaseContext());
            fragment.initHeaderBg(getBaseContext(), info.getAccountInfo().getCoverimgUrl());
        }
    }

    private void initAccountInfo(RoundedImageView imgAvatar, TextView tvAccountName, TextView tvStoreName, AccountInfo accountInfo) {
        BitmapUtils bitmapUtils = new BitmapUtils(getBaseContext());
        if (accountInfo == null) {
            if (imgAvatar != null) {
                imgAvatar.setImageResource(R.mipmap.default_avatar);
            }
            if (tvAccountName != null) {
                tvAccountName.setText(getString(R.string.tip_tourist));
            }
            return;
        }
        if (imgAvatar != null) {
            bitmapUtils.display(imgAvatar, accountInfo.getHeadimgUrl(), new BitmapLoadCallBack<RoundedImageView>() {
                @Override
                public void onLoadCompleted(RoundedImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                    container.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadFailed(RoundedImageView container, String uri, Drawable drawable) {
                    container.setImageResource(R.mipmap.default_avatar);
                }
            });
        }
        if (tvAccountName != null) {
            tvAccountName.setText(TextUtils.isEmpty(accountInfo.getAccountName()) ? getString(R.string.tip_tourist) : accountInfo.getAccountName());
        }
        if (tvStoreName != null) {
            tvStoreName.setText(TextUtils.isEmpty(accountInfo.getStoreName()) ? "" : accountInfo.getStoreName());
        }
    }

    /**
     * 初始化侧滑菜单
     */
    private void initMenu(List<ModularMenu> modularMenuList) {
        ModularMenu modularMenu;
        if (modularMenuList == null) {
            return;
        }
        int size = modularMenuList.size();
        for (int i = 0; i < size; i++) {
            modularMenu = modularMenuList.get(i);
            if (ModularMenu.CODE_COUPON.equals(modularMenu.getModularCode())) {
                if (mContent != null && mContent instanceof MainFragment) {
                    ((MainFragment) mContent).
                            initCouponMenuList(getBaseContext(), modularMenu, this);
                }
            } else if (ModularMenu.CODE_MAIN_MENU.equals(modularMenu.getModularCode())) {
                if (mContent != null && mContent instanceof MainFragment) {
                    ((MainFragment) mContent).
                            initMainMenuList(getBaseContext(), modularMenu, this);
                }
            } else if (ModularMenu.CODE_LEFTMENU.equals(modularMenu.getModularCode())) {
                initLeftMenuList(modularMenu, this);
            }
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

    @Override
    protected void onResume() {
        super.onResume();

//        if (mLocClient != null) {
//            mLocClient.start();
//        }
    }

    /**
     * 初始化侧滑菜单
     */
    private void initLeftMenu(AccountSettingInfo asi) {

        ModularMenu modularMenu, leftMenu = null;
        if (asi != null && asi.getModularMenus() != null) {
            List<ModularMenu> modularMenuList = asi.getModularMenus();
            if (modularMenuList != null) {
                int size = modularMenuList.size();
                for (int i = 0; i < size; i++) {
                    modularMenu = modularMenuList.get(i);
                    if (ModularMenu.CODE_LEFTMENU.equals(modularMenu.getModularCode())) {
                        leftMenu = modularMenu;
                        break;
                    }
                }
            }
        }
        initLeftMenuList(leftMenu, this);
    }

    public void initLeftMenuList(ModularMenu mm, AdapterView.OnItemClickListener listener) {
        if (leftMenuListAdapter == null) {
            leftMenuListAdapter = new MenuItemAdapter<MenuInfo>(getBaseContext(), null, ModularMenu.CODE_LEFTMENU);
            menuListView.setAdapter(leftMenuListAdapter);
        }
        if (mm != null) {
            leftMenuListAdapter.update(mm.getMenuList(), mm.getModularCode());
        }
        menuListView.setOnItemClickListener(listener);
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
    }

    @OnClick(R.id.account_header)
    public void onAccountHeaderClick(View v) {
        if (isLogin()) {
            Intent intent = new Intent(getBaseContext(), MerchantDetailActivity.class);
            startActivity(intent);
        } else {
            startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQ_CODE_LOGIN:
                    initAccountSettingInfo();
//                    if (mContent != null) {
//                        mContent.onActivityResult(requestCode, resultCode, data);
//                    }
                    break;
                case Constant.REQ_CODE_LOGOUT:
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    intent.putExtra(Constant.KEY_FROM, HomePageActivity.class.getName());//标明是Home页面跳转
                    startActivity(intent);
                    finish();
                    break;
                default:
                    if (mContent != null) {
                        mContent.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }
        }
    }

    private void initAccountSettingInfo() {
        if (isLogin()) {
            getAccoutInfo();
        }
    }

    // 获取用户详细信息
    public void getAccoutInfo() {
        send(Constant.PK_MAIN_SETTING, null, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (mContent != null) {
                    if (mContent instanceof MainFragment) {
                        ((MainFragment) mContent).setRefreshing(true);
                    }
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mContent != null) {
                    if (mContent instanceof MainFragment) {
                        ((MainFragment) mContent).setRefreshing(false);
                    }
                }
                if (responseInfo.statusCode == 200) {
                    BaseResult<AccountSettingInfo> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<AccountSettingInfo>>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    if ("0000".equals(result.code)) {
                        saveAndNoticeAccountInfoChange(result.data);
                    } else {
                        showToast(result.msg);
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mContent != null) {
                    if (mContent instanceof MainFragment) {
                        ((MainFragment) mContent).setRefreshing(false);
                    }
                }
                fail(error, msg);
            }
        });
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
     * 菜单处理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuItemAdapter adapter = (MenuItemAdapter) parent.getAdapter();
        Object item = adapter.getItem(position);
        if (item instanceof MenuInfo) {
            MenuInfo menuInfo = ((MenuInfo) item);
            if (!MenuInfo.TYPE_NORMAL.equals(menuInfo.getType())) {
                return;
            }
            if (menuInfo.isNeedLogin()) {
                if (!isLogin()) {
                    showToast("请先登陆");
                    startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
                    return;
                }
                if (MenuInfo.TYPE_OPERATE_NATIVE.equals(menuInfo.getOperateType())) {
                    switch (menuInfo.getMenuCode()) {
                        case MenuInfo.TO_COUPON_SCAN:
                            scan();
                            break;
                        case MenuInfo.TO_COUPON_CAR_NUM:
                            showCarNumInputDialog(false);
                            break;
                        case MenuInfo.TO_COUPON_CARD:
                            showCardInputDialog(true);
                            break;
                        default:
                            menuInfo.click(HomePageActivity.this);
                            break;
                    }
                }

            } else {
                switch (menuInfo.getMenuCode()) {
                    case MenuInfo.TO_EXIT://退出掌停宝
                        finish();
                        break;
                    case MenuInfo.TO_COUPON_SCAN:
                        scan();
                        break;
                    case MenuInfo.TO_COUPON_CAR_NUM:
                        showCarNumInputDialog(true);
                        break;
                    case MenuInfo.TO_COUPON_CARD:
                        showCardInputDialog(true);
                        break;
                    case MenuInfo.TO_SETTING:
                        Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                        startActivityForResult(intent, Constant.REQ_CODE_LOGOUT);
                        break;
                    default:
                        menuInfo.click(HomePageActivity.this);
                        break;

                }
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
                            lv.setOnItemClickListener(new OnItemClickListener() {
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
                lv.setOnItemClickListener(new OnItemClickListener() {
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
            districtGridView.setOnItemClickListener(new OnItemClickListener() {
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

    protected void showCarNumInputDialog(boolean canCancel) {
        String title = getString(R.string.title_coupon_car_num);
        String okText = getString(R.string.action_search);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edCarno = null;
                TextView tvCode = null;
                switch (v.getId()) {
                    case R.id.sure_btn:
                        edCarno = (EditText) v.getTag(R.id.edCarno);
                        tvCode = (TextView) v.getTag(R.id.tvCode);
                        if (tvCode == null || edCarno == null) {
                            return;
                        }
                        String province = tvCode.getText().toString().trim();
                        String carNo = edCarno.getText().toString().trim()
                                .toUpperCase();
                        if (TextUtils.isEmpty(province)) {
                            showToast(getString(R.string.tip_input_car_num_area));
                            Animation shake = AnimationUtils.loadAnimation(getBaseContext(),
                                    R.anim.shake);
                            ((View) edCarno.getParent()).startAnimation(shake);
                            return;
                        }
                        if (TextUtils.isEmpty(carNo)) {
                            showToast(getString(R.string.tip_input_car_num));
                            Animation shake = AnimationUtils.loadAnimation(getBaseContext(),
                                    R.anim.shake);
                            ((View) edCarno.getParent()).startAnimation(shake);
                            return;
                        }
                        String carNoStr = province + carNo;
                        if (!isCarNumberValidated(carNoStr)) {
                            showToast(getString(R.string.tip_input_car_num_correct));
                            Animation shake = AnimationUtils.loadAnimation(getBaseContext(),
                                    R.anim.shake);
                            ((View) edCarno.getParent()).startAnimation(shake);
                            return;
                        }
                        if (carNumInputDialog != null) {
                            carNumInputDialog.dismiss();
                        }
                        Intent intent = new Intent(getBaseContext(), CouponGivingActivity.class);
                        intent.putExtra(Constant.KEY_TITLE, getString(R.string.title_coupon_car_num));
                        CarInParkingBuilder carInParkingBuilder = new CarInParkingBuilder();
                        carInParkingBuilder.setCarNo(carNoStr);
                        intent.putExtra(Constant.KEY_CARINPARKING, carInParkingBuilder);
                        startActivity(intent);
                        break;
                    case R.id.cancel_btn:
                        if (carNumInputDialog != null) {
                            carNumInputDialog.dismiss();
                        }
                        break;
                    case R.id.tvCode:
                        edCarno = (EditText) v.getTag(R.id.edCarno);
                        if (edCarno != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edCarno.getWindowToken(), 0);
                        }
                        showCarNoPopupWindow((TextView) v);
                        break;
                }
            }
        };
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_carnum_input_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        tvTitle.setText(title);
        View submit = contentView.findViewById(R.id.sure_btn);
        View cancel = contentView.findViewById(R.id.cancel_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        TextView tvCode = (TextView) contentView
                .findViewById(R.id.tvCode);
        EditText edCarno = (EditText) contentView
                .findViewById(R.id.edCarno);
        tvCode.setTag(R.id.edCarno, edCarno);
        edCarno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (carNumInputDialog == null) {
                    return;
                }
                EditText edCarno = (EditText) carNumInputDialog.getWindow().getDecorView()
                        .findViewById(R.id.edCarno);
                if (edCarno == null) {
                    return;
                }
                edCarno.removeTextChangedListener(this);
                String str = s.toString().trim().toUpperCase();
                edCarno.setText(str);
                edCarno.setSelection(str.length());// 重新设置光标位置
                edCarno.addTextChangedListener(this);// 重新绑
            }
        });
        submit.setTag(R.id.tvCode, tvCode);
        submit.setTag(R.id.edCarno, edCarno);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        submit.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
        tvCode.setOnClickListener(listener);
        if (carNumInputDialog == null) {
            carNumInputDialog = new Dialog(this, R.style.Dialog);
            Window win = carNumInputDialog.getWindow();
            win.setContentView(contentView);
            carNumInputDialog.setCancelable(canCancel);
            carNumInputDialog.show();
        } else {
            Window win = carNumInputDialog.getWindow();
            win.setContentView(contentView);
            carNumInputDialog.setCancelable(canCancel);
            if (!carNumInputDialog.isShowing()) {
                carNumInputDialog.show();
            }
        }
    }

    /**
     * 卡编号弹窗
     */
    protected void showCardInputDialog(boolean canCancel) {
        String title = getString(R.string.tip_input_card);
        String okText = getString(R.string.action_search);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edCard = null;
                switch (v.getId()) {
                    case R.id.sure_btn:
                        edCard = (EditText) v.getTag(R.id.edCard);
                        String card = edCard.getText().toString().trim()
                                .toUpperCase();
                        if (TextUtils.isEmpty(card)) {
                            showToast(getString(R.string.tip_input_card));
                            Animation shake = AnimationUtils.loadAnimation(getBaseContext(),
                                    R.anim.shake);
                            ((View) edCard.getParent()).startAnimation(shake);
                            return;
                        }
                        if (cardInputDialog != null) {
                            cardInputDialog.dismiss();
                        }
                        Intent intent = new Intent(getBaseContext(), CouponGivingActivity.class);
                        intent.putExtra(Constant.KEY_TITLE, getString(R.string.title_coupon_card));
                        CarInParkingBuilder carInParkingBuilder = new CarInParkingBuilder();
                        carInParkingBuilder.setCarSN(card);
                        intent.putExtra(Constant.KEY_CARINPARKING, carInParkingBuilder);
                        startActivity(intent);
                        break;
                    case R.id.cancel_btn:
                        if (cardInputDialog != null) {
                            cardInputDialog.dismiss();
                        }
                        break;
                    case R.id.tvCode:
                        edCard = (EditText) v.getTag(R.id.edCarno);
                        if (edCard != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edCard.getWindowToken(), 0);
                        }
                        showCarNoPopupWindow((TextView) v);
                        break;
                }
            }
        };
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_card_input_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        tvTitle.setText(title);
        View submit = contentView.findViewById(R.id.sure_btn);
        View cancel = contentView.findViewById(R.id.cancel_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        EditText edCard = (EditText) contentView
                .findViewById(R.id.edCard);
        submit.setTag(R.id.edCard, edCard);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        submit.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
        if (cardInputDialog == null) {
            cardInputDialog = new Dialog(this, R.style.Dialog);
            Window win = cardInputDialog.getWindow();
            win.setContentView(contentView);
            cardInputDialog.setCancelable(canCancel);
            cardInputDialog.show();
        } else {
            Window win = cardInputDialog.getWindow();
            win.setContentView(contentView);
            cardInputDialog.setCancelable(canCancel);
            if (!cardInputDialog.isShowing()) {
                cardInputDialog.show();
            }
        }
    }


    /**
     * 创建popupWindow菜单
     */
    private void showCarNoPopupWindow(TextView textView) {
        // TODO Auto-generated method stub
        if (carNoPopupView == null || carNoPopupWindow == null) {
            carNoPopupView = getLayoutInflater().inflate(R.layout.popmenu, null);
            /** 初始化PopupWindow */
            carNoPopupWindow = new PopupWindow(carNoPopupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            carNoPopupWindow.setFocusable(true);// 取得焦点
            carNoPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            /** 设置PopupWindow弹出和退出时候的动画效果 */
//        carNoPopupWindow.setAnimationStyle(R.style.animation);
            /** 网格布局界面 */
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.space:
                            carNoPopupWindow.dismiss();
                            break;

                        default:
                            break;
                    }
                }
            };
            MyGridView gridView = (MyGridView) carNoPopupView.findViewById(R.id.gridView);
            carNoPopupView.findViewById(R.id.space).setOnClickListener(onClickListener);
            /** 设置网格布局的适配器 */
            BaseListAdapter<String> adapter = PopupWindowAdapter.getAdapter(getBaseContext());
            adapter.setChecked(textView.getText().toString());
            gridView.setAdapter(adapter);
            /** 设置网格布局的菜单项点击时候的Listener */
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BaseListAdapter<String> adapter = (BaseListAdapter<String>) parent
                            .getAdapter();
                    String item = adapter.getItem(position);
                    adapter.setChecked(item);
                    if (carNumInputDialog != null) {
                        TextView tvCode = (TextView) carNumInputDialog.getWindow().getDecorView().findViewById(R.id.tvCode);
                        if (tvCode != null) {
                            tvCode.setText(item);
                        }
//                        EditText edCarno = (EditText) carNumInputDialog.getWindow().getDecorView().findViewById(R.id.edCarno);
//                        if (edCarno != null) {
//                            edCarno.setSelection(edCarno.getText().length());
//                            edCarno.setSelected(true);
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                            imm.showSoftInput(edCarno, InputMethodManager.SHOW_FORCED);
//                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                        }
                    }
                    if (carNoPopupWindow != null) {
                        carNoPopupWindow.dismiss();
                    }
                }
            });
        }
        carNoPopupWindow.showAtLocation(textView, Gravity.BOTTOM, 0, 0);

    }
}
