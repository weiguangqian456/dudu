/**
 * Copyright 2014 eTao Tech. Inc. LTD. All rights reserved.
 * - Powered by Team GOF. -
 */
package com.weiwei.salemall.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.weiwei.salemall.receiver.NetStateChangedReceiver;


/**
 * @ClassName: IntentFilterUtils
 * @Description: 用一句话描述该文件做什么
 * @author Tanlifei 
 * @date 2014-6-5 下午3:58:46
 * 
*/

public abstract class IntentFilterUtils {

	public void register(Context context){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(getNetStateChangedReceiver(), filter);
	}
	public abstract NetStateChangedReceiver getNetStateChangedReceiver();
}
