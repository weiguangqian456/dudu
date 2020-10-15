package com.weiwei.base.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.weiwei.base.dataprovider.GlobalVariables;

/**
 * Created by Jiangxuewu on 2015/3/9.
 * <p>
 *     FragmentActivity基础类，实现左滑后退事件。
 * </p>
 */
public class VsBaseFragmentActivity extends FragmentActivity {
    private float mLastMotionX;
    private float mLastMotionY;
    private static int SNAP_VELOCITY_X = 0;
    private static int SNAP_VELOCITY_Y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        if (SNAP_VELOCITY_X == 0) {
            SNAP_VELOCITY_X = GlobalVariables.width / 3;
        }
        if (SNAP_VELOCITY_Y == 0) {
            SNAP_VELOCITY_Y = (int) (GlobalVariables.density * 45 + 0.5F);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (x - mLastMotionX);
                int deltaY = (int) Math.abs(y - mLastMotionY);
                if (deltaX >= SNAP_VELOCITY_X && deltaY <= SNAP_VELOCITY_Y) {
                    onBackPressed();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
