package com.ajb.merchants.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.CardInfo;
import com.ajb.merchants.util.SharedFileUtils;
import com.ajb.merchants.view.SlideSwitch;

import java.util.ArrayList;
import java.util.List;

public class BindCarAdapter<T> extends BaseAdapter {

    private SharedFileUtils sharedFileUtils;
    private Context context;
    private List<T> list;
    public boolean isShowDeleteLayout;   //是否显示删除layout
    private int selectItem = -1; // 当前选择的item position
    public View.OnClickListener onClickListener;
    public View.OnClickListener onCheckedClickListener;
    public String carOpenTip = "自由进出已开启";  //自由进出已开启
    public String carCloseTip = "自由进出已关闭"; //自由进出已关闭

    public BindCarAdapter(Context context, List<T> list, View.OnClickListener onClickListener, View.OnClickListener onCheckedClickListener, String carOpenTip, String carCloseTip) {
        super();
        sharedFileUtils = new SharedFileUtils(context);
        this.context = context;
        this.onClickListener = onClickListener;
        this.onCheckedClickListener = onCheckedClickListener;
        if (list == null) {
            this.list = new ArrayList<T>();
        } else {
            this.list = list;
        }
        if (!TextUtils.isEmpty(carOpenTip)) {
            this.carOpenTip = carOpenTip;
        }
        if (!TextUtils.isEmpty(carCloseTip)) {
            this.carCloseTip = carCloseTip;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View v, ViewGroup arg2) {
        ViewHolder holder = null;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.bing_item, null);
            holder = new ViewHolder<T>();
            holder.carNo = (TextView) v.findViewById(R.id.tv_carnum);
            holder.btn_cancle = (LinearLayout) v.findViewById(R.id.btn_carcancle_ll);
            holder.carnum_state_switch = (SlideSwitch) v.findViewById(R.id.carnum_state_switch);
            holder.btn_carcancle_ll = (LinearLayout) v.findViewById(R.id.btn_carcancle_ll);
            holder.tv_month_service_state = (TextView) v.findViewById(R.id.tv_month_service_state);
            holder.tv_car_state = (TextView) v.findViewById(R.id.tv_car_state);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.init(context, list.get(position), position);
        return v;
    }

    public class ViewHolder<T> {
        private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
        TextView carNo;
        LinearLayout btn_cancle;
        SlideSwitch carnum_state_switch;
        LinearLayout btn_carcancle_ll;
        TextView tv_month_service_state;
        TextView tv_car_state;

        public ViewHolder() {
            onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (tv_month_service_state == null) {
                        return;
                    }
                    if (isChecked) {
                        tv_month_service_state.setText(carOpenTip);
                    } else {
                        tv_month_service_state.setText(carCloseTip);
                    }
                }
            };
        }

        public void init(Context context, T info, int position) {
            if (info == null) {
                return;
            }
            if (info instanceof String) {
                carNo.setText((String) info);
                btn_cancle.setTag(info);
                btn_carcancle_ll.setTag(info);
                btn_carcancle_ll.setOnClickListener(BindCarAdapter.this.onClickListener);
                carnum_state_switch.setVisibility(View.GONE);
                tv_month_service_state.setVisibility(View.GONE);
                tv_car_state.setVisibility(View.VISIBLE);
                if (isShowDeleteLayout) {
                    btn_carcancle_ll.setVisibility(View.VISIBLE);
                } else {
                    btn_carcancle_ll.setVisibility(View.GONE);
                }
            } else if (info instanceof CardInfo) {
                carNo.setText(((CardInfo) info).getCarNo());
                btn_cancle.setTag(info);
                btn_carcancle_ll.setTag(info);
                carnum_state_switch.setTag(info);
                carnum_state_switch.setOnClickListener(onCheckedClickListener);
                carnum_state_switch.setOnCheckedChangeListener(onCheckedChangeListener);
                btn_carcancle_ll.setOnClickListener(onClickListener);
                tv_month_service_state.setVisibility(View.VISIBLE);
                tv_car_state.setVisibility(View.GONE);
                if (isShowDeleteLayout) {
                    btn_carcancle_ll.setVisibility(View.VISIBLE);
                    carnum_state_switch.setVisibility(View.GONE);
                } else {
                    btn_carcancle_ll.setVisibility(View.GONE);
                    carnum_state_switch.setVisibility(View.VISIBLE);
                }
                if (((CardInfo) info).getEnable() == 1) {
                    tv_month_service_state.setText(carOpenTip);
                    carnum_state_switch.setChecked(true);
                    carnum_state_switch.invalidate();
                } else {
                    tv_month_service_state.setText(carCloseTip);
                    carnum_state_switch.setChecked(false);
                    carnum_state_switch.invalidate();
                }
            }
        }
    }

    /**
     * 展开删除按钮
     *
     * @param isShowDeleteLayout
     */
    public void showDeleteLayoutVisable(boolean isShowDeleteLayout) {
        this.isShowDeleteLayout = isShowDeleteLayout;
        notifyDataSetChanged();
    }


    public void remove(T info) {
        if (info == null) {
            return;
        }
        list.remove(info);
        notifyDataSetChanged();
    }

}
