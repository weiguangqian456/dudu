package com.weiwei.merchant.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.hwtx.dududh.R;
import com.weiwei.base.util.ViewPageFragment;
import com.weiwei.merchant.fragment.MerchantOrderFragment;
import com.weiwei.rider.base.RiderBaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MerchantOrderActivity extends RiderBaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tabLayout)
    XTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private String[] titles = {"全部","待发货","待收货","已完成"};
    private List<Fragment> fragmentList;
    private String riderId;
    public List<String> nameList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant_order;
    }

    @Override
    protected void initView() {
        tvTitle.setText("商家订单");
        nameList = getIntent().getStringArrayListExtra("username");

    }

    @Override
    protected void initData() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MerchantOrderFragment orderFragment = MerchantOrderFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putString("type",i == 0 ? "" : i+"");
            orderFragment.setArguments(bundle);
            fragmentList.add(orderFragment);
        }
        viewPager.setAdapter(new ViewPageFragment(getSupportFragmentManager(),fragmentList,titles));
        viewPager.setOffscreenPageLimit(titles.length);
        tabLayout.setupWithViewPager(viewPager);
    }
}
