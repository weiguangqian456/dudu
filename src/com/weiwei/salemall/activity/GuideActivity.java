package com.weiwei.salemall.activity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hwtx.dududh.R;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Created by EDZ
 * 用户第一次进入运用时的导向
 */
public class GuideActivity extends AppCompatActivity {
    private static final String LOG = "GuideActivity";
    int currentItem;
    List<Integer> imageIDList;
    List<ImageView> imageViews;
    ViewPager viewPager;
    LinearLayout ll_container;

    boolean firstLoginSuccessFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        //初始化引导数据
        initGuideData();
        //初始化引导页
        initGuideView();
        //初始化分页控件
        iniView();
    }

    /**
     * 初始化引导页数据
     */
    private void initGuideData() {
        imageIDList = new ArrayList();
        imageIDList.add(R.drawable.app_wel_first);
        imageIDList.add(R.drawable.app_wel_second);
        imageIDList.add(R.drawable.app_wel_third);
    }

    /**
     * 初始化引导页
     */
    private void initGuideView() {
        ImageView imageView;
        imageViews = new ArrayList<>();
        for (int i = 0; i < imageIDList.size(); i++) {

            imageView = new ImageView(this);
            imageViews.add(imageView);
            if(i == imageIDList.size()-1){
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMainActivity();
                    }
                });
            }
        }
    }

    /**
     * 初始化分页控件
     */
    private void iniView() {
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        viewPager = (ViewPager) findViewById(R.id.guide_pager);
        viewPager.setAdapter(new GuideAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                Log.i("Guide", "监听改变" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            float endX;
            float endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        WindowManager windowManager = (WindowManager) getApplicationContext()
                                .getSystemService(Context.WINDOW_SERVICE);
                        //获取屏幕的宽度
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        int width = size.x;
                        //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
                        if (currentItem == (imageViews.size() - 1) && startX - endX > 0 && startX
                                - endX >= (width / 4)) {
                            Log.i(LOG, "进入了触摸");
                            goToMainActivity();
//                            overridePendingTransition(R.anim.slide_in_right, R.anim
// .slide_in_left);
                        }
                        break;
                }
                return false;
            }
        });
    }
    boolean isClickToMain = false;
    private void goToMainActivity() {
        if(!isClickToMain){
            SpUtils.put(GuideActivity.this, "firstLoginSuccessFlag", firstLoginSuccessFlag);
            SkipPageUtils.getInstance(this).skipPageAndFinish(VsMainActivity.class);
       /*     Intent intent = new Intent(this,VsMainActivity.class);
            intent.putExtra("indicator",6);
            startActivity(intent);
            finish();*/
        }
    }

    /**
     * Viewpager适配器
     */
    private class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 判断当前分页是不是view
         * 由于ViewPager里面的分页可以填入Fragment
         *
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 清理内存
         * 从第一页滑动到第二页，此时第一页的内存应该释放
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));//释放滑动过后的前一页
        }

        /**
         * 得到---->暂时是没有用的
         *
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        /**
         * 初始化分页
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
//            imageView.setImageResource(imageIDList.get(position));
            Glide.with(getApplicationContext()).load(imageIDList.get(position)).into(imageView);
            ViewGroup.LayoutParams viewLayoutParams = new ViewGroup.LayoutParams(ViewGroup
                    .LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            container.addView(imageView, viewLayoutParams);//设置图片的宽高
            return imageView;
        }
    }
}