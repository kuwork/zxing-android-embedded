package com.ajb.merchants.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.ajb.merchants.model.AdInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Splash界面ViewPager的适配器
 * Created by Yuven-Lee on 2015/10/27.
 */
public class BannerAdapter extends PagerAdapter {

    private List<AdInfo> dataList;
    private LinkedList<View> cacheViewList;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    public BannerAdapter(List<AdInfo> dataList, Context context, View.OnClickListener onClickListener) {
        super();
        if (dataList == null) {
            this.dataList = new ArrayList<AdInfo>();
        } else {
            this.dataList = dataList;
            this.onClickListener = onClickListener;
        }
        this.context = context;
        cacheViewList = new LinkedList<View>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (dataList.isEmpty()) {
            return 0;
        } else if (dataList.size() == 1) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        ViewHolder viewHolder = new ViewHolder();
        if (cacheViewList.isEmpty()) {
            convertView = inflater.inflate(R.layout.activity_splash_style, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.splash_item_img);
            convertView.setOnClickListener(onClickListener);
            convertView.setTag(viewHolder);
        } else {
            convertView = cacheViewList.removeFirst();
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AdInfo adInfo = dataList.get(position % dataList.size());
        viewHolder.adInfo = adInfo;

        if (adInfo.getType() == 0 && !TextUtils.isEmpty(adInfo.getUrl())) {
            Bitmap bitmap = BitmapFactory.decodeFile(adInfo.getUrl());
            viewHolder.image.setImageBitmap(bitmap);
        } else if (adInfo.getType() == 1) {
            viewHolder.image.setImageResource(adInfo.getRes());
        }
        container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        container.removeView(contentView);
        cacheViewList.add(contentView);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public class ViewHolder {
        public AdInfo adInfo;
        public ImageView image;
    }

}
