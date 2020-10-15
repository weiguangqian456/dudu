package com.weiwei.base.activity.me;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.hwtx.dududh.R;

/**
 * 
 * @Title:Android客户端
 * @Description:关于界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsAboutActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 服务电话电话号码
	 */
	private TextView vs_about_server_phone,vs_about_computer_wap,vs_about_phone_wap;
	/**
	 * 服务电话号码
	 */
	private String callNumber,phone_wap,com_wap;
	/**
	 * 服务QQ
	 */
	private TextView vs_about_server_qq;
	/**
	 * 版本号
	 */
	private TextView vs_vesion;
	/**
	 * 有版本更新
	 */
	private TextView vs_about_update_tv;
	/**
	 * 更新
	 */
	private RelativeLayout vs_about_update;
	/**
	 * 帮助中心
	 */
	private RelativeLayout vs_about_help;
	/**
	 * 意见反馈
	 */
	private RelativeLayout vs_about_fk;
	/**
	 * 服务条款
	 */
	private RelativeLayout vs_about_fw;
	
	
	/**
	 * 友盟用户反馈
	 */
//	private FeedbackAgent agent = null;
	/**
	 * 升级标志
	 */
	private boolean updateFlag = false;
	/**
	 * 更新检测
	 */
	private static final char MSG_ID_SendUpgradeMsg = 71;
	/**
	 * 最后一根线
	 */
	private View line_end;
	/**
	 * 评价
	 */
	private RelativeLayout vs_about_pj;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_about_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		// 实例化友盟用户反馈
