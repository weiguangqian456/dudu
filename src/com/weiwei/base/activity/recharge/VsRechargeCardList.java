package com.weiwei.base.activity.recharge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description:多卡充值 卡记录
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsRechargeCardList extends VsBaseActivity implements OnClickListener {
	/**
	 * 确定按钮
	 */
	private RoundTextView vs_recharge_cardlist_btn;
	/**
	 * 添加组件布局
	 */
	private LinearLayout vs_recharge_cardlist_layout;
	/**
	 * 充值卡列表控件
	 */
	private VsRechargeCardWidget[] vsRechargeCardWidget;
	/**
	 * 是否提交的标志
	 */
	private boolean flag = false;
	/**
	 * 充值卡类型
	 */
	private String mPayKind;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_recharge_cardlist_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		mTitleTextView.setText("已输入(" + VsUserConfig.cardList.size() + ")");
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		checkSumit();// 判断提交状态
		initView();
		Intent intnet = getIntent();
		mPayKind = intnet.getStringExtra("mPayKind");
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
		// 初始化控件
		vs_recharge_cardlist_btn = (RoundTextView) findViewById(R.id.vs_recharge_cardlist_btn);
		vs_recharge_cardlist_layout = (LinearLayout) findViewById(R.id.vs_recharge_cardlist_layout);
		vs_recharge_cardlist_btn.setOnClickListener(this);
		vsRechargeCardWidget = initWidget();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		// TODO Auto-generated method stub
		String cardNumber = null;
		String cardpassword = null;
		for (int i = 0; i < vsRechargeCardWidget.length; i++) {
			cardNumber = vsRechargeCardWidget[i].getKc_recharge_card_number_edit().getText().toString();
			cardpassword = vsRechargeCardWidget[i].getKc_recharge_card_password_edit().getText().toString();
			if (checkNumber(cardNumber, cardpassword)) {
				VsUserConfig.cardList.set(i, new String[] { cardNumber, cardpassword, "0" });
			} else {
				mToast.show("第" + (i + 1) + "张位数不正确", Toast.LENGTH_SHORT);
				return;
			}
		}
		finish();
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
	 * 判断是否提交了
	 */
	public void checkSumit() {
		for (String[] array : VsUserConfig.cardList) {
			if ("1".equals(array[2]) || "2".equals(array[2])) {
				flag = true;
			}
		}
	}

	/**
	 * 初始化自定义控件
	 * 
	 * @return
	 */
	public VsRechargeCardWidget[] initWidget() {
		VsRechargeCardWidget[] krcl = new VsRechargeCardWidget[VsUserConfig.cardList.size()];
		for (int i = 0; i < krcl.length; i++) {
			krcl[i] = new VsRechargeCardWidget(mContext);
			krcl[i].getKc_recharge_card_number_edit().setText(VsUserConfig.cardList.get(i)[0]);
			krcl[i].getKc_recharge_card_password_edit().setText(VsUserConfig.cardList.get(i)[1]);
			if (flag) {
				krcl[i].getCardlist_widget_hint_imageview().setVisibility(View.VISIBLE);
				if ("1".equals(VsUserConfig.cardList.get(i)[2])) {
					krcl[i].getCardlist_widget_hint_imageview().setBackgroundResource(
							R.drawable.vs_recharge_list_success);
				} else if ("2".equals(VsUserConfig.cardList.get(i)[2])) {
					krcl[i].getCardlist_widget_hint_imageview().setBackgroundResource(R.drawable.vs_recharge_list_fail);
				}
			}
			// 添加组件到布局中
			vs_recharge_cardlist_layout.addView(krcl[i]);
		}
		return krcl;
	}

	@Override
	protected void handleBaseMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleBaseMessage(msg);
	}

	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);
	}

}
