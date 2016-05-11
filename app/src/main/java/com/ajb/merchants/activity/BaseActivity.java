package com.ajb.merchants.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.interfaces.OnViewErrorListener;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.model.ShareInfo;
import com.ajb.merchants.others.MyApplication;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.AutoWrapcontentListView;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.model.UpdateContent;
import com.model.UpdateInfo;
import com.model.UpdateResult;
import com.umeng.analytics.MobclickAgent;
import com.util.App;
import com.util.NumberUtil;
import com.util.ObjectUtil;
import com.util.PathManager;
import com.util.UpdateManager;
import com.util.UpdateManager.UpdateCallback;

import org.apache.http.NameValuePair;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wechat.utils.WechatClientNotExistException;

public class BaseActivity extends AppCompatActivity implements BaiduNaviManager.RoutePlanListener, OnViewErrorListener, PlatformActionListener {

    public static final String CHECK_ACTION = "com.ajb.anjubao.intelligent.activity.CHECK_ACTION";
    protected Gson gson;
    protected boolean isNavigationStarting;//启动导航设置为true
    private View errorView;
    private ArrayList<String> pmList;
    private int id;
    protected Map<Integer, Runnable> runnableMap;//延时定位处理
    private View rootMenuView;
    private PopupWindow popupMenu;
    private MenuItemAdapter<MenuInfo> popupMenuListAdapter;
    private List<MenuInfo> leftMenus;
    private List<MenuInfo> rightMenus;
    private AdapterView.OnItemClickListener leftMenusItemClick;
    private AdapterView.OnItemClickListener rightMenusItemClick;
    private MyBroadcast broadcastReceiver;

    /**
     * @return 定位任务列表
     */
    protected Map<Integer, Runnable> getRunnableMap() {
        if (runnableMap == null) {
            runnableMap = new HashMap<>();
        }
        return runnableMap;
    }

    /**
     * 处理定位请求
     */
    protected void dealRunnableMap() {
        if (!getRunnableMap().isEmpty()) {
            for (Runnable r :
                    getRunnableMap().values()) {
                r.run();
            }
        }
    }

    public SharedFileUtils getSharedFileUtils() {
        if (sharedFileUtils == null) {
            sharedFileUtils = new SharedFileUtils(BaseActivity.this);
        }
        return sharedFileUtils;
    }

    public boolean isLogin() {
        return getSharedFileUtils().getBoolean(SharedFileUtils.IS_LOGIN);
    }

    public String getLoginName() {
        return sharedFileUtils.getString(SharedFileUtils.LOGIN_NAME);
    }

