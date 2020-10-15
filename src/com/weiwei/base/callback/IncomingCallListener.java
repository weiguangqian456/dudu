package com.weiwei.base.callback;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.internal.telephony.ITelephony;
import com.zte.functions.autoanswer.FunctionApi;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.bakcontact.ContactHelper;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;

public class IncomingCallListener extends PhoneStateListener {
    private final String TAG = "IncomingCallListener";
    private final int MSG_CLOSE_DLG = 0;
    private final int MSG_MESSAGE_DIALOG = 3;
    private ITelephony mPhone;
    private final int MSG_BIGSDK = 23;
    Handler mHandler = null;
    private Context mContext;

    @SuppressWarnings("deprecation")
    public int nSdkVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
    // private boolean JUST_FIRST = true;
    public static boolean CALL_STATE_RINGING = false;
    private boolean CALL_STATE_OFFHOOK = false;
    public static boolean isSuccCalling = false;

    /**
     * 是否自动接听来电
     */
    private boolean isAutoAnswer = true;

    /**
     * 标记是否是通话中
     */
    private boolean isCalling = false;
    private long inComingTime;

    /**
     * @param phone
     * @param tHandler
     * @deprecated
     */
    public IncomingCallListener(ITelephony phone, Handler tHandler) {
        mHandler = tHandler;
        mPhone = phone;
        mContext = null;
    }

    /**
     * 构造函数， 通过广播通知服务来处理事件
     *
     * @param phone
     * @param context
     */
    public IncomingCallListener(ITelephony phone, Context context) {
        mContext = context;
        mPhone = phone;
        mHandler = null;
    }

