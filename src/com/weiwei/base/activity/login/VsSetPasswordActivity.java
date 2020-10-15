package com.weiwei.base.activity.login;

import java.util.TreeMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsMd5;
import com.weiwei.base.common.VsRc4;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @instruction  设置密码
 * @author 黄发兴
 * @version 创建时间：2014-12-8下午08:25:18
 */
public class VsSetPasswordActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 一键删除
	 */
	private ImageView vs_set_password_eidt_del;
	/**
	 * 密码输入框
	 */
	private EditText vs_set_password_edit;
	/**
	 * 设置密码是否可见
	 */
	private ImageView set_password_show_btn;
	/**
	 * 完成按钮
	 */
	private RoundTextView vs_set_password_finish_btn;
	/**
	 * 电话号码
	 */
	private String phoneNumber = null;
	/**
	 * 类型:0注册、1重置密码
	 */
	private int type;
	/**
	 * 验证码
	 */
	private String code = null;;
	/**
	 * 显示隐藏密码
	 */
	private boolean pwd_show_hide = false;
	/**
	 * 新密码
	 */
	private String newPassword = null;
	/**
	 * 设置密码成功
	 */
	private final char MSG_ID_PROMPT_SUCC = 311;
	/**
	 * 设置密码失败
	 */
	private final char MSG_ID_PROMPT_FAIL = 312;
	/**
	 * 密码
	 */
	private String psswd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_set_password_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		initView();
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
		// 获得传递的数据
		Intent intent = getIntent();
		phoneNumber = intent.getStringExtra("phone");
		type = intent.getIntExtra("type", 0);
		code = intent.getStringExtra("code");
		// 判断是注册还是重置密码
		if (type == 0) {
			mTitleTextView.setText(R.string.vs_reghist_title_hint2);
		} else if (type == 1) {
			mTitleTextView.setText(R.string.vs_set_phone_title_hint3);
		}
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		// 初始化控件
		vs_set_password_eidt_del = (ImageView) findViewById(R.id.vs_set_password_eidt_del);
		vs_set_password_edit = (EditText) findViewById(R.id.vs_set_password_edit);
		set_password_show_btn = (ImageView) findViewById(R.id.set_password_show_btn);
		vs_set_password_finish_btn = (RoundTextView) findViewById(R.id.vs_set_password_finish_btn);
		// 设置监听事件
		vs_set_password_eidt_del.setOnClickListener(this);
		set_password_show_btn.setOnClickListener(this);
		vs_set_password_finish_btn.setOnClickListener(this);
		vs_set_password_edit.addTextChangedListener(new SetPwdWhatch());
	}

	/**
	 * 
	 * @Title:内容输入状态监听类
	 * @Description: 监听输入框是否输入内容
	 * @Copyright: Copyright (c) 2014
	 * 
	 * @author: 李志
	 * @version: 1.0.0.0
	 * @Date: 2014-8-28
	 */
	class SetPwdWhatch implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String pwd = vs_set_password_edit.getText().toString().trim();
			if (pwd.length() > 0) {
				vs_set_password_eidt_del.setVisibility(View.VISIBLE);
			} else {
				vs_set_password_eidt_del.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.set_password_show_btn:// 显示隐藏密码
			if (!pwd_show_hide) {
				set_password_show_btn.setImageResource(R.drawable.vs_checked_yes);
				vs_set_password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				vs_set_password_edit.setSelection(vs_set_password_edit.getText().toString().length());
				pwd_show_hide = true;
			} else {
				set_password_show_btn.setImageResource(R.drawable.vs_checked_no);
				vs_set_password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
				vs_set_password_edit.setSelection(vs_set_password_edit.getText().toString().length());
				pwd_show_hide = false;
			}
			break;
		case R.id.vs_set_password_eidt_del:// 一键清空
			vs_set_password_edit.setText("");
			break;
		case R.id.vs_set_password_finish_btn:
			// 判断是注册还是重置密码
			if (type == 0) {
				// 执行注册操作
				if (phoneNumber != null && !"".equals(phoneNumber) && phoneNumber.length() == 11) {
					if (code != null && !"".equals(code) && code.length() == 4) {
						psswd = vs_set_password_edit.getText().toString().trim();
						if (psswd != null && !"".equals(psswd) && psswd.length() > 5) {
							// 提交注册
							regPhoneNum(code, phoneNumber, psswd);
						} else if (psswd == null || "".equals(psswd)) {
							mToast.show(getResources().getString(R.string.vs_pwd_isnull_str));
						} else if (psswd.length() < 6) {
							mToast.show(getResources().getString(R.string.vs_pwd_xy6_str));
						}
					} else {
						mToast.show(getResources().getString(R.string.vs_code_erro));
					}
				} else {
					mToast.show(getResources().getString(R.string.vs_phone_erro));
				}
			} else if (type == 1) {
				// 执行重置密码操作
				if (phoneNumber != null && !"".equals(phoneNumber) && phoneNumber.length() == 11) {
					if (code != null && !"".equals(code) && code.length() == 4) {
						String pwd = vs_set_password_edit.getText().toString().trim();
						if (pwd != null && !"".equals(pwd) && pwd.length() > 5) {
							// 提交修改密码
							confirmPwd(phoneNumber, pwd, code);
						} else if (pwd == null || "".equals(pwd)) {
							mToast.show(getResources().getString(R.string.vs_pwd_isnull_str));
						} else if (pwd.length() < 6) {
							mToast.show(getResources().getString(R.string.vs_pwd_xy6_str));
						}
					} else {
						mToast.show(getResources().getString(R.string.vs_code_erro));
					}
				} else {
					mToast.show(getResources().getString(R.string.vs_phone_erro));
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 提交请求重置密码
	 */
	private void confirmPwd(String phoneNumber, String passwd, String code) {
		loadProgressDialog("提交中，请稍候...");
		newPassword = passwd;
		// 绑定广播接收器
		unregisterKcBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(VsUserConfig.VS_ACTION_RESET_PWD);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);

		// 发送请求
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		String phone = phoneNumber.replaceAll("-", "");
		treeMap.put("account",
				VsUtil.isNull(phone) ? VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber) : phone);
		treeMap.put("code", code);
		treeMap.put("passwd", VsRc4.encry_RC4_string(passwd, DfineAction.passwad_key));
		// 发送修改密码请求
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INRFACE_REST_PWD, DfineAction.authType_Key,
				treeMap, VsUserConfig.VS_ACTION_RESET_PWD);
	}

	/**
	 * 注册
	 */
	private void regPhoneNum(String validateCode, String PhoneNumber, String pwd) {
		loadProgressDialog("提交中，请稍等....");
		String target = "", action = "";
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		target = GlobalVariables.INTERFACE_REGISTER;
		action = GlobalVariables.actionRegister;
		treeMap.put("account", PhoneNumber);
		treeMap.put("vcode", validateCode);
		treeMap.put("accounttype", "mobile");
		treeMap.put("deviceid", VsUtil.getMacAddress(mContext));
		treeMap.put("ptype", android.os.Build.MODEL);
		treeMap.put("password", VsRc4.encry_RC4_string(pwd, DfineAction.passwad_key));
		unregisterKcBroadcast();
		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);
		// 提交注册或绑定
		CoreBusiness.getInstance().startThread(mContext, target, DfineAction.authType_AUTO, treeMap, action);
	}

	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);
		String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
		//CustomLog.i("lte", "重置密码后登录返回结果是:" + jStr);
		Bundle bundle = new Bundle();
		Message message = mBaseHandler.obtainMessage();
		try {
			JSONObject jData = new JSONObject(jStr);
			String retStr = jData.getString("result");
			String action = intent.getAction();
			if (VsUserConfig.VS_ACTION_RESET_PWD.equals(action)) {
				if ("0".equals(retStr)) {
					bundle.putString("msg", jData.getString("reason"));
					login(phoneNumber, newPassword);
				} else {
					dismissProgressDialog();// 关闭进度条
					bundle.putString("msg", jData.getString("reason"));
				}
				message.what = MSG_ID_PROMPT_FAIL;
			} else if (GlobalVariables.actionRegister.equals(action)) {
				try {
					if ("0".equals(retStr)) {
						String vsId = jData.getString("uid");
						VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, vsId);
						VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, psswd);
						VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, phoneNumber);
						// 发送登录成功广播
						Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
						intent2.putExtra("packname", mContext.getPackageName());
						sendBroadcast(intent2);
						mToast.show("注册成功!");
						// 进入主界面
						Intent intent1 = new Intent(mContext, VsMainActivity.class);
						startActivity(intent1);
						finish();

					} else {
						dismissProgressDialog();// 关闭进度条
						bundle.putString("msg", jData.getString("reason"));
						message.what = MSG_ID_PROMPT_FAIL;
					}
				} catch (JSONException e) {
					dismissProgressDialog();// 关闭进度条
					e.printStackTrace();
					bundle.putString("msg", "服务器异常，请稍后再试！");
					message.what = MSG_ID_PROMPT_FAIL;
				}
			} else if (VsCoreService.VS_ACTION_LOGIN.equals(action)) {
			    //CustomLog.i("lte", "重置密码后的登录:" + retStr);
				dismissProgressDialog();// 关闭进度条
				try {
					if (retStr.equals("0")) {
						String vsId = jData.getString("uid");
						VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, vsId);
						VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, newPassword);
						String check = jData.getString("check");
						if ("true".equals(check)) {
							VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, jData.getString("mobile"));
						} else {
							VsUserConfig.setData(context, VsUserConfig.JKey_BindPhoneNumberHint,jData.getString("msg2"));
							VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, jData.getString("mobile"));
						}
						VsUserConfig.setData(context, VsUserConfig.JKey_PhoneNumber, phoneNumber);
						// 发送登录成功广播
						Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
						intent2.putExtra("packname", mContext.getPackageName());
						sendBroadcast(intent2);
						// 进入主界面
						Intent intent1 = new Intent(mContext, VsMainActivity.class);
						intent1.putExtra("msg1", jData.getString("msg1"));
						intent1.putExtra("firstreg", jData.getString("firstreg"));
						intent1.putExtra("check", jData.getString("check"));
						startActivity(intent1);
						finish();

					} else {
						bundle.putString("msg", jData.getString("reason"));
						message.what = MSG_ID_PROMPT_FAIL;
					}
				} catch (Exception e) {
					e.printStackTrace();
					bundle.putString("msg", "登录失败，请稍后再试！");
					message.what = MSG_ID_PROMPT_FAIL;
				}
			}
		} catch (Exception e) {
			dismissProgressDialog();// 关闭进度条
			e.printStackTrace();
			bundle.putString("msg", "失败，请稍后再试！");
			message.what = MSG_ID_PROMPT_FAIL;
		}
		message.setData(bundle);
		mBaseHandler.sendMessage(message);
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case MSG_ID_PROMPT_SUCC:
			Intent intent3 = new Intent(mContext, VsMainActivity.class);
			startActivity(intent3);
			finish();
			break;
		case MSG_ID_PROMPT_FAIL:
			mToast.show(msg.getData().getString("msg"), Toast.LENGTH_LONG);
			break;
		default:
			break;
		}

	}

	@Override
	protected void HandleLeftNavBtn() {
		// TODO Auto-generated method stub
		showExitDialog();
	}

	/**
	 * 登录
	 * 
	 * @param acount
	 * @param pwd
	 */
	private void login(String acount, String pwd) {
		loginAccount();

		String netType = VsUtil.getNetTypeString();
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		if (acount.indexOf("+86") == 0) {
			acount = acount.substring(3, acount.length());
		}
		treeMap.put("account", acount);
		treeMap.put("passwd", VsMd5.md5(pwd));
		treeMap.put("netmode", netType);
		treeMap.put("ptype", android.os.Build.MODEL);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INRFACE_LOGIN, DfineAction.authType_Key,
				treeMap, VsCoreService.VS_ACTION_LOGIN);
		// KcHttpsClient.GetHttps(mContext, bizparams);
	}

	/**
	 * 发送登录账号广播
	 * 
	 * @param bundle
	 */
	private void loginAccount() {
		unregisterKcBroadcast();
		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(VsCoreService.VS_ACTION_LOGIN);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			showExitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出对话框
	 */
	private void showExitDialog() {
		String msg = null;
		String btnMsg = null;
		if (type == 0) {
			msg = getResources().getString(R.string.vs_reg_dialog_msg2);
			btnMsg = getResources().getString(R.string.vs_reg_dialog_goon);
		} else if (type == 1) {
			msg = getResources().getString(R.string.vs_pwd_dialog_msg2);
			btnMsg = getResources().getString(R.string.vs_pwd_dialog_goon);
		}
		VsUtil.showYesNoDialog(mContext, null, msg, btnMsg, getResources().getString(R.string.vs_exit),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (type == 0) {
							Intent intent = new Intent(mContext, VsRegisterActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						} else if (type == 1) {
							Intent intent = new Intent(mContext, VsLoginActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
						VsSetPasswordActivity.this.finish();
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
