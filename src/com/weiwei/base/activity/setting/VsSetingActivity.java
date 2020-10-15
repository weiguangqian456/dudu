package com.weiwei.base.activity.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.contacts.VsContactBackUpActivity;
import com.weiwei.base.activity.login.VsLoginActivity;
import com.weiwei.base.activity.login.VsSetPhoneActivity;
import com.weiwei.base.activity.me.KcQcodeActivity;
import com.weiwei.base.activity.me.VsPersonalizedActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsNotice;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.salemall.utils.CheckLoginStatusUtils;
import com.weiwei.salemall.utils.FitStateUtils;

/**
 * @Title:Android客户端
 * @Description: 设置
 */
public class VsSetingActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 拨打设置
     */
    private RelativeLayout vs_seting_call;
    /**
     * 自动接听
     */
    private RelativeLayout vs_seting_zdjt;
    /**
     * 来显设置
     */
    private RelativeLayout vs_seting_cid;
    /**
     * 声音设置
     */
    private RelativeLayout vs_seting_voice;
    /**
     * 修改密码
     */
    private RelativeLayout vs_seting_pwd;
    /**
     * 退出登录
     */
    /**
     * 改绑手机
     */
    private RelativeLayout vs_seting_change;
    private RelativeLayout vs_seting_exit;
    private RelativeLayout vs_seting_backup;
    private RelativeLayout vs_keytone_set;

    /**
     * 有版本更新
     */
    private TextView vs_about_update_tv;
    /**
     * 更新
     */
    private RelativeLayout vs_about_update;
    private RelativeLayout vs_about_help;
    private RelativeLayout vs_customer_help;
    /**
     * 更新检测
     */
    private static final char MSG_ID_SendUpgradeMsg = 71;
    private View setting_line1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_setting_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initView();
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
        mTitleTextView.setText(R.string.seting_title);
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    /**
     * 初始化控件视图
     */
    private void initView() {
        // 获取控件对象
        vs_seting_call = (RelativeLayout) findViewById(R.id.vs_seting_call);
        vs_seting_zdjt = (RelativeLayout) findViewById(R.id.vs_seting_zdjt);
        vs_seting_cid = (RelativeLayout) findViewById(R.id.vs_seting_cid);
        vs_seting_voice = (RelativeLayout) findViewById(R.id.vs_seting_voice);
        vs_seting_pwd = (RelativeLayout) findViewById(R.id.vs_seting_pwd);
        vs_seting_exit = (RelativeLayout) findViewById(R.id.vs_seting_exit);
        vs_seting_backup = (RelativeLayout) findViewById(R.id.vs_seting_backup);
        vs_seting_change = (RelativeLayout) findViewById(R.id.vs_change_phone);
        vs_keytone_set = (RelativeLayout) findViewById(R.id.vs_keytone_set);
        vs_about_help = (RelativeLayout) findViewById(R.id.vs_about_help);
        vs_customer_help = (RelativeLayout) findViewById(R.id.vs_customer_help);
        vs_about_update_tv = (TextView) findViewById(R.id.vs_about_update_tv);
        vs_about_update = (RelativeLayout) findViewById(R.id.vs_about_update);
        setting_line1 = (View) findViewById(R.id.setting_line1);

        // 检测更新
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
            // vs_about_update_tv.setVisibility(View.VISIBLE);
            // updateFlag = true;
        } else {
            vs_about_update_tv.setText(R.string.upgrade_is_a_new);
            vs_about_update_tv.setTextColor(getResources().getColor(R.color.update_new_color));
            // updateFlag = false;
        }

        String call_type = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_dial_types, "");
        try {
            JSONObject js = new JSONObject(call_type);
            String type = (String) js.get("type");
            String def = (String) js.get("default");
            if ("all".equals(type)) {
                vs_seting_call.setVisibility(View.VISIBLE);
                setting_line1.setVisibility(View.VISIBLE);
            } else {
                vs_seting_call.setVisibility(View.GONE);
                setting_line1.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * if (VsUserConfig.getDataString(mContext,
		 * VsUserConfig.JKey_UpgradeUrl).length() > 5) {
		 * iv_upgrade.setVisibility(View.VISIBLE); }
		 */

        // 加载消息中心信息[本地保存的消息]
        VsNotice.loadNoticeData(mContext);

        // 设置监听事件
        vs_seting_call.setOnClickListener(this);
        vs_seting_zdjt.setOnClickListener(this);
        vs_seting_cid.setOnClickListener(this);
        vs_seting_voice.setOnClickListener(this);
        vs_seting_pwd.setOnClickListener(this);
        vs_seting_exit.setOnClickListener(this);
        vs_seting_backup.setOnClickListener(this);
        vs_seting_change.setOnClickListener(this);
        vs_keytone_set.setOnClickListener(this);
        vs_about_update.setOnClickListener(this);
        vs_about_help.setOnClickListener(this);
        vs_customer_help.setOnClickListener(this);

    }

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_SendUpgradeMsg:
                dismissProgressDialog();
                if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {

                    showYesNoDialog("升级提示", "有新版本，确定升级？", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            startUpdateApk();
                        }
                    }, null, null);
                } else {
                    mToast.show("您的" + DfineAction.RES.getString(R.string.product) + "已是最新版本，无需升级！", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    /**
     * 开始更新
     */
    private void startUpdateApk() {
        VsUpdateAPK mUpdateAPK = new VsUpdateAPK(mContext);
        // mUpdateAPK.DowndloadThread(KcUserConfig.getDataString(mContext,
        // KcUserConfig.JKey_UpgradeUrl), true);
        mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl), true, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_seting_call:// 拨打设置
                startActivity(mContext, VsCallTypeSetingActivity.class);
                break;
            case R.id.vs_seting_zdjt:
                Intent zdjt_intent = new Intent(mContext, VsSetingVoiceHangActivity.class);
                zdjt_intent.putExtra("type", "hang");
                startActivity(zdjt_intent);
                break;
            case R.id.vs_seting_cid:
                startActivity(mContext, VsCallerIdentificationActivity.class);
                break;
            case R.id.vs_seting_voice:
                Intent v_intent = new Intent(mContext, VsSetingVoiceHangActivity.class);
                v_intent.putExtra("type", "voice");
                startActivity(v_intent);
                break;
            case R.id.vs_seting_pwd:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    startActivity(mContext, VsSetPhoneActivity.class);
                }
                break;
            case R.id.vs_seting_exit:
                if(CheckLoginStatusUtils.isLogin()) {
                    showYesNoDialog(null, getResources().getString(R.string.vs_setiong_exit_dialog_hint), getResources().getString(R.string.ok), getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, "");
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

                            // 清空个人信息资料
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Nickname, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Gender, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Birth, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_Province, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_City, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_MyInfo_MailBox, "");
                            VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, "");
                            // 注销
                            // 关闭通话服务
                            startActivity(new Intent(VsSetingActivity.this, VsLoginActivity.class));

                            //注销客服
