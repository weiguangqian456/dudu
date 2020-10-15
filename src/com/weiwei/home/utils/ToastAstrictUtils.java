package com.weiwei.home.utils;

import android.widget.Toast;

import com.weiwei.base.application.VsApplication;

/**
 * @author : hc
 * @date : 2019/3/22.
 * @description: 防止Toast无限弹出
 */

public class ToastAstrictUtils {

    private static Toast mToast;
    private static ToastAstrictUtils mToastUtils;

    public static ToastAstrictUtils getInstance() {
        if(mToastUtils == null) {
            mToastUtils = new ToastAstrictUtils();
        }
        return mToastUtils;
    }

    public void show(String msg){
        if(mToast == null){
            mToast = Toast.makeText(VsApplication.getContext(),msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

}
