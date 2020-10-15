package com.weiwei.dynamictest.plugin;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;

public class MyContext extends ContextThemeWrapper {
//	private static final String TAG = MyContext.class.getSimpleName();
	private AssetManager mAssetManager = null;
	private Resources mResources = null;
	private Resources.Theme mTheme = null;
//	private int e;
	private ClassLoader mClassLoader;
//	private Context mContext;

	public MyContext(Context paramContext, int paramInt, String paramString, ClassLoader paramClassLoader) {
		super(paramContext, paramInt);
		this.mClassLoader = paramClassLoader;
		this.mAssetManager = createAssetManager(paramString);
		this.mResources = createResources(paramContext, this.mAssetManager);
		this.mTheme = createTheme(this.mResources);
//		this.mContext = paramContext;
	}

	private AssetManager createAssetManager(String apkPath) {
		System.out.println("apkPath:" + apkPath);
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			// ����õ�assetMagCls����ʵ��,�޲�
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			AssetManager assetMag = (AssetManager) assetMagCls.newInstance();
			// ��assetMagCls��õ�addAssetPath����
			Class<?>[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
			Object[] valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			// ִ��assetMag_addAssetPathMtd����
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			return assetMag;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Resources.Theme createTheme(Resources paramResources) {
		Resources.Theme localTheme = paramResources.newTheme();
		// this.e = reflectTheme("com.android.internal.R.style.Theme");
		// localTheme.applyStyle(this.e, true);
		return localTheme;
	}

	private Resources createResources(Context context, AssetManager assetManager) {
		return new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources()
				.getConfiguration());
	}

	public AssetManager getAssets() {
		return this.mAssetManager;
	}

	public ClassLoader getClassLoader() {
		// if (this.classLoader != null);
		// for (ClassLoader localClassLoader = this.classLoader; ;
		// localClassLoader = super.getClassLoader())
		// return localClassLoader;

		return mClassLoader;
	}

	public Resources getResources() {
		return this.mResources;
	}

	public Resources.Theme getTheme() {
		return this.mTheme;
	}
}
