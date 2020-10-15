package com.zte.functions.ad;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.widgets.VsViewPager;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.hwtx.dududh.R;

/**
 * Created by Jiangxuewu on 2015/3/19.
 * <p>
 * 自定义广告视图
 * </p>
 */
public class AdWidget extends ViewGroup implements ViewPager.OnPageChangeListener {


    private static final String TAG = "AdWidget";
    private final VsViewPager mVsViewPager;
    private final LinearLayout mPointViewLL;
    private final TextView mTitleTV;
    private boolean isAutoScroll = false;//是否可自动滚动
    private static final long mAutoScrollDelay = 3000L;//自动滚动时间间隔.
    private int iCount;//广告个数
    /**
     * 是否已开启自动滚动
     */
    private boolean isAutoScrolling = false;
    /**
     * 是否暂停自动滚动
     */
    private boolean isAutoScrollSuspend = false;
    private ImageView[] tips;
    private List<AdData> mAds;

    public AdWidget(Context context) {
        this(context, null);
    }

    public AdWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.ad_custom_widget, this);
        mVsViewPager = (VsViewPager) findViewById(R.id.ad_scrollLayout);
        mPointViewLL = (LinearLayout) findViewById(R.id.ad_point_ll);
        mTitleTV = (TextView) findViewById(R.id.ad_id_tv);
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

    public void isNeedPointBg(boolean isNeed) {
        if (isNeed) {
            mPointViewLL.setBackgroundColor(Color.parseColor("#88222222"));
            mTitleTV.setVisibility(View.VISIBLE);
        } else {
            mPointViewLL.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            mTitleTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeAllViews() {
//        super.removeAllViews();
        mVsViewPager.removeAllViews();
    }

    public void addAdData(List<AdData> ads) {
        if (null == mVsViewPager || null == ads) {
            return;
        }

        mAds = ads;

        iCount = ads.size();
        if (iCount > 1) {
            tips = new ImageView[iCount];
        } else {
            tips = null;
        }

        if (mPointViewLL.getChildCount() > 0) {
            mPointViewLL.removeAllViews();
        }

        MyAdapter myAdapter = new MyAdapter();
        int i = 0;
        for (final AdData ad : ads) {
            AdDataView adDataView = new AdDataView(getContext());

            adDataView.setAdData(ad, true);

            if (adDataView.isCanClick()) {
                adDataView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        String[] aboutBusiness = new String[]{ad.getTitle(), "", ad.getUrl()};
                        intent.putExtra("AboutBusiness", aboutBusiness);
                        intent.putExtra("AboutTextSize", 16);
                        intent.setClass(getContext(), WeiboShareWebViewActivity.class);
                        getContext().startActivity(intent);
                    }
                });
            }

            mVsViewPager.addView(adDataView);
            if (i == 0) {
                mTitleTV.setText(ad.getTitle());
            }
            if (null != tips) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LayoutParams(10, 10));
                tips[i] = imageView;
                if (i == 0) {
                    tips[i].setBackgroundResource(R.drawable.vs_page_indicator_focused);
                } else {
                    tips[i].setBackgroundResource(R.drawable.vs_page_indicator_unfocused);
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 3;
                layoutParams.rightMargin = 3;
                mPointViewLL.addView(imageView, layoutParams);
            }

            myAdapter.addView(adDataView);
            i++;
        }

        CustomLog.i(TAG, "addAdData(), iCount = " + iCount);

        if (iCount == 2) {
            AdDataView adDataView = new AdDataView(getContext());
            final AdData ad = ads.get(0);
            adDataView.setAdData_(ad, true);

            if (adDataView.isCanClick()) {
                adDataView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        String[] aboutBusiness = new String[]{ad.getTitle(), "", ad.getUrl()};
                        intent.putExtra("AboutBusiness", aboutBusiness);
                        intent.putExtra("AboutTextSize", 16);
                        intent.setClass(getContext(), WeiboShareWebViewActivity.class);
                        getContext().startActivity(intent);
                    }
                });
            }
            myAdapter.addView(adDataView);
            mVsViewPager.addView(adDataView);
            iCount = mVsViewPager.getChildCount();
            CustomLog.i(TAG, "addAdData(), addView0 === size = " + mVsViewPager.getChildCount());

            AdDataView adDataView1 = new AdDataView(getContext());
            final AdData ad1 = ads.get(1);
            adDataView1.setAdData_(ad1, true);

            if (adDataView1.isCanClick()) {
                adDataView1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        String[] aboutBusiness = new String[]{ad1.getTitle(), "", ad1.getUrl()};
                        intent.putExtra("AboutBusiness", aboutBusiness);
                        intent.putExtra("AboutTextSize", 16);
                        intent.setClass(getContext(), WeiboShareWebViewActivity.class);
                        getContext().startActivity(intent);
                    }
                });
            }
            myAdapter.addView(adDataView1);
            mVsViewPager.addView(adDataView1);
            iCount = mVsViewPager.getChildCount();
            CustomLog.i(TAG, "addAdData(), addView1 === size = " + mVsViewPager.getChildCount());
        }

        // 设置Adapter
        mVsViewPager.setAdapter(myAdapter);
        // 设置监听，主要是设置点点的背景
        mVsViewPager.setOnPageChangeListener(this);
        // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        mVsViewPager.setCurrentItem(iCount * 100);
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
//        selectItems = selectItems % tips.length;
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.vs_page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.vs_page_indicator_unfocused);
            }
        }
    }

    public void startAutoScroll() {
        isAutoScrollSuspend = false;
    }

    public void stopAutoScroll() {
        isAutoScrollSuspend = true;
    }

    /**
     * 启动自动滚动
     */
    public void startAutoScroll(final Handler handler) {

        if (null == mVsViewPager || mVsViewPager.getChildCount() <= 1) {
            return;
        }

        if (isAutoScrolling) {
            return;
        }
        isAutoScrolling = true;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isAutoScrollSuspend) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVsViewPager.isCanSnap()) {
                            mVsViewPager.snapToNextScreen();
                        }
                    }
                });

            }
        };
        long delay = mAutoScrollDelay;
        long period = mAutoScrollDelay;
        timer.schedule(task, delay, period);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        i = i % tips.length;
        setImageBackground(i);
        String title = mAds.get(i).getTitle();
        if (null == title)
            title = "";
        mTitleTV.setText(title);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    public class MyAdapter extends PagerAdapter {

        private List<View> viewPagerItems;

        public void addView(View v) {
            if (null == viewPagerItems) {
                viewPagerItems = new ArrayList<View>();
            }
            viewPagerItems.add(v);
        }


        @Override
        public int getCount() {
            return iCount <= 1 ? iCount : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
//            ((ViewPager) container).removeView(viewPagerItems.get(position % iCount));
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {

            try {
                ((ViewPager) container).addView(viewPagerItems.get(position % iCount), 0);
            } catch (Exception ignored) {
            }
            return viewPagerItems.get(position % iCount);
        }


    }
}
