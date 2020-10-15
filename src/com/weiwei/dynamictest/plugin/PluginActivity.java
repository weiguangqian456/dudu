package com.weiwei.dynamictest.plugin;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;

public class PluginActivity extends Activity implements IActivity {

	private static Handler mHandler;
	private static HandlerThread mHandlerThread;
	private static final Map<String, Context> sID2Context = new HashMap<String, Context>();
	private Activity mActivity = null;
	protected String mApkPath = "";
	public View mContentView = null;
	private Context mContext = null;
	private ClassLoader mDexClassLoader = null;
	boolean mFinished = false;
	protected boolean mIsRunInPlugin = false;
	protected Activity mOutActivity = null;
	protected PackageInfo mPackageInfo;

	/*
	 * private void loadPackageInfo() { this.mPackageInfo = ApkUtil.getPackageInfo(this.mApkPath, 64); }
	 */

	public static void postRunnable(Runnable paramRunnable) {
		if (mHandler == null) {
			if (mHandlerThread == null) {
				mHandlerThread = new HandlerThread("pluginThread");
				mHandlerThread.start();
			}
			mHandler = new Handler(mHandlerThread.getLooper());
		}
		mHandler.post(paramRunnable);
	}

	public View IGetContentView() {
		return this.mContentView;
	}

	public Handler IGetInHandler() {
		return null;
	}

	public Resources IGetResource() {
		if (this.mContext != null) {
			return mContext.getResources();
		}
		return mActivity.getResources();
	}

	public void IInit(String apkPath, Activity outActivity, ClassLoader classLoader, PackageInfo packageInfo) {
		this.mIsRunInPlugin = true;
		this.mDexClassLoader = classLoader;
		this.mOutActivity = outActivity;
		this.mApkPath = apkPath;
		this.mPackageInfo = packageInfo;
		this.mContext = ((Context) sID2Context.get(this.mApkPath));
		if (this.mContext == null) {
			this.mContext = new MyContext(outActivity, 0, this.mApkPath, this.mDexClassLoader);
			sID2Context.put(this.mApkPath, this.mContext);
		}
		// attachBaseContext(this.mContext);
	}

	public void Iinit_activity(Activity outActivity) {
		this.mOutActivity = outActivity;
	}

	public String getDataString(String key) {
		return VsUserConfig.getDataString(mContext, key);
	}

	public void IOnCreate(Bundle paramBundle) {
		System.out.println("...IOnCreate()...");
		this.mActivity = this.mOutActivity;
		onCreate(paramBundle);
	}

	public void IOnDestroy() {
		onDestroy();
	}

	// public boolean IOnKeyDown(int paramInt, KeyEvent paramKeyEvent) {
	// return onKeyDown(paramInt, paramKeyEvent);
	// }
	//
	// public boolean IOnKeyMultiple(int paramInt1, int paramInt2, KeyEvent
	// paramKeyEvent) {
	// return onKeyMultiple(paramInt1, paramInt2, paramKeyEvent);
	// }
	//
	// public boolean IOnKeyUp(int paramInt, KeyEvent paramKeyEvent) {
	// return onKeyUp(paramInt, paramKeyEvent);
	// }
	public ProgressDialog mProgressDialog;

