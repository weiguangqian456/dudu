package com.weiwei.salemall.utils;

import android.view.View;

/**
 * @author Created by EDZ on 2018/11/6.
 *         Describe   防止重复点击
 */

public abstract class OnClickEvent implements View.OnClickListener {

    private static long lastTime;

    public abstract void singleClick(View v);

    private long delay;

    public OnClickEvent(long delay) {
        this.delay = delay;
    }

    @Override
    public void onClick(View v) {
        if (onMoreClick(v)) {
            return;
        }
        singleClick(v);
    }

    public boolean onMoreClick(View v) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }
}