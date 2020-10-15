/**
 * Copyright 2014 eTao Tech. Inc. LTD. All rights reserved.
 * - Powered by Team GOF. -
 */

package com.weiwei.base.application;

import android.content.Context;

/**
 * @author jinsongliu
 * @ClassName: ApplicationStateChangedHandler
 * @Description: 应用状态变化的监听
 * @date 2015-03-15 下午5:12:01
 */

public class ApplicationStateChangedHandler implements IApplicationStateChangedHandler {

    /**
     * @Description: 暂停所有下载
     * @throws:throws
     */
    @Override
    public void setStopAllAppDownload() {
//        try {
//            AppDownloadImpl.getInstance().stopAllAppDownload();
//        } catch (DbException e) {
//            e.printStackTrace();
//            LoggerUtils.info(e.getMessage());
//        }
    }

    public void sendAppInstalledOrUninstalledStatistics(Context activity) {
//        HttpRequestUtils.sendAppDownloadStatistics(activity);
    }

}
