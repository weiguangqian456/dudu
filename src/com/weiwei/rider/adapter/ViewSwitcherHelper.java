package com.weiwei.rider.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.weiwei.base.application.VsApplication;
import com.weiwei.salemall.utils.DensityUtils;

public class ViewSwitcherHelper {

    private Drawable mPosOff;
    private Drawable mPosOn;
    private ViewGroup viewGroup;
    private Context mContext;
    private int currentPos;

    public ViewSwitcherHelper(Context context, ViewGroup layout,Drawable draw_one,Drawable draw_two) {
        mContext = context;
        viewGroup = layout;
        mPosOn = draw_one;
        mPosOff= draw_two;
    }

    public void setViewSwitcherTip(int count, int current) {
        this.currentPos = current;
        viewGroup.removeAllViews();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                ImageView mImageView = getPositionView(i == current);
                mImageView.setTag(i);
                viewGroup.addView(mImageView);
            }
        } else {

        }
    }

    private ImageView getPositionView(boolean isOn) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setImageDrawable(isOn ? mPosOn : mPosOff);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(DensityUtils.dip2px(10), DensityUtils.dip2px(10),
                DensityUtils.dip2px(10), DensityUtils.dip2px(20));
        mImageView.setLayoutParams(mLayoutParams);
        return mImageView;
    }

    public void setCurrent(int current) {
        if (current >= viewGroup.getChildCount()) {
            return;
        }
        viewGroup.removeViewAt(currentPos);
        viewGroup.addView(getPositionView(false), currentPos);
        this.currentPos = current;
        viewGroup.removeViewAt(current);
        viewGroup.addView(getPositionView(true), current);
    }

}
