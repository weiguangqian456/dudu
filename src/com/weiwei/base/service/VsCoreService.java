package com.weiwei.base.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.callback.IncomingCallListener;
import com.weiwei.base.callback.VSCallBackHintActivity;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.base.item.VsMissCall;
import com.weiwei.base.item.Vspiano;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.netphone.SplashActivity;
import com.hwtx.dududh.R;

/**
 * @Title:Android客户端
 * @Description: 后台服务
 * @Copyright: Copyright (c) 2014
 * @author: 石云升
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsCoreService extends Service {
    private final String TAG = VsCoreService.class.getSimpleName();
    public Context mContext = this;

    /**
     * 获取JSON数据的msg
     */
    public final static String VS_KeyMsg = "msg";
    /**
     * 登录
     */
    public final static String VS_ACTION_LOGIN = "com.kc.logic.login";
    public final static String VS_ACTION_NICK = "com.kc.logic.nick";
    /**
     * 活动设置闹铃
     */
    public final static String VS_ACTION_ALARM_ACTIVITY = "com.weiwei.alarm.activity.broadcastreceiver";
    /**
     * 登录成功拉取的数据
     */
    public final static String VS_ACTION_LOGIN_SUCC = "kc_action_login_succ";

    /**
     * 自动接听参数
     */
    private ITelephony mPhone;
    private TelephonyManager mTelMgr;
    private IncomingCallListener mIncomingCallMonitor;

    /**
     * 遮挡浮框参数
     */
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View mFloatView;
    private ImageView mClose;

    /**
     * 拨打用户信息参数
     */
    private String mCallNumber;
    private String mLocalName;
    private CallBackReceiver mCallBackReceiver;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;// return null不能用bindService 启动该服务
    }

    public class MyBinder extends Binder {
        public VsCoreService getService() {
            CustomLog.i(TAG, "getService....");
            return VsCoreService.this;
        }
    }

    @Override
    public void onCreate() {
        CustomLog.i(TAG, "onCreate()... this.toString() = " + this.toString());
        mHandler = new Handler();
        mCallBackReceiver = new CallBackReceiver();

        // 注册广播监听回拨电话
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.action_start_listen_system_phone);
        filter.addAction(GlobalVariables.action_stop_listen_system_phone);
        filter.addAction(GlobalVariables.action_stop_auto_answer);
        filter.addAction(GlobalVariables.action_show_calling_float_view);
        filter.addAction(GlobalVariables.action_dismiss_calling_float_view);
        filter.addAction(GlobalVariables.action_start_app_after_call_end);
        filter.addAction(GlobalVariables.action_update_float_view_state);
        registerReceiver(mCallBackReceiver, filter);
        registerReceiver(activityAlarm, new IntentFilter(VS_ACTION_ALARM_ACTIVITY));
        //判断权限
        String[] permission = new String[]{Permission.READ_CONTACTS, Permission.WRITE_CONTACTS, Permission.GET_ACCOUNTS, Permission.READ_PHONE_STATE, Permission
                .WRITE_EXTERNAL_STORAGE};
        if(XXPermissions.isHasPermission(mContext,permission)){
            try {
                this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
                this.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
            }catch (Exception ex){
                ex.printStackTrace();
                LogUtils.e("ERROR: " + ex.toString());
            }
        }

        // 启动通话服务
