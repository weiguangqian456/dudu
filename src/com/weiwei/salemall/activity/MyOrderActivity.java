package com.weiwei.salemall.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.account.RefuelBalanceActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.home.activity.MoreRefuelActivity;
import com.weiwei.home.activity.RefuelDetailActivity;
import com.weiwei.home.activity.RefuelMainActivity;
import com.weiwei.home.activity.RefuelOrderActivity;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.base.TempAppCompatActivity;
import com.weiwei.salemall.fragment.OrderFragment;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.salemall.widget.CusViewPagerHelper;
import com.weiwei.salemall.widget.NoPreloadViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Created by EDZ on 2018/5/23.
 *         订单界面(指示器 + ViewPager + Fragment)
 */

public class MyOrderActivity extends TempAppCompatActivity {
    @BindView(R.id.rl_back)
    RelativeLayout backRl;
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.view_pager)
    NoPreloadViewPager viewPager;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    private String[] titles = new String[]{"全部", "待付款", "待发货", "待收货", "待评论"};
    private List<Fragment> fragmentList = null;
    /**
     * 订单flag
     */
    private int flag = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        initView();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent=new Intent();
            intent.setClass(MyOrderActivity.this,VsMainActivity.class);
            startActivity(intent);
            return true;//不执行父类的点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类的其他点击事件
    }


    @Override
    protected void init() {
    }

    private void initView() {
        titleTv.setText("我的订单");
        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MyOrderActivity.this,VsMainActivity.class);
                startActivity(intent);
            }
        });
        initMagicIndicator();
        initViewPager();
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString("classificationFlag", titles[i]);
            Fragment orderFragment = OrderFragment.newInstance(bundle);
            fragmentList.add(orderFragment);
        }
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragmentList));

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        viewPager.setCurrentItem(flag);
    }

    private void initMagicIndicator() {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        //ture 即标题平分屏幕宽度的模式
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(titles[index]);
                int size = (int) getResources().getDimension(R.dimen.w_14_dip);
                simplePagerTitleView.setTextSize(px2sp(context, size));
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.public_color_EC6941));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setColors(getResources().getColor(R.color.public_color_EC6941));
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        CusViewPagerHelper.bind(magicIndicator, viewPager);
//        ViewPagerHelper.bind(magicIndicator,viewPager);
    }

    public int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


}