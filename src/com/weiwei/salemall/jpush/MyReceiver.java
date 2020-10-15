package com.weiwei.salemall.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.hwtx.dududh.R;
import com.weiwei.base.common.VsUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.base.dataprovider.DfineAction.MAINACTIVITY_CLASSNAME;
import static com.weiwei.base.dataprovider.DfineAction.RECHARGE_CLASSNAME;
import static com.weiwei.base.dataprovider.DfineAction.VIPACTIVITY_CLASSNAME;

/**
 * @author Created by EDZ on 2018/12/25.
 *         Describe  自定义接收器
 *         <p>
 *         如果不定义这个 Receiver，则：
 *         1) 默认用户会打开主界面
 *         2) 接收不到自定义消息
 */

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG";
    public static String regId;
    private String extra_json = "";
    private String androidClassName = "";
    private String productNo = "";
    private String skipUrl = "";
    private String storeNo = "";
    private String seckillValue = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                // 自定义消息不是通知，默认不会被SDK展示到通知栏上，极光推送仅负责透传给SDK。其内容和展示形式完全由开发者自己定义。
                // 自定义消息主要用于应用的内部业务逻辑和特殊展示需求
                extra_json = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extra_json)) Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息附加字段" + extra_json);
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(extra_json);
                androidClassName = jsonObject.getString("androidClassName");
                String params = jsonObject.getString("androidParams");

                com.alibaba.fastjson.JSONObject jsonObject1 = null;
                if (!StringUtils.isEmpty(params)) {
                    jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(params);
                }
                if (jsonObject1 != null) {
                    productNo = (String) jsonObject1.get("productNo");
                    skipUrl = (String) jsonObject1.get("skipUrl");
                    storeNo = (String) jsonObject1.get("storeNo");
                    seckillValue = (String) jsonObject1.get("seckillProductId");
                }
                if (!StringUtils.isEmpty(androidClassName)) {     //有参数
                    processCustomMessage(context, bundle, androidClassName, productNo, skipUrl, storeNo, seckillValue);
                } else {  //无参数
                    processCustomMessage(context, bundle, MAINACTIVITY_CLASSNAME, "", "", "", "");
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                receivingNotification(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) { //点击通知去到对应的界面
                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");
                extra_json = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (!TextUtils.isEmpty(extra_json)) Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义通知附加字段" + extra_json);
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(extra_json);
                androidClassName = jsonObject.getString("androidClassName");
                String params = jsonObject.getString("androidParams");

                com.alibaba.fastjson.JSONObject jsonObject1 = null;
                if (!StringUtils.isEmpty(params)) {
                    jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(params);
                }
                if (jsonObject1 != null) {
                    productNo = (String) jsonObject1.get("productNo");
                    skipUrl = (String) jsonObject1.get("skipUrl");
                    storeNo = (String) jsonObject1.get("storeNo");
                    seckillValue = (String) jsonObject1.get("seckillProductId");
                }
                if (!StringUtils.isEmpty(androidClassName)) {     //有参数
                    jumpActivity(context, bundle, androidClassName, productNo, skipUrl, storeNo, seckillValue);
                } else {  //无参数
                    jumpActivity(context, bundle, MAINACTIVITY_CLASSNAME, "", "", "", "");
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);

            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
        }
    }

    private void jumpActivity(Context context, Bundle bundle, String className, String productNo, String skipUrl, String storeNo, String seckillValue) {
        Intent intent = new Intent();
        intent.setClassName(context, className);
        if (className.equals(VIPACTIVITY_CLASSNAME) || className.equals(RECHARGE_CLASSNAME)) {    //VIP会员界面
            if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                context.startActivity(intent);
            }
            return;
        }
        if (!StringUtils.isEmpty(seckillValue)) {
            intent.putExtra("productNo", productNo);
            intent.putExtra("seckill", "seckill");
            intent.putExtra("seckillProductId", seckillValue);
        }
        if (!StringUtils.isEmpty(productNo)) {
            intent.putExtra("productNo", productNo);
        }
        if (!StringUtils.isEmpty(storeNo)) {
            intent.putExtra("storeNo", storeNo);
        }
        if (!StringUtils.isEmpty(skipUrl)) {
            intent.putExtra("skipUrl", skipUrl);
        }
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Logger.d(TAG, "通知title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Logger.d(TAG, "通知message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.d(TAG, "通知extras : " + extras);
    }

    // 打印所有的 intent extra 数据
    @NonNull
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    /**
     * send to activity
     *
     * @param context
     * @param bundle       参数
     * @param className    类名
     * @param productNo    商品编号
     * @param skipUrl      跳转链接
     * @param storeNo      店铺编号
     * @param seckillValue 秒杀商品编号
     */
    private void processCustomMessage(Context context, Bundle bundle, String className, String productNo, String skipUrl, String storeNo, String seckillValue) {
        String channelID = "1";
        String channelName = "channel_name";

        // 跳转的Activity
        Intent intent = new Intent();
        intent.setClassName(context, className);
        if (className.equals(VIPACTIVITY_CLASSNAME) || className.equals(RECHARGE_CLASSNAME)) {    //VIP会员界面
            if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                context.startActivity(intent);
            }
        }
        if (!StringUtils.isEmpty(seckillValue)) {
            intent.putExtra("productNo", productNo);
            intent.putExtra("seckill", "seckill");
            intent.putExtra("seckillProductId", seckillValue);
        }
        if (!StringUtils.isEmpty(productNo)) {
            intent.putExtra("productNo", productNo);
        }
        if (!StringUtils.isEmpty(storeNo)) {
            intent.putExtra("storeNo", storeNo);
        }
        if (!StringUtils.isEmpty(skipUrl)) {
            intent.putExtra("skipUrl", skipUrl);
        }
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (Math.random() * 100), intent, 0);

        // 获得系统推送的自定义消息
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //适配安卓8.0的消息渠道 (targetSdk >= 26)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelID);
        notification.setAutoCancel(true).setContentText(message).setContentTitle(title).setSmallIcon(R.drawable.icon).setDefaults(Notification.DEFAULT_ALL).setContentIntent
                (pendingIntent);
        notificationManager.notify((int) (System.currentTimeMillis() / 1000), notification.build());
    }
}

