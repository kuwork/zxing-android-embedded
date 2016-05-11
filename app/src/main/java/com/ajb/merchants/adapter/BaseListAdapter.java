package com.ajb.merchants.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.model.BaseModel;
import com.ajb.merchants.model.Coupon;
import com.ajb.merchants.model.HomePageInfo;
import com.ajb.merchants.model.Info;
import com.ajb.merchants.model.Product;
import com.ajb.merchants.model.filter.ConditionValue;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class BaseListAdapter<T> extends BaseAdapter {

    public final static String TYPE_CAR_NUM = "CAR_NUM";

    private final BitmapUtils bitmapUtils;
    private String typeName;
    private List<T> dataList;
    private Context context;
    private LayoutInflater mInflater;
    private View.OnClickListener onClickListener;
    private int res;
    private int currentPosition = -1;

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
        notifyDataSetChanged();
    }

    private String checked;

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

    class ViewHolder {
        @ViewInject(R.id.title)
        TextView title;
        @ViewInject(R.id.desc)
        TextView desc;
        @ViewInject(R.id.select_ib)
        ImageView img;
        @ViewInject(R.id.item_bg)
        RelativeLayout bg;
        @ViewInject(R.id.divider)
        View divider;


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
                        img.setImageResource(R.drawable.actionbar_done);
                        img.setVisibility(View.VISIBLE);
                    } else {
                        img.setVisibility(View.GONE);
                    }
                }
            } else if (info instanceof Coupon) {
                Coupon coupon = ((Coupon) info);
                if (title != null) {
                    title.setText(TextUtils.isEmpty(coupon.getName()) ? ""
                            : coupon.getName());
                }
                if (null != desc) {
                    String unit = coupon.getUnit();
                    desc.setText(TextUtils.isEmpty(coupon.getValue()) ? ""
                            : coupon.getValue());
                    desc.append((TextUtils.isEmpty(unit) ? "" : unit));
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
            } else if (info instanceof BalanceLimitInfo) {// R.layout.balance_limit_item
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
            } else if (info instanceof String) {
                if (title != null) {
                    title.setText((String) info);
                    if (isCheck(info)) {
                        title.setSelected(true);
                    } else {
                        title.setSelected(false);
                    }
                }
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
        return false;
    }

    public void selectItem(int position) {
        this.currentPosition = position;
        notifyDataSetChanged();
    }
}
