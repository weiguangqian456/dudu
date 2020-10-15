package com.weiwei.salemall.widget;

import net.lucode.hackware.magicindicator.MagicIndicator;

/**
 * @author Created by EDZ on 2018/8/9.
 *         禁止预加载ViewPager 与Indicator绑定
 */

public class CusViewPagerHelper {
    public CusViewPagerHelper() {
    }

    public static void bind(final MagicIndicator magicIndicator, NoPreloadViewPager viewPager) {
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }
}
