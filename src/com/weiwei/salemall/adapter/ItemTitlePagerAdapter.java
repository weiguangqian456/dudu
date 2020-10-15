package com.weiwei.salemall.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Created by ED
 *         item页ViewPager的内容适配器
 */
public class ItemTitlePagerAdapter extends FragmentPagerAdapter {
    private String[] titleArray;
    private List<Fragment> fragmentList;
    private String productNo;
    private String columnId;
    private String seckill;  //抢购专区标志位
    private String seckillProductId;
    private String type;

    public ItemTitlePagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titleArray, String productNo, String columnId,String seckill,String seckillProductId) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleArray = titleArray;
        this.productNo = productNo;
        this.columnId = columnId;
        this.seckill = seckill;
        this.seckillProductId = seckillProductId;
    }

    public ItemTitlePagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titleArray, String productNo, String columnId,String seckill,String seckillProductId,String type) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleArray = titleArray;
        this.productNo = productNo;
        this.columnId = columnId;
        this.seckill = seckill;
        this.seckillProductId = seckillProductId;
        this.type = type;
    }

    public void setFramentData(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

    public void setTitleData(String[] titleArray) {
        this.titleArray = titleArray;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    @Override
    public int getCount() {
        return titleArray.length;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("productNo", productNo);
        bundle.putString("columnId", columnId);
        bundle.putString("seckill", seckill);
        bundle.putString("seckillProductId", seckillProductId);
        bundle.putString("type", type);
        fragmentList.get(position).setArguments(bundle);
        return fragmentList.get(position);
    }
}
