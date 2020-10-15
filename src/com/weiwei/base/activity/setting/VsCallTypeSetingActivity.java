package com.weiwei.base.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.me.VsthirdTypeActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @Title:Android客户端
 * @Description: 拨打设置类
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-14
 */
public class VsCallTypeSetingActivity extends VsBaseActivity implements OnCheckedChangeListener, OnClickListener {
	/**
	 * wifi下回拨
	 */
	private ImageView vs_calltype_dialong_wifi;
	/**
	 * 3g、4g下回拨
	 */
	private ImageView vs_calltype_dialong_3g_4g;
	/**
	 * wifi下回拨文字
	 */
	private TextView vs_calltype_dialong_wifi_tv;
	/**
	 * 3g、4g下回拨文字
	 */
	private TextView vs_calltype_dialong_3g_4g_tv;
	/**
	 * 了解回拨
	 */
	private TextView vs_calltype_callback_tv;
	/**
	 * 了解回拨内容
	 */
	private TextView vs_calltype_callback_tv_hint;
	private RadioButton rb_call_back,rb_call_soft,rb_call_can,rb_third_call_hand;
	private RadioGroup rg_call_setting;
	private String def_call_type = "2";
	private LinearLayout tv_third_call_type;
	private RelativeLayout third_call_type;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_call_setting);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
		mTitleTextView.setText(R.string.vs_calltype_hint);
		// showLeftTxtBtn(getResources().getString(R.string.seting_title));
		// 获取控件对象
		rg_call_setting = (RadioGroup)findViewById(R.id.rg_call_setting);
		rb_call_back = (RadioButton)findViewById(R.id.rb_call_back);
		rb_call_soft = (RadioButton)findViewById(R.id.rb_call_soft);
		rb_call_can = (RadioButton)findViewById(R.id.rb_call_can);
		tv_third_call_type = (LinearLayout)findViewById(R.id.tv_third_call_type);
		rb_third_call_hand = (RadioButton)findViewById(R.id.rb_third_call_hand);
		third_call_type = (RelativeLayout)findViewById(R.id.third_call_type);
		rg_call_setting.setOnCheckedChangeListener(this);
		tv_third_call_type.setOnClickListener(this);
		third_call_type.setOnClickListener(this);
//		vs_calltype_dialong_wifi = (ImageView) findViewById(R.id.vs_calltype_dialong_wifi);// wifi下回拨
//		vs_calltype_dialong_3g_4g = (ImageView) findViewById(R.id.vs_calltype_dialong_3g_4g);// 3g4g下回拨
//		vs_calltype_dialong_wifi_tv = (TextView) findViewById(R.id.vs_calltype_dialong_wifi_tv);
//		vs_calltype_dialong_3g_4g_tv = (TextView) findViewById(R.id.vs_calltype_dialong_3g_4g_tv);
//		vs_calltype_callback_tv = (TextView) findViewById(R.id.vs_calltype_callback_tv);
//		vs_calltype_callback_tv_hint = (TextView) findViewById(R.id.vs_calltype_callback_tv_hint);
//		vs_calltype_callback_tv_hint.setOnClickListener(this);
//		vs_calltype_dialong_wifi.setOnClickListener(this);
//		vs_calltype_dialong_3g_4g.setOnClickListener(this);
		// 设置监听事件

		VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// wifi下回拨--默认关闭
		boolean wifiCallback_state = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true);
		// 3g、4g下回拨--默认开启
		boolean callback_state_3g_4g = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true);
		 String call_type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_dial_types, "");
	        try {
				JSONObject js = new JSONObject(call_type);
				String type = (String)js.get("type");
				String def = (String)js.get("default");
				Boolean bl = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_dial_swich, false);
				if ("all".equals(type)) {
					
					if (bl) {
						
					}else {
						
						if ("direct".equals(def)) {
							def_call_type = "0";
						}else if ("callback".equals(def)) {
							def_call_type = "2";
						}
						VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
					}
					
					
				}else if("direct".equals(type)){
					def_call_type = "0";
					VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_swich, false);
					VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
				}else {
					def_call_type = "2";
					VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_swich, false);
					VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			String type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_USERDIALVALUE, "2");
			if (type.equals("0")) {
			rb_call_soft.setChecked(true);
			rb_call_back.setChecked(false);
			rb_call_can.setChecked(false);
			rb_third_call_hand.setChecked(false);
		}else if (type.equals("1")) {
			rb_call_soft.setChecked(false);
			rb_call_back.setChecked(false);
			rb_call_can.setChecked(true);
			rb_third_call_hand.setChecked(false);
		}else if (type.equals("3")) {
			rb_call_soft.setChecked(false);
			rb_call_back.setChecked(false);
			rb_call_can.setChecked(false);
			rb_third_call_hand.setChecked(true);
		}else {
			rb_call_soft.setChecked(false);
			rb_call_back.setChecked(true);
			rb_call_can.setChecked(false);
			rb_third_call_hand.setChecked(false);
		}
		// 获取wifi下回拨状态
