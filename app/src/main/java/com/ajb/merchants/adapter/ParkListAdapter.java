package com.ajb.merchants.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.CarPark;
import com.ajb.merchants.util.SharedFileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车位适配器
 *
 * @author chenming
 */
public class ParkListAdapter extends BaseAdapter {

    private Context context;
    private List<CarPark> list;
    private LayoutInflater inflater;
    private int selectItem = -1; // 当前选择的item position
    private Activity currentActivity;
    private SharedFileUtils sharedFileUtils;
    private View.OnClickListener onClickListener;

    public ParkListAdapter(Context context, List<CarPark> list) {
        super();
        if (list == null) {
            list = new ArrayList<CarPark>();
        } else {
            this.list = list;
        }
        this.context = context;
        currentActivity = (Activity) context;
        sharedFileUtils = new SharedFileUtils(context);
        inflater = LayoutInflater.from(context);
    }

    public ParkListAdapter(Context context, List<CarPark> list, View.OnClickListener onClickListener) {
        this(context, list);
        this.onClickListener = onClickListener;
    }

    // 更新列表数据
    public void update(List<CarPark> list) {
        if (list == null) {
            this.list = new ArrayList<CarPark>();
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

    public void append(List<CarPark> list2) {
        if (list2 == null) {
            return;
        }
        list.addAll(list2);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int lastPosition = position;
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.parklist_item, null);
            holder = new ViewHolder();
            holder.parking_item_line_number = (TextView) convertView
                    .findViewById(R.id.line_number);
            holder.parkName = (TextView) convertView
                    .findViewById(R.id.tv_parkname);
            holder.Discount = (TextView) convertView
                    .findViewById(R.id.tv_discount);
            holder.myDistance = (TextView) convertView
                    .findViewById(R.id.distance_tv);
            holder.support_online_pay_tv = (TextView) convertView
                    .findViewById(R.id.support_online_pay_tv);
            holder.discount_ly = (LinearLayout) convertView
                    .findViewById(R.id.discount_ly);
            holder.nearby_info_group = (LinearLayout) convertView
                    .findViewById(R.id.nearby_info_group);
            holder.navigation_btn = (LinearLayout) convertView
                    .findViewById(R.id.navigation_btn);
            holder.layout_online_pay = (LinearLayout) convertView
                    .findViewById(R.id.layout_online_pay);
            holder.support_online_pay_img = (ImageView) convertView
                    .findViewById(R.id.support_online_pay_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            CarPark carPark = list.get(position);
            holder.parking_item_line_number.setText((position + 1) + "");
            holder.parkName.setText(carPark.getParkName());
            if (TextUtils.isEmpty(carPark.getMyDistance())) {
                holder.myDistance.setText("未知");
            } else {
                try {
                    Double d = Double.parseDouble(carPark.getMyDistance());
                    if (d < 1000) {
                        holder.myDistance
                                .setText(carPark.getMyDistance() + "m");
                    } else {
                        holder.myDistance.setText(String.format("%.2f",
                                d / 1000) + "km");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.myDistance.setText(String.format("0km"));
                }
            }
            if ("2".equals(carPark.getMember())) {// 优惠
                holder.discount_ly.setVisibility(View.VISIBLE);
                holder.Discount.setText("(" + carPark.getDiscount() + "折)");
            } else if ("1".equals(carPark.getMember())) {// 会员
                holder.discount_ly.setVisibility(View.GONE);
            } else {
                holder.discount_ly.setVisibility(View.GONE);
            }
            Log.i("OnLinePay", "-OnLinePay->>" + carPark.getOnlinePay());
            // 判断carPark中info是否为空
            List<Map<String, String>> infoList = carPark.getInfo();
            if ("1".equals(carPark.getOnlinePay())) { // 支持在线支付
                holder.layout_online_pay.setVisibility(View.VISIBLE);
                holder.support_online_pay_img
                        .setImageResource(R.mipmap.support_online_pay);
                holder.support_online_pay_tv.setText("在线支付");
            } else { // 不支持在线支付
                holder.support_online_pay_img
                        .setImageResource(R.mipmap.unsupported_online_pay);
                if (infoList != null) {
                    boolean isShowOnLinePayLayout = false;
                    for (int i = 0; i < infoList.size(); i++) {
                        String keyStr = infoList.get(i).get("key");
                        if (keyStr.contains("支付")) {
                            String valueStr = infoList.get(i).get("value");
                            if (!TextUtils.isEmpty(valueStr)) {
                                holder.support_online_pay_tv.setText(valueStr);
                            }
                            isShowOnLinePayLayout = true;
                        }
                    }
                    if (isShowOnLinePayLayout) {
                        holder.layout_online_pay.setVisibility(View.VISIBLE);
                    } else {
                        holder.layout_online_pay.setVisibility(View.GONE);
                    }
                }
            }
            holder.nearby_info_group.removeAllViews();
            if (infoList != null) {
                View view;
                TextView carPrice;
                if (position == selectItem) {
                    for (int i = 0; i < infoList.size(); i++) {
                        view = inflater.inflate(
                                R.layout.popup_around_listview_style_small,
                                null);
                        carPrice = (TextView) view
                                .findViewById(R.id.pop_around_lv_number);
                        carPrice.setSingleLine(false);
                        holder.nearby_info_group.addView(initItem(view,
                                infoList.get(i)));
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        view = inflater.inflate(
                                R.layout.popup_around_listview_style_small,
                                null);
                        carPrice = (TextView) view
                                .findViewById(R.id.pop_around_lv_number);
                        carPrice.setSingleLine(true);
                        holder.nearby_info_group.addView(initItem(view,
                                infoList.get(i)));
                    }
                    // 只有超过三条记录才会显示点击加载更多
                    if (infoList.size() > 3) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("key", "点击查看更多");
                        map.put("value", "");
                        view = inflater.inflate(
                                R.layout.popup_around_listview_style_small,
                                null);
                        holder.nearby_info_group.addView(initItem(view, map));
                    }
                }

                // 匹配Value颜色
                /*
                 * int color =
				 * context.getResources().getColor(R.color.darkorange); for (int
				 * i = 0; i < infoList.size(); i++) { String key =
				 * infoList.get(i).get("key"); if (key.contains("车位")) { String
				 * value = infoList.get(i).get("value"); SpannableString
				 * spanString = new SpannableString(value + "  ");
				 * ForegroundColorSpan span = new ForegroundColorSpan( color);
				 * spanString.setSpan(span, 0, spanString.length(),
				 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); } else { continue; } }
				 */
            } else {
                // info为空
                holder.nearby_info_group.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.nearby_info_group.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                changeLayoutVisable(lastPosition);
            }
        });
        holder.navigation_btn.setTag(list.get(position));
        holder.navigation_btn.setOnClickListener(onClickListener);
        return convertView;
    }

