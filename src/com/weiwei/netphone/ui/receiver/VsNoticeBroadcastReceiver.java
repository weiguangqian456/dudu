package com.weiwei.netphone.ui.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.db.provider.VsNotice;
import com.weiwei.base.item.VsNoticeItem;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.base.widgets.VsDlogMakeMoney;
import com.hwtx.dududh.R;

public class VsNoticeBroadcastReceiver extends BroadcastReceiver {
	// private String displaytype = "";// 传递过来的消息类型
	// private String messageid = ""; // 消息id
	// private String messagebody = ""; // 消息类容
	// private String messagetype = "";// 消息状态
	// private String messagetitle = "";// 消息标题
	// private String messagelink = "";// 跳转链接
	// private String messagebuttontext = "";// 按钮标题
	// private String messagelinktype = "";// 跳转类别
	// private String push_id = "";// 略读反馈的id
	// 通知管理
	private NotificationManager m_NotificationManager;
	private Notification m_Notification;
	private PendingIntent m_PendingIntent;
	private String TAG = "ConnectionService";

	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void onReceive(Context context, Intent intent) {
		CustomLog.i(TAG, "NotificationManager onReceive");
		Bundle bundle = intent.getExtras();
		String displaytype = VsUtil.rtNoNullString(bundle.getString("display_type"));
		String messagebody = VsUtil.rtNoNullString(bundle.getString("messagebody"));
		String messagetype = "0";
		String messagetitle = VsUtil.rtNoNullString(bundle.getString("messagetitle"));
		if (messagetitle.length() == 0) {
			messagetitle = messagebody;
		}
		String messagelink = VsUtil.rtNoNullString(bundle.getString("messagelink"));
		String messagebuttontext = VsUtil.rtNoNullString(bundle.getString("messagebuttontext"));
		String messagelinktype = VsUtil.rtNoNullString(bundle.getString("messagelinktype"));
		String push_id = VsUtil.rtNoNullString(bundle.getString("push_id"));
		String minutes = VsUtil.rtNoNullString(bundle.getString("minutes"));
		String balance = VsUtil.rtNoNullString(bundle.getString("balance"));
		
		if (displaytype.equals("msgcenter") || displaytype.equals("tips")) {// 消息中心
			VsNoticeItem noticeItem = new VsNoticeItem();
			noticeItem.messageid = push_id;// 反馈的push_id
			noticeItem.messagebody = messagebody;
			noticeItem.messagetype = messagetype;
			noticeItem.messagetitle = messagetitle;
			// 获取当前时间
			noticeItem.messagetime = VsUtil.getDate();
			noticeItem.messagelink = messagelink;
			noticeItem.messagebuttontext = messagebuttontext;
			noticeItem.messagelinktype = messagelinktype;

			VsNotice.addNoticeMsg(VsApplication.getContext(), noticeItem);
//			context.sendBroadcast(new Intent(GlobalVariables.action_my_show_red));
		} else if (displaytype.equals("sysbroadcast") || displaytype.equals("pushad")) {
			// 系统广播
//			m_Notification = new Notification();
//			m_Notification.tickerText = messagetitle;
//			m_Notification.defaults = Notification.DEFAULT_SOUND;
//			m_Notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// 在通知栏上点击此通知后自动清除此通知
			if (displaytype.equals("sysbroadcast")) {
//				m_Notification.icon = R.drawable.icon;
			} else if (displaytype.equals("pushad")) {
//				m_Notification.icon = R.drawable.vs_news;
			}
			Intent mInten = new Intent(DfineAction.ACTION_SHOW_NOTICEACTIVITY);
			mInten.putExtra("messagelink", messagelink);
			mInten.putExtra("messagebody", messagebody);
			mInten.putExtra("push_id", push_id);
			mInten.putExtra("messagebuttontext", messagebuttontext);
			mInten.putExtra("messagelinktype", messagelinktype);
			m_PendingIntent = PendingIntent.getActivity(context, 0, mInten, PendingIntent.FLAG_UPDATE_CURRENT);
			if (displaytype.equals("sysbroadcast")) {
//				m_Notification.setLatestEventInfo(context,
//						DfineAction.RES.getString(R.string.product) + context.getResources().getString(R.string.point), messagetitle,
//						m_PendingIntent);

				m_Notification = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.icon)
						/**设置通知的标题**/
						.setContentTitle(messagetitle)
						/**设置通知的内容**/
						.setContentText(DfineAction.RES.getString(R.string.product) + context.getResources().getString(R.string.point))
						.setWhen(System.currentTimeMillis())
						.setAutoCancel(true)
						.setOngoing(false)
						.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
						.setContentIntent(m_PendingIntent)
						.build();

			} else if (displaytype.equals("pushad")) {
//				m_Notification.setLatestEventInfo(context, messagetitle, messagetitle, m_PendingIntent);
				m_Notification = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.icon)
						/**设置通知的标题**/
						.setContentTitle(messagetitle)
						/**设置通知的内容**/
						.setContentText(messagetitle)
						.setWhen(System.currentTimeMillis())
						.setAutoCancel(true)
						.setOngoing(false)
						.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
						.setContentIntent(m_PendingIntent)
						.build();
			}
			m_NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//			m_NotificationManager.notify(0, m_Notification);
			m_NotificationManager.notify(0, m_Notification);


			mInten = null;
		} else if (displaytype.equals("popbox")) {
			// 弹出框
			Intent dialogIntent = new Intent(VsApplication.getContext(), CustomDialogActivity.class);
			dialogIntent.putExtra("messagebody", messagebody);
			dialogIntent.putExtra("messagetitle", messagetitle);
			dialogIntent.putExtra("messagelink", messagelink);
			dialogIntent.putExtra("messagebuttontext", messagebuttontext);
			dialogIntent.putExtra("messagelinktype", messagelinktype);
			dialogIntent.putExtra("push_id", push_id);
			dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			VsApplication.getContext().startActivity(dialogIntent);
		}else if("task".equals(displaytype)){//任务赠送
			CustomLog.i("mydebug", "分钟++"+minutes);
			Intent makeMoneyIntent = new Intent(VsApplication.getContext(), VsDlogMakeMoney.class);
			makeMoneyIntent.putExtra("minutes", minutes);
			makeMoneyIntent.putExtra("balance", balance);
			makeMoneyIntent.putExtra("messagetitle", messagetitle);
			makeMoneyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			VsApplication.getContext().startActivity(makeMoneyIntent);
		}
	}
}
