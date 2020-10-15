package com.zte.common;

import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

/**
 * @author Jiangxuewu 封装系统相关函数： 1.获取sdk版本号码
 */
public class SystemUtil {
	public static final String OS_ANDROID = "Android";
	private static final String TAG = "SystemUtil";

	/**
	 * 获取当前手机语言设置类别，调用android的local类实现
	 */
	public static final String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * 获取当前手机语言设置类别，调用android的local类实现 中文简体与繁体是通过countryCode来区分
	 */
	public static final String getCountryCode() {
		return Locale.getDefault().getCountry();
	}

	/**
	 * 获取手机品牌
	 */
	public static String getBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取手机型号
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取操作系统型号
	 */
	public static String getOS() {
		return OS_ANDROID;
	}

	/**
	 * 获取操作系统版本
	 */
	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机imei号码
	 * 
	 * <p>
	 * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
	 * READ_PHONE_STATE}
	 */
	public static String getIMEI(Context context) {
		if (null == context) {
			return null;
		}

		// 获取电话服务
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (null == telMgr) {
			Log.d(TAG, "SystemUtil.java, getIMEI(),get telephone service fail!");
			return null;
		}

		return telMgr.getDeviceId();
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @version:2015年1月16日
	 * @author:Jiangxuewu Requires Permission:
	 *                    {@link android.Manifest.permission#READ_PHONE_STATE
	 *                    READ_PHONE_STATE}
	 *
	 */
	public static String getIMSI(Context context) {
		if (null == context) {
			return null;
		}
		// 获取电话服务
		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (null == telMgr) {
			Log.d(TAG, "SystemUtil.java, getIMSI(),get telephone service fail!");
			return null;
		}

		return telMgr.getSubscriberId();
	}

	/**
	 * 获取当前系统时间，单位：毫秒
	 */
	public static long getCurTime() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取随机数
	 * 
	 * @param length
	 *            随机数的长度
	 * @return 下午5:52:20 Jiangxuewu
	 * 
	 */
	public static String getRandom(int length) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sBuffer.append(new Random().nextInt(10));
		}
		return sBuffer.toString();
	}

	/**
	 * 获取系统当前时间，yyyyMMddHHmmss
	 * 
	 * @return 上午2:24:47 Jiangxuewu
	 * 
	 */
	public static String getFormatTime() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	/**
	 * 获取当前系统的SDK版本
	 */
	public static int getSdkLevel() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 获取系统软件包版本号
	 */
	public static String getApkVersion(Context context) {
		if (null == context) {
			return null;
		}

		PackageManager pkgManager = context.getPackageManager();
		if (null == pkgManager) {
			return null;
		}

		try {
			PackageInfo info = pkgManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (null == info) {
				return null;
			}

			return info.versionName;
		} catch (Exception e) {
			Log.d(TAG, "SystemUtil.java,getApkVersion(context),exception occur:" + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取archiveFilePath该文件的版本号
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            为apk的路径
	 * @return 版本号
	 */
	public static String getApkVersion(Context context, String archiveFilePath) {
		if (null == context) {
			return null;
		}

		PackageManager pkgManager = context.getPackageManager();
		if (null == pkgManager) {
			return null;
		}

		try {
			PackageInfo info = pkgManager.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
			if (null == info) {
				return null;
			}

			return info.versionName;
		} catch (Exception e) {
			Log.d(TAG, "SystemUtil.java,getApkVersion(context,archiveFilePath),exception occur:" + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 安装APK
	 */
	@SuppressWarnings("unused")
	public static Intent getInstallApkIntent(String path) {
		if (null == path || path.length() <= 0) {
			return null;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (null == intent) {
			return null;
		}

		intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");

		return intent;
	}

	/**
	 * 获取粘贴板管理类
	 */
	public static ClipboardManager getSystemClipboardManager(Context context) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
		if (null == manager) {
			return null;
		}

		return manager;
	}

	/**
	 * 截屏功能
	 * 
	 * @param v
	 * @param filePath
	 *            图标保存地址
	 */
	public static void screenShot(View v, String filePath) {
		View view = v.getRootView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		if (bitmap != null) {
			Log.i(TAG, "get bitmap!");
			try {
				FileOutputStream out = new FileOutputStream(filePath);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				Log.i(TAG, "get bitmap done :" + filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "get bitmap failed!");
		}
	}

	/**
	 * 获取系统状态栏高度
	 * 
	 * @param view
	 * @return
	 */
	public static int getStatusBarHeight(View view) {
		Rect frame = new Rect();
		view.getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}

	@SuppressLint("NewApi")
	public static void setClipboardText(Context context, String url) {
		android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboardManager.setText(url);
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @version:2015年1月16日
	 * @author:Jiangxuewu <p>
	 *                    Requires Permission:
	 *                    {@link android.Manifest.permission#ACCESS_WIFI_STATE
	 *                    ACCESS_WIFI_STATE}
	 *
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (null != wifi) {
			WifiInfo info = wifi.getConnectionInfo();
			if (null != info) {
				return info.getMacAddress();
			}
		}

		return getLocalIpAddress();
	}

	private static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "getLocalIpAddress() error:" + e.getMessage());
		}
		return null;
	}

	public static boolean isRoot() {
		String result = null;
		try {
			result = ShellUtil.adbshellexecute("su", "echo root", 30 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result != null && result.contains("root")) {
			return true;
		}

		return false;
	}
	
}
