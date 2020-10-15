package com.weiwei.dynamictest.plugin;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class PluginProxyActivity extends Activity {

	private Toast toast = null;
	private IActivity pluginActivity = null;  //待加载的apk中的pluginActivity子类
	private String pluginLocation = null;  //apk路径 
    private int numActivity=0;     //启动apk中哪个activity,顺序由加载的apk包中的mainfest.xml顺序决定
	private String activityName = null; //启动的Activity名
	
	@SuppressLint("ShowToast")
	public void showToast(CharSequence paramCharSequence) {
		if (this.toast == null)
			this.toast = Toast.makeText(this, paramCharSequence, 0);
		this.toast.setText(paramCharSequence);
		this.toast.show();
	}
	protected void onCreate(Bundle paramBundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(paramBundle);
		//获取传递过来的信息
		Intent localIntent = getIntent();
		this.pluginLocation = localIntent.getStringExtra("pluginLocation");
		this.activityName = localIntent.getStringExtra("activityName");
		this.numActivity=localIntent.getIntExtra("numActivity", 0);
		//判断apk是否存在
		if ((this.pluginLocation == null) || (this.pluginLocation.length() == 0)) {
			showToast("APK启动失败, 文件不存在");
			finish();
		}
		if (!new File(this.pluginLocation).exists()) {
			showToast("文件不存在" + this.pluginLocation + "上");
			finish();
		}
		//启动activity
		//localIntent.putExtra("IsPluginActivity", false);
		String launchActivityName = localIntent.getComponent().getClassName();
		if ((launchActivityName != null) && (launchActivityName.length() > 0)) {
			startPlugin(pluginLocation);
		}
	}
	@SuppressLint("NewApi")
	public void startPlugin(String pakName) {
		//PackageInfo packageInfo = ApkUtil.getPackageInfo(pakName, 1); //获取apk包中的信息
		PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(pakName, PackageManager.GET_ACTIVITIES);
		if ((packageInfo.activities == null) || (packageInfo.activities.length == 0)) {
			return;
		}
		System.out.println("VERSION_NAME:" + packageInfo.versionName + "   VERSION_CODE:" + packageInfo.versionCode);
		try {
			DexClassLoader dexClassLoader = new DexClassLoader(pakName, getCacheDir().getCanonicalPath(), null,super.getClassLoader());
			if (!(this.activityName != null && this.activityName.length() > 0)) {
				this.activityName = packageInfo.activities[numActivity].name;
			}
			Class<?> pluginActivityDexClass = dexClassLoader.loadClass(activityName);
			this.pluginActivity = ((IActivity) pluginActivityDexClass.newInstance());
			this.pluginActivity.IInit(pakName, this, dexClassLoader, packageInfo);
			this.pluginActivity.ISetIntent(getIntent()); //设置父Intent
			this.pluginActivity.IOnCreate(null);         //启动actviity
			//luginActvitiy:IOnCreate  --> PluginActvitiy自己的:onCreate  -->  onCreate在子类中实现
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
   //下面的是本activity-proxyactivity的生命周期，在其中绑定pluginactvitiy的生命周期，具体实现在pluginactvitiy的子类中实现
	//如proxy:onDestroy->PluginActvitiy:IOnDestroy->PluginActvitiy自己的:OnDestroy->OnDestroy在子类中实现
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (this.pluginActivity != null) {
				this.pluginActivity.IOnDestroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		boolean bool = false;
		if (this.pluginActivity != null) {
			bool = this.pluginActivity.IOnKeyDown(paramInt, paramKeyEvent);
		}
		if (!bool) {
			bool = super.onKeyDown(paramInt, paramKeyEvent);
		}
		return bool;
	}

	protected void onPause() {
		super.onPause();
		if (this.pluginActivity != null)
			this.pluginActivity.IOnPause();
	}

	protected void onResume() {
		super.onResume();
		if (this.pluginActivity != null) {
			this.pluginActivity.IOnResume();
		}
	}

	protected void onStop() {
		super.onStop();
		if (this.pluginActivity != null)
			this.pluginActivity.IOnStop();
	}
}
