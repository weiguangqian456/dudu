package com.weiwei.home.utils;


import android.util.Log;

/**
 * @author : hc
 * @date : 2019/3/19.
 * @description: Log
 */

public class LogUtils {
    private static final String TAG = "TAG-DEFAULT";
    public static void e(String msg){
        Log.e(TAG,msg);
    }

    public static void e(String tag,String msg){
        Log.e(tag,msg);
    }
}
