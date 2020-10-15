package com.weiwei.base.activity.setting;

import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @Title:Android客户端
 * @Description: 来电显示操作
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsCallerIdentificationActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 来显开关
	 */
	private ImageButton cid_open_close;
	/**
	 * /** 查来显
	 */
	private static final char OPERATE_SEARCH = 1;
	/**
	 * 开来显
	 */
	private static final char OPERATE_OPEN = 2;
	/**
	 * 关来显
	 */
	private static final char OPERATE_STOP = 5;
	/**
	 * 请求类型
	 */
	private char requettype;
	/**
	 * 执行操作失败
	 */
	private static final char MSG_ID_OPERATE_FAILED = 12;
	/**
	 * 执行操作成功
	 */
	private static final char MSG_ID_OPERATE_SUCCESS = 13;
	/**
	 * 来显开通关闭状态 true已开通、false未开通
	 */
	private boolean cidFlag = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weiwei.base.activity.KcBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_account_cid_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		// showLeftTxtBtn(getResources().getString(R.string.account_title));
		mTitleTextView.setText(R.string.account_cid_tv_str);
		initView();
		initData();
		VsApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
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
		// 获取控件对象
		cid_open_close = (ImageButton) findViewById(R.id.cid_open_close);
		// 设置监听事件
		cid_open_close.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 来显操作
		operateCallDisplay("search");
	}

	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		// TODO Auto-generated method stub\
		switch (v.getId()) {
		case R.id.cid_open_close:// 来显开关
			if (!VsUtil.isNoNetWork(mContext)) {
				if (cidFlag) {
					operateCallDisplay("stop");
				} else {
					operateCallDisplay("open");
				}
			} else {
				mToast.show(getString(R.string.not_network_connon_msg), Toast.LENGTH_SHORT);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 发送来显操作广播
	 * 
	 * @param bundle
	 */
	private void sendRequestService(final String operate) {
		unregisterKcBroadcast();
		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVariables.actionOPENSERVICE);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("operate", operate);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_CID_SERVER,
                DfineAction.authType_UID, treeMap, GlobalVariables.actionOPENSERVICE);
	}

	/**
	 * 操作来显的方法
	 * 
	 * @param operate
	 */
	private void operateCallDisplay(String operate) {
		String msg = "";
		if ("search".equals(operate)) {
			requettype = OPERATE_SEARCH;
			msg = getString(R.string.loading_special_info);
		} else if ("open".equals(operate)) {
			requettype = OPERATE_OPEN;
			msg = getString(R.string.call_display_turnningmsg);
		} else if ("stop".equals(operate)) {
			requettype = OPERATE_STOP;
			msg = getString(R.string.turnning_off_msg);
		}
		loadProgressDialog(msg, false);
		sendRequestService(operate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weiwei.base.activity.KcBaseActivity#handleBaseMessage(android.os.Message)
	 */
	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case MSG_ID_OPERATE_FAILED:// 操作失败
			mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
			break;
		case MSG_ID_OPERATE_SUCCESS:// 操作成功
			// mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weiwei.base.activity.KcBaseActivity#handleKcBroadcast(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);
		dismissProgressDialog();// 销毁等待界面
		String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
		Message msg = mBaseHandler.obtainMessage();
		Bundle bundle = new Bundle();
		JSONObject jData;
		try {
			jData = new JSONObject(jStr);
			int result = Integer.parseInt(jData.getString("result"));
			if (result != 0) {
				if (result == -99) {
					if (!VsUtil.isCurrentNetworkAvailable(mContext))
						return;
				}
				bundle.putString("msg", jData.getString("reason"));
				msg.what = MSG_ID_OPERATE_FAILED;
			}

			int status = Integer.parseInt(jData.getString("status"));// 来显状态
			if (requettype == OPERATE_SEARCH) {
				boolean isbtnen;
				if (0 != status) {
					isbtnen = true;
					VsUserConfig.setData(mContext, VsUserConfig.JKey_IsHadOpenedCallDispaly, true);
					VsUserConfig.setData(mContext, VsUserConfig.JKey_ValidityTime, jData.getString("renewal_time"));
				} else {
					isbtnen = false;
				}
				callDisplayState(status, isbtnen);
				return;
			} else if (requettype == OPERATE_OPEN) {
				VsUserConfig.setData(mContext, VsUserConfig.JKey_IsHadOpenedCallDispaly, true);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_ValidityTime, jData.getString("renewal_time"));// 来显有效期
				callDisplayState(status, true);// 处理显示界面
				bundle.putString("msg", getResources().getString(R.string.call_display_haveopen));
				msg.what = MSG_ID_OPERATE_SUCCESS;
			} else if (requettype == OPERATE_STOP) {
				callDisplayState(status, false);
				bundle.putString("msg", getResources().getString(R.string.call_display_haveclose));
				msg.what = MSG_ID_OPERATE_SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (requettype == OPERATE_OPEN) {
				bundle.putString("msg", getResources().getString(R.string.call_display_openfail));
				msg.what = MSG_ID_OPERATE_FAILED;
			} else if (requettype == OPERATE_STOP) {
				bundle.putString("msg", getResources().getString(R.string.call_display_closefail));
				msg.what = MSG_ID_OPERATE_FAILED;
			}
		}
		msg.setData(bundle);
		mBaseHandler.sendMessage(msg);
	}

	/**
	 * 去电显示状态，0：关闭1：开启2：未开通但已购买
	 */
	private void callDisplayState(int status, boolean flagBtn) {

		// 显示去电显示状态
		switch (status) {
		case 0:// 当前未开通来显
			if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_IsHadOpenedCallDispaly, false)) {// 之前开通过来显
				cid_open_close.setBackgroundResource(R.drawable.vs_switch_close);
				cidFlag = false;
				break;
			} else {// 未开通过来显
				cidFlag = false;
				cid_open_close.setBackgroundResource(R.drawable.vs_switch_close);
				return;
			}
		case 1:// 当前已开通来显
			cidFlag = true;
			cid_open_close.setBackgroundResource(R.drawable.vs_switch_open);
			break;
		case 2:// 当前未开通来显，但是用户已经购买
			cidFlag = false;
			cid_open_close.setBackgroundResource(R.drawable.vs_switch_close);
			break;
		default:
			break;
		}

	}

	/**
	 * 设置imageview false:不可用,true:可用
	 * 
	 * @param mAutoRenewalSwitch
	 */
	private void setImageViewEnabled(ImageView image, boolean bool) {
		image.setEnabled(bool);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.weiwei.base.activity.KcBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