//        Intent callServiceIntent = new Intent(this, ConnectionService.class);
//        this.startService(callServiceIntent);

    }

    /**
     * 系统联系人改变监听
     */
    public ContentObserver mObserver = new ContentObserver(mHandler) {
        public void onChange(boolean selfChange) {
            CustomLog.i(TAG, "ContentObserver, change is " + selfChange);
            // 当系统联系人数据库发生更改时触发此操作
            againLoadContact();
        }
    };

    private ContentObserver mCallLogObserver = new ContentObserver(mHandler) {
        public void onChange(boolean selfChange) {
            CustomLog.i(TAG, "mCallLogObserver changed selfChange = " + selfChange);

            //通话记录变化时， 检测未接来电
            checkMissedCallLog();

            if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, true)) {
                // 当通话记录数据库发生更改时触发此操作
                // 系统通话记录数据库发生变化时，更新App通话记录表，更新App通话记录界面列表
                VsPhoneCallHistory.loadCallLog();
            }
        }
    };

    /**
     * 系统联系人更改时重新读取联系人
     *
     * @return
     */
    public void againLoadContact() {
        if (!VsPhoneCallHistory.isloadContact) {
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactLocalNum, VsHttpTools.getContactsCount(mContext));
            VsPhoneCallHistory.loadContactData(mContext, 0);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CustomLog.i(TAG, "onStartCommand().");
        start(intent, startId);
        return START_STICKY;
    }

    private void start(Intent intent, int startId) {
        CustomLog.i(TAG, "start().");
        try {
            if (null == intent) {
                return;
            }
            Bundle b = intent.getExtras();
            if (b != null) {
                String aActionId = b.getString("action");
                CustomLog.i(TAG, "actionId = " + aActionId);
                if (aActionId.endsWith(VS_ACTION_LOGIN_SUCC)) {

                    // 获取Token建立拨打连接
                    VsBizUtil.getInstance().getToken(mContext);
                    // 模版配置
                    VsBizUtil.getInstance().templateConfig(mContext);
                	//app静态配置
                    VsBizUtil.getInstance().getAppInfo(mContext);
                    //升级信息
                    VsUtil.getUpdate(mContext);

                    /**
                     * 广告
                     */
                    VsBizUtil.getInstance().getAdInfos(mContext,VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
                    /**
                     * 联系人备份信息
                     */
                    VsBizUtil.getInstance().contactbackinfo(mContext);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomLog.e(TAG, "start() error : " + e.getMessage());
        }
    }

    /**
     * 广播接收器：活动闹铃广播
     */
    private BroadcastReceiver activityAlarm = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            CustomLog.i(TAG, "come in？" + bundle.getString("packname") + "," + bundle.getString("title") + "," + bundle.getString("url"));
            if (bundle != null) {
                String packname = bundle.getString("packname");
                if (packname != null && packname.equals(mContext.getPackageName())) {
                    VsUtil.setNotification(context, bundle.getString("title"), bundle.getString("url"));
                }
            }
        }
    };
    private NotificationManager manager;
    private Notification notif;
    private Runnable mCloseCallActivityRun = new Runnable() {

        @Override
        public void run() {
            CustomLog.i(TAG, "launch intent closeing....");
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        }
    };


    @Override
    public void onDestroy() {
        CustomLog.i(TAG, "onDestroy()");
        super.onDestroy();
        if (activityAlarm != null) {
            unregisterReceiver(activityAlarm);
            activityAlarm = null;
        }
    }

    class CallBackReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            CustomLog.i(TAG, "call back receiver action = " + action);

            if (GlobalVariables.action_start_listen_system_phone.equals(action)) {
                /** 开始监听系统来电，并且自动接听来电 **/
                mCallNumber = intent.getStringExtra("callNumber");
                mLocalName = intent.getStringExtra("localName");

                if (null == mPhone) {
                    try {
                        Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                        IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                        mPhone = ITelephony.Stub.asInterface(binder);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (null == mTelMgr)
                    mTelMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (null == mIncomingCallMonitor)
                    mIncomingCallMonitor = new IncomingCallListener(mPhone, mContext);
                mIncomingCallMonitor.setAutoAnswerEnable(true);
                mTelMgr.listen(mIncomingCallMonitor, PhoneStateListener.LISTEN_CALL_STATE);
            } else if (GlobalVariables.action_stop_listen_system_phone.equals(action)) {
                /** 停止监听系统来电 **/
                if (null != mTelMgr && null != mIncomingCallMonitor) {
                    mTelMgr.listen(mIncomingCallMonitor, PhoneStateListener.LISTEN_NONE);
                }
            } else if (GlobalVariables.action_stop_auto_answer.equals(action)) {
                /** 关闭自动接听标记 **/
                if (null != mIncomingCallMonitor) {
                    mIncomingCallMonitor.setAutoAnswerEnable(false);
                }
            } else if (GlobalVariables.action_show_calling_float_view.equals(action)) {
                /** 显示遮挡浮框 **/
//                createFloatView();
            } else if (GlobalVariables.action_start_app_after_call_end.equals(action)) {
                /** 通话结束，启动App **/
                startApp();
            } else if (GlobalVariables.action_dismiss_calling_float_view.equals(action)) {
                /** 关闭遮挡浮框 **/
                dismissFloatView();
            } else if (GlobalVariables.action_update_float_view_state.equals(action)) {
                /** 修改遮挡浮框状态 **/
                if (null != mFloatView) {
                }
            }
        }
    }

    /**
     *
     */
    private synchronized void checkMissedCallLog() {
        CustomLog.i(TAG, "checkCallLog(), ");
        String sortOrder = CallLog.Calls.DATE + " desc limit 50";
        Cursor c = null;
        ArrayList<VsMissCall> list = new ArrayList<VsMissCall>();

        long lastTime = VsUserConfig.getDataLong(VsApplication.getContext(), VsUserConfig.JKey_CheckSysMissedCallLogLastTime);
        String selection;

        if (0 == lastTime) {
            selection = null;
        } else {
            selection = CallLog.Calls.DATE + " > '" + lastTime + "'";
            sortOrder = CallLog.Calls.DATE + " desc ";
        }


        try {
            c = VsApplication.getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, null, sortOrder);

            if (null != c && c.moveToFirst()) {
                int i = 0;
                int colType = c.getColumnIndex(CallLog.Calls.TYPE);
                int colRead = -1;
                int colStatNew = -1;

                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    colRead = c.getColumnIndex(CallLog.Calls.IS_READ);
                }

                if (Build.MODEL.contains("Coolpad")) {//酷派机型适配
                    colStatNew = c.getColumnIndex("statNew");
                }

                int colNumber = c.getColumnIndex(CallLog.Calls.NUMBER);
                int colName = c.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int colDate = c.getColumnIndex(CallLog.Calls.DATE);
                int colNew = c.getColumnIndex(CallLog.Calls.NEW);
                do {

                    int type = c.getInt(colType);
                    boolean isRead = false;
                    if (colRead > 0) {
                        isRead = c.getInt(colRead) == 1;// 0 表示未读， 1表示已读
                    }
                    boolean isNew = c.getInt(colNew) == 1;// 1表示来电是新来的，即未读，

                    if (colStatNew > 0) {
                        isNew = c.getInt(colStatNew) == 1 && isNew;
                    }

                    // 0表示已读
                    String number = c.getString(colNumber);
                    String name = c.getString(colName);
                    long date = c.getLong(colDate);
                    if (i == 0) {
                        VsUserConfig.setData(VsApplication.getContext(), VsUserConfig.JKey_CheckSysMissedCallLogLastTime, date);
                        Log.i(TAG, "checkMissedCallLog(), i == 0 and callTimeL = " + date);
                        i++;
                    }

                    if (type == CallLog.Calls.MISSED_TYPE && isNew && !isRead) {
                        VsMissCall item = new VsMissCall();
                        item.setDate(date);
                        item.setName(name);
                        item.setNew(isNew);
                        item.setNumber(number);
                        item.setRead(isRead);
                        item.setType(type);
                        list.add(item);
                    }
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            CustomLog.e(TAG, "checkCallLog(), error:" + e.getMessage());
        } finally {
            if (null != c && !c.isClosed()) {
                c.close();
                c = null;
            }

            if (list.size() > 0) {
                showNotification(list);
            }
        }
    }

    private void showNotification(ArrayList<VsMissCall> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        String str = callToString(list);
        CustomLog.d(TAG, "showNotification(), str is " + str);

        String missedCall = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_missed_call);
        CustomLog.d(TAG, "showNotification(), missedCall is " + missedCall);

        if (!TextUtils.isEmpty(missedCall)) {
            str += "," + missedCall;
        }

        CustomLog.d(TAG, "showNotification(), str is " + str);

        String[] tmpList = toArray(str);

        final int size = tmpList.length;

        CustomLog.d(TAG, "showNotification(), incomingNumber size is " + size);

        Intent intent = new Intent(mContext, SplashActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        manager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//        notif = new Notification();
//        notif.icon = R.drawable.icon;
        String titleStr = mContext.getResources().getString(R.string.vs_miss_call);
//        notif.tickerText = titleStr;

        String contentStr = "";

        if (size > 1) {
            String missString = mContext.getResources().getString(R.string.vs_miss_call_d);
            titleStr = String.format(missString, size);
        }

        String tmpNumber;
        for (int i = 0; i < size; i++) {
            tmpNumber = tmpList[i];
            if (TextUtils.isEmpty(contentStr)) {
                int count = getCountInList(tmpNumber, tmpList);
                if (count <= 1) {
                    contentStr = tmpNumber;
                } else {
                    contentStr = tmpNumber + "(" + count + ")";
                }
            } else {
                if (!contentStr.startsWith(tmpNumber)
                        && !contentStr.contains("," + tmpNumber + "(")) {
                    int count = getCountInList(tmpNumber, tmpList);

                    if (count <= 1) {
                        contentStr += "," + tmpNumber;
                    } else {
                        contentStr += "," + tmpNumber + "(" + count + ")";
                    }
                }
            }
        }

        CustomLog.i(TAG, "showNotification(), title is " + contentStr);
        CustomLog.i(TAG, "showNotification(), incomingNumber is " + titleStr);
//        notif.setLatestEventInfo(mContext, contentStr, titleStr, pIntent);
//        notif.when = System.currentTimeMillis();

        VsUserConfig.setData(mContext, VsUserConfig.JKey_missed_call, toString(tmpList));

//        notif.contentIntent = pIntent;
//        notif.flags = Notification.FLAG_AUTO_CANCEL;
//        manager.notify(861927, notif);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.icon)
                /**设置通知的标题**/
                .setContentTitle(titleStr)
                /**设置通知的内容**/
                .setContentText(contentStr)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(pIntent)
                .build();
        manager.notify(0, notification);

        clearSysMissCallNotify();
    }

    private String[] toArray(String str) {
        return str.split(",");
    }

    private String callToString(ArrayList<VsMissCall> list) {
        StringBuffer sb = new StringBuffer();
        for (VsMissCall i : list) {
            String name = TextUtils.isEmpty(i.getName()) ? i.getNumber() : i.getName();
            if (sb.length() == 0) {
                sb.append(name);
            } else {
                sb.append(",");
                sb.append(name);
            }
        }
        return sb.toString();
    }

    private String toString(String[] list) {
        StringBuffer sb = new StringBuffer();
        for (String i : list) {
            if (sb.length() == 0) {
                sb.append(i);
            } else {
                sb.append(",");
                sb.append(i);
            }
        }
        return sb.toString();
    }

    /**
     * list 中包含多少个tmpNumber
     *
     * @param tmpNumber
     * @param list
     * @return
     * @version:2015年1月15日
     * @author:Jiangxuewu
     */
    private int getCountInList(String tmpNumber, String[] list) {
        int iRet = 0;
        if (TextUtils.isEmpty(tmpNumber) || null == list || list.length == 0) {
            return iRet;
        }
        for (String i : list) {
            if (tmpNumber.equals(i)) {
                iRet++;
            }
        }
        return iRet;
    }

    /**
     * 清除系统未接来电通知
     *
     * @version:2015年1月14日
     * @author:Jiangxuewu
     */
    private void clearSysMissCallNotify() {
        openSysCallHistoryActivity();
    }

    private void openSysCallHistoryActivity() {

        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
        long delay = 200;
        if (Build.MODEL.contains("Coolpad")) {//酷派机型适配
            i = getPackageManager().getLaunchIntentForPackage("com.yulong.android.callhistory");
            delay = 2000;
        } else if (Build.MODEL.contains("MX")) {//魅族机型适配
            i = getPackageManager().getLaunchIntentForPackage("com.android.dialer");
        }

        if (null != i) {
            CustomLog.i(TAG, "launch intent is not null");
            startActivity(i);
            mHandler.postDelayed(mCloseCallActivityRun, delay);
        } else {
            CustomLog.i(TAG, "launch intent is null");
        }

    }

    /**
     * 创建浮框
     *
     * @version:2014年12月24日
     * @author:Jiangxuewu
     */
    private void createFloatView() {
        CustomLog.i(TAG, "createFloatView()...");
        wmParams = new WindowManager.LayoutParams();
        // 获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
                // LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // LayoutParams.FLAG_NOT_TOUCHABLE
        ;

        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;

        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // 获取浮动窗口视图所在布局
        mFloatView = View.inflate(getApplication(), R.layout.vs_windows_manager_float_layout, null);

        mClose = (ImageView) mFloatView.findViewById(R.id.vs_incoming_close);

        mClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissFloatView();
            }
        });

        // 添加mFloatLayout
        mWindowManager.addView(mFloatView, wmParams);

        CustomLog.i(TAG, "tView.left=" + mFloatView.getLeft());
        CustomLog.i(TAG, "tView.right=" + mFloatView.getRight());
        CustomLog.i(TAG, "tView.top=" + mFloatView.getTop());
        CustomLog.i(TAG, "tView.bottom=" + mFloatView.getBottom());

        TextView mLocal = (TextView) mFloatView.findViewById(R.id.vs_incoming_local);
        if (TextUtils.isEmpty(mLocalName)) {
            mLocalName = "";
        }
        mLocal.setText(mLocalName);
        TextView mNumber = (TextView) mFloatView.findViewById(R.id.vs_incoming_number);
        if (TextUtils.isEmpty(mCallNumber)) {
            mCallNumber = "";
        }
        mNumber.setText(mCallNumber);
    }

    /**
     * 销毁浮框
     *
     * @version:2014年12月24日
     * @author:Jiangxuewu
     */
    private void dismissFloatView() {
        CustomLog.i(TAG, "dismissFloatView()...");
        if (mFloatView != null && null != mFloatView) {
            mWindowManager.removeView(mFloatView);
            mFloatView = null;
        }
    }

    /**
     * 钢琴音
     */
    public static  Map<Integer,Vspiano> pianoMap=new HashMap<Integer, Vspiano>();
    private static SoundPool sp;
    public static HashMap<Integer,Integer> spMap=new HashMap<Integer,Integer>();

    /**钢琴音播放曲谱数组
     *
     * @param rawId
     */
    @SuppressWarnings("deprecation")
	public static void setSpData(final int key,final Context context){
    	if(sp==null){
    		sp=new SoundPool(2,AudioManager.STREAM_MUSIC,0);
    	}
    	spMap.clear();;
    	Thread setData=new Thread(){
    		@Override
    		public void run() {
    			int[] rawId=pianoMap.get(key).getPianoId();
    	    	for(int i=0;i<rawId.length;i++){
    	    		if(key!=VsUserConfig.getDataInt(context,VsUserConfig.JKEY_PIANO_ISCHECHED_ID)){
     	    			return;
     	    		}
    	    		 spMap.put(i, sp.load(context,rawId[i],1));
    	    	}
    		}
    	};
    	GlobalVariables.cachedThreadPool.execute(setData);
    }


    /**
     * 2.拨号播放钢琴音方法
     * @param sound
     * @param number
     */
	public static void playSounds(final int sound, final int number,Context context){
        AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        final float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
        Thread play=new Thread(){
        	public void run() {
        		if(spMap.get(sound)!=null){
        		sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number, 1);
        		}
        	};
        };
        GlobalVariables.fixedThreadPoolPiano.execute(play);
    }
    public static void getData(){
		//梁祝
		int[] lz={R.raw.a38,R.raw.a41,R.raw.a43,R.raw.a46,R.raw.a48,R.raw.a43,R.raw.a46,R.raw.a41,R.raw.a53,R.raw.a58,R.raw.a55,R.raw.a53,R.raw.a50,R.raw.a53,R.raw.a48,R.raw.a48,R.raw.a50,R.raw.a45,R.raw.a43,R.raw.a41,R.raw.a43,R.raw.a46,R.raw.a48,R.raw.a38,R.raw.a46,R.raw.a43,R.raw.a41,R.raw.a43,R.raw.a46,R.raw.a41,R.raw.a50,R.raw.a53,R.raw.a45,R.raw.a48,R.raw.a43,R.raw.a46,R.raw.a41,R.raw.a38,R.raw.a41,R.raw.a38,R.raw.a41,R.raw.a43,R.raw.a45,R.raw.a48,R.raw.a43,R.raw.a41,R.raw.a43,R.raw.a46,R.raw.a48,R.raw.a53,R.raw.a50,R.raw.a48,R.raw.a50,R.raw.a48,R.raw.a46,R.raw.a43,R.raw.a41,R.raw.a38,R.raw.a46,R.raw.a43,R.raw.a46,R.raw.a43,R.raw.a41,R.raw.a38,R.raw.a41,R.raw.a43,R.raw.a46,R.raw.a41};
		//天空之城
		int[] tkzc={R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a44,R.raw.a39,R.raw.a32,R.raw.a37,R.raw.a35,R.raw.a37,R.raw.a40,R.raw.a35,R.raw.a32,R.raw.a32,R.raw.a33,R.raw.a32,R.raw.a33,R.raw.a40,R.raw.a32,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a39,R.raw.a34,R.raw.a39,R.raw.a39,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a44,R.raw.a39,R.raw.a32,R.raw.a32,R.raw.a37,R.raw.a35,R.raw.a37,R.raw.a40,R.raw.a35,R.raw.a32,R.raw.a33,R.raw.a32,R.raw.a33,R.raw.a40,R.raw.a32,R.raw.a30,R.raw.a32,R.raw.a40,R.raw.a40,R.raw.a39,R.raw.a37,R.raw.a39,R.raw.a36,R.raw.a37};
		//茉莉花
		int[] mlh={R.raw.a47,R.raw.a45,R.raw.a47,R.raw.a50,R.raw.a52,R.raw.a50,R.raw.a55,R.raw.a52,R.raw.a50,R.raw.a47,R.raw.a50,R.raw.a52,R.raw.a55,R.raw.a57,R.raw.a59,R.raw.a57,R.raw.a55,R.raw.a52,R.raw.a50,R.raw.a50,R.raw.a47,R.raw.a50,R.raw.a52,R.raw.a55,R.raw.a57,R.raw.a59,R.raw.a55,R.raw.a52,R.raw.a50,R.raw.a50,R.raw.a45,R.raw.a47,R.raw.a50,R.raw.a47,R.raw.a45,R.raw.a43,R.raw.a40,R.raw.a43,R.raw.a47,R.raw.a45,R.raw.a43,R.raw.a45,R.raw.a50,R.raw.a52,R.raw.a55,R.raw.a52,R.raw.a50,R.raw.a50,R.raw.a47,R.raw.a45,R.raw.a47,R.raw.a50,R.raw.a47,R.raw.a45,R.raw.a43,R.raw.a45,R.raw.a40,R.raw.a43,R.raw.a45,R.raw.a47,R.raw.a43,R.raw.a45,R.raw.a43,R.raw.a43,R.raw.a40,R.raw.a38};
		//Spring
		int[] spr={R.raw.a49,R.raw.a58,R.raw.a56,R.raw.a54,R.raw.a56,R.raw.a53,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a49,R.raw.a51,R.raw.a49,R.raw.a47,R.raw.a49,R.raw.a47,R.raw.a46,R.raw.a47,R.raw.a46,R.raw.a42,R.raw.a44,R.raw.a49,R.raw.a58,R.raw.a56,R.raw.a58,R.raw.a56,R.raw.a53,R.raw.a54,R.raw.a56,R.raw.a58,R.raw.a53,R.raw.a49,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a56,R.raw.a54,R.raw.a53,R.raw.a54};
		//我在那一角患过伤风
		int[] feng={R.raw.a46,R.raw.a55,R.raw.a53,R.raw.a55,R.raw.a55,R.raw.a55,R.raw.a55,R.raw.a56,R.raw.a58,R.raw.a58,R.raw.a58,R.raw.a51,R.raw.a53,R.raw.a55,R.raw.a55,R.raw.a63,R.raw.a63,R.raw.a62,R.raw.a58,R.raw.a58,R.raw.a56,R.raw.a51,R.raw.a48,R.raw.a44,R.raw.a39,R.raw.a51,R.raw.a36,R.raw.a51,R.raw.a32,R.raw.a51,R.raw.a36,R.raw.a51,R.raw.a39,R.raw.a51,R.raw.a50,R.raw.a58,R.raw.a53,R.raw.a50,R.raw.a46,R.raw.a50,R.raw.a46,R.raw.a41,R.raw.a46,R.raw.a41,R.raw.a38,R.raw.a41,R.raw.a34};
		//月亮代表我的心
		int[] yueliang={R.raw.a36,R.raw.a41,R.raw.a45,R.raw.a48,R.raw.a41,R.raw.a40,R.raw.a45,R.raw.a48,R.raw.a48,R.raw.a50,R.raw.a52,R.raw.a53,R.raw.a50,R.raw.a48,R.raw.a45,R.raw.a43,R.raw.a41,R.raw.a41,R.raw.a41,R.raw.a45,R.raw.a43,R.raw.a41,R.raw.a41,R.raw.a41,R.raw.a43,R.raw.a45,R.raw.a43,R.raw.a38,R.raw.a40,R.raw.a43,R.raw.a41};
		//卡农
		int[] kl={R.raw.a47,R.raw.a44,R.raw.a45,R.raw.a47,R.raw.a44,R.raw.a45,R.raw.a47,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a45,R.raw.a44,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a37,R.raw.a40,R.raw.a39,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a33,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a37,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a39,R.raw.a37,R.raw.a39,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a45,R.raw.a47,R.raw.a44,R.raw.a45,R.raw.a47,R.raw.a44,R.raw.a45,R.raw.a47,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a45,R.raw.a44,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a37,R.raw.a40,R.raw.a39,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a33,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a37,R.raw.a40,R.raw.a39,R.raw.a40,R.raw.a39,R.raw.a37,R.raw.a39,R.raw.a35,R.raw.a37,R.raw.a39,R.raw.a40,R.raw.a42,R.raw.a44,R.raw.a45,R.raw.a47};
		//多啦A梦
		int[] dlam={R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a51,R.raw.a49,R.raw.a56,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a56,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a54,R.raw.a56,R.raw.a54,R.raw.a53,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a53,R.raw.a55,R.raw.a56,R.raw.a51,R.raw.a53,R.raw.a54,R.raw.a47,R.raw.a51,R.raw.a49,R.raw.a53,R.raw.a46,R.raw.a47,R.raw.a49,R.raw.a47,R.raw.a49,R.raw.a51,R.raw.a44,R.raw.a46,R.raw.a47,R.raw.a46,R.raw.a47,R.raw.a49,R.raw.a42,R.raw.a44,R.raw.a46,R.raw.a56,R.raw.a55,R.raw.a54,R.raw.a53,R.raw.a52,R.raw.a51,R.raw.a50,R.raw.a49,R.raw.a48,R.raw.a47,R.raw.a46,R.raw.a45,R.raw.a44,R.raw.a37,R.raw.a42,R.raw.a42,R.raw.a46,R.raw.a51,R.raw.a46,R.raw.a49,R.raw.a49,R.raw.a51,R.raw.a49,R.raw.a46,R.raw.a47,R.raw.a46,R.raw.a44,R.raw.a39,R.raw.a39,R.raw.a39,R.raw.a44,R.raw.a53,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a47,R.raw.a47,R.raw.a47,R.raw.a46,R.raw.a39,R.raw.a41,R.raw.a42,R.raw.a44,R.raw.a37,R.raw.a42,R.raw.a42,R.raw.a46,R.raw.a51,R.raw.a46,R.raw.a49,R.raw.a49,R.raw.a51,R.raw.a49,R.raw.a46,R.raw.a47,R.raw.a46,R.raw.a44,R.raw.a39,R.raw.a39,R.raw.a39,R.raw.a44,R.raw.a53,R.raw.a53,R.raw.a51,R.raw.a49,R.raw.a47,R.raw.a47,R.raw.a47,R.raw.a46,R.raw.a41,R.raw.a44,R.raw.a42};
		//我愿意
		int[] wyy={R.raw.a33,R.raw.a45,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a33,R.raw.a45,R.raw.a42,R.raw.a40,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a38,R.raw.a50,R.raw.a49,R.raw.a49,R.raw.a47,R.raw.a45,R.raw.a47,R.raw.a45,R.raw.a42,R.raw.a45,R.raw.a38,R.raw.a50,R.raw.a49,R.raw.a49,R.raw.a47,R.raw.a45,R.raw.a45,R.raw.a50,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a42,R.raw.a43,R.raw.a42,R.raw.a38,R.raw.a35,R.raw.a38,R.raw.a35,R.raw.a33,R.raw.a33,R.raw.a45,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a33,R.raw.a45,R.raw.a42,R.raw.a40,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a38,R.raw.a50,R.raw.a49,R.raw.a49,R.raw.a47,R.raw.a45,R.raw.a47,R.raw.a45,R.raw.a42,R.raw.a45,R.raw.a38,R.raw.a50,R.raw.a49,R.raw.a49,R.raw.a47,R.raw.a45,R.raw.a45,R.raw.a50,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a43,R.raw.a42,R.raw.a38,R.raw.a35,R.raw.a38,R.raw.a43,R.raw.a42,R.raw.a38,R.raw.a35,R.raw.a38,R.raw.a40,R.raw.a38};

		int[] awm={R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a33,R.raw.a40,R.raw.a37,R.raw.a35,R.raw.a40,R.raw.a35,R.raw.a33,R.raw.a30,R.raw.a37,R.raw.a33,R.raw.a32,R.raw.a32,R.raw.a30,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a28,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a38,R.raw.a38,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a33,R.raw.a40,R.raw.a37,R.raw.a35,R.raw.a40,R.raw.a35,R.raw.a33,R.raw.a30,R.raw.a30,R.raw.a32,R.raw.a33,R.raw.a28,R.raw.a28,R.raw.a30,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a28,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a38,R.raw.a38,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a33,R.raw.a37,R.raw.a38,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a37,R.raw.a37,R.raw.a37,R.raw.a37,R.raw.a37,R.raw.a38,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a33,R.raw.a33,R.raw.a32,R.raw.a30,R.raw.a32,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a35,R.raw.a37,R.raw.a35,R.raw.a37,R.raw.a35,R.raw.a37,R.raw.a38,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a40,R.raw.a42,R.raw.a40,R.raw.a38,R.raw.a37,R.raw.a37,R.raw.a37,R.raw.a37,R.raw.a38,R.raw.a37,R.raw.a35,R.raw.a33,R.raw.a32,R.raw.a30,R.raw.a30,R.raw.a32,R.raw.a33,R.raw.a35,R.raw.a28,R.raw.a33,R.raw.a35,R.raw.a37,R.raw.a35,R.raw.a35,R.raw.a35,R.raw.a33,R.raw.a33};

		pianoMap.put(1,new Vspiano(1, "梁祝", lz));
		pianoMap.put(2,new Vspiano(2, "天空之城", tkzc));
		pianoMap.put(3,new Vspiano(3, "茉莉花", mlh));
		pianoMap.put(4,new Vspiano(4, "Spring", spr));
		pianoMap.put(5,new Vspiano(5, "我在那一角患过伤风", feng));
		pianoMap.put(6,new Vspiano(6, "月亮代表我的心", yueliang));
		pianoMap.put(7,new Vspiano(7, "卡农", kl));
		pianoMap.put(8,new Vspiano(8, "多啦A梦",dlam));
		pianoMap.put(9,new Vspiano(9, "我愿意",wyy));
		pianoMap.put(10,new Vspiano(10, "Always with me",awm));

	}
    /**
     * 返回App, 从系统通话界面返回我们的程序
     *
     * @version:2014年12月24日
     * @author:Jiangxuewu
     */
    private void startApp() {
        Intent i = new Intent(getApplicationContext(), VSCallBackHintActivity.class);
        i.putExtra("after_auto_answer_exit", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage(mContext.getPackageName());
        getApplicationContext().startActivity(i);
    }
}
