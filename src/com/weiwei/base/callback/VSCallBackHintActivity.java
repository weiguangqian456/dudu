package com.weiwei.base.callback;

import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.setting.VsChangePhoneActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.softphone.AudioPlayer;
import com.hwtx.dududh.R;


/**
 * 
 * @Title:回拨界面
 * @Description: 处理回拨请求
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-9
 */
public class VSCallBackHintActivity extends VsBaseActivity {

	private static final long CALL_BACK_LISTENER_TIME = 15000;
	private static final String TAG = VSCallBackHintActivity.class
			.getSimpleName();
	/**
	 * 被叫名称
	 */
	private TextView mCalledNameTV;
	/**
	 * 被叫号码
	 */
	private TextView mCalledNumberTV;
	/**
	 * 被叫归属地
	 */
	private TextView mCalledLocalTV;

	/**
	 * 以广告图做背景
	 */
	private RelativeLayout mAdRL;

	/**
	 * 是否是当前Activity
	 */
	private boolean isResume = false;

	private final char MSG_CLOSE_DLG = 0;
	private final char MSG_MESSAGE_DIALOG = 3;
	private final int MSG_BIGSDK = 23;
	/**
	 * 联系人名称
	 */
	private String callName;
	/**
	 * 电话号码
	 */
	private String callNumber;
	/**
	 * 归属地
	 */
	private String callLocal = "未知";
	/**
	 * 回拨成功
	 */
	public final char MSG_LOGIN_OK = 51;
	/**
	 * 回拨失败
	 */
	public final char MSG_LOGIN_FAIL = 52;
	private final char MSG_UNREGISTER_CALL = 5;
	/**
	 * 余额不足
	 */
	private final char MSG_CALLBACK_MONERY = 55;
	/**
	 * TOKEN错误
	 */
	private final char MSG_TOKEN_ERROR = 62;
	/**
	 * 网络连接失败
	 */
	private final char MSG_CALLBACK_NETFAIL = 56;
	/**
	 * 没有绑定
	 */
	private final char MSG_CALLBACK_NO_BIND = 57;

