package com.weiwei.salemall.utils;

import android.content.Context;

/**
 * @author: EDZ
 * @description: 时间格式化工具类
 */
public class TimeChangeUtils {
    private static TimeChangeUtils instance;
    private Context mContext;

    private TimeChangeUtils(Context context) {
        this.mContext = context;
    }

    public synchronized static TimeChangeUtils getInstance(Context context) {
        if (instance == null) {
            instance = new TimeChangeUtils(context);
        }
        return instance;
    }

    public String formatLongToTimeStr(Long duration) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = duration.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + "：" + minute + "：" + second;
        return strtime;
    }
}
