/**
 * Copyright 2014 eTao Tech. Inc. LTD. All rights reserved.
 * - Powered by Team GOF. -
 */

package com.weiwei.base.application;

/**
 * @ClassName: IApplicationStateChangedHandler
 * @Description: 用一句话描述该文件做什么
 * @author jinsongliu 
 * @date 2015-03-15 下午5:13:19
 * 
*/

public interface IApplicationStateChangedHandler {
	/** 
	 * @Description: 应用状态变化，如进程被杀死 做的善后处理
	 * @throws:throws
	*/ 
	
	public void setStopAllAppDownload();
}