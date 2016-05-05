package com.ajb.merchants.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ajb.merchants.activity.PopupWindowBannerActivity;

public class BannerBroadCast extends BroadcastReceiver {
    public BannerBroadCast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent bannerIntent = new Intent(context, PopupWindowBannerActivity.class);
        bannerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bannerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(bannerIntent);
    }
}
