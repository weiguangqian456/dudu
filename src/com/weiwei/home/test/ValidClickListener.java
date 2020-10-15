package com.weiwei.home.test;

import android.view.View;

import com.hwtx.dududh.R;
import com.weiwei.home.utils.LogUtils;

/**
 * @author : hc
 * @date : 2019/3/11.
 * @description: 防止多次点击
 */

public abstract class ValidClickListener implements View.OnClickListener{

    /**
     * 点击间隔时长超过 time 时调用
     */
    public abstract void onValidClick();

    private final int time = 100;
    private final int key  = R.id.tag_click;
    @Override
    public void onClick(View v) {
        Object tag = v.getTag(key);
        if(v.getTag(key) == null){
            onValidClick();
            v.setTag(key,System.currentTimeMillis());
        }else{
            if((System.currentTimeMillis() - (long)tag) >= time){
                onValidClick();
                v.setTag(key,System.currentTimeMillis());
            }else{
                LogUtils.e("重复点击");
            }
        }
    }

}