	public void loadProgressDialog(String message) {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		mProgressDialog = new ProgressDialog(this.mOutActivity);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	/**
	 * 广播接收器
	 */
	protected VsBroadcastReceiver vsBroadcastReceiver;

	/**
	 * 注销广播接收器
	 */
	protected void unregisterVsBroadcast() {
		if (vsBroadcastReceiver != null) {
			this.mOutActivity.unregisterReceiver(vsBroadcastReceiver);
			vsBroadcastReceiver = null;
		}
	}

	protected void loadProgressDialog(String message, boolean Cancelable) {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		mProgressDialog = new ProgressDialog(this.mOutActivity);
		mProgressDialog.setCancelable(Cancelable);// 设置是否可以取消
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(message);
		mProgressDialog.setView(this.mContentView);
		mProgressDialog.show();
	}

	public class VsBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleVsBroadcast(context, intent);
		}
	}

	/**
	 * 处理收到的公告信息
	 * 
	 * @param context
	 * @param intent
	 */
	protected void handleVsBroadcast(Context context, Intent intent) {
		unregisterVsBroadcast();
		String msg = intent.getStringExtra(VsCoreService.VS_KeyMsg);
		CustomLog.i("GDK", "收到签到返回信息" + msg);
	}

	public void bindBroadcast(String action) {
		// 绑定广播接收广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);
		vsBroadcastReceiver = new VsBroadcastReceiver();
		this.mOutActivity.registerReceiver(vsBroadcastReceiver, filter);

	}

	/**
	 * 销毁等待界面
	 * 
	 * @param message
	 */
	protected void dismissProgressDialog() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = null;
	}

	public void IOnPause() {
		onPause();
	}

	public void IOnRestart() {
		onRestart();
	}

	public void IOnResume() {
		onResume();
	}

	public void IOnStart() {
		onStart();
	}

	public void IOnStop() {
		onStop();
	}

	public void ISetIntent(Intent paramIntent) {
		// setIntent(paramIntent);
	}

	public void ISetOutHandler(Handler paramHandler) {
	}

	public Context IGetContext() {
		return this.mContext;
	}

	@SuppressWarnings("unused")
	public View findViewById(int paramInt) {
		if ((this.mIsRunInPlugin) && (this.mContentView != null)) {

		}
		for (View localView = this.mContentView.findViewById(paramInt);; localView = super.findViewById(paramInt))
			return localView;
	}

	public void finish() {
		if (this.mIsRunInPlugin) {
			this.mOutActivity.finish();
			this.mFinished = true;
		}
		// while (true) {
		// return;
		// super.finish();
		// }
	}

	public ApplicationInfo getApplicationInfo() {
		return this.mPackageInfo.applicationInfo;
	}

	public int getChangingConfigurations() {
		return this.mActivity.getChangingConfigurations();
	}

	public LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(this.mContext);
	}

	public Activity getOutActivity() {
		return this.mOutActivity;
	}

	@SuppressWarnings("unused")
	public Resources getOutResources() {
		if (this.mIsRunInPlugin == true)
			;
		for (Resources localResources = this.mOutActivity.getResources();; localResources = this.mActivity
				.getResources())
			return localResources;
	}

	public PackageInfo getPackageInfo() {
		return this.mPackageInfo;
	}

	public String getPackageName() {
		return this.mPackageInfo.packageName;
	}

	@SuppressWarnings("unused")
	public Object getSystemService(String paramString) {
		if (("window".equals(paramString)) || ("search".equals(paramString)))
			;
		for (Object localObject = this.mActivity.getSystemService(paramString);; localObject = this.mContext
				.getSystemService(paramString))
			return localObject;
	}

	protected PluginTableManager getTableManager() {
		return null;
	}

	public Window getWindow() {
		System.out.println("----");
		System.out.println(this.mActivity.toString());
		return this.mActivity.getWindow();
	}

	public WindowManager getWindowManager() {
		return this.mActivity.getWindowManager();
	}

	// public boolean isFinishing() {
	// if (this.mIsRunInPlugin)
	// ;
	// for (boolean bool = this.mFinished;; bool = super.isFinishing())
	// return bool;
	// }

	protected void onCreate(Bundle paramBundle) {
		Log.i("sys", "PluginActivity onCreate������");
		System.out.println("isRunInPlugin:" + this.mIsRunInPlugin);
		System.out.println("========:" + this.mOutActivity);
		if (this.mIsRunInPlugin) {
			this.mActivity = this.mOutActivity;
		}

	}

	protected void onDestroy() {
		Log.i("sys", "PluginActivity onDestroy������");
		if (this.mIsRunInPlugin)
			;
	}

	// public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
	// if (this.mIsRunInPlugin)
	// ;
	// for (boolean bool = false;; bool = super.onKeyDown(paramInt,
	// paramKeyEvent)) {
	// return bool;
	// }
	// }
	//
	// public boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent
	// paramKeyEvent) {
	// if (this.mIsRunInPlugin)
	// ;
	// for (boolean bool = false;; bool = super.onKeyMultiple(paramInt1,
	// paramInt2, paramKeyEvent)) {
	// return bool;
	// }
	// }
	//
	// public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
	// if (this.mIsRunInPlugin)
	// ;
	// for (boolean bool = false;; bool = super.onKeyUp(paramInt,
	// paramKeyEvent)) {
	// return bool;
	// }
	// }

	protected void onPause() {
		Log.i("sys", "PluginActivity onPause������");
		if (this.mIsRunInPlugin)
			;

	}

	protected void onRestart() {
		Log.i("sys", "PluginActivity onRestart������");
		if (this.mIsRunInPlugin)
			;

	}

	protected void onResume() {
		Log.i("sys", "PluginActivity onResume������");
		if (this.mIsRunInPlugin)
			;

	}

	protected void onStart() {
		Log.i("sys", "PluginActivity onStart������");
		if (this.mIsRunInPlugin)
			;

	}

	protected void onStop() {
		Log.i("sys", "PluginActivity onStop������");
		if (this.mIsRunInPlugin)
			;

	}

	public void setContentView(int paramInt) {
		if (this.mIsRunInPlugin) {
			this.mContentView = LayoutInflater.from(this.mContext).inflate(paramInt, null);
			this.mActivity.setContentView(this.mContentView);
		}
	}

	public void setContentView(View paramView) {
		this.mContentView = paramView;
		this.mActivity.setContentView(paramView);
	}

	// public void setRequestedOrientation(int paramInt) {
	// super.setRequestedOrientation(paramInt);
	// }

	@SuppressWarnings("static-access")
	public void startActivity(Intent paramIntent) {
		paramIntent.setFlags(paramIntent.FLAG_ACTIVITY_NEW_TASK);
		// List localList = this.mActivity.getPackageManager().queryIntentActivities(paramIntent, 65536);
		// CustomLog.i("GDK", "localList=" + localList);
		// if ((localList != null) && (localList.size() != 0)) {
		// KcApplication.getContext().startActivity(paramIntent);
		// }
		//
		// if (this.mIsRunInPlugin) {
		// paramIntent.putExtra("IsPluginActivity", true);
		// paramIntent.putExtra("pluginLocation", this.mApkPath);
		// paramIntent.putExtra("activityName", paramIntent.getComponent().getClassName());
		VsApplication.getContext().startActivity(paramIntent);
		// }
	}

	@Override
	public boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean IOnKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
		return false;
	}
}
