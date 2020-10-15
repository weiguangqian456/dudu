package com.weiwei.netphone.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.service.VsCoreService;

public class VsPhoneIntensityBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// CustomLog.i("DataPack", "电话信号强度发生改变KcPhoneIntensityBroadcastReceiver");
		try {
			if (VsUtil.isServiceRunning(VsApplication.getContext(), VsApplication.getContext().getPackageName())) {
				CustomLog.i("DataPack", "service已经运行");
			} else {
				CustomLog.i("DataPack", "service没有运行");
				// 启动服务
				Intent sendIntent = new Intent(VsApplication.getContext(), VsCoreService.class);
				context.startService(sendIntent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
