package com.weiwei.netphone.ui.receiver;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.service.VsCoreService;

/**
 * 广播接收器，网络状态变化
 */
public class VsNetConnectionReceiver extends BroadcastReceiver {

	Timer time;

	@Override
	public void onReceive(final Context context, Intent intent) {

		try {
			// CustomLog.i("DataPack", "网络状态发生改变进入KcNetConnectionReceiver");
			// // TODO Auto-generated method stub
			if (VsUtil.isServiceRunning(VsApplication.getContext(), VsApplication.getContext().getPackageName())) {
				CustomLog.i("DataPack", "service已经运行");
			} else {
				CustomLog.i("DataPack", "service没有运行");
				// 启动服务
				Intent sendIntent = new Intent(VsApplication.getContext(), VsCoreService.class);
				context.startService(sendIntent);
			}
			if (time != null) {
				time.cancel();
				time = null;
			}
			time = new Timer();
			time.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// Bundle bundle = new Bundle();
					// bundle.putString("action", KcCoreService.KC_ACTION_TCP_HEARTBEAR);
					// // 启动服务去处理连接和心跳
					// Intent intentService = new Intent(context, KcCoreService.class);
					// intentService.putExtras(bundle);
					// context.startService(intentService);
					if (time != null) {
						time.cancel();
						time = null;
					}
					GlobalVariables.netmode = VsNetWorkTools.getSelfNetworkType(context);
					// 无网络
					if (GlobalVariables.netmode == 0) {
						Intent network_change = new Intent(GlobalVariables.action_no_network);
						context.sendBroadcast(network_change);
					} else if (GlobalVariables.netmode == 3) {
						Intent network_change = new Intent(GlobalVariables.action_net_change);
						context.sendBroadcast(network_change);
					}
				}
			}, 0, 3000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
