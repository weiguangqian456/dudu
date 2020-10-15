package com.weiwei.base.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.softphone.TutorialRegistrationCall;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:Android客户端
 * @Description: 通话通知服务
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-21
 */
public class VsCallNotificationService extends Service {
	/**
	 * 接收消息广播
	 */
	private CallNotificationBroadcast callNSB = null;

	/**
	 * 通话通知管理器
	 */
	private NotificationManager callNotificationManager = null;
	/**
	 * 通话通知
	 */
	private Notification callNotification = null;
	/**
	 * 通话通知视图
	 */
	private RemoteViews callContentView = null;

	private PendingIntent callPi = null;
	/**
	 * 拨打类型
	 */
	private int callType = 0;
	/**
	 * 拨打号码
	 */
	private String phoneNumber = null;
	/**
	 * 联系人名称
	 */
	private String mCalledName = null;
	/**
	 * 更新时间
	 */
	private boolean updateTime = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		callNSB = new CallNotificationBroadcast();
		// 注册广播
		IntentFilter filter = new IntentFilter();
//		filter.addAction(UIDfineAction.ACTION_DIAL_STATE);
//		filter.addAction(UIDfineAction.ACTION_CALL_TIME);
		filter.addAction(GlobalVariables.action_show_notification);
		filter.addAction(GlobalVariables.action_close_notification);
		filter.addAction(GlobalVariables.action_agin_notification);
		registerReceiver(callNSB, filter);
	}

	private class CallNotificationBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
//			if (UIDfineAction.ACTION_CALL_TIME.equals(intent.getAction())) {// 接收通话时间通知
//				String time = intent.getStringExtra("timer");
//				if (updateTime) {
//					updateCallNotification(time);
//				}
//			} else
				if (GlobalVariables.action_show_notification.equals(intent.getAction())) {// 显示通知栏
				callType = intent.getIntExtra("callType", 0);
				phoneNumber = intent.getStringExtra("phoneNumber");
				mCalledName = intent.getStringExtra("mCalledName");
				showCallNotification(callType, phoneNumber, mCalledName);
				updateTime = true;
			} else if (GlobalVariables.action_close_notification.equals(intent.getAction())) {// 电话挂断关闭通知栏
				updateTime = false;
				if (callNotificationManager != null) {
					callNotificationManager.cancel(1);
					callNotificationManager = null;
				}
			} else if (GlobalVariables.action_agin_notification.equals(intent.getAction())) {
				CustomLog.i("SoftCallActivity", "receiver------BroadCast");
				updateTime = false;
				if (callNotificationManager != null) {
					callNotificationManager.cancel(1);
					callNotificationManager = null;
				}
				Intent intent2 = new Intent(VsCallNotificationService.this, TutorialRegistrationCall.class);
				// intent2.putExtra("callType", callType);
				intent2.putExtra("called_num", GlobalVariables.saveCallName);
				intent2.putExtra("NT", true);
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				VsCallNotificationService.this.startActivity(intent2);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (callNSB != null) {
			unregisterReceiver(callNSB);
		}
		updateTime = false;
		GlobalVariables.isStartConnect = false;
		if (callNotificationManager != null) {
			callNotificationManager.cancel(1);
			callNotificationManager = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 通话中通知栏
	 */
	private void showCallNotification(int callType, String phoneNumber, String mCalledName) {
		CustomLog.i("vsdebug", "runing-call-notification---------");
		// 设置通知跳转界面
		Intent intent = new Intent(this, TutorialRegistrationCall.class);
		intent.putExtra("callType", callType);
		intent.putExtra("called_num", phoneNumber);
		intent.putExtra("NT", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		callPi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// 获取系统通知管理对象
		callNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 实例化通知对象
		callNotification = new Notification();
		// 设置通知图标
		callNotification.icon = R.drawable.icon_notification;
		callNotification.when = System.currentTimeMillis();
		callNotification.flags = Notification.FLAG_ONGOING_EVENT;
		callContentView = new RemoteViews(getPackageName(), R.layout.vs_notification_layout);
		callContentView.setTextViewText(R.id.ntTypeTV, "当前通话（00:00）");
		// initData(this, contentView, notification);
		String localName = VsLocalNameFinder.findLocalName(phoneNumber, false, this);
		if (localName != null && !"".equals(localName)) {
			callContentView.setTextViewText(R.id.noti_callName, mCalledName + "(" + localName + ")");
		} else {
			callContentView.setTextViewText(R.id.noti_callName, phoneNumber);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		callContentView.setTextViewText(R.id.noti_time, sdf.format(new Date()));
		callNotification.contentView = callContentView;
		callNotification.contentIntent = callPi;
		callNotificationManager.notify(1, callNotification);
	}

	/**
	 * 更新通知栏时间
	 * 
	 * @param timer
	 */
	private void updateCallNotification(String time) {
		if (callNotificationManager != null) {
			if (callNotification != null) {
				if (callContentView != null && callPi != null) {
					callContentView.setTextViewText(R.id.ntTypeTV, "当前通话（" + time + "）");
					callNotification.contentView = callContentView;
					callNotification.contentIntent = callPi;
					callNotificationManager.notify(1, callNotification);
				}
			}
		}
	}
}
