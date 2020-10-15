package com.weiwei.home.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author : hc
 * @date : 2019/5/5.
 * @description:
 */

public class PagerVerticalView extends ViewPager {

    public PagerVerticalView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PagerVerticalView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化
     */
    private void initView(){
        // 设置去掉滑到最左或最右时的滑动效果
        setOverScrollMode(OVER_SCROLL_NEVER);
        // 将viewpager翻转
        setPageTransformer(true, new VerticalPageTransformer());
    }

    /**
     * 滑动
     */
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return true;
    }

    /**
     * 交换触摸事件的X和Y坐标
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();
        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        ev.setLocation(newX, newY);
        return ev;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        return super.onTouchEvent(swapXY(ev));
    }

    private float olderY = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                olderY = ev.getY();
                break;
//            case MotionEvent.ACTION_MOVE:
//                float moveY = Math.abs(olderY - ev.getY());
//                View childAt = getChildAt(0);
//                if(getChildAt(0) instanceof ClassifyRecycleView){
//                    childAt.
//                    return true;
//                }
//                break;
        }
        float mDirection = ev.getY() - olderY;
        View childAt = getChildAt(0);
        if(!childAt.canScrollVertically(-1) && mDirection > 5){
            return true;
        }else if(!childAt.canScrollVertically(1) && mDirection < -5) {
            return true;
        }
        return intercepted; //为所有子视图返回触摸的原始坐标
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View view, float position) {
            if (position <= 1) {
                view.setTranslationX(view.getWidth() * -position);// 抵消默认幻灯片过渡
                float yPosition = position * view.getHeight();//设置从上滑动到Y位置
                view.setTranslationY(yPosition);
            }
        }
    }

}
