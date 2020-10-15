package com.weiwei.base.activity.me;

import java.util.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.setting.VsSetingActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsNotice;
import com.weiwei.base.fragment.FragmentIndicator;
import com.weiwei.base.fragment.VsDialFragment;
import com.weiwei.base.util.BadgeView;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;
import com.hwtx.dududh.R;

/**
 * @Description: 我界面
 * @author: 黄发兴
 * @Date: 2014-10-20
 */
public class VsMyselfActivity extends VsBaseActivity implements OnClickListener {

    public BadgeView badge = null;
    private RelativeLayout rl_my_account, rl_invite_friends, rl_small_vs,
            rl_vs_setting, vs_myself_about, rl_recharge, rl_sigin, vs_keytone_set, rl_voice;
    private ImageView iv_red_dot;
    public static ImageView iv_yellow_new;
    private TextView iv_upgrade;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private VsDialFragment vsDialFragment;

    /**
     * 帮助中心
     */
    private RelativeLayout vs_about_help;
    /**
     * 更新检测
     */
    private static final char MSG_ID_SendUpgradeMsg = 71;
    /**
     * 标志
     */
    private boolean updateFlag = false;
    /**
     * 有版本更新
     */
    private TextView vs_about_update_tv;
    /**
     * 更新
     */
    private RelativeLayout vs_about_update;
    /**
     * 是否进入余额界面
     */
    private boolean isToBalance = false;
    /**
     * 余额
     */
    private TextView my_balance_tv;
    /**
     * 二维码
     */
    private RelativeLayout vs_myselft_qcodelayout;
    /**
     * 显示手机号或者邮箱
     */
    private TextView vs_myselft_account;
    private final char MYSELF_SPEAKER_1 = 100;
    /**
     * 加载邀请人数成功（并且数量大于存储的数量）
     */
    private final char MYSELF_INVITE_1 = 200;
    /**
     * 加载邀请人数失败
     */
    private final char MYSELF_INVITE_2 = 201;
    private final char MYSELF_RICH_MESSAGE_3 = 300;
    private final char MYSELF_UPDATE_4 = 400;
    /**
     * 查询余额成功
     */
    private final char MYSELF_SEARCH_BALANCE_SUCC = 401;
    /**
     * 查询余额失败
     */
    private final char MYSELF_SEARCH_BALANCE_FAIL = 402;
    /**
     * 第一次点击事件
     */
    private long startTime = 0;
    /**
     * 余额
     */
    private String saveBalance = null;
    private android.app.FragmentManager fragmentManager1;
    private VsDialFragment fragment;
    public FragmentIndicator.OnIndicateListener onIndicateListener = new FragmentIndicator.OnIndicateListener() {
        @Override
        public void onIndicate(View v, int which) {

        }
    };
    private android.app.FragmentManager fragmentManager2;

    /**
     * Create a new instance of DetailsFragment, initialized to show the text at 'index'.
     */

