package com.weiwei.base.activity.recharge;

import java.util.TreeMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @Title:Android客户端
 * @Description: 移动/联通充值卡充值
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-15
 */
public class VsRechargeMobileCard extends VsBaseActivity implements OnClickListener {
	/**
	 * 充值金额
	 */
	private TextView vs_recharge_mobilecard_money;
	/**
	 * 输入卡的数量
	 */
	private TextView vs_recharge_mobilecard_sumit_number;
	/**
	 * 提交提示
	 */
	private TextView vs_recharge_mobilecard_sumit_hint;
	/**
	 * 卡号
	 */
	private TextView vs_recharge_mobilecard_cardNumber;
	/**
	 * 密码
	 */
	private TextView vs_recharge_mobilecard_password;
	/**
	 * 提交失败提示图标
	 */
	private ImageView vs_recharge_mobilecard_hint_image;
	/**
	 * 充值提交加载图标
	 */
	private ImageView vs_recharge_mobilecard_load;
	/**
	 * 增加按钮
	 */
	private TextView vs_recharge_mobilecard_add;
	/**
	 * 充值提交按钮
	 */
	private RoundTextView vs_recharge_cardlist_btn;
	/**
	 * 账号输入框
	 */
	private EditText vs_recharge_mobilecard_cardNumber_edit;
	/**
	 * 密码输入框
	 */
	private EditText vs_recharge_mobilecard_password_edit;
	/**
	 * 查看输入充值卡张数
	 */
	private RelativeLayout vs_recharge_mobilecard_hintlayout;
	/**
	 * 加载图标布局
	 */
	private RelativeLayout vs_recharge_mobilecard_load_layout;
	/**
	 * 箭头
	 */
	private ImageView vs_recharge_mobilecard_jt;
	// /**
	// * 输入或提交状态
	// */
	// private boolean summitStateFlag = false;
	// /**
	// * 帐号
	// */
	// private String mAcount;
	/**
	 * 商品编号
	 */
	private String goodsid;
	// /**
	// * 商品名称
	// */
	// private String mPakDesc;
	// /**
	// * ???
	// */
	// private String mPromotion;
	/**
	 * 充值类型
	 */
	private String mPayType;
	/**
	 * 充值类型编号
	 */
	private String mPayKind;
	/**
	 * 充值金额
	 */
	private String mPrice;
	/**
	 * 充值类型名称
	 */
	private String mPayTypeDesc;
	public int[] cardCharSequence = { 4, 9, 14, 19 };
	public int[] pwdCharSequence = { 4, 9, 14, 19, 24 };

