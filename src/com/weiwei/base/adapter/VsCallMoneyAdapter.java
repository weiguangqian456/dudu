package com.weiwei.base.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


		public class VsCallMoneyAdapter extends PagerAdapter {
		    private List<View> viewList;
		    
		    // 构造方法，参数是我们的页卡
		    public VsCallMoneyAdapter(List<View> viewList) {
		        this.viewList = viewList;
		    }
		    
		    // 获取当前窗体的页卡数目
		    @Override
		    public int getCount() {
		        return viewList.size();
		    }
		    @Override
		    public void destroyItem(ViewGroup container, int position, Object object) {
		        // TODO Auto-generated method stub
		        container.removeView(viewList.get(position));
		    }
		    // 实例化页卡， 初始化position位置的界面
		    @Override
		    public Object instantiateItem(ViewGroup container, int position) {
		        // TODO Auto-generated method stub
		        container.addView(viewList.get(position), 0);
		        return viewList.get(position);
		    }
		    // 判断是都由对象生成界面
		    @Override
		    public boolean isViewFromObject(View arg0, Object arg1) {
		        return arg0 == arg1; // 官方提示这么写
		    }
		}  
	