//                            SobotApi.exitSobotChat(mContext);
                            finish();
                        }
                    }, null, null);
                }else {
                    mToast.show("您目前尚未登录，请先登录");
                }
                break;
            case R.id.vs_seting_backup:
                startActivity(mContext, VsContactBackUpActivity.class);
                break;
            case R.id.vs_change_phone:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    startActivity(mContext, VsSetPhoneActivity.class);
                }
                break;
            case R.id.vs_keytone_set:
                startActivity(mContext, VsPersonalizedActivity.class);
                break;
            case R.id.vs_about_help:// 帮助
                VsUtil.startActivity("3019", mContext, null);
                break;
            case R.id.vs_customer_help:// 关于我们
                startActivity(mContext, KcQcodeActivity.class);
                break;
            case R.id.vs_about_update:// 升级
                loadProgressDialog(getResources().getString(R.string.upgrade_checking_version));
                mBaseHandler.sendEmptyMessageDelayed(MSG_ID_SendUpgradeMsg, 1000);
                // if (updateFlag) {
                // loadProgressDialog(getResources().getString(R.string.upgrade_checking_version));
                // mBaseHandler.sendEmptyMessageDelayed(MSG_ID_SendUpgradeMsg,
                // 1000);
                // }
                // else {
                // mBaseHandler.sendEmptyMessage(MSG_ID_SendUpgradeMsg);
                // }
                break;
            default:
                break;
        }
    }
}
