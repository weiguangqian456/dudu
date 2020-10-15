package com.weiwei.netphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hwtx.dududh.BuildConfig;
import com.hwtx.dududh.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.provb.h.ADError;
import com.umeng.analytics.provb.h.ADSplashListener;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsNetWorkTools;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.salemall.activity.AdActivity;
import com.weiwei.salemall.activity.GuideActivity;
import com.weiwei.salemall.bean.BannerImageEntity;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.citypicker.model.City;
import com.weiwei.salemall.inter.IReceiveLocation;
import com.weiwei.salemall.retrofit.ApiService;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.PreferencesUtils;
import com.weiwei.salemall.utils.SkipPageUtils;
import com.weiwei.salemall.utils.SpUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import retrofit2.Callback;

import static com.weiwei.salemall.base.Const.REQUEST_CODE;

public class SplashActivity extends VsBaseActivity implements IReceiveLocation, ADSplashListener {
    /**
     * This must be false for production. If true, turns on logging, test code,
     * etc.
     */
    private final char GOTO_VSMAIN = 1;// 跳转主页面
    private final char MSG_SPLASH = 2;// 跳转到滑屏页面
    private final char MSG_VSSTART = 3;// 跳转到登陆页面
    private ImageView splash_screen_img;
    private String ad_str;
    private String img_url;
    private String TAG = "SplashActivity";
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private RelativeLayout rl_ad_container;            //显示广告
    private CardView llDialog;

    private List<City> mAllCities = null;

    private FrameLayout mFlAdImage;
    private RoundTextView mRtvSkip;

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            //主界面
            case GOTO_VSMAIN:/*
             * Intent intent = new Intent(mContext,
             * VsMainActivity.class); startActivity(intent);
             * VsBizUtil.getInstance().getRYToken(mContext, "0");
             * VsBizUtil.getInstance().getToken(mContext); finish();
             */
                String adUrl = PreferencesUtils.getString(VsApplication.getContext(), "adUrl");
                if (adUrl != null && !adUrl.equals("")) {
                    SkipPageUtils.getInstance(mContext).skipPageAndFinish(AdActivity.class);
                } else {
                  /*  Intent intent = new Intent(SplashActivity.this,VsMainActivity.class);
                    intent.putExtra("indicator",6);
                   startActivity(intent);
                    finish();*/
                    SkipPageUtils.getInstance(mContext).skipPageAndFinish(VsMainActivity.class);
                }

//                RandomAccessFile raf;
//                try {
//                    raf = new RandomAccessFile(LockPatternUtils.sLockPatternFilename, "rwd");
//                    if (raf.length() == 0) {
//                        Intent intent = new Intent(mContext, VsMainActivity.class);
//                        startActivity(intent);
////					VsBizUtil.getInstance().getRYToken(mContext, "0");
//                        VsBizUtil.getInstance().getToken(mContext);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(this, UnlockGesturePasswordActivity.class);
//                        if (link != null && link.length() > 0) {
//                            intent.putExtra("messagelink", link);
//                        }
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    raf.close();
//                    finish();
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                break;
            case MSG_SPLASH:
                startActivity(this, SlideActivity.class);
                finish();
                break;
            case MSG_VSSTART:
                //是否首次运行app
 //               initFirstInstallApp();
//                finish();
                break;
            default:
                break;
        }
    }

    private String link;// 外部要跳转的链接

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
//
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Code_Account, "");
      if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        initAdInfo();

        // 获取屏幕宽高与密度
        VsUtil.setDensityWH(this);
        // 获取手机型号
        GlobalVariables.PHONE_MODEL = android.os.Build.MODEL;
        CustomLog.i(TAG, "手机型号:" + GlobalVariables.PHONE_MODEL);

        // 防止外部修改我们的包名
        String packageName = getApplication().getPackageName();
        if (!packageName.equals(DfineAction.PACKAGE_FIRST) && !packageName.equals(DfineAction.PACKAGE_SECOND)) {
            finish();
            return;
        }