	public boolean flag = false;
	private boolean isVs = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_callback_hint_layout);
		// 如果是回拨结束返回该界面就直接退出
		if (getIntent().getBooleanExtra("after_auto_answer_exit", false)) {
			Intent i = new Intent(
					GlobalVariables.action_dismiss_calling_float_view);
			i.setPackage(mContext.getPackageName());
			sendBroadcast(i);
			finish();
			return;
		}

		checkFloatViewPrmiss();

		initView();
		VsApplication.getInstance().addActivity(this);
	}

	/**
	 * 检查该系统是否有权限弹出浮框
	 * 
	 * @version:2014年12月26日
	 * @author:Jiangxuewu
	 *
	 */
	private void checkFloatViewPrmiss() {

		System.out.println(android.os.Build.MODEL);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		isResume = true;
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method sthub
		isResume = false;
		super.onPause();
		
	}

	@Override
	public void onBackPressed() {
		Log.e(TAG, "onBackPressed");
		stopListeneCall();
		handler = null;
		super.onBackPressed();
	}

	public void onClick_ReturnWait(View view) {
		stopListeneCall();
		VSCallBackHintActivity.this.finish();
	}

	/**
	 * 初始化视图组件
	 */
	private void initView() {
		// 初始化控件
		mCalledNameTV = (TextView) findViewById(R.id.vs_callback_call_to_name);
		mCalledNumberTV = (TextView) findViewById(R.id.vs_callback_call_to_number);
		mCalledLocalTV = (TextView) findViewById(R.id.vs_callback_call_to_local);
		mAdRL = (RelativeLayout) findViewById(R.id.vs_callback_ad_bg);

		Bitmap ad = getAdBitmap();
		if (null != ad) {
			mAdRL.setBackgroundDrawable(toDrawable(ad));
		} else {
			mAdRL.setBackgroundResource(R.drawable.vs_callphone_bg);
		}

		// 获取数据
		Intent intent = getIntent();
		callName = intent.getStringExtra("callName");
		callNumber = intent.getStringExtra("callNumber");
		callLocal = intent.getStringExtra("localName");
		if (TextUtils.isEmpty(callName)) {
			callName = callNumber;
		}
		mCalledNameTV.setText(callName);

		if (TextUtils.isEmpty(callNumber)) {
			callNumber = "";
		}
		mCalledNumberTV.setText(callNumber);

		// 判断拨打号码或联系人是否是好友
		if (callNumber != null && callNumber.length() >= 11) {
			if (VsUtil.checheNumberIsVsUser(mContext, callNumber)) {
				isVs = true;
			} else {
				isVs = false;
			}
		}

		// 获取归属地
		if (!TextUtils.isEmpty(callLocal)) {
			callLocal = VsLocalNameFinder.findLocalName(callNumber, false,
					mContext);
		}
		mCalledLocalTV.setText(callLocal);

		// 播放提示音
		AudioPlayer.getInstance().startRingBefore180Player(
				R.raw.vs_callback_raing, false);
		// 注册回拨广播
		registerKcBroadcast();
		placecall(callNumber, callName, callLocal);
	}

	/**
	 * Bitmap to Drawable
	 * 
	 * @param bm
	 * @return
	 * @version:2014年12月24日
	 * @author:Jiangxuewu
	 *
	 */
	private Drawable toDrawable(Bitmap bm) {

		return new BitmapDrawable(bm);
	}

	/**
	 * 获取广告图片
	 * 
	 * @return
	 * @version:2014年12月24日
	 * @author:Jiangxuewu
	 *
	 */
	private Bitmap getAdBitmap() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 注册广播
	 */
	public void registerKcBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVariables.actionCallBackF);
		mContext.registerReceiver(callBackBroadReceiverF, filter);
	}

	/**
	 * 设置是否为framgement发起
	 * 
	 * @param flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (null == handler) {
				return false;
			}
			switch (msg.what) {

			case MSG_LOGIN_OK:
				if (VsUserConfig.getDataBoolean(VSCallBackHintActivity.this,
						VsUserConfig.JKey_CALL_ANSWER_SWITCH, true)) {
					startListenAndAnswer();// 自动接听
				}
				break;
			case MSG_MESSAGE_DIALOG:
				boolean QIHOO_APK_EXIST = VsUtil.isMobile_APKExist(
						VSCallBackHintActivity.this, "com.qihoo360.mobilesafe");
				boolean QQ_APK_EXIST = VsUtil.isMobile_APKExist(
						VSCallBackHintActivity.this, "com.tencent.qqphonebook");
				if (QIHOO_APK_EXIST || QQ_APK_EXIST) {
					Intent intent = new Intent(VSCallBackHintActivity.this,
							CustomDialogActivity.class);
					intent.putExtra("business", "CallDialog");
					startActivity(intent);
				}
				break;
			case MSG_CLOSE_DLG:
				stopListeneCall();
				VSCallBackHintActivity.this.finish();
				break;
			case MSG_UNREGISTER_CALL:// 15秒后只停止自动接听
				stopAutoAnswer();
				break;
			case MSG_LOGIN_FAIL:
				Toast.makeText(VSCallBackHintActivity.this,
						msg.getData().getString("msg"), Toast.LENGTH_SHORT)
						.show();
				handler.sendEmptyMessageDelayed(MSG_CLOSE_DLG, 5000);
				break;
			case MSG_BIGSDK:
				try {
					PhoneUtils.answerRingingCall(VSCallBackHintActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case MSG_CALLBACK_NO_BIND:// 没有绑定

				AudioPlayer.getInstance().stopRingBefore180Player();

				showYesNoDialog("温馨提示", "回拨需要绑定手机才能使用！",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(mContext,
										VsChangePhoneActivity.class);
								// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("comeflag", "callBind");
								startActivity(intent);
								VSCallBackHintActivity.this.finish();

							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								VSCallBackHintActivity.this.finish();

							}
						}, null);
				break;
			case MSG_TOKEN_ERROR:// 余额不足
				showMessageDialog_token("温馨提示", "您的账号已经在另外一个设备上登陆，请您重新登陆", false);
				break;
			case MSG_CALLBACK_MONERY:// 余额不足
				if (VsUserConfig.getDataBoolean(mContext,
						VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {// 判断语音提醒是否打开
					AudioPlayer.getInstance().startRingBefore180Player(
							R.raw.vs_no_money, false);// 播放语音提醒
				}
				VsUtil.showYesNoDialogM(mContext, mContext.getResources()
						.getString(R.string.callphone_monery_title), mContext
						.getResources()
						.getString(R.string.callphone_monery_msg),
						getResources().getString(R.string.vs_gain_money),
						getResources().getString(R.string.vs_cannel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								VsUtil.startActivity("0402", mContext, null);
								AudioPlayer.getInstance()
										.stopRingBefore180Player();
								VSCallBackHintActivity.this.finish();
								dialog.cancel();
							}
						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								AudioPlayer.getInstance()
										.stopRingBefore180Player();
								VSCallBackHintActivity.this.finish();
								dialog.cancel();
							}
						});
				break;
			case MSG_CALLBACK_NETFAIL:
				VsUtil.showYesNoDialog(
						mContext,
						getResources().getString(
								R.string.callphone_callback_fail_title),
						getResources().getString(
								R.string.callphone_callback_fail_msg),
						getResources().getString(
								R.string.callphone_callback_fail_cannel),
						getResources().getString(
								R.string.callphone_callback_fail_sys),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								VsUtil.LocalCallNumber(mContext, callNumber);
								dialog.cancel();
							}
						});
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AudioPlayer.getInstance().stopRingBefore180Player();// 释放资源
		if (callBackBroadReceiverF != null) {
			//尝试解除广播
			try {
				unregisterReceiver(callBackBroadReceiverF);
			}catch (Exception ex){
				ex.printStackTrace();
			}
			callBackBroadReceiverF = null;
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 启动监听
	 */
	private void startListenAndAnswer() {

		// 广播通知服务开始监听来电
		Intent i = new Intent(GlobalVariables.action_start_listen_system_phone);
		i.putExtra("callName", callName);
		i.putExtra("callNumber", callNumber);
		i.putExtra("localName", callLocal);
		i.setPackage(mContext.getPackageName());
		sendBroadcast(i);

		Message msg = new Message();
		msg.what = MSG_UNREGISTER_CALL;
		handler.sendMessageDelayed(msg, CALL_BACK_LISTENER_TIME);// 只听15秒
	}

	private void stopListeneCall() {
		Intent i = new Intent(GlobalVariables.action_stop_listen_system_phone);
		i.setPackage(mContext.getPackageName());
		sendBroadcast(i);
	}

	private void stopAutoAnswer() {
		Intent i = new Intent(GlobalVariables.action_stop_auto_answer);
		sendBroadcast(i);
		if (isResume) {
			VSCallBackHintActivity.this.finish();
		}
	}

	/**
	 * 回拨执行反馈广播
	 */

	private BroadcastReceiver callBackBroadReceiverF = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			String action = intent.getAction();
			String callStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
			if(handler == null){ return; }
			Message message = handler.obtainMessage();
			Bundle bundle = new Bundle();
			String msg = null;
			try {
				if (GlobalVariables.actionCallBackF.endsWith(action)) {
					JSONObject callData = new JSONObject(callStr);
					String callResult = callData.getString("code");
					if (callResult.equals("0")) {
						String myNumber = VsUtil.GetStringFromJSON(callData,
								"caller");
						if (myNumber.length() > 0) {
							if ("true".equals(VsUtil.GetStringFromJSON(
									callData, "check"))) {
								VsUserConfig
										.setData(context,
												VsUserConfig.JKey_PhoneNumber,
												myNumber);
							}
						}
						message.what = MSG_LOGIN_OK;
						addCallog();
					} else if (callResult.equals("14")) {// 没有绑定
						message.what = MSG_CALLBACK_NO_BIND;
					} else if (callResult.equals("16")) {
						message.what = MSG_CALLBACK_MONERY;
					} else if (callResult.equals("1003")) {
						message.what = MSG_TOKEN_ERROR;
					} else {
						if (callResult.equals("-99")) {
							if (!VsUtil.isCurrentNetworkAvailable(mContext))
								return;
							message.what = MSG_CALLBACK_NETFAIL;
						}
						msg = callData.getString("msg");
						message.what = MSG_LOGIN_FAIL;
						bundle.putString("msg", msg);
					}
				}

			} catch (Exception e) {
				msg = mContext.getResources()
						.getString(R.string.servicer_wrong);
				bundle.putString("msg", msg);
				message.what = MSG_LOGIN_FAIL;
			}
			message.setData(bundle);
			handler.sendMessage(message);
		}
	};

	/**
	 * 添加通话记录
	 */
	private void addCallog() {
		VsCallLogItem callLogItem = new VsCallLogItem();
		if (VsUtil.isNull(callName)) {
			callLogItem.callName = callNumber;
		} else {
			callLogItem.callName = callName;
		}
		callLogItem.callNumber = callNumber;
		callLogItem.local = callLocal;
		callLogItem.calltimestamp = System.currentTimeMillis();
		callLogItem.calltimelength = "2";
		callLogItem.ctype = "2";
		callLogItem.directCall = 2; // 添加通话记录
		callLogItem.isVs = isVs;
		VsBizUtil.getInstance().addCallLog(mContext, callLogItem);
	}

	/**
	 * 执行回拨
	 */
	public void placecall(String mCalledNumber, String mCallName,
			String mCallLocal) {
		// 向服务器发送数据请求
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("callee", mCalledNumber);
		treeMap.put("uid",
				VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		CoreBusiness.getInstance().startThread(mContext,
				GlobalVariables.INTERFACE_CALLBACK, DfineAction.authType_UID,
				treeMap, GlobalVariables.actionCallBackF);
		// }
	}
}
