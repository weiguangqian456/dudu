package com.weiwei.base.activity.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.bakcontact.ContactSync;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.http.VsHttpTools;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.weiwei.salemall.utils.FitStateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class VsContactBackUpActivity extends VsBaseActivity {
    private static final String TAG = VsContactBackUpActivity.class.getSimpleName();
    private LinearLayout mManualBackupLayout;// 手动备份
    private LinearLayout mRenewBackupLayout;// 恢复
    private TextView mBackupLocalNumTextView;// 本地通讯录个数
    private TextView mBackupServerNumTextView;// 备份通讯录个数
    private TextView mBackupTimeTextView;// 备份时间
    private TextView mRewBackTimeTextView;// 恢复时间

    private ProgressDialogBar mProgressDialogBar;
    /**
     * 显示进度框，通知用户正在备份
     */
    private static final char MSG_ID_Progress_Recharging = 1;
    /**
     * 取消进度框
     */
    private static final char MSG_ID_Cancel_ProgressDialog = 2;
    /**
     * 消息ID-提示用户是否备份
     */
    private static final char MSG_ID_ShowBakupDialog = 3;
    /**
     * 消息ID-提示用户是否恢复
     */
    private static final char MSG_ID_ShowRenewDialog = 4;
    /**
     * 消息ID-显示进度条
     */
    private static final char MSG_ID_ShowProgressDialog = 5;
    /**
     * 消息ID-更新进度条
     */
    private static final char MSG_ID_UpdateProgressDialog = 6;
    /**
     * 消息ID-备份完成
     */
    private static final char MSG_ID_BakupContactFinished = 101;
    /**
     * 消息ID-备份未完成
     */
    private static final char MSG_ID_BakupContactUnfinished = 102;
    /**
     * 消息ID-恢复完成
     */
    private static final char MSG_ID_RenewContactFinished = 103;
    /**
     * 消息ID-恢复未完成
     */
    private static final char MSG_ID_RenewContactUnfinished = 104;
    private int progressDialogUpdateDelayTime = 100;

    /**
     * 是否已经取消备份对话框
     */
    private boolean isCancelBakupProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_contact_backup);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getResources().getString(R.string.bakcontact_bakcontact));
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        init();
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
        loadContact();


    }

    /**
     * 刷新界面信息
     */
    private void refreshViewInfo() {
        mBackupLocalNumTextView = (TextView) findViewById(R.id.backup_phone_num);
        mBackupServerNumTextView = (TextView) findViewById(R.id.backup_phone_servernum);
        mBackupTimeTextView = (TextView) findViewById(R.id.backup_phone_time);
        mRewBackTimeTextView = (TextView) findViewById(R.id.rew_backup_phone_time);

        int localnum = VsUserConfig.getDataInt(mContext, VsUserConfig.JKey_ContactLocalNum);
        mBackupLocalNumTextView.setText(localnum + "人");
        mBackupServerNumTextView.setText(VsUserConfig.getDataInt(mContext, VsUserConfig.JKey_ContactServerNum) + "人");

        if (localnum > 0) {
            progressDialogUpdateDelayTime = (2 * localnum) / 100;
        }
        String backuptime = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ContactBakTime);
        if (backuptime.length() > 1) {
            mBackupTimeTextView.setText(backuptime);
        } else {
            mBackupTimeTextView.setText(getResources().getString(R.string.bakcontact_bak_never));
        }
        String rew_backup_time = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ContactRenewBakTime);
        if (rew_backup_time.length() > 1) {
            mRewBackTimeTextView.setText(rew_backup_time);
        } else {
            mRewBackTimeTextView.setText(getResources().getString(R.string.bakcontact_recover_never));
        }
    }

    private void init() {
        mManualBackupLayout = (LinearLayout) findViewById(R.id.manual_backup_id);
        mRenewBackupLayout = (LinearLayout) findViewById(R.id.renew_backup_id);
        mManualBackupLayout.setOnClickListener(mManualBackupListener);
        mRenewBackupLayout.setOnClickListener(mManualBackupListener);
    }

    /**
     * 进度条百分比
     */
    int progressPercent = 0;
    boolean flag = false;
    private View.OnClickListener mManualBackupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.manual_backup_id://备份
                /*  if (!isLogin(R.string.bakup_login_prompt))
                      return;*/
                    mBaseHandler.sendEmptyMessage(MSG_ID_ShowBakupDialog);
                    break;
                case R.id.renew_backup_id://恢复
                    //                if (!isLogin(R.string.renew_login_prompt))
                    //                    return;
                    mBaseHandler.sendEmptyMessage(MSG_ID_ShowRenewDialog);
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * 确定备份通讯录按钮响应类
     */
    private class BakupBtnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            int countactNum = VsHttpTools.getContactsCount(mContext);// 取本地联系人个数
            if (countactNum > 0) {
                mBaseHandler.sendEmptyMessage(MSG_ID_ShowProgressDialog);

                unregisterKcBroadcast();
                // 绑定广播接收器
                IntentFilter filter = new IntentFilter();
                filter.addAction(GlobalVariables.actionBackUp);
                vsBroadcastReceiver = new KcBroadcastReceiver();

                mContext.registerReceiver(vsBroadcastReceiver, filter);
                /* Hashtable<String, String> bizparams = new Hashtable<String, String>();
                 bizparams.put("vcard_contacts", ContactSync.getInstance().getBakContactData(mContext));
                 KcCoreService.requstServiceMethod(mContext, "contacts/backup", bizparams, KcCoreService.KC_ACTION_BACKUP_CONTACTS, DfineAction.authType_AUTO);*/

                CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_CONTACT_BACKUP, DfineAction.authType_AUTO, null, GlobalVariables.actionBackUp);

            } else {
                if (mProgressDialogBar != null) {
                    mProgressDialogBar.dismiss();
                }
                progressPercent = 0;
                mToast.show(getResources().getString(R.string.bak_contact_null_ornot), Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 确定恢复通讯录按钮响应类
     */
    private class RenewBtnClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            loadProgressDialog(getResources().getString(R.string.bak_loading_contact));
            mProgressDialog.setCancelable(false);

            unregisterKcBroadcast();
            // 绑定广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(GlobalVariables.actionRenew);
            vsBroadcastReceiver = new KcBroadcastReceiver();

            mContext.registerReceiver(vsBroadcastReceiver, filter);

            //bizparams.put("vcard_contacts", ContactSync.getInstance().getBakContactData(mContext));
            CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_CONTACT_DOWN, DfineAction.authType_AUTO, null, GlobalVariables.actionRenew);
        }
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        super.handleKcBroadcast(context, intent);

        String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
        if (intent.getAction().equals(GlobalVariables.actionBackUp)) {
            Message message = mBaseHandler.obtainMessage();
            Bundle bundle = new Bundle();
            try {
                JSONObject jData = new JSONObject(jStr);
                String retStr = jData.getString("result");
                if (retStr.equals("0")) {
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 ");
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactBakTime, formatter.format(curDate));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactServerNum, VsUserConfig.getDataInt(mContext, VsUserConfig.JKey_ContactLocalNum));

                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_BakupContactFinished;
                } else {
                    if (retStr.equals("-99")) {
                        dismissProgressDialog();
                        /*  if (!KcTestAccessPoint.isCurrentNetworkAvailable(mContext))
                              return;*/
                    }
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_BakupContactUnfinished;
                }
            } catch (Exception e) {
                e.printStackTrace();
                bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                bundle.putString("msg", getResources().getString(R.string.bak_bakup_failinfo));
                message.what = MSG_ID_BakupContactUnfinished;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        } else if (intent.getAction().equals(GlobalVariables.actionRenew)) {
            Message message = mBaseHandler.obtainMessage();
            Bundle bundle = new Bundle();
            try {
                JSONObject jData = new JSONObject(jStr);
                String retStr = jData.getString("result");
                if (retStr.equals("0")) {
                    boolean recoverSuccess = ContactSync.getInstance().recoverPhoneContatcs_Cover(jData, mContext);
                    if (recoverSuccess) {
                        bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                        bundle.putString("msg", getResources().getString(R.string.bak_renew_success));
                        message.what = MSG_ID_RenewContactFinished;
                    } else {
                        bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                        bundle.putString("msg", "通讯录恢复失败！");
                        message.what = MSG_ID_RenewContactUnfinished;
                    }
                } else {
                    if (retStr.equals("-99")) {
                        // if (!KcTestAccessPoint.isCurrentNetworkAvailable(mContext)) {
                        dismissProgressDialog();
                        return;
                        // }
                    }
                    bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                    bundle.putString("msg", jData.getString("reason"));
                    message.what = MSG_ID_RenewContactUnfinished;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                bundle.putString("title", DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.prompt));
                bundle.putString("msg", getResources().getString(R.string.bak_renew_failinfo));
                message.what = MSG_ID_RenewContactUnfinished;
            }
            message.setData(bundle);
            mBaseHandler.sendMessage(message);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        CustomLog.i(TAG, "handleBaseMessage(), msg.what = " + msg.what);
        switch (msg.what) {
            case MSG_ID_BakupContactFinished:
            case MSG_ID_BakupContactUnfinished:
            case MSG_ID_RenewContactUnfinished:
                dismissProgressDialog();
                if (mProgressDialogBar != null) mProgressDialogBar.dismiss();
                if (isCancelBakupProgress) return;
                refreshViewInfo();
                showMessageDialog(msg.getData().getString("title"), msg.getData().getString("msg"));
                mBaseHandler.removeMessages(MSG_ID_UpdateProgressDialog);
                break;
            case MSG_ID_RenewContactFinished:
                dismissProgressDialog();
                if (mProgressDialogBar != null) mProgressDialogBar.dismiss();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 ");
                Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactRenewBakTime, formatter.format(curDate));
                VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactLocalNum, VsHttpTools.getContactsCount(mContext));
                refreshViewInfo();
                showMessageDialog(msg.getData().getString("title"), msg.getData().getString("msg"));
                break;
            case MSG_ID_UpdateProgressDialog:
                if (progressPercent <= 98) progressPercent += 1;
                CustomLog.i(TAG, "handleBaseMessage(), progressPercent =========== " + progressPercent);

                if (null != mProgressDialogBar) {
                    mProgressDialogBar.setMessage(progressPercent + "%");
                    mProgressDialogBar.setProgress(progressPercent);
                    mBaseHandler.sendEmptyMessageDelayed(MSG_ID_UpdateProgressDialog, progressDialogUpdateDelayTime);
                }
                break;
            case MSG_ID_ShowProgressDialog:
                dismissProgressDialog();
                flag = true;

                isCancelBakupProgress = false;
                progressPercent = 0;
                mProgressDialogBar = new ProgressDialogBar(mContext);

                mProgressDialogBar.setTitle(getResources().getString(R.string.bakcontact_bakcontact));
                mProgressDialogBar.setProgressStyle(ProgressDialogBar.STYLE_HORIZONTAL);
                mProgressDialogBar.setMax(100);
                mProgressDialogBar.setButton2(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        flag = false;
                        mProgressDialogBar.dismiss();
                        mProgressDialogBar = null;
                        isCancelBakupProgress = true;
                    }
                });
                mProgressDialogBar.show();

                // 发送消息启动-进度条更新
                mBaseHandler.sendEmptyMessage(MSG_ID_UpdateProgressDialog);
                break;
            case MSG_ID_Progress_Recharging:
                loadProgressDialog(msg.getData().getString("msg"));
                break;
            case MSG_ID_Cancel_ProgressDialog:
                dismissProgressDialog();
                break;
            case MSG_ID_ShowBakupDialog:
                showYesNoDialog(R.string.bak_bakup_confirm, DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.bak_contact_bakup_query), new
                        BakupBtnClickListener(), null);
                break;
            case MSG_ID_ShowRenewDialog:
                showYesNoDialog(R.string.bak_renew_confirm, DfineAction.RES.getString(R.string.product) + getResources().getString(R.string.bak_contact_renew_query), new
                        RenewBtnClickListener(), null);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshViewInfo();
    }

    public void loadContact() {
        if (!VsPhoneCallHistory.isloadContact) {
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ContactLocalNum, VsHttpTools.getContactsCount(mContext));
            VsPhoneCallHistory.loadContactData(mContext, 0);
        }


    }
}