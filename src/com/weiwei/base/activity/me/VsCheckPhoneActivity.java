package com.weiwei.base.activity.me;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.adapter.VsCallMoneyAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.ArrayList;

public class VsCheckPhoneActivity extends VsBaseActivity {

    private TextView callmoney, calllog, taocan;
    private ImageView cursor;
    private String uid = "";
    private int offset, imageWidth;
    private ViewPager viewPager;
    private ArrayList<View> viewList;
    private int currIndex = 0;
    private String flag_type = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_mycheck_dtail);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getResources().getString(R.string.vs_mycheck_detail));

        showLeftNavaBtn(R.drawable.icon_back);
        setDisEnableLeftSliding();
        uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId, "");
        init();
        InitViewPager(savedInstanceState);
        VsBizUtil.getInstance().goodsConfig(this);

        VsApplication.getInstance().addActivity(this);
    }


    private void init() {

        cursor = (ImageView) findViewById(R.id.cursor);
        callmoney = (TextView) findViewById(R.id.callmoney);
        calllog = (TextView) findViewById(R.id.calllog);
        taocan = (TextView) findViewById(R.id.taocan);


        int screenWidth = this.getScreenWidth();
        imageWidth = BitmapFactory.decodeResource(getResources(), R.drawable.scour).getWidth();//screenWidth/3;
        offset = (screenWidth / 3 - imageWidth) / 2;
        Matrix matrix = new Matrix();
        if ("2".equals(flag_type)) {
            matrix.postTranslate(2 * screenWidth / 3 + offset, 0);
            currIndex = 2;
        } else {
            matrix.postTranslate(offset, 0);
        }

        cursor.setImageMatrix(matrix);
        callmoney.setOnClickListener(new MyOnClickListener(0));
        calllog.setOnClickListener(new MyOnClickListener(1));
        taocan.setOnClickListener(new MyOnClickListener(2));
    }


    private void InitViewPager(Bundle savedInstanceState) {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        LocalActivityManager manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        // 添加页面数据
        viewList = new ArrayList<View>();
        Intent intent = new Intent(this, VsBalanceMoney.class);
        viewList.add(manager.startActivity("A", intent).getDecorView());
        intent = new Intent(this, VsCallLogActivity.class);
        viewList.add(manager.startActivity("B", intent).getDecorView());
        intent = new Intent(this, VsMyMealActivity.class);
        viewList.add(manager.startActivity("C", intent).getDecorView());

        viewPager.setAdapter(new VsCallMoneyAdapter(viewList));
        if ("2".equals(flag_type)) {
            viewPager.setCurrentItem(2);

        } else {
            viewPager.setCurrentItem(0);
        }

        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    ;


    //获取手机屏幕的宽度，为滑条移动定位
    private int getScreenWidth() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        return screenWidth;
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            int one = offset * 2 + imageWidth;    // 页卡1移动到 页卡2的偏移量
            int two = one * 2;                    // 页卡1移动到 页卡3的偏移量.
            switch (arg0) {
                case 0://在第一位置，，要么移到1.要么到2
                    if (currIndex == 1) {
                        //设置图片平移（）
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-one, -two, 0, 0);
                        } else {
                            animation = new TranslateAnimation(one, 0, 0, 0);
                        }


                    } else if (currIndex == 2) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(0, -two, 0, 0);
                        } else {
                            animation = new TranslateAnimation(two, 0, 0, 0);
                        }

                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-two, -one, 0, 0);
                        } else {
                            animation = new TranslateAnimation(offset, one, 0, 0);
                        }


                    } else if (currIndex == 2) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(0, -one, 0, 0);
                        } else {
                            animation = new TranslateAnimation(two, one, 0, 0);
                        }


                    }
                    break;
                case 2:
                    if (currIndex == 0) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-two, 0, 0, 0);
                        } else {
                            animation = new TranslateAnimation(offset, two, 0, 0);
                        }

                    } else if (currIndex == 1) {
                        if ("2".equals(flag_type)) {
                            animation = new TranslateAnimation(-one, 0, 0, 0);
                        } else {
                            animation = new TranslateAnimation(one, two, 0, 0);
                        }


                    }
                    break;

            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
