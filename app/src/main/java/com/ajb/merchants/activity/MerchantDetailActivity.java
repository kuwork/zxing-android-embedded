package com.ajb.merchants.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.MenuItemAdapter;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.util.CommonUtils;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.view.MyListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class MerchantDetailActivity extends BaseActivity {

    @ViewInject(R.id.detailListView)
    MyListView detailListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_detail);
        ViewUtils.inject(this);
        initDetailMenu();
        initTitle("");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initHeaderDivider(false);
    }

    /**
     * 初始化侧滑菜单
     */
    private void initDetailMenu() {
        ModularMenu modularMenu;
        String menuJson = CommonUtils.getFromAssets(getBaseContext(), "merchants_details.json");
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
                if (ModularMenu.CODE_MERCHANTS_DETAILS.equals(modularMenu.getModularCode())) {
                    MenuItemAdapter<MenuInfo> adapter = new MenuItemAdapter<>(getBaseContext(), dealMenuGroup(modularMenu.getMenuList()), modularMenu.getModularCode());
                    detailListView.setAdapter(adapter);
                }
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
                            menuInfo.click(MerchantDetailActivity.this);
                        } else {
                            switch (menuInfo.getMenuCode()) {
                                case MenuInfo.TO_EXIT://退出掌停宝
                                    finish();
                                    break;
                                default:
                                    menuInfo.click(MerchantDetailActivity.this);
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
}
