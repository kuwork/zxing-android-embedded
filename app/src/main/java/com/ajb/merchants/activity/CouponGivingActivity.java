package com.ajb.merchants.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.AccountSettingInfo;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.CarInParkingBuilder;
import com.ajb.merchants.model.CarParkingInInfo;
import com.ajb.merchants.model.Coupon;
import com.ajb.merchants.model.CouponSendType;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.MyProgressDialog;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MyListView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设置界面
 */
public class CouponGivingActivity extends BaseActivity {
    @ViewInject(R.id.infoListView)
    MyListView listView;
    @ViewInject(R.id.gridView)
    MyGridView gridView;
    @ViewInject(R.id.tabGridView)
    MyGridView tabGridView;
    @ViewInject(R.id.tvEdit)
    TextView tvEdit;
    @ViewInject(R.id.tvSave)
    TextView tvSave;
    @ViewInject(R.id.btnSure)
    Button btnSure;
    private BaseListAdapter<Info> infoAdapter;
    private BaseListAdapter<Coupon> gridViewAdapter;
    private CarInParkingBuilder carInParkingBuilder;
    private Dialog mDialog;
    private CarParkingInInfo carParkingInInfo;
    private BaseListAdapter<CouponSendType> tabGridViewAdapter;
    private CouponSendType couponSendTypeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupongiving);
        ViewUtils.inject(this);
        String title = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(Constant.KEY_TITLE)) {
                title = bundle.getString(Constant.KEY_TITLE);
            }
            if (bundle.containsKey(Constant.KEY_CARINPARKING)) {
                carInParkingBuilder = (CarInParkingBuilder) bundle.getSerializable(Constant.KEY_CARINPARKING);
                if (carInParkingBuilder != null) {
                    requestCarParkInInfo(carInParkingBuilder);
                }
            }
        }
        initTitle(title);
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        List<Info> list = Arrays.asList(
//                new Info("车牌号码:", "粤A12345"),
//                new Info("进场时间:", "2016-05-12 00:00:00"));
//        initInfoListView(list);

//        try {
//            String moneyJsonStr = sharedFileUtils.getString(SharedFileUtils.COUPON_MONEY_LIST);
//            moneyList = gson.fromJson(moneyJsonStr, new TypeToken<List<Coupon>>() {
//            }.getType());
//            String timeJsonStr = sharedFileUtils.getString(SharedFileUtils.COUPON_TIME_LIST);
//            timeList = gson.fromJson(timeJsonStr, new TypeToken<List<Coupon>>() {
//            }.getType());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (moneyList == null) {
//            moneyList = new ArrayList<>();
//        }
//        if (timeList == null) {
//            timeList = new ArrayList<>();
//        }
    }

    private void initGridView(CouponSendType item, String checked, boolean isEditable) {
        if (item == null) {
            return;
        }
        if (null == gridView) {
            return;
        }
        this.couponSendTypeSelected = item;
        List<Coupon> list = null;
        List<Coupon> data = new ArrayList<>();
        if (item.getSendModel() == CouponSendType.TYPE_SERVER) {
            list = item.getFavList();
            if (list != null && list.size() > 0) {
                data.addAll(list);
            }
            tvSave.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
        } else if (item.getSendModel() == CouponSendType.TYPE_NATIVE) {
            try {
                String jsonStr = sharedFileUtils.getString(SharedFileUtils.COUPON_LIST + "." + item.getTitle());
                list = gson.fromJson(jsonStr, new TypeToken<List<Coupon>>() {
                }.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) {
                item.setFavList(list);
                data.addAll(list);
            }
            data.add(new Coupon("+", ""));
            tvSave.setVisibility(View.GONE);
            tvEdit.setVisibility(View.VISIBLE);
        }

        if (null == gridViewAdapter) {
            gridViewAdapter = new BaseListAdapter<Coupon>(
                    getBaseContext(),
                    data,
                    R.layout.grid_item_coupon, BaseListAdapter.TYPE_COUPON_GIVING,
                    null);
            gridViewAdapter.setChecked(checked);
            gridViewAdapter.setEditing(false);
            gridViewAdapter.setEditable(isEditable);
            gridView.setAdapter(gridViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BaseListAdapter<Coupon> adapter = (BaseListAdapter<Coupon>) parent.getAdapter();
                    Coupon item = adapter.getItem(position);
                    if (adapter.isEditable()) {
                        showToast("删除" + item.toString());
                        adapter.removeAt(position);
                    } else {
                        adapter.setChecked(item.toString());
                        btnSure.setEnabled(true);
                        if ("+".equals(item.getValue())) {
                            adapter.setEditing(true);
                        } else {
                            adapter.setEditing(false);
                        }
                    }
                }
            });
        } else {
            gridViewAdapter.setChecked(checked);
            gridViewAdapter.setEditing(false);
            gridViewAdapter.setEditable(isEditable);
            gridViewAdapter.update(data);
        }
    }

    private void initInfoListView(List<Info> infoList) {
        if (null == listView) {
            return;
        }
        if (null == infoAdapter) {
            infoAdapter = new BaseListAdapter<Info>(
                    getBaseContext(),
                    infoList,
                    R.layout.list_item_coupon_giving_info,
                    null);
            listView.setAdapter(infoAdapter);
        } else {
            infoAdapter.update(infoList);
        }
    }

    @OnClick(value = {R.id.tvEdit, R.id.tvSave})
    public void onEditOrSaveClick(View v) {
        Coupon item = null;
        switch (v.getId()) {
            case R.id.tvEdit:
                if (gridViewAdapter == null || gridViewAdapter.getCount() == 1) {
                    showToast("不可编辑");
                    return;
                }
                v.setVisibility(View.GONE);
                tvSave.setVisibility(View.VISIBLE);
                item = gridViewAdapter.getItem(gridViewAdapter.getCount() - 1);
                if ("+".equals(item.getValue()) && TextUtils.isEmpty(item.getUnit())) {
                    gridViewAdapter.removeAt(gridViewAdapter.getCount() - 1);
                }
                gridViewAdapter.setEditable(true);
                gridViewAdapter.setChecked(null);
                btnSure.setEnabled(false);
                break;
            case R.id.tvSave:
                v.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                btnSure.setEnabled(false);
                List<Coupon> dataList = gridViewAdapter.getDataList();
                if (couponSendTypeSelected != null) {
                    if (couponSendTypeSelected.getSendModel() == CouponSendType.TYPE_NATIVE) {
                        sharedFileUtils.putString(SharedFileUtils.COUPON_LIST + "." + couponSendTypeSelected.getTitle(), gson.toJson(dataList));
                        initGridView(couponSendTypeSelected, "", false);
                    }
                }
                break;
        }
    }


    @OnClick(value = R.id.btnSure)
    public void onSureClick(View v) {
        String tip = "";
        if (gridViewAdapter != null) {
            String checked = gridViewAdapter.getChecked();
            tip = checked;
            if (TextUtils.isEmpty(checked)) {
                showToast("请选择一张优惠券!");
                return;
            } else if ("+".equals(checked)) {
                String editingStr = gridViewAdapter.getEditingStr().trim();
                if (TextUtils.isEmpty(editingStr)) {
                    showToast("请输入自定义时间!");
                    return;
                } else {
                    tip = editingStr;
                    if (couponSendTypeSelected != null) {
                        tip += couponSendTypeSelected.getUnit();
                    }
                }
            }
        }
        showOkCancelAlertDialog(false, "提示！",
                "确定要赠送" + tip + "?",
                "是", "否",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        give();
                        dimissOkCancelAlertDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissOkCancelAlertDialog();
                    }
                });


    }

    private void give() {
        AccountSettingInfo account = getAccountSettingInfo();
        if (account.getAccountInfo() == null
                || TextUtils.isEmpty(account.getAccountInfo().getAccount())
                || carParkingInInfo == null
                || couponSendTypeSelected == null) {
            showToast(getString(R.string.error_coupon_send_fail));
            return;
        }
        String checked = null;
        if (gridViewAdapter != null) {
            checked = gridViewAdapter.getChecked();
            if (TextUtils.isEmpty(checked)) {
                showToast("请选择一张优惠券!");
                return;
            }
        }

        if (couponSendTypeSelected.getSendModel() == CouponSendType.TYPE_NATIVE) {
            if ("+".equals(checked)) {//自定义
                String editingStr = gridViewAdapter.getEditingStr().trim();
                if (TextUtils.isEmpty(editingStr)) {
                    showToast("请输入自定义金额!");
                    return;
                }
                Coupon p = new Coupon(editingStr, couponSendTypeSelected.getUnit());
                checked = p.toString();
                //添加进历史
                addAndSort(couponSendTypeSelected.getFavList(), p);
                sharedFileUtils.putString(SharedFileUtils.COUPON_LIST + "." + couponSendTypeSelected.getTitle(), gson.toJson(couponSendTypeSelected.getFavList()));
                initGridView(couponSendTypeSelected, checked, false);
            }
        }
        showToast(checked);
        Pattern pat = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher mat = pat.matcher(checked);
        RequestParams requestParams = new RequestParams();
        requestParams.addQueryStringParameter(Constant.InterfaceParam.ACCOUNT, account.getAccountInfo().getAccount());
        requestParams.addQueryStringParameter(Constant.InterfaceParam.CARDID, carParkingInInfo.getCardId());
        requestParams.addQueryStringParameter(Constant.InterfaceParam.CARNO, carParkingInInfo.getCarNo());
        requestParams.addQueryStringParameter(Constant.InterfaceParam.ACCTIME, carParkingInInfo.getAccTime());
        requestParams.addQueryStringParameter(Constant.InterfaceParam.COUPON_TYPE, couponSendTypeSelected.getCouponType() + "");
        if (couponSendTypeSelected.getCouponType() == CouponSendType.COUPON_MONEY) {
            requestParams.addQueryStringParameter(Constant.InterfaceParam.VALUE, mat.replaceAll(""));
        } else if (couponSendTypeSelected.getCouponType() == CouponSendType.COUPON_TIME) {
            requestParams.addQueryStringParameter(Constant.InterfaceParam.TIME, mat.replaceAll(""));
        }
        send(Constant.PK_SEND_COUPON, requestParams, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = MyProgressDialog.createLoadingDialog(
                        CouponGivingActivity.this, "请稍后...");
                mDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (responseInfo.statusCode == 200) {
                    BaseResult<String> result = null;
                    try {
                        result = gson.fromJson(
                                responseInfo.result,
                                new TypeToken<BaseResult<String>>() {
                                }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        showToast(getString(R.string.error_network_short));
                        return;
                    }
                    showToast(result.msg);
                    if ("0000".equals(result.code)) {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                fail(error, msg);
            }
        });
    }

    private void addAndSort(List<Coupon> list, Coupon p) {
        if (!list.contains(p)) {
            list.add(p);
        }
    }

    private void requestCarParkInInfo(CarInParkingBuilder carInParkingBuilder) {
        if (carInParkingBuilder == null
                || (TextUtils.isEmpty(carInParkingBuilder.getCardSnId()) && TextUtils.isEmpty(carInParkingBuilder.getCarNo()))) {
            showToast("查询失败");
            return;
        }
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.CARNO, carInParkingBuilder.getCarNo());
        params.addQueryStringParameter(Constant.InterfaceParam.CARD_SN_ID, carInParkingBuilder.getCardSnId());
        send(Constant.PK_GET_CAR_MERCHANT_MSG, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        mDialog = MyProgressDialog.createLoadingDialog(
                                CouponGivingActivity.this, "请稍后...");
                        mDialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (responseInfo.statusCode == 200) {
                            BaseResult<CarParkingInInfo> result = null;
                            try {
                                result = gson.fromJson(
                                        responseInfo.result,
                                        new TypeToken<BaseResult<CarParkingInInfo>>() {
                                        }.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (result == null) {
                                showOkAlertDialog(false, "提示", getString(R.string.error_network_short), getString(R.string.action_close), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dimissOkAlertDialog();
                                        finish();
                                    }
                                });
                                return;
                            }
                            if ("0000".equals(result.code)) {
                                initPageSetting(result.data);
                            } else {
                                showOkAlertDialog(false, "提示", result.msg, getString(R.string.action_close), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dimissOkAlertDialog();
                                        finish();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        fail(error, msg);
                    }

                }
        );
    }

    /**
     * 页面设置
     */
    private void initPageSetting(CarParkingInInfo data) {
        if (data == null) {
            return;
        }
        this.carParkingInInfo = data;
        initInfoListView(data.getInfoList());
        if (data.getTopList() != null && data.getTopList().size() > 0) {
            if (tabGridView != null) {
                if (tabGridViewAdapter == null) {
                    tabGridViewAdapter = new BaseListAdapter<CouponSendType>(getBaseContext(), data.getTopList(), R.layout.grid_item_tab, null);
                    tabGridView.setAdapter(tabGridViewAdapter);
                    tabGridViewAdapter.setChecked(data.getTopList().get(0).getTitle());
                    initGridView(data.getTopList().get(0), "", false);
                    tabGridView.setNumColumns(data.getTopList().size());
                    tabGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            BaseListAdapter<CouponSendType> adapter = (BaseListAdapter<CouponSendType>) parent.getAdapter();
                            CouponSendType item = adapter.getItem(position);
                            if (item != null) {
                                adapter.setChecked(item.getTitle());
                                initGridView(item, "", false);
                            }

                        }
                    });
                } else {
                    tabGridViewAdapter.update(data.getTopList());
                }

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (carInParkingBuilder != null) {
                requestCarParkInInfo(carInParkingBuilder);
            }
        }
    }

}
