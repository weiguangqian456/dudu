package com.weiwei.salemall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * @author Created by EDZ on 2018/8/10.
 *         跳转页面工具类封装
 */

public class SkipPageUtils {
    private static SkipPageUtils instance;
    private static Context mContext;
    private static Activity mActivity;

    public static SkipPageUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SkipPageUtils();
        }
        mContext = context;
        mActivity = (Activity) mContext;
        return instance;
    }

    public void skipPage(Class<? extends Activity> cls) {
        Intent intent = new Intent(mContext, cls);
        mContext.startActivity(intent);
    }

    public void skipPage(Class<? extends Activity> cls, String key, String value) {
        Intent intent = new Intent(mContext, cls);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        mContext.startActivity(intent);
    }

    public void skipPage(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(mContext, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        mContext.startActivity(intent);
    }

    public void skipPageAndFinish(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(mContext, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }


    public void skipPage(Class<? extends Activity> cls, String[] key, String[] values) {
        Intent intent = new Intent(mContext, cls);
        if (null != key && null != values) {
            for (int i = 0; i < key.length; i++) {
                intent.putExtra(key[i], values[i]);
            }
        }
        mContext.startActivity(intent);
    }

    private void skipPageAndFinish(Class<? extends Activity> cls, String key, String value) {
        Intent intent = new Intent(mContext, cls);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }

    public void skipPageAndFinish(Class<? extends Activity> cls, String[] key, String[] values) {
        Intent intent = new Intent(mContext, cls);
        if (null != key && null != values) {
            for (int i = 0; i < key.length; i++) {
                intent.putExtra(key[i], values[i]);
            }
        }
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }

    public void skipPageAndFinish(Class<? extends Activity> cls) {
        Intent intent = new Intent(mContext, cls);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }
}
