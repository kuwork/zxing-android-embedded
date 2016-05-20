package com.ajb.merchants.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.AccountInfo;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.model.BaseModel;
import com.ajb.merchants.model.CouponSendType;
import com.ajb.merchants.model.HomePageInfo;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.Product;
import com.ajb.merchants.model.filter.ConditionValue;
import com.ajb.merchants.view.MyGridView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseListAdapter<T> extends BaseAdapter {

    public final static String TYPE_CAR_NUM = "CAR_NUM";
    public final static String TYPE_COUPON_GIVING = "COUPON_GIVING";
    public final static String TYPE_COUPON_GIVING_RECORD = "COUPON_GIVING_RECORD";
    private final BitmapUtils bitmapUtils;
    private String typeName;
    private List<T> dataList;
    private Context context;
    private LayoutInflater mInflater;
    private View.OnClickListener onClickListener;
    private int res;
    private int currentPosition = -1;
    private boolean isEditable = false;//可否删除
    private String checked;
    private List<Map<String, String>> headers;//标题栏数据

    public List<Map<String, String>> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Map<String, String>> headers) {
        Collections.sort(headers, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                //o1，o2是list中的Map，可以在其内取得值，按其排序，此例为升序，s1和s2是排序字段值
                Integer s1 = Integer.parseInt(o1.get("id"));
                Integer s2 = Integer.parseInt(o2.get("id"));
                if (s1 > s2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        this.headers = headers;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
        notifyDataSetChanged();
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    private boolean isEditing = false;//编辑中
    private String editingStr = "";//编辑文本

    public String getEditingStr() {
        return editingStr;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
        notifyDataSetChanged();
    }

    public BaseListAdapter(Context context, List<T> dataList, int res, View.OnClickListener onClickListener) {
        this(context, dataList, res, null, onClickListener);
    }

    public BaseListAdapter(Context context, List<T> dataList, int res, String typeName, View.OnClickListener onClickListener) {
        if (dataList == null) {
            this.dataList = new ArrayList<T>();
        } else {
            this.dataList = dataList;
        }
        this.context = context;
        this.typeName = typeName;
        mInflater = LayoutInflater.from(context);
        bitmapUtils = new BitmapUtils(context);
        this.res = res;
        this.onClickListener = onClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(res, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.init(context, dataList.get(position), position);
        return convertView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(List<T> dataList) {
        if (dataList == null) {
            this.dataList = new ArrayList<T>();
        } else {
            this.dataList = dataList;
        }
        notifyDataSetChanged();
    }

    public void append(List<T> result) {
        if (dataList == null) {
            return;
        } else {
            this.dataList.addAll(result);
        }
        notifyDataSetChanged();
    }

    public void removeAt(int index) {
        if (dataList != null) {
            if (index < getCount()) {
                ArrayList<T> list = new ArrayList<T>();
                for (int i = 0; i < getCount(); i++) {
                    if (i != index) {
                        list.add(getItem(i));
                    }
                }
                update(list);
            }
        }
    }

    public void add(T product) {
        if (dataList != null) {
            dataList.add(product);
            notifyDataSetChanged();
        }
    }


    class ViewHolder {
        @ViewInject(R.id.title)
        TextView title;
        @ViewInject(R.id.tvPermisssion)
        TextView tvPermisssion;
        @ViewInject(R.id.edTitle)
        EditText edTitle;
        @ViewInject(R.id.desc)
        TextView desc;
        @ViewInject(R.id.tvDesc)
        TextView tvDesc;
        @ViewInject(R.id.select_ib)
        ImageView img;
        @ViewInject(R.id.btn_edit)
        ImageView imgEdit;
        @ViewInject(R.id.btn_delete)
        ImageView imgDelete;
        @ViewInject(R.id.item_bg)
        RelativeLayout bg;
        @ViewInject(R.id.divider)
        View divider;
        @ViewInject(R.id.gridView)
        MyGridView gridView;
        @ViewInject(R.id.tv1)
        TextView tv1;
        @ViewInject(R.id.tv2)
        TextView tv2;
        @ViewInject(R.id.tv3)
        TextView tv3;
        @ViewInject(R.id.tv4)
        TextView tv4;
        private TextWatcher tw;
        private InputFilter lengthfilter;

        public ViewHolder() {
            super();
        }

        public void init(Context context, T info, int position) {
            if (info instanceof HomePageInfo) {
                if (title != null) {
                    title.setText(((HomePageInfo) info).getName());
                }
                if (img != null) {
                    if (isCheck(info)) {
                        img.setImageResource(R.drawable.actionbar_check);
                        img.setVisibility(View.VISIBLE);
                    } else {
                        img.setVisibility(View.GONE);
                    }
                }
            } else if (info instanceof Info) {
                if (title != null) {
                    title.setText(((Info) info).getKey());
                }
                if (null != desc) {
                    desc.setText(((Info) info).getValue());
                }
            } else if (info instanceof BaseModel) {
                if (null != title) {
                    title.setText(((BaseModel) info).getTitle());
                }
                if (position == currentPosition) {
                    if (null != img) {
                        img.setVisibility(View.VISIBLE);
                    }
                    if (bg != null) {
                        bg.setBackgroundResource(R.drawable.btn_stroke_press);
                    }
                } else {
                    if (null != img) {
                        img.setVisibility(View.GONE);
                    }
                    if (bg != null) {
                        bg.setBackgroundResource(R.drawable.btn_stroke_selector);
                    }
                }
            } else if (info instanceof ConditionValue) {
                if (title != null) {
                    title.setText(((ConditionValue) info).getDataName());
                }
            } else if (info instanceof Product) {
                if (info instanceof BalanceLimitInfo) {
                    // R.layout.balance_limit_item
                    if (title != null) {
                        SpannableStringBuilder ss = new SpannableStringBuilder();
                        ss.append(((BalanceLimitInfo) info).getValue());
                        ss.append(((BalanceLimitInfo) info).getUnit());
                        ss.setSpan(new RelativeSizeSpan(0.5f), ss.length() - ((BalanceLimitInfo) info).getUnit().length(), ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        title.setText(ss);
                    }
                    if (desc != null) {
                        desc.setText(((BalanceLimitInfo) info).getDesc());
                    }
                    if (divider != null) {
                        if ((position + 1) % 4 == 0) {
                            divider.setVisibility(View.GONE);
                        } else {
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    switch (typeName) {
                        case TYPE_COUPON_GIVING:
                            if ("+".equals(((Product) info).getValue()) && TextUtils.isEmpty(((Product) info).getUnit())) {
                                if (title != null && edTitle != null) {
                                    if (isEditing) {
                                        title.setVisibility(View.GONE);
                                        edTitle.setVisibility(View.VISIBLE);
                                        if (tw == null) {
                                            tw = new TextWatcher() {
                                                private boolean isChanged = false;

                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                    editingStr = s.toString();

                                                }
                                            };
                                        }
                                        edTitle.addTextChangedListener(tw);
                                        if (lengthfilter != null) {
                                            lengthfilter = new InputFilter() {
                                                public CharSequence filter(CharSequence source, int start, int end,
                                                                           Spanned dest, int dstart, int dend) {
                                                    // 删除等特殊字符，直接返回
                                                    if ("".equals(source.toString())) {
                                                        return null;
                                                    }
                                                    String dValue = dest.toString();
                                                    String[] splitArray = dValue.split("\\.");
                                                    if (splitArray.length > 1) {
                                                        String dotValue = splitArray[1];
                                                        //输入框小数的位数
                                                        int DECIMAL_DIGITS = 1;
                                                        int diff = dotValue.length() + 1 - DECIMAL_DIGITS;
                                                        if (diff > 0) {
                                                            return source.subSequence(start, end - diff);
                                                        }
                                                    }
                                                    return null;
                                                }
                                            };
                                        }
                                        edTitle.setFilters(new InputFilter[]{lengthfilter});
                                        edTitle.requestFocus();
                                        edTitle.setSelection(edTitle.getText().length());

                                    } else {
                                        title.setVisibility(View.VISIBLE);
                                        edTitle.setVisibility(View.GONE);
                                        if (tw != null) {
                                            edTitle.removeTextChangedListener(tw);
                                        }
                                        title.setText(((Product) info).getValue());
                                    }
                                    title.setSelected(isCheck(info));
                                    edTitle.setSelected(isCheck(info));
                                }
                            } else {
                                if (title != null) {
                                    title.setVisibility(View.VISIBLE);
                                    if (edTitle != null) {
                                        edTitle.setVisibility(View.GONE);
                                    }
                                    SpannableStringBuilder ss = new SpannableStringBuilder();
                                    ss.append(((Product) info).getValue());
                                    ss.append(((Product) info).getUnit());
                                    title.setText(ss);
                                    title.setSelected(isCheck(info));
                                    edTitle.setSelected(isCheck(info));
                                }
                            }
                            if (img != null) {
                                if (isEditable) {
                                    img.setVisibility(View.VISIBLE);
                                } else {
                                    img.setVisibility(View.GONE);
                                }
                            }
                            break;
                    }

                }
            } else if (info instanceof String) {
                if (title != null) {
                    title.setText((String) info);
                    if (isCheck(info)) {
                        title.setSelected(true);
                    } else {
                        title.setSelected(false);
                    }
                }
            } else if (info instanceof AccountInfo) {
                AccountInfo accountInfo = (AccountInfo) info;
                if (title != null) {
                    title.setText(TextUtils.isEmpty(accountInfo.getAccount()) ? "" : accountInfo.getAccount());
                }
                if (img != null) {
                    String headImgUrl = accountInfo.getHeadimgUrl();
                    if (!TextUtils.isEmpty(headImgUrl)) {
                        bitmapUtils.display(img, accountInfo.getHeadimgUrl());
                    } else {
                        img.setImageResource(R.mipmap.default_avatar);
                    }
                }
                if (desc != null) {
                    String descStr = accountInfo.getRemark();
                    if (TextUtils.isEmpty(descStr)) {
                        if (tvDesc != null) {
                            tvDesc.setVisibility(View.GONE);
                        }
                        desc.setVisibility(View.GONE);
                    } else {
                        if (tvDesc != null) {
                            tvDesc.setVisibility(View.VISIBLE);
                        }
                        desc.setVisibility(View.VISIBLE);
                        desc.setText(descStr);
                    }
                }
                if (imgEdit != null) {
                    imgEdit.setTag(accountInfo);
                    imgEdit.setOnClickListener(onClickListener);
                }
                if (imgDelete != null) {
                    imgDelete.setTag(accountInfo);
                    imgDelete.setOnClickListener(onClickListener);
                }
                if (gridView != null) {
                    List<String> permissionList = accountInfo.getRightList();
                    if (permissionList != null && !permissionList.isEmpty()) {
                        if (tvPermisssion != null) {
                            tvPermisssion.setVisibility(View.VISIBLE);
                        }
                        gridView.setVisibility(View.VISIBLE);
                        String[] from = {"competence"};
                        int[] to = {R.id.tv_competence};
                        SimpleAdapter simpleAdapter = new SimpleAdapter(context, dealListToMap(permissionList), R.layout.accout_competence_list, from, to);
                        gridView.setAdapter(simpleAdapter);
                        gridView.setFocusable(false);
                        gridView.setFocusableInTouchMode(false);
                    } else {
                        if (tvPermisssion != null) {
                            tvPermisssion.setVisibility(View.GONE);
                        }
                        gridView.setVisibility(View.GONE);
                    }
                }
            } else if (info instanceof CouponSendType) {
                if (title != null) {
                    title.setText(((CouponSendType) info).getTitle());
                    ViewGroup parent = (ViewGroup) title.getParent();
                    boolean isCheck = isCheck(info);
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        parent.getChildAt(i).setSelected(isCheck);
                    }
                }
            } else if (info instanceof Map) {
                switch (typeName) {
                    case TYPE_COUPON_GIVING_RECORD:
                        if (headers != null && headers.size() == 4) {
                            if (tv1 != null) {
                                tv1.setText(((Map<String, String>) info).get(headers.get(0).get("key")));
                            }
                            if (tv2 != null) {
                                tv2.setText(((Map<String, String>) info).get(headers.get(1).get("key")));
                            }
                            if (tv3 != null) {
                                tv3.setText(((Map<String, String>) info).get(headers.get(2).get("key")));
                            }
                            if (tv4 != null) {
                                tv4.setText(((Map<String, String>) info).get(headers.get(3).get("key")));
                            }
                        }
                        break;
                }
            }
        }

        private boolean isCheck(T info) {
            if (info instanceof HomePageInfo) {
                if (TextUtils.isEmpty(((HomePageInfo) info).getClassName())) {
                    return false;
                } else {
                    return ((HomePageInfo) info).getClassName().equals(checked);
                }
            } else if (info instanceof Product) {
                if (info.toString().equals(getChecked())) {
                    return true;
                } else {
                    return false;
                }
            } else if (info instanceof String) {
                if (info.equals(getChecked())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (info instanceof CouponSendType) {
                if (TextUtils.isEmpty(((CouponSendType) info).getTitle())) {
                    return false;
                } else {
                    return ((CouponSendType) info).getTitle().equals(checked);
                }
            }
            return false;
        }
    }

    public void selectItem(int position) {
        this.currentPosition = position;
        notifyDataSetChanged();
    }

    private List<Map<String, Object>> dealListToMap(List<String> list) {
        List<Map<String, Object>> listObject = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("competence", list.get(i));
            listObject.add(map);
        }
        return listObject;
    }
}
