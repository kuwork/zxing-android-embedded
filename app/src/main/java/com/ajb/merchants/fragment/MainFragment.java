package com.ajb.merchants.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ajb.merchants.R;
import com.ajb.merchants.adapter.BaseListAdapter;
import com.ajb.merchants.model.BalanceLimitInfo;
import com.ajb.merchants.task.BlurBitmapTask;
import com.ajb.merchants.util.FastBlur;
import com.ajb.merchants.view.MyGridView;
import com.ajb.merchants.view.MySwipeRefreshLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

public class MainFragment extends BaseFragment {

    @ViewInject(R.id.swipeLayout)
    MySwipeRefreshLayout swipeLayout;
    @ViewInject(R.id.balanceGridView)
    MyGridView balanceGridView;
    @ViewInject(R.id.imgHeaderBanner)
    ImageView imgHeaderBanner;
    private boolean isFirst = true;

    BlurBitmapTask blurBitmapTask = new BlurBitmapTask(getActivity()) {
        @Override
        protected void onPostExecute(List<Bitmap> list) {
            super.onPostExecute(list);
            if (list.size() > 0) {
                imgHeaderBanner.setImageBitmap(list.get(0));
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d("hidden=" + hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ViewUtils.inject(this, v);
        swipeLayout.setCanRefresh(true);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.holo_blue_bright),
                getResources().getColor(R.color.holo_green_light),
                getResources().getColor(R.color.holo_orange_light),
                getResources().getColor(R.color.holo_red_light));

        List<BalanceLimitInfo> balanceLimitInfoList = Arrays.asList(
                new BalanceLimitInfo("", "元", "可赠金额"),
                new BalanceLimitInfo("0.0", "", "可赠时间"),
                new BalanceLimitInfo("0.0", "元", "当月已赠金额"),
                new BalanceLimitInfo("0.0", "小时", "当月已赠时间")
        );
        BaseListAdapter<BalanceLimitInfo> balanceLimitInfoAdapter = new BaseListAdapter<BalanceLimitInfo>(getActivity(), balanceLimitInfoList, R.layout.balance_limit_item, null);
        balanceGridView.setAdapter(balanceLimitInfoAdapter);
//        Bitmap blur = blur(BitmapFactory.decodeResource(getResources(), R.mipmap.header_bg), 15);
        ViewTreeObserver viewTreeObserver = imgHeaderBanner.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isFirst) {
                    blurBitmapTask.execute(BitmapFactory.decodeResource(getResources(), R.mipmap.pc01));
//                    int width = imgHeaderBanner.getMeasuredWidth();
//                    int height = imgHeaderBanner.getMeasuredHeight();
//                    blur(BitmapUtil.resizedBitmap(getActivity(), R.mipmap.header_bg, width, height), imgHeaderBanner, width, height);
                    isFirst = false;
                }
                return true;
            }
        });
//        imgHeaderBanner.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pc01));

        return v;
    }

    private void blur(Bitmap bkg, View view, int width, int height) {
        long startMs = System.currentTimeMillis();
        float radius = 15;
        float scaleFactor = 8;

        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(getResources(), overlay));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        }
        LogUtils.d("cost " + (System.currentTimeMillis() - startMs) + "ms");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("onPause");
    }

}
