package com.weiwei.base.activity.setting;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.login.VsStartActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.netphone.VsMainActivity;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @Title:Android客户端
 * @Description: 升级界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 石云升
 * @version: 1.0.0.0
 * @Date: 2014-9-16
 */
public class VsUpdateActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 升级图片
	 */
	private ImageView vs_update_img;
	/**
	 * 升级按钮
	 */
	private RoundTextView vs_update_btn;
	/**
	 * 跳过
	 */
	private TextView vs_update_jump;
	private String imgUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_update_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		Intent intent = getIntent();
		imgUrl = intent.getStringExtra("imgUrl");
		init();
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

	/**
	 * 初试化
	 */
	private void init() {
		vs_update_img = (ImageView) findViewById(R.id.vs_update_img);
		vs_update_btn = (RoundTextView) findViewById(R.id.vs_update_btn);
		vs_update_jump = (TextView) findViewById(R.id.vs_update_jump);
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 2;
		// Bitmap bm = BitmapFactory.decodeFile(imgUrl, options);
		Drawable drawable = BitmapDrawable.createFromPath(imgUrl);
		vs_update_img.setBackgroundDrawable(drawable);
		vs_update_jump.setOnClickListener(this);
		vs_update_btn.setOnClickListener(this);
		if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeMandatory).equals("force")) {
			vs_update_jump.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vs_update_jump:
			if (VsUtil.checkHasAccount(mContext)) {
				startActivity(this, VsMainActivity.class);
			} else {
				startActivity(this, VsMainActivity.class);
			}
			finish();
			break;
		case R.id.vs_update_btn:
			VsUpdateAPK mUpdateAPK = new VsUpdateAPK(mContext);
			mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl), true,
					null);
			break;
		}
	}
}
