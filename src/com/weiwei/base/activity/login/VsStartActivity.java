package com.weiwei.base.activity.login;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:Android客户端
 * @Description: 注册与登录选择界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsStartActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * logo布局
	 */
	private RelativeLayout vs_start_logo_layout;
	/**
	 * 注册按钮
	 */
	private Button vs_register_btn;
	/**
	 * 登录按钮
	 */
	private Button vs_login_btn;
	/**
	 * 提示
	 */
	private TextView vs_start_hint;
	/**
	 * 注册按钮布局
	 */
	private FrameLayout vs_register_btn_layout;
	/**
	 * 登录按钮布局
	 */
	private FrameLayout vs_login_btn_layout;
	/**
	 * 显示logo
	 */
	private final char MSG_ID_LOGO_SHOW = 100;
	/**
	 * 显示注册按钮
	 */
	private final char MSG_ID_REGBTN_SHOW = 101;
	/**
	 * 显示登录按钮
	 */
	private final char MSG_ID_LOGBTN_SHOW = 102;
	/**
	 * 动画
	 */
	private Animation anim = null;
	private Animation anim1 = null;
	private Animation anim2 = null;
	private Animation anim3 = null;
	private Animation alphaAnimation = null;
	private Animation alphaAnimation2 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_start_layout);
		initView();// 初始化视图
		VsApplication.getInstance().addActivity(this);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		vs_start_logo_layout = (RelativeLayout) findViewById(R.id.vs_start_logo_layout);
		vs_register_btn = (Button) findViewById(R.id.vs_register_btn);
		vs_login_btn = (Button) findViewById(R.id.vs_login_btn);
		vs_register_btn_layout = (FrameLayout) findViewById(R.id.vs_register_btn_layout);
		vs_login_btn_layout = (FrameLayout) findViewById(R.id.vs_login_btn_layout);
		vs_start_hint = (TextView) findViewById(R.id.vs_start_hint);
		// 设置监听事件
		vs_register_btn.setOnClickListener(this);
		vs_login_btn.setOnClickListener(this);
		vs_start_logo_layout.setVisibility(View.INVISIBLE);
		
		CustomLog.i("pos", "屏幕宽:" + GlobalVariables.width + ",高:" + GlobalVariables.height);
		if (GlobalVariables.height == 0) {
			// 获取屏幕宽高与密度
			VsUtil.setDensityWH(this);
		}
		
		vs_start_hint.setText(R.string.vs_start_hint1);
		vs_start_hint.append(Html.fromHtml("<font color=#ff7e00>" + getResources().getString(R.string.vs_start_hint2) + "</font>"));
		vs_start_hint.append(getResources().getString(R.string.vs_start_hint3));
		
		mBaseHandler.sendEmptyMessageDelayed(MSG_ID_LOGO_SHOW, 200);
		// logo动画
		anim = new TranslateAnimation(0, 0, 50 * GlobalVariables.density, 0);
		anim.setDuration(800);
		// 第一个按钮平移动画
		anim2 = new TranslateAnimation(0, 0, 100 * GlobalVariables.density, 0);
		anim2.setDuration(500);
		// 第一个按钮创建渐变动画
		alphaAnimation = new AlphaAnimation(0f, 1.0f);
		// 设置动画持续时间
		alphaAnimation.setDuration(490);
		// 第二个按钮平移动画
		anim3 = new TranslateAnimation(0, 0, 100 * GlobalVariables.density, 0);
		anim3.setDuration(400);
		// 第二个按钮创建渐变动画
		alphaAnimation2 = new AlphaAnimation(0f, 1.0f);
		// 设置动画持续时间
		alphaAnimation2.setDuration(390);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vs_register_btn:// 注册
			startActivity(mContext, VsRegisterActivity.class);
			break;
		case R.id.vs_login_btn:// 登录
			startActivity(mContext, VsLoginActivity.class);
			break;
		default:
			break;
		}
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case MSG_ID_LOGO_SHOW:
			// 平移动画
			vs_start_logo_layout.setVisibility(View.VISIBLE);
			vs_start_logo_layout.startAnimation(anim);
			mBaseHandler.sendEmptyMessageDelayed(MSG_ID_REGBTN_SHOW, 300);
			break;
		case MSG_ID_REGBTN_SHOW:
			vs_register_btn.setVisibility(View.VISIBLE);
			vs_register_btn_layout.startAnimation(anim2);
			// 开始动画
			vs_register_btn.startAnimation(alphaAnimation);
			mBaseHandler.sendEmptyMessageDelayed(MSG_ID_LOGBTN_SHOW, 100);
			break;
		case MSG_ID_LOGBTN_SHOW:
			vs_login_btn.setVisibility(View.VISIBLE);
			vs_login_btn_layout.startAnimation(anim3);
			// 开始动画
			vs_login_btn.startAnimation(alphaAnimation2);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (anim != null) {
			anim.cancel();
		}
		if (anim1 != null) {
			anim1.cancel();
		}
		if (anim2 != null) {
			anim2.cancel();
		}
		if (anim3 != null) {
			anim3.cancel();
		}
		if (alphaAnimation != null) {
			alphaAnimation.cancel();
		}
		if (alphaAnimation2 != null) {
			alphaAnimation2.cancel();
		}
	}
}
