package com.weiwei.base.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.R;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.provider.FileProvider7;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sundy
 * @ClassName: VsUpdateAPK
 * @Description: 升级
 * @date 2015年1月4日 下午4:31:29
 */
public class VsUpdateAPK {

    private static final String TAG = VsUpdateAPK.class.getSimpleName();
    Context mContext = null;
    private int dlsize = 0;
    private int maxsize = 0;
    private final int SET_SUCCEED = 0;
    private final int SET_PROGRESS = 1;
    private final int SET_ERRORMSG = 2;
    private final int NM_SET_SUCCEED = 10;
    private final int NM_SET_PROGRESS = 11;
    private final int NM_SET_ERRORMSG = 12;
    private String SaveFileName = "vs2011.apk";

    private NotificationManager manager = null;
    private Notification notif;

    private ProgressDialog progressdialog = null;

    public VsUpdateAPK(Context ts) {
        mContext = ts;
    }

    /**
     * 通知栏下载
     *
     * @param sur
     * @param needStart
     * @param cls
     */
    public void NotificationDowndload(final String sur, final boolean needStart, Class<?> cls) {
        Intent intent;
        if (cls == null) {
            intent = new Intent();
        } else {// cls:点击通知栏后打开的activity
            intent = new Intent(mContext, cls);
        }

        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.drawable.icon;
        notif.tickerText = mContext.getResources().getString(R.string.upgrade_download_notify);
        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(mContext.getPackageName(), R.layout.vs_content_view);
        notif.contentIntent = pIntent;
        manager.notify(0, notif);
        new DownLoadThread(sur, needStart, true).start();
    }

    Timer timer = null;

    public class DownLoadThread extends Thread {
        private String url;
        private boolean needStart;
        private boolean isnm;

        /**
         * 下载线程构造
         *
         * @param sur       下载URL
         * @param needStart 是否启动
         * @param isnm      通知栏下载
         */
        public DownLoadThread(final String sur, final boolean needStart, final boolean isnm) {
            this.url = sur;
            this.needStart = needStart;
            this.isnm = isnm;
            timer = new Timer();
        }

