package com.ajb.merchants.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.PopupWindowAdapter;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赠送记录
 */
public class CouponGivingRecordActivity extends BaseActivity {

    @ViewInject(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;
    @ViewInject(R.id.listview)
    PullToRefreshListView listView;
    @ViewInject(R.id.layoutSearch)
    View layoutSearch;
    @ViewInject(R.id.layoutPlateNum)
    View layoutPlateNum;
    @ViewInject(R.id.layoutCard)
    View layoutCard;
    @ViewInject(R.id.tabCarNo)
    View tabCarNo;
    @ViewInject(R.id.tabCard)
    View tabCard;
    @ViewInject(R.id.tvCode)
    TextView tvCode;
    @ViewInject(R.id.edCarno)
    EditText edCarno;
    @ViewInject(R.id.edCard)
    EditText edCard;
    private View carNoPopupView;
    private PopupWindow carNoPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_coupongiving);
        ViewUtils.inject(this);
        String title = "赠送记录";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constant.KEY_TITLE)) {
            title = bundle.getString(Constant.KEY_TITLE);
        }
        initTitle(title);
        initBackClick(R.id.NO_RES, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initMenuClick(NO_ICON, "", null, R.drawable.actionbar_search, "搜索", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSearch.setVisibility(View.VISIBLE);
                tabCarNo.performClick();

            }
        });
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

        //测试数据
        List<Map<String, String>> list = new ArrayList<Map<String, String>>() {
            {
                add(new HashMap<String, String>() {{
                    put("tv1", "13416333417");
                    put("tv2", "500小时");
                    put("tv3", "粤A12345");
                    put("tv4", "2016-05-15 00:00:00");
                }});
                add(new HashMap<String, String>() {{
                    put("tv1", "13416333417");
                    put("tv2", "500小时");
                    put("tv3", "粤A12345");
                    put("tv4", "2016-05-15 00:00:00");
                }});
                add(new HashMap<String, String>() {{
                    put("tv1", "13416333417");
                    put("tv2", "500小时");
                    put("tv3", "粤A12345");
                    put("tv4", "2016-05-15 00:00:00");
                }});
                add(new HashMap<String, String>() {{
                    put("tv1", "13416333417");
                    put("tv2", "500小时");
                    put("tv3", "粤A12345");
                    put("tv4", "2016-05-15 00:00:00");
                }});
            }
        };
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), list, R.layout.list_item_record_coupon_giving,
                new String[]{"tv1", "tv2", "tv3", "tv4"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
        listView.setAdapter(adapter);
    }

    @OnClick(R.id.space)
    public void onSpaceClick(View v) {
        layoutSearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.sure_btn)
    public void onSureClick(View v) {
        layoutSearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.tvCode)
    public void onTvCodeClick(View v) {
        showCarNoPopupWindow((TextView) v);
    }

    @OnClick(value = {R.id.tabCarNo, R.id.tabCard})
    public void onTabClick(View v) {
        ViewGroup parent = (ViewGroup) v.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setSelected(false);
        }
        switch (v.getId()) {
            case R.id.tabCarNo:
                v.setSelected(true);
                layoutPlateNum.setVisibility(View.VISIBLE);
                layoutCard.setVisibility(View.GONE);
                break;
            case R.id.tabCard:
                v.setSelected(true);
                layoutPlateNum.setVisibility(View.GONE);
                layoutCard.setVisibility(View.VISIBLE);
                break;
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
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BaseListAdapter<String> adapter = (BaseListAdapter<String>) parent
                            .getAdapter();
                    String item = adapter.getItem(position);
                    adapter.setChecked(item);
                    if (tvCode != null) {
                        tvCode.setText(item);
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
