package com.weiwei.home.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * @author : hc
 * @date : 2019/4/2.
 * @description:
 */

public class PagerScroller extends Scroller {

    private int mDuration = 1000;

    public PagerScroller(Context context) {
        super(context);
    }

    public PagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public PagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy,mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    void initViewPagerScroll(ViewPager pager, int mDuration){
        this.mDuration = mDuration;
        try{
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(pager,this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
