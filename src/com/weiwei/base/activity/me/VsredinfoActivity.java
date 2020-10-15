package com.weiwei.base.activity.me;

import android.os.Bundle;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description:关于界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: Sundy
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsredinfoActivity extends VsBaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_redinfo_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.icon_back);
		mTitleTextView.setText("绑定微信步骤");
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
