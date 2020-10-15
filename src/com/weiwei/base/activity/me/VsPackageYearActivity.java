package com.weiwei.base.activity.me;


import android.os.Bundle;
import android.widget.TextView;
import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;

public class VsPackageYearActivity extends VsBaseActivity {

	private String uid;
	private TextView packg_year_text;
	private String stime = "";
	private String etime = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vs_package_year);
		init();
		uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);

		// IntentFilter mycalllogFilter = new IntentFilter();
		// mycalllogFilter.addAction(VsUserConfig.JKey_GET_MY_CALL_LOG);
		// mContext.registerReceiver(mycalllogReceiver, mycalllogFilter);

		stime = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_GiftExpireTime, "");
		etime = VsUserConfig.getDataString(mContext,
				VsUserConfig.JKey_ValidDate, "");
		packg_year_text.setText(stime.substring(0, stime.indexOf(" ")) + " 至  "
				+ etime.substring(0, stime.indexOf(" ")));
		// getMyCallLog(uid,startTime,endTime);
		VsApplication.getInstance().addActivity(this);
	}

	private void init() {
		packg_year_text = (TextView) mContext
				.findViewById(R.id.packg_year_text);

	}

}
