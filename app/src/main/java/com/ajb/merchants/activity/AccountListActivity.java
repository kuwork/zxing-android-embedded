package com.ajb.merchants.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.AccoutManagementAdapter;
import com.ajb.merchants.model.MerchantsAccoutInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class AccountListActivity extends BaseActivity {

    private Context context;
    @ViewInject(R.id.listview)
    private ListView listview;//账户列表
    AccoutManagementAdapter accoutManagementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        ViewUtils.inject(this);
        initView();
        setModel();
    }


    /**
     * 模拟静态数据
     */
    private void setModel() {
        List<String> listCompetence = new ArrayList<String>();
        listCompetence.add("扫卡优惠赠送");
        listCompetence.add("回收优惠");
        listCompetence.add("车牌优惠赠送");
        listCompetence.add("卡号优惠赠送");
        listCompetence.add("发布优惠信息");
        String[] strName = {"张三", "李四", "李四1", "李四2", "李四3", "李四4", "李四5"};
        String[] strAddress = {"高德汇", "东圃大厦", "东圃大厦1", "东圃大厦2", "东圃大厦3", "东圃大厦4", "东圃大厦5"};
        String[] strRemark = {"111111", "222222", "xas", "xa", "342342", "5656", "e1e1e"};

        List<MerchantsAccoutInfo> listmodel = new ArrayList<MerchantsAccoutInfo>();

        for (int i = 0; i < strName.length; i++) {
            MerchantsAccoutInfo merchantsAccoutInfo = new MerchantsAccoutInfo();
            merchantsAccoutInfo.setMa_accoutname(strName[i]);
            merchantsAccoutInfo.setMa_address(strAddress[i]);
            merchantsAccoutInfo.setMa_remark(strRemark[i]);
            merchantsAccoutInfo.setMa_listCompetence(listCompetence);
            listmodel.add(merchantsAccoutInfo);
        }
        AccoutManagementAdapter accoutManagementAdapter = new AccoutManagementAdapter(context, listmodel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (tag.equals("edit")) {

                } else if (tag.equals("delete")) {

                }
                showOkCancelAlertDialog(false, "确定删除账户？",
                        "",
                        "确定", "取消",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(
                                    View arg0) {
                                dimissOkCancelAlertDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(
                                    View arg0) {
                                dimissOkCancelAlertDialog();
                            }
                        });
            }
        });
        listview.setAdapter(accoutManagementAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout lv_detail = (LinearLayout) view.findViewById(R.id.lv_detail);
                if (lv_detail.getVisibility() == View.VISIBLE) {
                    lv_detail.setVisibility(View.GONE);
                } else {
                    lv_detail.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    /**
     * 视图初始化
     */
    private void initView() {
        context = AccountListActivity.this;
        initTitle("账号管理");
        initBackClick(NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //图标，对应文本，对应监听事件
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_add, "新增", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AccountManagementActivity.class);
                startActivity(intent);
            }
        });
    }
}
