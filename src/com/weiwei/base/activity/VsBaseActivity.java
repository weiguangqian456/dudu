package com.weiwei.base.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.area.CityModel;
import com.weiwei.base.area.DistrictModel;
import com.weiwei.base.area.ProvinceModel;
import com.weiwei.base.area.TownModel;
import com.weiwei.base.area.XmlParserHandler;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsAdConfigItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.widgets.CustomDialog;
import com.weiwei.base.widgets.CustomToast;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;

import org.apache.http.NameValuePair;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @Title:Android客户端
 * @Description: Activity父类
 * @Copyright: Copyright (c) 2014
 * @author: 石云升
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsBaseActivity extends Activity {

    // private final String TAG = "KcBaseLibActivity";

    protected Activity mContext = this;
    /**
     * 内核服务
     */
    protected VsCoreService vsCoreService;
    /**
     * 共用的handle
     */
    protected Handler mBaseHandler;
    /**
     * 广播接收器
     */
    protected KcBroadcastReceiver vsBroadcastReceiver;
    /**
     * 共用的mToast
     */
    protected CustomToast mToast;
    /**
     * 标题栏和Tips提示
     */
    protected TextView mTitleTextView;
    private ImageView mRightLine, mLeftLine, mRightImage;
    protected LinearLayout mBtnNavLeft, mBtnNavRight;
    protected TextView mBtnNavLeftTv, mBtnNavRightTv;
    protected RelativeLayout small_title, mBtnNavRightRv;
    private static final char MSG_CloseTips = 1001;
    private static int SNAP_VELOCITY_X = 0;
    private static int SNAP_VELOCITY_Y = 0;

    /**
     * 弹出框
     */
    protected ProgressDialog mProgressDialog;
    protected Resources resource;
    private float mLastMotionX, mLastMotionY;
    private boolean enableLeftSliding = true;
    private static long mLastBackTime;

    /**
     * jPush参数start
     */
//    private MessageReceiver mMessageReceiver;
//    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
//    public static final String KEY_TITLE = "title";
//    public static final String KEY_MESSAGE = "message";
//    public static final String KEY_EXTRAS = "extras";
//    public static boolean isForeground = false;
    /**
     * jPush参数end
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FitStateUtils.setImmersionStateMode(this);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
        resource = getResources();
        mBaseHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                handleBaseMessage(msg);
                return false;
            }
        });
        mToast = new CustomToast(this);

        /**
         * 按home键退出软件。在进入时候我们可以做的操作
         */
        CustomLog.i("SimBackActivity", "onCreate()... savedInstanceState = " + (savedInstanceState != null));
        if (null != savedInstanceState) {
            CustomLog.i("SimBackActivity", "onCreate()... savedInstanceState.getBoolean(HomeExit) = " + (savedInstanceState.getBoolean("HomeExit")));
        }
        if (savedInstanceState != null && savedInstanceState.getBoolean("HomeExit")) {
            CustomLog.i("SimBackActivity", "come back for home , VsPhoneCallHistory.CONTACTLIST.size() = " + VsPhoneCallHistory.CONTACTLIST.size() + ", VsPhoneCallHistory.callLogs"
                    + ".size() = " + VsPhoneCallHistory.callLogs.size());
            if (VsPhoneCallHistory.CONTACTLIST.size() == 0 || VsPhoneCallHistory.callLogs.size() == 0) {
                VsPhoneCallHistory.loadCallLog();
                VsPhoneCallHistory.loadContacts();
            }
        }

//        registerMessageReceiver();  //接收jpush广播消息
    }

    /**
     * 申明一个广播
     */
    protected void initBroadcastReceiver(String action) {
        unregisterKcBroadcast();
        // 绑定广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SNAP_VELOCITY_X == 0) {
            SNAP_VELOCITY_X = GlobalVariables.width / 3;
        }
        if (SNAP_VELOCITY_Y == 0) {
            SNAP_VELOCITY_Y = (int) (GlobalVariables.density * 45 + 0.5F);
        }
    }

    /**
     * 申明一个广播
     */
    protected void initBroadcastReceiver(IntentFilter filter) {
        unregisterKcBroadcast();
        vsBroadcastReceiver = new KcBroadcastReceiver();
        registerReceiver(vsBroadcastReceiver, filter);

    }

    public void initTipsLayout(String tipsMsg) {
        if (small_title == null) {
            return;
        }
        if (TextUtils.isEmpty(tipsMsg)) {
            return;
        }
        small_title.setVisibility(View.GONE);
        mBaseHandler.sendEmptyMessageDelayed(MSG_CloseTips, 3000);
    }

    public VsApplication getKcApplication() {
        return (VsApplication) getApplicationContext();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (x - mLastMotionX);
                int deltaY = (int) Math.abs(y - mLastMotionY);
                if (deltaX >= SNAP_VELOCITY_X && enableLeftSliding && deltaY <= SNAP_VELOCITY_Y) {
                    handleBackPressed();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 处理左滑返回事件
     *
     * @version:2015年1月6日
     * @author:Jiangxuewu
     */
    protected void handleBackPressed() {
        if (System.currentTimeMillis() - mLastBackTime > 500) {
            mLastBackTime = System.currentTimeMillis();
            onBackPressed();
        }
    }

    /**
     * 设置不允许左滑 返回事件
     *
     * @version:2015年1月6日
     * @author:Jiangxuewu
     */
    protected void setDisEnableLeftSliding() {
        enableLeftSliding = false;
    }

    /**
     * 设置允许左滑返回事件
     *
     * @version:2015年1月6日
     * @author:Jiangxuewu
     */
    protected void setEnableLeftSliding() {
        enableLeftSliding = true;
    }

    /**
     * 处理Handler消息
     *
     * @param msg
     */
    protected void handleBaseMessage(Message msg) {
        switch (msg.what) {
            case MSG_CloseTips:
                try {
                    small_title.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    protected void setTitleGone() {
        small_title.setVisibility(View.GONE);
    }

    protected void initTitleNavBar() {
        mTitleTextView = (TextView) findViewById(R.id.sys_title_txt);
        mBtnNavLeft = (LinearLayout) findViewById(R.id.btn_nav_left);
        mBtnNavLeftTv = (TextView) findViewById(R.id.btn_nav_left_tv);
        mBtnNavRight = (LinearLayout) findViewById(R.id.btn_nav_right);
        mBtnNavRightTv = (TextView) findViewById(R.id.btn_nav_right_tv);
        mBtnNavRightRv = (RelativeLayout) findViewById(R.id.btn_nav_right_rl);
        mLeftLine = (ImageView) findViewById(R.id.title_line_left);
        mRightLine = (ImageView) findViewById(R.id.title_line_right);
        small_title = (RelativeLayout) findViewById(R.id.small_title);
        mRightImage = (ImageView) findViewById(R.id.title_right_image);


        // Tips
    }

    /**
     * 隐藏右边导航
     */
    protected void hideRightNavaBtn() {
        mBtnNavRightTv.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示左边导航
     */
    protected void showLeftNavaBtn(int id) {
        mBtnNavLeftTv.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mBtnNavLeftTv.setCompoundDrawables(drawable, null, null, null);
        mLeftLine.setVisibility(View.VISIBLE);
        mBtnNavLeft.setOnClickListener(leftNavBtnListener);
    }

    /**
     * 显示左边文字
     */
    protected void showLeftTxtBtn(String txt) {
        mBtnNavLeftTv.setVisibility(View.VISIBLE);
        mBtnNavLeftTv.setText(txt);
        mLeftLine.setVisibility(View.VISIBLE);
        mBtnNavLeft.setOnClickListener(leftNavBtnListener);
    }

    /**
     * 显示右边导航
     */
    protected void showRightNavaBtn(int id) {
        mBtnNavRightTv.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mBtnNavRightTv.setCompoundDrawables(drawable, null, null, null);
        mRightLine.setVisibility(View.VISIBLE);
        mBtnNavRightRv.setOnClickListener(rightNavBtnListener);
    }

    /**
     * 显示右边文字
     */
    protected void showRightTxtBtn(String txt) {
        mBtnNavRightTv.setVisibility(View.VISIBLE);
        mBtnNavRightTv.setText(txt);
        mRightLine.setVisibility(View.VISIBLE);
        mBtnNavRight.setOnClickListener(rightNavBtnListener);
    }

    /**
     * 显示右边图片
     */
    protected void showRightImage() {
        mRightImage.setVisibility(View.VISIBLE);
        mRightImage.setOnClickListener(rightNavImageListener);
    }

    protected void HandleLeftNavBtn() {
        // TODO 处理左导航按钮事件
        finish();// 默认为后退
    }

    protected void HandleRightNavBtn() {
        // TODO 处理右导航按钮事件
    }

    private View.OnClickListener leftNavBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            HandleLeftNavBtn();
        }
    };

    private View.OnClickListener rightNavImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            HandleRightNavBtn();
        }
    };

    private View.OnClickListener rightNavBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VsUtil.isFastDoubleClick()) {
                return;
            }
            HandleRightNavBtn();
        }
    };

    /**
     * 加载等待界面
     *
     * @param message
     */
    protected void loadProgressDialog(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    protected void loadProgressDialog(String message, boolean Cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(Cancelable);// 设置是否可以取消
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    /**
     * 销毁等待界面
     *
     * @param
     */
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = null;
    }

    public class KcBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleKcBroadcast(context, intent);
        }
    }

    /**
     * 处理收到的公告信息
     *
     * @param context
     * @param intent
     */
    protected void handleKcBroadcast(Context context, Intent intent) {
        unregisterKcBroadcast();
    }

    /**
     * 注销广播接收器
     */
    protected void unregisterKcBroadcast() {
        if (vsBroadcastReceiver != null) {
            unregisterReceiver(vsBroadcastReceiver);
            vsBroadcastReceiver = null;
        }
    }

    /**
     * 设置文本大小
     *
     * @param et
     */
    protected void setEditTextTextSize(final EditText et) {
        if (et.getText().length() == 0) {
            et.setTextSize(16);
        } else {
            et.setTextSize(20);
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    et.setTextSize(16);
                } else {
                    et.setTextSize(20);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterKcBroadcast();
    }

    // /**
    // * 设置Activity全屏显示
    // *
    // * @param activity
    // * Activity引用
    // * @param isFull
    // * true为全屏，false为非全屏
    // */
    // public void setFullScreen(Activity activity, boolean isFull) {
    // Window window = activity.getWindow();
    // WindowManager.LayoutParams params = window.getAttributes();
    // if (isFull) {
    // params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
    // window.setAttributes(params);
    // window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    // } else {
    // params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
    // window.setAttributes(params);
    // window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    // }
    // }

    /**
     * 隐藏Activity的系统默认标题栏
     *
     * @param activity Activity对象
     */
    public void hideTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    // /**
    // * 强制设置Actiity的显示方向为垂直方向。
    // *
    // * @param activity
    // * Activity对象
    // */
    // public void setScreenVertical(Activity activity) {
    // activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    // }

    // /**
    // * 强制设置Actiity的显示方向为横向。
    // *
    // * @param activity
    // * Activity对象
    // */
    // public void setScreenHorizontal(Activity activity) {
    // activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    // }

    /**
     * 隐藏软件输入法
     *
     * @param activity
     */
    public void hideSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 使UI适配输入法
     *
     * @param activity
     */
    public void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 跳转到某个Activity
     *
     * @param activity       本Activity
     * @param targetActivity 目标Activity的Class
     */
    public void startActivity(Activity activity, Class<? extends Activity> targetActivity) {
        startActivity(activity, new Intent(activity, targetActivity));
    }

    public void startActivity(Activity activity, Class<? extends Activity> targetActivity, String key, String value) {
        Intent intent = new Intent(activity, targetActivity);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    /**
     * 根据给定的Intent进行Activity跳转
     *
     * @param activity Activity对象
     * @param intent   要传递的Intent对象
     */
    public void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
    }

    /**
     * 带参数进行Activity跳转
     *
     * @param activity       Activity对象
     * @param targetActivity 目标Activity的Class
     * @param params         跳转所带的参数
     */
    public void startActivity(Activity activity, Class<? extends Activity> targetActivity, Map<String, Object> params) {
        if (null != params) {
            Intent intent = new Intent(activity, targetActivity);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                setValueToIntent(intent, entry.getKey(), entry.getValue());
            }
            startActivity(activity, intent);
        }
    }

    /**
     * 带参数进行Activity跳转
     *
     * @param activity
     * @param target
     * @param params
     */
    public void startActivity(Activity activity, Class<? extends Activity> target, NameValuePair... params) {
        if (null != params) {
            Intent intent = new Intent(activity, target);
            for (NameValuePair param : params) {
                setValueToIntent(intent, param.getName(), param.getValue());
            }
            startActivity(activity, intent);
        }
    }

    /**
     * 界面跳转
     *
     * @param cls
     */
    public void skipPage(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void skipPageAndFinish(Class<? extends Activity> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();
    }

    /**
     * @param cls
     * @param key
     * @param values
     */
    public void skipPage(Class<? extends Activity> cls, String[] key, String[] values) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != values) {
            for (int i = 0; i < key.length; i++) {
                intent.putExtra(key[i], values[i]);
            }
        }
        startActivity(intent);
    }

    /**
     * @param cls
     * @param key
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String key, String value) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    /**
     * @param cls
     * @param key
     * @param value
     */
    public void skipPage(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(this, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    public void skipPageAndFinish(Class<? extends Activity> cls, String key, int value) {
        Intent intent = new Intent(this, cls);
        if (null != key) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
        finish();
    }

    public void skipPageAndFinish(Class<? extends Activity> cls, String key, String value) {
        Intent intent = new Intent(this, cls);
        if (null != key && null != value) {
            intent.putExtra(key, value);
        }
        startActivity(intent);
        finish();
    }

    // /**
    // * 显示Toast消息，并保证运行在UI线程中,如果我们用的是封装好的Toast可不用这个方法
    // *
    // * @param activity
    // * @param message
    // */
    // public void toastShow(final Activity activity, final String message) {
    // activity.runOnUiThread(new Runnable() {
    // public void run() {
    // Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    // }
    // });
    // }

    /**
     * 将值设置到Intent里
     *
     * @param intent Inent对象
     * @param key    Key
     * @param val    Value
     */
    public void setValueToIntent(Intent intent, String key, Object val) {
        if (val instanceof Boolean) intent.putExtra(key, (Boolean) val);
        else if (val instanceof Boolean[]) intent.putExtra(key, (Boolean[]) val);
        else if (val instanceof String) intent.putExtra(key, (String) val);
        else if (val instanceof String[]) intent.putExtra(key, (String[]) val);
        else if (val instanceof Integer) intent.putExtra(key, (Integer) val);
        else if (val instanceof Integer[]) intent.putExtra(key, (Integer[]) val);
        else if (val instanceof Long) intent.putExtra(key, (Long) val);
        else if (val instanceof Long[]) intent.putExtra(key, (Long[]) val);
        else if (val instanceof Double) intent.putExtra(key, (Double) val);
        else if (val instanceof Double[]) intent.putExtra(key, (Double[]) val);
        else if (val instanceof Float) intent.putExtra(key, (Float) val);
        else if (val instanceof Float[]) intent.putExtra(key, (Float[]) val);
    }

    /**
     * 显示消息提示框
     *
     * @param title
     * @param message
     */
    protected void showMessageDialog(String title, String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), null);
        // builder.setNegativeButton(this.getResources().getString(R.string.cancel),
        // null);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示消息提示框
     *
     * @param titleResId
     * @param message
     */
    protected void showMessageDialog(int titleResId, String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(titleResId);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), null);
        // builder.setNegativeButton(this.getResources().getString(R.string.cancel),
        // null);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示消息提示框
     *
     * @param title
     * @param message
     */
    protected void showMessageDialog(String title, String message, boolean nocanbtn) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), null);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    protected void showMessageDialog_token(String title, String message, boolean nocanbtn) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_ISLOGOUTBUTTON, true);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_RegAwardSwitch, true);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_VipValidtime, "");
                // 清空Token
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_GETVSUSERINFO, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_TOKEN_RONGYUN_RESULT, "");
                VsUserConfig.setData(mContext, VsUserConfig.JKEY_CALLSERVER_FLAG, false);
            }
        });
        CustomDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 显示选择提示框
     *
     * @param titleResId
     * @param messageId
     * @param
     * @param okBtmListener
     * @param cancelBtnListener
     */
    protected void showYesNoDialog(int titleResId, String messageId, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(titleResId);
        builder.setMessage(messageId);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), okBtmListener);
        builder.setNegativeButton(this.getResources().getString(R.string.cancel), cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示选择提示框
     *
     * @param titleResId
     * @param messageId
     * @param
     * @param okBtmListener
     * @param cancelBtnListener
     */
    protected void showYesNoDialog(int titleResId, int messageId, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(titleResId);
        builder.setMessage(messageId);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), okBtmListener);
        builder.setNegativeButton(this.getResources().getString(R.string.cancel), cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    protected void showYesNoDialog(String title, int messageId, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(messageId);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), okBtmListener);
        builder.setNegativeButton(this.getResources().getString(R.string.cancel), cancelBtnListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    protected void showYesNoDialog(String title, String message, DialogInterface.OnClickListener okBtmListener, DialogInterface.OnClickListener cancelBtnListener,
                                   DialogInterface.OnCancelListener cancelListener) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(this.getResources().getString(R.string.ok), okBtmListener);
        builder.setNegativeButton(this.getResources().getString(R.string.cancel), cancelBtnListener);
        builder.setOnCancelListener(cancelListener);
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示选择提示
     *
     * @param title
     * @param message
     * @param PositiveButton
     * @param NegativeButton
     * @param okBtmListener
     * @param cancelBtnListener
     * @param cancelListener
     */
    protected void showYesNoDialog(String title, String message, String PositiveButton, String NegativeButton, DialogInterface.OnClickListener okBtmListener, DialogInterface
            .OnClickListener cancelBtnListener, DialogInterface.OnCancelListener cancelListener) {
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
     * 省
     */
    protected String[] mProvinceDatas;
    /**
     * 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

    /**
     * 县
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    protected Map<String, String[]> mTownDatasMap = new HashMap<String, String[]>();

    /**
     *
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * ��ǰʡ�����
     */
    protected String mCurrentProviceName;
    /**
     * ��ǰ�е����
     */
    protected String mCurrentCityName;
    /**
     * ��ǰ������
     */
    protected String mCurrentDistrictName = "";

    protected String mCurrentTownName = "";

    /**
     * ��ǰ�����������
     */
    protected String mCurrentZipCode = "";

    /**
     * 加载assest文件夹中地址的数据（解析xml文件）
     */
    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();

            if (provinceList != null && !provinceList.isEmpty()) {
                //当前省
                mCurrentProviceName = provinceList.get(0).getName();
                //当前省下所有市
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {

                    //第一个市
                    mCurrentCityName = cityList.get(0).getName();
                    //第一个市下的所有县
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();

                    if (districtList != null && !districtList.isEmpty()) {
                        //第一个县
                        mCurrentDistrictName = districtList.get(0).getName();
                        //第一个县下的所有镇
                        List<TownModel> townList = districtList.get(0).getTownList();
                        if (townList != null && !townList.isEmpty()) {
                            mCurrentTownName = townList.get(0).getName();
                            mCurrentZipCode = townList.get(0).getZipcode();
                        }
                    }
                }
            }

            //省
            mProvinceDatas = new String[provinceList.size()];

            //遍历省份
            for (int i = 0; i < provinceList.size(); i++) {
                //当前省份名
                mProvinceDatas[i] = provinceList.get(i).getName();
                //当前省份下的所有市
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];

                //遍历市
                for (int j = 0; j < cityList.size(); j++) {
                    //当前市名
                    cityNames[j] = cityList.get(j).getName();
                    //当前市下的所有县
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctName = new String[districtList.size()];

                    //遍历县
                    for (int k = 0; k < districtList.size(); k++) {
                        //当前县名
                        distrinctName[k] = districtList.get(k).getName();
                        //当前县下的所有镇
                        List<TownModel> townList = districtList.get(k).getTownList();
                        String[] townName = new String[townList.size()];

                        //遍历镇
                        for (int l = 0; l < townList.size(); l++) {
                            townName[l] = townList.get(l).getName();
                        }
                        mTownDatasMap.put(distrinctName[k], townName);
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctName);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }


    /**
     * 初始化广告位
     *
     * @param ad_conf_str
     * 广告位信息
     * @param ui_id
     * 广告位界面ID
     * @param isclose
     * 广告位关闭图标是否显示
     */
    private ArrayList<VsAdConfigItem> adlist = null;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected DisplayImageOptions options;
    private ViewPager viewPager;
    private LinearLayout vs_viewGroupimg;
    private ImageView[] tipImgs;
    private ArrayList<ImageView> imageViews; // 滑动的图片合集
    private int currentItem = 0; // 当前图片的索引号
    private ScheduledExecutorService scheduledExecutorService;
    protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    protected void initSlide(Activity view, final String ad_conf_str, final String ui_id, final boolean isclose) {
        options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.vs_fail_icon).cacheInMemory(true).cacheOnDisc(true) // 开启硬盘缓存
                .build();
        // TODO 初始化广告视图
        viewPager = (ViewPager) view.findViewById(R.id.image_slide_page);
        vs_viewGroupimg = (LinearLayout) view.findViewById(R.id.vs_viewGroupimg);
        Time time = new Time("GMT+8");
        time.setToNow();
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CLOSE_TIME + "_" + ui_id).equals(time.year + "-" + time.month + "-" + time.monthDay)) {
            view.findViewById(R.id.title).setVisibility(View.VISIBLE);
            // view.findViewById(R.id.slid_title).setVisibility(View.GONE);
        } else {
            if (isclose) {
                final ImageView close_iv = (ImageView) view.findViewById(R.id.ad_close_iv);
                close_iv.setVisibility(View.GONE);
                close_iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // findViewById(R.id.title).setVisibility(View.VISIBLE);
                        // findViewById(R.id.slid_title).setVisibility(View.GONE);
                        Time time = new Time("GMT+8");
                        time.setToNow();
                        VsUserConfig.setData(mContext, VsUserConfig.JKEY_AD_CLOSE_TIME + "_" + ui_id, time.year + "-" + time.month + "-" + time.monthDay);
                    }
                });
            }
        }

        // 取广告数据
        try {
            JSONArray ad_conf_json = new JSONArray(ad_conf_str);
            int i = 0;
            int len = ad_conf_json.length();
            JSONObject jObj = null;
            adlist = new ArrayList<VsAdConfigItem>(len);
            while (i < len && (jObj = (JSONObject) ad_conf_json.get(i)) != null) {
                VsAdConfigItem item = new VsAdConfigItem();
                item.setAdpid(jObj.getString("remark"));
                item.setAdid(jObj.getInt("ad_id"));
                item.setAdid(jObj.getInt("a.id"));
                item.setAdpid(jObj.getString("ad_place_id"));
                item.setName(jObj.getString("title"));
                item.setImg(jObj.getString("image_url"));
                item.setAdtype(jObj.getString("redirect_type"));
                item.setUrl(jObj.getString("target_url"));
//				item.setSortid(jObj.getString("redirect_type"));
                i++;
                adlist.add(item);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
            return;
        }
        imageViews = new ArrayList<ImageView>(adlist.size());
        for (int i = 0; i < adlist.size(); i++) {
            ImageView imageView = new ImageView(mContext);

            final String imgurl = adlist.get(i).getImg();
            if (imgurl != null && !imgurl.equals("")) {
                imageLoader.displayImage(imgurl, imageView, options, animateFirstListener);
            }
            imageView.setOnClickListener(new ImageOnClickListener());
            imageView.setScaleType(ScaleType.FIT_XY);
            imageViews.add(imageView);
        }
        viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        tipImgs = new ImageView[adlist.size()];
        for (int i = 0; i < tipImgs.length; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(10, 10));
            tipImgs[i] = imageView;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            vs_viewGroupimg.addView(imageView, lp);
        }

    }

	
	/* 图片监听 */

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }


    /**
     * 滑动页面点击事件监听器
     *
     * @author dell
     */
    private class ImageOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (adlist != null) {
                VsAdConfigItem item = adlist.get(currentItem);
                if (item.getAdtype().equals("wap")) {
                    Intent intent = new Intent();
                    // intent.putExtra("url", item.getUrl());
                    // intent.putExtra("title", item.getName());
                    /*
                     * //intent.setClass(mContext, KcHtmlActivity.class);
					 * startActivity(intent);
					 */
                    String[] aboutBusiness = new String[]{item.getName(), "", item.getUrl()};
                    intent.putExtra("AboutBusiness", aboutBusiness);
                    intent.setClass(mContext, WeiboShareWebViewActivity.class);
                    startActivity(intent);
                } else if (item.getAdtype().equals("in")) {
                    String url = item.getUrl();
                    CustomLog.i("inline", "url==" + url);

                    try {
                        JSONObject json = null;

                        url = URLDecoder.decode(url);
                        url = url.replace(DfineAction.scheme_head, "");// DfineAction.image_head
                        CustomLog.i("inline", "url  url==" + url);
                        if (url.startsWith("inline")) {// 本应用界面跳转。
                            json = new JSONObject(url.replace("inline?param=", ""));
                            CustomLog.i("inline", "inline==" + json.getString("page"));
                            VsUtil.startActivity(json.getString("page"), mContext, item);
                        }
                        CustomLog.i("", "");
                    } catch (Exception e) {
                        // TODO: handle exception
                        CustomLog.i("", "");
                    }

                    // KcUtil.showInView(url, mContext, item.getAdid(),
                    // item.getAdpid());
                } else if (item.getAdtype().equals("web")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(item.getUrl());
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        }
    }


    /**
     * 填充ViewPager页面的适配器
     *
     * @author Administrator
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }


    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     */
    private class MyPageChangeListener implements OnPageChangeListener {
        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            setImageBackground(currentItem);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }


    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tipImgs.length; i++) {
            if (i == selectItems) {
                tipImgs[i].setBackgroundResource(R.drawable.drawable_guide_call_selected);
            } else {
                tipImgs[i].setBackgroundResource(R.drawable.drawable_guide_call_normal);
            }
        }
    }

    // 切换当前显示的图片
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
        }

        ;
    };

    @Override
    protected void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
        super.onStart();
    }

    @Override
    public void onStop() {
        // 当Activity不可见的时候停止切换
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
        super.onStop();
    }


    /**
     * 换行切换任务
     *
     * @author Administrator
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
            }
        }
    }

}
