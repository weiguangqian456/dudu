package com.weiwei.base.cache;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;

public class FileCache {
	private File cacheDir;

	public FileCache(Context context) {
		// 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
		// 没有SD卡就放在系统的缓存目录中
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			// cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
			cacheDir = new File(Environment.getExternalStorageDirectory() + File.separator + "data" + File.separator
					+ "wldh" + File.separator + "download_image" + File.separator);
		} else {
			// cacheDir = context.getCacheDir();
			cacheDir = new File(VsApplication.getContext().getFilesDir().getPath() + File.separator + "download_image"
					+ File.separator);
		}
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		
		CustomLog.i("vsdebug", cacheDir.getPath()+"--");
	}

	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名
		String filename = String.valueOf(url.hashCode());
		// Another possible solution
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}
