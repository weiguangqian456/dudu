package com.weiwei.base.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.BuildConfig;
import com.hwtx.dududh.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.weiwei.base.activity.aplpay.Base64;
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.activity.me.KcMyQcodeActivity;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.base.activity.me.VsAboutActivity;
import com.weiwei.base.activity.me.VsBalanceActivity;
import com.weiwei.base.activity.me.VsMakeMoneyActivity;
import com.weiwei.base.activity.me.VsMakeMoneyTaskActivity;
import com.weiwei.base.activity.me.VsMyBalanceDetailActivity;
import com.weiwei.base.activity.me.VsMyInfoTextActivity;
import com.weiwei.base.activity.me.VsMyRedPagActivity;
import com.weiwei.base.activity.me.VsSignInFirstActivity;
import com.weiwei.base.activity.more.KcUnlockedActivity;
import com.weiwei.base.activity.more.VsMiniGameActivity;
import com.weiwei.base.activity.more.VsSetingVoicePianoActivity;
import com.weiwei.base.activity.recharge.VsRechargePayTypes;
import com.weiwei.base.activity.setting.VsCallTypeSetingActivity;
import com.weiwei.base.activity.setting.VsCallerIdentificationActivity;
import com.weiwei.base.activity.setting.VsSetingActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.bakcontact.VsBatchOperation;
import com.weiwei.base.bakcontact.VsContactManager;
import com.weiwei.base.bakcontact.VsContactOperations;
import com.weiwei.base.callback.VSCallBackHintActivity;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.Resource;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsAction;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.item.VsInviteItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.util.CustomAlertDialog;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.base.widgets.CustomDialog2;
import com.weiwei.base.widgets.CustomDialogActivity;
import com.weiwei.home.dialog.PromptDialog;
import com.weiwei.home.fragment.HintMessageDialog;
import com.weiwei.home.fragment.HintMessageNotV4Dialog;
import com.weiwei.home.utils.ToastAstrictUtils;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.SplashActivity;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.bean.ResultEntity;
import com.weiwei.salemall.retrofit.RetrofitClient;
import com.weiwei.salemall.utils.NetUtils;
import com.weiwei.softphone.AudioPlayer;
import com.weiwei.softphone.TutorialRegistrationCall;
import com.weiwei.weibo.WeiboShareWebViewActivity;
import com.zbar.lib.CaptureActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Title:Android客户端
 * @Description: 工具类
 * @Copyright: Copyright (c) 2014
 */
public class VsUtil {
    public static final String TAG = "VsUtil";
    /**
     * 最后点击按钮的时间
     */
    private static long lastClickTime;
    private static BroadcastReceiver getOReceiver = null;

    /**
     * 防止按钮连续点击的方法（Button,ImageButton等）
     *
     * @return 点击按钮的时间间隔够不够0.5秒
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastDoubleClick(long timelong) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < timelong) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断是否为数字
     *
     * @param content
     * @return
     */
    public static boolean isNumber(String content) {
        String str = content.replaceAll("-", "").trim().replaceAll(" ", "");
        if (str.matches("^[0-9]*$")) {
            return true;
        }
        return false;
    }