//		if (wifiCallback_state) {
//			vs_calltype_dialong_wifi.setBackgroundResource(R.drawable.vs_wifi_gree_selecter);
//			vs_calltype_dialong_wifi_tv.setText(getString(R.string.vs_calltype_open_hint));
//			vs_calltype_dialong_wifi_tv.setTextColor(getResources().getColor(R.color.vs_gree));
//		} else {
//			vs_calltype_dialong_wifi.setBackgroundResource(R.drawable.vs_wifi_gray_selecter);
//			vs_calltype_dialong_wifi_tv.setText(getString(R.string.vs_calltype_close_hint));
//			vs_calltype_dialong_wifi_tv.setTextColor(getResources().getColor(R.color.vs_black));
//		}
//		// 获取3g、4g回拨状态
//		if (callback_state_3g_4g) {
//			vs_calltype_dialong_3g_4g.setBackgroundResource(R.drawable.vs_3g_4g_gree_selecter);
//			vs_calltype_dialong_3g_4g_tv.setText(getString(R.string.vs_calltype_open_hint));
//			vs_calltype_dialong_3g_4g_tv.setTextColor(getResources().getColor(R.color.vs_gree));
//		} else {
//			vs_calltype_dialong_3g_4g.setBackgroundResource(R.drawable.vs_3g_4g_gray_selecter);
//			vs_calltype_dialong_3g_4g_tv.setText(getString(R.string.vs_calltype_close_hint));
//			vs_calltype_dialong_3g_4g_tv.setTextColor(getResources().getColor(R.color.vs_black));
//		}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vs_calltype_dialong_wifi:// wifi下回拨
//			MobclickAgent.onEvent(mContext, "Set_CallMode_Backwifi");
//			if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true)) {
//				vs_calltype_dialong_wifi.setBackgroundResource(R.drawable.vs_wifi_gree_selecter);
//				vs_calltype_dialong_wifi_tv.setText(getString(R.string.vs_calltype_open_hint));
//				vs_calltype_dialong_wifi_tv.setTextColor(getResources().getColor(R.color.vs_gree));
//				VsUserConfig.setData(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true);
//			} else {
//				vs_calltype_dialong_wifi.setBackgroundResource(R.drawable.vs_wifi_gray_selecter);
//				vs_calltype_dialong_wifi_tv.setText(getString(R.string.vs_calltype_close_hint));
//				vs_calltype_dialong_wifi_tv.setTextColor(getResources().getColor(R.color.vs_black));
//				VsUserConfig.setData(mContext, VsUserConfig.JKey_WIFI_CALLBACK, false);
//			}
			break;
		case R.id.vs_calltype_dialong_3g_4g:// 3g、4g下回拨
//			MobclickAgent.onEvent(mContext, "Set_CallMode_Back3G/4G");
//			if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true)) {
//				vs_calltype_dialong_3g_4g.setBackgroundResource(R.drawable.vs_3g_4g_gree_selecter);
//				vs_calltype_dialong_3g_4g_tv.setText(getString(R.string.vs_calltype_open_hint));
//				vs_calltype_dialong_3g_4g_tv.setTextColor(getResources().getColor(R.color.vs_gree));
//				VsUserConfig.setData(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true);
//			} else {
//				vs_calltype_dialong_3g_4g.setBackgroundResource(R.drawable.vs_3g_4g_gray_selecter);
//				vs_calltype_dialong_3g_4g_tv.setText(getString(R.string.vs_calltype_close_hint));
//				vs_calltype_dialong_3g_4g_tv.setTextColor(getResources().getColor(R.color.vs_black));
//				VsUserConfig.setData(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, false);
//			}
			break;
		case R.id.vs_calltype_callback_tv_hint:// 什么是回拨 （用帮助中心）
		    VsUtil.startActivity("3019", mContext, null);
			break;
		case R.id.third_call_type:// 第三方拨打方式
		case R.id.tv_third_call_type:
			rb_call_soft.setChecked(false);
			rb_call_back.setChecked(false);
			rb_call_can.setChecked(false);
			rb_third_call_hand.setChecked(true);
			VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, "3");
			VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLVALUE, true);
			startActivity(new Intent(VsCallTypeSetingActivity.this,VsthirdTypeActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		//用户手动改变过拨打方式
				VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_swich, true);
		switch (checkedId) {
		
		// 默认回拨
		case R.id.rb_call_back:
			VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, "2");
			VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLVALUE, false);
			break;
		// 默认直拨
		case R.id.rb_call_soft:
			VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, "0");
			VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLVALUE, false);
			break;
		case R.id.rb_call_can:
			VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, "1");
			VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLVALUE, false);
			break;
		case R.id.rb_third_call_hand:
			VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, "3");
			VsUserConfig.setData(mContext, VsUserConfig.JKey_THIRDCALLVALUE, true);
			break;
				}
	}

}