    public static class ViewHolder {
        TextView Discount; // 车场名称
        TextView parkName; // 车场名称
        TextView myDistance; // 距离
        TextView parking_item_line_number;
        TextView support_online_pay_tv; // 在线支付textview
        LinearLayout discount_ly;
        LinearLayout nearby_info_group;
        LinearLayout navigation_btn; // 地图导航
        LinearLayout layout_online_pay; // 在线支付layout
        ImageView support_online_pay_img; // 是否支持在线支付图标
    }

    /**
     * 展开info全部信息
     *
     * @param position
     * @Title changeLayoutVisable
     * @Description
     * @author 李庆育
     * @date 2015-12-24 下午4:45:37
     */
    public void changeLayoutVisable(int position) {
        if (position != selectItem) {
            selectItem = position;
        } else {
            selectItem = -1;
        }
        notifyDataSetChanged();
    }

    private View initItem(View view, Map<String, String> map) {
        TextView item_name = (TextView) view
                .findViewById(R.id.pop_around_lv_text);
        TextView item_number = (TextView) view
                .findViewById(R.id.pop_around_lv_number);
        item_name.setText(map.get("key") + "");
        item_number.setText(map.get("value") + "");
        String keyColor = map.get("keyColor");
        String valueColor = map.get("valueColor");
        if (!TextUtils.isEmpty(keyColor)) {
            item_name.setTextColor(Color.parseColor(keyColor));
        } else {
            item_name.setTextColor(context.getResources().getColor(
                    R.color.item_tv_color_02));
        }

        if (!TextUtils.isEmpty(valueColor)) {
            item_number.setTextColor(Color.parseColor(valueColor));
        } else {
            item_number.setTextColor(context.getResources().getColor(
                    R.color.black));
        }
        return view;
    }

    /**
     * 启动GPS导航. 前置条件：导航引擎初始化成功
     */
    /*private void launchNavigator(double fromlatitude, double fromlongitude,
                                 String fromAddress, double topoilocationlatitude,
                                 double topoilocationlongitude, String topoiaddress) {
        BNaviPoint startPoint = new BNaviPoint(fromlongitude, fromlatitude,
                fromAddress, BNaviPoint.CoordinateType.BD09_MC);
        BNaviPoint endPoint = new BNaviPoint(topoilocationlongitude,
                topoilocationlatitude, topoiaddress,
                BNaviPoint.CoordinateType.BD09_MC);
        BaiduNaviManager.getInstance().launchNavigator(
                currentActivity,
                startPoint, // 起点（可指定坐标系）
                endPoint, // 终点（可指定坐标系）
                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
                true, // 真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY,
                new OnStartNavigationListener() { // 跳转监听
                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        Intent intent = new Intent(context,
                                BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        currentActivity.startActivity(intent);
                    }

                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }*/

}
