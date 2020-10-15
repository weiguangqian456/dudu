package com.weiwei.home.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * @author : hc
 * @date : 2019/4/9.
 * @description: 今日头条 屏幕适配
 */

public class ScreenAdaptiveUtils {

    private final static int SCREEN_WIDTH_DP = 360;
    private static float mDensity;
    private static float mScaleDensity;

    private static void initCustomDensity(final Activity activity,final  Application application,boolean isAdaptiveSP){
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //已经获取？
        if(mDensity == 0){
            mDensity = displayMetrics.density;
            mScaleDensity = displayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        float targetDensity       = displayMetrics.widthPixels/SCREEN_WIDTH_DP;
        float scaledDensity       = targetDensity * (mScaleDensity / mDensity);
        float targetScaledDensity = isAdaptiveSP ? scaledDensity : targetDensity;
        int   targetDensityDpi    = (int) (160 * targetDensity);
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    public static void initCustomDensity(final Activity activity,final  Application application){
        initCustomDensity(activity,application,false);
    }
}
