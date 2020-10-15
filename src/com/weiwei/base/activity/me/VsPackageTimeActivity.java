package com.weiwei.base.activity.me;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;

public class VsPackageTimeActivity extends VsBaseActivity {
	private String uid;
	private String stime = "";
	private String etime = "";
	private String balance = "";
	private TextView packg_end_time, packg_left_time, packg_time;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_package_time);
		init();
		uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
		balance = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_Balance, "");
		stime = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_GiftExpireTime, "");
		etime = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_ValidDate, "");

		packg_time.setText(balance);
		packg_left_time.setText(balance);
		packg_end_time.setText(stime.substring(0, stime.indexOf(" ")) + " 至  "
				+ etime.substring(0, stime.indexOf(" ")));
		// getMyCallLog(uid,startTime,endTime);
		VsApplication.getInstance().addActivity(this);
	}

	private void init() {
		packg_time = (TextView) mContext.findViewById(R.id.packg_time);
		packg_left_time = (TextView) mContext
				.findViewById(R.id.packg_left_time);
		packg_end_time = (TextView) mContext.findViewById(R.id.packg_end_time);

	}

}
