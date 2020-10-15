package com.weiwei.base.util;

import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;

/**
 * 
 * @Title:Android客户端
 * @Description: 短信手机监听类
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class ReceiverSendNoteObserver extends ContentObserver {
	/**
	 * 句柄
	 */
	private Handler mHandler = null;

	public ReceiverSendNoteObserver(Handler handler) {
		super(handler);
		this.mHandler = handler;
	}

	Timer time;
	Timer time2;

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		if (time != null) {
			time.cancel();
			time = null;
		}
		if (time2 != null) {
			time2.cancel();
			time2 = null;
		}
		Cursor cursor = null;
		Cursor cursor2 = null;
		try {
			ContentResolver resolver = VsApplication.getContext().getContentResolver();		
			// 查到收到的短信
			Uri uri2 = Uri.parse("content://sms/inbox");
			String[] projection = new String[] { "_id", "address", "person",  
	                "body", "date", "type" };
			String where = "date > "
				+ (System.currentTimeMillis() - 60 * 1000);
			cursor2 = resolver.query(uri2, projection, where, null, "date desc");			
			if (null == cursor2)
				return;			
			StringBuffer str=new StringBuffer();
			if(cursor2.moveToNext()) {
				String body = cursor2.getString(cursor2.getColumnIndex("body"));	
				str.append(body);
			}
			String bodystr=str.toString();
			Pattern pattern = Pattern.compile("[0-9]{4}");
			Matcher matcher = pattern.matcher(bodystr);
			if (matcher.find()) {
				String res = matcher.group();
				Message message = mHandler.obtainMessage();
				Bundle data = new Bundle();
				data.putString("code",res);
				message.setData(data);
				message.what = VsUserConfig.MSG_ID_GET_MSG_SUCCESS;
				mHandler.sendMessage(message);

			}
			
			
			/*if (cursor2 != null && cursor2.moveToFirst()
					&& VsUtil.checkSMSTime(cursor2.getLong(cursor2.getColumnIndex("date")), 30)) {
				if (cursor2.getColumnCount() > 0) {//判断是否有内容
					receiverNoteBody = cursor2.getString(0);
				}
				CustomLog.i("vsdebug", "监听到收件箱变化：" + receiverNoteBody);
				String securityCode = "验证码";
				String securityCodeEnd = "，";
				if (receiverNoteBody != null && receiverNoteBody.length() > 2) {
					int index = receiverNoteBody.indexOf(securityCode);
					if (index >= 0) {
						int endIndex = receiverNoteBody.indexOf(securityCodeEnd, index);
						if (endIndex >= 0) {
							final String SecurityCodeStr = receiverNoteBody.substring(index + 2, endIndex);
							if (SecurityCodeStr != null) {
								time2 = new Timer();
								time2.schedule(new TimerTask() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Message message = mHandler.obtainMessage();
										Bundle data = new Bundle();
										data.putString("code", SecurityCodeStr.replaceAll("[^0-9]", ""));
										message.setData(data);
										message.what = VsUserConfig.MSG_ID_GET_MSG_SUCCESS;
										mHandler.sendMessage(message);

									}
								}, 2000);
							}
						}
					}
				}
			}*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (cursor2 != null) {
				cursor2.close();
				cursor2 = null;
			}
		}
	}
}