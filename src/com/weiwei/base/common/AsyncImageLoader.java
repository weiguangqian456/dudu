package com.weiwei.base.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.item.ImageData;

/**
 * 异步加载图片类，内部有缓存，可以通过后台线程获取网络图片。首先生成一个实例，并调用loadDrawableByTag方法来获取一个Drawable对象
 */
@SuppressLint("HandlerLeak")
public class AsyncImageLoader {
	public String ALBUM_PATH;
	String TAG = "AsyncImageLoader";

	public AsyncImageLoader() {
		if (VsUpdateAPK.IsCanUseSdCard()) {
			ALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + "data" + File.separator + "wldh"
					+ File.separator + "download_image" + File.separator;
		} else {
			ALBUM_PATH = VsApplication.getContext().getFilesDir().getPath() + File.separator + "download_image"
					+ File.separator;
		}
	}

	public AsyncImageLoader(boolean isInformation) {
		if (VsUpdateAPK.IsCanUseSdCard()) {
			ALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + "data" + File.separator + "wldh"
					+ File.separator + "download_image" + File.separator + "information" + File.separator;
		} else {
			ALBUM_PATH = VsApplication.getContext().getFilesDir().getPath() + File.separator + "download_image"
					+ File.separator + "information" + File.separator;
		}
	}

	/**
	 * 通过传入的TagInfo来获取一个网络上的图片
	 * 
	 * @param tag
	 *            TagInfo对象，保存了position、url和一个待获取的Drawable对象
	 * @param redown
	 *            重新下载图片
	 * @param isInformation
	 *            是否是富媒体图片
	 * @param callback
	 *            ImageCallBack对象，用于在获取到图片后供调用侧进行下一步的处理
	 * @return drawable 从网络或缓存中得到的Drawable对象，可为null，调用侧需判断
	 */
	public ImageData loadDrawableByTag(final TagInfo tag, final boolean isInformation, final ImageCallBack callback) {
		// 如果是富媒体图片 文件名以msgid+index方式保存
		final String fileName = tag.getUrl().substring(tag.getUrl().lastIndexOf("/") + 1);	
		// KcUtil.System("fileName =========="+fileName);
		File file = new File(ALBUM_PATH + fileName);
		if (file.exists()) {
			ImageData imgData = new ImageData();
			String endstr = fileName.substring(fileName.length() - 4, fileName.length());
			if (".gif".equals(endstr)) {
				imgData.setImgType(GlobalVariables.IMAGE_TYPE_JIF);
			} else {
				imgData.setImgType(GlobalVariables.IMAGE_TYPE_PNGORJPG);
			}
			Uri uri = Uri.fromFile(file);
			imgData.setUri(uri);
			return imgData;
		}

		/**
		 * 用于在获取到网络图片后，保存图片到缓存，并触发调用侧的处理
		 */
		// final Handler handler = new Handler() {
		// @Override
		// public void handleMessage(Message msg) {
		// TagInfo info = (TagInfo) msg.obj;
		// callback.obtainImage(info);
		// super.handleMessage(msg);
		// }
		// };
		final Handler handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				TagInfo info = (TagInfo) msg.obj;
				callback.obtainImage(info);
				return false;
			}
		});

		/**
		 * 如果在缓存中没有找到，则开启一个线程来进行网络请求
		 */
		BaseRunable newRunable = new BaseRunable() {
			public void run() { // 线程运行主体
				TagInfo info = getDrawableIntoTag(tag, fileName);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = info;
				handler.sendMessage(msg);
			}
		};
		// 使用线程池进行管理
		GlobalVariables.fixedThreadPool.execute(newRunable);
		return null;
	}

	/**
	 * 通过传入的TagInfo对象，利用其URL属性，到网络请求图片，获取到图片后保存在TagInfo的Drawable属性中，并返回该TagInfo
	 * 
	 * @param info
	 *            TagInfo对象，需要利用里面的url属性
	 * @return TagInfo 传入的TagInfo对象，增加了Drawable属性后返回
	 */
	public TagInfo getDrawableIntoTag(TagInfo info, String fileName) {
		final String imgUrl = info.getUrl();
		try {
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// InputStream input = (InputStream) request.getContent();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				ImageData imgData = new ImageData();
				String endstr = imgUrl.substring(imgUrl.length() - 4, imgUrl.length());
				if (".gif".equals(endstr)) {
					imgData.setImgType(GlobalVariables.IMAGE_TYPE_JIF);
				} else {
					imgData.setImgType(GlobalVariables.IMAGE_TYPE_PNGORJPG);
					// Drawable drawable = Drawable.createFromStream(is, "src"); // 第二个属性可为空，为DEBUG下使用，网上的说明
				}
				Uri uri = saveFile(is, fileName);
				is.close();
				is = null;
				imgData.setUri(uri);
				info.setImgData(imgData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 保存文件
	 * 
	 * @param is
	 * @param fileName
	 * @throws IOException
	 */
	private Uri saveFile(InputStream is, String fileName) throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists())
			dirFile.mkdir();
		CustomLog.i(TAG, "fileName====" + fileName);
		File file = new File(ALBUM_PATH + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		fos.flush();
		fos.close();
		fos = null;
		return Uri.fromFile(file);
	}

	/**
	 * 获取图片的回调接口，里面的obtainImage方法在获取到图片后进行调用
	 */
	public interface ImageCallBack {
		/**
		 * 获取到图片后在调用侧执行具体的细节
		 * 
		 * @param info
		 *            TagInfo对象，传入的info经过处理，增加Drawable属性，并返回给传入者
		 */
		public void obtainImage(TagInfo info);
	}
}