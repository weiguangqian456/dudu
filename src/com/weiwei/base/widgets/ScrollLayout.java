package com.weiwei.base.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.zte.functions.ad.AdData;
import com.zte.functions.ad.AdDataView;
import com.weiwei.base.common.CustomLog;
import com.weiwei.weibo.WeiboShareWebViewActivity;

public class ScrollLayout extends ViewGroup {

    private static final String TAG = "ScrollLayout";

    private VelocityTracker mVelocityTracker; // 用于判断甩动手势

    private static final int SNAP_VELOCITY = 300;

    private Scroller mScroller;

    private int mCurScreen;

    private int mDefaultScreen = 0;

    private float mLastMotionX;
    private OnViewChangeListener mOnViewChangeListener;
    private boolean isTouch = false;


    private float downX, downY, upX, upY;
    private long downTime;


    public ScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mCurScreen = mDefaultScreen;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childLeft = 0;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    final int childWidth = childView.getMeasuredWidth();
                    childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                    childLeft += childWidth;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurScreen * width, 0);
    }

    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        CustomLog.i("GDK", "snapToNextScreen whichScreen" + whichScreen + "getChildCount() - 1=" + (getChildCount() - 1));
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        CustomLog.i("GDK", "getScrollX()" + getScrollX() + "," + whichScreen * getWidth());
        if (getScrollX() != (whichScreen * getWidth())) {

            final int delta = whichScreen * getWidth() - getScrollX();

            mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta));

            mCurScreen = whichScreen;
            invalidate(); // Redraw the layout

            if (mOnViewChangeListener != null) {
                if (whichScreen == getChildCount() - 1 && getScrollX() > (whichScreen * getWidth())) {
                    mOnViewChangeListener.OnViewChange(mCurScreen, false);
                } else {
                    mOnViewChangeListener.OnViewChange(mCurScreen, true);
                }
            }
        } else {
            if (mOnViewChangeListener != null && whichScreen == getChildCount() - 1) {
                mOnViewChangeListener.OnViewChange(mCurScreen, false);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                CustomLog.i("GDK", "MotionEvent.ACTION_DOWN");
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(event);
                }

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastMotionX = x;

                downTime = System.currentTimeMillis();
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                int deltaX = (int) (mLastMotionX - x);

                if (IsCanMove(deltaX)) {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                    }

                    mLastMotionX = x;
                    scrollBy(deltaX, 0);
                }

                break;

            case MotionEvent.ACTION_UP:
                CustomLog.i("GDK", "MotionEvent.ACTION_UP");
                isTouch = false;
                int velocityX = 0;
                upX = event.getX();
                upY = event.getY();
                if (Math.abs(upX - downX) < 30 && Math.abs(upY - downY) < 30 && (System.currentTimeMillis() - downTime) < 200) {
                    View v = getChildAt(getCurScreen());
                    if (!(v instanceof AdDataView)) {
                        return true;
                    }
                    AdData adData = ((AdDataView) v).getAdData();
                    boolean canClick = ((AdDataView) v).isCanClick();
                    if (null == adData || !canClick) {
                        return true;
                    }
                    Intent intent = new Intent();
                    String[] aboutBusiness = new String[]{adData.getTitle(), "", adData.getUrl()};
                    intent.putExtra("AboutBusiness", aboutBusiness);
                    intent.putExtra("AboutTextSize", 16);
                    intent.setClass(getContext(), WeiboShareWebViewActivity.class);
                    getContext().startActivity(intent);
                    return true;
                }
                try {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(event);
                        mVelocityTracker.computeCurrentVelocity(1000);
                        velocityX = (int) mVelocityTracker.getXVelocity();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    Log.e(TAG, "snap left");
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    Log.e(TAG, "snap right");
                    snapToScreen(mCurScreen + 1);
                } else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                break;
        }

        return true;
    }

    private boolean IsCanMove(int deltaX) {

        if (getScrollX() <= 0 && deltaX < 0) {
            return false;
        }

        if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
            mOnViewChangeListener.OnViewChange(mCurScreen, false);
            return false;
        }

        return true;
    }

    public void SetOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }


    public int getCurScreen() {
        return mCurScreen;
    }

    /**
     * 是否有手指触摸
     *
     * @return
     */
    public boolean isCanSnap() {

        return !isTouch;
    }
}
