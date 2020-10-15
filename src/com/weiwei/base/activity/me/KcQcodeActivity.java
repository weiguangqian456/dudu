package com.weiwei.base.activity.me;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.home.fragment.UpDataNotV4Dialog;
import com.weiwei.salemall.utils.FitStateUtils;
import com.weiwei.weibo.WeiboShareWebViewActivity;

/**
 * @Title:Android客户端
 * @Description:关于界面
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class KcQcodeActivity extends VsBaseActivity implements OnClickListener {
    /**
     * 服务电话电话号码
     */
    private TextView vs_about_server_phone, vs_about_server_gphone, vs_about_computer_wap, vs_about_phone_wap, vs_about_kfsj_tv, vs_about_bq_tv, vs_about_weixin_tv,
            vs_about_weibo_tv;
    /**
     * 服务电话号码
     */
    private String callNumber, phone_wap, com_wap, serviceTime, copyRight, wechatPublic, sinaWeibo, logoName, qq;
    /**
     * 服务QQ
     */
    private TextView vs_about_server_qq;
    /**
     * 版本号
     */
    private TextView vs_vesion, vs_vesionName;
    /**
     * 有版本更新
     */
    private TextView vs_about_update_tv;
    /**
     * 服务条款
     */
    private RelativeLayout vs_about_fw;

    /**
     * 更新
     */
    private RelativeLayout vs_about_update;
    /**
     * 帮助中心
     */
    private RelativeLayout vs_about_help;
    /**
     * 意见反馈
     */
    private RelativeLayout vs_about_fk;
    /**
     * 友盟用户反馈
     */
//	private FeedbackAgent agent = null;
    /**
     * 升级标志
     */
    private boolean updateFlag = false;
    /**
     * 更新检测
     */
    private static final char MSG_ID_SendUpgradeMsg = 71;
    /**
     * 最后一根线
     */
    private View line_end;
    /**
     * 评价
     */
    private RelativeLayout vs_about_pj;
    private ImageView contacslButton;
    private RelativeLayout vs_about_weibo, vs_about_weixin, vs_about_kfqq, vs_about_kfdh, vs_about_kfsj, vs_about_bq;
    private View view1, view2, view3, view4, view5, view6, view7, view8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_about_layout);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        // 实例化友盟用户反馈
//		agent = new FeedbackAgent(mContext);
        // 启动用户反馈通知
