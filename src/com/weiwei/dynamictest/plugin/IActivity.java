/**
 * 
 */
package com.weiwei.dynamictest.plugin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * @author Administrator
 * 
 */
public interface IActivity {

	public void IInit(String paramString, Activity paramActivity, ClassLoader paramClassLoader,
			PackageInfo paramPackageInfo);

	public void IOnCreate(Bundle paramBundle);

	public void IOnDestroy();

	public boolean IOnKeyDown(int paramInt, KeyEvent paramKeyEvent);

	public void IOnPause();

	public void IOnResume();

	public void IOnStop();

	public void ISetIntent(Intent paramIntent);

	public boolean isRouteDisplayed();

}