    /*	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		View view = inflater.inflate(R.layout.fragment_myself, container, false);
    		return view;
    	}
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myself);
        fragmentManager2 = getFragmentManager();
        initDate();
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭

    }

    protected void HandleRightNavBtn() {
        if (VsUtil.isLogin(mContext.getResources().getString(R.string.recommend_friends_prompt), mContext)) {
            // 处理右导航按钮事件
            VsUtil.startActivity(3010 + "", mContext, null);
            if (badge != null) {
                badge.hide();
            }
        }
    }

    /**
     * 接收查询
     */
    private BroadcastReceiver show_balance = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (VsUserConfig.JKEY_SEARCH_BALANCE.equals(action)) {// 进入我界面广播
                long nowTime = System.currentTimeMillis();
                long splitTime = 2 * 60 * 1000;
                if ((nowTime - startTime) > splitTime) {
                    CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_BALANCE_MY, DfineAction.authType_UID, null, GlobalVariables.action_search_balance_do);
                    startTime = nowTime;
                } else {
                    if (saveBalance != null) {
                        my_balance_tv.setText(saveBalance + "元");
                    }
                }
            } else if (GlobalVariables.action_search_balance_do.equals(action)) {// 查询余额完成广播
                Message msg = mBaseHandler.obtainMessage();
                Bundle bundle = new Bundle();
                try {
                    String jStr = intent.getStringExtra(GlobalVariables.VS_KeyMsg);
                    JSONObject jData = new JSONObject(jStr);
                    String retStr = jData.getString("result");
                    if ("0".equals(retStr)) {
                        bundle.putString("balance", jData.getString("balance"));
                        msg.setData(bundle);
                        msg.what = MYSELF_SEARCH_BALANCE_SUCC;
                    } else {
                        bundle.putString("msg", jData.getString("reason"));
                        msg.setData(bundle);
                        msg.what = MYSELF_SEARCH_BALANCE_FAIL;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    bundle.putString("msg", "查询失败！");
                    msg.setData(bundle);
                    msg.what = MYSELF_SEARCH_BALANCE_FAIL;
                }
                mBaseHandler.sendMessage(msg);
            }
        }
    };

    public void initDate() {

        // 初始化title
        initTitleNavBar();

        // 高亮红点和文字提醒
        badge = new BadgeView(mContext, mBtnNavRight);
        Drawable drawable = getResources().getDrawable(R.drawable.vs_red_dot);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        badge.setCompoundDrawables(drawable, null, null, null);
        badge.setBackgroundResource(android.R.color.transparent);
        badge.setBadgePosition(badge.POSITION_TOP_RIGHT);
        initTitleNavBar();
        showLeftNavaBtn(R.drawable.vs_title_back_selecter);
        // 注册查询余额广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(VsUserConfig.JKEY_SEARCH_BALANCE);
        filter.addAction(GlobalVariables.action_search_balance_do);
        mContext.registerReceiver(show_balance, filter);
        vs_about_update_tv = (TextView) findViewById(R.id.vs_about_update_tv);
        vs_about_update = (RelativeLayout) findViewById(R.id.vs_about_update);
        rl_voice = (RelativeLayout) findViewById(R.id.rl_voice);

        // title
        mTitleTextView.setText("个人中心");

        // 我的基本信息
        vs_myselft_qcodelayout = (RelativeLayout) findViewById(R.id.vs_myselft_qcodelayout);
        // 个人基本信息，帐号
        vs_myselft_account = (TextView) findViewById(R.id.vs_myselft_account);
        vs_myselft_account.setText(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        // 我的话费
        rl_my_account = (RelativeLayout) findViewById(R.id.rl_my_account);
        my_balance_tv = (TextView) findViewById(R.id.my_balance_tv);
        // 赚话费-邀请好友
//        rl_invite_friends = (RelativeLayout) findViewById(R.id.rl_invite_friends);
//        // 充值
//        rl_recharge = (RelativeLayout) findViewById(R.id.rl_recharge);
//        //签到
//        rl_sigin=(RelativeLayout) findViewById(R.id.rl_sigin_tow);
//        // 设置
//        rl_vs_setting = (RelativeLayout) findViewById(R.id.rl_vs_setting);
//        // 关于
//        vs_myself_about = (RelativeLayout) findViewById(R.id.vs_myself_about);
//        iv_upgrade = (TextView) findViewById(R.id.iv_upgrade);
//        // 高亮红点和文字提醒
//        iv_red_dot = (ImageView) findViewById(R.id.iv_red_dot);
        vs_about_help = (RelativeLayout) findViewById(R.id.vs_about_help);
        /**
         * 按键音
         */
        vs_keytone_set = (RelativeLayout) findViewById(R.id.vs_keytone_set);
        vs_about_help.setOnClickListener(this);

        vs_myselft_qcodelayout.setOnClickListener(this);
        rl_my_account.setOnClickListener(this);
        rl_invite_friends.setOnClickListener(this);
        rl_recharge.setOnClickListener(this);
        rl_voice.setOnClickListener(this);
        rl_small_vs.setOnClickListener(this);
        rl_vs_setting.setOnClickListener(this);
        vs_myself_about.setOnClickListener(this);
        vs_about_update.setOnClickListener(this);
        rl_sigin.setOnClickListener(this);
        vs_keytone_set.setOnClickListener(this);

        // 检测更新
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
            vs_about_update_tv.setVisibility(View.VISIBLE);
            updateFlag = true;
        } else {
            vs_about_update_tv.setText("已是最新");
            vs_about_update_tv.setTextColor(getResources().getColor(R.color.vs_gray_deep));
            updateFlag = false;
        }

        /* if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
             iv_upgrade.setVisibility(View.VISIBLE);
         }*/

        // 加载消息中心信息[本地保存的消息]
        VsNotice.loadNoticeData(mContext);

        // 获取上报错误日志标准
        VsBizUtil.getInstance().reportControl(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (show_balance != null) {
            mContext.unregisterReceiver(show_balance);
        }
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_my_account:
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.login_prompt2), mContext)) {
                    isToBalance = true;
                    startActivity(mContext, VsBalanceActivity.class);
                }
                // startActivity(mContext, KcMsgInviteActivity.class);
                /*
                 * if (KcNetWorkTools.isNetworkAvailable(mContext)) {
                 * if(KcUtil.isLogin(mContext.getResources().getString(R.string.recommend_friends_prompt), mContext)){
                 * KcUtil.startActivity("3005", mContext); } } else { Toast.makeText(mContext,
                 * R.string.not_network_connon_msg, Toast.LENGTH_SHORT).show(); }
                 */

                // MobclickAgent.onEvent(mContext, "Wo_Accout");
                break;
