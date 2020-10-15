package com.zte.functions.ad;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hwtx.dududh.R;

/**
 * Created by Jiangxuewu on 2015/3/19.
 */
public class AdDataView extends ViewGroup {

    private static final String TAG = AdDataView.class.getSimpleName();
    private ImageView mAdImage;

    private AdData mAdData;

    private boolean isCanClick = false;

    public AdData getAdData() {
        return mAdData;
    }

    public boolean isCanClick() {
        return mAdData.isRight() && isCanClick;
    }

    public void setAdData(final AdData adData, boolean isCanClick) {
        mAdData = adData;
        if (null != mAdImage) {
            AdManager.getInstance().initImageView(mAdImage, adData.getLogopic());
        }
        this.isCanClick = isCanClick && null != adData && !TextUtils.isEmpty(adData.getUrl());
    }
    public void setAdData_(final AdData adData, boolean isCanClick) {
        mAdData = adData;
        if (null != mAdImage) {
            AdManager.getInstance().initImageView_(mAdImage, adData.getLogopic());
        }
        this.isCanClick = isCanClick && null != adData && !TextUtils.isEmpty(adData.getUrl());
    }

    public AdDataView(Context context) {
        this(context, null);
    }

    public AdDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.ad_custom_data_view, this);
        mAdImage = (ImageView) findViewById(R.id.ad_bg_image);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth,
                        childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
