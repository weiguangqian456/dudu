/*
TutorialRegistration.java
Copyright (C) 2010  Belledonne Communications SARL 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.weiwei.softphone;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.LogCollectionUploadState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCore.RemoteProvisioningState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;


import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.common.VsMd5;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.service.VsCallNotificationService;
import com.hwtx.dududh.R;
import com.zte.functions.ad.AdDataView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This program is a _very_ simple usage example of liblinphone. Demonstrating
 * how to initiate a SIP registration from a sip uri identity passed from the
 * command line.
 *
 * First argument must be like sip:jehan@sip.linphone.org, second must be
 * password. <br>
 * ex registration sip:jehan@sip.linphone.org secret
 *
 * Ported from registration.c
 *
 * @author Guillaume Beraudo
 *
 */
public class TutorialRegistrationCall extends VsBaseActivity implements
		LinphoneCoreListener, OnClickListener {
	private boolean running;
	private static TutorialRegistrationCall instance;
	private static LinphoneCore lc;
	private LinphoneCall call;
	private static String defaultSipAddress = null;
	private static String defaultSipPassword = null;
	// private static final String defaultSipAddress =
	// "sip:3710001@120.24.181.188:518;brand=lltong";
	// private static final String defaultSipPassword =
	// "d8578edf8458ce06fbc5bb76a58c5ca4";
//	private static final String defaultSipip = "139.129.19.234:518";
	private static  String defaultSipip ;

	private Button vs_call_miro, vs_call_speak, vs_call_end;
	private Handler mHandler = new Handler();
	private String callphone;
	private boolean isMicMuted = false;
	private boolean isSpeakerEnabled = false;
	private boolean isAddCall = false;
	/**
	 * 更新拨打时间
	 */
	private final char CALL_TEMI = 50;
	private final char CALL_END = 51;
	private final char CALL_RING = 52;
	/**
	 * 通话时间
	 */
	private String callTime = null;
	/**
	 * 拨打时间
	 */
	private long duration = 0;
	/**
	 * 拨打联系人姓名/电话号码
	 */
	private TextView vs_callphone_callName;
	/**
	 * 归属地
	 */
	private TextView vs_callphone_local;
	/**
	 * 通话时间与拨打提示
	 */
	private TextView vs_callphone_calltime;
	/**
	 * 联系人名称
	 */
	private String mCalledName;
	/**
	 * 号码
	 */
	private String mCalledNumber;
	private String callCalledNumber;
	/**
	 * 归属地
	 */
	private String mlocalname;
	/**
	 * 挂断
	 */
	private Button vs_callphone_endbtn;
	/**
	 * 右边按钮
	 */
	private Button vs_callphone_rightbtn;
	/**
	 * 静音
	 */
	private ImageButton vs_callphone_voice_btn;
	/**
	 * 免提
	 */
	private ImageButton vs_callphone_hf_btn;
	/**
	 * 键盘
	 */
	private ImageButton vs_callphone_keyboard_btn;
	private LinearLayout vs_callphone_button_layout;
	/**
	 * 拨号盘布局
	 */
	private RelativeLayout vs_callphone_keyboard_layout;
	/**
	 * 键盘显示隐藏标记
	 */
	private boolean keypadOpen = false;
	/**
	 * DTMF输入框
	 */
	private EditText vs_callphone_edit;
	/**
	 * 金额
	 */
	private String mCalleMoney;
	private String errorflag = "false";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_linphone_calling);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			callphone = bundle.getString("phone");
		}

		String vsid = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_KcId);
		String pwd = VsMd5.md5(getResources().getString(R.string.brandid) + VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_KcId));
		Boolean flag = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_THIRDCALLVALUE, false);
		if (flag) {
			String ip = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_THIRDCALLIP, "");
			String port = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_THIRDCALLPORT, "");
			String account = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_THIRDCALLACCOUNT, "");
			String paw = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_THIRDCALLPASSWORD, "");
			defaultSipip = ip + ":" + port;// "120.24.181.188:518");
			defaultSipAddress = "sip:" + account + "@" + defaultSipip + ";" + "brand="+ getResources().getString(R.string.brandid);
			defaultSipPassword = paw;
		}else {
			defaultSipip = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Driect_Call, "120.24.181.188:518");
			defaultSipAddress = "sip:" + vsid + "@" + defaultSipip + ";" + "brand="+ getResources().getString(R.string.brandid);
			defaultSipPassword = pwd;
		}
		CustomLog.i("123", "defaultSipAddress=" + defaultSipAddress);
		CustomLog.i("123", "defaultSipPassword=" + defaultSipPassword);
		// 初始化视图组件
		initView();

		IntentFilter ift = new IntentFilter();
		ift.addAction(DfineAction.VS_CALLING_END);
		ift.addAction(DfineAction.VS_CALLING_TIME);
		ift.addAction(DfineAction.VS_CALLING_RING);
		registerReceiver(actionReceiver, ift);

		AudioPlayer.getInstance().startRingBefore180Player(
				R.raw.vs_softcall_ringring, true);
		initNumbers();
		setDisEnableLeftSliding();
		TutorialLaunchingThread thread = new TutorialLaunchingThread();
		thread.start();
	}

	/**
	 * 初始化视图组件
	 */

	public void initView() {
		// 初始化控件
		vs_callphone_callName = (TextView) findViewById(R.id.vs_callphone_callName);
		vs_callphone_local = (TextView) findViewById(R.id.vs_callphone_local);
		vs_callphone_calltime = (TextView) findViewById(R.id.vs_callphone_calltime_ad);
		vs_callphone_voice_btn = (ImageButton) findViewById(R.id.vs_callphone_voice_btn);
		vs_callphone_hf_btn = (ImageButton) findViewById(R.id.vs_callphone_hf_btn);
		vs_callphone_keyboard_btn = (ImageButton) findViewById(R.id.vs_callphone_keyboard_btn);
		vs_callphone_endbtn = (Button) findViewById(R.id.vs_callphone_leftbtn);
		vs_callphone_rightbtn = (Button) findViewById(R.id.vs_callphone_rightbtn);
		vs_callphone_button_layout = (LinearLayout) findViewById(R.id.vs_callphone_button_layout);
		vs_callphone_keyboard_layout = (RelativeLayout) findViewById(R.id.vs_callphone_keyboard_layout);
		vs_callphone_edit = (EditText) findViewById(R.id.vs_callphone_edit);
		vs_callphone_edit.addTextChangedListener(new CallPhoneTextWatcher());
		// 设置监听事件
		vs_callphone_voice_btn.setOnClickListener(this);
		vs_callphone_hf_btn.setOnClickListener(this);
		vs_callphone_keyboard_btn.setOnClickListener(this);
		vs_callphone_rightbtn.setOnClickListener(this);
		vs_callphone_endbtn.setOnClickListener(this);
	}

	/**
	 * @Title:监听键盘输入
	 * @Description:
	 * @Copyright: Copyright (c) 2014
	 * @author: 李志
	 * @version: 1.0.0.0
	 * @Date: 2014-9-4
	 */
	class CallPhoneTextWatcher implements TextWatcher {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
		 * int, int, int)
		 */
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
		 * int, int, int)
		 */
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		@Override
		public void afterTextChanged(Editable s) {
			String input = s.toString();
			if (input.length() > 0) {
				vs_callphone_callName.setVisibility(View.GONE);
				vs_callphone_local.setVisibility(View.GONE);
			} else {
				vs_callphone_callName.setVisibility(View.VISIBLE);
				vs_callphone_local.setVisibility(View.VISIBLE);
			}

		}
	}

	/**
	 * 初始话电话号码
	 */
	private void initNumbers() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mCalledNumber = bundle.getString("called_num");
			mCalledName = bundle.getString("called_name");
			mlocalname = bundle.getString("local_name");
			if (mCalledNumber == null) {
				mCalledNumber = "";
			}
			if (VsUtil.isNull(mCalledName)) {
				vs_callphone_callName.setText(mCalledNumber);
				mCalledName = mCalledNumber;
			} else {
				vs_callphone_callName.setText(mCalledName);
			}

			// 获取归属地
			if (mlocalname != null && !"".equals(mlocalname)) {
				vs_callphone_local.setText(mlocalname);
			} else {
				mlocalname = VsLocalNameFinder.findLocalName(mCalledNumber,
						false, mContext);
				if (mlocalname != null && !"".equals(mlocalname)) {
					vs_callphone_local.setText(mlocalname);
				}
			}

		}
		/* 注册拨号相关数字键 */
		findViewById(R.id.DTMF_DigitOneButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitTwoButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitThreeButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitFourButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitFiveButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitSixButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitSevenButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitEightButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitNineButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitJingButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitFlagButton).setOnClickListener(this);
		findViewById(R.id.DTMF_DigitZeroButton).setOnClickListener(this);// 特殊的"0"
	}

	private BroadcastReceiver actionReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (DfineAction.VS_CALLING_END.equals(intent.getAction())) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				msg.what = CALL_END;
				msg.setData(bundle);
				mBaseHandler.sendMessage(msg);
			}else if (DfineAction.VS_CALLING_RING.equals(intent.getAction())) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				msg.what = CALL_RING;
				msg.setData(bundle);
				mBaseHandler.sendMessage(msg);
			} else if (intent.getAction().equals(DfineAction.VS_CALLING_TIME)) {// 显示通话时间
				AudioPlayer.getInstance().stopRingBefore180Player();
				duration = intent.getLongExtra("callduration", 0);
				String timer = intent.getStringExtra("timer");
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("timer", timer);
				msg.what = CALL_TEMI;
				msg.setData(bundle);
				mBaseHandler.sendMessage(msg);
			}
		}
	};

	// 注册
	private class TutorialLaunchingThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				launchTutorialReg(defaultSipAddress, defaultSipPassword);
			} catch (LinphoneCoreException e) {
				e.printStackTrace();
				vs_callphone_calltime.setText(e.getMessage() + "\n"
						+ vs_callphone_calltime.getText());
			}
		}
	}

	// 打电话
	private class TutorialLaunchingCallThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				if (!mCalledNumber.startsWith("0")) {
					callCalledNumber = "0" + mCalledNumber;
				} else {
					callCalledNumber = mCalledNumber;
				}
				String destinationSipAddress = "sip:" + callCalledNumber + "@"
						+ defaultSipip;
				CustomLog.i("123", "callCalledNumber=" + callCalledNumber);
				CustomLog.i("123", "destinationSipAddress="
						+ destinationSipAddress);
				launchTutorialCall(destinationSipAddress);
			} catch (LinphoneCoreException e) {
				e.printStackTrace();
				vs_callphone_calltime.setText(e.getMessage() + "\n"
						+ vs_callphone_calltime.getText());
			}
		}
	}

	/*
	 * Registration state notification listener
	 */
	public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg,
			RegistrationState cstate, String smessage) {
		CustomLog.i("123", "smessage=" + smessage);
		if (lc.getDefaultProxyConfig().getState() == RegistrationState.RegistrationOk) {
			TutorialLaunchingCallThread thread = new TutorialLaunchingCallThread();
			thread.start();
		}
	}

	/*
	 * Call state notification listener
	 */
	public void callState(LinphoneCore lc, LinphoneCall call, State cstate,
			String msg) {

		CustomLog.i("123", "msgcall=" + msg);
		if (State.OutgoingRinging.equals(cstate)) {
			sendBroadcast(new Intent(DfineAction.VS_CALLING_RING).putExtra(
					"smessage", msg).setPackage(getPackageName()));
		}
		if (State.Error.equals(cstate)) {
			sendBroadcast(new Intent(DfineAction.VS_CALLING_END).putExtra(
					"smessage", msg).setPackage(getPackageName()));
			errorflag = "true";
			CustomLog.i("123", "error");
		}
		if (State.Connected.equals(cstate)) {
			startCallTimer();
		}
		if (State.CallEnd.equals(cstate)) {
			sendBroadcast(new Intent(DfineAction.VS_CALLING_END).putExtra(
					"smessage", msg).setPackage(getPackageName()));
		}
	}

	public void launchTutorialReg(String sipAddress, String password)
			throws LinphoneCoreException {
		final LinphoneCoreFactory lcFactory = LinphoneCoreFactory.instance();

		// First instantiate the core Linphone object given only a listener.
		// The listener will react to events in Linphone core.
		lc = lcFactory.createLinphoneCore(this, null);

		try {
			// Parse identity
			LinphoneAddress address = lcFactory
					.createLinphoneAddress(sipAddress);
			String username = address.getUserName();
			String domain = defaultSipip;

			if (password != null) {
				// create authentication structure from identity and add to
				// linphone
				lc.addAuthInfo(lcFactory.createAuthInfo(username, password,
						null, domain));
			}

			// create proxy config
			LinphoneProxyConfig proxyCfg = lc.createProxyConfig(sipAddress,
					domain, null, true);
			proxyCfg.setExpires(2000);
			lc.addProxyConfig(proxyCfg); // add it to linphone
			lc.setDefaultProxyConfig(proxyCfg);

			// main loop for receiving notifications and doing background
			// linphonecore work
			running = true;
			while (running) {
				lc.iterate(); // first iterate initiates registration
				sleep(50);
			}

			// Automatic unregistration on exit
		} finally {
		}
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			return;
		}
	}

	public void launchTutorialCall(String destinationSipAddress)
			throws LinphoneCoreException {
		try {
			// Send the INVITE message to destination SIP address
			call = lc.invite(destinationSipAddress);
			if (call == null) {
				return;
			}

			// main loop for receiving notifications and doing background
			// linphonecore work
			running = true;
			while (running) {
				// lc.iterate();
				try {
					// lc.enableSpeaker(true);
					Thread.sleep(50);
				} catch (InterruptedException ie) {
					return;
				}
			}

			if (!State.CallEnd.equals(call.getState())) {
				lc.terminateCall(call);
			}
		} finally {
			// You need to destroy the LinphoneCore object when no longer used
			lc.destroy();
		}
	}

	public void stopMainLoop() {
		running = false;
	}

	public void show(LinphoneCore lc) {
	}

	public void byeReceived(LinphoneCore lc, String from) {
	}

	public void authInfoRequested(LinphoneCore lc, String realm, String username) {
	}

	public void displayStatus(LinphoneCore lc, String message) {
	}

	public void displayMessage(LinphoneCore lc, String message) {
	}

	public void displayWarning(LinphoneCore lc, String message) {
	}

	public void globalState(LinphoneCore lc, GlobalState state, String message) {
	}

	public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf,
			String url) {
	}

	public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
	}

	public void textReceived(LinphoneCore lc, LinphoneChatRoom cr,
			LinphoneAddress from, String message) {
	}

	public void callStatsUpdated(LinphoneCore lc, LinphoneCall call,
			LinphoneCallStats stats) {
	}

	public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status,
			int delay_ms, Object data) {
	}

	public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call,
			boolean encrypted, String authenticationToken) {
	}

	public void notifyReceived(LinphoneCore lc, LinphoneCall call,
			LinphoneAddress from, byte[] event) {
	}

	public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {
	}

	@Override
	public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr,
			LinphoneChatMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferState(LinphoneCore lc, LinphoneCall call,
			State new_call_state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void infoReceived(LinphoneCore lc, LinphoneCall call,
			LinphoneInfoMessage info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev,
			SubscriptionState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneEvent ev,
			String eventName, LinphoneContent content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev,
			PublishState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void authInfoRequested(LinphoneCore lc, String realm,
			String username, String Domain) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileTransferProgressIndication(LinphoneCore lc,
			LinphoneChatMessage message, LinphoneContent content, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message,
			LinphoneContent content, byte[] buffer, int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message,
			LinphoneContent content, ByteBuffer buffer, int size) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void configuringStatus(LinphoneCore lc,
			RemoteProvisioningState state, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadProgressIndication(LinphoneCore lc, int offset, int total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadStateChanged(LinphoneCore lc,
			LogCollectionUploadState state, String info) {
		// TODO Auto-generated method stub

	}

	public void onClick(View v) {
		switch (v.getId()) {
		// 静音
		case R.id.vs_callphone_voice_btn:
			isMicMuted = !isMicMuted;
			if (isMicMuted) {
				vs_callphone_voice_btn
						.setBackgroundResource(R.drawable.vs_callphone_voice_click_);
			} else {
				vs_callphone_voice_btn
						.setBackgroundResource(R.drawable.vs_callphone_voice_);
			}
			lc.muteMic(isMicMuted);
			break;
		// 免提
		case R.id.vs_callphone_hf_btn:
			isSpeakerEnabled = !isSpeakerEnabled;
			if (isSpeakerEnabled) {
				vs_callphone_hf_btn
						.setBackgroundResource(R.drawable.vs_callphone_hf_click_);
			} else {
				vs_callphone_hf_btn
						.setBackgroundResource(R.drawable.vs_callphone_hf_);
			}
			AudioPlayer.getInstance().setPlayoutSpeaker(isSpeakerEnabled, 0);
			break;
		// 键盘
		case R.id.vs_callphone_keyboard_btn:
			showKeyBorad(keypadOpen);
			break;
		case R.id.vs_callphone_rightbtn:// 右边按钮
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			// 显示隐藏键盘
			showKeyBorad(true);
			break;
		// 挂断
		case R.id.vs_callphone_leftbtn:
			AudioPlayer.getInstance().stopRingBefore180Player();
			stopMainLoop();
			stopCallTimer();
			addOneCallLog(duration);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					TutorialRegistrationCall.this.finish();
				}
			}, 1500);
			break;
		case R.id.DTMF_DigitZeroButton:// 特殊的"0"键
			sendDTMF("0");
			break;
		case R.id.DTMF_DigitOneButton:// "1"
			sendDTMF("1");
			break;
		case R.id.DTMF_DigitTwoButton:// "2"
			sendDTMF("2");
			break;
		case R.id.DTMF_DigitThreeButton:// "3"
			sendDTMF("3");
			break;
		case R.id.DTMF_DigitFourButton:// "4"
			sendDTMF("4");
			break;
		case R.id.DTMF_DigitFiveButton:// "5"
			sendDTMF("5");
			break;
		case R.id.DTMF_DigitSixButton:// "6"
			sendDTMF("6");
			break;
		case R.id.DTMF_DigitSevenButton:// "7"
			sendDTMF("7");
			break;
		case R.id.DTMF_DigitEightButton:// "8"
			sendDTMF("8");
			break;
		case R.id.DTMF_DigitNineButton:// "9"
			sendDTMF("9");
			break;
		case R.id.DTMF_DigitFlagButton:
			sendDTMF("*");
			break;
		case R.id.DTMF_DigitJingButton:
			sendDTMF("#");
			break;
		default:
			break;
		}
	}

	public void sendDTMF(String dtmf) {
		lc.sendDtmf(dtmf.charAt(0));
		if (vs_callphone_edit.getText().length() > 20) {
			vs_callphone_edit.setText(vs_callphone_edit
					.getText()
					.toString()
					.substring(vs_callphone_edit.getText().length() - 18,
							vs_callphone_edit.getText().length()));
		}
		vs_callphone_edit.setText(vs_callphone_edit.getText() + dtmf);
	}

	/**
	 * 显示隐藏键盘
	 *
	 * @param isOpen
	 */
	public void showKeyBorad(boolean isOpen) {
		if (isOpen) {
			keypadOpen = false;
			vs_callphone_button_layout.setVisibility(View.VISIBLE);
			vs_callphone_keyboard_layout.setVisibility(View.GONE);
			vs_callphone_rightbtn.setVisibility(View.GONE);
		} else {
			keypadOpen = true;
			vs_callphone_button_layout.setVisibility(View.GONE);
			vs_callphone_keyboard_layout.setVisibility(View.VISIBLE);
			vs_callphone_rightbtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case CALL_END:
			if(errorflag.equals("true")){
				setCallState("呼叫超时");
				CustomLog.i("123", "5689");
				errorflag = "false";
			}else{
				if (vs_callphone_calltime != null) {
					setCallState("已挂机");
					CustomLog.i("123", "234");
				}
			}
			AudioPlayer.getInstance().stopRingBefore180Player();
			stopMainLoop();
			stopCallTimer();
			addOneCallLog(duration);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					TutorialRegistrationCall.this.finish();
				}
			}, 1500);
			break;
		case CALL_RING:
			setCallState("对方正在响铃");
			break;
		case CALL_TEMI: // 更新通话时间
			callTime = msg.getData().getString("timer");
			if (vs_callphone_calltime != null) {
				setCallState(callTime);
			}
			break;
		default:
			break;
		}
	}

	private int second = 0;
	private int minute = 0;
	private int hour = 0;
	private Timer timer = null;

	/**
	 * 通话走时
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-6-24 上午10:19:56
	 */
	public void startCallTimer() {
		if (timer == null) {
			timer = new Timer();
		} else if (timer != null) {
			timer.cancel();
			timer = null;
			timer = new Timer();
		}
		second = 0; // 秒
		minute = 0; // 分
		hour = 0; // 时
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				StringBuffer callTime = new StringBuffer();
				second++;
				if (second >= 60) {
					minute++;
					second = 0;
				}
				if (minute >= 60) {
					hour++;
					minute = 0;
				}
				if (hour != 0) {
					if (hour < 10) {
						callTime.append(0);
					}
					callTime.append(hour);
					callTime.append(":");
				}
				if (minute < 10) {
					callTime.append(0);
				}
				callTime.append(minute);
				callTime.append(":");
				if (second < 10) {
					callTime.append(0);
				}
				callTime.append(second);
				sendBroadcast(new Intent(DfineAction.VS_CALLING_TIME)
						.putExtra("callduration",
								(long) (hour * 3600 + minute * 60 + second))
						.setPackage(getPackageName())
						.putExtra("timer", callTime.toString()));
				// CustomLog.i(TAG_TCP, "计时--" + callTime.toString());
			}
		}, 0, 1000);
	}

	public void stopCallTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void setCallState(final String state) {

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				vs_callphone_calltime.setText(state);
			}
		});
	}

	/**
	 * 保存通话记录
	 *
	 * @param duration
	 */
	public void addOneCallLog(long duration) {
		if (isAddCall) {// 为了不多次加记录
			return;
		}
		int calltimelength = Integer.parseInt(String.valueOf(duration));
		/*
		 * if (calltimelength == 0) { return; }
		 */
		if (mCalledNumber.equals("8801")) {
			return;
		}
		VsCallLogItem callLogItem = new VsCallLogItem();

		callLogItem.callName = mCalledName;
		callLogItem.callNumber = mCalledNumber;
		callLogItem.local = mlocalname;
		callLogItem.calltimestamp = System.currentTimeMillis();
		callLogItem.calltimelength = secondsToTime(calltimelength);
		callLogItem.directCall = 0;

		DecimalFormat fnum = new DecimalFormat("##0.00");
		mCalleMoney = fnum.format(0.31 * ((int) (duration / 60 / 60 + 1)));
		callLogItem.callmoney = mCalleMoney;

		// 增加通话记录
		VsBizUtil.getInstance().addCallLog(mContext, callLogItem);
		isAddCall = true;
	}

	/**
	 * 将秒转换为对应时间，如00:12:08
	 *
	 * @param seconds
	 *            秒
	 * @return
	 * @author: 龙小龙
	 * @version: 2012-4-5 上午11:11:00
	 */
	public static String secondsToTime(int seconds) {
		String mFormateTime = "";
		int h = 0;
		int d = 0;
		int s = 0;
		int temp = seconds % 3600;
		if (seconds > 3600) {
			h = seconds / 3600;
			if (temp != 0) {
				if (temp > 60) {
					d = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else {
			d = seconds / 60;
			if (seconds % 60 != 0) {
				s = seconds % 60;
			}
		}
		if (h < 10) {
			if (h > 1) {
				mFormateTime = "0" + h + ":" + (d < 10 ? "0" + d : d) + ":"
						+ (s < 10 ? "0" + s : s);
			} else {
				mFormateTime = "" + (d < 10 ? "0" + d : d) + "分"
						+ (s < 10 ? "0" + s : s) + "秒";
			}
		} else {
			mFormateTime = "" + h + ":" + (d < 10 ? "0" + d : d) + ":"
					+ (s < 10 ? "0" + s : s);
		}

		return mFormateTime;
	}

	@Override
	protected void onDestroy() {
		 if (actionReceiver != null) {
		 unregisterReceiver(actionReceiver);
		 }
		AudioPlayer.getInstance().stopRingBefore180Player();
		super.onDestroy();
	}
}
