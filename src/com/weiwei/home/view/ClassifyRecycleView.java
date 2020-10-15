package com.weiwei.home.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.weiwei.home.utils.LogUtils;

/**
 * @author : hc
 * @date : 2019/5/7.
 * @description: 滑到底部时不消费事件,//失败
 */

public class ClassifyRecycleView extends RecyclerView {

    private float mDownY = 0;

    public ClassifyRecycleView(Context context) {
        super(context);
    }

    public ClassifyRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassifyRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = e.getY();
                break;
        }
        //...
        float mDirection = e.getY() - mDownY;
        if(!canScrollVertically(-1) && mDirection > 5){
            LogUtils.e("滑到顶部");
            return false;
        }else if(!canScrollVertically(1) && mDirection < -5) {
            LogUtils.e("滑到底部");
            return false;
        }
        return super.onTouchEvent(e);
    }
}
