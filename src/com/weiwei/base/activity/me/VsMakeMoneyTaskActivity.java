package com.weiwei.base.activity.me;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.VsContactsSelectActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsInviteItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description:赚话费任务界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsMakeMoneyTaskActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 数据内容
	 */
	private VsInviteItem vsInviteItem = null;
	/**
	 * 提示文字
	 */
	private TextView vs_task_hint_tv;
	/**
	 * 赚得时间
	 */
	private TextView vs_task_time;
	/**
	 * 累计金额
	 */
	private TextView vs_task_money;
	/**
	 * 按钮
	 */
	private Button vs_task_btn;
	/**
	 * 说明内容
	 */
	private TextView vs_task_content;
	/**
	 * 弹出选择分享方式
	 */
	private Dialog dialog_share;
	/**
	 * 移动动画
	 */
	private Animation mTranslateAnimation;
	/**
	 * 邀请移动动画
	 */
	private View v = null;
	/**
	 * 分享内容
	 */
	private String shareText = null;
	/**
	 * 分享图片链接
	 */
	private String share_imageUrl = null;
	/**
	 * 成功
	 */
	private final char MSG_ID_SUCC = 202;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_makeymoney_task_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		initView();
		initData();
		v = initAnimation();
		VsApplication.getInstance().addActivity(this);
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
	 * 初始化视图
	 */
	private void initView() {
		vs_task_hint_tv = (TextView) findViewById(R.id.vs_task_hint_tv);
		vs_task_time = (TextView) findViewById(R.id.vs_task_time);
		vs_task_money = (TextView) findViewById(R.id.vs_task_money);
		vs_task_btn = (Button) findViewById(R.id.vs_task_btn);
		vs_task_content = (TextView) findViewById(R.id.vs_task_content);
		// 设置监听事件
		vs_task_btn.setOnClickListener(this);

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		vsInviteItem = intent.getParcelableExtra("vsInviteItem");
		if (vsInviteItem != null) {
			mTitleTextView.setText(vsInviteItem.getTitle() + getResources().getString(R.string.vs_invite_title));
			showLeftNavaBtn(R.drawable.vs_title_back_selecter);
			vs_task_hint_tv.setText(vsInviteItem.getTitle() + getResources().getString(R.string.vs_invite_task_hint));
			vs_task_time.setText(vsInviteItem.getTotal_min());
			vs_task_money.setText("(" + vsInviteItem.getTotal_money() + "元)");
			vs_task_content.setText(vsInviteItem.getTips());
			vs_task_btn.setText(vsInviteItem.getBtn_name());
		} else {
			vs_task_btn.setVisibility(View.GONE);
		}
		IntentFilter filter = new IntentFilter(GlobalVariables.action_makemoney_share);
		registerReceiver(myBroadcaset, filter);
	}

	private BroadcastReceiver myBroadcaset = new BroadcastReceiver() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
			try {
				if (GlobalVariables.action_makemoney_share.equals(intent.getAction())) {
					JSONObject jsData = new JSONObject(jStr);
					if (jsData != null) {
						/*
						 * shareText = jsData.getString("shareText"); share_imageUrl =
						 * KcJsonTool.GetStringFromJSON(jsData, "share_imageurl");
						 */
						mBaseHandler.sendEmptyMessage(MSG_ID_SUCC);
					}
				}

			} catch (Exception e) {
				mBaseHandler.sendEmptyMessage(MSG_ID_SUCC);
			}
		}

	};

	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
		showDialogNow(true);// 显示对话框
	}

	/**
	 * 显示分享方式
	 * 
	 * @param show
	 *            是否显示
	 */
	public void showDialogNow(boolean show) {
		if (show) {
			v.startAnimation(mTranslateAnimation);
			dialog_share.show();
		} else {
			if (dialog_share != null && dialog_share.isShowing()) {
				dialog_share.dismiss();
			}
		}
	}

	/**
	 * 显示要求对话框
	 * 
	 * @return
	 */
	private View initAnimation() {
		dialog_share = new Dialog(mContext, R.style.CommonDialogStyle);
		final View v = View.inflate(mContext, R.layout.vs_makemoney_task_dlog, null);
		v.findViewById(R.id.tv_message).setOnClickListener(this);
		v.findViewById(R.id.tv_weixin).setOnClickListener(this);
		v.findViewById(R.id.tv_friends).setOnClickListener(this);
		v.findViewById(R.id.tv_qq).setOnClickListener(this);
		v.findViewById(R.id.btn_wait).setOnClickListener(this);
		dialog_share.setContentView(v);
		dialog_share.setCanceledOnTouchOutside(true);
		dialog_share.setCancelable(true);
		/*
		 * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window 对象,这样这可以以同样的方式改变这个Activity的属性.
		 */
		Window dialogWindow = dialog_share.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.TOP);

		/*
		 * lp.x与lp.y表示相对于原始位置的偏移. 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
		 * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略. 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
		 * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略. 当参数值包含Gravity.CENTER_HORIZONTAL时
		 * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动. 当参数值包含Gravity.CENTER_VERTICAL时
		 * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动. gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
		 * Gravity.CENTER_VERTICAL.
		 * 
		 * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
		 * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
		 */
		if (GlobalVariables.width == 0) {
			// 获取屏幕宽高与密度
			VsUtil.setDensityWH(this);
		}
		lp.x = 0; // 新位置X坐标
		lp.y = GlobalVariables.height - (int) (417.5 * GlobalVariables.density); // 新位置Y坐标
		lp.width = GlobalVariables.width; // 宽度
		lp.height = (int) (417.5 * GlobalVariables.density); // 高度
		lp.alpha = 1f; // 透明度

		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);
		mTranslateAnimation = new TranslateAnimation(0, 0, GlobalVariables.height, 0);// 移动
		mTranslateAnimation.setDuration(500);
		return v;
	}

	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		showDialogNow(false);
		switch (v.getId()) {
		case R.id.vs_task_btn:
			if (vsInviteItem != null) {
				if (vsInviteItem.getTasktype().equals("invite")) {
				} else if (vsInviteItem.getTasktype().equals("charge")) {
				}
				VsUtil.skipForSchemeNew(vsInviteItem.getJump_url(), mContext);
			}
			break;
		// // 待会再说
		// case R.id.btn_wait:
		// break;
		// 短信邀请
		case R.id.tv_message:
			Intent intent = new Intent(mContext, VsContactsSelectActivity.class);
			intent.putExtra("INVITECONTACTSENDSMS", true);
			startActivity(intent);
			break;
		// 微信邀请
		case R.id.tv_weixin:
			if (shareText != null && !"".equals(shareText)) {
				VsUtil.weixinShare(mContext, shareText, VsUtil.returnBitMap(share_imageUrl), "");
			} else {
				VsUtil.weixinShare(mContext, VsUserConfig.getDataString(VsApplication.getContext(),
						VsUserConfig.JKey_WEIXIN_SHARE_CONTENT, ""), VsUtil.returnBitMap(VsUserConfig.getDataString(
						VsApplication.getContext(), VsUserConfig.JKey_WEIXIN_SHARE_IMAGE_LOCAL_URL)), "");
			}
			break;
		// 朋友圈邀请
		case R.id.tv_friends:
			if (shareText != null && !"".equals(shareText)) {
				VsUtil.weixinShare(mContext, shareText, VsUtil.returnBitMap(share_imageUrl), "weixinquan");
			} else {
				VsUtil.weixinShare(mContext, VsUserConfig.getDataString(VsApplication.getContext(),
						VsUserConfig.JKey_WEIXINQUAN_SHARE_CONTENT, ""), VsUtil.returnBitMap(VsUserConfig
						.getDataString(VsApplication.getContext(), VsUserConfig.JKey_WEIXIN_SHARE_IMAGE_LOCAL_URL)),
						"weixinquan");
			}
			break;
		// QQ邀请
		case R.id.tv_qq:
			if (shareText != null && !"".equals(shareText)) {
				VsUtil.qqShare(mContext, shareText, VsUserConfig.getDataString(VsApplication.getContext(),
						VsUserConfig.JKey_QQDX_SHARE_IMAGE_LOCAL_URL), false);
			} else {
				VsUtil.qqShare(mContext, VsUserConfig.getDataString(VsApplication.getContext(),
						VsUserConfig.JKey_QQDX_SHARE_CONTENT, ""), VsUserConfig.getDataString(
						VsApplication.getContext(), VsUserConfig.JKey_QQDX_SHARE_IMAGE_LOCAL_URL), false);
			}
			break;
		case R.id.btn_wait:
			dialog_share.cancel();		
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (myBroadcaset != null) {
			unregisterReceiver(myBroadcaset);
		}
	}
}
