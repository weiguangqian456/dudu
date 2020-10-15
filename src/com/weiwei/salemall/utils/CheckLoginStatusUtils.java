package com.weiwei.salemall.utils;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;

/**
 * @author Created by EDZ on 2018/8/8.
 *         检查登录状态
 */

public class CheckLoginStatusUtils {
    public static boolean isLogin() {
        boolean isLogin = false;
        String uid = VsUserConfig.getDataString(VsApplication.getContext(), VsUserConfig.JKey_KcId);
        if (uid != null && !uid.isEmpty()) {
            isLogin = true;
        }
        return isLogin;
    }
}