    /**
     * 获取屏幕的宽高和密度
     *
     * @param mContext
     */
    public static void setDensityWH(Activity mContext) {
        // 得到屏幕密度
        DisplayMetrics dm = new DisplayMetrics();// add by zzw
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);// add by
        // zzw
        GlobalVariables.density = dm.density;// add by zzw
        GlobalVariables.width = dm.widthPixels; // 像素：宽
        GlobalVariables.height = dm.heightPixels; // 像素：高
        CustomLog.i(TAG, "屏幕宽:" + GlobalVariables.width + ",高:" + GlobalVariables.height);
    }

    /**
     * 短信邀请
     *
     * @param mContext
     * @param sendMsNote
     * @param phoneNumber
     */
    public static void smsShare(Context mContext, String sendMsNote, String phoneNumber) {
        try {
            CustomLog.i("GDK", "phoneNumber=" + phoneNumber);
            Intent msIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phoneNumber, null));
            msIntent.putExtra("sms_body", sendMsNote);
            msIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(msIntent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(mContext, "找不到可用短信设备", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 友盟新浪分享
     *
     * @param mContext
     * @param content
     */
    // public static void sinaShare(Context mContext, String content) {
    // UMSocialService mController =
    // UMServiceFactory.getUMSocialService("com.umeng.share");
    // // 设置新浪SSO handler
    // mController.getConfig().setSsoHandler(new SinaSsoHandler());
    // SocializeConfig.getSocializeConfig().closeToast();// 关闭提示
    // mController.setShareContent(content);
    // mController.directShare(mContext, SHARE_MEDIA.SINA, null);
    // }

    /**
     * 友盟QQ定向分享
     *
     * @param mContext
     * @param content
     */
    public static void qqShare(final Context mContext, String content, String imgUrl, boolean isQQzone) {
        try {
            UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
            SocializeConfig.getSocializeConfig().closeToast();// 关闭提示
            String qqAppId = DfineAction.QqAppId;
            String qqAppKey = DfineAction.QqAppKey;
            if (!isQQzone) {// 判读是否为QQ空间分享
                // 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，
                // 参数3为开发者在QQ互联申请的APP kEY.
                UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) mContext, qqAppId, qqAppKey);
                qqSsoHandler.addToSocialSDK();
                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setShareContent(content);
                qqShareContent.setTitle(DfineAction.RES.getString(R.string.product) + "电话");
                qqShareContent.setShareImage(new UMImage(mContext, imgUrl));
                qqShareContent.setTargetUrl(content.substring(content.indexOf("http://")));
                mController.setShareMedia(qqShareContent);
                // 参数1为当前Activity对象， 参数2为要分享到的目标平台， 参数3为分享操作回调接口
                mController.directShare(mContext, SHARE_MEDIA.QQ, null);
            } else {
                // 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，
                // 参数3为开发者在QQ互联申请的APP kEY.
                QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mContext, qqAppId, qqAppKey);
                qZoneSsoHandler.addToSocialSDK();
                QZoneShareContent qzone = new QZoneShareContent();
                qzone.setShareContent(content);
                qzone.setTargetUrl(content.substring(content.indexOf("http://")));
                qzone.setTitle(DfineAction.RES.getString(R.string.product) + "电话");
                qzone.setShareImage(new UMImage(mContext, imgUrl));
                mController.setShareMedia(qzone);
                // 参数1为当前Activity对象， 参数2为要分享到的目标平台， 参数3为分享操作回调接口
                mController.directShare(mContext, SHARE_MEDIA.QZONE, null);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 友盟微信分享
     *
     * @param mContext
     * @param content
     * @param bmp
     * @param type
     */
    public static void weixinShare(final Context mContext, String content, Bitmap bmp, String type) {
        try {
            UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
            String appId = DfineAction.WEIXIN_APPID;
            SocializeConfig.getSocializeConfig().closeToast();// 关闭提示
            if ("".equals(type) || type == null) {
                // 添加微信平台
                UMWXHandler wxHandler = new UMWXHandler(mContext, appId);
                wxHandler.addToSocialSDK();
                WeiXinShareContent weixinContent = new WeiXinShareContent();
                weixinContent.setShareContent(content);
                weixinContent.setTitle(DfineAction.RES.getString(R.string.product) + "电话");
                weixinContent.setTargetUrl(content.substring(content.indexOf("http://")));
                if (bmp != null) {
                    weixinContent.setShareImage(new UMImage(mContext, bmp));
                }
                mController.setShareMedia(weixinContent);
                // 参数1为当前Activity对象， 参数2为要分享到的目标平台， 参数3为分享操作回调接口
                mController.directShare(mContext, SHARE_MEDIA.WEIXIN, null);
            } else {
                // 支持微信朋友圈
                UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId);
                wxCircleHandler.setToCircle(true);
                wxCircleHandler.addToSocialSDK();
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent(content);
                circleMedia.setTitle(DfineAction.RES.getString(R.string.product) + "电话");
                if (bmp != null) {
                    circleMedia.setShareImage(new UMImage(mContext, bmp));
                }
                circleMedia.setTargetUrl(content.substring(content.indexOf("http://")));
                mController.setShareMedia(circleMedia);
                // 参数1为当前Activity对象， 参数2为要分享到的目标平台， 参数3为分享操作回调接口
                mController.directShare(mContext, SHARE_MEDIA.WEIXIN_CIRCLE, null);
            }
            if (bmp != null) {
                bmp.recycle();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 友盟微信分享手机号
     *
     * @param mContext
     * @param content
     */
    public static void weixinShare(final Context mContext, String content) {
        try {
            UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
            String appId = DfineAction.WEIXIN_APPID;
            SocializeConfig.getSocializeConfig().closeToast();// 关闭提示
            UMWXHandler wxHandler = new UMWXHandler(mContext, appId);
            wxHandler.addToSocialSDK();
            WeiXinShareContent weixinContent = new WeiXinShareContent();
            weixinContent.setShareContent(content);
            weixinContent.setTitle("好友名片");
            mController.setShareMedia(weixinContent);
            // 参数1为当前Activity对象， 参数2为要分享到的目标平台， 参数3为分享操作回调接口
            mController.directShare(mContext, SHARE_MEDIA.WEIXIN, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * url转换json格式
     */
    public String getUrlToJson(String url) {
        int sun = 0;
        int count = 0;
        String json = "{\"data\":{";
        String data = "";
        String end = "}";
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        // 计算出字符串里有多少个&符号
        for (int i = 0; i < url.length(); i++) {
            if ("&".equals(url.substring(i, i + 1))) {
                sun++;
            }
        }
        String a[] = url.split("[&]");// 以&符号分隔
        // 将分隔出来的字符串加上，
        for (int i = 0; i < sun + 1; i++) {
            sb.append(a[i] + ",");
        }
        // 以=分隔
        String[] c = sb.toString().split("[=]");
        // 将分隔出来的字符串加上，
        for (int j = 0; j < c.length; j++) {
            sb2.append(c[j] + ",");
        }
        // 去掉最后面两个,
        String result = sb2.toString().substring(0, sb2.length() - 2);
        // 计算出字符串里有多少个,符号
        for (int i = 0; i < result.length(); i++) {
            if (",".equals(result.substring(i, i + 1))) {
                count++;
            }
        }
        // 以，分隔
        String[] d = result.toString().split("[,]");
        for (int i = 0; i < count + 1; i++) {
            if (i % 2 != 0) {// 字符下标为奇数时加上，
                data += "\"" + d[i] + "\",";
            } else {
                data += "\"" + d[i] + "\":";
            }
        }
        // 拼接json格式
        String jsonArray = (json + data).substring(0, (json + data).length() - 1) + end + ",\"success\":true,\"info\":\"json转换\"}";
        return jsonArray;
    }

    /**
     * 获取IWXAPI实例
     *
     * @param mContext
     * @return
     */
    /*
     * public static IWXAPI getWeiXinApi(Context mContext) { //
     * 通过WXAPIFactory工厂，获取IWXAPI实例 IWXAPI localIWXAPI =
     * WXAPIFactory.createWXAPI(mContext, DfineAction.WEIXIN_APPID, true); if
     * (localIWXAPI.isWXAppInstalled()) { if
     * (!localIWXAPI.registerApp(DfineAction.WEIXIN_APPID)) {// 将应用的APPID注册到微信
     * localIWXAPI = null; } } else { localIWXAPI = null; } return localIWXAPI;
     * }
     */

    /**
     * Toast提示
     *
     * @param mContext
     * @param content
     */
    public static void showT(Context mContext, String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
    }

    /**
     * 需要对图片进行处理，否则微信会在log中输出thumbData检查错误
     *
     * @param bitmap
     * @param paramBoolean
     * @return
     */
    public static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
        Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        int i;
        int j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, 80, 80), null);
            if (paramBoolean) bitmap.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }

    /**
     * 分享的activity
     *
     * @param mContext
     * @param content
     */
    public static void shareWebActivity(Context mContext, String content, String title) {
        /*
         * Intent intent = new Intent(mContext, WeiboShareActivity.class);
         * intent.putExtra("AboutBusiness", new String[] { "0", title, content
         * }); intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         * mContext.startActivity(intent);
         */
    }

    /**
     * 回调方法
     *
     * @param mContext
     * @param callBackType 1表示url ，2表示接口
     * @param callback
     * @param params       有些业务需要带参数
     */
    public static void callBack(Context mContext, String callBackType, String callback, String params) {
        if (callBackType.equals("1")) {
            Intent intent = new Intent(mContext, WeiboShareWebViewActivity.class);
            String[] aboutBusiness = new String[]{mContext.getString(R.string.app_name), "", callback};
            intent.putExtra("AboutBusiness", aboutBusiness);
            // 传参
            mContext.startActivity(intent);
        } else {
            CoreBusiness.getInstance().startCallBackThread(mContext, callback, params);
        }
    }

    private static Boolean isF;

    private static void httpCallNumber(final String name, String number, final String local, final Context mContext, final String dialType, final boolean checkFree) {
        isF = true;
        String callnumber = filterPhoneNumber(number);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Call_Number, callnumber);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Call_Name, name);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Call_Local, local);
        VsUserConfig.setData(mContext, VsUserConfig.JKey_Call_DialType, dialType);

        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber).equals(callnumber)) {
            Toast.makeText(mContext, DfineAction.RES.getString(R.string.product) + "不支持拨打本机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 未登录提醒
        if (!isLogin(mContext.getResources().getString(R.string.dial_layout_login_prompt), mContext)) {
            return;
        }

        // 拨打鉴权广播
        if (getOReceiver == null) {
            GlobalVariables.TAG = 1;
            getOReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (GlobalVariables.actionGetOK.equals(intent.getAction())) {
                        Bundle bundle = intent.getExtras();

                        String allow_time = "0";
                        try {
                            JSONObject js = new JSONObject(bundle.get("data").toString());
                            allow_time = js.get("allow_time").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //居然有额外的回调？？？
                            try {
                                if (isF) {
                                    PromptDialog promptDialog = new PromptDialog(mContext);
                                    promptDialog.setContent("你是普通会员\n不可免费通话").setBtnText("知道了");
                                    promptDialog.widthScale(0.7f);
                                    promptDialog.show();
//                                    CustomUpdataNotV4Dialog mHintDialog = CustomUpdataNotV4Dialog
//                                            .getInstance()
//                                            .setDrawableId(R.drawable.imgae_up_member_phone);
//                                    mHintDialog.show(((Activity) mContext).getFragmentManager(), "TAG");
                                    isF = false;
                                    mContext.unregisterReceiver(getOReceiver);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return;
                        }
                        if (Integer.parseInt(allow_time) > 0) {
                            String callnumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Call_Number);
                            String name = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Call_Name);
                            String local = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Call_Local);
                            String dialType = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Call_DialType);

                            dialphone(name, callnumber, local, mContext, dialType, checkFree);
                        } else {
                            Toast.makeText(mContext, "鉴权失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
        }
        Boolean flag = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_THIRDCALLVALUE, false);
        if (flag) {
            dialphone(name, callnumber, local, mContext, dialType, checkFree);
        } else {
            // 拨打鉴权
            IntentFilter Filter = new IntentFilter();
            Filter.addAction(GlobalVariables.actionGetOK);
            mContext.registerReceiver(getOReceiver, Filter);
            String caller = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
            String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
            VsBizUtil.getInstance().getOK(mContext, caller, number, getCallType(mContext), uid);
        }
    }

    public static void callNumber(final String name,
                                  final String number,
                                  final String local,
                                  final Context mContext,
                                  final String dialType,
                                  final boolean checkFree) {

    }

    /**
     * 呼叫方法
     *
     * @param name
     * @param number
     * @param local
     * @param mContext
     * @param dialType 0为直拨 1免费 2为回拨
     */
    public static void callNumber(final String name,
                                  final String number,
                                  final String local,
                                  final Context mContext,
                                  final String dialType,
                                  final boolean checkFree,
                                  final FragmentManager fragmentManager, boolean isV4) {
        if (!VsUtil.checkHasAccount(VsApplication.getContext())) {
            ToastAstrictUtils.getInstance().show("请先登录哦！");
            return;
        }
        String phoneNumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        Map<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);
        RetrofitClient.getInstance(mContext)
                .Api()
                .getPhoneMag(map)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        ResultEntity body = response.body();
                        if (body != null && body.getCode() == 0) {
                            VsUtil.httpCallNumber(name, number, local, mContext, dialType, checkFree);
                            if (!TextUtils.isEmpty(body.getMsg())) {
                                ToastAstrictUtils.getInstance().show(body.getMsg());
                            }
                        } else {
                            String msg = body != null && !TextUtils.isEmpty(body.getMsg()) ? body.getMsg() : "服务器异常";
                            HintMessageDialog dialog = new HintMessageDialog().setMessage(msg);
                            if (fragmentManager != null) {
                                dialog.show(fragmentManager, "TAG");
                            } else {
                                ToastAstrictUtils.getInstance().show(msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        ToastAstrictUtils.getInstance().show("服务器异常");
                    }
                });
    }

    public static void callNumber(final String name,
                                  final String number,
                                  final String local,
                                  final Context mContext,
                                  final String dialType,
                                  final boolean checkFree,
                                  final android.app.FragmentManager fragmentManager) {
        if (!VsUtil.checkHasAccount(VsApplication.getContext())) {
            ToastAstrictUtils.getInstance().show("请先登录哦！");
            return;
        }
        String phoneNumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber);
        Map<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);
        RetrofitClient.getInstance(mContext)
                .Api()
                .getPhoneMag(map)
                .enqueue(new Callback<ResultEntity>() {
                    @Override
                    public void onResponse(Call<ResultEntity> call, Response<ResultEntity> response) {
                        ResultEntity body = response.body();
                        if (body != null && body.getCode() == 0) {
                            VsUtil.httpCallNumber(name, number, local, mContext, dialType, checkFree);
                            if (!TextUtils.isEmpty(body.getMsg())) {
                                ToastAstrictUtils.getInstance().show(body.getMsg());
                            }
                        } else {
                            String msg = body != null && !TextUtils.isEmpty(body.getMsg()) ? body.getMsg() : "服务器异常";
                            HintMessageNotV4Dialog dialog = new HintMessageNotV4Dialog().setMessage(msg);
                            if (fragmentManager != null) {
                                dialog.show(fragmentManager, "TAG");
                            } else {
                                ToastAstrictUtils.getInstance().show(msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultEntity> call, Throwable t) {
                        ToastAstrictUtils.getInstance().show("服务器异常");
                    }
                });
    }

    // 后台获取拨打方式 0直拨 1回拨
    private static String getCallType(Context mContext) {
        String call_type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_dial_types, "");
        String def_call_type = "1";

        try {
            JSONObject js = new JSONObject(call_type);
            String type = (String) js.get("type");
            String def = (String) js.get("default");
            if ("all".equals(type)) {
                if ("direct".equals(def)) {
                    def_call_type = "0";
                } else if ("callback".equals(def)) {
                    def_call_type = "1";
                }

            } else if ("direct".equals(type)) {
                def_call_type = "0";
            } else {
                def_call_type = "1";
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
        if ("3".equals(type)) {
            type = "0";
        }
        return type;
    }

    /**
     * 去掉电话号码前缀
     *
     * @param phoneNumber
     * @return
     */
    public static String filterPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        String phone = phoneNumber.replaceAll(" ", "").replaceAll("-", "").trim();
        if (phone.length() > 11) {
            if (phone.startsWith("86")) {
                phone = phone.substring(2);
            } else if (phone.startsWith("+86")) {
                phone = phone.substring(3);
            } else if (phone.startsWith("086")) {
                phone = phone.substring(3);
            } else if (phone.startsWith("0086")) {
                phone = phone.substring(4);
            } else if (phone.startsWith("+0086")) {
                phone = phone.substring(5);
            }
        }
        return phone;
    }

    /**
     * 2g/3g下打免费提示会消耗流量
     *
     * @param name
     * @param number
     * @param local
     * @param context
     * @param dialType
     * @param checkFree
     */
    public static void showDialogFreeCall(final String name, final String number, final String local, final Context context, final String dialType, final boolean checkFree) {

        final CustomDialog2.Builder builder = new CustomDialog2.Builder(context);
        final CustomDialog2 dialog = builder.create();
        // 初始化内容
        builder.getDialog_chioce().setVisibility(View.GONE);
        builder.getDialogMessage().setText(R.string.callphone_not_wifi_hint);
        builder.getDialog_call().setText(R.string.callphone_not_wifi_btn1);
        builder.getDialog_inivt().setText(R.string.callphone_not_wifi_btn2);
        builder.getDialog_chioce_msg().setVisibility(View.GONE);
        // 设置监听事件
        builder.getDialog_call().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof Activity) {
                    android.app.FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                    VsUtil.callNumber(name, number, (local != null && !"".equals(local)) ? local : VsLocalNameFinder.findLocalName(number, false, context), context, null, false, fragmentManager);
                } else {
                    VsUtil.callNumber(name, number, (local != null && !"".equals(local)) ? local : VsLocalNameFinder.findLocalName(number, false, context), context, null, false, null);
                }
                dialog.cancel();
            }
        });
        builder.getDialog_inivt().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 跳免费
                Intent intent = new Intent();
                intent.putExtra("called_name", name);
                intent.putExtra("called_num", number);
                intent.putExtra("local_name", local);
                intent.putExtra("callType", 1);
                intent.setClass(context, TutorialRegistrationCall.class);
                context.startActivity(intent);
                dialog.cancel();
            }
        });

        builder.getDialog_cannel().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
                AudioPlayer.getInstance().stopRingBefore180Player();
            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 检测是否输入号码是否是用户，提醒是否打开
     *
     * @param context
     * @param phoneNumber
     */
    public static boolean checkPhoneNmbeIsVs(final Context context, final String callName, final String phoneNumber, final VsContactItem item) {
        if (VsUserConfig.getDataString(context, VsUserConfig.JKey_PhoneNumber).equals(phoneNumber)) {
            return true;
        }
        boolean flag = VsUserConfig.getDataBoolean(context, VsUserConfig.JKEY_DIALOG_HINT, false);
        if (!checheNumberIsVsUser(context, phoneNumber) && !flag) {
            context.sendBroadcast(new Intent(VsUserConfig.JKey_CLOSE_USER_LEAD));// 情况输入内容
            if (VsUserConfig.getDataBoolean(context, VsUserConfig.JKEY_SETTING_HINT_VOICE, true)) {// 判断语音提醒是否打开
                // AudioPlayer.getInstance().startRingBefore180Player(R.raw.un_vs_hm,
                // false);// 播放提示音
            }
            final CustomDialog2.Builder builder = new CustomDialog2.Builder(context);
            final CustomDialog2 dialog = builder.create();
            String formatNumber = VsUtil.filterPhoneNumber(phoneNumber);
            if (!formatNumber.startsWith("1")) {
                builder.getDialog_inivt().setVisibility(View.GONE);
                builder.getDialog_inivt_line().setVisibility(View.GONE);
            }
            builder.getDialog_call().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item != null) {// 判断是否有对应联系人
                        VsUtil.callNumber((item.mContactName == null || item.mContactName.equals("")) ? phoneNumber : item.mContactName, phoneNumber, item.mContactBelongTo,
                                context, null, false, null);
                    } else {
                        VsUtil.callNumber(callName, phoneNumber, VsLocalNameFinder.findLocalName(phoneNumber, false, context), context, null, false, null);
                    }
                    AudioPlayer.getInstance().stopRingBefore180Player();// 释放提示音资源
                    dialog.cancel();
                }
            });
            builder.getDialog_inivt().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String mRecommendInfo = VsUserConfig.getDataString(context, VsUserConfig.JKey_FRIEND_INVITE);
                    if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
                        String InviteFriendInfo = DfineAction.InviteFriendInfo;
                        String uid = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
                        if (uid != null && !"".equals(uid)) {
                            StringBuilder builder = new StringBuilder(InviteFriendInfo);
                            builder.append("a=").append(uid).append("&s=sm");
                            mRecommendInfo = new String(builder);
                        } else {
                            mRecommendInfo = DfineAction.InviteFriendInfo;
                        }
                    } else {
                        mRecommendInfo = VsUserConfig.getDataString(context, VsUserConfig.JKey_FRIEND_INVITE);
                    }
                    SendNoteObserver.sendSendNoteNumber = 1;
                    // 发送邀请短信
                    VsUtil.smsShare(context, mRecommendInfo, phoneNumber);
                    AudioPlayer.getInstance().stopRingBefore180Player();// 释放提示音资源
                    dialog.cancel();
                }
            });

            builder.getDialog_chioce().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (VsUserConfig.getDataBoolean(context, VsUserConfig.JKEY_DIALOG_HINT, false)) {
                        VsUserConfig.setData(context, VsUserConfig.JKEY_DIALOG_HINT, false);
                        builder.getDialog_chioce().setImageResource(R.drawable.vs_small_chioce_false);
                    } else {
                        VsUserConfig.setData(context, VsUserConfig.JKEY_DIALOG_HINT, true);
                        builder.getDialog_chioce().setImageResource(R.drawable.vs_small_chioce_true);
                    }
                }
            });
            builder.getDialog_cannel().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AudioPlayer.getInstance().stopRingBefore180Player();// 释放提示音资源
                    dialog.cancel();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        } else {
            return true;
        }
        return false;
    }

    public static String getContactPhone(VsContactItem item, String number) {
        for (String phone : item.phoneNumList) {
            if (phone.contains(number)) {
                return phone;
            }
        }
        return null;
    }

    /**
     * 是否无网络 true 表示没有
     *
     * @param mContext
     * @return
     */
    public static boolean isNoNetWork(final Context mContext) {
        if (GlobalVariables.netmode == 0) {
            showDialog(DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.prompt), mContext.getResources().getString(R.string
                    .no_network_prompt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                }
            }, null, mContext);
            return true;
        }
        return false;
    }

    /**
     * 是否无网络 true 表示没有,没有网络播放提示音
     *
     * @param mContext
     * @return
     */
    public static boolean isCallNoNetWork(final Context mContext) {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            showDialog(null, mContext.getResources().getString(R.string.no_network_prompt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    mContext.startActivity(intent);
                }
            }, null, mContext);
            return true;
        }
        return false;
    }

    /**
     * 拨打
     *
     * @param name
     * @param number
     * @param local
     * @param mContext
     * @param dialType
     */
    public static void dialphone(final String name, final String number, final String local, final Context mContext, String dialType, final boolean checkFree) {
        Intent intent = new Intent();
        intent.putExtra("called_name", name);
        intent.putExtra("called_num", number);
        intent.putExtra("local_name", local);
        GlobalVariables.isEnterCallScreen = false;
        // wifi下回拨--默认开启
        boolean wifiCallback_state = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true);
        // 3g、4g下回拨--默认开启
        boolean callback_state_3g_4g = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true);
        String call_type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_dial_types, "");
        String def_call_type = "2";

        try {
            JSONObject js = new JSONObject(call_type);
            String type = (String) js.get("type");
            String def = (String) js.get("default");
            Boolean bl = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_dial_swich, false);

            if ("all".equals(type)) {
                if (bl) {

                } else {
                    if ("direct".equals(def)) {
                        def_call_type = "0";
                    } else if ("callback".equals(def)) {
                        def_call_type = "2";
                    }
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
                }

            } else if ("direct".equals(type)) {
                def_call_type = "0";
                VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_swich, false);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
            } else {
                def_call_type = "2";
                VsUserConfig.setData(mContext, VsUserConfig.JKey_dial_swich, false);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // VsUserConfig.setData(mContext, VsUserConfig.JKey_USERDIALVALUE,
        // def_call_type);
        String type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_USERDIALVALUE, def_call_type);
        /**
         * 直拨开关
         */
        boolean softCallFlag = false;
        // 判断能否打直拨
        if (type.equals("0") || type.equals("3")) {
            softCallFlag = true;
        } else if (type.equals("1")) {
            if (VsUtil.isWifi(mContext)) {
                softCallFlag = true;
            } else {
                softCallFlag = false;
            }
        } else {
            softCallFlag = false;
        }

        // if (VsUtil.isWifi(mContext) && !wifiCallback_state) {
        // softCallFlag = true;
        // } else if ("3g".equals(VsUtil.getNetTypeString()) &&
        // !callback_state_3g_4g) {
        // softCallFlag = true;
        // } else if ("4g".equals(VsUtil.getNetTypeString()) &&
        // !callback_state_3g_4g) {
        // softCallFlag = true;
        // } else if ("gprs".equals(VsUtil.getNetTypeString()) &&
        // !callback_state_3g_4g) {
        // softCallFlag = true;
        // }
        // if (!callback_state_3g_4g && !wifiCallback_state) {
        // softCallFlag = true;
        // }

        if (checheNumberIsVsUser(mContext, number) && checkFree) {
            if (!GlobalVariables.isStartConnect) {
                if (VsUtil.isWifi(mContext)) {
                    // 跳免费
                    intent.putExtra("callType", 1);
                    intent.setClass(mContext, TutorialRegistrationCall.class);
                    mContext.startActivity(intent);
                } else {
                    AudioPlayer.getInstance().startRingBefore180Player(R.raw.vs_not_wifi, false);
                    showDialogFreeCall(name, number, local, mContext, dialType, checkFree);
                }
            } else {
                CustomLog.i("SoftCallActivity", "receiver------BroadCast--send");
                mContext.sendBroadcast(new Intent(GlobalVariables.action_agin_notification));
            }
        } else if (softCallFlag) {
            if (!GlobalVariables.isStartConnect) {
                // 跳直拨
                intent.putExtra("callType", 0);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(mContext, TutorialRegistrationCall.class);
                mContext.startActivity(intent);
            } else {
                CustomLog.i("SoftCallActivity", "receiver------BroadCast--send");
                mContext.sendBroadcast(new Intent(GlobalVariables.action_agin_notification));
            }
        } else {
            if (!GlobalVariables.isStartConnect) {
                GlobalVariables.isEnterCallScreen = false;
                // 跳回拨---执行回拨展示
                Intent callback_intent = new Intent(mContext, VSCallBackHintActivity.class);
                callback_intent.putExtra("callNumber", number);
                callback_intent.putExtra("callName", name);
                callback_intent.putExtra("localName", local);
                mContext.startActivity(callback_intent);

            } else {
                CustomLog.i("SoftCallActivity", "receiver------BroadCast--send");
                mContext.sendBroadcast(new Intent(GlobalVariables.action_agin_notification));
            }
        }
    }

    /**
     * 查询电话号码是否为VS好友
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checheNumberIsVsUser(Context context, String phoneNum) {
        String phoneArray = VsUserConfig.getDataString(context, VsUserConfig.JKEY_GETVSUSERINFO);
        String phoneNumber = filterPhoneNumber(phoneNum);
        if (phoneArray != null && !"".equals(phoneArray)) {
            if (checkMobilePhone(phoneNumber) && phoneArray.contains(phoneNumber)) {
                try {
                    String phone = new JSONObject(VsUserConfig.getDataString(context, VsUserConfig.JKEY_GETVSUSERINFO)).getString(phoneNumber);
                    if (phone != null) {
                        return true;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        return false;
    }

    /**
     * 根据目标跳转
     *
     * @param url
     * @param context
     * @param activityId
     * @param actionType
     */
    @SuppressWarnings("deprecation")
    public static void skipForTarget(String url, Activity context, int activityId, String actionType) {
        if (url == null || context == null) {
            return;
        }
        // 如果不是协议跳转 url即为界面编号 直接启动即可
        if (activityId != 0 && actionType != null) {
            // 动作统计 activityId界面id ,actionType 界面类型
            // KcAction.insertAction(activityId, actionType,
            // String.valueOf(System.currentTimeMillis() / 1000), "0",
            // context);
        }
        url = URLDecoder.decode(url);
        // 判断是否是协议跳转
        if (url.indexOf(DfineAction.scheme_head) == -1) {
            if (VsApplication.getInstance().getActivitySize() == 0) {
                // 如果软件没启动。要启动主页面
                // 这里启动主页面。注意：启动主页面模式设置成singleTask
                Intent intent = new Intent(DfineAction.goMainAction);
                intent.putExtra("messagelink", url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                VsApplication.getContext().startActivity(intent);
            } else {
                startActivity(url, context, null);
            }
        } else {
            skipForScheme(url, context, null);
        }
    }

    /**
     * 根据协议跳转不同的方法
     *
     * @param url
     * @param context
     * @throws JSONException
     */
    @SuppressWarnings("deprecation")
    public static void skipForSchemeNew(String url, Activity context) {
        try {
            String scheme_type_inline = "inline";
            String scheme_type_sdk = "sdk";
            String scheme_type_business = "business";
            String scheme_type_web = "web";
            String scheme_type_wap = "wap";
            String scheme_type_finish = "finish";
            String scheme_type_parame = "?param=";
            System.currentTimeMillis();
            url = URLDecoder.decode(url.replace(DfineAction.scheme_head, ""));
            JSONObject json = null;
            if (url.startsWith(scheme_type_inline)) {
                // 本应用界面跳转。
                json = new JSONObject(url.replace(scheme_type_inline + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, null);
            } else if (url.startsWith(scheme_type_sdk)) {
                // 跳转到SDK
                json = new JSONObject(url.replace(scheme_type_sdk + scheme_type_parame, ""));
                schemeToSdk(context, json);
            } else if (url.startsWith(scheme_type_business)) {
                // 根据业务类型做不同的处理
                json = new JSONObject(url.replace(scheme_type_business + scheme_type_parame, ""));
                jumMethod(context, json);
            } else if (url.startsWith(scheme_type_web)) {
                // 跳转到webview
                json = new JSONObject(url.replace(scheme_type_web + scheme_type_parame, ""));
                schemeToWeb(json.getString("url"), context);
            } else if (url.startsWith(scheme_type_wap)) {
                // 跳转到wap页面
                json = new JSONObject(url.replace(scheme_type_wap + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, null);
                schemeToWap(json.getString("url"), json.getString("title"), context);
            } else if (url.startsWith(scheme_type_finish)) {
                // 在webview里关闭activity。 在次跳转到要去的界面
                json = new JSONObject(url.replace(scheme_type_finish + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, null);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 业务跳转处理方法
     *
     * @param json
     */
    public static void jumMethod(Context mContext, JSONObject json) {
        if (json != null) {
            try {
                if ("rechargetype".equals(json.getString("type"))) {
                    startActivity("0301", mContext, null);
                } else if ("sharetype".equals(json.getString("type"))) {
                    Intent intent = new Intent(GlobalVariables.action_makemoney_share);
                    intent.putExtra("msg", json.toString());
                    mContext.sendBroadcast(intent);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据协议跳转不同的方法
     *
     * @param url
     * @param context
     * @throws JSONException
     */
    @SuppressWarnings("deprecation")
    public static void skipForScheme(String url, Activity context, Object item) {
        try {
            String scheme_type_inline = "inline";
            String scheme_type_sdk = "sdk";
            String scheme_type_business = "business";
            String scheme_type_web = "web";
            String scheme_type_wap = "wap";
            String scheme_type_finish = "finish";
            String scheme_type_parame = "?param=";
            System.currentTimeMillis();
            url = URLDecoder.decode(url.replace(DfineAction.scheme_head, ""));
            JSONObject json = null;
            if (url.startsWith(scheme_type_inline)) {
                // 本应用界面跳转。
                json = new JSONObject(url.replace(scheme_type_inline + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, item);
            } else if (url.startsWith(scheme_type_sdk)) {
                // 跳转到SDK
                json = new JSONObject(url.replace(scheme_type_sdk + scheme_type_parame, ""));
                schemeToSdk(context, json);
            } else if (url.startsWith(scheme_type_business)) {
                // 根据业务类型做不同的处理
                json = new JSONObject(url.replace(scheme_type_business + scheme_type_parame, ""));
                schemeToBusiness(context, json);
            } else if (url.startsWith(scheme_type_web)) {
                // 跳转到webview
                json = new JSONObject(url.replace(scheme_type_web + scheme_type_parame, ""));
                schemeToWeb(json.getString("url"), context);
            } else if (url.startsWith(scheme_type_wap)) {
                // 跳转到wap页面
                json = new JSONObject(url.replace(scheme_type_wap + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, item);
                schemeToWap(json.getString("url"), json.getString("title"), context);
            } else if (url.startsWith(scheme_type_finish)) {
                // 在webview里关闭activity。 在次跳转到要去的界面
                json = new JSONObject(url.replace(scheme_type_finish + scheme_type_parame, ""));
                startActivity(json.getString("page"), context, item);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

    /**
     * 根据协议跳转业务方法
     *
     * @param url
     * @param mContext
     * @param json
     * @param scheme_type_business
     * @param scheme_type_parame
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    public static void schemeToBusiness(Activity mContext, JSONObject json) throws JSONException {
        Enumeration<String> et = json.keys();
        Bundle bund = new Bundle();
        setValueToBundle(bund, mContext);
        while (et.hasMoreElements()) {
            String key = (String) et.nextElement();
            String value = json.getString(key);
            bund.putString(key, value);
            CustomLog.i("GDK", "key=" + key + " value=" + value);
        }
        BusinessProcess(bund, mContext);
        // intent.putExtras(bund);
        // context.startActivity(intent);
    }

    /**
     * 根据协议跳转到系统游览器
     *
     * @param url
     * @param mContext
     */
    public static void schemeToWeb(String url, Context mContext) {
        if (mContext == null) {
            return;
        }
        if (!URLUtil.isNetworkUrl(url))// 判断url是否有效地址
        {
            return;
        }
        // 调用系统浏览器
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        mContext.startActivity(intent);
    }

    /**
     * 根据协议跳转到webview
     *
     * @param url
     * @param mContext
     */
    public static void schemeToWap(String url, String title, Context mContext) {
        Intent intent = new Intent();
        String[] aboutBusiness = new String[]{title, "", VsUtil.getUrlParams(url, mContext)};
        intent.putExtra("AboutBusiness", aboutBusiness);
        intent.setClass(mContext, WeiboShareWebViewActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 业务处理方法。
     *
     * @param bund
     * @param mContext
     */
    public static void BusinessProcess(Bundle bund, Activity mContext) {
        try {
            Intent intent = new Intent();
            String type = bund.getString("type");
            if (type.equals("recharge")) {
                startActivity("0301", mContext, null);
            } else if (type.equals("rechargetype")) {
                intent.putExtras(bund);
                intent.setClass(mContext, VsRechargePayTypes.class);
                mContext.startActivityForResult(intent, 1);
            } else if (type.equals("share")) {
                if (VsNetWorkTools.isNetworkAvailable(VsApplication.getContext()) == false) {
                    Toast.makeText(VsApplication.getContext(), DfineAction.NETWORK_INVISIBLE, Toast.LENGTH_SHORT).show();
                }
                String channel = bund.getString("channel");
                String content = bund.getString("share_text");
                String share_imageurl = bund.getString("share_imageurl");
                if (channel.equals("sms")) {
                    smsShare(mContext, content, share_imageurl);
                } else if (channel.equals("weixin")) {
                    weixinShare(mContext, content, returnBitMap(share_imageurl), channel);
                } else if (channel.equals("weixinquan")) {
                    weixinShare(mContext, content, returnBitMap(share_imageurl), channel);
                } else if (channel.equals("weibo")) {
                    // sinaShare(mContext, content);
                } else if (channel.equals("qq")) {
                    qqShare(mContext, content, share_imageurl, false);
                } else if (channel.equals("qzone")) {
                    qqShare(mContext, content, share_imageurl, true);
                } else if (channel.equals("txl")) {
                    // 通讯录分享
                } else if (channel.equals("share_sms")) {
                    // 红包分享
                }
            } else if (type.equals("historycontact")) {
                // 在webview里做了处理
            } else if (type.equals("call")) {
                String phone = bund.getString("phone");
                String calltype = bund.getString("calltype");
                if (calltype.equals("4")) {
                    LocalCallNumber(mContext, phone);
                } else {
                    callNumber(phone, phone, null, mContext, bund.getString("calltype"), false, null);
                }
            } else if (type.equals("setalarm")) {
                setAlarmForActivity(mContext, Long.parseLong(bund.getString("time")), bund.getString("title"), bund.getString("url"));
            } else if (type.equals("closealarm")) {
                closeAlarm(mContext);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 图片下载地址转换为bitmap对象
     *
     * @param url
     * @return
     */
    public static Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            if (url != null && !"".equals(url)) {
                myFileUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 根据协议跳转到SDK方法
     *
     * @param url
     * @param context
     * @param json
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    public static void schemeToSdk(Context context, JSONObject json) throws JSONException {
        Enumeration<String> et = json.keys();
        Intent intent = new Intent();
        Bundle bund = new Bundle();
        while (et.hasMoreElements()) {
            String key = (String) et.nextElement();
            String value = json.getString(key);
            if (key.equals("action")) {
                intent.setAction(value);
            } else {
                bund.putString(key, value);
            }
            CustomLog.i("GDK", "key=" + key + " value=" + value);
        }
        if (checkApkExist(context, bund.getString("package"))) {
            setValueToBundle(bund, context);
            intent.putExtras(bund);
            context.sendBroadcast(intent);
        } else {
            // showMessageDialog(DfineAction.RES.getString(R.string.product) +
            // context.getResources().getString(R.string.prompt), context
            // .getResources().getString(R.string.more_sdk_uploadcontext), 0,
            // new UploadSdkClickListener(context),
            // null, context, "立即更新",
            // context.getResources().getString(R.string.cancel));
        }
    }

    /**
     * 设置共用的参数到Bundle
     *
     * @param bund
     * @param context
     */
    public static void setValueToBundle(Bundle bund, Context context) {
        bund.putString("brandid", DfineAction.brandid);
        bund.putString("uid", VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId));
        bund.putString("phone", VsUserConfig.getDataString(context, VsUserConfig.JKey_PhoneNumber));
        bund.putString("pv", DfineAction.pv);
        bund.putString("v", BuildConfig.VERSION_NAME);
        bund.putString("pwd", VsMd5.md5(VsUserConfig.getDataString(context, VsUserConfig.JKey_Password)));
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getPackageName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 判断apk是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据给定的Intent进行Activity跳转
     *
     * @param activity Activity对象
     * @param intent   要传递的Intent对象
     */
    public static void startActivity(Context mContext, Intent intent) {
        mContext.startActivity(intent);
    }

    /**
     * 根据传进来的界面id跳转到不同的界面
     *
     * @param link     界面id
     * @param isStart  本程序是否已启动
     * @param mContext
     * @throws Exception
     */
    public static void startActivity(String link, Context mContext, Object item) {
        CustomLog.i("GDK", "link=" + link);
        Intent intent = new Intent();
        // 未登录用户点击push消息程序启动进入方法
        if (link.equals("0101") || link.equals("0000") || link.equals("10001")) {// 登录程序跳转到通话记录和拨号界面
            intent.putExtra("indicator", 0);
            GlobalVariables.curIndicator = 0;
            intent.setClass(mContext, VsMainActivity.class);
        }
        // 联系人界面
        else if (link.equals("1000") || link.equals("1000") || link.equals("20001")) {
            intent.putExtra("indicator", 1);
            GlobalVariables.curIndicator = 1;
            intent.setClass(mContext, VsMainActivity.class);
        } else if (link.equals("40002")) {// 个人信息界面
            intent.setClass(mContext, VsMyInfoTextActivity.class);
        } else if (link.equals("30002")) {// 赚钱计划界面
            String urlTo = "http://www.wind163.com/help/money_plan.html";
            String[] aboutBusiness = new String[]{"赚钱计划", "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        } else if (link.equals("30005")) {// 帮助中心界面
            String urlTo = "http://www.wind163.com/help/help.html";
            String[] aboutBusiness = new String[]{"帮助中心", "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        } else if (link.equals("40003")) {// 我的余额界面
            intent.putExtra("flag", "3");
            intent.setClass(mContext, VsMyBalanceDetailActivity.class);
        } else if (link.equals("40004")) {// 我的套餐界面
            intent.putExtra("flag", "2");
            intent.setClass(mContext, VsMyBalanceDetailActivity.class);
        } else if (link.equals("40005")) {// 我的红包界面
            intent.setClass(mContext, VsMyRedPagActivity.class);
        } else if (link.equals("40006")) {// 我的二维码界面
            intent.setClass(mContext, KcMyQcodeActivity.class);
        } else if (link.equals("40008")) {// 设置界面
            intent.setClass(mContext, VsSetingActivity.class);
        } else if (link.equals("40009")) {// 拨打方式界面
            intent.setClass(mContext, VsCallTypeSetingActivity.class);
        } else if (link.equals("40010")) {// 关于界面
            intent.setClass(mContext, KcQcodeActivity.class);
        } else if (link.equals("40007")) {// 扫码绑定界面
            intent.putExtra("code", "2");
            intent.setClass(mContext, CaptureActivity.class);
        }
        // 话费界面
        else if (link.equals("0301") || link.equals("30001")) {
            intent.putExtra("indicator", 2);
            GlobalVariables.curIndicator = 2;
            intent.setClass(mContext, VsMainActivity.class);
        } else if (link.equals("40001")) {
            intent.putExtra("indicator", 3);
            GlobalVariables.curIndicator = 3;
            intent.setClass(mContext, VsMainActivity.class);
        } else if (link.equals("3027")) {// 迷你小游戏
            intent.setClass(mContext, VsMiniGameActivity.class);
        } else if (link.equals("4002") || link.equals("30004")) {// 扫描二维码
            intent.putExtra("code", "0");
            intent.setClass(mContext, CaptureActivity.class);
        } else if (link.equals("4001")) {// 手势密码

            intent.setClass(mContext, KcUnlockedActivity.class);
        } else if (link.equals("3035")) {
            intent.setClass(mContext, VsSetingVoicePianoActivity.class);
        }
        // 充值方式
        else if (link.equals("0302") || link.equals("2000")) {
            if (item != null) {
                try {
                    JSONObject json = (JSONObject) item;
                    intent.putExtra("brandid", "");
                    intent.putExtra("goodsid", json.getString("goodsid"));
                    intent.putExtra("goodsvalue", json.getString("goodsvalue"));
                    intent.putExtra("goodsname", json.getString("goodsname"));
                    intent.putExtra("goodsdes", json.getString("goodsdes"));
                    intent.putExtra("recommend_flag", "n");
                    intent.putExtra("convert_price", "");
                    intent.putExtra("present", "");
                    intent.putExtra("pure_name", "");
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
            intent.setClass(mContext, VsRechargePayTypes.class);
        }
        // 余额
        else if (link.equals("0401") || link.equals("3016")) {
            if (!isLogin(mContext.getResources().getString(R.string.seach_balance_login_prompt), mContext))
                return;
            intent.setClass(mContext, VsBalanceActivity.class);
        } // 赚话费
        else if (link.equals("3002") || link.equals("3002")) {
            intent.setClass(mContext, VsMakeMoneyActivity.class);
        }

        // 资费说明页面
        else if (link.equals("3015") || link.equals("3015")) {
            String urlTo = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_TARIFF);
            /*
             * if (urlTo.indexOf("?") == -1) { urlTo = urlTo +
             * "?userDto.account=" + VsUserConfig.getDataString(mContext,
             * VsUserConfig.JKey_KcId) + "&userDto.password=" +
             * VsMd5.md5(VsUserConfig.getDataString(mContext,
             * VsUserConfig.JKey_Password)); }
             */
            /*
             * if (VsUtil.isNull(urlTo)) { urlTo =
             * "http://m.ibbzg.com/bz/zf.html"; }
             */
            String[] aboutBusiness = new String[]{mContext.getString(R.string.favourate_button_tariff), "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        }

        // 官方网站页面
        else if (link.equals("3017")) {
            String urlTo = DfineAction.WAPURI;
            if (urlTo.indexOf("?") == -1) {
                urlTo = urlTo + "?userDto.account=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) + "&userDto.password=" + VsMd5.md5(VsUserConfig.getDataString
                        (mContext, VsUserConfig.JKey_Password));
            }
            String[] aboutBusiness = new String[]{DfineAction.RES.getString(R.string.product) + "官网", "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        }
        // 联系客服页面
        else if (link.equals("3018")) {
            intent.setClass(mContext, VsAboutActivity.class);
        }
        // 帮助中心页面
        else if (link.equals("3019")) {
            String urlTo = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_HELP);
            /*
             * if (VsUtil.isNull(urlTo)) { urlTo =
             * "http://m.ibbzg.com/bz/bz.html"; }
             */

            /*
             * if (urlTo.indexOf("?") == -1) { urlTo = urlTo +
             * "?userDto.account=" + VsUserConfig.getDataString(mContext,
             * VsUserConfig.JKey_KcId) + "&userDto.password=" +
             * VsMd5.md5(VsUserConfig.getDataString(mContext,
             * VsUserConfig.JKey_Password)); }
             */
            String[] aboutBusiness = new String[]{mContext.getString(R.string.help), "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        } else if (link.equals("301997")) {// 服务条款
            String urlTo = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_SERVICE_TIAO);
            /*
             * if (VsUtil.isNull(urlTo)) { // urlTo =
             * "http://m.ibbzg.com/bz/tk.html"; }
             */
            String[] aboutBusiness = new String[]{mContext.getString(R.string.welcome_main_clause), "service", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        }
        /*
         * else if (link.equals("301998")) {//商城 String urlTo =
         * VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_MALL); if
         * (VsUtil.isNull(urlTo)) { urlTo = "http://www.51bbzg.com"; } String[]
         * aboutBusiness = new String[] { mContext.getString(R.string.mall), "",
         * urlTo }; intent.putExtra("AboutBusiness", aboutBusiness);
         * intent.setClass(mContext, WeiboShareWebViewActivity.class); } else if
         * (link.equals("301999")) {//服务 String urlTo =
         * VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_SERVICE);
         * if (VsUtil.isNull(urlTo)) { urlTo = "http://fw.ibbzg.com"; } String[]
         * aboutBusiness = new String[] { mContext.getString(R.string.service),
         * "", urlTo }; intent.putExtra("AboutBusiness", aboutBusiness);
         * intent.setClass(mContext, WeiboShareWebViewActivity.class); }
         */
        // 在线升级页面
        else if (link.equals("3020")) {
            intent.setClass(mContext, VsSetingActivity.class);
        }
        // 签到---
        else if (link.equals("3006")) {
            intent.setClass(mContext, VsSignInFirstActivity.class);
        }
        // 查询话单页面
        else if (link.equals("3024")) {
            if (!isLogin(mContext.getResources().getString(R.string.seach_bill_login_prompt), mContext)) {
                return;
            }
            String urlTo1 = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_CALL_LOG);
            String[] aboutBusiness1 = new String[3];
            aboutBusiness1[0] = mContext.getResources().getString(R.string.vs_balance_expend);
            aboutBusiness1[1] = "";
            aboutBusiness1[2] = urlTo1;
            intent.putExtra("AboutBusiness", aboutBusiness1);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        }
        // 来电显示页面
        else if (link.equals("3025")) {
            if (!isLogin(mContext.getResources().getString(R.string.call_display_login_prompt), mContext)) {
                return;
            }
            intent.setClass(mContext, VsCallerIdentificationActivity.class);
        }
        // 赚话费任务界面---
        else if (link.equals("040201")) {
            if (item != null) {
                intent.putExtra("vsInviteItem", (VsInviteItem) item);
            }
            intent.setClass(mContext, VsMakeMoneyTaskActivity.class);

        } else if (link.contains("http://")) {
            String urlTo = link;
            String[] aboutBusiness = new String[]{(String) item, "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
            // JSONObject json = (JSONObject) item;
            // String[] aboutBusiness;
            // try {
            // aboutBusiness = new String[] { json.getString("title"),
            // json.getString("title"), urlTo };
            // intent.putExtra("AboutBusiness", aboutBusiness);
            // intent.setClass(mContext, WeiboShareWebViewActivity.class);
            // } catch (JSONException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        } else if (link.equals("3026")) {// 什么是回拨
            String urlTo = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_URL_CALLBACK);
            if (urlTo.indexOf("?") == -1) {
                urlTo = urlTo + "?userDto.account=" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId) + "&userDto.password=" + VsMd5.md5(VsUserConfig.getDataString
                        (mContext, VsUserConfig.JKey_Password));
            }

            String[] aboutBusiness = new String[]{mContext.getString(R.string.help), "", urlTo};
            intent.putExtra("AboutBusiness", aboutBusiness);
            intent.setClass(mContext, WeiboShareWebViewActivity.class);
        } else if (link.trim().equals("call")) {
            JSONObject json = (JSONObject) item;
            String callNumber = null;
            final Context context = mContext;
            try {
                callNumber = json.getString("phone");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final String phone = callNumber;
            DialogInterface.OnClickListener okListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VsUtil.callNumber("", phone, "", context, "", false, null);
                }
            };
            DialogInterface.OnClickListener cancel = new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VsUtil.LocalCallNumber(context, phone);
                }
            };
            showYesNoDialog(mContext, phone, "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打", DfineAction.RES.getString(R.string.product) + "拨打", DfineAction
                    .RES.getString(R.string.phone_call), okListener, cancel, null);
        } else {
            return;
        }
        startActivity(mContext, intent);
    }

    /**
     * 显示选择提示框
     *
     * @param okBtmListener
     * @param cancelBtnListener
     */
    protected static void showYesNoDialog(Context mContext, String title, String message, String PositiveButton, String NegativeButton, DialogInterface.OnClickListener
            okBtmListener, DialogInterface.OnClickListener cancelBtnListener, DialogInterface.OnCancelListener cancelListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(Html.fromHtml(message) + "");
        builder.setPositiveButton(PositiveButton, okBtmListener);
        builder.setNegativeButton(NegativeButton, cancelBtnListener);
        builder.setOnCancelListener(cancelListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 跳转程序内部界面（包括极致界面）
     *
     * @param context
     */
    public static void showInView(String url, Context context, int activityId, String actionType) {
        // 如果不是协议跳转 url即为界面编号 直接启动即可
        if (activityId != 0 && actionType != null) {
            VsAction.insertAction(activityId, actionType, String.valueOf(System.currentTimeMillis() / 1000), "0", context);
        }
        try {
            url = URLDecoder.decode(url);
            CustomLog.i("DGK", "url = " + url);
            if (url.indexOf(DfineAction.image_head) == -1) {
                if (VsApplication.getInstance().getActivitySize() == 0) {// 没有启动时
                    Intent intent = new Intent(DfineAction.goMainAction);
                    intent.putExtra("messagelink", url);
                    context.startActivity(intent);
                } else {// 内部跳转
                    VsUtil.startActivity(url, context, actionType);
                }
            } else {
                // 特殊协议跳转
                JSONObject json = null;
                url = URLDecoder.decode(url.replace(DfineAction.image_head, ""));
                if (url.startsWith("inline?param")) {// 本应用界面跳转。
                    json = new JSONObject(url.replace("inline?param=", ""));

                    // CollinWang修改2014.11.20
                    String ad_url = json.getString("url");
                    String[] str1 = ad_url.split("url=");
                    String[] str2 = str1[1].split("&");
                    String urlCode = new String(Base64.decode(str2[0]), "utf-8");
                    CustomLog.i("DGK", "inlineurl = " + urlCode);
                    if (!urlCode.equals("")) {
                        VsUtil.startActivity(urlCode, context, json);
                    }

                } else if (url.startsWith("web?param")) {// CollinWang2014.12.11
                    json = new JSONObject(url.replace("web?param=", ""));
                    Uri uri = Uri.parse(json.getString("url"));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } else if (url.startsWith("inline_wap?param")) {
                    String jsonstr = url.replace("inline_wap?param=", "");
                    CustomLog.i("DGK", "inline_wap = " + url);
                    json = new JSONObject(jsonstr);
                    String ad_url = json.getString("url");
                    VsUtil.startActivity(ad_url, context, json);
                } else if (url.startsWith("business?param")) {// CollinWang
                    json = new JSONObject(url.replace("business?param=", ""));
                    VsUtil.startActivity(json.getString("type"), context, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否登录 并弹框提示
     *
     * @return
     */
    public static boolean isLogin(String msg, Context mContext) {
        boolean retbool = true;
        if (!checkHasAccount(mContext)) {
            retbool = false;
            // showDialog(DfineAction.RES.getString(R.string.product) +
            // mContext.getResources().getString(R.string.prompt), msg,
            // mContext.getResources()
            // .getString(R.string.ok),
            // mContext.getResources().getString(R.string.cancel), new
            // LoginBtnClickListener(mContext), null, mContext);
            Intent it = new Intent(mContext, VsLoginActivity.class);
            mContext.startActivity(it);
        } else {
            retbool = true;
        }
        return retbool;
    }

    /**
     * 把链表转换为
     *
     * @param phoneNumList
     * @return
     */
    public static String[] listToStringArray(ArrayList<String> phoneNumList) {
        if (phoneNumList == null || phoneNumList.size() == 0) {
            return null;
        }
        String[] ret = new String[phoneNumList.size()];
        for (int i = 0; i < phoneNumList.size(); i++) {
            ret[i] = phoneNumList.get(i);
        }
        return ret;
    }

    /**
     * 获取手机唯一标识
     *
     * @param context
     * @return
     */
    public static String getAloneImei(Context context) {
        String aloneImei = null;
        try {
            aloneImei = getOperatorBySlot(context, "getDeviceIdGemini", 1);
        } catch (GeminiMethodNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                aloneImei = getOperatorBySlot(context, "getDeviceId", 1);
            } catch (GeminiMethodNotFoundException e1) {
                // TODO: handle exception
                e1.printStackTrace();
            }
        }
        Log.e(TAG, "手机唯一标识msg===>" + aloneImei);
        return aloneImei == null ? "" : aloneImei;
    }

    private static String getOperatorBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {
        // TODO Auto-generated method stub
        String inumeric = null;
        TelephonyManager telephony = null;
        if (null != context) {
            telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (telephony == null) {
            return "";
        }
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);
            if (ob_phone != null) {
                inumeric = ob_phone.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }
        return inumeric;
    }

    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -3241033488141442594L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }

    /**
     * 登录确定按钮响应类
     */
    public static class LoginBtnClickListener implements DialogInterface.OnClickListener {
        Context context;

        LoginBtnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent it = new Intent(context, VsLoginActivity.class);
            context.startActivity(it);
        }
    }

    /**
     * 弹出提示框
     *
     * @param title
     * @param message
     * @param ok
     * @param cancel
     * @param okBtmListener
     * @param cancelBtnListener
     * @param mContext
     */
    public static void showDialog(String title, String message, String ok, String cancel, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener
            cancelBtnListener, Context mContext) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(ok, okBtmListener);
        builder.setNegativeButton(cancel, cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * @param title
     * @param message
     * @param okBtmListener
     * @param cancelBtnListener
     * @param mContext          默认确定和取消按钮
     */
    protected static void showDialog(String title, String message, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener, Context
            mContext) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok), okBtmListener);
        builder.setNegativeButton(mContext.getResources().getString(R.string.cancel), cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 设置闹铃
     *
     * @param mContext
     * @param Time     响铃的时间点
     */
    public static void setAlarmForActivity(Context mContext, Long Time, String title, String url) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(VsCoreService.VS_ACTION_ALARM_ACTIVITY);
        intent.putExtra("packname", mContext.getPackageName());
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Time * 1000, 24 * 60 * 60 * 1000, sender);// 设置循环执行
        // alarm.setRepeating(AlarmManager.RTC_WAKEUP,
        // System.currentTimeMillis(), 60 * 1000, sender);
        CustomLog.i("GDK", "开启闹铃:" + alarm.toString());
        // alarm.set(AlarmManager.RTC_WAKEUP, Time, sender);// 设置时间点执行一次
    }

    /**
     * 停止闹铃
     *
     * @param mContext
     */
    public static void closeAlarm(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(VsCoreService.VS_ACTION_ALARM_ACTIVITY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        CustomLog.i("GDK", "取消闹铃:" + alarmManager.toString() + "pendingIntent=" + pendingIntent);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 设置系统广播
     *
     * @param mContext
     * @param title
     * @param link
     */
    @SuppressWarnings("deprecation")
    public static void setNotification(Context mContext, String title, String link) {
        // 系统广播
//		Notification m_Notification = new Notification();
//		m_Notification.tickerText = title;
//		m_Notification.defaults = Notification.DEFAULT_SOUND;
//		m_Notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		m_Notification.icon = R.drawable.icon;
        Intent mInten = new Intent(DfineAction.ACTION_SHOW_NOTICEACTIVITY);
        mInten.putExtra("messagelink", link);
        mInten.putExtra("messagetitle", title);
        PendingIntent m_PendingIntent = PendingIntent.getActivity(mContext, 0, mInten, PendingIntent.FLAG_UPDATE_CURRENT);
//		m_Notification.setLatestEventInfo(mContext,
//				DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.point), title,
//				m_PendingIntent);
        @SuppressWarnings("static-access") NotificationManager m_NotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
//		m_NotificationManager.notify(0, m_Notification);

        Notification notification = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.icon)
                /**设置通知的标题**/.setContentTitle(title)
                /**设置通知的内容**/.setContentText(DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.point)).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setOngoing(false).setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND).setContentIntent(m_PendingIntent).build();
        m_NotificationManager.notify(0, notification);
        mInten = null;
    }

    /**
     * 对参数进行判断并返回
     *
     * @param content
     * @return
     */
    public static String rtNoNullString(String content) {
        if (content != null && content.length() > 0) {
            return content;
        }
        return "";
    }

    /**
     * 发送广播
     *
     * @param context 广播的唯一标识
     */
    public static void sendSpecialBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action));
    }

    /**
     * 是否是wifi网络
     *
     * @param mcontext
     * @return
     */
    public static boolean isWifi(Context mcontext) {
        // 获取当前可用网络信息
        ConnectivityManager connMng = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInf = connMng.getActiveNetworkInfo();

        // 如果当前是WIFI连接
        if (netInf != null && "WIFI".equalsIgnoreCase(netInf.getTypeName())) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络状态
     *
     * @return
     */
    public static String getNetTypeString() {
        int net_type = VsNetWorkTools.getSelfNetworkType(VsApplication.getContext());
        String netType = "";
        if (net_type == 1) {
            netType = "wifi"; // wifi
        } else if (net_type == 2) {
            netType = "3g";// 3g
        } else if (net_type == 3) {
            netType = "gprs";// gprs 2g
        } else if (net_type == 4) {
            netType = "4g";
        }
        CustomLog.i(TAG, "net_type=" + net_type);
        return netType;
    }

    /**
     * 下载文件
     *
     * @param urlStr
     * @param mContext
     */
    public synchronized static void getDownLoadFile(String urlFile, String urlStr, Context mContext) {
        String SaveFileName = "";
        String SavePath = null;
        if (urlStr == null || urlStr.length() < 2) {
            return;
        }
        int Findex = urlStr.lastIndexOf("/");
        if (Findex < 0 || Findex == urlStr.length()) {
            // 使用默认的信息
        } else {
            SaveFileName = urlStr.substring(Findex + 1);
        }
        if (VsUpdateAPK.IsCanUseSdCard()) {
            SavePath = urlFile;
        } else {
            SavePath = mContext.getFilesDir().getPath() + File.separator;
        }
        File tmpFile = new File(SavePath);
        if (tmpFile.exists()) {
            // 继续
        } else {
            tmpFile.mkdir();// 新建文件夹
        }
        if (SavePath == null || "" == SavePath) {
            return;
        }
        final File file = new File(SavePath + SaveFileName);

        // 判断文件是否存在，不存在就去下载
        if (!file.exists()) {
            try {
                File tempfile = FileUtils.createFile(SavePath + "temp.tmp");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream input = conn.getInputStream();
                FileOutputStream output = new FileOutputStream(tempfile);
                int len = conn.getContentLength();
                CustomLog.i("GDK", "urlStr=" + urlStr + "len=" + len);

                // Map<String, List<String>> headers = conn.getHeaderFields();
                // Set<Entry<String, List<String>>> set = headers.entrySet();
                // Iterator<Entry<String, List<String>>> iter = set.iterator();
                // for (Iterator iterator = set.iterator(); iterator.hasNext();)
                // {
                // Entry<String, List<String>> entry = (Entry<String,
                // List<String>>) iterator.next();
                // StringBuffer sb = new StringBuffer();
                // for (int i = 0; i < entry.getValue().size(); i++) {
                // sb.append(entry.getValue().get(i));
                // sb.append(",");
                // }
                // CustomLog.i("header", entry.getKey() + ":" + sb.toString());
                // }

                int totalReadedLen = 0;
                byte[] buffer = new byte[4 * 1024];
                conn.connect();
                if (conn.getResponseCode() >= 400) {
                    // Toast.makeText(mContext, "连接超时",
                    // Toast.LENGTH_SHORT).show();
                } else {
                    // 读取大文件
                    int readedLen;
                    while ((readedLen = input.read(buffer)) != -1) {
                        output.write(buffer, 0, readedLen);
                        totalReadedLen += readedLen;
                    }
                    if (totalReadedLen == len) {
                        tempfile.renameTo(file);
                    }
                    output.flush();
                    conn.disconnect();
                    if (output != null) {
                        output.close();
                    }
                    input.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 得到当前时间
     *
     * @return
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strDate = format.format(date);
        return strDate;
    }

    /**
     * 得到当前时间
     *
     * @return
     */
    public static String getDateTwo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String strDate = format.format(date);
        return strDate;
    }

    /**
     * 得到存储的时间是否大于一个月
     *
     * @return
     */
    public static boolean getDateMonth(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt2 = null;
        try {
            dt2 = format.parse(time);
            if (System.currentTimeMillis() - dt2.getTime() > 1000L * 60 * 60 * 24 * 30) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断包名是否存在
     *
     * @param object
     * @return
     */
    public static boolean isHasPackName(Context mContext, String packname) {
        if (packname != null && packname.equals(mContext.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 一段String字体大小不一处理
     *
     * @param type    选择需要处理的类型参数
     * @param content 选择需要处理的内容
     * @param List    <String> 第一个参数为Type 第二个参数为content
     */
    public static String setTextTypeStyles(HashMap<String, ArrayList<String>> hashMap) {
        StringBuffer msp_content = new StringBuffer();
        SpannableString msp = null;
        String type = null;
        for (Entry<String, ArrayList<String>> hMap : hashMap.entrySet()) {
            // 得到处理的内容
            msp = new SpannableString(hMap.getValue().get(1));
            // 得到处理的类型参数
            type = hMap.getValue().get(0);
            // 得到处理的类型
            switch (Integer.parseInt(hMap.getKey())) {
                case 1:
                    /** 设置字体(default,default-bold,monospace,serif,sans-serif) **/
                    msp.setSpan(new TypefaceSpan(type), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 2:
                    /** 设置字体大小（绝对值,单位：像素） **/
                    msp.setSpan(new AbsoluteSizeSpan(Integer.parseInt(type)), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 3:
                    /** 设置字体颜色 **/
                    msp.setSpan(new ForegroundColorSpan(Integer.parseInt(type)), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 4:
                    /** 设置字体背景色 **/
                    msp.setSpan(new BackgroundColorSpan(Integer.parseInt(type)), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 5:
                    /** 设置下划线 **/
                    msp.setSpan(new UnderlineSpan(), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 6:
                    /** 设置删除线 **/
                    msp.setSpan(new StrikethroughSpan(), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 7:
                    /** 设置上标 **/
                    msp.setSpan(new SubscriptSpan(), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 8:
                    /** 设置下标 **/
                    msp.setSpan(new SuperscriptSpan(), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 9:
                    /** 设置字体样式粗体 **/
                    msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 10:
                    /** 设置字体样式斜体 **/
                    msp.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 11:
                    /** 设置字体样式粗斜体 **/
                    msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
            }
            msp_content.append(msp);
        }
        return msp_content.toString();
    }

    /**
     * T9输入匹配上色
     *
     * @param formatString ：
     * @param input        ：用户输入的数字
     * @param pyToNumber   ：:数字
     * @param index        :搜索索引
     * @param py           :全拼
     * @return
     * @version: 2012-5-16 下午03:17:36
     */
    public static Spanned formatHtml(String formatString, String py, String input, String pyToNumber, int index) {
        Spanned spanned = null;
        try {
            switch (index) {
                case 0:
                case 1:
                    int i = pyToNumber.indexOf(input);
                    String newString = formatString.substring(i, i + input.length());
                    char[] chars = newString.toCharArray();
                    int n = 0, c = 0;
                    StringBuffer sbf = new StringBuffer();
                    char[] pyChar = py.toCharArray();
                    for (; n < py.length(); n++) {
                        if (c < chars.length && pyChar[n] == chars[c]) {
                            c++;
                            sbf.append("<font color=#FF6600>" + pyChar[n] + "</font>");
                        } else {
                            sbf.append(pyChar[n]);
                        }
                    }
                    spanned = Html.fromHtml(sbf.toString());
                    break;
                case 2:
                    StringBuffer regxString = new StringBuffer();
                    try {
                        for (int j = 0; j < input.length(); j++) {
                            regxString.append("[" + VsSearchSql.array[input.charAt(j) - '2'] + "]");
                        }
                    } catch (Exception e) {
                    }
                    formatString = formatString.replaceFirst("(" + regxString + ")", "<font color=#FF6600>$1</font>");
                    spanned = Html.fromHtml(formatString);
                    break;

                case 3:
                    spanned = Html.fromHtml(formatString.replaceFirst("(" + input + ")", "<font color=#FF6600>$1</font>"));
                    break;
                case 4:
                    spanned = Html.fromHtml(formatString.toLowerCase().replaceFirst("(" + input + ")", "<font color=#FF6600>$1</font>"));
                    break;
                case 5:
                    spanned = Html.fromHtml(formatString.replaceFirst("(" + input + ")", "<font color=#FF6600>$1</font>"));
                    break;
                case 6:
                case 7:
                    int j = pyToNumber.indexOf(input.toUpperCase());
                    String newString2 = formatString.substring(j, j + input.length());
                    char[] chars2 = newString2.toCharArray();
                    int n2 = 0, c2 = 0;
                    StringBuffer sbf2 = new StringBuffer();
                    char[] pyChar2 = py.toCharArray();
                    for (; n2 < py.length(); n2++) {
                        if (c2 < chars2.length && pyChar2[n2] == chars2[c2]) {
                            c2++;
                            sbf2.append("<font color=#FF6600>" + pyChar2[n2] + "</font>");
                        } else {
                            sbf2.append(pyChar2[n2]);
                        }
                    }
                    spanned = Html.fromHtml(sbf2.toString());
                    break;
            }

        } catch (Exception e) {
        }
        return spanned;
    }

    /**
     * 添加单个联系人
     *
     * @param mContext
     * @param phone
     */
    public static void addContact(Context mContext, String phone) {
        if (!isNumeric(phone)) {
            phone = "";
        }
        Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
        addContactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
        addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phone);
        try {
            mContext.startActivity(addContactIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判读那字符窜是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 添加单个联系人
     *
     * @param mContext
     * @param phone
     */
    public static void addContact(Context mContext, String phone, String name) {
        Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
        addContactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
        addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phone);
        addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, name);
        try {
            mContext.startActivity(addContactIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台添加联系人
     *
     * @param mContext
     * @param name
     * @param phones
     */
    public static void addContactBackground(final Context mContext, final String name, final String[] phones) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!VsContactManager.isContextExist(mContext, phones, name)) {
                    ContentResolver resolver = mContext.getContentResolver();
                    VsBatchOperation batchOperation = new VsBatchOperation(mContext, resolver);
                    VsContactOperations vsContactOperations = VsContactOperations.createNewContact(mContext, null, batchOperation);
                    vsContactOperations.addName(name, name, null, null, null, null, null, null, null);
                    vsContactOperations.addNickName(name);
                    Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
                    vsContactOperations.addAvatar(getBitmapBytes(bm, false));

                    for (String i : phones) {
                        vsContactOperations.addPhone(i, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    }
                    batchOperation.execute();
                }
            }
        };

        GlobalVariables.fixedThreadPool.execute(runnable);
    }

    /**
     * 删除全部联系人
     *
     * @param context
     * @param handler
     * @param contactList
     * @return
     */
    public static boolean deleteContactAll(Context context, Handler handler, ArrayList<VsContactItem> contactList) {
        int failDeleteCount = 0;
        int size = contactList.size();
        int mOperationCount = 100;
        for (int i = 0; i < size; ) {
            ArrayList<ContentProviderOperation> arrayList = new ArrayList<ContentProviderOperation>(mOperationCount);
            for (int j = 0; j < mOperationCount; j++) {
                String contactid = null;
                if (i < size && (contactid = contactList.get(i).mContactId) != null && contactid.length() > 0) {
                    // KcCommonContactHistory.deleteContact(contactid, null,
                    // context);// 在常用联系人中查找并删除
                    ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI);
                    builder.withSelection(ContactsContract.RawContacts.CONTACT_ID + " = ?", new String[]{contactid});
                    i++;
                    arrayList.add(builder.build());
                } else {
                    break;
                }
            }
            try {
                if (arrayList.size() > 0) {
                    ContentProviderResult[] results1 = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, arrayList);
                    if (results1 != null && results1.length != 0) {
                        for (int j = 0; j < results1.length; j++) {
                            if (null == results1[j] || 0 == results1[j].count) {
                                failDeleteCount++;
                            }
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
        if (failDeleteCount == 0) {
            handler.sendEmptyMessage(400);
            return true;
        } else {
            handler.sendEmptyMessage(300);
            return false;
        }
    }

    /**
     * 删除单个联系人
     *
     * @param context
     * @param handler
     * @param contactid
     * @return
     */
    public static boolean deleteContact(Context context, String contactid) {
        if (contactid != null && contactid.length() > 0) {
            CustomLog.i("beifen", "contactid==== " + contactid);
            ContentResolver contentResolver = context.getContentResolver();
            int row = contentResolver.delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.CONTACT_ID + " = ?", new String[]{contactid});
            if (row > 0) {
                return true;
            }
        }
        return false;
    }

    public static void addCurrentContact(Context mContext, String phone) {
        Intent addContactIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        addContactIntent.setType("vnd.android.cursor.item/person");
        addContactIntent.setType("vnd.android.cursor.item/contact");
        addContactIntent.setType("vnd.android.cursor.item/raw_contact");
        addContactIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phone);
        try {
            mContext.startActivity(addContactIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否为合法的固定电话
     *
     * @param phoneNum
     * @return
     */
    public static String isFixedPhone(String num) {
        String oldString = num.replace("-", "");
        oldString = oldString.replace("+", "");

        if (oldString.matches("^86.*")) {
            oldString = oldString.substring("86".length());
        }
        if (oldString.matches("^12593.*|17951.*|17909.*|17911.*")) {
            oldString = oldString.substring("12593".length());
        }

        if (oldString.matches("^(0){1}[0-9]*$")) {
            if (oldString.matches("[0-9]{8,12}")) { // 固定电话
                CustomLog.v(TAG, "isFixedPhone5");
                return "is_phone_number";
            } else { // 非固定电话
                if (VsLocalNameFinder.isITT(oldString)) {
                    return "is_phone_number";
                } else {
                    CustomLog.v(TAG, "isFixedPhone6");
                    return "invalid_phone_number";
                }
            }
        } else { // 其它号码(手机号(合法／非法)
            CustomLog.v(TAG, "isFixedPhone7");
            if (oldString.length() >= 9) {
                return "is_mobile_phone_number";
            } else {
                return "invalid_phone_number";
            }
        }
    }

    /**
     * 设置颜色透明度
     *
     * @param num 透明的倍数
     * @return
     */
    public static int setTransparency(int num) {
        return Color.argb(2 * 255 / num, 89, 89, 89);
    }

    /**
     * 联系人详情有多个联系人点击拨打和邀请的选择弹框
     *
     * @param mcontxt
     * @param title_name
     * @param click_listener
     * @param phoneList
     * @return
     */
    public static CustomAlertDialog showChoose(Context mcontxt, String title_name, ArrayList<View.OnClickListener> click_listener, ArrayList<String> phoneList) {
        ArrayList<String> content_name = new ArrayList<String>();
        // else if (temp == 3) {
        for (int i = 0; i < phoneList.size(); i++) {
            content_name.add(phoneList.get(i));
            /*
             * author :黄文武 修改时间:14/10/11
             */
            if (i == phoneList.size() - 1) {
                content_name.add("取消");
            }
        }
        // }
        final CustomAlertDialog dialog = new CustomAlertDialog(mcontxt, content_name, click_listener);
        dialog.setTitle(title_name);
        // if (temp == 3) {
        // dialog.setTitltVisibility(false);
        // }
        dialog.show();
        return dialog;
    }

    /**
     * 通话记录/联系人 长按处理
     *
     * @param number
     * @param Name
     * @param temp   1 为通话记录 2为联系人 3为选择签名
     */
    public static CustomAlertDialog showChoose(Context mcontxt, String title_name, int temp, ArrayList<View.OnClickListener> click_listener) {
        ArrayList<String> content_name = new ArrayList<String>();
        if (temp == 1) {
            content_name.add("删除该通话记录");
            // content_name.add("打电话");
            content_name.add("查记录");
            /*
             * author :黄文武 修改时间:14/09/29
             */
            content_name.add("取消");
        } else if (temp == 2) {
            content_name.add("查看");
            content_name.add("编辑");
            content_name.add("删除该联系人");
            /*
             * author :黄文武 修改时间:14/09/29
             */
            content_name.add("取消");
        } else if (temp == 3) {
            content_name.add("编辑");
            content_name.add("删除");
            content_name.add("添加联系人");
        } else if (temp == 4) {
            content_name.add("查看");
            content_name.add("编辑");
            content_name.add("删除该联系人");
            content_name.add("移出群组");
        }
        if (temp == 5) {
            content_name.add("删除记录");
            // content_name.add("打电话");
            // content_name.add("查记录");
            /*
             * author :黄文武 修改时间:14/09/29
             */
            content_name.add("取消");
        }
        if (temp == 6) {
            content_name.add("清空全部");
            // content_name.add("打电话");
            // content_name.add("查记录");
            /*
             * author :黄文武 修改时间:14/09/29
             */
            content_name.add("取消");
        }
        if (temp == 7) {
            content_name.add("拍照");
            content_name.add("从相册选取");
            // content_name.add("查记录");
            /*
             * author :黄文武 修改时间:14/09/29
             */
            content_name.add("取消");
        }
        // else if (temp == 3) {
        // content_name.add(mcontxt.getString(R.string.user_signature_1));
        // content_name.add(mcontxt.getString(R.string.user_signature_2));
        // content_name.add(mcontxt.getString(R.string.user_signature_3));
        // content_name.add(mcontxt.getString(R.string.user_signature_4));
        // content_name.add(mcontxt.getString(R.string.user_signature_5));
        // content_name.add(mcontxt.getString(R.string.user_signature_6));
        // }
        final CustomAlertDialog dialog = new CustomAlertDialog(mcontxt, content_name, click_listener);
        dialog.setTitle(title_name);
        // if (temp == 3) {
        // dialog.setTitltVisibility(false);
        // }
        dialog.show();
        return dialog;
    }

    /**
     * 判断手机号码是否符合规范
     *
     * @param s
     * @return
     */
    public static boolean isContinuityCharacter(String s) {
        boolean bol = false;
        char[] data = s.toCharArray();
        String regEx = "1\\d{10}$";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(s);
        bol = mat.find();
        if (bol) {
            int sameNum = 0;
            int conNum = 0;
            for (int i = 0; i < data.length - 1; i++) {
                int a = Integer.parseInt(data[i] + "");
                int b = Integer.parseInt(data[i + 1] + "");
                if ((a + 1 == b || a - 1 == b)) {
                    conNum++;
                } else {
                    conNum = 0;
                }
                if (a == b) {
                    sameNum++;
                } else {
                    sameNum = 0;
                }
                if (conNum >= 5) {
                    bol = false;
                }
                if (sameNum >= 5) {
                    bol = false;
                }
            }
        }
        return bol;
    }

    /**
     * 校验手机号码
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkPhone(String phoneNum) {
        // 匹配规则
        if (phoneNum.startsWith("18") || phoneNum.startsWith("15") || phoneNum.startsWith("13") || phoneNum.startsWith("14") || phoneNum.startsWith("16") || phoneNum.startsWith("17") || phoneNum.startsWith("19")) {
            // mPhoneNum = phoneNum;
            return phoneNum.length() == 11;
        } else if (phoneNum.startsWith("0") && phoneNum.length() >= 4) {
            // 国际电话
            if (phoneNum.charAt(1) == phoneNum.charAt(0)) {
                // mPhoneNum = phoneNum;
                return true;
            } else {
                // mPhoneNum = phoneNum;
                return (phoneNum.length() >= 10 && phoneNum.length() <= 12);
            }
        } else if (phoneNum.startsWith("+86")) {
            return checkPhone(phoneNum.substring(3));
        } else if (phoneNum.startsWith("+860")) {
            return checkPhone(phoneNum.substring(4));
        } else if (phoneNum.startsWith("86")) {
            if (phoneNum.length() >= 13) {
                return checkPhone(phoneNum.substring(2));
            }
        } else if (phoneNum.startsWith("0086")) {
            return checkPhone(phoneNum.substring(4));
        } else if (phoneNum.startsWith("+0086")) {
            return checkPhone(phoneNum.substring(5));
        }
        return false;
    }

    /**
     * 校验手机号码
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkMobilePhone(String phoneNum) {
        // 匹配规则
        if (phoneNum.startsWith("18") || phoneNum.startsWith("15") || phoneNum.startsWith("13") || phoneNum.startsWith("14") || phoneNum.startsWith("17")) {
            // mPhoneNum = phoneNum;
            return phoneNum.length() == 11;
        } else if (phoneNum.startsWith("0") && phoneNum.length() >= 4) {
            // 国际电话
            if (phoneNum.charAt(1) == phoneNum.charAt(0)) {
                // mPhoneNum = phoneNum;
                return true;
            } else {
                // mPhoneNum = phoneNum;
                return false;
            }
        } else if (phoneNum.startsWith("+86")) {
            return checkPhone(phoneNum.substring(3));
        } else if (phoneNum.startsWith("+860")) {
            return checkPhone(phoneNum.substring(4));
        } else if (phoneNum.startsWith("86")) {
            if (phoneNum.length() >= 13) {
                return checkPhone(phoneNum.substring(2));
            }
        }
        return false;
    }

    /**
     * 获取电话号码
     *
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phonenumber = tm.getLine1Number();
        if (phonenumber == null) {
            return "";
        }
        if (phonenumber.startsWith("+86")) {
            phonenumber = phonenumber.substring(3);
        } else if (phonenumber.startsWith("86")) {
            phonenumber = phonenumber.substring(2);
        }
        return phonenumber;
    }

    /**
     * 检测是否有账号
     */
    public static boolean checkHasAccount(Context context) {
        // TODO 检测是否有账号
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        String id = settings.getString(VsUserConfig.JKey_KcId, null);
        String pwd = settings.getString(VsUserConfig.JKey_Password, null);
        if (id != null && pwd != null && id.length() > 2 && pwd.length() > 2) {
            return true;
        }
        return false;
        //13380391613   123456
    }

    /**
     * 检查是否绑定手机
     */
    public static boolean checkHasBindPhone(Context context) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        String id = settings.getString(VsUserConfig.JKey_KcId, null);
        String pwd = settings.getString(VsUserConfig.JKey_Password, null);
        String phone = settings.getString(VsUserConfig.JKey_PhoneNumber, null);
        // String pbPhone = settings.getString(KcUserConfig.JKey_PBPhoneNumber,
        // null);
        if (id != null && pwd != null && id.length() > 2 && pwd.length() > 2 && phone != null && !"".equals(phone)) {
            return true;
        }
        return false;
    }

    /**
     * 得到手机的MAC地址或者imei号。都取不到就用sid
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String deviceid = VsUserConfig.getDataString(context, VsUserConfig.JKey_tcpsid);
        if (deviceid.length() > 1) {
            // return (deviceid.substring(0, deviceid.length() - 1) + 48);
            return deviceid;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info.getMacAddress() != null && info.getMacAddress().length() > 0) {
            VsUserConfig.setData(context, VsUserConfig.JKey_tcpsid, info.getMacAddress());
        } else {
            String imei = getIMSI(context);
            if (imei.length() > 1) {
                VsUserConfig.setData(context, VsUserConfig.JKey_tcpsid, getIMSI(context));
            } else {
                VsUserConfig.setData(context, VsUserConfig.JKey_tcpsid, generateSID(context));
            }
        }
        // CustomLog.i("kcdebug", KcUserConfig.getDataString(context,
        return VsUserConfig.getDataString(context, VsUserConfig.JKey_tcpsid);
        // 修改mac地址测试
        // return (KcUserConfig.getDataString(context,
        // KcUserConfig.JKey_tcpsid).substring(0,
        // KcUserConfig.getDataString(context,
        // KcUserConfig.JKey_tcpsid).length() - 1) + 48);
    }

    /**
     * 获取 IMSI
     *
     * @param context
     * @return String
     */
    public static String getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        return imsi == null ? "" : imsi;
    }

    /**
     * 随机生成sid
     */
    public static String generateSID(Context context) {
        String sId = VsUserConfig.getDataString(context, VsUserConfig.JKey_MO_SID);
        if (sId.length() > 3) {
            return sId;
        }
        long tempLong = Math.round(Math.random() * 9000 + 1000);
        sId = DfineAction.AUTO_REG_MARK + System.currentTimeMillis() / 1000 + "" + tempLong;
        VsUserConfig.setData(context, VsUserConfig.JKey_MO_SID, sId);
        return sId;
    }

    /**
     * 格式化电话号码
     *
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            StringBuffer strBuffer = new StringBuffer(phoneNumber);
            strBuffer.insert(3, "-");
            strBuffer.insert(8, "-");
            return strBuffer.toString();
        } else {
            return phoneNumber;
        }
    }

    /**
     * 电话号码字符判断
     */

    public static boolean isNull(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 解决布局中ScrollView与ListView的冲突
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 编辑联系人
     *
     * @param mContext
     * @param id
     */
    public static void updateContact(Context mContext, String id) {
        if (id == null || "".equals(id)) {
            CustomLog.i("Error", "updateContact id is null");
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("content://com.android.contacts/contacts/" + id));
            mContext.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            return;
        }
    }

    /**
     * 任意字符串 半角字符串。全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
            } else if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 转换时间格式
     *
     * @param timestamp
     * @return
     */
    public static String kc_times_conversion_forcallog(Long oldtime) {
        CustomLog.i(TAG, "timestamp=" + oldtime + "=");
        String showTime = "";
        Calendar cal = Calendar.getInstance();
        String Tday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String Yday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        String OldDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date(oldtime));

        if (OldDay.equals(Tday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            showTime = "今天 " + sdf.format(new Date(oldtime));
        } else if (OldDay.equals(Yday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            showTime = "昨天 " + sdf.format(new Date(oldtime));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            showTime = sdf.format(new Date(oldtime));
            if ('0' == showTime.charAt(3)) {
                showTime = showTime.substring(0, 3) + showTime.substring(4);
            }
            if ('0' == showTime.charAt(0)) {
                showTime = showTime.substring(1);
            }
        }
        return showTime;
    }

    /**
     * 转换时间格式
     *
     * @param timestamp
     * @return
     */
    public static String kc_times_conversion_forcallog_too(Long oldtime) {
        CustomLog.i(TAG, "timestamp=" + oldtime + "=");
        String showTime = "";
        Calendar cal = Calendar.getInstance();
        String Tday = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String Yday = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        String OldDay = new SimpleDateFormat("yyyy/MM/dd").format(new Date(oldtime));
        String Yyear = new SimpleDateFormat("yyyy").format(cal.getTime());
        String OldYear = new SimpleDateFormat("yyyy").format(new Date(oldtime));
        if (OldDay.equals(Tday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            showTime = sdf.format(new Date(oldtime));
        } else if (OldDay.equals(Yday)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            showTime = "昨天" + sdf.format(new Date(oldtime));
        } else if (OldYear.equals(Yyear)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            showTime = sdf.format(new Date(oldtime));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            showTime = sdf.format(new Date(oldtime));
            if ('0' == showTime.charAt(3)) {
                showTime = showTime.substring(0, 3) + showTime.substring(4);
            }
            if ('0' == showTime.charAt(0)) {
                showTime = showTime.substring(1);
            }
        }
        return showTime;
    }

    public static String kc_times_conversion_forcallog(String year, String hour) {
        CustomLog.i(TAG, "timestamp=" + year + "= hour===" + hour);
        String showTime = "";
        Calendar cal = Calendar.getInstance();
        String Tday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String Yday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String Qday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        if (year.equals(Tday)) {
            showTime = hour;
        } else if (year.equals(Yday)) {
            showTime = "昨天 " + hour;
        } else if (year.equals(Qday)) {
            showTime = "前天 " + hour;
        } else {
            showTime = year + " " + hour;
            // if ('0' == showTime.charAt(3)) {
            // showTime = showTime.substring(0, 3) + showTime.substring(4);
            // }
            // if ('0' == showTime.charAt(0)) {
            // showTime = showTime.substring(1);
            // }
        }
        return showTime;
    }

    /**
     * @param year
     * @param hour
     * @return
     */
    public static String getTimeNotice(String year, String hour) {
        CustomLog.i(TAG, "timestamp=" + year + "= hour===" + hour);
        String showTime = "";
        Calendar cal = Calendar.getInstance();
        String Tday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String Yday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        if (year.equals(Tday)) {
            showTime = "今天  " + hour;
        } else if (year.equals(Yday)) {
            showTime = "昨天  " + hour;
        } else {
            showTime = year.substring(5, 7) + " 月 " + year.substring(8, 10) + " " + hour;
            // if ('0' == showTime.charAt(3)) {
            // showTime = showTime.substring(0, 3) + showTime.substring(4);
            // }
            // if ('0' == showTime.charAt(0)) {
            // showTime = showTime.substring(1);
            // }
        }
        return showTime;
    }

    /**
     * 对话框
     *
     * @param title
     * @param msg
     * @param positive
     * @param negative
     * @param OkBtnListener
     * @param context
     */
    public static void showCustomDialog(String title, String msg, String positive, String negative, DialogInterface.OnClickListener OkBtnListener, Context context) {
        new CustomDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(positive, OkBtnListener).setNegativeButton(negative, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 获取通话记录时间
     *
     * @param calltimestamp
     * @return
     */
    public static String getCallLogTime(long calltimestamp) {
        // 获取当前时间数据
        Calendar calendarNow = Calendar.getInstance();
        int year_now = calendarNow.get(Calendar.YEAR);
        int month_now = calendarNow.get(Calendar.MONTH) + 1;
        int day_now = calendarNow.get(Calendar.DAY_OF_MONTH);
        // 获取拨打时间
        String result = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calltimestamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // System.out.println(calendar.get(Calendar.YEAR) + "年" +
        // (Integer.valueOf(calendar.get(Calendar.MONTH)) + 1)
        // + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" +
        // calendar.get(Calendar.HOUR_OF_DAY) + "时"
        // + calendar.get(Calendar.MINUTE) + "分");
        if ((year_now == year) && (month_now == month)) {
            if (day_now == day) {
                result = "今天" + " " + getFormateTimeNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + getFormateTimeNumber(calendar.get(Calendar.MINUTE));
            } else if (day_now - day == 1) {
                result = "昨天" + " " + getFormateTimeNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + getFormateTimeNumber(calendar.get(Calendar.MINUTE));
            } else {
                result = getFormateTimeNumber(month) + "-" + getFormateTimeNumber(day);
                ;
            }
        } else if ((year_now == year) && (month_now - month == 1)) {// 相隔一个月
            if ((calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + day_now) - day == 1) {
                result = "昨天" + " " + getFormateTimeNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + getFormateTimeNumber(calendar.get(Calendar.MINUTE));
            } else {
                result = getFormateTimeNumber(month) + "-" + getFormateTimeNumber(day);
            }
        } else {
            result = getFormateTimeNumber(month) + "-" + getFormateTimeNumber(day);
        }

        return result;
    }

    /**
     * author:黄文武 修改时间:2014/10/15 获取通话记录的 通话时间
     *
     * @param duration 通话时间(calltimelength)
     * @return
     */
    public static String getFormatDuration(long duration) {
        if (duration == -1) {
            return "未接通";
        }
        long min = (duration % (60 * 60)) / (60);
        long second = (duration % (60));
        return min + "分" + second + "秒";
    }

    /**
     * 比较当前时间与指定时间的差
     *
     * @param date
     * @return
     */
    public static int compareDate(Context context, String vaild_date) {
        // 获取当前时间数据
        Calendar calendarNow = Calendar.getInstance();
        int year_now = calendarNow.get(Calendar.YEAR);
        int month_now = calendarNow.get(Calendar.MONTH) + 1;
        int day_now = calendarNow.get(Calendar.DAY_OF_MONTH);
        int hour_now = calendarNow.get(Calendar.HOUR_OF_DAY);
        String date_now = year_now + "-" + month_now + "-" + day_now;
        String save_date = VsUserConfig.getDataString(context, VsUserConfig.JKEY_DATE_NOW);
        if (save_date != null && !"".equals(save_date) && !date_now.equals(save_date)) {// 判断当天是否已经提示过了
            VsUserConfig.setData(context, VsUserConfig.JKEY_DATE_NOW, date_now);
            // 判断当前时间是否小于18点
            if (hour_now < 18) {
                // 获取当月的天数
                calendarNow.set(Calendar.DATE, 1);// 把日期设置为当月第一天
                calendarNow.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
                int maxDate = calendarNow.get(Calendar.DATE);
                // 获取有效期时间
                String date = vaild_date.split(" ")[0];
                String[] date_array = null;
                if (date != null && !"".equals(date)) {
                    date_array = date.split("-");
                }
                int vaild_year = Integer.valueOf(date_array[0]);
                int vaild_month = Integer.valueOf(date_array[1]);
                int vaild_day = Integer.valueOf(date_array[2]);
                if (year_now == vaild_year) {
                    if (month_now == vaild_month) {
                        return vaild_day - day_now;
                    } else if (vaild_month - month_now == 1) {
                        return (vaild_day + maxDate) - day_now;
                    }
                } else if (vaild_year - year_now == 1) {
                    if ((vaild_month + 12) - month_now == 1) {
                        return (vaild_day + maxDate) - day_now;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 验证短信的有效期
     *
     * @param calltimestamp
     * @param data          当前多久之前时间
     * @return
     */
    public static boolean checkSMSTime(long calltimestamp, int data) {
        // 获取当前时间数据
        Calendar calendarNow = Calendar.getInstance();
        int year_now = calendarNow.get(Calendar.YEAR);
        int month_now = calendarNow.get(Calendar.MONTH) + 1;
        int day_now = calendarNow.get(Calendar.DAY_OF_MONTH);
        int hour_now = calendarNow.get(Calendar.HOUR_OF_DAY);
        int mint_now = calendarNow.get(Calendar.MINUTE);
        // 获取短信收发时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calltimestamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mint = calendar.get(Calendar.MINUTE);
        if (year_now == year && month_now == month && day_now == day) {// 判断年月日是否相等
            if (hour_now == hour) {
                if (mint_now - mint < data) {
                    return true;
                }
            } else if (hour_now - hour == 1) {
                if ((mint_now + 60) - mint < data) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 格式化时间
     *
     * @param number
     * @return
     */
    public static String getFormateTimeNumber(int number) {
        String result = "";
        if (number < 10) {
            result = "0" + number;
        } else {
            result = number + "";
        }
        return result;
    }

    public static String getValidPhoneNumber(Context mContext, String tempStr) {
        CustomLog.i(TAG, "Entering VsUtil.getValidPhoneNumber(Context context,String tempStr)...");
        if (validate(tempStr)) {
            return tempStr;
        }

        return null;
    }

    private static String REG_EXP = "[0-9]*";

    /**
     * To check if the name illegal.
     *
     * @param s input string.
     * @return if the name illegal.
     */
    private static boolean validate(String ss) {
        if ("".equals(ss)) {
            return false;
        }
        if (ss.length() > 24) {
            return false;
        }
        return (ss.matches(REG_EXP));
    }

    /**
     * 返回字符串
     *
     * @param getObj
     * @param item
     * @return
     */
    public static String GetStringFromJSON(JSONObject getObj, String item) {
        String getStr = "";
        try {
            if (item != null) {
                getStr = String.valueOf(getObj.get(item));
                if (getStr.endsWith("null")) {
                    getStr = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getStr;
    }

    /**
     * 返回数字
     *
     * @param getObj
     * @param item
     * @return
     */
    public static int GetIntegerFromJSON(JSONObject getObj, String item) {
        int getInt = -1000;
        try {
            getInt = Integer.valueOf(String.valueOf(getObj.get(item)).trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInt;
    }

    /**
     * 判断当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isCurrentNetworkAvailable(Context context) {
        if (VsUserConfig.getDataInt(context, VsUserConfig.JKEY_TestAccessPointState) != 0) {
            Intent intent = new Intent(context, CustomDialogActivity.class);
            intent.putExtra("business", "NetworkError");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    /**
     * 判断是否安装了apk包
     */
    public static boolean isMobile_APKExist(Context mContext, String packagename) {
        PackageManager manager = mContext.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        int size = pkgList.size();
        for (int i = 0; i < size; i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase(packagename)) return true;
        }

        return false;
    }

    //
    // /**
    // * 设置图片位置
    // *
    // * @param left
    // * @param top
    // * @param right
    // * @param bottom
    // * @param imageView
    // */
    // public static void setImageMargins(int right, int buttom, final ImageView
    // imageView, Drawable drawable,
    // Activity activity) {
    // DisplayMetrics dm = new DisplayMetrics();// add by zzw
    // activity.getWindowManager().getDefaultDisplay().getMetrics(dm);// add by
    // zzw
    // int width = dm.widthPixels; // 像素：宽
    // int height = dm.heightPixels; // 像素：高
    // int dy = (int) dm.density;
    // int left = (int) (width - width * right / 640 -
    // drawable.getMinimumWidth());
    // int top = (int) (height - height * buttom / 1136 -
    // drawable.getMinimumHeight());
    // CustomLog.i("GDK", drawable.getMinimumWidth() + "_" +
    // drawable.getMinimumHeight());
    // RelativeLayout.LayoutParams params = new
    // RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
    // LayoutParams.WRAP_CONTENT);
    // params.setMargins(left, /* height - 280 * dy / 2 -
    // drawable.getMinimumHeight() */top, 0, 0);
    // imageView.setLayoutParams(params);
    //
    // }

    /**
     * 创建快捷方式
     *
     * @param context
     * @param reqParam
     */
    public static void CreateDeskShortCut(Context context, String reqParam, int iconid) {

        // 创建快捷方式的Intent
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, reqParam);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconid);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        // 点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播。OK
        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 支付信息
     *
     * @param context
     * @return
     */
    public static String getPayInfo(Context context) {
        CustomLog.i(TAG, "Entering VsUtil.getFavourableInfo(Context context)...");

        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(Resource.PREFS_NAME_PAY_INFO, 0);
        return settings.getString("content", "");
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            CustomLog.i("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 启动到对应包名的应用详情界面
     *
     * @param context
     * @param packageName
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        String SCHEME = "package:";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
         */
        String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
        /**
         * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
         */
        String APP_PKG_NAME_22 = "pkg";
        /**
         * InstalledAppDetails所在包名
         */
        String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
        /**
         * InstalledAppDetails类名
         */
        String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            Uri packageURI = Uri.parse(SCHEME + packageName);
            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            context.startActivity(intent1);
        } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
            context.startActivity(intent);
        }
    }

    /**
     * 是否是直拨
     *
     * @return
     */
    public static boolean isDialCall(Context mContext) {
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_USERDIALVALUE, "2").equals("1") || VsUtil.isWifi(mContext) && VsUserConfig.getDataString(mContext,
                VsUserConfig.JKey_USERDIALVALUE, "2").equals("2")) {
            return true;
        }
        return false;
    }

    /**
     * 是否是回拨
     *
     * @return
     */
    public static boolean isCallBack(Context mContext) {
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_USERDIALVALUE, "2").equals("3") || !VsUtil.isWifi(mContext)) {
            return true;
        }
        return false;
    }

    /**
     * 遍历程序列表，判断是否有极致包
     *
     * @return
     */
    public static boolean isMobile_spExist(Context mContext, String pkg) {
        PackageManager manager = mContext.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase(pkg)) return true;
        }

        return false;
    }

    /**
     * 缩放图片 方案3
     *
     * @return
     */
    public static Bitmap zoomImg1(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        Bitmap newbm = null;
        // int startx = 0;//剪切的起始點
        // int starty = 0;
        Matrix matrix = new Matrix();
        try {
            if (width > newWidth && height > newHeight) {
                // 计算缩放比例
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                // 取得想要缩放的matrix参数
                matrix.postScale(scaleWidth, scaleHeight);
                // 得到新的图片
                newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            } else if (width > newWidth && height <= newHeight) {
                float scaleHeight = ((float) newHeight) / height;
                // 取得想要缩放的matrix参数
                matrix.postScale(1.0F, scaleHeight);
                // 得到新的图片
                newbm = Bitmap.createBitmap(bm, 0, 0, newWidth, height, matrix, true);
            } else if (width <= newWidth && height > newHeight) {
                float scaleWidth = ((float) newWidth) / width;
                // 取得想要缩放的matrix参数
                matrix.postScale(scaleWidth, 1.0F);
                // 得到新的图片
                newbm = Bitmap.createBitmap(bm, 0, 0, width, newHeight, matrix, true);
            } else {
                // 计算缩放比例
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                // 取得想要缩放的matrix参数
                matrix.postScale(scaleWidth, scaleHeight);
                // 得到新的图片
                CustomLog.i(TAG, "压缩图片 图片宽==" + width + "图片高===" + height + "宽===" + newWidth + "高===" + newHeight);
                newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbm;
    }

    // set backgroud img
    public static Bitmap zoomImg(Bitmap bitmap, int cavas_width, int cavas_height) {
        int cut_width;
        int cut_height;
        Matrix cut_matrix = new Matrix();
        int startx = 0;// 剪切的起始點
        int starty = 0;
        try {
            if (bitmap.getWidth() > bitmap.getHeight()) {// 當width>height的時候，設置縮放比例
                cut_width = (bitmap.getHeight() * cavas_width) / cavas_height;
                cut_height = bitmap.getHeight();
                float xb = ((float) cavas_width) / cut_width;
                float yb = ((float) cavas_height) / cut_height;
                cut_matrix.postScale(xb, yb);
                startx = (bitmap.getWidth() - cut_width) / 2;
                return Bitmap.createBitmap(bitmap, startx, starty, cut_width, cut_height, cut_matrix, true);
            } else if (bitmap.getWidth() < bitmap.getHeight()) {// 當width <
                // height的時候，設置縮放比例
                cut_width = bitmap.getWidth();
                cut_height = (bitmap.getWidth() * cavas_height) / cavas_width;
                float xb = ((float) cavas_width) / cut_width;
                float yb = ((float) cavas_height) / cut_height;
                cut_matrix.postScale(xb, yb);
                starty = (bitmap.getHeight() - cut_height) / 2;
                if (starty < 0) {
                    starty = (cut_height - bitmap.getHeight()) / 2;
                }
                return Bitmap.createBitmap(bitmap, 0, starty, cut_width, cut_height, cut_matrix, true);
            } else if (bitmap.getWidth() == bitmap.getHeight()) {
                return zoomImg1(bitmap, cavas_width, cavas_height);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return zoomImg1(bitmap, cavas_width, cavas_height);
        }
        return bitmap;
    }

    /**
     * 显示选择提示框
     *
     * @param titleResId
     * @param messageId
     * @param dialogId
     * @param okBtmListener
     * @param cancelBtnListener
     */
    public static void showYesNoDialog(Context mContext, String title, String message, String PositiveButton, String NegativeButton, DialogInterface.OnClickListener
            okBtmListener, DialogInterface.OnClickListener cancelBtnListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(PositiveButton, okBtmListener);
        builder.setNegativeButton(NegativeButton, cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示选择提示框
     *
     * @param titleResId
     * @param messageId
     * @param dialogId
     * @param okBtmListener
     * @param cancelBtnListener
     */
    public static void showYesNoDialogM(Context mContext, String title, String message, String PositiveButton, String NegativeButton, DialogInterface.OnClickListener
            okBtmListener, DialogInterface.OnClickListener cancelBtnListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(PositiveButton, okBtmListener);
        builder.setNegativeButton(NegativeButton, cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 进度条旋转
     *
     * @param view
     */
    public static void statAnimAciton(ImageView view) {
        view.setVisibility(View.VISIBLE);
        // 图标旋转动画
        Animation rotateAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        view.startAnimation(rotateAnim);
    }

    /**
     * 判断字符串中是否含有整数
     *
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        if (value.indexOf("2") < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 取随机数
     *
     * @param n   取随机数的个数
     * @param max 随机数的最大值
     * @return
     */
    public static int[] getRandom(int n, int max) {
        int[] randoms = new int[n];
        String randomString = "";
        for (int i = 0; i < n; i++) {
            int random = (int) (Math.random() * max);
            while (randomString.contains(String.valueOf(random))) {
                random = (int) (Math.random() * max);
            }
            randomString += String.valueOf(random);
            randoms[i] = random;
        }
        return randoms;
    }

    /**
     * Drawable 转 Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 :
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 把特定参数拼接到url后面
     *
     * @param url
     * @param mContext
     * @return
     */
    public static String getUrlParams(String url, Context mContext) {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        hashtable.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        hashtable.put("pv", DfineAction.pv);
        hashtable.put("v", BuildConfig.VERSION_NAME);
        hashtable.put("brandid", DfineAction.brandid);
        hashtable.put("pwd", VsMd5.md5(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Password)));
        if (url.indexOf("?") != -1) {
            url = url + "&" + CoreBusiness.getInstance().enmurParse(hashtable);
        } else {
            url = url + "?" + CoreBusiness.getInstance().enmurParse(hashtable);
        }

        return url;
    }

    /**
     * 是否存在号码的的通话记录
     *
     * @param phoneNum
     * @return
     */
    public static VsCallLogListItem isInMyLogList(String phoneNum) {
        for (VsCallLogListItem listItem : VsPhoneCallHistory.callLogs) {
            if (listItem.getFirst() != null && listItem.getFirst().callNumber.equals(phoneNum)) {
                return listItem;
            }
        }
        return null;
    }

    /**
     * 手机号码类型判断
     *
     * @param phoneNumer
     * @return
     */
    public static String checkMobileType(String phoneNum) {
        if (phoneNum.startsWith("+86") || phoneNum.startsWith("086")) {
            return getMobleType(phoneNum.substring(3));
        } else if (phoneNum.startsWith("+086") || phoneNum.startsWith("0086")) {
            return getMobleType(phoneNum.substring(4));
        } else if (phoneNum.startsWith("86")) {
            if (phoneNum.length() >= 13) {
                return getMobleType(phoneNum.substring(2));
            }
        } else {
            return getMobleType(phoneNum);
        }
        return null;
    }

    /**
     * 获取号码运营商类型
     *
     * @param phoneNumber
     * @return
     */
    public static String getMobleType(String phoneNumber) {
        String[] cmcc = new String[]{"134", "135", "136", "137", "138", "139", "147", "150", "151", "152", "157", "158", "159", "182", "187", "188", "183", "184"};// 移动号段
        String[] cucc = new String[]{"130", "131", "132", "155", "156", "185", "186", "154"};// 联通号段
        String[] ctcc = new String[]{"133", "153", "180", "189", "181"};// 电信号段
        for (String str : cmcc) {// 判断是否为移动号码
            if (phoneNumber.startsWith(str)) {
                return "-移动";
            }
        }
        for (String str : cucc) {// 判断是否为联通号码
            if (phoneNumber.startsWith(str)) {
                return "-联通";
            }
        }
        for (String str : ctcc) {// 判断是否为电信号码
            if (phoneNumber.startsWith(str)) {
                return "-电信";
            }
        }
        return null;
    }

    /**
     * 读取SD卡的账号密码信息
     *
     * @param mContext
     * @param filename
     * @return
     */
    public static String readFromFile(Context mContext, final String filename) {
        // 删除老的保存密码文件
        String filepath = DfineAction.mWldhFilePath + filename + ".txt";
        File file = new File(filepath);
        if (file.exists()) file.delete();

        filepath = Environment.getExternalStorageDirectory() + "//" + filename + ".txt";
        file = new File(filepath);
        if (file.exists()) file.delete();

        String ret = "";
        String key = DfineAction.passwad_key2;

        filepath = DfineAction.mWldhFilePath + filename + "_2.txt";
        file = new File(filepath);
        if (!file.exists()) {
            filepath = DfineAction.mWldhFilePath + filename + "_1.txt";
            file = new File(filepath);
            key = DfineAction.passwad_key;
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byte buffer[] = new byte[is.available()];
            is.read(buffer);
            String content = new String(buffer);
            ret = VsRc4.decry_RC4(content, key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    /**
     * 本地拨打电话。跳转到系统拨号界面
     *
     * @param mContext
     * @param callNumber
     */
    public static void LocalCallNumber(Context mContext, String callNumber) {
        CustomLog.i("GDK", callNumber);
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumber));
        mContext.startActivity(dial);
    }

    /**
     * 判断富媒体文件是否有30天，有就删除掉
     *
     * @param file
     * @return
     */
    public static void deleteFoder(final File file) {
        // 删除富媒体图片
        BaseRunable newRunable = new BaseRunable() {
            public void run() { // 线程运行主体
                if (file.exists()) { // 判断文件是否存在
                    if (file.isFile()) { // 判断是否是文件
                        file.delete(); // delete()方法 你应该知道 是删除的意思;
                    } else if (file.isDirectory()) { // 否则如果它是一个目录
                        File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                        if (files != null) {
                            for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                                if ((System.currentTimeMillis() - files[i].lastModified()) / 1000 / 60 / 60 / 24 > 29) {
                                    deleteFoder(files[i]); // 把每个文件 用这个方法进行迭代
                                }
                            }
                        }
                    }
                }
            }
        };
        // 使用线程池进行管理
        GlobalVariables.fixedThreadPool.execute(newRunable);
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        CustomLog.i(TAG, "apiKey=" + apiKey);
        return apiKey;
    }

    /**
     * @return 根据参数中的contactId, 查询在raw_contacts表中谁属于参数中的contactId
     */
    private static long queryForRawContactId(ContentResolver cr, String contactId) {
        Cursor rawContactIdCursor = null;
        long rawContactId = -1;
        try {
            rawContactIdCursor = cr.query(RawContacts.CONTENT_URI, new String[]{RawContacts._ID}, RawContacts.CONTACT_ID + "=" + contactId, null, null);
            if (rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
                // Just return the first one.
                rawContactId = rawContactIdCursor.getLong(0);
            }
        } finally {
            if (rawContactIdCursor != null) {
                rawContactIdCursor.close();
            }
        }
        return rawContactId;
    }

    /**
     * 获取联系人名称
     *
     * @param phone
     * @return
     */
    public static String getContactsName(String phone) {
        // ArrayList<KcContactItem> list = (ArrayList<KcContactItem>)
        // ((ArrayList<KcContactItem>)
        // KcPhoneCallHistory.CONTACTLIST).clone();
        if (VsPhoneCallHistory.CONTACTLIST.size() > 0) {
            for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                if (item.phoneNumList.contains(phone)) {
                    return item.mContactName;
                }
            }
        }
        return null;
    }

    /**
     * 字符输入流处理
     *
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readData(InputStream inSream) throws Exception {
        BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inSream));
        StringBuffer tStringBuffer = new StringBuffer();
        String sTempOneLine = new String("");
        while ((sTempOneLine = tBufferedReader.readLine()) != null) {
            tStringBuffer.append(sTempOneLine);
        }
        tBufferedReader.close();
        return tStringBuffer.toString();
    }

    /**
     * 根据号码获取联系人对象
     *
     * @param phone
     * @return
     */
    public static VsContactItem getContactsItem(String phone) {
        if (VsPhoneCallHistory.CONTACTLIST.size() > 0) {
            for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                if (item.phoneNumList.contains(phone)) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 动态设置Textview字体颜色(点击效果)
     *
     * @param mContext
     * @param textView
     * @param resId
     */
    public static void setTextViewColor(Context mContext, TextView textView, int resId) {
        XmlPullParser xrp = mContext.getResources().getXml(resId);
        try {
            ColorStateList csl = ColorStateList.createFromXml(mContext.getResources(), xrp);

            textView.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    /*
     * // 检测是否插入SD卡 public static boolean checkSdCard() { String status =
     * Environment.getExternalStorageState(); if
     * (status.equals(Environment.MEDIA_MOUNTED)) { File file = new
     * File(Environment
     * .getExternalStorageDirectory().getAbsolutePath()+"/AboutQX/image");
     * if(!file.exists()){ file.mkdirs(); } return true; } else { return false;
     * } }
     */
    public static boolean isExistsSDcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void getUpdate(Context mContext) {
        // 拉取升级信息
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        // treeMap.put("package_name", mContext.getPackageName());
        treeMap.put("netmode", VsNetWorkTools.getNetMode(mContext));
        CoreBusiness.getInstance().startThread(mContext, GlobalVariables.UPDATE_CONFIG, DfineAction.authType_AUTO, treeMap, GlobalVariables.actionupdate);
    }

    /**
     * 保存日志到本地sdkard
     *
     * @param sender  手机型号
     * @param content
     */
    public static void savedToSDCard(String content, String title) {
        /*
         * if (!DfineAction.issaveLog) { return; }
         */

        if (1 > 0) {
            return;
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录
            File saveFile = new File(sdCardDir, DfineAction.brandid + "_cdn_log.txt");
            try {
                FileOutputStream outStream = new FileOutputStream(saveFile, true);
                StringBuilder builder = new StringBuilder("手机型号：").append(Build.MODEL).append("\t\t").append(title + ":").append(content).append("\t\t").append("打印时间：").append
                        (getDate()).append("\r\n");

                outStream.write(new String(builder).getBytes("UTF-8"));
                outStream.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    /**
     * KB 转换为 MKB
     *
     * @param km
     * @return M
     * @version:2014年12月26日
     * @author:Jiangxuewu
     */
    // @SuppressLint("DefaultLocale")
    public static float kbToM(int km) {
        float result = 0F;

        if (km <= 0) {
            return result;
        }

        int i = km / 1024;
        int d = km % 1024;

        String s = i + "." + d;

        try {
            result = Float.valueOf(s);

            s = String.format("%.2f", result);

            result = Float.valueOf(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * @param context
     * @param px
     * @return
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (px / scale + 0.5f);
    }

    /**
     * 注销拨号鉴权广播
     */
    public static void unregisterCallNumberBC(Context context) {
        if (getOReceiver != null && GlobalVariables.TAG == 1) {
            try {
                //context.unregisterReceiver(getOReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getOReceiver = null;
        }
    }
}
