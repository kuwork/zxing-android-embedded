package com.ajb.merchants.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.adapter.PopupWindowAdapter;
import com.ajb.merchants.model.BaseResult;
import com.ajb.merchants.model.Pager;
import com.ajb.merchants.model.PagerWithHeader;
import com.ajb.merchants.util.Constant;
import com.ajb.merchants.view.MyGridView;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Map;

/**
 * 赠送记录
 */
public class CouponGivingRecordActivity extends BaseActivity {

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
    @ViewInject(R.id.tvHeader1)
    TextView tvHeader1;
    @ViewInject(R.id.tvHeader2)
    TextView tvHeader2;
    @ViewInject(R.id.tvHeader3)
    TextView tvHeader3;
    @ViewInject(R.id.tvHeader4)
    TextView tvHeader4;
    private View carNoPopupView;
    private PopupWindow carNoPopupWindow;
    private Pager<Map<String, String>> pager;
    private BaseListAdapter<Map<String, String>> billListAdapter;
    private int PAGE_SIZE = 10;
    private String carNo;
    private String cardSnId;
    private BaseListAdapter<String> provinceAdapter;

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
                if (!TextUtils.isEmpty(cardSnId)) {
                    edCard.setText(cardSnId);
                } else {
                    edCard.setText(null);
                }
                if (!TextUtils.isEmpty(carNo)) {
                    tvCode.setText(carNo.substring(0, 1));
                    edCarno.setText(carNo.substring(1));
                } else {
                    if (provinceAdapter != null && !TextUtils.isEmpty(provinceAdapter.getChecked())) {
                        tvCode.setText(provinceAdapter.getChecked());
                    } else {
                        tvCode.setText("粤");
                    }
                    edCarno.setText(null);
                }

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
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>() {
//            {
//                add(new HashMap<String, String>() {{
//                    put("tv1", "13416333417");
//                    put("tv2", "500小时");
//                    put("tv3", "粤A12345");
//                    put("tv4", "2016-05-15 00:00:00");
//                }});
//                add(new HashMap<String, String>() {{
//                    put("tv1", "13416333417");
//                    put("tv2", "500小时");
//                    put("tv3", "粤A12345");
//                    put("tv4", "2016-05-15 00:00:00");
//                }});
//                add(new HashMap<String, String>() {{
//                    put("tv1", "13416333417");
//                    put("tv2", "500小时");
//                    put("tv3", "粤A12345");
//                    put("tv4", "2016-05-15 00:00:00");
//                }});
//                add(new HashMap<String, String>() {{
//                    put("tv1", "13416333417");
//                    put("tv2", "500小时");
//                    put("tv3", "粤A12345");
//                    put("tv4", "2016-05-15 00:00:00");
//                }});
//            }
//        };
//        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(),null, R.layout.list_item_record_coupon_giving,
//                new String[]{"tv1", "tv2", "tv3", "tv4"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
//        listView.setAdapter(adapter);


        billListAdapter = new BaseListAdapter<Map<String, String>>(getBaseContext(), null, R.layout.list_item_record_coupon_giving, BaseListAdapter.TYPE_COUPON_GIVING_RECORD, null);
        listView.setAdapter(billListAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                pager = null;
                sendLoadLogRequest(1, PAGE_SIZE);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                if (pager == null) {
                    sendLoadLogRequest(1, PAGE_SIZE);
                } else {
                    sendLoadLogRequest(pager.page + 1, PAGE_SIZE);
                }
            }

        });
        pager = null;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.onRefreshComplete();
                listView.setRefreshing(true);
            }
        }, 1000);
    }

    private void sendLoadLogRequest(int index, int size) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(Constant.InterfaceParam.ROWS, size + "");
        params.addQueryStringParameter(Constant.InterfaceParam.PAGE, index + "");
        if (!TextUtils.isEmpty(carNo)) {
            params.addQueryStringParameter(Constant.InterfaceParam.CARNO, carNo);
        }
        if (!TextUtils.isEmpty(cardSnId)) {
            params.addQueryStringParameter(Constant.InterfaceParam.CARD_SN_ID, cardSnId);
        }
        send(Constant.PK_GET_SEND_RECORDS, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        listView.onRefreshComplete();
                        try {
                            BaseResult<PagerWithHeader> rr = gson.fromJson(
                                    responseInfo.result,
                                    new TypeToken<BaseResult<PagerWithHeader>>() {
                                    }.getType());
                            if ("0000".equals(rr.code)) {
                                if (rr.data != null) {
                                    if (rr.data.headers != null && rr.data.headers.size() == 4) {
                                        billListAdapter.setHeaders(rr.data.headers);
                                        if (tvHeader1 != null) {
                                            tvHeader1.setText(rr.data.headers.get(0).get("title"));
                                        }
                                        if (tvHeader2 != null) {
                                            tvHeader2.setText(rr.data.headers.get(1).get("title"));
                                        }
                                        if (tvHeader3 != null) {
                                            tvHeader3.setText(rr.data.headers.get(2).get("title"));
                                        }
                                        if (tvHeader4 != null) {
                                            tvHeader4.setText(rr.data.headers.get(3).get("title"));
                                        }
                                    }
                                    if (rr.data.pager.page == rr.data.pager.pageCount) {
                                        showToast("到达最后一页");
                                        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    } else {
                                        listView.setMode(PullToRefreshBase.Mode.BOTH);
                                    }
                                    if (rr.data.pager.list.size() > 0) {
                                        if (rr.data.pager.page <= 1) {
                                            billListAdapter
                                                    .update(rr.data.pager.list);
                                            billListAdapter
                                                    .notifyDataSetChanged();
                                        } else {
                                            billListAdapter
                                                    .append(rr.data.pager.list);
                                            billListAdapter
                                                    .notifyDataSetChanged();
                                        }
                                    } else {
                                        showToast(getString(R.string.error_no_more));
                                        if (billListAdapter.getCount() == 0) {
                                            showErrorPage(listView, R.string.error_no_more, R.mipmap.norecord);
                                        }
                                    }
                                    pager = rr.data.pager;
                                } else {
                                    showToast("刷新列表失败");
                                }
                            } else {
                                showToast(rr.msg);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            showToast("刷新列表失败");
                        } finally {
                            listView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.e(error.getExceptionCode() + ":" + msg);
                        listView.onRefreshComplete();
                        if (error.getExceptionCode() == 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorPage(listView, R.string.error_network, R.mipmap.network);
                                }
                            }, 500);
                        } else {
                            fail(error, msg);
                        }
                    }
                });
    }

    @Override
    public void onErrorPageClick() {
        super.onErrorPageClick();
        if (listView.isRefreshing()) {
            listView.onRefreshComplete();
        }
        listView.setRefreshing();
    }

    @OnClick(R.id.space)
    public void onSpaceClick(View v) {
        layoutSearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.sure_btn)
    public void onSureClick(View v) {

        if (tabCarNo.isSelected()) {
            if (TextUtils.isEmpty(edCarno.getText().toString().trim())) {
                carNo = null;
            } else {
                carNo = tvCode.getText().toString().trim() + edCarno.getText().toString().trim();
            }
            cardSnId = null;
            pager = null;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.onRefreshComplete();
                    listView.setRefreshing(true);
                }
            }, 200);
        } else if (tabCard.isSelected()) {
            carNo = null;
            cardSnId = edCard.getText().toString().trim();
            pager = null;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.onRefreshComplete();
                    listView.setRefreshing(true);
                }
            }, 200);
        }
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
            if (provinceAdapter == null) {
                provinceAdapter = PopupWindowAdapter.getAdapter(getBaseContext());
            }
            gridView.setAdapter(provinceAdapter);
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
        if (provinceAdapter != null) {
            provinceAdapter.setChecked(textView.getText().toString());
        }
        carNoPopupWindow.showAtLocation(textView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constant.REQ_CODE_LOGIN) {
            pager = null;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.onRefreshComplete();
                    listView.setRefreshing(true);
                }
            }, 1000);
        }
    }
}
