package com.weiwei.base.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.hwtx.dududh.R;
import com.weiwei.salemall.utils.FitStateUtils;


/**
 * 
 * @Title:Android客户端
 * @Description: 自动接听、拨打按键音设置
 * @Copyright: Copyright (c) 2014
 * 
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsSetingVoiceHangActivity extends VsBaseActivity implements OnClickListener {
	/**
	 * 设置说明
	 */
	private TextView vs_seting_about;
	/**
	 * 设置按钮
	 */
	private ImageView iv_right_icon_set;
	/**
	 * 设置文本
	 */
	private TextView iv_right_tv;
	/**
	 * 类型,voice:声音设置，hang:自动接听设置
	 */
	private String type = null;
	/**
	 * 语音提醒布局
	 */
	private RelativeLayout vs_seting_hintVoice;
	/**
	 * 语音提醒按键音
	 */
	private ImageView iv_right_icon_set_hintVoice;
	/**
	 * 线
	 */
	private View line_center;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_seting_voice_hang_layout);
		FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
		initTitleNavBar();
		showLeftNavaBtn(R.drawable.vs_title_back_selecter);
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
		vs_seting_about = (TextView) findViewById(R.id.vs_seting_about);
		iv_right_icon_set = (ImageView) findViewById(R.id.iv_right_icon_set);
		iv_right_tv = (TextView) findViewById(R.id.iv_right_tv);
		vs_seting_hintVoice = (RelativeLayout) findViewById(R.id.vs_seting_hintVoice);
		iv_right_icon_set_hintVoice = (ImageView) findViewById(R.id.iv_right_icon_set_hintVoice);
		line_center = findViewById(R.id.line_center);
		// 设置监听事件
		iv_right_icon_set_hintVoice.setOnClickListener(this);
		iv_right_icon_set.setOnClickListener(this);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		if ("voice".equals(type)) {
			vs_seting_hintVoice.setVisibility(View.VISIBLE);
			line_center.setVisibility(View.VISIBLE);
			mTitleTextView.setText(R.string.vs_seting_call_voice);
			vs_seting_about.setText(R.string.vs_seting_voice_hint);
			iv_right_tv.setText(R.string.vs_seting_voice_str);
			// 拨打安静音状态
			if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_KEYPAD_TONE, true)) {
				iv_right_icon_set.setImageResource(R.drawable.vs_switch_open);
			} else {
				iv_right_icon_set.setImageResource(R.drawable.vs_switch_close);
			}
			// 语音提醒状态
			if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {
				iv_right_icon_set_hintVoice.setImageResource(R.drawable.vs_switch_open);
			} else {
				iv_right_icon_set_hintVoice.setImageResource(R.drawable.vs_switch_close);
			}

		} else if ("hang".equals(type)) {
			vs_seting_hintVoice.setVisibility(View.GONE);
			line_center.setVisibility(View.GONE);
			mTitleTextView.setText(R.string.vs_seting_call_get);
			vs_seting_about.setText(R.string.vs_seting_zdjt_hint);
			iv_right_tv.setText(R.string.vs_seting_zdjt_str);
			// 获取自动接听状态
			if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_CALL_ANSWER_SWITCH, true)) {// 判读是否设置为自动接听
				iv_right_icon_set.setImageResource(R.drawable.vs_switch_open);
			} else {
				iv_right_icon_set.setImageResource(R.drawable.vs_switch_close);
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.iv_right_icon_set:
			if ("voice".equals(type)) {
				// 获取拨打安静音状态
				if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_KEYPAD_TONE, true)) {// 判读是否有拨打按键音
					VsUserConfig.setData(mContext, VsUserConfig.JKEY_SETTING_KEYPAD_TONE, false);
					iv_right_icon_set.setImageResource(R.drawable.vs_switch_close);
				} else {
					VsUserConfig.setData(mContext, VsUserConfig.JKEY_SETTING_KEYPAD_TONE, true);
					iv_right_icon_set.setImageResource(R.drawable.vs_switch_open);
				}

			} else if ("hang".equals(type)) {
				// 设置自动接听状态
				if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_CALL_ANSWER_SWITCH, true)) {// 判读是否设置为自动接听
					VsUserConfig.setData(mContext, VsUserConfig.JKey_CALL_ANSWER_SWITCH, false);
					iv_right_icon_set.setImageResource(R.drawable.vs_switch_close);
				} else {
					VsUserConfig.setData(mContext, VsUserConfig.JKey_CALL_ANSWER_SWITCH, true);
					iv_right_icon_set.setImageResource(R.drawable.vs_switch_open);
				}

			}
			break;
		case R.id.iv_right_icon_set_hintVoice:// 语音提醒
			// 语音提醒状态
			if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {
				iv_right_icon_set_hintVoice.setImageResource(R.drawable.vs_switch_close);
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, false);
			} else {
				iv_right_icon_set_hintVoice.setImageResource(R.drawable.vs_switch_open);
				VsUserConfig.setData(mContext, VsUserConfig.JKEY_SETTING_HINT_VOICE, true);
			}
			break;
		default:
			break;
		}

	}
}
