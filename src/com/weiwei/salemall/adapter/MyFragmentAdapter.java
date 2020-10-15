package com.weiwei.salemall.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Created by EDZ on 2018/6/8.
 *         viewPager + Fragment适配器
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private FragmentManager fragmetnmanager;

    public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fragmentList = list;
        this.fragmetnmanager = fm;
    }

    /**
     * 返回需要展示的fragment
     *
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * 返回需要展示的fangment数量
     *
     * @return
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
