package com.weiwei.base.util;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class ViewPageFragment extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private String[] titles;
    public ViewPageFragment(FragmentManager fm, List<Fragment> fragments,String[] titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = null;
        fragment = (Fragment) super.instantiateItem(container,position);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