    /**
     * 设置是否自动接听标记
     *
     * @param isAutoAnswer true 表示自动接听
     * @version:2014年12月24日
     * @author:Jiangxuewu
     */
    public void setAutoAnswerEnable(boolean isAutoAnswer) {
        this.isAutoAnswer = isAutoAnswer;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        CustomLog.i(TAG, "state = " + state + ", incomingNumber = " + incomingNumber);

        if (Build.MODEL.contains("MX4") && TextUtils.isEmpty(incomingNumber) && state != TelephonyManager.CALL_STATE_RINGING) {
            return;
        } else if (Build.MODEL.contains("MX4") && TextUtils.isEmpty(incomingNumber) && state == TelephonyManager.CALL_STATE_RINGING){
            setAutoAnswerEnable(false);
            isCalling = false;
            Intent i = new Intent(GlobalVariables.action_start_app_after_call_end);
            i.setPackage(mContext.getPackageName());
            mContext.sendBroadcast(i);
            return;
        }


        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:// 1 来电了

                boolean autoAnswer = VsUserConfig.getDataBoolean(mContext,
                        VsUserConfig.JKEY_SETTING_HINT_VOICE, true);
                if (!autoAnswer) {
                    return;
                }

                if (!isAutoAnswer)
                    return;

                isSuccCalling = false;
                CALL_STATE_RINGING = true;
                CustomLog.i(TAG, "获得的开发版本号是:" + nSdkVersion);
                if (null != mContext) {
                    boolean isExit = ContactHelper.isExitInContacts(mContext,
                            incomingNumber);
                    if (isExit) {
                        CustomLog.i(TAG, "Incoming number is exit in contaxts");
                        return;
                    }
                }

                // 如果已经在通话了就不在自动接听
                if (isCalling) {
                    CustomLog.i(TAG, "call coming, but had exit calling.");
                    return;
                }

                // 铃响，广播通知服务，显示遮挡视图
                if (null != mContext) {
                    Intent i = new Intent(
                            GlobalVariables.action_show_calling_float_view);
                    i.setPackage(DfineAction.packagename);
                    mContext.sendBroadcast(i);
                }

                if (nSdkVersion > 8) {
                    CustomLog.i(TAG, "自动接听支持2.3系统");
                    if (mHandler != null)
                        mHandler.sendEmptyMessageDelayed(MSG_BIGSDK, 200);

                    if (null == mHandler && null != mContext) {
                        try {
                            PhoneUtils.answerRingingCall(mContext);
                            isCalling = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            new FunctionApi(VsApplication.getContext()).reportNotAnswered("sdk > 8, Exception:" + e.getMessage());
                        } finally {
                            // 只自动接听一次
                            // 广播通知服务取消自动接听来电
                            Intent i = new Intent(GlobalVariables.action_stop_auto_answer);
                            i.setPackage(mContext.getPackageName());
                            mContext.sendBroadcast(i);
                        }
                    }

                } else {
                    CustomLog.i(TAG, "自动接听支持2.3或以下系统");
                    // Ringing-振铃，有电话呼入
                    CustomLog.d(TAG, "获得来电号码：" + incomingNumber);
                    try {
                        Thread.sleep(200);
                        mPhone.answerRingingCall();
                        isCalling = true;
                        // if (mHandler != null)
                        // mHandler.sendEmptyMessageDelayed(MSG_CLOSE_DLG, 1000);
                        // mPhone.showCallScreen();
                        // mPhone.showCallScreenWithDialpad(false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        new FunctionApi(VsApplication.getContext()).reportNotAnswered("RemoteException:" + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        new FunctionApi(VsApplication.getContext()).reportNotAnswered("sdk <= 8, Exception:" + e.getMessage());
                    } finally {
                        // 只自动接听一次
                        // 广播通知服务取消自动接听来电
                        if (null != mContext) {
                            Intent i = new Intent(
                                    GlobalVariables.action_stop_auto_answer);
                            i.setPackage(DfineAction.packagename);
                            mContext.sendBroadcast(i);
                        }
                    }
                }
                inComingTime = 0;
                inComingTime = System.currentTimeMillis();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 2
                CALL_STATE_OFFHOOK = true;
                isSuccCalling = true;
                isCalling = true; // 接通来电了， 可能是其他电话
                VsUserConfig.CallBackStartTime = System.currentTimeMillis() / 1000;
                // Offhook-摘机，呼出电话已接通或呼入电话已接起

                checkAutoAnswerRes(2);
                break;
            case TelephonyManager.CALL_STATE_IDLE:// 0
                checkAutoAnswerRes(0);
                CustomLog.i(TAG,
                        "===========TelephonyManager.CALL_STATE_IDLE============\nCALL_STATE_OFFHOOK="
                                + CALL_STATE_OFFHOOK);
                isSuccCalling = true;
                if (mHandler != null && CALL_STATE_OFFHOOK) {
                    VsUserConfig.CallBackEndTime = System.currentTimeMillis() / 1000;
                    CustomLog.i(TAG, "挂机");
                    // mHandler.sendEmptyMessage(MSG_CLOSE_DLG);
                    mHandler.sendEmptyMessageDelayed(MSG_CLOSE_DLG, 1 * 1000);
                }

                if (null != mContext && isCalling) {

                    // 通话结束，通知服务，更新浮框的状态
                    Intent i = new Intent(
                            GlobalVariables.action_update_float_view_state);
                    i.setPackage(mContext.getPackageName());
                    mContext.sendBroadcast(i);
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                // 广播通知服务，取消遮挡视图
                                Intent i = new Intent(
                                        GlobalVariables.action_start_app_after_call_end);
                                i.setPackage(DfineAction.packagename);
                                mContext.sendBroadcast(i);
                                isCalling = false;
                            }
                        }
                    };

                    // 使用线程池进行管理
//				GlobalVariables.fixedThreadPool.execute(run);
                    new Thread(run).start();
                }
                isCalling = false;

                break;
        }
        // 定时10秒钟
        /*
		 * TimerTask task = new TimerTask() { public void run() { if
		 * ((!CALL_STATE_RINGING) && (!CALL_STATE_OFFHOOK)) {
		 * mHandler.sendEmptyMessage(MSG_MESSAGE_DIALOG); } } };
		 * 
		 * Timer timer = new Timer(); timer.schedule(task, 35 * 1000);
		 */

    }

    private void checkAutoAnswerRes(int type) {
        long curTime = System.currentTimeMillis();
        if (inComingTime > 1000) {
            if (type == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (curTime - inComingTime > 0 && curTime - inComingTime < 3000) {
                    //auto answer success
                    //接听成功，初始化变量
                    inComingTime = 0;
                } else {
                    //auto answer failed
                    new FunctionApi(VsApplication.getContext()).reportNotAnswered("type = CALL_STATE_OFFHOOK, curTime = " + curTime + ", inComingTime = " + inComingTime);
                }
            } else if (type == TelephonyManager.CALL_STATE_IDLE) {
                new FunctionApi(VsApplication.getContext()).reportNotAnswered("type = CALL_STATE_IDLE, curTime = " + curTime + ", inComingTime = " + inComingTime);
            }
        }
        inComingTime = 0;
    }
}