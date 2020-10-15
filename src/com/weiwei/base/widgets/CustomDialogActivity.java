package com.weiwei.base.widgets;

import java.util.Hashtable;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsTestAccessPoint;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.softphone.AudioPlayer;
import com.hwtx.dududh.R;

/**
 * 界面弹出框。在软件退出的情况下给用户弹出一个界面。
 * 
 * @author sys 所有业务根据传入参数business做区分处理
 */
public class CustomDialogActivity extends Activity {
	private final String TAG = "ConnectionService";
	private Button positiveButton, negativeButton;
	private TextView dialogtitle, dialogcontent;
	private String buttonText, cancelButText, link, title, content, type, push_id, business;
	private Activity mContext = this;
	private int testAccessPointState = 0;
	private boolean isNoBanlance=false;
	// private ImageView cancelImg;
	private String[][] NetworkErrorStr = {
			{ "网络无法连接，现在去设置网络吧！" },
			{ "网络已开启，但无法正常连接网络，请检查网络。", "1.正在检测QQ(www.qq.com)，请稍后...", "2.正在检测百度(www.baidu.com)，请稍后...",
					"1.检测QQ:www.qq.com，连接失败！\n2.检测百度:www.baidu.com，连接失败！", "检测到您的手机无法正常连接网络(无法登录QQ)，请检查网络设置。" },
			{ "网络已开启，但无法连接到服务器，请您尝试转换网络接入点。", "1.正在测试接入点1，请稍后...", "2.正在测试接入点2，请稍后...", "3.正在测试接入点3，请稍后...",
					"4.正在测试接入点4，请稍后...", "5.正在测试接入点5，请稍后...",
					"1.转换接入点1，接入失败；\n2.转换接入点2，接入失败；\n3.转换接入点3，接入失败；\n4.转换接入点4，接入失败；\n5.转换接入点5，接入失败！",
					"转换完毕，没有接入点可连接服务器，请您联系客服。" } };
	private boolean canCelButton = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_myself_dialog);
		Intent intent = getIntent();
		testAccessPointState = VsUserConfig.getDataInt(mContext, VsUserConfig.JKEY_TestAccessPointState);
		dialogcontent = (TextView) findViewById(R.id.message);
		positiveButton = (Button) findViewById(R.id.positiveButton);
		negativeButton = (Button) findViewById(R.id.negativeButton);
		dialogtitle = (TextView) findViewById(R.id.dialog_title);
		positiveButton = (Button) findViewById(R.id.positiveButton);
		isNoBanlance=intent.getBooleanExtra("isNoBanlance", false);
		if(isNoBanlance){//判断是否为余额不足
			if(VsUserConfig.getDataBoolean(this, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)){//判断语音提醒是否打开
			AudioPlayer.getInstance().startRingBefore180Player(R.raw.vs_no_money, false);//播放语言提醒
			}
		}
		// cancelImg = (ImageView) findViewById(R.id.kc_register_dialog_close);

		canCelButton = intent.getBooleanExtra("cancelButton", false);
		// 内容
		content = VsUtil.rtNoNullString(intent.getStringExtra("messagebody"));
		// 标题
		title = VsUtil.rtNoNullString(intent.getStringExtra("messagetitle"));
		// 点击跳转地址
		link = VsUtil.rtNoNullString(intent.getStringExtra("messagelink"));
		// 确定按钮名字
		buttonText = VsUtil.rtNoNullString(intent.getStringExtra("messagebuttontext"));
		// 取消按钮名字
		cancelButText = VsUtil.rtNoNullString(intent.getStringExtra("negativeButtontext"));
		// 跳转类型
		type = VsUtil.rtNoNullString(intent.getStringExtra("messagelinktype"));
		// 业务类型
		business = VsUtil.rtNoNullString(intent.getStringExtra("business"));
		
		

		if (business.equals("sendNotedialog")) {
			String sendNoteContent = intent.getStringExtra("sendNoteContent");
			negativeButton.setVisibility(View.GONE);
			positiveButton.setOnClickListener(new CallTishiOkoBtnListener());
			dialogtitle.setText(getResources().getString(R.string.warm_point));
			dialogcontent.setText(sendNoteContent);
			positiveButton.setText(getResources().getString(R.string.ok));
		} else if (business.equals("NetworkError")) {
			dialogtitle.setText(mContext.getResources().getString(R.string.warm_point));
			if (testAccessPointState == VsTestAccessPoint.MSG_NONETWORK) {
				dialogcontent.setText(NetworkErrorStr[0][0]);
				positiveButton.setText(getResources().getString(R.string.ok));
				negativeButton.setVisibility(View.GONE);
			} else if (testAccessPointState == VsTestAccessPoint.MSG_NOLINKNETWORK) {
				dialogcontent.setText(NetworkErrorStr[1][0]);
				positiveButton.setText(getResources().getString(R.string.dial_now_check));
				negativeButton.setText(getResources().getString(R.string.cancel));
			} else if (testAccessPointState == VsTestAccessPoint.MSG_CONVERTNETWORK) {
				dialogcontent.setText(NetworkErrorStr[2][0]);
				positiveButton.setText(getResources().getString(R.string.dial_now_change));
				negativeButton.setText(getResources().getString(R.string.cancel));
			}
			positiveButton.setOnClickListener(new NetworkErrorListener());
		} else if (business.equals("webtel")) {
			positiveButton.setText(buttonText);
			negativeButton.setText(cancelButText);
			dialogtitle.setText(title);
			dialogcontent.setText(content);
			positiveButton.setOnClickListener(new VsDialListener(intent.getStringExtra("telphone"), true));
			negativeButton.setOnClickListener(new VsDialListener(intent.getStringExtra("telphone"), false));
		} else if (business.equals("CallDialog")) {
			positiveButton.setOnClickListener(positiveClickListener);
			negativeButton.setOnClickListener(negativeClickListener);
			negativeButton.setVisibility(View.GONE);
			dialogtitle.setText(getResources().getString(R.string.prompt));
			dialogcontent.setText(getResources().getString(R.string.dial_back_intercept));
			positiveButton.setText(getResources().getString(R.string.ok));
		} else {
			if (canCelButton) {
				negativeButton.setVisibility(View.GONE);
			}
			push_id = VsUtil.rtNoNullString(intent.getStringExtra("push_id"));
			dialogtitle.setText(title);
			dialogcontent.setText(content);
			if (buttonText != null && buttonText.length() > 0) {
				positiveButton.setText(buttonText);
				if(cancelButText!=null&&cancelButText.length()>0){
					negativeButton.setText(cancelButText);
				}else{
				negativeButton.setText(getResources().getString(R.string.cancel));
				}
			} else {
				positiveButton.setText(getResources().getString(R.string.ok));
				negativeButton.setText(getResources().getString(R.string.cancel));
			}
			positiveButton.setOnClickListener(positiveClickListener);
			negativeButton.setOnClickListener(negativeClickListener);
			CustomLog.i(TAG, "content=" + content + "\nlink=" + link + "\ntype=" + type);
		}
		// cancelImg.setOnClickListener(negativeClickListener);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(isNoBanlance){//判断是否为余额不足
			AudioPlayer.getInstance().stopRingBefore180Player();//是否资源
		}
	}

	public void setPositiveBtnClickListener(View.OnClickListener listener) {
		positiveButton.setOnClickListener(listener);
	}

	public void setCancelBtnClickListener(View.OnClickListener listener) {
		negativeButton.setOnClickListener(listener);
	}

	private View.OnClickListener positiveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			// TODO Auto-generated method stub
			finish();
			if (push_id != null && push_id.length() > 0) {
				TreeMap<String, String> feedBackParams = new TreeMap<String, String>();
				feedBackParams.put("push_id", push_id);
				CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_PUSHNOTIFY,
                        DfineAction.authType_AUTO, feedBackParams, GlobalVariables.actionPushNotify);
			}
			if (type.equals("in")) {
				VsUtil.skipForTarget(link, mContext, 0, null);
			} else if (type.equals("out")) {
				VsUtil.schemeToWeb(link, mContext);
			}
		}
	};

	private class VsDialListener implements View.OnClickListener {
		private String callnumber = null;
		private boolean isVsdial = true;

		public VsDialListener(String tel, boolean bol) {
			this.callnumber = tel;
			this.isVsdial = bol;
		}

		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			finish();
			if (isVsdial) {
				VsUtil.callNumber(callnumber, callnumber, "", mContext, "",true,null);
			} else {
				VsUtil.LocalCallNumber(mContext, callnumber);
			}
		}
	};

	private View.OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			// TODO Auto-generated method stub
			finish();
		}
	};

	private class CallTishiOkoBtnListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			finish();
			// KcDialogActivity.this.sendBroadcast(new
			// Intent(DfineAction.ACTION_DIAL_EDITTEXT_SETNULL));
		}
	}

	private int i = 0;

	/**
	 * 无法连接网络
	 * 
	 * @author dell create at 2013-5-29上午09:24:34
	 */
	private class NetworkErrorListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (VsUtil.isFastDoubleClick()) {
				return;
			}
			i = 1;
			if (testAccessPointState == VsTestAccessPoint.MSG_NONETWORK) {
				finish();
				// Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				// mContext.startActivity(intent);
				startActivity(new Intent(Settings.ACTION_SETTINGS));
			} else if (testAccessPointState == VsTestAccessPoint.MSG_NOLINKNETWORK) {
				positiveButton.setVisibility(View.GONE);
				negativeButton.setVisibility(View.GONE);
				dialogtitle.setText(getResources().getString(R.string.dial_checking));
				dialogcontent.setText(NetworkErrorStr[1][i++]);
				if (NetworkErrorStr[1].length > i)
					mHandler.sendEmptyMessageDelayed(0, 2000);
			} else if (testAccessPointState == VsTestAccessPoint.MSG_CONVERTNETWORK) {
				positiveButton.setVisibility(View.GONE);
				negativeButton.setVisibility(View.GONE);
				dialogtitle.setText(getResources().getString(R.string.dial_checking));
				dialogcontent.setText(NetworkErrorStr[2][i++]);
				if (NetworkErrorStr[1].length > i)
					mHandler.sendEmptyMessageDelayed(0, 2000);
			}
		}
	};

	// private Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// try {
	// if (testAccessPointState == KcTestAccessPoint.MSG_NOLINKNETWORK) {
	// dialogcontent.setText(NetworkErrorStr[1][i++]);
	// if (NetworkErrorStr[1].length > i) {
	// mHandler.sendEmptyMessageDelayed(0, 2000);
	// } else {
	// dialogtitle.setText(mContext.getResources().getString(R.string.warm_point));
	// positiveButton.setText(mContext.getResources().getString(R.string.ok));
	// positiveButton.setVisibility(View.VISIBLE);
	// positiveButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
	// mContext.startActivity(intent);
	// finish();
	// }
	// });
	// }
	// } else if (testAccessPointState == KcTestAccessPoint.MSG_CONVERTNETWORK) {
	// dialogcontent.setText(NetworkErrorStr[2][i++]);
	// if (NetworkErrorStr[2].length > i) {
	// mHandler.sendEmptyMessageDelayed(0, 2000);
	// } else {
	// dialogtitle.setText(mContext.getResources().getString(R.string.warm_point));
	// positiveButton.setText(getResources().getString(R.string.dial_touch_customer));
	// positiveButton.setVisibility(View.VISIBLE);
	// negativeButton.setVisibility(View.VISIBLE);
	// positiveButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// String callNumber = KcUserConfig
	// .getDataString(mContext, KcUserConfig.JKey_ServicePhone);
	// if (callNumber.length() < 3) {
	// callNumber = DfineAction.mobile.replace(" ", "").replace("-", "");
	// }
	// Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumber));
	// startActivity(dial);
	// finish();
	// }
	// });
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// };
	// };
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			try {
				if (testAccessPointState == VsTestAccessPoint.MSG_NOLINKNETWORK) {
					dialogcontent.setText(NetworkErrorStr[1][i++]);
					if (NetworkErrorStr[1].length > i) {
						mHandler.sendEmptyMessageDelayed(0, 2000);
					} else {
						dialogtitle.setText(mContext.getResources().getString(R.string.warm_point));
						positiveButton.setText(mContext.getResources().getString(R.string.ok));
						positiveButton.setVisibility(View.VISIBLE);
						positiveButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (VsUtil.isFastDoubleClick()) {
									return;
								}
								Intent intent = new Intent(Settings.ACTION_SETTINGS);
								mContext.startActivity(intent);
								finish();
							}
						});
					}
				} else if (testAccessPointState == VsTestAccessPoint.MSG_CONVERTNETWORK) {
					dialogcontent.setText(NetworkErrorStr[2][i++]);
					if (NetworkErrorStr[2].length > i) {
						mHandler.sendEmptyMessageDelayed(0, 2000);
					} else {
						dialogtitle.setText(mContext.getResources().getString(R.string.warm_point));
						positiveButton.setText(getResources().getString(R.string.dial_touch_customer));
						positiveButton.setVisibility(View.VISIBLE);
						negativeButton.setVisibility(View.VISIBLE);
						positiveButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (VsUtil.isFastDoubleClick()) {
									return;
								}
								String callNumber = VsUserConfig
										.getDataString(mContext, VsUserConfig.JKey_ServicePhone);
								if (callNumber.length() < 3) {
									String mobile = mContext.getResources().getString(R.string.curstomer_server_mobile);
									callNumber = mobile.replace(" ", "").replace("-", "");
								}
								Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumber));
								startActivity(dial);
								finish();
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	});
 
}