//        case R.id.rl_invite_friends:
//            // 邀请好友
//            if (VsUtil.isLogin(mContext.getResources().getString(R.string.recommend_friends_prompt), mContext)) {
//                startActivity(mContext, VsMakeMoneyActivity.class);
//                if (iv_red_dot.getVisibility() == View.VISIBLE) {
//                    iv_red_dot.setVisibility(View.GONE);
//                }
//            }
//            break;
//        case R.id.rl_recharge:
//            // 充值
//            //if (VsUtil.isLogin(mContext.getResources().getString(R.string.recommend_friends_prompt), mContext)) {
//            startActivity(mContext, VsRechargeActivity.class);
//            //    if (iv_red_dot.getVisibility() == View.VISIBLE) {
//            iv_red_dot.setVisibility(View.GONE);
//            //    }
//            //}
//            //TODO:need add
//            //MobclickAgent.onEvent(mContext, "My_MakeCalls");
//            break;
            case R.id.rl_sigin_tow:
                startActivity(mContext, VsSignInFirstActivity.class);
                break;
//        case R.id.rl_vs_setting:
//            // 设置
//            Intent intent = new Intent(mContext, VsSetingActivity.class);
//            startActivity(intent);
//            break;
//        case R.id.vs_myself_about:// 关于
//            Intent intent2 = new Intent(mContext, VsAboutActivity.class);
//            startActivity(intent2);
//            break;
            case R.id.vs_myselft_qcodelayout:// 进入二维码界面
                //  startActivity(new Intent(mContext, VsQRCodeActivity.class));
                break;
            case R.id.vs_about_help://帮助
                VsUtil.startActivity("3019", mContext, null);
                break;
            case R.id.vs_keytone_set:
                VsUtil.startActivity("3035", mContext, null);
                break;
            case R.id.rl_voice:   //语音通话
                if (VsUtil.isLogin(mContext.getResources().getString(R.string.nologin_auto_hint), mContext)) {
                    //注意v4包的配套使用
//                  fragment = new VsDialFragment();
//                fragmentManager2.beginTransaction()
//                          .addToBackStack(null)
//                          .replace(R.id.realtabcontent,fragment)
//                          .commit();
//                    onIndicateListener.onIndicate(v, 2);
//
//                    Log.d("onIndicateListener执行了", "onClick: " + onIndicateListener);
//                    setIndicator(2);

//                    Intent intent=new Intent();
//                    intent.setClass(VsMyselfActivity.this, VsMainActivity.class);
//                    startActivity(intent);

                }



                break;

            case R.id.vs_about_update://升级
                if (!updateFlag) {
                    loadProgressDialog(getResources().getString(R.string.upgrade_checking_version));
                    mBaseHandler.sendEmptyMessageDelayed(MSG_ID_SendUpgradeMsg, 1000);
                } else {
                    mBaseHandler.sendEmptyMessage(MSG_ID_SendUpgradeMsg);
                }
                break;

        }

    }

    private void setIndicator(int which) {
        switch (which){
            case 2:
               // Log.d("which+++++++", "setIndicator: "+which);
                break;
        }
    }

    Timer time;

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MYSELF_SPEAKER_1:
                break;
            case MYSELF_INVITE_1:
                iv_red_dot.setVisibility(View.VISIBLE);
                break;
            case MYSELF_INVITE_2:
                iv_red_dot.setVisibility(View.GONE);
                break;
            case MYSELF_RICH_MESSAGE_3:
                break;
            case MYSELF_UPDATE_4:
                break;
            case MSG_ID_SendUpgradeMsg:
                dismissProgressDialog();
                if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {
                    startUpdateApk();
                } else {
                    mToast.show("您的" + DfineAction.RES.getString(R.string.product) + "已是最新版本，无需升级！", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    /**
     * 查余额
     */
    public void searchBalance() {
        // 执行异步操作
        CustomLog.i("vsdebug", "查余额被调用了");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 开始更新
     */
    private void startUpdateApk() {
        VsUpdateAPK mUpdateAPK = new VsUpdateAPK(mContext);
        // mUpdateAPK.DowndloadThread(KcUserConfig.getDataString(mContext, KcUserConfig.JKey_UpgradeUrl), true);
        mUpdateAPK.NotificationDowndload(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl), true, null);
    }

}
