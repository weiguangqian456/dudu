package com.weiwei.netphone.ui.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsRc4;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
/**
 * 
 * @Title:Android客户端
 * @Description:登录成功广播--用于加载一些配置数据
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-10-24
 */
public class VsSucceedLoginReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		CustomLog.i("vsdebug", "VsSucceedLoginReceiver--runing");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String packname = bundle.getString("packname");
			if (packname != null && packname.equals(context.getPackageName())) {
				if (VsUtil.checkHasAccount(context)) {
					try {
						if (!VsUserConfig.getDataBoolean(context, VsUserConfig.JKey_ActionSwitchAccount, false)) {// 用户切换账号（用于来显状态显示）
							VsUserConfig.setData(context, VsUserConfig.JKey_ActionSwitchAccount, true);
							String user_id = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
							VsUserConfig.setData(context, VsUserConfig.JKey_KcOldId, user_id);// 保存ID，用于判断用户切换账号

						} else {
							String new_id = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
							String old_id = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcOldId);
							if (!old_id.equals(new_id)) {
								VsUserConfig.setData(context, VsUserConfig.JKey_FIRSTBIND_QQKJ, false);
								VsUserConfig.setData(context, VsUserConfig.JKey_FIRSTBIND_TENX, false);
								// 切换账号后需要重新拉取30分钟赠送信息
								VsUserConfig.setData(context, VsUserConfig.JKey_RegSurplus, "");
								VsUserConfig.setData(context, VsUserConfig.JKey_RegAwardSwitch, true);
								// 保存ID，用于判断用户切换账号
								VsUserConfig.setData(context, VsUserConfig.JKey_KcOldId, new_id);
								// 还需要上报一次安装量
								VsUserConfig.setData(context, VsUserConfig.JKey_RECORDINSTALL_WITH_UID, false);
							}
						}
						// 启动服务去拉取登录成功的数据
						Bundle loginSuccBundle = new Bundle();
						Intent intentService = new Intent(context, VsCoreService.class);
						loginSuccBundle.putString("action", VsCoreService.VS_ACTION_LOGIN_SUCC);
						intentService.putExtras(loginSuccBundle);
						context.startService(intentService);
                         // 获取融云 Token建立拨打连接
                        
//                        VsBizUtil.getInstance().getRYToken(context, "0");
						// 创建一个文件存放账号密码
						String filePath = creatFile();
						FileOutputStream writer = new FileOutputStream(filePath);
						writer.write(VsRc4.encry_RC4_string(
								"账号:" + VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId) + ",密码:"
										+ VsUserConfig.getDataString(context, VsUserConfig.JKey_Password) + ",",
								DfineAction.passwad_key2).getBytes("utf-8"));
						writer.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					// stopAutoRegister = true;// 停止其他自动注册
					// connctTcp();// 重新连接tcp服务器
				}
			}
		}
	}

	/**
	 * 创建文件存放账号密码
	 * 
	 * @return
	 */
	public String creatFile() {
		File file = new File(DfineAction.mWldhFilePath);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		String fileName = DfineAction.mWldhFilePath + DfineAction.brandid + "_2.txt";
		File destDir = new File(fileName);
		try {
			destDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

}