//        if (VsPhoneCallHistory.IMCONTACTLIST == null || VsPhoneCallHistory.IMCONTACTLIST.size() == 0) {
//            // VsPhoneCallHistory.loadContacts();
//            VsPhoneCallHistory.loadContactData(SplashActivity.this, 1);
//        }

        // 初始化变量
        getVersionCode();
        init();
        boolean login = VsUtil.checkHasAccount(mContext);

        Intent intent = getIntent();
        link = intent.getStringExtra("messagelink");
        if (link != null && link.length() > 0) {
            VsUtil.startActivity(link, mContext, null);
            finish();
        }
        // 加载钢琴音
        VsCoreService.getData();
        if (VsUserConfig.getDataInt(mContext, VsUserConfig.JKEY_PIANO_ISCHECHED_ID) != 0) {
            VsCoreService.setSpData(VsUserConfig.getDataInt(mContext, VsUserConfig.JKEY_PIANO_ISCHECHED_ID), mContext);
        }

        // 用户清空账号手动退出。下次进入软件直接进入到登录界面
        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, false) && !login) {
            // startActivity(this, VsStartActivity.class);
            // finish();
//            mBaseHandler.sendEmptyMessageDelayed(MSG_VSSTART, 2500);
            initFirstInstallApp();
            return;
        }

        // 有帐号记录，去拉取各类配置信息
        startLoadInfo(login);
        ad_str = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CONFIG_400501);
        if (ad_str.length() > 1) {

            try {
                JSONArray ad_conf = new JSONArray(ad_str);
                JSONObject ad_object = (JSONObject) ad_conf.get(0);
                img_url = ad_object.getString("img");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        initView();

        // 首次进入软件（创建桌面快捷方式）
        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOGIN_APP, true)) {
            VsUtil.CreateDeskShortCut(mContext, getString(R.string.app_name), R.drawable.icon);
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_FRIST_LOGIN_APP, false);
//            mBaseHandler.sendEmptyMessageDelayed(MSG_VSSTART, 2500);
            initFirstInstallApp();
            return;
        } else {
//            ADShow.getInstance().addSplash(this, mFlAdImage, mRtvSkip, 0, false, this);
        }

        if (intent != null) {
            // 手机拨号盘拨打
            Uri uri = intent.getData();
            if (uri != null) {
                if (uri.toString().startsWith(DfineAction.scheme_head)) {
                    VsUtil.skipForScheme(uri.toString(), mContext, null);
                    finish();
                    return;
                } else {
                    String tempStr = uri.toString();
                    tempStr = tempStr.replaceAll("%20", "").replaceAll("%2B86", "").replaceAll("%2B", "");
                    // 去掉tel:
                    tempStr = tempStr.substring(4);
                    if (tempStr.indexOf("%") == -1) {
                        // 拨打
                        VsUtil.callNumber(tempStr, tempStr, "", mContext, "", true, getFragmentManager());
                        finish();
                        return;
                    }
                }
            }
        }

        if (!VsUtil.checkHasAccount(mContext)) {
            // startActivity(this, VsStartActivity.class);
            // finish();
            unRgisterGO();
//			mBaseHandler.sendEmptyMessageDelayed(MSG_VSSTART, 2500);
        } else {
            unRgisterGO();
        }

        if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_TJ_ONE_START, false)) {
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_TJ_ONE_START, true);
        }

        VsApplication.getInstance().addActivity(this);
    }

    @Override
    public void onError(ADError adError) {
        LogUtils.e("+++++++++-------------------->" + adError.getErrorCode() + "<---------------->" + adError.getErrorMsg());
        unRgisterGO();
    }

    @Override
    public void onSuccess() {
//        mFlAdImage.setVisibility(View.VISIBLE);
//        mRtvSkip.setVisibility(View.VISIBLE);

        splash_screen_img.setVisibility(View.GONE);
    }

    @Override
    public void onDismissed() {
        SkipPageUtils.getInstance(mContext).skipPageAndFinish(VsMainActivity.class);
    }

    @Override
    public void onTick(long l) {
        mRtvSkip.setText("跳过 " + l / 1000 + "s");
    }

    @Override
    public void onClicked() {

    }

    /**
     * 获取启动引导页
     */
    private void initAdInfo() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getAdpage();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "广告msg===>" + result.getData().toString());
                    if (result.getData() != null) {
                        BannerImageEntity bannerDataBean = com.alibaba.fastjson.JSONObject.parseObject(result.getData().toString(), BannerImageEntity.class);
                        String adUrl = bannerDataBean.getImageUrl();
                        String className = bannerDataBean.getAndroidClassName();
                        String params = bannerDataBean.getAndroidParams();
                        int duration = bannerDataBean.getDuration();
                        PreferencesUtils.putString(SplashActivity.this, "adUrl", adUrl);
                        PreferencesUtils.putString(SplashActivity.this, "className", className);
                        PreferencesUtils.putString(SplashActivity.this, "params", params);
                        PreferencesUtils.putInt(SplashActivity.this, "duration", duration);
                    }
                } else {
                    PreferencesUtils.putString(SplashActivity.this, "adUrl", "");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    private void initFirstInstallApp() {
        final boolean firstLoginSuccessFlag = (boolean) SpUtils.get(this, "firstLoginSuccessFlag", true);
        Intent intent;
        if (firstLoginSuccessFlag) {
                checkContactPermission();
        } else {
            intent = new Intent(SplashActivity.this, VsMainActivity.class);
            startActivity(intent);
            finish();
        }



    }

    private void initView() {
        // setContentView(R.layout.splashregister);
        // final LinearLayout centerLL = (LinearLayout)
        // findViewById(R.id.center_ll);
        // ImageView splash_img = (ImageView) findViewById(R.id.splash_image);
        // String url = VsUserConfig.getDataString(mContext,
        // VsUserConfig.JKEY_AD_CONFIG_400501);
        // if (url.length() > 1) {
        // imageLoader.displayImage(url, splash_img);
        // } else {
        // splash_img.setImageResource(R.drawable.vs_splash_img);
        // }
        setContentView(R.layout.splashregister);
        splash_screen_img = (ImageView) findViewById(R.id.splash_image);

        mFlAdImage = findViewById(R.id.fl_ad_image);
        mRtvSkip = findViewById(R.id.rtv_skip);

        if (img_url != null && img_url.length() > 1) {
            imageLoader.displayImage(img_url, splash_screen_img);
        } else {
            splash_screen_img.setImageResource(R.drawable.app_guide);
        }

        rl_ad_container = (RelativeLayout) findViewById(R.id.rl_ad_container);
        llDialog = findViewById(R.id.ll_dialog);

        // final AdDataView adDataView = (AdDataView)
        // findViewById(R.id.ad_data_view);
        /*
         * if (AdManager.getInstance().hasAd(SplashActivity.this,
         * AdManager.TYPE_START_PAGE)) { TranslateAnimation animation = new
         * TranslateAnimation(0, 0, 0, (int) (GlobalVariables.height * 0.389));
         * animation.setDuration(300L); animation.setFillAfter(true);
         * centerLL.startAnimation(animation);
         * animation.setAnimationListener(new Animation.AnimationListener() {
         *
         * @Override public void onAnimationStart(Animation animation) {
         * companyTV.setVisibility(View.GONE); }
         *
         * @Override public void onAnimationEnd(Animation animation) {
         * adDataView.setLayoutParams(new RelativeLayout.LayoutParams(-1, (int)
         * (GlobalVariables.height * 0.778)));
         * adDataView.setAdData(AdManager.getInstance
         * ().getAd(SplashActivity.this, AdManager.TYPE_START_PAGE, new
         * Random().nextInt(5)), false); }
         *
         * @Override public void onAnimationRepeat(Animation animation) {
         *
         * } }); } else { adDataView.setVisibility(View.GONE); }
         */

    }
    private AlertDialog dialog;
    private void showSplashDialog() {
        AlertDialog.Builder splashDialog = new AlertDialog.Builder(this,R.style.AlertDialog);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.splash_dialog,null);
        splashDialog.setView(dialogView);
        TextView tvContent = dialogView.findViewById(R.id.tv_intro1);
        TextView tvIntrol = dialogView.findViewById(R.id.tv_intro2);
        TextView tvOk = dialogView.findViewById(R.id.tv_ok);
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProtocol();
            }
        });
        tvIntrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProtocol();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splashDialog.create().dismiss();
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                finish();
            }
        });
        splashDialog.setCancelable(false);
        dialog = splashDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        WindowManager wm = this.getWindowManager();
        Display display = wm.getDefaultDisplay();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.9);
        dialog.getWindow().setAttributes(params);
    }

    private void showProtocol() {
        final String dealUrl = "file:///android_asset/shop_service_terms.html";
        Intent dealIntent = new Intent();
        dealIntent.setClass(mContext, WeiboShareWebViewActivity.class);
        String[] aboutBusiness_deal = new String[]{mContext.getString(R.string.welcome_main_elecdeal), "service", dealUrl};
        dealIntent.putExtra("AboutBusiness", aboutBusiness_deal);
        startActivity(dealIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDisEnableLeftSliding();
        saveCitiesInfo();
        commitDeviceInfo();
    }

    private void checkContactPermission() {
        //READ_CALL_LOG
        XXPermissions.with(this).permission(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS, Permission.READ_PHONE_STATE, Permission
                .WRITE_EXTERNAL_STORAGE,Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION,
                Permission.READ_CALL_LOG).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                if (isAll) {
                    showSplashDialog();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                showSplashDialog();
            }
        });
    }

    /**
     * 提交设备信息
     */
    private void commitDeviceInfo() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        Map<String, String> params = new HashMap<>();
        params.put("tagVersion", BuildConfig.VERSION_NAME);
        params.put("mobileIdentity", VsUtil.getAloneImei(this));
        retrofit2.Call<ResultEntity> call = api.getDeviceInfo(params);
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();
                if (REQUEST_CODE == result.getCode()) {
                    Log.e(TAG, "设备信息提交成功");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    /**
     * 保存城市信息
     */
    private void saveCitiesInfo() {
        ApiService api = RetrofitClient.getInstance(this).Api();
        retrofit2.Call<ResultEntity> call = api.getCities();
        call.enqueue(new Callback<ResultEntity>() {
            @Override
            public void onResponse(retrofit2.Call<ResultEntity> call, retrofit2.Response<ResultEntity> response) {
                if (response.body() == null) {
                    return;
                }
                ResultEntity result = response.body();

                if (REQUEST_CODE == result.getCode() && result.getData() != null) {
                    Log.e(TAG, "城市msg===>" + result.getData().toString());
                    PreferencesUtils.putString(mContext, "citiesData", result.getData().toString());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResultEntity> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 初始化变量信息
     */
    public void init() {
        GlobalVariables.netmode = VsNetWorkTools.getSelfNetworkType(mContext);
        if (VsHttpTools.getInstance(mContext).getUri_prefix().trim().length() < 1 || VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UriAndPort) == null || BuildConfig.VERSION_NAME.equals("8.0.0.1")) {
            VsUserConfig.setData(mContext, VsUserConfig.JKey_UriAndPort, DfineAction.RES.getString(R.string.uri_prefix));
            VsHttpTools.getInstance(mContext).setUri_prefix(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UriAndPort));
        }
        // VsBizUtil.getInstance().defaultConfig(mContext);// 多点接入
        try {
            // 初始化渠道号和版本号
            Properties properties = new Properties();
            properties.load(getAssets().open("config.properties"));
            DfineAction.invite = properties.getProperty("inviete", "5");
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_INVITEDBY, DfineAction.invite);

            // 第一次进入或者升级安装
            if (!BuildConfig.VERSION_NAME.equals(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V, ""))) {// 版本号不同
                // 保存版本号
                VsUserConfig.setData(mContext, VsUserConfig.JKey_V, BuildConfig.VERSION_NAME);
                // 保存不带uid数据状态
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RECORDINSTALL_NO_UID, true);
                // 保存带有uid数据状态
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RECORDINSTALL_WITH_UID, true);
                // 保存第一次进入应用状态
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_FRIST_LOGIN_APP, true);
            }

            String isTestV = properties.getProperty("istestv", "no");
            CustomLog.setPrintlog(isTestV.equals("yes"));// 设置是否显示Log信息
            properties.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 启动拉取
     */
    public void startLoadInfo(boolean login) {

        // 判断网络
        if (GlobalVariables.netmode == 0) {
            return;
        }

        // app静态配置
        VsBizUtil.getInstance().getAppInfo(mContext);
        //升级信息
        VsUtil.getUpdate(mContext);
        // fir升级
        VsBizUtil.getInstance().getFirUpDate(mContext);
        // 有帐号拉取
        if (login) {
            // 模版配置
            VsBizUtil.getInstance().templateConfig(mContext);
            //分享内容拉取
            VsBizUtil.getInstance().getShareContent(mContext);
            // 广告信息

            VsBizUtil.getInstance().getAdInfos(mContext, VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));

            //拉取个人信息
            VsBizUtil.getInstance().getMyInfoText(mContext);
        } else {
            if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, false)) {
                // 读取本地保存的帐号密码并登录
                VsBizUtil.getInstance().redSDCardInfo(mContext);
            }
        }
    }

    public void unRgisterGO() {
        showVS2011(2000);
    }

    public void onDestroy() {
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    public void showVS2011(int tm) {
        mBaseHandler.sendEmptyMessageDelayed(GOTO_VSMAIN, tm);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = Integer.toString(info.versionCode);
            VsUserConfig.setData(mContext, VsUserConfig.JKey_FIRVERSION, version);
            return "V" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.version_unkown);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    @Override
    public void OnReceive() {

    }
}
