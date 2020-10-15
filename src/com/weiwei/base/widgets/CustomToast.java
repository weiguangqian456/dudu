package com.weiwei.base.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class CustomToast {
	final Context mContext;
	private Toast mToast;

	public CustomToast(Context context) {
		mContext = context;
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}

	public void show(int resId, int duration) {
		show(mContext.getText(resId), duration);
	}

	public void show(CharSequence s, int duration) {
		mToast.setDuration(duration);
		mToast.setText(s);
		mToast.show();
	}
	public void show(CharSequence s) {
		mToast.setText(s);
		mToast.show();
	}
	public void cancel() {
		mToast.cancel();
	}
}