    class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                checkUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final static int NO_RES = R.id.NO_RES;// 不修改
    public final static int NO_ICON = R.id.NO_ICON;// 不显示
    private Dialog okAlertDialog;
    private Dialog okCancelAlertDialog;
    private UpdateReceiver updateReceiver;
    protected SharedFileUtils sharedFileUtils;
    protected Toolbar toolbar;
    private TextView headerMenu2, headerMenu3, headerMenu1;
    private String mSDCardPath;
    private BNRoutePlanNode mBNRoutePlanNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlway();
        getSharedFileUtils();
        mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        updateReceiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHECK_ACTION);
        registerReceiver(updateReceiver, filter);
        gson = new Gson();
    }

    @Override
    protected void onDestroy() {
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
            updateReceiver = null;
        }
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            overridePendingTransition(R.anim.push_right_in,
//                    R.anim.push_left_out);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    ;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        warnUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * @param str 标题
     * @Title initTitle
     * @Description 设置标题文字
     * @author jerry
     * @date 2015年7月1日 上午9:39:46
     */
    protected void initTitle(String str) {
        try {
            TextView title = (TextView) findViewById(R.id.tv_title);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (title != null) {
                title.setText(str);
            }
            if (toolbar != null) {
                toolbar.setTitle("");
                setSupportActionBar(toolbar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initHeaderDivider(boolean hide) {
        try {
            TextView divider = (TextView) findViewById(R.id.divider);
            if (divider != null) {
                if (hide) {
                    divider.setVisibility(View.VISIBLE);
                } else {
                    divider.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initBackClick(int resLeft,
                                 View.OnClickListener navigationOnClickListener) {
        try {
            headerMenu1 = (TextView) findViewById(R.id.headerMenu1);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                if (resLeft == NO_ICON) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    if (resLeft != NO_RES) {
                        toolbar.setNavigationIcon(resLeft);
                    } else {
                        toolbar.setNavigationIcon(R.drawable.actionbar_goback);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //小于21，导致返回键不响应
                        for (int i = 0; i < toolbar.getChildCount(); i++) {
                            View v = toolbar.getChildAt(i);
                            if (v instanceof ImageView || v instanceof ImageButton) {
                                v.setBackgroundResource(R.drawable.selector_menu_bg);
                            }
                        }
                    }
                }
                if (navigationOnClickListener != null) {
                    if (headerMenu1 != null) {
                        headerMenu1.setOnClickListener(navigationOnClickListener);
                    }
                    toolbar.setNavigationOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (headerMenu1 != null) {
                                headerMenu1.performClick();
                            }

                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initMenuClick(int resLeft,
                                 int textLeftRes, OnClickListener left,
                                 int resRight, int textRightRes, OnClickListener right) {
        initMenuClick(resLeft,
                getString(textLeftRes), left,
                resRight, getString(textRightRes), right);
    }

    protected void initMenuWithSubMenuClick(int resLeft,
                                            int textLeftRes,
                                            List<MenuInfo> leftMenus,
                                            AdapterView.OnItemClickListener left,
                                            int resRight, int textRightRes, List<MenuInfo> rightMenus,
                                            AdapterView.OnItemClickListener right) {
        initMenuWithSubMenuClick(resLeft,
                getString(textLeftRes), leftMenus, left,
                resRight, getString(textRightRes), rightMenus, right);
    }

    protected void initMenuWithSubMenuClick(int resLeft, String textLeft, List<MenuInfo> leftMenus, AdapterView.OnItemClickListener left, int resRight, String textRight, final List<MenuInfo> rightMenus, final AdapterView.OnItemClickListener right) {
        try {
            headerMenu2 = (TextView) findViewById(R.id.headerMenu2);
            headerMenu3 = (TextView) findViewById(R.id.headerMenu3);
            this.leftMenus = leftMenus;
            this.rightMenus = rightMenus;
            this.leftMenusItemClick = left;
            this.rightMenusItemClick = right;
            ActionMenuView actionMenuView = (ActionMenuView) findViewById(R.id.action_menu_view);
            if (actionMenuView == null) {
                LogUtils.d("Toolbar为空");
                return;
            }
            actionMenuView.getMenu().clear();
            if (headerMenu2 != null && resLeft != NO_ICON) {
                if (resLeft == NO_RES) {
                    resLeft = R.drawable.actionbar_menu;
                }
                headerMenu2.setText(textLeft + "");
                MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.headerMenu2, 1, textLeft + "").setIcon(resLeft);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {//小于API11时
                    MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                if (left != null) {
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (rootMenuView == null || popupMenu == null) {
                                rootMenuView = getLayoutInflater()
                                        .inflate(R.layout.popup_actionbar_menu_list, null);
                                // 创建PopupWindow对象
                                popupMenu = new PopupWindow(rootMenuView, ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                                popupMenu.setWidth(DensityUtil.dp2px(getBaseContext(), 80));
                                // popupMenu.setAnimationStyle(R.anim.in);
                                popupMenu.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
                                popupMenu.setOutsideTouchable(true);// 以及下一句 同时写才会有效
                                popupMenu.setFocusable(true);// 获取焦点
                                popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                    @Override
                                    public void onDismiss() {

                                    }
                                });
                            }
                            AutoWrapcontentListView list = (AutoWrapcontentListView) rootMenuView.findViewById(R.id.listView);
                            if (popupMenuListAdapter == null) {
                                popupMenuListAdapter = new MenuItemAdapter<MenuInfo>(getBaseContext(), BaseActivity.this.leftMenus, ModularMenu.CODE_MENU);
                                list.setAdapter(popupMenuListAdapter);
                            } else {
                                popupMenuListAdapter.update(BaseActivity.this.leftMenus);
                            }
                            list.setOnItemClickListener(leftMenusItemClick);
                            popupMenu.showAsDropDown(toolbar.findViewById(R.id.action_menu_view).findViewById(R.id.headerMenu2));
                            return true;
                        }
                    });
                }
            }
            if (headerMenu3 != null && resRight != NO_ICON) {
                if (resRight == NO_RES) {
                    resRight = R.drawable.actionbar_menu;
                }
                headerMenu3.setText(textRight + "");
                MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.headerMenu3, 2, textRight + "").setIcon(resRight);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {//小于API11时
                    MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                if (right != null && headerMenu3 != null) {
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (rootMenuView == null || popupMenu == null) {
                                rootMenuView = getLayoutInflater()
                                        .inflate(R.layout.popup_actionbar_menu_list, null);
                                // 创建PopupWindow对象
                                popupMenu = new PopupWindow(rootMenuView, ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                                popupMenu.setWidth(DensityUtil.dp2px(getBaseContext(), 80));
                                // popupMenu.setAnimationStyle(R.anim.in);
                                popupMenu.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
                                popupMenu.setOutsideTouchable(true);// 以及下一句 同时写才会有效
                                popupMenu.setFocusable(true);// 获取焦点
                                popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                    @Override
                                    public void onDismiss() {

                                    }
                                });
                            }
                            AutoWrapcontentListView list = (AutoWrapcontentListView) rootMenuView.findViewById(R.id.listView);
                            if (popupMenuListAdapter == null) {
                                popupMenuListAdapter = new MenuItemAdapter<MenuInfo>(getBaseContext(), BaseActivity.this.rightMenus, ModularMenu.CODE_MENU);
                                list.setAdapter(popupMenuListAdapter);
                            } else {
                                popupMenuListAdapter.update(BaseActivity.this.rightMenus);
                            }
                            list.setOnItemClickListener(rightMenusItemClick);
                            popupMenu.showAsDropDown(toolbar.findViewById(R.id.action_menu_view).findViewById(R.id.headerMenu3));
                            return true;
                        }
                    });
                }
            }

//            MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.action_pay, 1, "更多").setIcon(resLeft);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            } else {//小于API11时
//                MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
//            }

            for (int i = 0; i < actionMenuView.getChildCount(); i++) {
                ActionMenuItemView v = (ActionMenuItemView) actionMenuView.getChildAt(i);
                v.setBackgroundResource(R.drawable.selector_menu_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭ActionBar菜单
     */
    protected void dismissMenu() {
        if (popupMenu != null && popupMenu.isShowing()) {
            popupMenu.dismiss();
        }
    }

    /**
     * @param resLeft  左侧菜单图标,NO_RES表示不显示
     * @param left     左侧菜单监听器
     * @param resRight 右侧菜单图标,NO_RES表示不显示
     * @param right    右侧菜单监听器
     * @Title initMenuClick
     * @Description 有文字菜单设置，对应布局R.layout.header_btn_text_layout
     * @author jerry
     * @date 2015年7月1日 上午9:53:45
     */

    protected void initMenuClick(int resLeft,
                                 String textLeft, OnClickListener left,
                                 int resRight, String textRight, OnClickListener right) {
        try {
            headerMenu2 = (TextView) findViewById(R.id.headerMenu2);
            headerMenu3 = (TextView) findViewById(R.id.headerMenu3);
            ActionMenuView actionMenuView = (ActionMenuView) findViewById(R.id.action_menu_view);
            if (actionMenuView == null) {
                LogUtils.d("Toolbar为空");
                return;
            }
            actionMenuView.getMenu().clear();
            if (headerMenu2 != null && resLeft != NO_ICON) {
                if (resLeft == NO_RES) {
                    resLeft = R.drawable.actionbar_menu;
                }
                headerMenu2.setText(textLeft + "");
                MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.headerMenu2, 1, textLeft + "").setIcon(resLeft);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {//小于API11时
                    MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                if (left != null && headerMenu2 != null) {
                    headerMenu2.setOnClickListener(left);
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (headerMenu2 != null) {
                                headerMenu2.performClick();
                            }
                            return false;
                        }
                    });
                }
            }
            if (headerMenu3 != null && resRight != NO_ICON) {
                if (resRight == NO_RES) {
                    resRight = R.drawable.actionbar_menu;
                }
                headerMenu3.setText(textRight + "");
                MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.headerMenu3, 2, textRight + "").setIcon(resRight);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {//小于API11时
                    MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                if (right != null && headerMenu3 != null) {
                    headerMenu3.setOnClickListener(right);
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (headerMenu3 != null) {
                                headerMenu3.performClick();
                            }
                            return true;
                        }
                    });
                }
            }

//            MenuItem menuItem = actionMenuView.getMenu().add(100, R.id.action_pay, 1, "更多").setIcon(resLeft);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            } else {//小于API11时
//                MenuCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
//            }

            for (int i = 0; i < actionMenuView.getChildCount(); i++) {
                ActionMenuItemView v = (ActionMenuItemView) actionMenuView.getChildAt(i);
                v.setBackgroundResource(R.drawable.selector_menu_bg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param searchListener
     * @return
     * @Title initSearchInput
     * @Description 设置搜索按钮事件，返回搜索框
     * @author jerry
     * @date 2015年7月6日 下午4:16:53
     */
//    protected AutoCompleteTextView initSearchInput(String hintStr,
//                                                   OnClickListener searchListener) {
//        final AutoCompleteTextView input = (AutoCompleteTextView) findViewById(R.id.key);
//        if (input == null) {
//            return null;
//        }
//        if (TextUtils.isEmpty(hintStr)) {
//            input.setHint("搜索");
//        } else {
//            input.setHint(hintStr);
//        }
//        View btnSearch = findViewById(R.id.headerSearchBtn);
//        if (btnSearch != null) {
//            btnSearch.setOnClickListener(searchListener);
//        }
//
//        input.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//                                      int arg3) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1,
//                                          int arg2, int arg3) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                String editable = input.getText().toString();
//                // 如果包含特殊字符，则将特殊字符替换成""
//                String str = CommonUtils.removeSpecialChar(editable.toString());
//                if (!editable.equals(str)) {
//                    input.setText(str);
//                    input.setSelection(str.length()); // 光标置后
//                }
//            }
//        });
//
//        return input;
//    }

    /**
     * @param str
     * @Title showToast
     * @Description Toast显示提示信息
     * @author jerry
     * @date 2015年7月8日 下午12:45:29
     */
    protected void showToast(String str) {
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param title           标题
     * @param content         提示内容
     * @param okText          按钮文字
     * @param onClickListener 按钮事件
     * @Title showOkAlertDialog
     * @Description 显示单一按钮的OkAlertDialog窗口
     * @author jerry
     * @date 2015年7月8日 下午12:45:26
     */
    protected void showOkAlertDialog(boolean canCancel, String title,
                                     String content, String okText, OnClickListener onClickListener) {
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_ok_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        TextView tvContent = (TextView) contentView
                .findViewById(R.id.dialogContent);
        View submit = contentView.findViewById(R.id.sure_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        submit.setOnClickListener(onClickListener);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content) && tvContent != null) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        if (okAlertDialog == null) {
            okAlertDialog = new Dialog(this, R.style.Dialog);
            Window win = okAlertDialog.getWindow();
            win.setContentView(contentView);
            okAlertDialog.setCancelable(canCancel);
            okAlertDialog.show();
        } else {
            Window win = okAlertDialog.getWindow();
            win.setContentView(contentView);
            okAlertDialog.setCancelable(canCancel);
            if (!okAlertDialog.isShowing()) {
                okAlertDialog.show();
            }
        }
    }

    /**
     * @param title           标题
     * @param content         提示内容
     * @param okText          按钮文字
     * @param onClickListener 按钮事件
     * @param onKeyListener   按键事件
     * @Title showOkAlertDialog
     * @Description 显示单一按钮的OkAlertDialog窗口
     * @author jerry
     * @date 2015年7月8日 下午12:45:26
     */
    protected void showOkAlertDialog(boolean canCancel, String title,
                                     String content, String okText, OnClickListener onClickListener,
                                     OnKeyListener onKeyListener) {
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_ok_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        TextView tvContent = (TextView) contentView
                .findViewById(R.id.dialogContent);
        View submit = contentView.findViewById(R.id.sure_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        submit.setOnClickListener(onClickListener);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content) && tvContent != null) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }

        if (okAlertDialog == null) {
            okAlertDialog = new Dialog(this, R.style.Dialog);
            Window win = okAlertDialog.getWindow();
            win.setContentView(contentView);
            okAlertDialog.setCancelable(canCancel);
            okAlertDialog.show();
        } else {
            Window win = okAlertDialog.getWindow();
            win.setContentView(contentView);
            okAlertDialog.setCancelable(canCancel);
            if (!okAlertDialog.isShowing()) {
                okAlertDialog.show();
            }
        }

    }

    /**
     * @Title dimissOkAlertDialog
     * @Description 关闭OkAlertDialog窗口
     * @author jerry
     * @date 2015年7月8日 下午12:45:10
     */
    protected void dimissOkAlertDialog() {
        if (okAlertDialog != null && okAlertDialog.isShowing()) {
            okAlertDialog.dismiss();
        }
    }

    /**
     * @param title          标题
     * @param content        提示内容
     * @param okText         确定按钮文字
     * @param cancelText     取消按钮文字
     * @param okListener     确定按钮事件
     * @param cancelListener 取消按钮事件
     * @Title showOkCancelAlertDialog
     * @Description 显示双按钮的OkCancelAlertDialog窗口
     * @author jerry
     * @date 2015年7月8日 下午12:45:26
     */
    protected void showOkCancelAlertDialog(boolean canCancel, String title,
                                           String content, String okText, String cancelText,
                                           OnClickListener okListener, OnClickListener cancelListener) {
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_ok_cancel_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        TextView tvContent = (TextView) contentView
                .findViewById(R.id.dialogContent);
        tvTitle.setText(title);
        tvContent.setText(content);
        View submit = contentView.findViewById(R.id.sure_btn);
        View cancel = contentView.findViewById(R.id.cancel_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        TextView tvConcel = (TextView) contentView
                .findViewById(R.id.cancel_btn_tv);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content) && tvContent != null) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        if (!TextUtils.isEmpty(cancelText) && tvConcel != null) {
            tvConcel.setText(cancelText);
        }

        submit.setOnClickListener(okListener);
        cancel.setOnClickListener(cancelListener);
        if (okCancelAlertDialog == null) {
            okCancelAlertDialog = new Dialog(this, R.style.Dialog);
            Window win = okCancelAlertDialog.getWindow();
            win.setContentView(contentView);
            okCancelAlertDialog.setCancelable(canCancel);
            okCancelAlertDialog.show();
        } else {
            Window win = okCancelAlertDialog.getWindow();
            win.setContentView(contentView);
            okCancelAlertDialog.setCancelable(canCancel);
            if (!okCancelAlertDialog.isShowing()) {
                okCancelAlertDialog.show();
            }
        }
    }

    /**
     * @param title          标题
     * @param content        提示内容
     * @param okText         确定按钮文字
     * @param cancelText     取消按钮文字
     * @param okListener     确定按钮事件
     * @param cancelListener 取消按钮事件
     * @param onKeyListener  按键事件
     * @Title showOkCancelAlertDialog
     * @Description 显示双按钮的OkCancelAlertDialog窗口
     * @author jerry
     * @date 2015年7月8日 下午12:45:26
     */
    protected void showOkCancelAlertDialog(boolean canCancel, String title,
                                           String content, String okText, String cancelText,
                                           OnClickListener okListener, OnClickListener cancelListener,
                                           OnKeyListener onKeyListener) {
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_ok_cancel_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        TextView tvContent = (TextView) contentView
                .findViewById(R.id.dialogContent);
        tvTitle.setText(title);
        tvContent.setText(content);
        View submit = contentView.findViewById(R.id.sure_btn);
        View cancel = contentView.findViewById(R.id.cancel_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        TextView tvConcel = (TextView) contentView
                .findViewById(R.id.cancel_btn_tv);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content) && tvContent != null) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        if (!TextUtils.isEmpty(cancelText) && tvConcel != null) {
            tvConcel.setText(cancelText);
        }

        submit.setOnClickListener(okListener);
        cancel.setOnClickListener(cancelListener);
        if (okCancelAlertDialog == null) {
            okCancelAlertDialog = new Dialog(this, R.style.Dialog);
            Window win = okCancelAlertDialog.getWindow();
            win.setContentView(contentView);
            okCancelAlertDialog.setCancelable(canCancel);
            okCancelAlertDialog.show();
        } else {
            Window win = okCancelAlertDialog.getWindow();
            win.setContentView(contentView);
            okCancelAlertDialog.setCancelable(canCancel);
            if (!okCancelAlertDialog.isShowing()) {
                okCancelAlertDialog.show();
            }
        }

    }

    protected void dimissOkCancelAlertDialog() {
        if (okCancelAlertDialog != null && okCancelAlertDialog.isShowing()) {
            okCancelAlertDialog.dismiss();
        }
    }

    protected UpdateManager updateManager;
    protected Dialog updateInfoDialog;
    protected NotificationManager mNotifManager;
    private Dialog mDialog;

    protected void checkUpdate() {
        if (updateManager == null) {
            updateManager = new UpdateManager(getBaseContext(),
                    new UpdateCallback() {
                        private Notification notification;

                        @Override
                        public void onCheckUpdateStart() {
                            try {
                                if (mDialog != null) {
                                    mDialog.dismiss();
                                }
                                mDialog = MyProgressDialog.createLoadingDialog(
                                        BaseActivity.this, "正在检测版本...");
                                mDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCheckUpdateCancelled() {
                            try {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCheckUpdateCompleted(UpdateResult result) {
                            onCheckUpdateCancelled();
                            if (result == null) {
                                return;
                            }
                            UpdateInfo updateInfo = result.getUpdateInfo();
                            if (updateInfo == null) {
                                return;
                            }
                            if (UpdateInfo.NO_UPDATE.equals(updateInfo
                                    .getUpdateType())) {// 无更新
                                showNoUpdateMsg();
                                sharedFileUtils
                                        .putInt(SharedFileUtils.LAST_CHECK_NEWEST_VERSION_CODE,
                                                0);
                                warnUser();
                                return;
                            }

                            showUpdateInfoDialog(false, "版本更新", getUpdateTipContent(updateInfo), "更新",
                                    "取消", new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (updateInfoDialog != null) {
                                                updateInfoDialog.dismiss();
                                            }
                                            updateManager.download();
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (updateInfoDialog != null) {
                                                updateInfoDialog.dismiss();
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onError(int erroCode, Object error,
                                            String msg) {
                            onCheckUpdateCancelled();
                            switch (erroCode) {
                                case ERRORCODE_NO_PRODUCT_ID:
                                    showToast("产品ID不能为空!");
                                    break;
                                case ERRORCODE_NO_UPDATE_TYPE:
                                    showToast("更新类型不能为空!");
                                    break;
                                case ERRORCODE_NO_UPDATE_URL:
                                    showToast("更新链接不能为空!");
                                    break;
                                case ERRORCODE_JSON_PRASE_EXCEPTION:
                                    showToast("更新服务器返回结果解释失败");
                                    break;
                                case ERRORCODE_SERVER_PRODUCT_ID_NOT_EXIST:
                                    showToast("产品不存在");
                                    break;
                                case ERRORCODE_SERVER_DB_CONNECT_FAIL:
                                    showToast("数据库服务器连接失败");
                                    break;
                                case ERRORCODE_HTTP_EXCEPTION:
                                    HttpException e = (HttpException) error;
                                    if (e.getExceptionCode() == 404) {
                                        showToast("接口地址不存在");
                                    } else if (e.getExceptionCode() == 500) {
                                        showToast("服务处理异常");
                                    } else {
                                        showToast("网络不给力,请重新尝试");
                                        LogUtils.e(e.getExceptionCode() + ":" + msg);
                                    }
                                    break;
                                case ERRORCODE_MERGE_ERROR:
                                    showToast("增量文件下载成功,合并失败,无法进行更新");
                                    if (notification == null) {
                                        notification = new Notification();
                                    }
                                    notification.icon = android.R.drawable.stat_sys_download_done;
                                    notification.tickerText = getString(R.string.notif_down_file);
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                                    RemoteViews mContentView = new RemoteViews(App
                                            .getPackageName(getBaseContext()),
                                            R.layout.download_notifcation);
                                    mContentView.setTextViewText(
                                            R.id.progressPercent, "更新失败");
                                    mContentView.setTextViewText(
                                            R.id.progressPercent, "增量文件下载成功,合并失败");
                                    mContentView.setImageViewResource(
                                            R.id.downLoadIcon, R.mipmap.ic_launcher);
                                    mContentView.setProgressBar(
                                            R.id.downLoadProgressss, 10000, 10000,
                                            false);
                                    notification.contentView = mContentView;
                                    mNotifManager.notify(R.id.downLoadIcon,
                                            notification);
                                    break;
                                case ERRORCODE_DOWNLOAD_ERROR:
                                    showToast("文件下载失败,无法进行更新");
                                    if (notification == null) {
                                        notification = new Notification();
                                    }
                                    notification.icon = android.R.drawable.stat_sys_download_done;
                                    notification.tickerText = getString(R.string.notif_down_file);
                                    notification.flags = Notification.FLAG_AUTO_CANCEL;
                                    mContentView = new RemoteViews(App
                                            .getPackageName(getBaseContext()),
                                            R.layout.download_notifcation);
                                    mContentView.setTextViewText(
                                            R.id.progressPercent, "更新失败");
                                    mContentView.setTextViewText(
                                            R.id.progressPercent2, "文件下载失败");
                                    mContentView.setImageViewResource(
                                            R.id.downLoadIcon, R.mipmap.ic_launcher);
                                    mContentView.setProgressBar(
                                            R.id.downLoadProgressss, 10000, 10000,
                                            false);
                                    notification.contentView = mContentView;
                                    mNotifManager.notify(R.id.downLoadIcon,
                                            notification);
                                    break;
                                case ERRORCODE_IO_EXCEPTION:
                                case ERRORCODE_NAME_NOT_FOUND_EXCEPTION:
                                    showToast(msg);
                                    break;
                                default:
                                    showToast("" + msg);
                                    break;
                            }
                        }

                        @Override
                        public void onDownloadStart() {
                            showToast("正在启动下载，请稍后...");
                            if (notification == null) {
                                notification = new Notification();
                            }
                        }

                        @Override
                        public void onDownloadCancelled() {
                            mNotifManager.cancel(R.id.downLoadIcon);
                        }

                        @Override
                        public void onDownloadProgressChanged(long total,
                                                              long current, boolean isUploading) {
                            double progress = current * 100.0f / total;
                            if (notification == null) {
                                notification = new Notification();
                            }
                            if (current == total) {
                                notification.icon = android.R.drawable.stat_sys_download_done;
                                notification.tickerText = getString(R.string.notif_down_file);
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                RemoteViews mContentView = new RemoteViews(App
                                        .getPackageName(getBaseContext()),
                                        R.layout.download_notifcation);
                                mContentView.setTextViewText(
                                        R.id.progressPercent, "下载完成");
                                mContentView.setTextViewText(
                                        R.id.progressPercent2, "点击进行安装");
                                mContentView.setImageViewResource(
                                        R.id.downLoadIcon, R.mipmap.ic_launcher);
                                mContentView.setProgressBar(
                                        R.id.downLoadProgressss, 10000, 10000,
                                        false);
                                notification.contentView = mContentView;
                                LogUtils.d("进度条已满");
                            } else {
                                notification.icon = android.R.drawable.stat_sys_download;
                                notification.tickerText = getString(R.string.notif_down_file);
                                notification.flags = Notification.FLAG_ONGOING_EVENT;
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                RemoteViews mContentView = new RemoteViews(App
                                        .getPackageName(getBaseContext()),
                                        R.layout.download_notifcation);
                                mContentView.setTextViewText(
                                        R.id.progressPercent,
                                        String.format("%.2f", progress) + "%");
                                mContentView.setTextViewText(
                                        R.id.progressPercent2,
                                        NumberUtil.byteFormat(current) + "/"
                                                + NumberUtil.byteFormat(total));
                                mContentView.setImageViewResource(
                                        R.id.downLoadIcon, R.mipmap.ic_launcher);
                                mContentView.setProgressBar(
                                        R.id.downLoadProgressss, 10000,
                                        (int) progress * 100, false);
                                notification.contentView = mContentView;
                            }
                            mNotifManager.notify(R.id.downLoadIcon,
                                    notification);
                        }

                        @Override
                        public void onDownloadCompleted(UpdateInfo updateInfo,
                                                        File file) {
                            showToast("下载完成");
                            LogUtils.d("下载完成");
                            if (notification == null) {
                                notification = new Notification();
                            }
                            notification.icon = android.R.drawable.stat_sys_download_done;
                            notification.tickerText = getString(R.string.notif_down_file);
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            RemoteViews mContentView = new RemoteViews(App
                                    .getPackageName(getBaseContext()),
                                    R.layout.download_notifcation);
                            mContentView.setTextViewText(R.id.progressPercent,
                                    "下载完成");
                            mContentView.setImageViewResource(
                                    R.id.downLoadIcon, R.mipmap.ic_launcher);
                            mContentView.setProgressBar(
                                    R.id.downLoadProgressss, 10000, 10000,
                                    false);
                            notification.contentView = mContentView;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(file),
                                    "application/vnd.android.package-archive");
                            PendingIntent contentIntent = PendingIntent
                                    .getActivity(BaseActivity.this, 0, intent,
                                            0);
                            notification.contentIntent = contentIntent;
                            mNotifManager.notify(R.id.downLoadIcon,
                                    notification);
                        }
                    });
        }
        updateManager.checkUpdate();
    }

    /**
     * 合并处理更新提示内容
     *
     * @param updateInfo
     * @return
     */
    private SpannableString getUpdateTipContent(UpdateInfo updateInfo) {
        SpannableString content = null;
        if (UpdateInfo.FULL_UPDATE.equals(updateInfo
                .getUpdateType())) {// 完整更新
            findNewVersion(BaseActivity.this, updateInfo);
            String str = "";
            if (!updateManager
                    .isWifiConnected(BaseActivity.this)) {
                str = "当前不是WIFI网络，更新下载会使用你的移动网络流量，请注意!\n";
            } else {
                str = "当前是WIFI网络，请放心更新!\n";
            }
            String str1 = "发现新版本"
                    + updateInfo.getVersionName()
                    + "！\n完整更新: ";
            String str2 = NumberUtil.byteFormat(updateInfo
                    .getSize()) + "\n";
            String str3 = "更新内容:\n";
            str3 = dealUpdateContentList(updateInfo, str3);
            content = new SpannableString(str + str1 + str2
                    + str3);
            content.setSpan(
                    new ForegroundColorSpan(
                            getResources()
                                    .getColor(
                                            R.color.btn_color_normal)),
                    0, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new RelativeSizeSpan(0.8f), 0,
                    str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    str.length() + str1.length(),
                    str.length() + str1.length()
                            + str2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (UpdateInfo.INC_UPDATE.equals(updateInfo
                .getUpdateType())) {// 增量更新
            findNewVersion(BaseActivity.this, updateInfo);
            String str = "";
            if (!updateManager
                    .isWifiConnected(BaseActivity.this)) {
                str = "当前不是WIFI网络，更新下载会使用你的移动网络流量，请注意!\n";
            } else {
                str = "当前是WIFI网络，请放心更新!\n";
            }
            String str1 = "发现新版本"
                    + updateInfo.getVersionName()
                    + "！\n省流量更新: ";
            String str2 = NumberUtil.byteFormat(updateInfo
                    .getSizeOriginal());
            String str4 = " "
                    + NumberUtil.byteFormat(updateInfo
                    .getSize()) + "\n";
            String str3 = "更新内容:\n";
            str3 = dealUpdateContentList(updateInfo, str3);
            content = new SpannableString(str + str1 + str2
                    + str4 + str3);
            content.setSpan(
                    new ForegroundColorSpan(
                            getResources()
                                    .getColor(
                                            R.color.btn_color_normal)),
                    0, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(new RelativeSizeSpan(0.8f), 0,
                    str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    str.length() + str1.length(),
                    str.length() + str1.length()
                            + str2.length() + str4.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(
                    new StrikethroughSpan(),
                    str.length() + str1.length(),
                    str.length() + str1.length()
                            + str2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return content;
    }

    /**
     * 显示暂无更新提示。
     */
    protected void showNoUpdateMsg() {
        showToast("暂无更新");
    }


    protected void showUpdateInfoDialog(boolean canCancel, String title,
                                        SpannableString content, String okText, String cancelText,
                                        OnClickListener okListener, OnClickListener cancelListener) {
        View contentView = getLayoutInflater().inflate(
                R.layout.alertdialog_update_layout, null);
        TextView tvTitle = (TextView) contentView
                .findViewById(R.id.dialogTitle);
        TextView tvContent = (TextView) contentView
                .findViewById(R.id.dialogContent);
        tvTitle.setText(title);
        tvContent.setText(content);
        View submit = contentView.findViewById(R.id.sure_btn);
        View cancel = contentView.findViewById(R.id.cancel_btn);
        TextView tvSubmit = (TextView) contentView
                .findViewById(R.id.sure_btn_tv);
        TextView tvConcel = (TextView) contentView
                .findViewById(R.id.cancel_btn_tv);
        if (!TextUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content) && tvContent != null) {
            tvContent.setText(content);
        }
        if (!TextUtils.isEmpty(okText) && tvSubmit != null) {
            tvSubmit.setText(okText);
        }
        if (!TextUtils.isEmpty(cancelText) && tvConcel != null) {
            tvConcel.setText(cancelText);
        }

        submit.setOnClickListener(okListener);
        cancel.setOnClickListener(cancelListener);
        if (updateInfoDialog == null) {
            updateInfoDialog = new Dialog(BaseActivity.this, R.style.Dialog);
            Window win = updateInfoDialog.getWindow();
            win.setContentView(contentView);
            updateInfoDialog.setCancelable(canCancel);
            updateInfoDialog.show();
        } else {
            Window win = updateInfoDialog.getWindow();
            win.setContentView(contentView);
            updateInfoDialog.setCancelable(canCancel);
            if (!updateInfoDialog.isShowing()) {
                updateInfoDialog.show();
            }
        }
    }

    private void findNewVersion(Context context, UpdateInfo updateInfo) {
        sharedFileUtils.putInt(SharedFileUtils.LAST_CHECK_NEWEST_VERSION_CODE,
                updateInfo.getVersionCode());
        warnUser();
    }

    /**
     * @Title warnUser
     * @Description 检查版本更新界面
     * @author jerry
     * @date 2015年8月27日 下午1:51:09
     */
    protected void warnUser() {

    }

    protected String dealUpdateContentList(UpdateInfo updateInfo, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        List<UpdateContent> list = updateInfo.getUpdateList();
        UpdateContent updateContent = null;
        if (list == null || list.size() == 0) {
            return "";
        }
        Pattern pattern = Pattern.compile("^[\\d]+");
        Map<String, List<UpdateContent>> map = new HashMap<String, List<UpdateContent>>();
        for (int i = 0; i < list.size(); i++) {
            updateContent = list.get(i);
            String versionName = updateContent.getVersionName();
            if (map.containsKey(updateContent.getVersionName())) {
                map.get(versionName).add(updateContent);
            } else {
                List<UpdateContent> list2 = new ArrayList<UpdateContent>();
                list2.add(updateContent);
                map.put(versionName, list2);
            }
        }
        String lastVersionName = "";
        for (int i = 0; i < list.size(); i++) {
            updateContent = list.get(i);
            if (lastVersionName.equals(updateContent.getVersionName())) {
                continue;
            } else {
                StringBuilder sb2 = new StringBuilder();
                List<UpdateContent> list2 = map.get(updateContent
                        .getVersionName());
                sb2.append("版本V" + updateContent.getVersionName() + ":\n");
                UpdateContent updateContentTMP = null;
                for (int j = 0; j < list2.size(); j++) {
                    updateContentTMP = list2.get(j);
                    sb2.append(updateContentTMP.getUpdateContent());
                    if (!sb2.toString().endsWith("\n")) {
                        sb2.append("\n");
                    }
                }
                int index = 1;
                Scanner sr = new Scanner(sb2.toString());
                String line = null;
                while (sr.hasNext()) {
                    line = sr.nextLine();
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        line = line.replace(matcher.group(), index + "");
                        index++;
                    }
                    sb.append(line + "\n");
                }
            }
            lastVersionName = updateContent.getVersionName();
        }
        return sb.toString();
    }


    protected TextView getToolbarTitle(Toolbar mToolBar) {
        TextView titleTextView = null;
        try {
            Field f = mToolBar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(mToolBar);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return titleTextView;
    }


    private void setOverflowShowingAlway() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }


    /**
     * 显示OverflowMenu的Icon
     *
     * @param featureId
     * @param menu
     */
    private void setOverflowIconVisible(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showTip(String str, String action, OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(str)) {
            Snackbar.make(getWindow().getDecorView(), str, Snackbar.LENGTH_LONG)
                    .setAction("Action", onClickListener).show();
        }
    }

    public <T> HttpHandler<T> send(String uri, RequestParams params,
                                   RequestCallBack<T> callBack) {
        String url = Constant.SERVER_URL + uri;
        LogUtils.v("requestUrl:" + url);
        HttpUtils http = MyApplication.getNoCacheHttpUtils();
        if (params != null) {
            if (LogUtils.allowD) {
                List<NameValuePair> list = (List<org.apache.http.NameValuePair>) params.getQueryStringParams();
                int size = list.size();
                StringBuilder sb = new StringBuilder();
                org.apache.http.NameValuePair nameValuePair;
                for (int i = 0; i < size; i++) {
                    nameValuePair = list.get(i);
                    sb.append(nameValuePair.getName() + "=" + nameValuePair.getValue() + "&");
                }
                if (sb.toString().endsWith("&")) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                LogUtils.d(sb.toString());
            }
            return http.send(HttpRequest.HttpMethod.POST, url, params, callBack);
        } else {
            return http.send(HttpRequest.HttpMethod.POST, url, callBack);
        }
    }

    //将隐式启动转换为显示启动
    public static Intent getExplicitIntent(Context context, Intent implicitIntent, String name) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = null;
        if (resolveInfo.size() == 1) {
            serviceInfo = resolveInfo.get(0);
        } else {
            for (int i = 0; i < resolveInfo.size(); i++) {
                ResolveInfo info = resolveInfo.get(i);
                if (name.equals(info.serviceInfo.name)) {
                    serviceInfo = info;
                    break;
                }
            }
        }
        if (serviceInfo == null) {
            return null;
        }
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    protected boolean initDirs() {
        mSDCardPath = PathManager.getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, Constant.APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    protected void navToCarpark(double latitude, double longitude, CarPark carPark) {
        boolean isInitSuccess = BaiduNaviManager.isNaviInited();
        boolean isInitSoSuccess = BaiduNaviManager.isNaviSoLoadSuccess();

        if (!isInitSuccess || !isInitSoSuccess) {
            //版本判断
            if (Build.VERSION.SDK_INT >= 23) {
                showToast("当前系统版本暂不支持使用导航功能");
                return;
            } else {
                showToast("百度地图初始化引擎失败");
            }
            return;
        }
        if (carPark == null) {
            showToast("缺少车场信息，无法开启导航");
            return;
        }
        launchNavigator(
                latitude,
                longitude,
                "我的位置",
                Double.valueOf(carPark.getLatitude()),
                Double.valueOf(carPark.getLongitude()),
                carPark.getAddress());
    }

    protected void initNavi() {

        BaiduNaviManager.getInstance().init(this, mSDCardPath, Constant.APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String authinfo;
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        LogUtils.d(authinfo);
                    }

                    public void initSuccess() {
                        LogUtils.d("百度导航引擎初始化成功");
                    }

                    public void initStart() {
                        LogUtils.d("百度导航引擎初始化开始");
                    }

                    public void initFailed() {
                        LogUtils.d("百度导航引擎初始化失败");
                    }
                }, null);
    }

    /**
     * 启动GPS导航. 前置条件：导航引擎初始化成功
     */
    protected void launchNavigator(double fromlatitude, double fromlongitude,
                                   String fromAddress, double tolatitude,
                                   double tolongitude, String topoiaddress) {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            showToast("当前系统版本暂不支持使用导航功能");
            return;
        }
        BNRoutePlanNode sNode = new BNRoutePlanNode(fromlongitude, fromlatitude,
                fromAddress, null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(tolongitude,
                tolatitude, topoiaddress, null,
                BNRoutePlanNode.CoordinateType.BD09LL);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BNRouteGuideManager.getInstance().setVoiceModeInNavi(BNRouteGuideManager.VoiceMode.Novice);
            this.mBNRoutePlanNode = sNode;
            showToast("导航启动中，请稍后");
            BaiduNaviManager.getInstance().launchNavigator(this, list,
                    BaiduNaviManager.RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND,
                    true, this);
        }
    }


    @Override
    public void onJumpToNavigator() {
//        Intent intent = new Intent(this, BaiduNavGuideActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(BaiduNavGuideActivity.KEY_ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {
        showToast("导航规划失败，请重试");
    }

    /**
     * 扫卡支付跳转
     *
     * @param bundle
     */
    protected void gotoCouponGiving(Bundle bundle) {
        Intent intent = new Intent(getBaseContext(), CouponGivingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQ_CODE_SCAN:
                    gotoCouponGiving(data.getExtras());
                    break;
            }
        }
    }

    /**
     * 显示无网络布局
     */
    @Override
    public void showErrorPage(View v, int strId, int imgId) {
        ViewGroup parent = (ViewGroup) v.getParent();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        errorView = getLayoutInflater().inflate(R.layout.error_layout, null);
        if (parent instanceof FrameLayout) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            parent.addView(errorView, params);
        } else {
            FrameLayout frameLayout = new FrameLayout(getBaseContext());
            parent.removeView(v);
            FrameLayout.LayoutParams paramsMargin = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            paramsMargin.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            frameLayout.addView(v, paramsMargin);
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
            parent.addView(frameLayout, layoutParams);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.addView(errorView, params);
        }

        TextView tvError = (TextView) errorView.findViewById(R.id.tvError);
        ImageView imgError = (ImageView) errorView.findViewById(R.id.imgError);
        if (tvError != null) {
            tvError.setText(getResources().getString(strId));
        }
        if (imgError != null) {
            imgError.setImageResource(imgId);
            imgError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshErrorPage();
                }
            });
        }
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void refreshErrorPage() {
        if (errorView != null) {
            ((ViewGroup) errorView.getParent()).removeView(errorView);
        }
    }

    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    /**
     * 请求权限
     *
     * @param id                   请求授权的id 唯一标识即可
     * @param permission           请求的权限
     * @param allowableRunnable    同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    protected void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        requestPermission(id, new String[]{permission}, allowableRunnable, disallowableRunnable);
    }

    protected void requestPermission(int id, String[] permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null || permission == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }
        this.id = id;
        allowablePermissionRunnables.put(id, allowableRunnable);
        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (pmList == null) {
                pmList = new ArrayList<String>();
            } else {
                pmList.clear();
            }
            //减少是否拥有权限
            for (int i = 0; i < permission.length; i++) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission[i]);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    pmList.add(permission[i]);
                }
            }
            if (!pmList.isEmpty()) {
                //弹出对话框接收权限
                showOkCancelAlertDialog(true, "提示", "请允许定位相关权限运行，否则无法使用部分功能", "允许", "拒绝", new OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        requestPermissions(pmList.toArray(new String[pmList.size()]), BaseActivity.this.id);
                        dimissOkCancelAlertDialog();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                    }
                });
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean flag = true;
        for (int i = 0; i < grantResults.length; i++) {
            flag &= (grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
        if (flag) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        } else {
            Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
            disallowRun.run();
        }
    }


    protected int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    protected void clearBeforeExit(Context context) {
        clearCacheFolder(context.getCacheDir(), System.currentTimeMillis());
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
    }


    /**
     * 设置当前Activity透明度
     *
     * @param value
     * @Title backgroundgAlpha
     * @Description
     * @author 李庆育
     * @date 2015-8-3 上午9:10:42
     */
    protected void backgroundgAlpha(float value) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = value;
        getWindow().setAttributes(params);
    }

    /**
     * 分享到微信
     */
    protected void shareToWechat(ShareInfo shareInfo) {
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle(shareInfo.getTitle());
        wechat.setText(shareInfo.getContent());
        wechat.setImageUrl(shareInfo.getImageUrl());
        wechat.setUrl(shareInfo.getUrl());
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(getBaseContext(), Wechat.NAME);
        weixin.setPlatformActionListener(this);
        weixin.share(wechat);
    }

    /**
     * 分享到微信朋友圈
     */
    protected void shareToWechatMoment(ShareInfo shareInfo) {
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle(shareInfo.getContent());
        wechat.setImageUrl(shareInfo.getImageUrl());
        wechat.setUrl(shareInfo.getUrl());
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(getBaseContext(),
                WechatMoments.NAME);
        weixin.setPlatformActionListener(this);
        weixin.share(wechat);
    }

    /**
     * 短信分享
     */
    protected void shareToSMS(ShareInfo shareInfo) {
        ShortMessage.ShareParams shareParams = new ShortMessage.ShareParams();
        shareParams.setTitle(shareInfo.getTitle());
        shareParams.setText(shareInfo.getContent() + " " + shareInfo.getUrl());
        Platform sms = ShareSDK.getPlatform(ShortMessage.NAME);
        sms.share(shareParams);
    }

    /**
     * shareSDK分享的三个回调
     *
     * @param platform
     * @param i
     * @param hashMap
     */

    @Override
    public void onComplete(Platform platform, int i,
                           HashMap<String, Object> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(getString(R.string.tip_share_success));
            }
        });
    }

    @Override
    public void onError(Platform platform, int i, final Throwable throwable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (throwable != null && throwable instanceof WechatClientNotExistException) {
                    showToast(getString(R.string.error_no_wechat_found));
                } else {
                    showToast(getString(R.string.tip_share_error));
                }
            }
        });
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(getString(R.string.tip_share_cancel));
            }
        });
    }

    protected void fail(HttpException error, String msg) {
        if (error.getExceptionCode() == 0) {
            showToast(getString(R.string.error_network_short));
        } else {
            showToast(msg);
        }
    }

    protected void initBroadcast() {
        Intent intent = new Intent();
        broadcastReceiver = new MyBroadcast();
        IntentFilter filter = new IntentFilter(Constant.BROADCAST.ACCOUNT_INFO);
        //注册广播接收器
        registerReceiver(broadcastReceiver, filter);
    }

    private class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.BROADCAST.ACCOUNT_INFO.equals(intent.getAction())) {
                updateAccountInfo((AccountInfo) intent.getSerializableExtra(Constant.KEY_ACCOUNT_INFO));
            }
        }
    }

    /**
     * 相关用户信息界面更新
     *
     * @param info
     */
    protected void updateAccountInfo(AccountInfo info) {

    }

    /**
     * @return 本地缓存的用户信息
     */
    protected AccountInfo getAccountInfo() {
        return (AccountInfo) ObjectUtil.getObject(sharedFileUtils.getString(SharedFileUtils.ACCOUNT_INFO));
    }

    /**
     * 存储新获取的用户信息
     *
     * @param info
     */
    protected void saveAndNoticeAccountInfoChange(AccountInfo info) {
        sharedFileUtils.putString(SharedFileUtils.ACCOUNT_INFO, ObjectUtil.getBASE64String(info));
        Intent intent = new Intent(Constant.BROADCAST.ACCOUNT_INFO);
        intent.putExtra(Constant.KEY_ACCOUNT_INFO, info);
        sendBroadcast(intent);
    }

    /**
     * 模块菜单分组(插入大分隔条)
     *
     * @param menuList
     * @return
     */
    protected List<MenuInfo> dealMenuGroup(List<MenuInfo> menuList) {
        List<MenuInfo> list = new ArrayList<>();
        int groupId = 0;
        for (int i = 0; i < menuList.size(); i++) {
            if (MenuInfo.TYPE_NORMAL.equals(menuList.get(i).getType())) {
                if (groupId != menuList.get(i).getGroupId()) {
                    MenuInfo menuInfo = new MenuInfo();
                    menuInfo.setType(MenuInfo.TYPE_DIVIDE);
                    list.add(menuInfo);
                    groupId = menuList.get(i).getGroupId();
                }
                list.add(menuList.get(i));
            } else if (MenuInfo.TYPE_SEPARATOR.equals(menuList.get(i).getType())) {
                list.add(menuList.get(i));
            }
        }
        if (list.size() > 0) {
            MenuInfo menuInfo = new MenuInfo();
            menuInfo.setType(MenuInfo.TYPE_DIVIDE);
            list.add(list.size(), menuInfo);
        }
        return list;
    }

    public static boolean isCarNumberValidated(String carNumber) {
        String rule = "[\u4e00-\u9fa5]?([A-Za-z]{1}[A-Za-z0-9]{5}|[A-Za-z]{1}[A-Za-z_0-9]{4}[\u4e00-\u9fa5])";
        return Pattern.matches(rule, carNumber);
    }

}
