package com.weiwei.base.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.BuildConfig;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pgyersdk.crash.PgyCrashManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.provb.h.ADShow;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.lock.LockPatternUtils;
import com.weiwei.salemall.emsemob.DemoHelper;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class VsApplication extends Application {
    private static final String TAG = VsApplication.class.getSimpleName();
    public static Context mContext;

    private LinkedList<Activity> mActivityList = new LinkedList<Activity>();
    private static VsApplication mInstance;
    private LockPatternUtils mLockPatternUtils;

    public int payType = 0;

    public int noReadMsg;

    public int getNoReadMsg() {
        return noReadMsg;
    }

    public void setNoReadMsg(int noReadMsg) {
        this.noReadMsg = noReadMsg;
    }

    @Override
    public void onCreate() {
        CustomLog.i(TAG, "onCreate()..");
        super.onCreate();
        //环信初始化
        DemoHelper.getInstance().init(this);
        mContext = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
//        Intent serviceIntent = new Intent(this, VsCoreService.class);
 //       this.startService(serviceIntent);
        try {
            Intent serviceIntent = new Intent(this, VsCoreService.class);
            this.startService(serviceIntent);
        }catch (IllegalStateException e) {
        }
        mLockPatternUtils = new LockPatternUtils(this);
        initImageLoader(getApplicationContext());
        PgyCrashManager.register(this);
        initRobotSdk();   //智齿
        //bugly
        String phoneNumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        CrashReport.setUserId(phoneNumber);
        CrashReport.initCrashReport(getApplicationContext(), "757e5fdd83", true);
        //jPush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        setTagAndAlias();

//        ADShow.setNativeVersion(this, 2.0f);
        try{
            ADShow.setDebug(true);
            ADShow.init(this);
        }catch (Exception e) {

        }
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());

        SDKInitializer.initialize(VsApplication.this);

    }

    private void setTagAndAlias() {
        Set<String> tags = new HashSet<String>();
        tags.add(BuildConfig.VERSION_NAME);
        tags.add(CheckLoginStatusUtils.isLogin() ? VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) : "");
        JPushInterface.setAliasAndTags(this, VsUtil.getAloneImei(this), tags, mAliasCallback);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success极光推送别名设置成功";
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                    break;
                default:
                    logs = "极光推送设置失败，Failed with errorCode = " + code;
                    break;
            }
            Log.e("TAG", logs);
        }
    };

    /**
     * 初始化智齿客服sdk
     */
    private void initRobotSdk() {
        /**
         * 初始化sdk
         */
//        SobotApi.initSobotSDK(mContext, ZHICHI_APP_KEY, "");
//        /**
//         * 解决关闭app后进来没有收到消息的问题
//         */
//        SobotApi.initSobotChannel(mContext, "");
//        SobotApi.setNotificationFlag(mContext, true, R.drawable.icon, R.drawable.icon);
    }


    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return
     */
    public static VsApplication getInstance() {
        if (null == mInstance) {
            mInstance = new VsApplication();
        }
        return mInstance;
    }

    /**
     * 获取Context.
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 添加Activity到容器中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivityList == null) {
            mActivityList = new LinkedList<Activity>();
        }
        mActivityList.add(activity);
    }

    /**
     * 取得当前打开的Activity个数
     */
    public int getActivitySize() {
        if (mActivityList == null) {
            return 0;
        }
        return mActivityList.size();
    }

    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        GlobalVariables.curIndicator = 0;
        if (mActivityList == null) {
            return;
        }
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        ImageLoader.getInstance().clearDiscCache();
        ImageLoader.getInstance().clearMemoryCache();
        mActivityList.clear();
        mActivityList = null;
    }


    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of
        // them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove for
                // release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
