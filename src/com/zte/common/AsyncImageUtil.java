package com.zte.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.FileUtils;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.util.DiskLruCache;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Jiangxuewu on 2015/3/25.
 */
public class AsyncImageUtil {

    /**
     * 图片缓存文件名
     */
    private static final String AD_IMAGE_STORE_PATH = "AD";

    private static final String TAG = AsyncImageUtil.class.getSimpleName();
    private final Handler mHandler;
    /**
     * 存放图片未下载完，且又需要显示的ImageView
     */
    private final HashMap<String, ImageView> mLruCache;
    private final HashMap<String, ImageView> mLruCache_;
    private DiskLruCache mDiskLruCache;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
    private static AsyncImageUtil mInstance;

    public static AsyncImageUtil getInstance() {
        synchronized (TAG) {
            if (null == mInstance) {
                mInstance = new AsyncImageUtil();
            }
            return mInstance;
        }
    }

    private AsyncImageUtil() {
        mHandler = new Handler();
        mLruCache = new HashMap<String, ImageView>();
        mLruCache_ = new HashMap<String, ImageView>();
        File dirFile = null;
        try {
            dirFile = FileUtils.createDirFile(FileUtils.getSaveFilePath() + AD_IMAGE_STORE_PATH);
        } catch (IOException e) {
        }
        try {
            mDiskLruCache = DiskLruCache.open(dirFile, Build.VERSION.SDK_INT, 1, 1024 * 1024 * 10);
        } catch (IOException e) {
        }
    }

    /**
     * 下载网络图片到本地
     *
     * @param imageUrl 图片网络地址
     */
    public void downloadImage(final String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)){
            return;
        }

        boolean isExist = false;
        try {
            DiskLruCache.Snapshot snap = mDiskLruCache.get(getFileName(imageUrl));
            if (null != snap) {
                Bitmap bitmap = null;
                InputStream is = snap.getInputStream(0);
                if (null != is) {
                    bitmap = BitmapFactory.decodeStream(is);
                }
                isExist = bitmap != null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isExist){
            CustomLog.d(TAG, "image is exist in sd card.");
            return;
        } else {
            CustomLog.d(TAG, "image is not exist in sd card. getFilePath(imageUrl) = " + getFilePath(imageUrl) + ", imageUrl = " + imageUrl);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    DiskLruCache.Editor editor = mDiskLruCache.edit(getFileName(imageUrl));

                    if (null != editor) {
                        OutputStream opt = editor.newOutputStream(0);
                        if (downloadUrlToStream(imageUrl, opt)) {
                            editor.commit();
                            if (mLruCache.containsKey(imageUrl)) {
                                ImageView i = mLruCache.get(imageUrl);
                                if (null != i) {
                                    initImageView(i, imageUrl);
                                }
                            }
                            if (mLruCache_.containsKey(imageUrl)) {
                                ImageView i = mLruCache_.get(imageUrl);
                                if (null != i) {
                                    initImageView_(i, imageUrl);
                                }
                            }
                        } else {
                            editor.abort();
                        }
                    }

                    mDiskLruCache.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        GlobalVariables.fixedThreadPoolImageDown.execute(runnable);

    }

    /**
     * 图片下载
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
//            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
//                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取网络图片
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmap(String imageUrl) {
        try {
            DiskLruCache.Snapshot snap = mDiskLruCache.get(getFileName(imageUrl));
            if (null != snap) {
                Bitmap bitmap = null;
                InputStream is = snap.getInputStream(0);
                if (null != is) {
                    bitmap = BitmapFactory.decodeStream(is);
                }
                return bitmap;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 获取图片文件存储路劲
     *
     * @param imageUrl
     * @return
     */
    public String getFilePath(String imageUrl) {

        return FileUtils.getSaveFilePath() + AD_IMAGE_STORE_PATH + File.separator + getFileName(imageUrl);
    }

    /**
     * 获取图片文件存储名称
     *
     * @param imageUrl
     * @return
     */
    public String getFileName(String imageUrl) {

        return MD5.encode(imageUrl);
    }

    /**
     * @param imageView
     * @param imageUrl
     */
    public void initImageView(final ImageView imageView, final String imageUrl) {
       /* final Bitmap bm = getBitmap(imageUrl);
        if (null != mHandler) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (bm != null) {
                        imageView.setBackgroundDrawable(new BitmapDrawable(bm));
                        if (mLruCache.containsKey(imageUrl)) {
                            mLruCache.remove(imageUrl);
                        }
                    } else {
                        imageView.setBackgroundColor(Color.GRAY);
                        if (!mLruCache.containsKey(imageUrl)) {
                            mLruCache.put(imageUrl, imageView);
                        }
                    }
                }
            });
        }*/
    	imageLoader.displayImage(imageUrl, imageView);
    	
    }

    /**
     * 两个广告时候， 暗地新增两个控件更新使用
     * @param imageView
     * @param imageUrl
     */
    public void initImageView_(final ImageView imageView, final String imageUrl) {
        /*final Bitmap bm = getBitmap(imageUrl);
        if (null != mHandler) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (imageUrl.length()>1) {
                    	
                        imageView.setBackgroundDrawable(new BitmapDrawable(bm));
                        if (mLruCache_.containsKey(imageUrl)) {
                            mLruCache_.remove(imageUrl);
                        }
                    } else {
                        imageView.setBackgroundColor(Color.GRAY);
                        if (!mLruCache_.containsKey(imageUrl)) {
                            mLruCache_.put(imageUrl, imageView);
                        }
                    }
                }
            });
        }*/
    	imageLoader.displayImage(imageUrl, imageView);
    }

}