	private String gCardno;
	private String gCardpwd;
	/**
	 * 充值卡提交数
	 */
	private int sumintNumber;
	/**
	 * 充值失败数
	 */
	private int failNumber;
	/**
	 * 充值成功
	 */
	private final char MSG_ID_Recharge_Succeed_Message = 60;
	/**
	 * 充值失败
	 */
	private final char MSG_ID_Recharge_Fail_Message = 61;
	/**
	 * 返回消息
	 */
	private String msgString;
	/**
	 * 线
	 */
	private View recharge_line1;
	/**
	 * 商城外部订单号
	 */
	private String operparam;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vs_recharge_mobilecard);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);

		Intent intent = getIntent();
		// mAcount = intent.getStringExtra("mAcount");
		goodsid = intent.getStringExtra("goodsid");
		// mPakDesc = intent.getStringExtra("mPakDesc");
		// mPromotion = intent.getStringExtra("mPromotion");
		mPrice = intent.getStringExtra("mPrice");
		mPayType = intent.getStringExtra("mPayType");
		mPayKind = intent.getStringExtra("mPayKind");
		mPayTypeDesc = intent.getStringExtra("mPayTypeDesc");
		operparam = intent.getStringExtra("operparam");
		
        initTitleNavBar();
        mTitleTextView.setText(mPayTypeDesc);
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        // showLeftTxtBtn(getResources().getString(R.string.recharge_type));
        
		// 初始化视图
		initView();
		
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!(VsUserConfig.cardList.size() > 0)) {
			vs_recharge_mobilecard_hintlayout.setVisibility(View.GONE);
			recharge_line1.setVisibility(View.GONE);
			vs_recharge_mobilecard_hintlayout.setEnabled(false);// 设置进入充值卡列表不可点击

		}
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
		vs_recharge_mobilecard_money = (TextView) findViewById(R.id.vs_recharge_mobilecard_money);
		vs_recharge_mobilecard_sumit_number = (TextView) findViewById(R.id.vs_recharge_mobilecard_sumit_number);
		vs_recharge_mobilecard_sumit_hint = (TextView) findViewById(R.id.vs_recharge_mobilecard_sumit_hint);
		vs_recharge_mobilecard_cardNumber = (TextView) findViewById(R.id.vs_recharge_mobilecard_cardNumber);
		vs_recharge_mobilecard_password = (TextView) findViewById(R.id.vs_recharge_mobilecard_password);
		vs_recharge_mobilecard_hint_image = (ImageView) findViewById(R.id.vs_recharge_mobilecard_hint_image);
		vs_recharge_mobilecard_load = (ImageView) findViewById(R.id.vs_recharge_mobilecard_load);
		vs_recharge_mobilecard_add = (TextView) findViewById(R.id.vs_recharge_mobilecard_add);
		vs_recharge_cardlist_btn = (RoundTextView) findViewById(R.id.vs_recharge_cardlist_btn);
		vs_recharge_mobilecard_cardNumber_edit = (EditText) findViewById(R.id.vs_recharge_mobilecard_cardNumber_edit);
		vs_recharge_mobilecard_password_edit = (EditText) findViewById(R.id.vs_recharge_mobilecard_password_edit);
		vs_recharge_mobilecard_hintlayout = (RelativeLayout) findViewById(R.id.vs_recharge_mobilecard_hintlayout);
		vs_recharge_mobilecard_load_layout = (RelativeLayout) findViewById(R.id.vs_recharge_mobilecard_load_layout);
		vs_recharge_mobilecard_jt = (ImageView) findViewById(R.id.vs_recharge_mobilecard_jt);
		recharge_line1 = findViewById(R.id.recharge_line1);
		// 设置监听事件
		vs_recharge_cardlist_btn.setOnClickListener(this);
		vs_recharge_mobilecard_add.setOnClickListener(this);
		vs_recharge_mobilecard_hintlayout.setOnClickListener(this);
		vs_recharge_mobilecard_cardNumber_edit.addTextChangedListener(new KcTextWatcher(
				R.id.vs_recharge_mobilecard_cardNumber_edit));
		vs_recharge_mobilecard_password_edit.addTextChangedListener(new KcTextWatcher(
				R.id.vs_recharge_mobilecard_password_edit));
		/**
		 * 初始化提示
		 */
		vs_recharge_mobilecard_money.setText("" + Integer.valueOf(mPrice) / 100 + "元");

		if (mPayKind.equals("701")) {
			vs_recharge_mobilecard_cardNumber.setText("充值卡序列号");
			vs_recharge_mobilecard_password.setText("充值卡密码");
			pwdCharSequence = new int[] { 2, 7, 12, 17, 22 };
		} else if (mPayKind.equals("702")) {
			vs_recharge_mobilecard_cardNumber.setText(Html.fromHtml("充值卡序列号(<font color=#41B2F7>" + 0 + "</font>/15)"));
			vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font color=#41B2F7>" + 0 + "</font>/19)"));
			pwdCharSequence = new int[] { 4, 9, 14, 19, 23 };
		} else if (mPayKind.equals("703")) {
			vs_recharge_mobilecard_cardNumber.setText(Html.fromHtml("充值卡序列号(<font color=#41B2F7>" + 0 + "</font>/19)"));
			vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font color=#41B2F7>" + 0 + "</font>/18)"));
			pwdCharSequence = new int[] { 4, 9, 14, 19, 24 };
		}
	}

	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vs_recharge_cardlist_btn:
			// 点击提交初始化控件状态
			vs_recharge_mobilecard_sumit_number.setVisibility(View.INVISIBLE);
			vs_recharge_mobilecard_sumit_hint.setVisibility(View.INVISIBLE);
			vs_recharge_mobilecard_hint_image.setVisibility(View.INVISIBLE);
			gCardno = vs_recharge_mobilecard_cardNumber_edit.getText().toString();
			gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();

			if (!(VsUserConfig.cardList.size() > 0)) {// 单个提交
				if (checkNumber(gCardno, gCardpwd)) {
					vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
					recharge_line1.setVisibility(View.VISIBLE);
					vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
					// 设置按钮不可用
					vs_recharge_cardlist_btn.setEnabled(false);
					vs_recharge_mobilecard_add.setEnabled(false);
					// 启动动画
					VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
					// 充值
					recharge(gCardno.replaceAll(" ", ""), gCardpwd.replaceAll(" ", ""));
				} else {
					mToast.show("卡号卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
				}
			} else {
				// 设置按钮不可用
				vs_recharge_cardlist_btn.setEnabled(false);
				vs_recharge_mobilecard_add.setEnabled(false);
				vs_recharge_mobilecard_load_layout.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_jt.setVisibility(View.VISIBLE);
				// 启动动画
				VsUtil.statAnimAciton(vs_recharge_mobilecard_load);
				if (checkNumber(gCardno, gCardpwd)) {
					vs_recharge_mobilecard_cardNumber_edit.setText("");
					vs_recharge_mobilecard_password_edit.setText("");
					VsUserConfig.cardList.add(new String[] { gCardno, gCardpwd, "0" });
				}
				// 初始化计数
				failNumber = 0;
				sumintNumber = 0;
				// 充值第一个
				recharge(VsUserConfig.cardList.get(0)[0].replaceAll(" ", ""),
						VsUserConfig.cardList.get(0)[1].replaceAll(" ", ""));
			}

			break;
		case R.id.vs_recharge_mobilecard_add:
			gCardno = vs_recharge_mobilecard_cardNumber_edit.getText().toString();
			gCardpwd = vs_recharge_mobilecard_password_edit.getText().toString();
			if (checkNumber(gCardno, gCardpwd)) {
				VsUserConfig.cardList.add(new String[] { gCardno, gCardpwd, "0" });
				vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已输入<font color='#329EE2'>"
						+ VsUserConfig.cardList.size() + "</font>张"));
				CustomLog.i("vsdebug", "添加了----------");
				vs_recharge_mobilecard_cardNumber_edit.getText().clear();
				vs_recharge_mobilecard_password_edit.getText().clear();
				vs_recharge_mobilecard_hintlayout.setVisibility(View.VISIBLE);
				recharge_line1.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_hintlayout.setEnabled(true);// 设置进入充值卡列表可用
				vs_recharge_mobilecard_cardNumber_edit.requestFocus();
				vs_recharge_mobilecard_jt.setVisibility(View.VISIBLE);
			} else {
				mToast.show("卡号卡密不能为空或格式不正确！", Toast.LENGTH_SHORT);
			}
			break;
		case R.id.vs_recharge_mobilecard_hintlayout:
			Intent intent = new Intent(mContext, VsRechargeCardList.class);
			intent.putExtra("mPayKind", mPayKind);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	/**
	 * 检测输入内容是否符合规范
	 * 
	 * @param gCardno
	 * @param gCardpwd
	 */
	public boolean checkNumber(String gCardno_, String gCardpwd_) {
		String gCardno = gCardno_.replaceAll(" ", "");
		String gCardpwd = gCardpwd_.replaceAll(" ", "");
		if ((gCardno != null && !"".equals(gCardno)) && (gCardpwd != null && !"".equals(gCardpwd))) {
			if (mPayKind.equals("701")) {
				/*
				 * if (gCardno.length() != 16 && gCardno.length() != 10 && gCardno.length() != 17) {
				 * mToast.show(getResources().getString(R.string.charge_card_error), Toast.LENGTH_SHORT); return false;
				 * } else if (gCardpwd.length() != 8 && gCardpwd.length() != 17 && gCardpwd.length() != 18 &&
				 * gCardpwd.length() != 21) { mToast.show(getResources().getString(R.string.charge_pwd_error),
				 * Toast.LENGTH_SHORT); return false; }
				 */
				return true;
			} else if (mPayKind.equals("702")) {
				if (gCardno.length() != 15) {
					mToast.show(getResources().getString(R.string.charge_card_error), Toast.LENGTH_SHORT);
					return false;
				} else if (gCardpwd.length() != 19) {
					mToast.show(getResources().getString(R.string.charge_pwd_error), Toast.LENGTH_SHORT);
					return false;
				}
			} else if (mPayKind.equals("703")) {
				if (gCardno.length() != 19) {
					mToast.show(getResources().getString(R.string.charge_card_error), Toast.LENGTH_SHORT);
					return false;
				} else if (gCardpwd.length() != 18) {
					mToast.show(getResources().getString(R.string.charge_pwd_error), Toast.LENGTH_SHORT);
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
		// recharge();充值方法
	}

	/**
	 * 输入状态监听
	 * 
	 * @author 李志
	 * 
	 */
	class KcTextWatcher implements TextWatcher {

		private int prevLength = 0;
		/**
		 * 控件ID号
		 */
		private int resourceId;

		/**
		 * 构造方法
		 * 
		 * @param resourceId
		 */
		public KcTextWatcher(int resourceId) {
			this.resourceId = resourceId;
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			switch (resourceId) {
			case R.id.vs_recharge_mobilecard_cardNumber_edit:// 帐号
				String textNum = vs_recharge_mobilecard_cardNumber_edit.getText().toString().replaceAll(" ", "");

				if (mPayKind.equals("701")) {
					vs_recharge_mobilecard_cardNumber.setText(Html.fromHtml("充值卡序列号(<font color='#41B2F7'>"
							+ textNum.length() + "</font>)"));
				} else if (mPayKind.equals("702")) {
					vs_recharge_mobilecard_cardNumber.setText(Html.fromHtml("充值卡序列号(<font color='#41B2F7'>"
							+ textNum.length() + "</font>/15)"));
				} else if (mPayKind.equals("703")) {
					vs_recharge_mobilecard_cardNumber.setText(Html.fromHtml("充值卡序列号(<font color='#41B2F7'>"
							+ textNum.length() + "</font>/19)"));
				}
				if (prevLength > vs_recharge_mobilecard_cardNumber_edit.getText().length()) {
					prevLength = vs_recharge_mobilecard_cardNumber_edit.getText().length();
					return;
				}
				vs_recharge_mobilecard_cardNumber_edit.removeTextChangedListener(this);

				int len = cardCharSequence.length;
				for (int i = 0; i < len; i++) {
					if (vs_recharge_mobilecard_cardNumber_edit.getText().length() == cardCharSequence[i]) {
						vs_recharge_mobilecard_cardNumber_edit.setText(s + " ");
						vs_recharge_mobilecard_cardNumber_edit.setSelection(vs_recharge_mobilecard_cardNumber_edit
								.length());
					}
				}
				prevLength = vs_recharge_mobilecard_cardNumber_edit.getText().length();
				vs_recharge_mobilecard_cardNumber_edit.addTextChangedListener(this);
				break;
			case R.id.vs_recharge_mobilecard_password_edit:// 密码
				String textPwd = vs_recharge_mobilecard_password_edit.getText().toString().replaceAll(" ", "");
				if (mPayKind.equals("701")) {
					vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font color='#41B2F7'>"
							+ textPwd.length() + "</font>)"));
				} else if (mPayKind.equals("702")) {
					vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font color='#41B2F7'>"
							+ textPwd.length() + "</font>/19)"));
				} else if (mPayKind.equals("703")) {
					vs_recharge_mobilecard_password.setText(Html.fromHtml("充值卡密码(<font color='#41B2F7'>"
							+ textPwd.length() + "</font>/18)"));
				} else if (mPayKind.equals("708")) {
					vs_recharge_mobilecard_password.setText("(" + textPwd.length() + ")");
				}

				if (prevLength > vs_recharge_mobilecard_password_edit.getText().length()) {
					prevLength = vs_recharge_mobilecard_password_edit.getText().length();
					return;
				}
				vs_recharge_mobilecard_password_edit.removeTextChangedListener(this);

				int length = pwdCharSequence.length;
				for (int i = 0; i < length; i++) {
					if (vs_recharge_mobilecard_password_edit.getText().length() == pwdCharSequence[i]) {
						vs_recharge_mobilecard_password_edit.setText(s + " ");
						vs_recharge_mobilecard_password_edit
								.setSelection(vs_recharge_mobilecard_password_edit.length());
					}
				}
				prevLength = vs_recharge_mobilecard_password_edit.getText().length();
				vs_recharge_mobilecard_password_edit.addTextChangedListener(this);
				break;
			default:
				break;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 向服务器发送充值参数
	 */
	private void recharge(String gCardno, String gCardpwd) {
		// 充值提交数
		sumintNumber++;
		// 统计代码
		// startTime = System.currentTimeMillis();
		unregisterKcBroadcast();

		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVariables.actionMobileRecharge);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);

		if (mPayKind.equals("708")) {
			gCardno = "123456";
		}

		// 加载用户信息
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
//		if (mPayKind.equals("708")) {
//			treeMap.put("paytype", "5");
//			treeMap.put("goodsid", "40010");
//		} else {
//			treeMap.put("paytype", mPayType);
//			treeMap.put("goodsid", goodsid);
//		}
		treeMap.put("uid", VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_KcId));
		treeMap.put("phone", VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_PhoneNumber));
		treeMap.put("account", VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_PhoneNumber));
		treeMap.put("card_pwd", gCardpwd);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_MOBILE_RECHARGE,
                DfineAction.authType_UID, treeMap, GlobalVariables.actionMobileRecharge);
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
		msgString = msg.getData().getString("msgString");
		switch (msg.what) {
		case MSG_ID_Recharge_Succeed_Message:
			if (!(VsUserConfig.cardList.size() > 0)) {
				vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);
				vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font color=#50C965>" + 1 + "/" + 1
						+ "</font>张"));
				vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_hint.setText("提交成功");
				// 设置按钮可用
				vs_recharge_cardlist_btn.setEnabled(true);
				vs_recharge_mobilecard_add.setEnabled(true);
			} else {
				vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
				if (failNumber > 0) {
					// 显示提交张数
					vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font color=#FF8237>"
							+ (sumintNumber - failNumber) + "/" + VsUserConfig.cardList.size() + "</font>张"));
					// 显示失败张数
					vs_recharge_mobilecard_hint_image.setVisibility(View.VISIBLE);
					vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
					vs_recharge_mobilecard_sumit_hint.setText(Html.fromHtml("失败<font color=#41B2F7>" + failNumber
							+ "</font>张"));
				} else {
					vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font color=#50C965>" + sumintNumber
							+ "/" + VsUserConfig.cardList.size() + "</font>张"));
				}

				VsUserConfig.cardList.get(sumintNumber - 1)[2] = "1";
				if (VsUserConfig.cardList.size() > sumintNumber) {// 充值下一张
					recharge(VsUserConfig.cardList.get(sumintNumber)[0].replaceAll(" ", ""),
							VsUserConfig.cardList.get(sumintNumber)[1].replaceAll(" ", ""));
					return;
				} else {
					vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);// 隐藏加载
					if (failNumber == 0) {
						VsUserConfig.cardList.clear();
					}
					// 设置按钮可用
					vs_recharge_cardlist_btn.setEnabled(true);
					vs_recharge_mobilecard_add.setEnabled(true);
				}
			}
			// 提交完成后设置光标在第一个输入框
			vs_recharge_mobilecard_cardNumber_edit.requestFocus();
			// 充值完成后提示绑定手机
			new CustomDialog.Builder(mContext)
					.setTitle(DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt)).setMessage(msgString)
					.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							finish();
						}
					}).create().show();
			break;
		case MSG_ID_Recharge_Fail_Message:
			if (!(VsUserConfig.cardList.size() > 0)) {
				// 显示提交张数
				vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);
				vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font color=#41B2F7>" + 1 + "/" + 1
						+ "</font>张"));
				// 显示失败张数
				vs_recharge_mobilecard_hint_image.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_hint.setText(Html.fromHtml("失败<font color=#FF8237>" + 1 + "</font>张"));
				// 设置按钮可用
				vs_recharge_cardlist_btn.setEnabled(true);
				vs_recharge_mobilecard_add.setEnabled(true);

			} else {
				failNumber++;
				// 显示提交张数
				vs_recharge_mobilecard_sumit_number.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_number.setText(Html.fromHtml("已提交<font color=#41B2F7>"
						+ (sumintNumber - failNumber) + "/" + VsUserConfig.cardList.size() + "</font>张"));
				// 显示失败张数
				vs_recharge_mobilecard_hint_image.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_hint.setVisibility(View.VISIBLE);
				vs_recharge_mobilecard_sumit_hint.setText(Html.fromHtml("失败<font color=#FF8237>" + failNumber
						+ "</font>张"));
				VsUserConfig.cardList.get(sumintNumber - 1)[2] = "2";
				if (VsUserConfig.cardList.size() > sumintNumber) {
					recharge(VsUserConfig.cardList.get(sumintNumber)[0].replaceAll(" ", ""),
							VsUserConfig.cardList.get(sumintNumber)[1].replaceAll(" ", ""));
				} else {
					vs_recharge_mobilecard_load_layout.setVisibility(View.INVISIBLE);// 隐藏加载
					// 设置按钮可用
					vs_recharge_cardlist_btn.setEnabled(true);
					vs_recharge_mobilecard_add.setEnabled(true);

				}
				// 提交完成后设置光标在第一个输入框
				vs_recharge_mobilecard_cardNumber_edit.requestFocus();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);
		String vsStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);

		if (intent.getAction().equals(GlobalVariables.actionMobileRecharge)) {
			Message message = mBaseHandler.obtainMessage();
			Bundle bundle = new Bundle();
			try {
				JSONObject vsData = new JSONObject(vsStr);
				String vsResult = vsData.getString("result");
				// 成功
				if (vsResult.equals("0")) {
					// 清空输入
					vs_recharge_mobilecard_cardNumber_edit.setText("");
					vs_recharge_mobilecard_password_edit.setText("");
					VsUserConfig.isChangeBalance = true;
					VsUserConfig.changeBalanceTime = System.currentTimeMillis();
					msgString = vsData.getString("reason");
					message.what = MSG_ID_Recharge_Succeed_Message;
				} else {
					if (vsResult.equals("-99")) {
						if (!VsUtil.isCurrentNetworkAvailable(mContext)) {
							return;
						}
					}
					String wrongResult = vsData.getString("reason");
					if (wrongResult != null) {
						msgString = wrongResult.toString();
					}
					message.what = MSG_ID_Recharge_Fail_Message;
				}

			} catch (Exception e) {
				e.printStackTrace();
				msgString = getResources().getString(R.string.request_failinfo);
				message.what = MSG_ID_Recharge_Fail_Message;
			}
			bundle.putString("msgString", msgString);
			message.setData(bundle);
			mBaseHandler.sendMessage(message);
		}
	}

}
