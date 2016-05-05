package com.ajb.merchants.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.ParkingInfo;

import java.util.ArrayList;
import java.util.List;

public class ParkingListAdtpter extends BaseAdapter {
    private Context context;
    // private List<Map<String, Object>> mData;
    private LayoutInflater mInflater;
    private List<ParkingInfo> list;// 车场列表
    private boolean isDeleteModer = false;
    private boolean isCheckAll = false;
    private boolean isSet = true;
    private boolean isShow = true;

    public ParkingListAdtpter(Context context, List<ParkingInfo> parkingInfoList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = parkingInfoList;

    }


    /**
     * 是否编辑模式
     */
    public boolean isDeleteModer() {
        return isDeleteModer;
    }

    public void setDeleteModer(boolean isDeleteModer) {
        this.isDeleteModer = isDeleteModer;
        if (!isDeleteModer) {
            setCheckAll(false);
        }
    }


    // 更新列表数据
    public void update(List<ParkingInfo> list) {
        if (list == null) {
            this.list = new ArrayList<ParkingInfo>();
        } else {
            this.list = list;
        }
        this.notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (list != null && list.size() > 0) {
            list.clear();
        }
        notifyDataSetChanged();
    }

    public void append(List<ParkingInfo> list2) {
        if (list2 == null) {
            return;
        }
        list.addAll(list2);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (arg1 == null) {
            holder = new ViewHolder();

            arg1 = mInflater.inflate(R.layout.parkinglist_item, null);
            holder.lineNumber = (TextView) arg1.findViewById(R.id.line_number);
            holder.parkName_tv = (TextView) arg1.findViewById(R.id.parkName_tv);
            holder.carno_tv = (TextView) arg1.findViewById(R.id.carno_tv);
            holder.intime_tv = (TextView) arg1.findViewById(R.id.intime_tv);
            holder.outtime_tv = (TextView) arg1.findViewById(R.id.outtime_tv);
            holder.money_tv = (TextView) arg1.findViewById(R.id.money_tv);
            holder.stytle_tv = (TextView) arg1.findViewById(R.id.style_tv);
            holder.checkbox = (CheckBox) arg1.findViewById(R.id.check_box);

            holder.seting = (TextView) arg1.findViewById(R.id.setting);

            holder.parking_item_null_parking = (LinearLayout) arg1.findViewById(R.id.parking_item_null_parking);
            holder.parking_item_null_parking2 = (LinearLayout) arg1.findViewById(R.id.parking_item_null_parking2);
            holder.parking_item_null_parking3 = (LinearLayout) arg1.findViewById(R.id.parking_item_null_parking3);

            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        if (null != list.get(position)) {

            holder.lineNumber.setText(position + 1 + "");
            if ("".equals(list.get(position).getParkName())
                    || null == list.get(position).getParkName()) {
                holder.parkName_tv.setText("未知");
            } else {
                holder.parkName_tv.setText(list.get(position).getParkName());
            }

            if (isDeleteModer()) {
                holder.checkbox.setVisibility(View.VISIBLE);
            } else {
                holder.checkbox.setVisibility(View.GONE);
            }
            ParkingInfo parkingInfo = list.get(position);

            if (isSet) {
                holder.seting.setText("设置");

            } else {
                holder.seting.setText("显示");
            }

            holder.carno_tv.setText(parkingInfo.getCarNo());
            holder.intime_tv.setText(parkingInfo.getInTime());
            holder.outtime_tv.setText(parkingInfo.getOutTime());
            holder.money_tv.setText("￥" + parkingInfo.getEditFee());
            holder.checkbox.setTag(parkingInfo);
            holder.checkbox.setChecked(parkingInfo.isCheck());
            holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean check) {
                    // TODO Auto-generated method stub

                    ParkingInfo parkingInfo = (ParkingInfo) arg0.getTag();
                    parkingInfo.setCheck(check);

                }
            });
            if (parkingInfo.getStatus() == 1) {// 进场或不在场

                if (parkingInfo.getFeeseason().equals("")) {// 进场方式

                    holder.stytle_tv.setText(parkingInfo.getCardType());
                } else {
                    holder.stytle_tv.setText(parkingInfo.getFeeseason());

                }

                holder.parking_item_null_parking3.setVisibility(View.VISIBLE);
                holder.parking_item_null_parking.setVisibility(View.VISIBLE);
                holder.parking_item_null_parking2.setVisibility(View.GONE);

//                if (!isDeleteModer() && isShow) {
//
//
////					for (int i = 0; i <= list.size(); i++) {
//                    if (list.get(position).getStatus() == 1 && position == 0) {
//                        holder.seting.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.seting.setVisibility(View.GONE);
//                    }
////					}
//
//                } else {
//                    holder.seting.setVisibility(View.GONE);
//                }

            } else if (parkingInfo.getStatus() == 2) {// 离场
                holder.seting.setVisibility(View.GONE);
                if (parkingInfo.getFeeseason().equals("")) {

                    holder.stytle_tv.setText("" + parkingInfo.getCardType());// 离场方式:
                } else {
                    holder.stytle_tv.setText("" + parkingInfo.getFeeseason());// 进场方式:

                }
                holder.parking_item_null_parking3.setVisibility(View.VISIBLE);
                holder.parking_item_null_parking.setVisibility(View.GONE);
                holder.parking_item_null_parking2.setVisibility(View.VISIBLE);
            } else {
                holder.parking_item_null_parking3.setVisibility(View.GONE);
                holder.parking_item_null_parking.setVisibility(View.GONE);
                holder.parking_item_null_parking2.setVisibility(View.GONE);
            }

            holder.seting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
//                    deleteRecordClickListener.onDeleteClick();

                }
            });
        }
        return arg1;
    }

    /**
     * @return
     * @Title getAllDeleteItem
     * @Description 获取要删除的条目
     * @author 陈龙杰
     * @date 2015年7月22日 上午10:19:24
     */
    public List<ParkingInfo> getAllDeleteItem() {
        List<ParkingInfo> list = new ArrayList<ParkingInfo>();
        for (int i = 0; i < this.list.size(); i++) {
            if (this.list.get(i).isCheck()) {
                list.add(this.list.get(i));
            }

        }
        return list;
    }

    /**
     * @Title removeAllDeleteItem
     * @Description
     * @author 陈龙杰
     * @date 2015年7月22日 上午10:20:11
     */
    public void removeAllDeleteItem() {
        list.removeAll(getAllDeleteItem());
        notifyDataSetChanged();
    }

    class ViewHolder {
        public LinearLayout parking_item_null_parking2;
        public LinearLayout parking_item_null_parking;
        public LinearLayout parking_item_null_parking3;
        TextView lineNumber;
        TextView parkName_tv;
        TextView carno_tv;
        TextView intime_tv;
        TextView stytle_tv;
        TextView money_tv;
        TextView outtime_tv;
        CheckBox checkbox;
        TextView seting;
    }

    public void setingText(boolean set) {
        isSet = set;
        notifyDataSetChanged();
    }

    public void setShow(boolean show) {
        isShow = show;
        notifyDataSetChanged();
    }

    /**
     * @param isCheckAll
     * @Title setCheckAll
     * @Description 是否全选删除
     * @author 陈龙杰
     * @date 2015年7月22日 下午2:12:34
     */
    public void setCheckAll(boolean isCheckAll) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setCheck(isCheckAll);
        }
        notifyDataSetChanged();
    }

}
