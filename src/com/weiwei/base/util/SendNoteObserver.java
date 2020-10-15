package com.weiwei.base.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
/**
 * 
 * @Title:Android客户端
 * @Description: 短信发送监听类
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class SendNoteObserver extends ContentObserver {
	private Context mContext = null;
	public static int sendSendNoteNumber = 0;// 发送短信个数
	public static boolean isSendSuc = false;// 发送成功标识
	public Handler handler;

	public SendNoteObserver(Handler handler, Context context) {
		super(handler);
		mContext = context;
		this.handler = handler;
	}

	public void onChange(boolean selfChange) {
		Cursor cursor = null;
		try {
			ContentResolver resolver = mContext.getContentResolver();
			// 查到发出的短信
			Uri uri = Uri.parse("content://sms/outbox");
			cursor = resolver.query(uri, new String[] { "date", "address", "body" }, null, null, "_id desc limit 1");
			if (cursor != null && cursor.moveToFirst()
					&& VsUtil.checkSMSTime(cursor.getLong(cursor.getColumnIndex("date")), 2)) {
				isSendSuc = true;
				CustomLog.i("vsdebug", "捕获短信发送成功-----");
				handler.sendEmptyMessage(VsUserConfig.MSG_ID_GET_SEND_SMS_SIGNAL);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
}
