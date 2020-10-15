package com.weiwei.rider.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.util.ViewPageFragment;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.rider.base.RiderBaseActivity;
import com.weiwei.rider.fragment.RiderOrderFragment;
import com.weiwei.rider.utils.IndicatorLineUtil;
import com.weiwei.salemall.bean.MessageEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RiderOrderActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tabLayout)
    XTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private String[] titles = {"待接单","待拣货","配送中","已完成"};
    private List<Fragment> fragmentList;
    private String riderId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rider_order;
    }

    @Override
    protected void initView() {
        tvTitle.setText("嘟嘟骑手");
    }

    @Override
    protected void initData() {
        riderId = getIntent().getStringExtra("riderId");
        fragmentList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            RiderOrderFragment orderFragment = RiderOrderFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("orderStatus",i);
            bundle.putString("riderId",riderId);
            orderFragment.setArguments(bundle);
            fragmentList.add(orderFragment);
        }
        viewPager.setAdapter(new ViewPageFragment(getSupportFragmentManager(),fragmentList,titles));
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.rl_back)
    public void onViewClicked(View view) {
        finish();
    }
}
