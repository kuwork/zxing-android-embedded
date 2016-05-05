package com.ajb.merchants.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ajb.merchants.activity.HomePageActivity;
import com.ajb.merchants.activity.LoginActivity;
import com.ajb.merchants.activity.WebViewActivity;
import com.ajb.merchants.model.JPushExtra;
import com.ajb.merchants.util.SharedFileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JpushCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
                + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle
                    .getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            // send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            Log.d(TAG,
                    "[MyReceiver] 接收到推送下来的自定义消息: "
                            + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle
                    .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            String jsonStrExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Gson gson = new Gson();
            Intent i = new Intent();
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                JPushExtra extra = gson.fromJson(jsonStrExtra,
                        new TypeToken<JPushExtra>() {
                        }.getType());
                // 读取SharedPreferences中需要的数据
                // 使用SharedPreferences来记录程序的使用次数
                SharedFileUtils sharedFileUtils = new SharedFileUtils(context);
                boolean isLogin = sharedFileUtils.getBoolean("isLogin");
                // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
                if ("2".equals(extra.actionType)) {// 跳转网页
                    if (!TextUtils.isEmpty(extra.urlStr)) {
                        i.setClass(context, WebViewActivity.class);
                        i.putExtra("URL", extra.urlStr);
                        i.putExtra("TITLE", extra.title);
                    }
                } else {
                    if (!isLogin) {
                        i.setClass(context, LoginActivity.class);
                    } else {
                        if ("1".equals(extra.actionType)) {// 跳转消息中心
                            i.setClass(context, HomePageActivity.class);
                        } else if ("3".equals(extra.actionType)) {// 跳转个人账户
                            i.setClass(context, HomePageActivity.class);
                            i.putExtra("title", "分享记录");
                        } else if ("4".equals(extra.actionType)) {// 升级
                            i.setClass(context, HomePageActivity.class);
                        } else if ("5".equals(extra.actionType)) {// 预约记录
                            i.setClass(context, HomePageActivity.class);
                            i.putExtra("inputType", "2");
                        } else if ("6".equals(extra.actionType)) {
                            i.setClass(context, HomePageActivity.class);
                            i.putExtra("inputType", "2");
                        } else {
                            i.setClass(context, HomePageActivity.class);
                        }
                    }
                }
                context.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // //打开自定义的Activity
            // Intent i = new Intent(context, TestActivity.class);
            // i.putExtras(bundle);
            // //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            // Intent.FLAG_ACTIVITY_CLEAR_TOP );
            // context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
                .getAction())) {
            Log.d(TAG,
                    "[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
                            + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
                .getAction())) {
            boolean connected = intent.getBooleanExtra(
                    JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction()
                    + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        // if (MainActivity.isForeground) {
        // String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        // String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        // Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
        // msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        // if (!ExampleUtil.isEmpty(extras)) {
        // try {
        // JSONObject extraJson = new JSONObject(extras);
        // if (null != extraJson && extraJson.length() > 0) {
        // msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
        // }
        // } catch (JSONException e) {
        //
        // }
        //
        // }
        // context.sendBroadcast(msgIntent);
        // }
    }
}
