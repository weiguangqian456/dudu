package com.weiwei.netphone.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weiwei.base.service.VsCoreService;

/**
 * 广播接收器，监测设备开机后自动运行该应用程序
 */
public class VsTurnOnDeviceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 启动服务
		context.startService(new Intent(context, VsCoreService.class));
	}
}
