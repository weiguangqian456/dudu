package com.weiwei.salemall.utils;

/**
 * @author Created by EDZ on 2018/8/9.
 *         禁止快速点击
 */

public class NoMultClickUtils {
    // 两次点击间隔不能少于1000ms
    private static final int MIN_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
