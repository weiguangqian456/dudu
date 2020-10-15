package com.weiwei.netphone.activity.other;

import java.util.Hashtable;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.netphone.data.process.CoreBusiness;

public class VsShowNoTice extends Activity {
	public Activity mContext = this;
	@SuppressWarnings("unused")
	private String content, buttonText, link, push_id, type, title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		title = intent.getStringExtra("messagetitle");
		content = intent.getStringExtra("messagebody");
		link = intent.getStringExtra("messagelink");
		type = intent.getStringExtra("messagelinktype");
		buttonText = intent.getStringExtra("messagebuttontext");
		push_id = intent.getStringExtra("push_id");
		if (link.indexOf("http://") == -1) {
			VsUtil.skipForTarget(link, mContext, 0, null);
		} else {
			VsUtil.schemeToWap(link, title, mContext);
		}
		if (push_id != null) {
			TreeMap<String, String> feedBackParams = new TreeMap<String, String>();
			feedBackParams.put("push_id", push_id);
			CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_PUSHNOTIFY,
                    DfineAction.authType_AUTO, feedBackParams, GlobalVariables.actionPushNotify);
		}
		finish();
	}

}