        @Override
        public void run() {
            super.run();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CustomLog.i("DGK", "maxsize = " + maxsize + ", dlsize = " + dlsize);
                    if (isnm) {
                        mhandler.sendEmptyMessage(NM_SET_PROGRESS);
                        if (maxsize != 0 && dlsize >= maxsize) {
                            timer.cancel();
                            mhandler.sendEmptyMessage(NM_SET_SUCCEED);
                        }
                    } else {
                        mhandler.sendEmptyMessage(SET_PROGRESS);
                        if (maxsize != 0 && dlsize >= maxsize) {
                            timer.cancel();
                            mhandler.sendEmptyMessage(SET_SUCCEED);
                        }
                    }
                }
            }, 0, 1000);
            Download(url, needStart, isnm);
        }
    }

    /**
     * 版本升级
     *
     * @param sur
     * @param needStart
     */
    public void DowndloadThread(final String sur, final boolean needStart) {
        progressdialog = new ProgressDialog(mContext);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressdialog.setTitle(mContext.getResources().getString(R.string.upgrade_update));
        progressdialog.setMessage(mContext.getResources().getString(R.string.upgrade_getsize));
        progressdialog.show();
        new DownLoadThread(sur, needStart, false).start();
    }

    //
    // Handler mhandler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // switch (msg.what) {
    // case SET_PROGRESS:
    // if (maxsize != 0) {
    // int curmaxsize = maxsize / 1024;
    // int curdlsize = dlsize / 1024;
    // progressdialog.setMax(curmaxsize);
    // progressdialog.setMessage(String.format(
    // mContext.getResources().getString(R.string.upgrade_havedownload),
    // curmaxsize, curdlsize));
    // progressdialog.setProgress(curdlsize);
    // }
    // break;
    // case SET_SUCCEED:
    // progressdialog.cancel();
    // progressdialog = null;
    // break;
    // case NM_SET_PROGRESS:
    // String hint_str;
    // if (maxsize == 0) {
    // hint_str =
    // mContext.getResources().getString(R.string.upgrade_checking_version);
    // } else {
    // hint_str =
    // mContext.getResources().getString(R.string.upgrade_havedownload, maxsize
    // / 1024,
    // dlsize / 1024);
    // }
    // notif.contentView.setTextViewText(R.id.content_view_text1, hint_str);
    // notif.contentView.setProgressBar(R.id.content_view_progress, maxsize,
    // dlsize, false);
    // manager.notify(0, notif);
    // break;
    // case NM_SET_SUCCEED:
    // manager.cancel(0);
    // break;
    // case SET_ERRORMSG:
    // case NM_SET_ERRORMSG:
    // final String str = msg.getData().getString("erormsg");
    // ErrorShowDialog(str);
    // break;
    // default:
    // break;
    // }
    // }
    // };
    Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SET_PROGRESS:
                    if (maxsize != 0) {
                        int curmaxsize = maxsize / 1024;
                        int curdlsize = dlsize / 1024;
                        progressdialog.setMax(curmaxsize);
                        progressdialog.setMessage(String.format(mContext.getResources().getString(R.string.upgrade_havedownload), curmaxsize, curdlsize));
                        progressdialog.setProgress(curdlsize);
                    }
                    break;
                case SET_SUCCEED:
                    progressdialog.cancel();
                    progressdialog = null;
                    break;
                case NM_SET_PROGRESS:
                    String hint_str;
                    if (maxsize == 0) {
                        hint_str = mContext.getResources().getString(R.string.upgrade_checking_version);
                    } else {
                        if (maxsize / 1024 < 1024) {
                            hint_str = mContext.getResources().getString(R.string.upgrade_havedownload, maxsize / 1024, dlsize / 1024);
                        } else {
                            hint_str = mContext.getResources().getString(R.string.upgrade_havedownload, VsUtil.kbToM(maxsize / 1024), VsUtil.kbToM(dlsize / 1024));
                            hint_str = hint_str.replaceAll("K", "M");
                        }

                    }
                    notif.contentView.setTextViewText(R.id.content_view_text1, hint_str);
                    notif.contentView.setProgressBar(R.id.content_view_progress, maxsize, dlsize, false);
                    manager.notify(0, notif);
                    break;
                case NM_SET_SUCCEED:
                    manager.cancel(0);
                    break;
                case SET_ERRORMSG:
                case NM_SET_ERRORMSG:
                    final String str = msg.getData().getString("erormsg");
                    ErrorShowDialog(str);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public void updateRate(int rate, final boolean isnm) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("rate", rate);
        msg.what = isnm ? NM_SET_PROGRESS : SET_PROGRESS;
        msg.setData(data);
        mhandler.sendMessage(msg);
    }

    public void ErrorShowDialog(String str) {
        if (progressdialog != null) progressdialog.dismiss();
        if (timer != null) {
            timer.cancel();
        }
        try {
            VsDownloadProgressDialog mad = new VsDownloadProgressDialog(mContext);
            mad.show(mContext.getResources().getString(R.string.upgrade_update), mContext.getResources().getString(R.string.upgrade_updateerror) + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ErrorDialog(String str) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("erormsg", str);
        msg.what = SET_ERRORMSG;
        msg.setData(data);
        mhandler.sendMessage(msg);
    }

    /**
     * @param surl
     * @param needStart
     * @param isnm      是否在通知栏下下载
     */
    public synchronized void Download(String surl, boolean needStart, final boolean isnm) {
        String savePath;
        if (surl == null || surl.length() < 2) {
            ErrorDialog("Http address null!");
            return;
        }

        String version = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version, "");
        String appName = mContext.getResources().getString(R.string.app_name);
        SaveFileName = appName + "_V" + version + ".apk";

        if (VsUpdateAPK.IsCanUseSdCard()) {
            savePath = DfineAction.mWldhRootFilePath;
        } else {
            savePath = mContext.getFilesDir().getPath() + File.separator;
        }

        File tmpFile = new File(savePath);
        if (tmpFile.exists()) {
            // 继续
        } else {
            tmpFile.mkdir();// 新建文件夹
        }
        if (savePath == null || "" == savePath) {
            ErrorDialog("无法取得保存apk的路径");
            return;
        }

        final File file = new File(savePath + SaveFileName);

        if (file.exists()) {// 如果文件存在就删除
            FileUtils.deleteFile(savePath + SaveFileName);
        }

        // 判断文件是否存在，不存在就去下载
        if (!file.exists()) {
            File tempfile = new File(savePath + "temp.tmp");
            try {
                URL url = new URL(surl);
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos;
                    fos = new FileOutputStream(tempfile);
                    int len = conn.getContentLength();
                    int totalReadedLen = 0;
                    byte[] buf = new byte[4 * 1024];
                    conn.connect();
                    if (conn.getResponseCode() >= 400) {
                        // Toast.makeText(mContext, "连接超时",
                        // Toast.LENGTH_SHORT).show();
                    } else {
                        String size = conn.getHeaderField("Content-Length");
                        int iSize = 0;
                        if (!TextUtils.isEmpty(size)) iSize = Integer.valueOf(size);

                        maxsize = iSize;
                        int readedLen;
                        while ((readedLen = is.read(buf)) != -1) {
                            dlsize += readedLen;
                            fos.write(buf, 0, readedLen);
                            totalReadedLen += readedLen;
                        }
                    }
                    if (totalReadedLen == len) {
                        tempfile.renameTo(file);
                    }
                    conn.disconnect();
                    fos.flush();
                    fos.close();
                    is.close();
                    if (totalReadedLen == len) {
                        if (needStart) {// 安装APK
                            LauchInstall(file.getPath(), mContext);
                        } else {// 启动APK
                            Intent intent = new Intent(DfineAction.VS_ACTION_STARTPLUGIN);
                            intent.putExtra("pluginLocation", file.getPath());
                            mContext.sendBroadcast(intent);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ErrorDialog("IOException!");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                ErrorDialog("MalformedURLException!");
            }
        } else {
            timer.cancel();
            if (manager != null) {
                mhandler.sendEmptyMessage(NM_SET_SUCCEED);
            }
            if (progressdialog != null) {
                mhandler.sendEmptyMessage(SET_SUCCEED);
            }
            LauchInstall(file.getPath(), mContext);
        }
    }

    // sdcard是否可读写
    public static boolean IsCanUseSdCard() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 打开APK程序代码
    public void openFile(File file) {
        // TODO Auto-generated method stub
        CustomLog.i("OpenFile", file.getPath());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.android.package-archive", file, true);
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LauchInstall(String FilePathName, Context context) {
        chmod("777", FilePathName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.android.package-archive", new File(FilePathName), true);
//        intent.setDataAndType(Uri.fromFile(new File(FilePathName)), "application/vnd.android" +
//                ".package-archive");
        context.startActivity(intent);
        //安装后保存apk下载的路径
        VsUserConfig.setData(context, VsUserConfig.JKey_UPDATE_APK_FILE_PATH, FilePathName);
    }

    // 修改apk权限
    public void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 新建文件夹
    public static boolean makeDir(Context contex, String fileName) {
        if (fileName == null || fileName.trim().length() < 1) {
            return false;
        }
        File file = new File(fileName);
        if (file.exists()) {// 己存在
            return true;
        }

        return file.mkdir();
    }

    /**
     * 获取archiveFilePath该文件的版本号
     *
     * @param context
     * @param archiveFilePath 为apk的路径
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
}
