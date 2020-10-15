package com.weiwei.base.activity.more;

import android.os.Bundle;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.hwtx.dududh.R;

public class VsMiniGameActivity extends VsBaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_minigame);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		mTitleTextView.setText(R.string.minigame_title);
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

}