//		agent = new FeedbackAgent(mContext);
//		// 启动用户反馈通知
//		agent.sync();
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.icon_back);
		mTitleTextView.setText(R.string.vs_about_title);
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
	@SuppressLint("NewApi")
	private void init() {
		vs_about_server_phone = (TextView) findViewById(R.id.vs_about_server_phone);
		vs_about_server_qq = (TextView) findViewById(R.id.vs_about_server_qq);
		vs_vesion = (TextView) findViewById(R.id.vs_vesion);
		vs_about_update_tv = (TextView) findViewById(R.id.vs_about_update_tv);
		vs_about_update = (RelativeLayout) findViewById(R.id.vs_about_update);
		vs_about_help = (RelativeLayout) findViewById(R.id.vs_about_help);
		vs_about_fk = (RelativeLayout) findViewById(R.id.vs_about_fk);
		vs_about_fw = (RelativeLayout) findViewById(R.id.vs_about_fw);
		line_end = findViewById(R.id.line_end);
		vs_about_pj = (RelativeLayout) findViewById(R.id.vs_about_pj);
		vs_about_computer_wap=(TextView) findViewById(R.id.vs_about_computer_wap);
		vs_about_phone_wap =(TextView) findViewById(R.id.vs_about_phone_wap);

		if (GlobalVariables.SDK_VERSON > 13) {
			vs_about_server_qq.setTextIsSelectable(true);
		}
		// 设置版本号
		vs_vesion.setText(getVersion());
		callNumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServicePhone);
		String qq = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServiceQQ);
		com_wap = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_computer_wap);
		phone_wap = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_phone_wap);
		
		if (com_wap != null && com_wap.length() > 0) {
		    vs_about_computer_wap.setText(com_wap);
        } else {
            vs_about_computer_wap.setText(DfineAction.com_wap);
        }
		if (phone_wap != null && phone_wap.length() > 0) {
		    vs_about_phone_wap.setText(phone_wap);
        } else {
            vs_about_phone_wap.setText(DfineAction.phone_wap);
        }
		if (qq != null && qq.length() > 0) {
			vs_about_server_qq.setText(qq);
		} else {
			vs_about_server_qq.setText(R.string.curstomer_server_qq);
		}
		if (callNumber != null && callNumber.length() > 0) {
			//vs_about_server_phone.setText(Html.fromHtml("<u>" + callNumber + "</u>"));
		    vs_about_server_phone.setText(R.string.curstomer_server_mobile);
		} else {
			//vs_about_server_phone.setText(Html.fromHtml("<u>" + DfineAction.mobile + "</u>"));
			vs_about_server_phone.setText(R.string.curstomer_server_mobile);
		}
		vs_about_server_phone.setOnClickListener(this);
		vs_about_update.setOnClickListener(this);
		vs_about_help.setOnClickListener(this);
		vs_about_fk.setOnClickListener(this);
		vs_about_fw.setOnClickListener(this);
		vs_about_pj.setOnClickListener(this);

		// 检测更新
		if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
			vs_about_update_tv.setVisibility(View.VISIBLE);
			vs_about_update_tv.setText("有新版本");
			updateFlag = true;
		} else {
			vs_about_update_tv.setText("已是最新版本");
			vs_about_update_tv.setTextColor(getResources().getColor(R.color.vs_gray_deep));
			updateFlag = false;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vs_about_server_phone:
			showYesNoDialog(null, "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打客服热线", DfineAction.RES.getString(R.string.product) + "拨打",
					getResources().getString(R.string.phone_call), new OkBtnListener(callNumber),
					new CancelBtnListener(callNumber), null);
			break;
		case R.id.vs_about_update:
			if (!updateFlag) {
				loadProgressDialog(getResources().getString(R.string.upgrade_checking_version));
				mBaseHandler.sendEmptyMessageDelayed(MSG_ID_SendUpgradeMsg, 1000);
			} else {
				mBaseHandler.sendEmptyMessage(MSG_ID_SendUpgradeMsg);
			}
			break;
		case R.id.vs_about_help:
			VsUtil.startActivity("3019", mContext, null);
			break;
		case R.id.vs_about_fk:
//			agent.startFeedbackActivity();
			break;
		case R.id.vs_about_fw:
			final String urlTo = "file:///android_asset/service_terms.html";
			Intent intent_fw = new Intent();
			intent_fw.setClass(mContext, WeiboShareWebViewActivity.class);
			String[] aboutBusiness = new String[] {
					mContext.getString(R.string.welcome_main_clause),
					"service", urlTo };
			intent_fw.putExtra("AboutBusiness", aboutBusiness);
			startActivity(intent_fw);
			break;
		case R.id.vs_about_pj:
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
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
		case MSG_ID_SendUpgradeMsg:
			dismissProgressDialog();
			if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
				startUpdateApk();
			} else {
				mToast.show("您的" + DfineAction.RES.getString(R.string.product) + "已是最新版本，无需升级！", Toast.LENGTH_SHORT);
			}
			break;
		default:
			break;
		}

	}

	private class OkBtnListener implements DialogInterface.OnClickListener {
		private String callNumber = "";

		public OkBtnListener(String callNumber) {
			this.callNumber = callNumber;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// finish();
			// KcUtil.skipForTarget("000", mContext, 0, null);
			// 拨打客服号码
			VsUtil.callNumber("客服电话", callNumber, "深圳", mContext, "", false,getFragmentManager());
			/*
			 * Intent intent1 = new Intent(); intent1.putExtra("callNumber", callNumber);
			 * intent1.setAction(GlobalVariables.action_setcallphont); sendBroadcast(intent1);
			 */
		}
	}

	private class CancelBtnListener implements DialogInterface.OnClickListener {
		private String callNumber = "";

		public CancelBtnListener(String callNumber) {
			this.callNumber = callNumber;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			VsUtil.LocalCallNumber(mContext, callNumber);
		}
	}

	/**
	 * 开始更新
	 */
	private void startUpdateApk() {
		VsUpdateAPK mUpdateAPK = new VsUpdateAPK(mContext);
//		 mUpdateAPK.DowndloadThread(VsUserConfig.getDataString(mContext,VsUserConfig.JKey_UpgradeUrl), true);
		mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl), true, null);
	}

	/**
	 * 判断是否登录
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isLogin() {
		boolean retbool = true;
		if (!VsUtil.checkHasAccount(mContext)) {
			retbool = false;
		} else {
			retbool = true;
		}
		return retbool;
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return "V" + version;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getString(R.string.version_unkown);
		}
	}
}
