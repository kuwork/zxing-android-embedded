package com.ajb.merchants.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.MerchantsDetailInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyListView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.ajb.merchants.view.RoundedImageView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.util.ObjectUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MerchantDetailActivity extends BaseActivity {

    @ViewInject(R.id.swipeLayout)
    MySwipeRefreshLayout swipeLayout;
    @ViewInject(R.id.detailListView)
    private MyListView detailListView;
    @ViewInject(R.id.imgAvatar)
    private RoundedImageView imgAvatar;
    @ViewInject(R.id.tvKey)
    private TextView tvKey;
    @ViewInject(R.id.tvValue)
    private TextView tvValue;
    @ViewInject(R.id.title1)
    private TextView title1;
    @ViewInject(R.id.desc1)
    private TextView desc1;
    @ViewInject(R.id.title2)
    private TextView title2;
    @ViewInject(R.id.desc2)
    private TextView desc2;
    @ViewInject(R.id.title3)
    private TextView title3;
    @ViewInject(R.id.desc3)
    private TextView desc3;
    private View picPickView;
    private PopupWindow picPickPopwindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);
        ViewUtils.inject(this);
        initTitle("");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initHeaderDivider(false);
        swipeLayout.setCanRefresh(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMerchantsDetail();
            }
        });
        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(getBaseContext(), R.color.holo_blue_bright),
                ContextCompat.getColor(getBaseContext(), R.color.holo_green_light),
                ContextCompat.getColor(getBaseContext(), R.color.holo_orange_light),
                ContextCompat.getColor(getBaseContext(), R.color.holo_red_light));
        AccountSettingInfo accountSettingInfo = getAccountSettingInfo();
        if (accountSettingInfo != null && accountSettingInfo.getAccountInfo() != null) {
            if (imgAvatar != null) {
                BitmapUtils bitmapUtils = new BitmapUtils(getBaseContext());
                bitmapUtils.display(imgAvatar, accountSettingInfo.getAccountInfo().getHeadimgUrl(), new BitmapLoadCallBack<RoundedImageView>() {
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
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMerchantsDetail();
            }
        }, 1000);
    }

    private void initMenuInfo(ModularMenu modularMenu) {
        if (modularMenu == null) {
            return;
        }
        if (ModularMenu.CODE_MERCHANTS_DETAILS.equals(modularMenu.getModularCode())) {
            MenuItemAdapter<MenuInfo> adapter = new MenuItemAdapter<>(getBaseContext(), dealMenuGroup(modularMenu.getMenuList()), modularMenu.getModularCode());
            detailListView.setAdapter(adapter);
        }
        detailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            swipeLayout.setRefreshing(false);
                            Intent intent = null;
                            if (MenuInfo.TO_CONTACT.equals(menuInfo.getMenuCode())
                                    || MenuInfo.TO_STORE_DETAIL.equals(menuInfo.getMenuCode()) ||
                                    MenuInfo.TO_STORE_ADDRESS.equals(menuInfo.getMenuCode()) ||
                                    MenuInfo.TO_STORE_SCOPE.equals(menuInfo.getMenuCode())
                                    ) {
                                intent = new Intent(getBaseContext(), EditorActivity.class);
                                menuInfo.dealExtras(intent);
                                startActivityForResult(intent, Constant.REQ_CODE_MODIFY);
                            } else if (MenuInfo.TO_PHONE.equals(menuInfo.getMenuCode())) {
                                intent = new Intent(getBaseContext(), ModifyPhoneActivity.class);
                                menuInfo.dealExtras(intent);
                                startActivityForResult(intent, Constant.REQ_CODE_MODIFY);
                            }
                        } else {
                            menuInfo.click(MerchantDetailActivity.this);
                        }
                    } else {
                        switch (menuInfo.getMenuCode()) {
                            default:
                                menuInfo.click(MerchantDetailActivity.this);
                                break;

                        }
                    }
                }
            }
        });
    }

    private void initTopInfo(Info info) {
        if (info == null) {
            return;
        }
        if (tvKey != null) {
            tvKey.setText(info.getKey());
        }
        if (tvValue != null) {
            tvValue.setText(info.getValue());
        }
    }

    private void initBalanceInfo(List<Info> balanceList) {
        if (balanceList == null || balanceList.isEmpty()) {
            return;
        }
        if (title1 != null) {
            title1.setText(dealUnit(balanceList.get(0).getValue()));
        }
        if (desc1 != null) {
            desc1.setText(balanceList.get(0).getKey());
        }
        if (title2 != null) {
            title2.setText(dealUnit(balanceList.get(1).getValue()));
        }
        if (desc2 != null) {
            desc2.setText(balanceList.get(1).getKey());
        }
        if (title3 != null) {
            title3.setText(dealUnit(balanceList.get(2).getValue()));
        }
        if (desc3 != null) {
            desc3.setText(balanceList.get(2).getKey());
        }
    }

    private SpannableString dealUnit(String str) {
        if (TextUtils.isEmpty(str)) {
            return new SpannableString("");
        }
        SpannableString ss = new SpannableString(str);
        if (str.indexOf("/") == -1) {
            return ss;
        }
        Pattern pat = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher matcher = pat.matcher(str);
        while (matcher.find()) {
            ss.setSpan(new RelativeSizeSpan(0.5f),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        return ss;
    }

    @OnClick(R.id.imgAvatar)
    public void onImgAvatarClick(View v) {
        if (!isLogin()) {
            showToast("请先登陆");
            startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), Constant.REQ_CODE_LOGIN);
            return;
        }
        initPicPickPopWindow();
    }

    /**
     * 更换相册封面Popwindow
     */
    private void initPicPickPopWindow() {
        if (picPickView == null || picPickPopwindow == null) {
            picPickView = getLayoutInflater().inflate(R.layout.popup_pic_pick, null);
            picPickPopwindow = new PopupWindow(picPickView);
            picPickPopwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            picPickPopwindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            picPickPopwindow.setAnimationStyle(R.style.AnimationFade);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvPick:
                        break;
                    default:
                        picPickPopwindow.dismiss();
                        break;
                }
            }
        };
        LinearLayout emptyLayout = (LinearLayout) picPickView.findViewById(R.id.emptyLayout);
        TextView tvPick = (TextView) picPickView.findViewById(R.id.tvPick);
        TextView tvCancel = (TextView) picPickView.findViewById(R.id.tvCancel);
        emptyLayout.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
        tvPick.setOnClickListener(onClickListener);
        tvPick.setText("更换头像");
        picPickPopwindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void getMerchantsDetail() {
        AccountSettingInfo accountSettingInfo = getAccountSettingInfo();
        if (accountSettingInfo == null) {
            swipeLayout.setRefreshing(false);
            return;
        }
        AccountInfo accountInfo = accountSettingInfo.getAccountInfo();
        if (accountInfo == null) {
            swipeLayout.setRefreshing(false);
            return;
        }
        String account = accountInfo.getAccount();
        if (TextUtils.isEmpty(account)) {
            showToast(getString(R.string.tip_empty_account_name));
            swipeLayout.setRefreshing(false);
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account);
        send(Constant.PK_GETSTOREDETAILS, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                swipeLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.statusCode == 200) {
                    BaseResult<MerchantsDetailInfo> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<MerchantsDetailInfo>>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    if ("0000".equals(result.code)) {
                        if (result.data != null) {
                            sharedFileUtils.putString(SharedFileUtils.MERCHANT_DETAIL_INFO, ObjectUtil.getBASE64String(result.data));
                            initMenuInfo(result.data.getMenu());
                            initTopInfo(result.data.getTopList());
                            initBalanceInfo(result.data.getBalanceList());
                        }
                    } else {
                        showToast(result.getMsg());
                    }
                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                fail(error, msg);
                swipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MerchantsDetailInfo info = (MerchantsDetailInfo) ObjectUtil.getObject(sharedFileUtils.getString(SharedFileUtils.MERCHANT_DETAIL_INFO));
        if (info != null) {
            initMenuInfo(info.getMenu());
            initTopInfo(info.getTopList());
            initBalanceInfo(info.getBalanceList());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQ_CODE_MODIFY || requestCode == Constant.REQ_CODE_LOGIN) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMerchantsDetail();
                    }
                }, 500);
            }
        }
    }
}
