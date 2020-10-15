package com.weiwei.base.activity.me;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.flyco.roundview.RoundTextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.setting.VsCallTypeSetingActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.softphone.TutorialRegistrationCall;

public class VsthirdTypeActivity extends VsBaseActivity implements OnClickListener {

	
	private EditText third_ip,third_port,third_account,third_paw;
	private RoundTextView third_call;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_thirdcall_setting);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		mTitleTextView.setText(R.string.vs_thirdcalltype_hint);
//		showRightTxtBtn("保存");
		// 获取控件对象
		third_call = (RoundTextView)findViewById(R.id.third_call);
		third_ip = (EditText)findViewById(R.id.third_ip);
		third_account = (EditText)findViewById(R.id.third_account);
		third_port = (EditText)findViewById(R.id.third_port);
//		third_phone = (EditText)findViewById(R.id.third_phone);
		third_paw = (EditText)findViewById(R.id.third_paw);
		third_call.setOnClickListener(this);

		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}
	
	
	
	
	@Override
	public void onClick(View v) {
		if (VsUtil.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.third_call:

			String ip = third_ip.getText().toString().trim();
			String port = third_port.getText().toString().trim();
			String account = third_account.getText().toString().trim();
			
			String reg = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(ip);
			String paw = third_paw.getText().toString();
			boolean b = matcher.matches();
			if (b&&null!=account&&!("".equals(account))) {
				VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLIP, ip);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLPORT, port);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLACCOUNT, account);
				VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLPASSWORD, paw);
				mToast.show("保存成功", Toast.LENGTH_SHORT);
			}else {
				mToast.show("输入的信息有误", Toast.LENGTH_SHORT);
			}
			
			
		
//			if (VsUtil.isLogin(
//					mContext.getResources().getString(R.string.login_prompt0),mContext)) {
////				String number = third_phone.getText().toString().trim();
//				if (VsUtil.checkPhone(number)) {
//						VsUtil.callNumber("", number, VsLocalNameFinder
//								.findLocalName(number, false, mContext), mContext,
//								null, true);
////					}
//				} else {
//					mToast.show("请输入正确的手机号码");
//				}
//				
//			}
			break;
			
			
		}
	}

}
