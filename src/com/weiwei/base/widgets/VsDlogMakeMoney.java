package com.weiwei.base.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.hwtx.dududh.R;


public class VsDlogMakeMoney extends Activity {
	// 回拨提示
	/**
	 * 回拨提示布局
	 */
	private RelativeLayout vs_dlog_makemoney_layout,vs_dlog_makemoney;

	/**
	 * 赠送分钟数
	 */
	private TextView vs_dlog_number,vs_dlog_text2,vs_dlog_text4;

	private Context mContext;
	/**
	 * 关闭动画
	 */
	private AlphaAnimation aa;
	/**
	 * 缩小动画
	 */
	private ScaleAnimation mScaleAnimation2;
	/**
	 * 平移动画
	 */
	private  Animation anim;
	/**
	 * 放大动画
	 */
	private ScaleAnimation mScaleAnimation1;
	/**
	 * 赠送标题
	 */
	private TextView vs_dlog_text1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_dlog_makemoney);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		mContext = this;
		initView();
		new Handler().postDelayed(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				vs_dlog_makemoney_layout.startAnimation(aa);
			}
		}, 4000);
		VsApplication.getInstance().addActivity(this);
	}

	/**
	 * 初始化视图组件
	 */
	private void initView() {
		// 初始化控件
		vs_dlog_makemoney_layout = (RelativeLayout) findViewById(R.id.vs_dlog_makemoney_layout);
		vs_dlog_makemoney = (RelativeLayout) findViewById(R.id.vs_dlog_makemoney);
		vs_dlog_number = (TextView) findViewById(R.id.vs_dlog_number);
		vs_dlog_text2 = (TextView) findViewById(R.id.vs_dlog_text2);
		vs_dlog_text4=(TextView) findViewById(R.id.vs_dlog_text4);
		vs_dlog_text1=(TextView) findViewById(R.id.vs_dlog_text1);
		Intent intent=getIntent();
		String minutes=intent.getStringExtra("minutes");
		String balance=intent.getStringExtra("balance");
		String title=intent.getStringExtra("messagetitle");
		vs_dlog_number.setText(minutes);
		vs_dlog_text4.setText("("+balance+"元)");
		vs_dlog_text1.setText(title);
		
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
		lp.x = 0; // 新位置X坐标
		lp.y = (int)(75.5 * GlobalVariables.density); // 新位置Y坐标
		lp.width = GlobalVariables.width; // 宽度
		getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
		int www = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int hhh = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		vs_dlog_makemoney_layout.measure(www, hhh);
		int bottomLineWidth = vs_dlog_makemoney_layout.getMeasuredHeight();
		lp.height = bottomLineWidth; // 高度
		lp.alpha = 1f; // 透明度

		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		dialogWindow.setAttributes(lp);
		// 平移动画
		anim = new TranslateAnimation(0, 0, -bottomLineWidth+75.5f * GlobalVariables.density, 0);
		anim.setDuration(400);
		vs_dlog_makemoney.startAnimation(anim);
		//放大
		mScaleAnimation1 = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,// 整个屏幕就0.0到1.0的大小//缩放
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		//缩小
		mScaleAnimation2 = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,// 整个屏幕就0.0到1.0的大小//缩放
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mScaleAnimation1.setDuration(200);
		mScaleAnimation2.setDuration(200);
		mScaleAnimation1.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				vs_dlog_number.startAnimation(mScaleAnimation2);
				vs_dlog_text2.startAnimation(mScaleAnimation2);
			}
		});
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				vs_dlog_number.startAnimation(mScaleAnimation1);
				vs_dlog_text2.startAnimation(mScaleAnimation1);
			}
		});
		// 设置图片动画效果
		aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(1000);
		aa.setFillAfter(true); 
		// 内部匿名类实现动画监听，重写三个事件，我们关心的时最后一个
		aa.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			// 动画结束后，跳转到登录界面
			public void onAnimationEnd(Animation animation) {
				finish();
			}
		});
		vs_dlog_makemoney_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vs_dlog_makemoney_layout.startAnimation(aa);
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mScaleAnimation1.cancel();
		mScaleAnimation2.cancel();
		anim.cancel();
		aa.cancel();
	}
}
