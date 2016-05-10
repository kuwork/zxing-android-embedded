package com.ajb.merchants.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.LvMenuItem;
import com.ajb.merchants.model.MenuInfo;
import com.ajb.merchants.model.ModularMenu;
import com.ajb.merchants.util.CarLocation;
import com.lidroid.xutils.BitmapUtils;
import com.util.DensityUtil;
import com.util.PathManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter<T> extends BaseAdapter {

    private final int mIconSize;
    private final int mDotsSize;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<T> mItems;
    private BitmapUtils bitmapUtils;
    private List<String> dotsList;  //红点list，填写MenuInfo中的MenuCode
    private String modularCode; //模块编号

    public MenuItemAdapter(Context context, List<T> list, String modularCode) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.drawer_icon_size);//24dp
        mDotsSize = DensityUtil.dp2px(mContext, 8);
        File file = new File(PathManager.getDiskFileDir(context) + CarLocation.ZTB_CACHE_HOME_SLIDE_ICON);
        if (!file.exists()) {
            file.mkdirs();
        }
        bitmapUtils = new BitmapUtils(context, file.getAbsolutePath());
        if (list == null) {
            mItems = new ArrayList<T>();
        } else {
            mItems = list;
        }
        this.modularCode = modularCode;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public int getItemViewType(int position) {
        T t = mItems.get(position);
        if (t instanceof LvMenuItem) {
            return ((LvMenuItem) t).type;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T t = mItems.get(position);
        MenuInfo item = (MenuInfo) t;
        if (ModularMenu.CODE_LEFTMENU.equals(modularCode)) {    //侧滑菜单
            switch (item.getType()) {
                case MenuInfo.TYPE_NORMAL:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_custom, parent, false);
                    }
                    TextView itemView = (TextView) convertView.findViewById(R.id.drawer_custom_tv);
                    TextView itemDesc = (TextView) convertView.findViewById(R.id.drawer_custom_desc_tv);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.drawer_custom_img);
                    if (itemView != null) {
                        itemView.setText(item.getTitle());
                    }
                    if (imageView != null) {
                        if (!TextUtils.isEmpty(item.getMenuPictureUrl())) {
                            bitmapUtils.display(imageView, item.getMenuPictureUrl());
                        } else {
                            imageView.setImageBitmap(null);
                        }
                    }
                    if (itemDesc != null) {
                        itemDesc.setText(item.getDesc());
                        if (isDots(item.getMenuCode())) {
                            Drawable icon = mContext.getResources().getDrawable(R.drawable.dots_red);
                            if (icon != null) {
                                icon.setBounds(0, 0, mDotsSize, mDotsSize);
                                TextViewCompat.setCompoundDrawablesRelative(itemDesc, null, null, icon, null);
                            }
                        } else {
                            TextViewCompat.setCompoundDrawablesRelative(itemDesc, null, null, null, null);
                        }
                    }
                    break;

                case MenuInfo.TYPE_SEPARATOR:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                                parent, false);
                    }
                    break;
            }
        } else if (ModularMenu.CODE_SETTING.equals(modularCode) || ModularMenu.CODE_ACCOUNT.equals(modularCode)) {  //设置界面
            switch (item.getType()) {
                case MenuInfo.TYPE_NORMAL:
                    if (convertView == null) {
                        if (ModularMenu.CODE_ACCOUNT.equals(modularCode)) {
                            convertView = mInflater.inflate(R.layout.account_item_custom, parent, false);
                        } else {
                            convertView = mInflater.inflate(R.layout.setting_item_custom, parent, false);
                        }
                    }
                    TextView itemName = (TextView) convertView.findViewById(R.id.setting_item_name_tv);
                    TextView itemDesc = (TextView) convertView.findViewById(R.id.setting_item_desc_tv);
                    ImageView imgDots = (ImageView) convertView.findViewById(R.id.setting_item_dots_img);
                    ImageView imgItem = (ImageView) convertView.findViewById(R.id.drawer_custom_img);
                    ImageView imgArrow = (ImageView) convertView.findViewById(R.id.setting_arrow);
                    if (itemName != null) {
                        itemName.setText(item.getTitle());
                        if (MenuInfo.TO_LOGIN_OUT.equals(item.getMenuCode())) {
                            itemName.setGravity(Gravity.CENTER);
                            itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_18));
                        } else {
                            itemName.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_16));
                        }
                    }
                    if (itemDesc != null) {
                        if (TextUtils.isEmpty(item.getDesc())) {
                            itemDesc.setVisibility(View.GONE);
                        } else {
                            itemDesc.setText(item.getDesc());
                            itemDesc.setVisibility(View.VISIBLE);
                        }
                    }
                    if (imgDots != null) {
                        if (isDots(item.getMenuCode())) {
                            imgDots.setVisibility(View.VISIBLE);
                        } else {
                            imgDots.setVisibility(View.GONE);
                        }
                    }
                    if (imgItem != null) {
                        if (!TextUtils.isEmpty(item.getMenuPictureUrl())) {
                            imgItem.setVisibility(View.VISIBLE);
                            bitmapUtils.display(imgItem, item.getMenuPictureUrl());
                        } else {
                            imgItem.setVisibility(View.GONE);
                            imgItem.setImageBitmap(null);
                        }
                    }
                    if (imgArrow != null) {
                        if (MenuInfo.TO_LOGIN_OUT.equals(item.getMenuCode())) {
                            imgArrow.setVisibility(View.GONE);
                        } else {
                            imgArrow.setVisibility(View.VISIBLE);
                        }
                    }

                    break;

                case MenuInfo.TYPE_SEPARATOR:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                                parent, false);
                        View view = convertView.findViewById(R.id.separetor);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.setMargins(DensityUtil.dp2px(mContext, 10), 0, DensityUtil.dp2px(mContext, 10), 0);
                        view.setLayoutParams(params);
                    }
                    break;

                case MenuInfo.TYPE_DIVIDE://大分割线
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.setting_item_divide,
                                parent, false);
                    }
                    if (position == 0) {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.GONE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.VISIBLE);
                    } else if (position == getCount() - 1) {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.GONE);
                    } else {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.VISIBLE);
                    }
                    break;
            }
        } else if (ModularMenu.CODE_MENU.equals(modularCode)) {//菜单
            switch (item.getType()) {
                case MenuInfo.TYPE_NORMAL:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.menu_list_item, parent, false);
                    }
                    TextView itemName = (TextView) convertView.findViewById(R.id.setting_item_name_tv);
                    TextView itemDesc = (TextView) convertView.findViewById(R.id.setting_item_desc_tv);
                    ImageView dotsImg = (ImageView) convertView.findViewById(R.id.setting_item_dots_img);
                    if (itemName != null) {
                        itemName.setText(item.getTitle());
                    }
                    if (itemDesc != null) {
                        if (TextUtils.isEmpty(item.getDesc())) {
                            itemDesc.setVisibility(View.GONE);
                        } else {
                            itemDesc.setText(item.getDesc());
                            itemDesc.setVisibility(View.VISIBLE);
                        }
                    }
                    if (dotsImg != null) {
                        if (isDots(item.getMenuCode())) {
                            dotsImg.setVisibility(View.VISIBLE);
                        } else {
                            dotsImg.setVisibility(View.GONE);
                        }
                    }

                    break;

                case MenuInfo.TYPE_SEPARATOR:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                                parent, false);
                        View view = convertView.findViewById(R.id.separetor);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.setMargins(DensityUtil.dp2px(mContext, 10), 0, DensityUtil.dp2px(mContext, 10), 0);
                        view.setLayoutParams(params);
                    }
                    break;

                case MenuInfo.TYPE_DIVIDE://大分割线
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.setting_item_divide,
                                parent, false);
                    }
                    if (position == 0) {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.GONE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.VISIBLE);
                    } else if (position == getCount() - 1) {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.GONE);
                    } else {
                        convertView.findViewById(R.id.separetor1).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.separetor2).setVisibility(View.VISIBLE);
                    }
                    break;
            }
        } else if (ModularMenu.CODE_COUPON.equals(modularCode)) {//优惠方式菜单
            switch (item.getType()) {
                case MenuInfo.TYPE_NORMAL:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.grid_menu_item, parent, false);
                    }
                    TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                    ImageView imgMenu = (ImageView) convertView.findViewById(R.id.imgMenu);
                    if (tvTitle != null) {
                        tvTitle.setText(item.getTitle());
                    }
                    if (imgMenu != null) {
                        if (!TextUtils.isEmpty(item.getMenuPictureUrl())) {
                            bitmapUtils.display(imgMenu, item.getMenuPictureUrl());
                        } else {
                            imgMenu.setImageBitmap(null);
                        }
                    }
                    break;
                case MenuInfo.TYPE_SEPARATOR:
                    break;

                case MenuInfo.TYPE_DIVIDE://大分割线
                    break;
            }
        } else if (ModularMenu.CODE_MAIN_MENU.equals(modularCode)) {//主页功能菜单
            switch (item.getType()) {
                case MenuInfo.TYPE_NORMAL:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.grid_menu_item2, parent, false);
                    }
                    TextView tvMenu = (TextView) convertView.findViewById(R.id.tvTitle);
                    TextView tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
                    ImageView imgMenu = (ImageView) convertView.findViewById(R.id.imgMenu);
                    if (tvMenu != null) {
                        tvMenu.setText(item.getTitle());
                    }
                    if (tvDesc != null) {
                        tvDesc.setText(item.getDesc());
                    }
                    if (imgMenu != null) {
                        if (!TextUtils.isEmpty(item.getMenuPictureUrl())) {
                            bitmapUtils.display(imgMenu, item.getMenuPictureUrl());
                        } else {
                            imgMenu.setImageBitmap(null);
                        }
                    }
                    break;
                case MenuInfo.TYPE_SEPARATOR:
                    break;

                case MenuInfo.TYPE_DIVIDE://大分割线
                    break;
            }
        }
        return convertView;
    }

    public void setIconColor(Drawable icon) {
        int textColorSecondary = android.R.attr.textColorSecondary;
        TypedValue value = new TypedValue();
        if (!mContext.getTheme().resolveAttribute(textColorSecondary, value, true)) {
            return;
        }
        int baseColor = mContext.getResources().getColor(value.resourceId);
        icon.setColorFilter(baseColor, PorterDuff.Mode.MULTIPLY);
    }

    private boolean isDots(String menuCode) {
        if (dotsList == null) {
            return false;
        }
        return dotsList.contains(menuCode);
    }

    public void addDots(String menuCode) {
        if (dotsList == null) {
            dotsList = new ArrayList<>();
        }
        dotsList.add(menuCode);
        notifyDataSetChanged();
    }

    public void removeDots(String menuCode) {
        if (dotsList == null) {
            return;
        }
        dotsList.remove(menuCode);
        notifyDataSetChanged();
    }

    public void removeAllDots() {
        if (dotsList == null) {
            return;
        }
        dotsList.clear();
        notifyDataSetChanged();
    }


    public void update(List<T> leftMenus) {
        if (leftMenus == null) {
            mItems = new ArrayList<T>();
        } else {
            mItems = leftMenus;
        }
        notifyDataSetChanged();
    }

    public void update(List<T> leftMenus, String modularCode) {
        if (leftMenus == null) {
            mItems = new ArrayList<T>();
        } else {
            mItems = leftMenus;
        }
        this.modularCode = modularCode;
        notifyDataSetChanged();
    }


    public void setMenuDesc(String menuCode, String msg) {
        if (TextUtils.isEmpty(menuCode)) {
            return;
        }
        T menu;
        for (int i = 0; i < getCount(); i++) {
            menu = (T) getItem(i);
            if (menu instanceof MenuInfo) {
                if (menuCode.equals(((MenuInfo) menu).getMenuCode())) {
                    ((MenuInfo) menu).setDesc(msg);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }
}