package com.weiwei.base.activity.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.hwtx.dududh.R;

public class KcDrainageDialog extends VsBaseActivity {
	private static final String TAG = "KcSigninDialog";
	private TextView mSigninInfo, mSigninInfoTarget;
	private Button mBtnText;
	private String btntext = "";
	private String btntarget = "";
	private String btnresult = "";
	private String btnresulttarget = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kc_drainage_dialog);
		mSigninInfo = (TextView) findViewById(R.id.signin_info);
		mSigninInfoTarget = (TextView) findViewById(R.id.signin_info_target);
		mBtnText = (Button) findViewById(R.id.btn_text);
		mBtnText.setOnClickListener(mBtnTextListener);

		btntext = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_btntext);
		btntarget ="已签到";
		btnresult = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_btnresult);
		btnresulttarget = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_sign_btnresult_target);

		mSigninInfo.setText(btnresult);
		mSigninInfoTarget.setText(btnresulttarget);
		mBtnText.setText("签到成功");
	}

	private View.OnClickListener mBtnTextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			VsUtil.startActivity(btnresulttarget, mContext, null);
			finish();
		}
	};
}
