package com.weiwei.salemall.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * @author Created by EDZ on 2018/6/14.
 *         禁止RecycleView滑动
 */

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}