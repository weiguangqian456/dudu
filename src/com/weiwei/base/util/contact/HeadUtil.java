package com.weiwei.base.util.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import com.hwtx.dududh.R;

/**
 * 联系人头像管理工具
 * 
 * @author Jiangxuewu
 *
 */
@SuppressLint("NewApi")
public class HeadUtil {

	private LruCache<String, Bitmap> mCache;// key ： Image

	private static HeadUtil mInstance = null;

	private OnDoneListener mListener;

	private Bitmap mDefBm;

	/**
	 *  Constructor
	 * @param context
	 * @return
	 * @version:2015年1月16日
	 * @author:Jiangxuewu
	 *
	 */
	public static HeadUtil getInstance(Context context) {
		synchronized (HeadUtil.class.getName()) {
			if (null == mInstance) {
				mInstance = new HeadUtil(context);
			}
		}
		return mInstance;
	}

	private HeadUtil(Context context) {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		mDefBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

		mCache = new LruCache<String, Bitmap>(maxMemory / 8) {
			@Override
			protected int sizeOf(String key, Bitmap bm) {
				return bm.getByteCount() / 1024;
			}
		};
	}

	private void addBitmap(String key, Bitmap bm) {
		if (null != mCache.get(key)) {
			mCache.remove(key);
		}
		mCache.put(key, bm);
	}

	/**
	 * 获取图片
	 * @param key 图片id
	 * @return
	 * @version:2015年1月16日
	 * @author:Jiangxuewu
	 *
	 */
	public Bitmap getBitmap(String key) {
		Bitmap bm = mCache.get(key);
		if (null == bm) {
			new WorkTask().equals(key);
			bm = mDefBm;
		}
		return bm;
	}

	/**
	 * 设置回调，图片加载完毕后更新界面
	 * @param listener
	 * @version:2015年1月16日
	 * @author:Jiangxuewu
	 *
	 */
	public void setListener(OnDoneListener listener) {
		mListener = listener;
	}

	private Bitmap loadBitmap(String key) {
		// TODO 实现图片加载
		// TODO 图片加载前, 先判断是否已在队列中
		// TODO 优先去Disk上加载图片，再去网络上加载
		return mDefBm;
	}

	private class WorkTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			String key = params[0];
			Bitmap bm = loadBitmap(key);
			if (bm == mDefBm) {
				return bm;
			}
			
			
			
			addBitmap(key, bm);
			saveBitmapToSdcard(key, bm);
			return bm;
		}

		private void saveBitmapToSdcard(String key, Bitmap bm) {
			// TODO 保存图像到Disk
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (null != mListener && null != result) {
				mListener.refresh();
			}
		}
	}

	public interface OnDoneListener {
		/**
		 * 图片加载完毕后，更新界面
		 * 
		 * @version:2015年1月16日
		 * @author:Jiangxuewu
		 *
		 */
		public void refresh();
	}

}
