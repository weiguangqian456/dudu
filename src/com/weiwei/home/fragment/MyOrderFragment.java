package com.weiwei.home.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwtx.dududh.R;
import com.weiwei.salemall.adapter.MyFragmentAdapter;
import com.weiwei.salemall.fragment.OrderFragment;
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

public class MyOrderFragment extends Fragment {

    @BindView(R.id.view_pager)
    NoPreloadViewPager viewPager;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;

    private String[] titles = new String[]{"全部", "待付款", "待发货", "待收货", "待评论"};
    private List<Fragment> fragmentList = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_order_view,container,false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        viewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragmentList));
    }


    private void initMagicIndicator() {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
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
}