//		agent.sync();
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.icon_back);
        mTitleTextView.setText(R.string.my_tv10);
        init();
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 初试化
     */
    @SuppressLint("NewApi")
    private void init() {

        vs_about_weibo = (RelativeLayout) findViewById(R.id.vs_about_weibo);
        vs_about_weixin = (RelativeLayout) findViewById(R.id.vs_about_weixin);
        vs_about_kfqq = (RelativeLayout) findViewById(R.id.vs_about_kfqq);
        vs_about_kfdh = (RelativeLayout) findViewById(R.id.vs_about_kfdh);
        vs_about_kfsj = (RelativeLayout) findViewById(R.id.vs_about_kfsj);
        vs_about_bq = (RelativeLayout) findViewById(R.id.vs_about_bq);

        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        view3 = (View) findViewById(R.id.view3);
        view4 = (View) findViewById(R.id.view4);
        view5 = (View) findViewById(R.id.view5);
        view6 = (View) findViewById(R.id.view6);

        vs_about_server_phone = (TextView) findViewById(R.id.vs_about_server_phone);
        vs_about_server_gphone = (TextView) findViewById(R.id.vs_about_server_gphone);
        vs_about_server_qq = (TextView) findViewById(R.id.vs_about_server_qq);
        vs_vesion = (TextView) findViewById(R.id.vs_vesion);
        contacslButton = (ImageView) findViewById(R.id.contacslButton);
        vs_vesionName = (TextView) findViewById(R.id.vs_vesionName);
        vs_about_fw = (RelativeLayout) findViewById(R.id.vs_about_fw);
        vs_about_update_tv = (TextView) findViewById(R.id.vs_about_update_tv);
        vs_about_update = (RelativeLayout) findViewById(R.id.vs_about_update);
        vs_about_help = (RelativeLayout) findViewById(R.id.vs_about_help);
        vs_about_fk = (RelativeLayout) findViewById(R.id.vs_about_fk);
        line_end = findViewById(R.id.line_end);
        vs_about_pj = (RelativeLayout) findViewById(R.id.vs_about_pj);
        vs_about_computer_wap = (TextView) findViewById(R.id.vs_about_computer_wap);
        vs_about_phone_wap = (TextView) findViewById(R.id.vs_about_phone_wap);
        vs_about_kfsj_tv = (TextView) findViewById(R.id.vs_about_kfsj_tv);
        vs_about_bq_tv = (TextView) findViewById(R.id.vs_about_bq_tv);
        vs_about_weixin_tv = (TextView) findViewById(R.id.vs_about_weixin_tv);
        vs_about_weibo_tv = (TextView) findViewById(R.id.vs_about_weibo_tv);
        if (GlobalVariables.SDK_VERSON > 13) {
            vs_about_server_qq.setTextIsSelectable(true);
        }
        // 设置版本号
//		vs_vesion.setText(getVersion());
        vs_vesionName.setText(getVersion());


//		VsUserConfig.setData(mContext, VsUserConfig.JKey_ServiceTime,
//				VsJsonTool.GetStringFromJSON(kefuInfo, "ServiceTime"));
//		VsUserConfig.setData(mContext, VsUserConfig.JKey_CopyRight,
//				VsJsonTool.GetStringFromJSON(kefuInfo, "CopyRight"));
//		VsUserConfig.setData(mContext, VsUserConfig.JKey_WechatPublic,
//				VsJsonTool.GetStringFromJSON(kefuInfo, "WechatPublic"));
//		VsUserConfig.setData(mContext, VsUserConfig.JKey_SinaWeibo,
//				VsJsonTool.GetStringFromJSON(kefuInfo, "SinaWeibo"));
//		VsUserConfig.setData(mContext, VsUserConfig.JKey_LogoName,
//						VsJsonTool.GetStringFromJSON(kefuInfo, "LogoName"));

        callNumber = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServicePhone);
        qq = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServiceQQ);
        com_wap = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_computer_wap);
        phone_wap = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_phone_wap);

        serviceTime = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServiceTime);
        copyRight = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_CopyRight);
        wechatPublic = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_WechatPublic);
        sinaWeibo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_SinaWeibo);
        logoName = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_LogoName);
        vs_vesion.setText(logoName);
        if (com_wap != null && com_wap.length() > 0) {
            vs_about_computer_wap.setText(com_wap);
        } else {
            vs_about_computer_wap.setText(DfineAction.com_wap);
        }
        if (phone_wap != null && phone_wap.length() > 0) {
            vs_about_phone_wap.setText(phone_wap);
        } else {
            vs_about_phone_wap.setText(DfineAction.phone_wap);
        }
        if (qq != null && qq.length() > 0) {
            vs_about_server_qq.setText(qq);
            vs_about_kfqq.setVisibility(View.VISIBLE);
            view3.setVisibility(View.VISIBLE);
        } else {
            vs_about_server_qq.setText(R.string.curstomer_server_qq);
            vs_about_kfqq.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
        }
        if (serviceTime != null && serviceTime.length() > 0) {
            vs_about_kfsj_tv.setText(serviceTime);
            vs_about_kfsj.setVisibility(View.VISIBLE);
            view5.setVisibility(View.VISIBLE);
        } else {
            vs_about_kfsj_tv.setText(R.string.curstomer_server_time);
            vs_about_kfsj.setVisibility(View.GONE);
            view5.setVisibility(View.GONE);
        }
        if (copyRight != null && copyRight.length() > 0) {
            vs_about_bq_tv.setText(copyRight);
            vs_about_bq.setVisibility(View.VISIBLE);
            view6.setVisibility(View.VISIBLE);
        } else {
            vs_about_bq_tv.setText(R.string.curstomer_server_copyright);
            vs_about_bq.setVisibility(View.GONE);
            view6.setVisibility(View.GONE);
        }
        if (wechatPublic != null && wechatPublic.length() > 0) {
            vs_about_weixin_tv.setText(wechatPublic);
            vs_about_weixin.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
        } else {
            vs_about_weixin_tv.setText(R.string.curstomer_server_wechat);
            vs_about_weixin.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }
        if (sinaWeibo != null && sinaWeibo.length() > 0) {
            vs_about_weibo_tv.setText(sinaWeibo);
            vs_about_weibo.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
        } else {
            vs_about_weibo_tv.setText(R.string.curstomer_server_sina);
            vs_about_weibo.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }
        if (callNumber != null && callNumber.length() > 0) {
            //vs_about_server_phone.setText(Html.fromHtml("<u>" + callNumber + "</u>"));
            vs_about_server_phone.setText(callNumber);
            vs_about_kfdh.setVisibility(View.VISIBLE);
            view4.setVisibility(View.VISIBLE);
        } else {
            //vs_about_server_phone.setText(Html.fromHtml("<u>" + DfineAction.mobile + "</u>"));
            vs_about_server_phone.setText(R.string.curstomer_server_mobile);
            vs_about_kfdh.setVisibility(View.GONE);
            view4.setVisibility(View.GONE);
        }
        vs_about_server_phone.setOnClickListener(this);
        vs_about_server_gphone.setOnClickListener(this);
        vs_about_server_qq.setOnClickListener(this);
        vs_about_update.setOnClickListener(this);
        vs_about_help.setOnClickListener(this);
        vs_about_fk.setOnClickListener(this);
        vs_about_pj.setOnClickListener(this);
        vs_about_fw.setOnClickListener(this);

        // 检测更新
        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.HAS_NEW_VERSION, false)) {
            vs_about_update_tv.setVisibility(View.VISIBLE);
            vs_about_update_tv.setText("有新版本");
            vs_about_update_tv.setTextColor(getResources().getColor(R.color.vs_gray_light));
            updateFlag = true;
        } else {
            vs_about_update_tv.setText("版本" + getVersion());
            vs_about_update_tv.setTextColor(getResources().getColor(R.color.vs_gray_deep));
            updateFlag = false;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_about_server_phone:
                //客服电话
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt3), mContext)) {

                    if (callNumber.startsWith("1") && callNumber.length() == 13) {
                        showYesNoDialog(null, "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打客服热线", DfineAction.RES.getString(R.string.product) + "拨打",
                                getResources().getString(R.string.phone_call), new OkBtnListener(callNumber), new CancelBtnListener(callNumber), null);
                    } else {
                        VsUtil.LocalCallNumber(mContext, callNumber);
                    }
                }
                break;

            case R.id.vs_about_server_gphone:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt3), mContext)) {
                    String gcallNumber = "0755-23775895";
                    if (gcallNumber.startsWith("1") && gcallNumber.length() == 13) {
                        showYesNoDialog(null, "您可以选择" + DfineAction.RES.getString(R.string.product) + "电话或本地手机拨打客服热线", DfineAction.RES.getString(R.string.product) + "拨打",
                                getResources().getString(R.string.phone_call), new OkBtnListener(gcallNumber), new CancelBtnListener(gcallNumber), null);
                    } else {
                        VsUtil.LocalCallNumber(mContext, gcallNumber);
                    }
                }
                break;
            case R.id.vs_about_server_qq:
                //客服QQ
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
                Intent intent_qq = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (intent_qq.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent_qq);
                } else {
                    Toast.makeText(KcQcodeActivity.this, "未安装QQ", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.vs_about_update:
                //版本更新
                if (!updateFlag) {
                    loadProgressDialog(getResources().getString(R.string.upgrade_checking_version));
                    mBaseHandler.sendEmptyMessageDelayed(MSG_ID_SendUpgradeMsg, 1000);
                } else {
                    mBaseHandler.sendEmptyMessage(MSG_ID_SendUpgradeMsg);
                }
                break;
            case R.id.vs_about_help:
                VsUtil.startActivity("3019", mContext, null);
                break;
            case R.id.vs_about_fk:
//			agent.startFeedbackActivity();
                break;
            case R.id.vs_about_fw:
                //服务条款
                final String urlTo = "file:///android_asset/service_terms.html";
                Intent intent_fw = new Intent();
                intent_fw.setClass(mContext, WeiboShareWebViewActivity.class);
                String[] aboutBusiness = new String[]{mContext.getString(R.string.welcome_main_clause), "service", urlTo};
                intent_fw.putExtra("AboutBusiness", aboutBusiness);
                startActivity(intent_fw);
                break;
            case R.id.vs_about_pj:
                //评价
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_SendUpgradeMsg:
                dismissProgressDialog();
                String newVersionCode = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version);
                String currentVersionName = getVersion();
                String currentVersionCode = currentVersionName.substring(1, currentVersionName.length());
                if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5 && !currentVersionCode.equals(newVersionCode)) {
                    FragmentManager fragmentManager = getFragmentManager();
                    if(fragmentManager != null){
                        UpDataNotV4Dialog
                                .getInstance()
                                .isUpMandatory(false)
                                .setVersion(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version))
                                .setContent(VsUserConfig.getDataString(mContext, VsUserConfig .JKey_UpgradeInfo))
                                .setUpDataListener(new UpDataNotV4Dialog.UpDataListener() {
                                    @Override
                                    public void onUpDataClick() {
                                        startUpdateApk();
                                    }
                                })
                                .show(fragmentManager,"");
                    }else{
                        showYesNoDialog("升级提示", "有新版本，确定升级？", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                startUpdateApk();
                            }
                        }, null, null);
                    }
                } else {
                    mToast.show("您的" + DfineAction.RES.getString(R.string.product) + "已是最新版本，无需升级！", Toast.LENGTH_SHORT);
                }
                break;
            default:
                break;
        }

    }

    private class OkBtnListener implements DialogInterface.OnClickListener {
        private String callNumber = "";

        public OkBtnListener(String callNumber) {
            this.callNumber = callNumber;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // finish();
            // KcUtil.skipForTarget("000", mContext, 0, null);
            // 拨打客服号码
            VsUtil.callNumber("客服电话", callNumber, "深圳", mContext, "", false,getFragmentManager());
            /*
             * Intent intent1 = new Intent(); intent1.putExtra("callNumber", callNumber);
			 * intent1.setAction(GlobalVariables.action_setcallphont); sendBroadcast(intent1);
			 */
        }
    }

    private class CancelBtnListener implements DialogInterface.OnClickListener {
        private String callNumber = "";

        public CancelBtnListener(String callNumber) {
            this.callNumber = callNumber;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            VsUtil.LocalCallNumber(mContext, callNumber);
        }
    }

    /**
     * 开始更新
     */
    private void startUpdateApk() {
        VsUpdateAPK mUpdateAPK = new VsUpdateAPK(mContext);
        // mUpdateAPK.DowndloadThread(KcUserConfig.getDataString(mContext, KcUserConfig.JKey_UpgradeUrl), true);
        mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl), true, null);
//        mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version_pgyer_url, ""), true, null);
    }

    /**
     * 判断是否登录
     *
     * @param
     * @return
     */
    protected boolean isLogin() {
        boolean retbool = true;
        if (!VsUtil.checkHasAccount(mContext)) {
            retbool = false;
        } else {
            retbool = true;
        }
        return retbool;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = VsApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(VsApplication.getContext().getPackageName(), 0);
            String version = info.versionName;
            return "V" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return VsApplication.getContext().getString(R.string.version_unkown);
        }
    }

    public static String getVersionCode() {
        try {
            PackageManager manager = VsApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(VsApplication.getContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return VsApplication.getContext().getString(R.string.version_unkown);
        }
    }
}
