package com.weiwei.base.activity.me;

import java.util.TreeMap;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * 
 * @Title:Android客户端
 * @Description:关于界面
 * @Copyright: Copyright (c) 2014
 * 
 * @author: Sundy
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsUninopayGetActivity extends VsBaseActivity implements
		OnClickListener {
	public final char MSG_ID_GetCardSucceed = 82;
	/**
	 * 查询通话时间失败
	 */
	public final char MSG_ID_GetCardFail = 81;
	private EditText pay_name;
	private EditText pay_bank;
	private EditText pay_account;
	private Button get_button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_unionpay_get_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		pay_name = (EditText) findViewById(R.id.pay_name);
		pay_bank = (EditText) findViewById(R.id.pay_bank);
		pay_account = (EditText) findViewById(R.id.pay_account);
		get_button = (Button) findViewById(R.id.get_button);
		get_button.setOnClickListener(this);
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		mTitleTextView.setText("绑定银行卡");
		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.get_button:
			String payname = pay_name.getText().toString().trim();
			String paybank = pay_bank.getText().toString().trim();
			String payaccount = pay_account.getText().toString().trim();
			if (payname != null && payname.length() > 0) {
				if (paybank != null && paybank.length() > 0) {
					if (payaccount != null && payaccount.length() > 0) {
						getcard(payname, paybank, payaccount);
					} else {
						mToast.show("银行卡号不能为空");
					}
				} else {
					mToast.show("开户银行不能为空");
				}
			} else {
				mToast.show("开户姓名不能为空");
			}
			break;
		default:
			break;
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
	 * 绑定银行卡
	 */
	private void getcard(String payname, String paybank, String payaccount) {
		unregisterKcBroadcast();
		// 绑定广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalVariables.actionGetcard);
		vsBroadcastReceiver = new KcBroadcastReceiver();
		registerReceiver(vsBroadcastReceiver, filter);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("type", "n");
		treeMap.put("bank_name", payname);
		treeMap.put("bank_deposit", paybank);
		treeMap.put("bank_account", payaccount);
//		bizparams.put("bank_name", "项韬");
//		bizparams.put("bank_deposit", "招商银行");
//		bizparams.put("bank_account", "6226097805588597");
		CoreBusiness.getInstance().startThread(mContext,
				GlobalVariables.INTERFACE_SEACHE_CARD,
				DfineAction.authType_UID, treeMap,
				GlobalVariables.actionGetcard);
	}

	@Override
	protected void handleKcBroadcast(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.handleKcBroadcast(context, intent);

		String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
		Message message = mBaseHandler.obtainMessage();
		Bundle bundle = new Bundle();
		try {
			JSONObject jData = new JSONObject(jStr);
			String retStr = jData.getString("result");

			if (retStr.equals("0")) {

				message.what = MSG_ID_GetCardSucceed;
			} else {
				if (retStr.equals("-99")) {
					dismissProgressDialog();
					if (!VsUtil.isCurrentNetworkAvailable(mContext))
						return;
				}
				bundle.putString("msg", jData.getString("msg"));
				message.what = MSG_ID_GetCardFail;
			}

		} catch (Exception e) {
			e.printStackTrace();
			bundle.putString("msg",
					getResources().getString(R.string.servicer_busying));
			message.what = MSG_ID_GetCardFail;
		}
		message.setData(bundle);
		mBaseHandler.sendMessage(message);
	}

	protected void handleBaseMessage(Message msg) {
		super.handleBaseMessage(msg);
		switch (msg.what) {
		case MSG_ID_GetCardSucceed:
			mToast.show("绑定成功！", Toast.LENGTH_SHORT);
			finish();
			break;
		case MSG_ID_GetCardFail:
			mToast.show(msg.getData().getString("msg"), Toast.LENGTH_SHORT);
			break;
		default:
			break;
		}
	}
}
