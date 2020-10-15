package com.weiwei.base.common;

import android.app.AlertDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hwtx.dududh.R;

public class VsDownloadProgressDialog {
	private Context context;

	public VsDownloadProgressDialog(Context paramContext) {
		this.context = paramContext;
	}

	private ScrollView getTextView(String paramString) {
		Context localContext1 = this.context;
		ScrollView localScrollView = new ScrollView(localContext1);
		Context localContext2 = this.context;
		TextView localTextView = new TextView(localContext2);
		String str = paramString;
		SpannableString localSpannableString = new SpannableString(str);
		Linkify.addLinks(localSpannableString, 15);
		localTextView.setText(localSpannableString);
		localTextView.setTextColor(context.getResources().getColor(R.color.White));
		MovementMethod localMovementMethod = LinkMovementMethod.getInstance();
		localTextView.setMovementMethod(localMovementMethod);
		localScrollView.setPadding(14, 2, 10, 12);
		localScrollView.addView(localTextView);
		return localScrollView;
	}

	public void show(String paramString1, String paramString2) {
		ScrollView localScrollView = getTextView(paramString2);
		Context localContext = this.context;
		new AlertDialog.Builder(localContext).setTitle(paramString1).setCancelable(true)
				.setIcon(android.R.drawable.ic_dialog_info).setPositiveButton(android.R.string.ok, null)
				.setView(localScrollView).create().show();
	}
}
