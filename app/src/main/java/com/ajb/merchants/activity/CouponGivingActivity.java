package com.ajb.merchants.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.Product;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MyListView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设置界面
 */
public class CouponGivingActivity extends BaseActivity {
    @ViewInject(R.id.tabMoney)
    View tabMoney;
    @ViewInject(R.id.tabTime)
    View tabTime;
    @ViewInject(R.id.infoListView)
    MyListView listView;
    @ViewInject(R.id.gridView)
    MyGridView gridView;
    @ViewInject(R.id.tvEdit)
    TextView tvEdit;
    @ViewInject(R.id.tvSave)
    TextView tvSave;
    @ViewInject(R.id.btnSure)
    Button btnSure;
    private BaseListAdapter<Info> infoAdapter;
    private BaseListAdapter<Product> gridViewAdapter;
    private List<Product> moneyList, timeList;

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
        }
        initTitle(title);
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<Info> list = Arrays.asList(
                new Info("车牌号码:", "粤A12345"),
                new Info("进场时间:", "2016-05-12 00:00:00"));
        initListView(list);
        try {
            String moneyJsonStr = sharedFileUtils.getString(SharedFileUtils.COUPON_MONEY_LIST);
            moneyList = gson.fromJson(moneyJsonStr, new TypeToken<List<Product>>() {
            }.getType());
            String timeJsonStr = sharedFileUtils.getString(SharedFileUtils.COUPON_TIME_LIST);
            timeList = gson.fromJson(timeJsonStr, new TypeToken<List<Product>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (moneyList == null) {
            moneyList = new ArrayList<>();
        }
        if (timeList == null) {
            timeList = new ArrayList<>();
        }

        tabMoney.performClick();
    }

    /**
     * @param list    数据
     * @param checked 默认选中
     */
    private void initGridView(List<Product> list, String checked, boolean isEditable) {
        if (null == gridView) {
            return;
        }
        List<Product> data = new ArrayList<>();
        data.addAll(list);
        data.add(new Product("+", ""));
        if (null == gridViewAdapter) {
            gridViewAdapter = new BaseListAdapter<Product>(
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
                    BaseListAdapter<Product> adapter = (BaseListAdapter<Product>) parent.getAdapter();
                    Product item = adapter.getItem(position);
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

    private void initListView(List<Info> infoList) {
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

    @OnClick(value = {R.id.tabMoney, R.id.tabTime})
    public void onTabClick(View v) {
        ViewGroup parent = (ViewGroup) v.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setSelected(false);
        }
        tvSave.setVisibility(View.GONE);
        tvEdit.setVisibility(View.VISIBLE);
        switch (v.getId()) {
            case R.id.tabMoney:
                v.setSelected(true);
                initGridView(moneyList, "", false);

                break;
            case R.id.tabTime:
                v.setSelected(true);
                initGridView(timeList, "", false);
                break;
        }
    }

    @OnClick(value = {R.id.tvEdit, R.id.tvSave})
    public void onEditOrSaveClick(View v) {

        Product item = null;
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
                break;
            case R.id.tvSave:
                v.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                List<Product> dataList = gridViewAdapter.getDataList();
                if (tabMoney.isSelected()) {
                    moneyList.clear();
                    moneyList.addAll(dataList);
                    sharedFileUtils.putString(SharedFileUtils.COUPON_MONEY_LIST, gson.toJson(moneyList));
                    initGridView(moneyList, "", false);
                } else {
                    timeList.clear();
                    timeList.addAll(dataList);
                    sharedFileUtils.putString(SharedFileUtils.COUPON_TIME_LIST, gson.toJson(timeList));
                    initGridView(timeList, "", false);
                }
                break;
        }
    }


    @OnClick(value = R.id.btnSure)
    public void onSureClick(View v) {
        if (gridViewAdapter != null) {
            String checked = gridViewAdapter.getChecked();
            if (TextUtils.isEmpty(checked)) {
                showToast("请选择一张优惠券!");
                return;
            } else if ("+".equals(checked)) {
                String editingStr = gridViewAdapter.getEditingStr().trim();
                if (TextUtils.isEmpty(editingStr)) {
                    showToast("请输入自定义时间!");
                    return;
                }
            }
        }
        showOkCancelAlertDialog(false, "提示！",
                "确定要赠送？",
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
        String checked = null;
        if (gridViewAdapter != null) {
            checked = gridViewAdapter.getChecked();
            if (TextUtils.isEmpty(checked)) {
                showToast("请选择一张优惠券!");
                return;
            }
        }
        if (tabMoney.isSelected()) {
            if ("+".equals(checked)) {//自定义
                String editingStr = gridViewAdapter.getEditingStr().trim();
                if (TextUtils.isEmpty(editingStr)) {
                    showToast("请输入自定义金额!");
                    return;
                }
                Product p = new Product(editingStr, "元");
                checked = p.toString();
                //添加进历史
                if (!moneyList.contains(p)) {
                    addAndSort(moneyList, p);
                    sharedFileUtils.putString(SharedFileUtils.COUPON_MONEY_LIST, gson.toJson(moneyList));
                    initGridView(moneyList, p.toString(), false);
                }
            }
            showToast(checked);

        } else {
            if ("+".equals(checked)) {//自定义
                String editingStr = gridViewAdapter.getEditingStr().trim();
                if (TextUtils.isEmpty(editingStr)) {
                    showToast("请输入自定义时间!");
                    return;
                }
                Product p = new Product(editingStr, "小时");
                checked = p.toString();
                //添加进历史
                if (!timeList.contains(p)) {
                    addAndSort(timeList, p);
                    sharedFileUtils.putString(SharedFileUtils.COUPON_TIME_LIST, gson.toJson(timeList));
                    initGridView(timeList, p.toString(), false);
                }
            }
            showToast(checked);
        }
    }

    private void addAndSort(List<Product> list, Product p) {
        list.add(p);
    }

}
