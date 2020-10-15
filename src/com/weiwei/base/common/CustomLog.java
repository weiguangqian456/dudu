package com.weiwei.base.common;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class CustomLog {

	public static boolean printlog = true;

	public static void i(String tag, String msg) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.e(tag, msg);
		}

	}

	//
	// public static void e(String tag, String msg, Throwable tr) {
	// if (msg == null)
	// return;
	// if (printlog) {
	// android.util.Log.e(tag, msg, tr);
	// }
	// }
	//
	public static void v(String tag, String msg) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.v(tag, msg);
		}
	}

	//
	// public static void v(String tag, String msg, Throwable tr) {
	// if (msg == null)
	// return;
	// if (printlog) {
	// android.util.Log.v(tag, msg, tr);
	// }
	// }

	public static void w(String tag, String msg) {
		if (msg == null)
			return;
		if (printlog) {
			android.util.Log.w(tag, msg);
		}
	}

	//
	// public static void w(String tag, Throwable tr) {
	// if (tag == null)
	// return;
	// if (printlog) {
	// android.util.Log.w(tag, tr);
	// }
	// }
	//
	// public static void w(String tag, String msg, Throwable tr) {
	// if (msg == null)
	// return;
	// if (printlog) {
	// android.util.Log.w(tag, msg, tr);
	// }
	// }

	public static void setPrintlog(boolean printlog) {
		CustomLog.printlog = printlog;
	}
}