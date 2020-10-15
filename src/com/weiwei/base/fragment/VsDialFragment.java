package com.weiwei.base.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.weiwei.base.adapter.VsCallLogAdapter;
import com.weiwei.base.adapter.VsDialListAdapter;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.common.VsUpdateAPK;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalFuntion;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.service.VsCoreService;
import com.weiwei.base.util.CustomAlertDialog;
import com.weiwei.base.util.VsCallLogManagement;
import com.weiwei.base.widgets.TitleBarView;
import com.weiwei.home.fragment.UpDataDialog;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.zte.functions.ad.AdData;
import com.zte.functions.ad.AdManager;
import com.zte.functions.ad.AdWidget;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拨号Fragment
 * <p>
 * 显示通话记录与拨号
 * </p>
 */
@SuppressLint("NewApi")
public class VsDialFragment extends VsBaseFragment implements OnClickListener {
    private TitleBarView mTitleBarView;
    private TitleBarView mTitleBarView2;
    private ArrayList<View.OnClickListener> click_listener;
    public static boolean isall = true; // 是否输入搜索
    public static boolean isedit = false; // 是否输入搜索
    public static boolean isup = true; // 是否是所有通话记录
    private CustomAlertDialog dialog;
    private static final int MSG_UPDATE_AD = 1000;
    private String TAG = VsDialFragment.class.getSimpleName();
    /**
     * <p>
     * 配置wifi/3G/4G网络下拨打方式的Dialog
     * </p>
     */
    private Dialog mDialogCallSetting;
    /**
     * 弹系统公告的Dialog
     */
    private Dialog mDialogSystemNotice;
    /**
     * @see #getView()
     * <p>
     * 即该Fragment视图
     * </p>
     */
    private View mParent;
    /**
     * 获取资源变量
     */
    private Resources mResource;
    /**
     * title_bar_dlog 判断网络状态
     */
    private LinearLayout ll_title_bar_dlog, ll_title_bar_dlog_1/*
     * , ll_title_bar_dlog_2
     */;
    private Button btn_title_bar_copy, btn_title_bar_clear;
    private LinearLayout calllog_stop;
    /**
     * 拨号盘
     */
    private LinearLayout mInputKeyboard;
    /**
     * 移动动画
     */
    private Animation mTranslateAnimation;
    /**
     * 缩放动画
     */
    private Animation mscaleAnimation;
    /**
     * 键盘移动动画
     */
    private Animation openKeyboardAnimation;
    private Animation closeKeyboardAnimation;

    public static VsDialListAdapter adapter = null;
    public static VsCallLogAdapter mListAdapter;
    private TextView tv_network_change; // 未输入电话号码时，显示公告 / 网络状态切换
    private ImageView pri_image;
    private ImageView discuss_image;
    private FragmentManager supportFragmentManager;

    /**
     * viewpager
     */
    // private VsViewPagerWidget mCalllogNullPro;
    private ImageView calllog_no_p, calllog_no_p1;
    private ListView mListView, calllog_listView;
    // private Animation closeKeyboardAnimation1;
    private String mActivityState = "valid"; // ACTIVITY 状态(valid,invalid两种)
    private TextView ll_title_bar;
    private Object mToneGeneratorLock = new Object();
    private ToneGenerator mToneGenerator;
    private InputMethodManager imm;
    private AudioManager audioManager;
    private static final char OPERATION_NETWORK_INVALID = 1; // 网络不可用
    private static final char MSG_PROGRESS_QUERY_CALLLOG = 2;// 加载通话记录
    private static final char MSG_PROGRESS_LOADING_COMPLETION = 3;// 加载框
    /**
     * 关于回拨是否显示
     */
    private boolean istshowSetingAbout = false;
    /**
     * 加载通话记录方法
     */
    private static final char MSG_PROGRESS_QUERY_CALLLOG_do = 200;
    /**
     * 切换TAB改变清空编辑框数据
     */
    public static final char MSG_CLOSEUSERLEAD = 103;
    /**
     * 切换TAB改变清空编辑框数据
     */
    public static final char DIAL_PHONE = 109;

    /**
     * 显示网络状态变化
     */
    public static final char NETWORK_CHANGE = 104;
    /**
     * 关系网络状态提醒
     */
    public static final char NETWORK_GONE = 106;
    /**
     * 查询归属地
     */
    public static final char MSG_ID_SEARCH_LOCAL = 118;
    /**
     * 拨打电话广播
     */
    public static final char MSG_DIAL_PHONE = 107;
    private static long countdown_time = 0L;// 可免费拨打时间
    private static boolean mRegAwardSwitch = false;

    private ProgressDialog mProgressBar;
    public static boolean searchInput = false;// 是否输入搜索
    private boolean changeRegTime = false;// 已经在修改注册赠送时间了
    private String CHANGE_REG_TIME = "change_reg_time";
    private static final int DIALOG_NETWORK_INVALID = 0; // 网络不可用
    /**
     * 所有通话记录、未接来电选择Dialog
     */
    private Dialog mDialogSelect;
    /**
     * 设置拨号输入框为空
     */
    public static final String RECEIVER_SET_EDITTECT_NULL = "RECEIVER_SET_EDITTECT_NULL";
    public EditText mTitleTextView;

    /**
     * 键盘是否打开(默认打开)
     */
    private static boolean isopen = true;

    private final int AUTOREGISTER_TOAST = 7;
    String mActivityStatu = "valid";// 程序退出还弹公告BUG
    /**
     * 拨号按钮是否显示
     */
    public static boolean isShow = true;
    /**
     * 系统公告移动动画
     */
    private View v1 = null;
    /**
     * 是否是粘贴内容
     */
    private boolean isCopy = false;
    /**
     * 发送打开拨号按钮广播
     */
    private Intent open_call_btn;
    /**
     * 发送关闭拨号按钮广播
     */
    private Intent close_call_btn;
    /**
     * 发送是否显示两个按钮广播
     */
    private String callNumber = "";
    /**
     * 连续删除数
     */
    private int scannelNumber = 0;
    /**
     * 输入内容长度
     */
    private int oldLength = 0;
    /**
     * 是否为删除
     */
    private boolean flag = false;
    /**
     * 输入监听器
     */
    private PhoneNumberTextWatcher phoneTextWatcher = null;
    /**
     * 归属地
     */
    private TextView localName;

    /**
     * 广告
     */
    private AdWidget mAdWidget;
    /**
     * 主界面
     */
    private VsMainActivity vsMain;
    private Window dialogWindow = null;
    private WindowManager.LayoutParams lp = null;
    /**
     * 标记服务器是否需要显示广告
     */
    private boolean isNeedShowAd = false;
    private boolean isInit = false;
    private String ad_conf_str;
    private LinearLayout vs_mian_keyborad_layout;
    private ImageView vs_mian_keyborad_btn;
    private TextView vs_mian_keyborad_tv;
    private boolean isFirst;

    /**
     * Create a new instance of DetailsFragment, initialized to show the text at
     * 'index'.
     */
    public static VsDialFragment newInstance(int index) {
        VsDialFragment f = new VsDialFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CustomLog.i(TAG, "MainFragment------onCreateView(),...");
        long time = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_dial, container, false);

        //如果是用的v4的包，则用getActivity().getSuppoutFragmentManager();
        supportFragmentManager = getActivity().getSupportFragmentManager();
        CustomLog.i(TAG, "inflate(), end : " + (System.currentTimeMillis() - time));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.vsMain = (VsMainActivity) activity;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        init();
        if (!hidden) {
            long time = System.currentTimeMillis();
            CustomLog.i(TAG, "init(), end : " + (System.currentTimeMillis() - time));
            setDefaultUi();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CustomLog.i(TAG, "MainFragment------onActivityCreated(),...");
    }

    private void init() {
//        setDefaultUi();
        if (isInit) {
            return;
        }
        isInit = true;
        CustomLog.i(TAG, "MainFragment++++++init(),...");
        mParent = getView();
        if (getActivity() == null) {
            return;
        }
        mResource = getActivity().getResources();
        mBaseHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                handleBaseMessage(msg);
                return false;
            }
        });
        // kcCallBack.setFlag(true);
        callNumber = mContext.getIntent().getStringExtra("callNumber");
        long time = System.currentTimeMillis();
        initView();
        initTitle();
        initCalllogDialog();
        CustomLog.i(TAG, "initView(), end : " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        // addAd();
        CustomLog.i(TAG, "addAd(), end : " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        // 默认导入通话记录
        loadCallLog();
        CustomLog.i("calllog", "DialFramgent 拉取通话记录 loadCallLog(), end : " + (System.currentTimeMillis() - time));
    }

    /**
     * 设置默认ui(默认显示全部通话界面)
     */
    private void setDefaultUi() {
        mTitleBarView = (TitleBarView) getActivity().findViewById(R.id.title_tar);
        Button callBtn = mTitleBarView.getCallBtn();
        Button contactBtn = mTitleBarView.getContactBtn();
        mTitleBarView.setCalllogStopShow();
        mTitleBarView.setImageRightHide();
        callBtn.setTextColor(getResources().getColor(R.color.public_color_EC6941));
        callBtn.setBackground(getResources().getDrawable(R.drawable.leftbtn_select_bg));
        contactBtn.setTextColor(getResources().getColor(R.color.whites));
        contactBtn.setBackground(getResources().getDrawable(R.drawable.rightbtn_nomal_bg));
    }

    private void initView() {
        calllog_no_p = (ImageView) mParent.findViewById(R.id.calllog_no_p);
        ad_conf_str = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_AD_CONFIG_1001);
        initSlide(mParent, ad_conf_str, "1001", false);
        if (ad_conf_str.length() > 1) {
            isNeedShowAd = true;
            // 显示后台广告
            getView().findViewById(R.id.slid_title).setVisibility(View.VISIBLE);
            // 隐藏本地广告
            calllog_no_p.setVisibility(View.GONE);
            initSlide(mParent, ad_conf_str, "1001", false);
        } else {
            // 隐藏后台广告
            getView().findViewById(R.id.slid_title).setVisibility(View.GONE);
            // 显示本地广告
            calllog_no_p.setVisibility(View.VISIBLE);
        }

        open_call_btn = new Intent(GlobalVariables.action_open_call_btn);
        close_call_btn = new Intent(GlobalVariables.action_close_call_btn);
        initTitleNavBar(mParent);
        // 根据得到的设置来判断右边的字
        mTitleTextView.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        // showRightNavaBtn(R.drawable.vs_calltype_set_calllong_selecter);
        // mCalllogNullPro = (VsViewPagerWidget) mParent
        // .findViewById(R.id.calllog_null_pro);

        calllog_no_p1 = (ImageView) mParent.findViewById(R.id.calllog_no_p1);
        // mCalllogNullPro.getVs_calllog_btn().setOnClickListener(this);
        // mCalllogNullPro.getViewPager().setOnTouchListener(
        // new OnTouchListener() {
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // // TODO Auto-generated method stub
        // if (MotionEvent.ACTION_DOWN == event.getAction()) {
        // if (isopen) {
        // openOrCloseKeyboard(false);
        // }
        // }
        // return false;
        // }
        // });

        // 电话号码
        setTitleText();
        // 匹配输入结果的listview
        mListView = (ListView) mParent.findViewById(R.id.listview);
        // 通话记录的listview
        calllog_listView = (ListView) mParent.findViewById(R.id.calllog_list);

        new DisplayCallLogTask().execute();
        /* 设置并显示来电号码 */
        if (!"".equals(GlobalVariables.dialPhoneNumber)) {
            String tel = VsUtil.getValidPhoneNumber(mContext, GlobalVariables.dialPhoneNumber);

            if (tel != null) {
                mTitleTextView.setInputType(InputType.TYPE_NULL); // 关闭软键盘
                mTitleTextView.setText(tel);
                mTitleTextView.requestFocus();
                GlobalVariables.dialPhoneNumber = "";
            }
            // String tel = KcUtil.getValidPhoneNumber(mContext,
            // GlobalVariables.dialPhoneNumber);
            //
            // if (tel != null) {
            // GlobalVariables.dialPhoneNumber = "";
            // }
        }
        // mAdWidget = (AdWidget) mParent.findViewById(R.id.ad_widget);
        mInputKeyboard = (LinearLayout) mParent.findViewById(R.id.vs_input_keyboard);
        if (openKeyboardAnimation == null) {
            openKeyboardAnimation = new TranslateAnimation(0, 0, 518.5f * GlobalVariables.density, 0);// 移动
            openKeyboardAnimation.setDuration(300);
        }
        if (closeKeyboardAnimation == null) {
            closeKeyboardAnimation = new TranslateAnimation(0, 0, 0, 518.5f * GlobalVariables.density);// 移动
            closeKeyboardAnimation.setDuration(300);
        }
        openKeyboardAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInputKeyboard.setVisibility(View.VISIBLE);
                // if (mListView.getVisibility() != View.VISIBLE) {
                // showAd();
                // } else {
                // closeAd();
                // }
                showAd();
                calllog_listView.setVisibility(View.GONE);
            }
        });
        closeKeyboardAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                closeAd();
                calllog_listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInputKeyboard.setVisibility(View.INVISIBLE);// 不用GONE是为了防止某种异常下，键盘未弹起，广告却显示而且拉伸了。

            }
        });

        // 初始化拨打设置弹窗
        initAnimation();
        ll_title_bar = (TextView) mParent.findViewById(R.id.ll_title_bar);
        ll_title_bar.setOnClickListener(this);
        // 特殊状况弹窗
        ll_title_bar_dlog = (LinearLayout) mParent.findViewById(R.id.ll_title_bar_dlog);
        ll_title_bar_dlog_1 = (LinearLayout) mParent.findViewById(R.id.ll_title_bar_dlog_1);
        // ll_title_bar_dlog_2 = (LinearLayout)
        // mParent.findViewById(R.id.ll_title_bar_dlog_2);
        btn_title_bar_copy = (Button) mParent.findViewById(R.id.btn_title_bar_copy);
        btn_title_bar_clear = (Button) mParent.findViewById(R.id.btn_title_bar_clear);
        tv_network_change = (TextView) mParent.findViewById(R.id.tv_network_change);
        localName = (TextView) mParent.findViewById(R.id.localName);
        btn_title_bar_copy.setOnClickListener(this);
        btn_title_bar_clear.setOnClickListener(this);

        mParent.findViewById(R.id.DigitHideButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitDeleteButton).setOnClickListener(this);
        // mParent.findViewById(R.id.DigitDeleteButton).setOnLongClickListener(
        // new View.OnLongClickListener() {
        //
        // @Override
        // public boolean onLongClick(View v) {
        // setTitleText();
        // return false;
        // }
        // });
        mParent.findViewById(R.id.DigitOneButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitTwoButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitThreeButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitFourButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitFiveButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitSixButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitSevenButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitEightButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitNineButton).setOnClickListener(this);
        mParent.findViewById(R.id.DigitZeroButton).setOnClickListener(this);
        // 特殊的"0"
        mParent.findViewById(R.id.DigitZeroButton).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                keyPressed(KeyEvent.KEYCODE_PLUS);
                return false;
            }
        });
        // 拨号盘收起开关
        mParent.findViewById(R.id.DigitHideButton).setOnClickListener(this);
        // 删除键
        mParent.findViewById(R.id.DigitDeleteButton).setOnClickListener(this);
        phoneTextWatcher = new PhoneNumberTextWatcher();
        mTitleTextView.addTextChangedListener(phoneTextWatcher);
        adapter = new VsDialListAdapter(mContext);
        mListView.setAdapter(adapter);

        // 拉取系统公告
        mContext.registerReceiver(SysMsgReceiver, new IntentFilter(GlobalVariables.actionSysMsg));
        IntentFilter netChangeFilter = new IntentFilter();
        netChangeFilter.addAction(GlobalVariables.action_net_change);
        netChangeFilter.addAction(GlobalVariables.action_no_network);
        netChangeFilter.addAction(VsUserConfig.JKey_GET_VSUSER_OK);
        netChangeFilter.addAction(GlobalVariables.action_dial_phone);
        mContext.registerReceiver(actionNetChange, netChangeFilter);
        // KcBizUtil.getInstance().getSysMsg(mContext);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalVariables.action_loadcalllog_succ);
        filter.addAction(DfineAction.ACTION_REGSENDMONEY);
        mContext.registerReceiver(inituserleadlReceiver, filter);
        // 切换TAB改变清空编辑框数据
        //  mContext.registerReceiver(clear_edit, new IntentFilter(VsUserConfig.JKey_CLOSE_USER_LEAD));
        IntentFilter ift = new IntentFilter();
        ift.addAction(DfineAction.ACTION_DIAL_CALL);
        ift.addAction(DfineAction.ACTION_SHOW_CALLLOG);
        ift.addAction(DfineAction.ACTION_UPDATE_CALLLOG);
        ift.addAction(VsUserConfig.VS_ACTION_LOGIN_LOADING_DIAL);
        ift.addAction(VsUserConfig.JKey_GET_VSUSER_OK);
        ift.addAction(VsUserConfig.JKey_GET_VSUSER_FAIL);
        mContext.registerReceiver(diaOrShowCallLogBr, ift);
        initTonePlayer();
        // 隐藏输入法
        hideDigitsIM();

        mContext.registerReceiver(setEdittectNullReceiver, new IntentFilter(RECEIVER_SET_EDITTECT_NULL));
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


//                    openOrCloseKeyboard(false);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


//                    openOrCloseKeyboard(false);

            }
        });
        calllog_listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


//                    openOrCloseKeyboard(false);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 在item的底部不打开的时候，滑动清空存储的点击内容（防止关闭item底部内容后，滑动再次打开的bug）
                if (mListAdapter != null && !mListAdapter.isshow) {
                    mListAdapter.mOpenItem.clear();
                }


//                    openOrCloseKeyboard(false);

            }
        });

        setShowNewPeople();

        isopen = true;
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, isopen);
        update();
//		update_fir();// fir升级提醒
        PgyUpdateManager.register(VsDialFragment.this.getActivity(), new UpdateManagerListener() {

            @Override
            public void onUpdateAvailable(final String result) {
                CustomLog.d("onUpdateAvailable", "有新版本");
                VsUserConfig.setData(mContext, VsUserConfig.HAS_NEW_VERSION, true);
                // 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);
                VsUserConfig.setData(mContext, VsUserConfig.JKey_new_version_pgyer, appBean.getVersionName());
                VsUserConfig.setData(mContext, VsUserConfig.JKey_new_version_pgyer_url, appBean.getDownloadURL());
                if (IsNeedupgradeForTipsNumber(false)) {
                    showYesNoDialog("检测到新版本" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version), VsUserConfig.getDataString(mContext, VsUserConfig
                            .JKey_UpgradeInfo, "发现新版本，是否更新?"), "暂不升级", "现在升级", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startDownloadTask(VsDialFragment.this.getActivity(), appBean.getDownloadURL());
                        }
                    }, null, null);

                }

            }

            @Override
            public void onNoUpdateAvailable() {
                CustomLog.d("onUpdateAvailable", "没有新版本");
                VsUserConfig.setData(mContext, VsUserConfig.JKey_new_version_pgyer, "");
                VsUserConfig.setData(mContext, VsUserConfig.HAS_NEW_VERSION, false);
            }
        });
    }

    public void initTitle() {

        // 长按事件的处理
        isup = true;
        mTitleBarView = (TitleBarView) getActivity().findViewById(R.id.title_tar);
        // mTitleBarView.setVisibility(View.GONE);
        mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
        // mTitleBarView.setBtnRight(R.drawable.skin_conversation_title_right_btn);
        // mTitleBarView.setTxtRight(R.string.vs_dial_clog_edit);

        mTitleBarView.setLayoutBackOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
             /*     VsMyselfFragment  fragment = new VsMyselfFragment();
                supportFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fl_back, fragment)
                        .show(fragment)
                        .commit();*/

               /* Intent intent = new Intent();
                intent.putExtra("indicator", 4);
                intent.setClass(getActivity(), VsMainActivity.class);
                startActivity(intent);*/
                vsMain.showFragment(4);
                vsMain.setView(4, false);
                vsMain.setFragmentIndicator(4);

            }
        });
        mTitleBarView.setLayoutCalllogOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();// 所有通话、未接来电
                setcalllogSel(isup);
                mTitleBarView.setIMG(R.drawable.vs_calllog_up);
            }
        });
        // 编辑
        mTitleBarView.getImageRight().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                changEdit_edit();
            }
        });

        // 完成   编辑的点击事件
        mTitleBarView.getTxtRight().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (mTitleBarView.getTxtRight().getText().toString().equals("完成")) {
                    Log.d("bianji", "onClick完成: " + "进来了");
//                    changEdit_end();    //编辑
                    if (isopen == true && mTitleBarView.getTxtRight().getText().equals("完成")) {
                        openOrCloseKeyboard(true);
                        mTitleBarView.getBtnLeft().setVisibility(View.GONE);
                        mTitleBarView.getTxtRight().setVisibility(View.GONE);
                        isedit=false;

                    }else {
                        openOrCloseKeyboard(false);
                        mTitleBarView.getBtnLeft().setVisibility(View.VISIBLE);
                        mTitleBarView.getTxtRight().setVisibility(View.VISIBLE);
                        isedit=true;
                    }
                } else if (mTitleBarView.getTxtRight().getText().toString().equals("编辑")) {
                    Log.d("bianji", "onClick编辑: " + "进来了");
                    isedit=true;
                    changEdit_edit();    //完成

                }
            }
        });
        // mTitleBarView.setImageRightShow();
        mTitleBarView.setTxtRightHide();
        mTitleBarView.getTxtRight().setText("编辑");
        // mTitleBarView.getTxtRight().setVisibility(View.VISIBLE);
        mTitleBarView.getBtnLeft().setVisibility(View.INVISIBLE);
        mTitleBarView.getRightBtn().setVisibility(View.INVISIBLE);
        mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);
        mTitleBarView.setTitleLeft(R.string.vs_dial_clog_all);
        mTitleBarView.setTitleRight(R.string.vs_dial_clog_miss);
        mTitleBarView.getTitleLeft().setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleLeft().isEnabled()) {
                    mTitleBarView.getTitleLeft().setEnabled(false);
                    mTitleBarView.getTitleRight().setEnabled(true);
                    isall = true;

                    isedit = false;
                    changEdit(true);
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsDialFragment
                     * newsFragment = new VsDialFragment();
                     * ft.replace(R.id.child_fragment, newsFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */
                    new DisplayCallLogTask().execute();
                }
            }
        });

        mTitleBarView.getTitleRight().setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleRight().isEnabled()) {
                    mTitleBarView.getTitleLeft().setEnabled(true);
                    mTitleBarView.getTitleRight().setEnabled(false);
                    isall = false;
                    isedit = false;
                    changEdit(true);
                    new DisplayCallLogTask().execute();
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsMyselfFragment
                     * callFragment = new VsMyselfFragment();
                     * ft.replace(R.id.child_fragment, callFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsDialFragment
                     * newsFragment = new VsDialFragment();
                     * ft.replace(R.id.child_fragment, newsFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */

                }

            }
        });
        // 左边清除通话记录
        mTitleBarView.getBtnLeft().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                VsDialFragment.refresh();
                openOrCloseKeyboard(false);
                // TODO Auto-generated method stub
                click_listener = new ArrayList<View.OnClickListener>();
                // 删除通话记录
                click_listener.add(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (VsUtil.isFastDoubleClick()) {
                            return;
                        }
                        if (isall) {
                            VsPhoneCallHistory.delAllCallLog(mContext);
                        } else {
                            VsPhoneCallHistory.delCallLogBYtype(mContext, null, "3");

                        }
                        dialog.dismiss();
                    }
                });
                // 取消
                click_listener.add(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog = VsUtil.showChoose(mContext, "清除", 6, click_listener);
            }
        });

        mTitleBarView.getTitleLeft().performClick();

        final Button callBtn = mTitleBarView.getCallBtn();
        final Button contactBtn = mTitleBarView.getContactBtn();
        final ImageView addIvBtn = mTitleBarView.getImageRight();
        vs_mian_keyborad_layout = (LinearLayout) vsMain.findViewById(R.id.vs_mian_keyborad_layout);
        vs_mian_keyborad_btn = (ImageView) vsMain.findViewById(R.id.vs_mian_keyborad_btn);
        vs_mian_keyborad_tv = (TextView) vsMain.findViewById(R.id.vs_mian_keyborad_tv);
        vs_mian_keyborad_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    VsDialFragment.refresh();
                    Log.d("isShow++++++", "onClick:最近通话 " + isShow);
                    vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_blue_down);
                    vs_mian_keyborad_tv.setText("最近通话");
                    openOrCloseKeyboard(isopen);
                    isShow = false;

                    //切换成最近通话   右上角显示编辑按钮
//                    if (mTitleBarView.getTxtRight().getText().toString().equals("编辑")) {
                    changEdit_end();
//                    } else {
//                        changEdit_edit();
//                        //  changEdit(false);
//                    }

                } else {
                    VsDialFragment.refresh();
                    Log.d("isShow++++++", "onClick:拨号 " + isShow);
                    vs_mian_keyborad_btn.setBackgroundResource(R.drawable.ic_dial_focused_blue_up);
                    vs_mian_keyborad_tv.setText("拨号");
                    openOrCloseKeyboard(isopen);
                    isShow = true;
                    mTitleBarView.setTxtRightHide();

                }
            }
        });
        //全部通话
        callBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vsMain.findViewById(R.id.ll_mybottom).setVisibility(View.VISIBLE);
                vsMain.findViewById(R.id.rl_bottom).setVisibility(View.VISIBLE);
                callBtn.setTextColor(getResources().getColor(R.color.public_color_EC6941));
                callBtn.setBackground(getResources().getDrawable(R.drawable.leftbtn_select_bg));
                contactBtn.setTextColor(getResources().getColor(R.color.whites));
                contactBtn.setBackground(getResources().getDrawable(R.drawable.rightbtn_nomal_bg));
                mTitleBarView.setCalllogStopShow();

                mTitleBarView.setImageRightHide();
                vsMain.showFragment(FragmentIndicator.HOME_PAGE_PHONE);
            }
        });
        //通讯录
        contactBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                vsMain.findViewById(R.id.ll_mybottom).setVisibility(View.GONE);
                vsMain.findViewById(R.id.rl_bottom).setVisibility(View.VISIBLE);
                callBtn.setTextColor(getResources().getColor(R.color.whites));
                callBtn.setBackground(getResources().getDrawable(R.drawable.leftbtn_normal_bg));
                contactBtn.setTextColor(getResources().getColor(R.color.public_color_EC6941));
                contactBtn.setBackground(getResources().getDrawable(R.drawable.rightbtn_select_bg));
                mTitleBarView.setCalllogStopHide();

                mTitleBarView.getTxtRight().setVisibility(View.GONE);
                vsMain.showFragment(FragmentIndicator.HOME_PAGE_CONTACTS);
                mTitleBarView.setImageRightShow();
                addIvBtn.setImageDrawable(getResources().getDrawable(R.drawable.add_title));
                addIvBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VsUtil.addContact(mContext, "");
                    }
                });
            }
        });
    }

    public void changEdit(boolean back) {
        if (back) {
            isedit = false;
            VsDialFragment.refresh();
            // mTitleBarView.getTxtRight().setText("编辑");
            mTitleBarView.getBtnLeft().setVisibility(View.INVISIBLE);
            mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);       //清除
            // openOrCloseKeyboard(false);
        } else {
            if ("编辑".equals(mTitleBarView.getTxtRight().getText())) {
                isedit = true;
                VsDialFragment.refresh();
                mTitleBarView.getTxtRight().setText("完成");
                mTitleBarView.setImageRightHide();
                mTitleBarView.setTxtRightHide();
                mTitleBarView.getBtnLeft().setVisibility(View.VISIBLE);
                mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);
                openOrCloseKeyboard(false);
            } else if ("完成".equals(mTitleBarView.getTxtRight().getText())) {
                isedit = false;
                VsDialFragment.refresh();
                mTitleBarView.getTxtRight().setText("编辑");
                mTitleBarView.getBtnLeft().setVisibility(View.INVISIBLE);
                mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);
                openOrCloseKeyboard(false);
            }
        }

    }

    public void changEdit_edit() {                 //完成
        isedit = true;
        VsDialFragment.refresh();
        // mTitleBarView.setTxtRightHide();
//        if(mTitleBarView.getBtnLeft().getText().toString().contains("清除")){
//            openOrCloseKeyboard(false);
//        }

        mTitleBarView.setTxtRightShow();
        mTitleBarView.getTxtRight().setText("完成");
        mTitleBarView.setImageRightHide();
        mTitleBarView.getBtnLeft().setVisibility(View.VISIBLE);
        mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);
        openOrCloseKeyboard(false);


    }

    public void changEdit_end() {


        //编辑
        isedit = false;
        VsDialFragment.refresh();
        // mTitleBarView.setImageRightShow();
        // mTitleBarView.setImageRightHide();
        // mTitleBarView.setTxtRightHide();

        mTitleBarView.setTxtRightShow();
        mTitleBarView.getTxtRight().setText("编辑");
        mTitleBarView.getBtnLeft().setVisibility(View.INVISIBLE);
        mTitleBarView.setBtnLeft(R.string.vs_calllog_title_dial_del_all);
        openOrCloseKeyboard(false);


    }

    public static void refresh() {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }

    }

    public void setTtitlBar(boolean open) {
        if (open) {
            small_title.setVisibility(View.GONE);
            mTitleBarView.setVisibility(View.VISIBLE);
        } else {
            small_title.setVisibility(View.VISIBLE);
            mTitleBarView.setVisibility(View.GONE);
        }
    }

    public void setcalllogSel(boolean isup) {
        if (isup) {

            // mTitleBarView.setIMG(R.drawable.vs_calllog_up);
            // mTitleBarView.getIMG().setVisibility(View.GONE);

            pri_image.setVisibility(View.VISIBLE);
            discuss_image.setVisibility(View.INVISIBLE);
        } else {
            pri_image.setVisibility(View.INVISIBLE);
            discuss_image.setVisibility(View.VISIBLE);
        }
    }

    private void addAd() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                if (AdManager.getInstance().hasAd(VsDialFragment.this.getActivity(), AdManager.TYPE_DIAL)) {
                    CustomLog.i(TAG, "addAd(),....");
                    List<AdData> ads = AdManager.getInstance().getAd(VsDialFragment.this.getActivity(), AdManager.TYPE_DIAL);

                    if (null == ads || ads.size() == 0) {
                        isNeedShowAd = false;
                    } else {
                        isNeedShowAd = true;
                        msg.what = MSG_UPDATE_AD;
                        msg.obj = ads;
                    }
                } else {
                    isNeedShowAd = false;
                }
                mBaseHandler.sendMessage(msg);

            }
        };

        GlobalVariables.fixedThreadPool.execute(runnable);

    }

    /*
     * public void updateAd() { CustomLog.i(TAG, "updateAd(),...."); addAd(); }
     */

    private void showAd() {
        if (ad_conf_str.length() > 1) {
            mParent.findViewById(R.id.slid_title).setVisibility(View.VISIBLE);
        } else {
            mParent.findViewById(R.id.slid_title).setVisibility(View.GONE);
        }

        calllog_no_p1.setVisibility(View.VISIBLE);
        // calllog_listView.setVisibility(View.GONE);
        // mAdWidget.setVisibility(View.VISIBLE);
        // mAdWidget.startAutoScroll();
    }

    private void closeAd() {
        // mAdWidget.setVisibility(View.GONE);
        // mAdWidget.stopAutoScroll();
        mParent.findViewById(R.id.slid_title).setVisibility(View.GONE);
        calllog_no_p1.setVisibility(View.GONE);
        // calllog_listView.setVisibility(View.VISIBLE);
    }

    private void setShowNewPeople() {
        // 拉取系统公告
        VsBizUtil.getInstance().getSysMsg(mContext);
    }

    // 促销信息
    private void initUserLeadl() {
        mRegAwardSwitch = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_RegAwardSwitch, true);
        try {
            changeRegTime = VsUserConfig.getDataBoolean(mContext, CHANGE_REG_TIME, false);
            if (!changeRegTime) {
                countdown_time = Long.parseLong(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_RegSurplus));
                CustomLog.i("beifen", "countdown_time1111  ====" + countdown_time);
                VsUserConfig.setData(mContext, CHANGE_REG_TIME, true);
            } else {
                countdown_time = VsUserConfig.getDataLong(mContext, VsUserConfig.JKey_start_time) - (System.currentTimeMillis() / 1000);
                CustomLog.i("beifen", "countdown_time333333  ====" + countdown_time);
            }
        } catch (Exception e) {
            countdown_time = 0L;
        }
    }

    /**
     * 打开关闭键盘
     *
     * @param openOrClose
     */
    public void openOrCloseKeyboard(boolean openOrClose) {

        Log.d("openOrClose+++", "openOrCloseKeyboard: " + openOrClose);
        if (openOrClose == true) {
            isopen = false;
            mContext.sendBroadcast(open_call_btn);
            //vsMain.setTabImageFouse(isopen);
            mInputKeyboard.startAnimation(openKeyboardAnimation);
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, isopen);
            //     mTitleBarView.getTxtRight().setVisibility(View.GONE);


        } else {         //false
            isopen = true;
            mContext.sendBroadcast(close_call_btn);
            // vsMain.setTabImageFouse(isopen);
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_KEYBORD_IS_SHOW, isopen);
            mInputKeyboard.startAnimation(closeKeyboardAnimation);
            //    mTitleBarView.getTxtRight().setVisibility(View.VISIBLE);


        }
        // playTone(ToneGenerator.TONE_DTMF_7);
    }

    /**
     * 处理Handler消息
     *
     * @param msg
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void handleBaseMessage(Message msg) {
        switch (msg.what) {
            case MSG_ID_SEARCH_LOCAL:
                String number = mTitleTextView.getText().toString().replaceAll("-", "");
                if (number.length() > 3) {// 输入三位以上才查询归属地
                    String local = null;
                    local = VsLocalNameFinder.findLocalName(number, false, mContext);
                    if (local != null && !"".equals(local)) {
                        localName.setText(local);
                    }
                }
                break;
            case NETWORK_CHANGE:
                setNetWorkDlog();
                break;
            case NETWORK_GONE:
                setNetWorkDlogGone();
                break;
            case MSG_CLOSEUSERLEAD:
                //msg.getData().getBoolean("isopen")
                // iv_keyboard_open_or_close.setImageResource(R.drawable.kc_jianpan_img_select);
                int indicator = msg.getData().getInt("indicator");
                Log.d("indicator+++++++", "handleBaseMessage: " + indicator);
                if (msg.getData().getBoolean("isopen")) {
                    openOrCloseKeyboard(isopen);
                } else {
                    setTitleText();
                }
                break;


            case OPERATION_NETWORK_INVALID:
                // 避免activity 销毁时出现异常,由状态来控制是否显示
                if ("valid".equals(mActivityState)) {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().showDialog(DIALOG_NETWORK_INVALID);
                }
                break;
            case MSG_PROGRESS_LOADING_COMPLETION:
                if (mProgressBar != null) {
                    mProgressBar.dismiss();
                }
                break;
            case MSG_PROGRESS_QUERY_CALLLOG:
                CustomLog.d("calllog", "MSG_PROGRESS_QUERY_CALLLOG..............");
                if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, true)) {
                    // mCalllogNullPro.getVs_calllog_btn().setVisibility(View.GONE);
                    calllog_no_p.setVisibility(View.GONE);
                    // mCalllogNullPro.setVisibility(View.GONE);
                    // calllog_listView.setVisibility(View.VISIBLE);
                } else {
                    // mCalllogNullPro.getVs_calllog_btn().setVisibility(View.VISIBLE);

                }

                if (VsPhoneCallHistory.callLogs.size() == 0) {
                    // mCalllogNullPro.setVisibility(View.VISIBLE);
                    calllog_no_p.setVisibility(View.VISIBLE);
                    // calllog_listView.setVisibility(View.GONE);
                    GlobalFuntion.SendBroadcastMsg(mContext, VsUserConfig.JKEY_SHOW_HIDE_EDIT, "0");
                    CustomLog.i("fxing", "send broadca  0");
                } else {
                    GlobalFuntion.SendBroadcastMsg(mContext, VsUserConfig.JKEY_SHOW_HIDE_EDIT, "1");
                    CustomLog.i("fxing", "send broadca  1");
                }

                if (VsDialFragment.isall) {
                    VsCallLogManagement.copyStaticCallLogsToViewList(true);
                } else {
                    // mCalllogNullPro.getVs_calllog_btn().setVisibility(View.GONE);
                    // mCalllogNullPro.setVisibility(View.GONE);
                    VsCallLogManagement.copyStaticCallLogsToViewList(false);
                    if (VsPhoneCallHistory.callLogViewList.size() == 0) {
                        GlobalFuntion.SendBroadcastMsg(mContext, VsUserConfig.JKEY_SHOW_HIDE_EDIT, "0");
                    } else {
                        GlobalFuntion.SendBroadcastMsg(mContext, VsUserConfig.JKEY_SHOW_HIDE_EDIT, "1");
                    }
                }
                mListAdapter = new VsCallLogAdapter(mContext);
                calllog_listView.setAdapter(mListAdapter);
                calllog_listView.setDivider(null);
                mListAdapter.notifyDataSetChanged();
                CustomLog.i(TAG, "VsPhoneCallHistory.callLogs.SIZE ===" + VsPhoneCallHistory.callLogs.size() + "   \nVsPhoneCallHistory.callLogViewList.size====" +
                        VsPhoneCallHistory.callLogViewList.size());
                break;
            case AUTOREGISTER_TOAST:
                // 只有新人礼包弹出后，才弹出系统公告
                if ("valid".equals(mActivityStatu)) {
                    String button_text = msg.getData().getString("button_text");
                    String title = msg.getData().getString("title");
                    String content = msg.getData().getString("content");
                    String redirect_target = msg.getData().getString("redirect_target");
                    String redirect_type = msg.getData().getString("redirect_type");
                    String v = msg.getData().getString("v");
                    String status = msg.getData().getString("status");
                    String id = msg.getData().getString("id");
                    String pv = msg.getData().getString("pv");
                    String level = msg.getData().getString("level");
                    String flag = msg.getData().getString("flag");

                    if (content != null && mContext != null) {
                        setMSGRead(id);
                    }
                    // String title = msg.getData().getString("title");// 公告标题
                    // String str = (String) msg.getData().get("context");// 公告内容
                    // String button_text = (String)
                    // msg.getData().get("button_text");
                    // final String id = (String) msg.getData().get("id");// 公告ID
                    //
                    // if (str != null && mContext != null) {
                    // if (id.indexOf(",") != -1) {
                    // String ids[] = id.split(",");
                    // for (int j = 0; j < ids.length; j++) {
                    // setMSGRead(ids[j]);
                    // }
                    // } else {
                    // setMSGRead(id);
                    // }
                    // String btn_title = msg.getData().getString(
                    // "redirect_btn_text");// 按钮标题
                    // String type = msg.getData().getString("redirect_type");//
                    // 跳转类型
                    // String target = msg.getData().getString("redirect_target");//
                    // 跳转地址
                    // String url = msg.getData().getString("url");// 跳转url
                    // if (target == null || "".equals(target)) {
                    // target = url;
                    // }
                    // if (url == null || "".equals(url)) {
                    // url = target;
                    // }
                    showSystemDialog(title, content, button_text, redirect_type, redirect_target, redirect_target);
                    // }
                }
                break;
            case MSG_DIAL_PHONE:
                callPhone();
                break;
            case MSG_PROGRESS_QUERY_CALLLOG_do:
                VsPhoneCallHistory.loadCallLog();
                break;
            case MSG_UPDATE_AD:
                if (isNeedShowAd) {
                    List<AdData> ads = (List<AdData>) msg.obj;
                    if (mAdWidget.getChildCount() > 0) {
                        mAdWidget.removeAllViews();
                        mAdWidget.stopAutoScroll();
                    }
                    mAdWidget.addAdData(ads);
                    mAdWidget.isNeedPointBg(true);
                    mAdWidget.startAutoScroll(mBaseHandler);
                }

                if (mInputKeyboard.getVisibility() == View.VISIBLE) {
                    showAd();
                } else {
                    closeAd();
                }
                break;
        }

    }

//
//    /**
//     * 定义一个接口
//     */
//    public interface  onListener{
//        void OnListener(boolean isOpen);
//    }
//    /**
//     *定义一个变量储存数据
//     */
//    private onListener listener;
//    /**
//     *提供公共的方法,并且初始化接口类型的数据
//     */
//    public void setListener( onListener listener){
//        this.listener = listener;
//    }


    /**
     * 公告跳转
     *
     * @param type    跳转类型
     * @param msglink 内部跳转
     * @param url     外部跳转
     */
    private void systemNotice(final String type, final String title, final String msglink, String url, Dialog dialog) {
        // 跳转类型 wap：内部浏览器打开 web：外部浏览器打开 page：内部界面
        dialog.cancel();
        if (type.equals("page")) {// 内部页面
            VsUtil.skipForTarget(url, mContext, 0, null);
        } else if (type.equals("wap")) {// 内部浏览器
            VsUtil.schemeToWap(url, title, mContext);
        } else {// 外部浏览器
            VsUtil.schemeToWeb(url, mContext);
        }
    }

    /**
     * 显示系统公告
     *
     * @param title_str   公告标题
     * @param content_str 公告内容
     * @param btn_text    公告btn
     * @param type        跳转类型
     * @param target      内部跳转
     * @param url         外部跳转
     */
    public void showSystemDialog(String title_str, String content_str, String btn_text, final String type, final String target, final String url) {
        // title, content, button_text, redirect_type, redirect_target,
        // redirect_target
        if (mDialogSystemNotice != null && mDialogSystemNotice.isShowing()) {
            return;
        }
        try {
            v1 = initAnimation(title_str, content_str, type, btn_text, target, url);
            v1.startAnimation(mTranslateAnimation);
            mDialogSystemNotice.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block 这里有个崩溃问题在测试小米手机上
            e.printStackTrace();
        }
    }

    private View initAnimation(final String title_str, String content_str, final String type, String butText, final String target, final String url) {
        mDialogSystemNotice = new Dialog(mContext, R.style.CommonDialogStyle);
        final View v = View.inflate(mContext, R.layout.system_dlog, null);
        TextView title = (TextView) v.findViewById(R.id.tv_dlog_title);
        TextView content = (TextView) v.findViewById(R.id.tv_dlog_content);
        title.setText(title_str);
        content.setText(content_str);
        Button btn_obtain = (Button) v.findViewById(R.id.btn_dlog_obtain);
        btn_obtain.setText(butText);
        btn_obtain.setOnClickListener(new View.OnClickListener() {
            // title, content, button_text, redirect_type, redirect_target,
            // redirect_target
            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                systemNotice(type, title_str, target, url, mDialogSystemNotice);
            }
        });
        mDialogSystemNotice.setContentView(v);
        mDialogSystemNotice.setCanceledOnTouchOutside(true);
        mDialogSystemNotice.setCancelable(true);
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = mDialogSystemNotice.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
         * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        if (GlobalVariables.width == 0) {
            // 获取屏幕宽高与密度
            VsUtil.setDensityWH(mContext);
        }
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = GlobalVariables.width; // 宽度

        if (getActivity() == null) {
            return null;
        }
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        int www = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int hhh = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(www, hhh);
        int bottomLineWidth = v.getMeasuredHeight() + (int) (55 * GlobalVariables.density) + 20;
        lp.height = LayoutParams.WRAP_CONTENT; // 高度
        lp.alpha = 1f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        mTranslateAnimation = new TranslateAnimation(0, 0, -bottomLineWidth, 0);// 移动
        mTranslateAnimation.setDuration(500);
        return v;
    }

    /**
     * 显示拨打设置
     *
     * @param show 是否显示
     */
    public void showDialogCallSetting(boolean show) {
        if (show) {
            mDialogCallSetting.show();
        } else {
            if (mDialogCallSetting != null && mDialogCallSetting.isShowing()) {
                // dialog.hide();
                mDialogCallSetting.dismiss();
            }
        }
    }

    /**
     * 初始化拨打设置弹出框
     */
    private void initAnimation() {
        mDialogCallSetting = new Dialog(mContext, R.style.CommonDialogStyle);
        final View v = View.inflate(mContext, R.layout.vs_setting_calltype_dialog, null);
        final ImageView wifiCallback = (ImageView) v.findViewById(R.id.vs_calltype_dialong_wifi);// wifi下回拨
        final ImageView callback_3g4g = (ImageView) v.findViewById(R.id.vs_calltype_dialong_3g_4g);// 3g4g下回拨
        final TextView wifiCallback_tv = (TextView) v.findViewById(R.id.vs_calltype_dialong_wifi_tv);
        final TextView callback_3g4g_tv = (TextView) v.findViewById(R.id.vs_calltype_dialong_3g_4g_tv);
        final TextView aboutCallback = (TextView) v.findViewById(R.id.vs_calltype_callback_tv);
        final TextView aboutCallback_hint = (TextView) v.findViewById(R.id.vs_calltype_callback_tv_hint);
        // 了解回拨内容默认关闭
        aboutCallback_hint.setVisibility(View.GONE);

        // wifi下回拨--默认关闭
        boolean wifiCallback_state = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true);
        // 3g、4g下回拨--默认开启
        boolean callback_state_3g_4g = VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true);

        // 获取wifi下回拨状态
        if (wifiCallback_state) {
            wifiCallback.setBackgroundResource(R.drawable.vs_wifi_gree_selecter);
            wifiCallback_tv.setText(getString(R.string.vs_calltype_open_hint));
            wifiCallback_tv.setTextColor(getResources().getColor(R.color.vs_gree));
        } else {
            wifiCallback.setBackgroundResource(R.drawable.vs_wifi_gray_selecter);
            wifiCallback_tv.setText(getString(R.string.vs_calltype_close_hint));
            wifiCallback_tv.setTextColor(getResources().getColor(R.color.vs_black));
        }
        // 获取3g、4g回拨状态
        if (callback_state_3g_4g) {
            callback_3g4g.setBackgroundResource(R.drawable.vs_3g_4g_gree_selecter);
            callback_3g4g_tv.setText(getString(R.string.vs_calltype_open_hint));
            callback_3g4g_tv.setTextColor(getResources().getColor(R.color.vs_gree));
        } else {
            callback_3g4g.setBackgroundResource(R.drawable.vs_3g_4g_gray_selecter);
            callback_3g4g_tv.setText(getString(R.string.vs_calltype_close_hint));
            callback_3g4g_tv.setTextColor(getResources().getColor(R.color.vs_black));
        }

        wifiCallback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true)) {
                    wifiCallback.setBackgroundResource(R.drawable.vs_wifi_gree_selecter);
                    wifiCallback_tv.setText(getString(R.string.vs_calltype_open_hint));
                    wifiCallback_tv.setTextColor(getResources().getColor(R.color.vs_gree));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_WIFI_CALLBACK, true);
                } else {
                    wifiCallback.setBackgroundResource(R.drawable.vs_wifi_gray_selecter);
                    wifiCallback_tv.setText(getString(R.string.vs_calltype_close_hint));
                    wifiCallback_tv.setTextColor(getResources().getColor(R.color.vs_black));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_WIFI_CALLBACK, false);
                }
            }
        });
        callback_3g4g.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true)) {
                    callback_3g4g.setBackgroundResource(R.drawable.vs_3g_4g_gree_selecter);
                    callback_3g4g_tv.setText(getString(R.string.vs_calltype_open_hint));
                    callback_3g4g_tv.setTextColor(getResources().getColor(R.color.vs_gree));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, true);
                } else {
                    callback_3g4g.setBackgroundResource(R.drawable.vs_3g_4g_gray_selecter);
                    callback_3g4g_tv.setText(getString(R.string.vs_calltype_close_hint));
                    callback_3g4g_tv.setTextColor(getResources().getColor(R.color.vs_black));
                    VsUserConfig.setData(mContext, VsUserConfig.JKey_3G_4G_CALLBACK, false);
                }
            }
        });
        int myHeight = (int) getResources().getDimension(R.dimen.setting_calltype_height);
        aboutCallback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (istshowSetingAbout) {
                    int myHeight = (int) getResources().getDimension(R.dimen.setting_calltype_height);
                    aboutCallback_hint.setVisibility(View.GONE);
                    istshowSetingAbout = false;
                    lp.height = myHeight; // 高度
                    dialogWindow.setAttributes(lp);
                } else {
                    int myHeight = (int) getResources().getDimension(R.dimen.setting_calltype_height_long);
                    aboutCallback_hint.setVisibility(View.VISIBLE);
                    istshowSetingAbout = true;
                    lp.height = myHeight; // 高度
                    dialogWindow.setAttributes(lp);
                }
            }
        });
        mDialogCallSetting.setContentView(v);
        mDialogCallSetting.setCanceledOnTouchOutside(true);
        mDialogCallSetting.setCancelable(true);

        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        dialogWindow = mDialogCallSetting.getWindow();
        /**
         * 设置窗口动画 update by:黄文武
         */
        dialogWindow.setWindowAnimations(R.style.vs_call_setting_dialog_show);

        lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
         * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        if (GlobalVariables.width == 0) {
            // 获取屏幕宽高与密度
            VsUtil.setDensityWH(mContext);
        }
        lp.x = 0; // 新位置X坐标
        lp.y = (int) (50 * GlobalVariables.density); // 新位置Y坐标
        lp.width = GlobalVariables.width; // 宽度
        lp.height = myHeight; // 高度
        lp.alpha = 1f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
    }

    /**
     * 拨号盘按钮处理
     */

    @Override
    public void onClick(View v) {
        isCopy = false;
        switch (v.getId()) {
            case R.id.DigitHideButton:
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                /*
                 * setTitleText(); String content = ""; try { // SDK小于11导入 if
                 * (GlobalVariables.SDK_VERSON > 11) { // // 复制粘贴板内容
                 * ClipboardManager clipboard = (ClipboardManager) mContext
                 * .getSystemService(Context.CLIPBOARD_SERVICE); content =
                 * clipboard.getText() != null ? clipboard.getText()
                 * .toString().trim() : ""; } else { android.text.ClipboardManager
                 * clipboard = (android.text.ClipboardManager) mContext
                 * .getSystemService(Context.CLIPBOARD_SERVICE); content =
                 * clipboard.getText() != null ? clipboard.getText()
                 * .toString().trim() : ""; } if (content.toString().startsWith(
                 * "intent:#Intent;S.K_1171477665")) { CustomLog.i("beifen",
                 * content.toString()); content = ""; } // 是否为空 if
                 * (!VsUtil.isNull(content)) { isCopy = true; // 在粘贴板后面加一位
                 * mTitleTextView.setText(content + "1"); // 把光标移动到最后
                 * mTitleTextView.setSelection(content.length() + 1); //
                 * 删除最后一位（这一步只是为了调用keyPressed()方法，进行T9搜索）
                 * keyPressed(KeyEvent.KEYCODE_DEL); if (VsUtil.isNumber(content)) {
                 * mContext.sendBroadcast(open_call_btn); }
                 *
                 * } else { mToast.show("粘贴内容不能为空！", 1); } } catch (Exception e) {
                 * e.printStackTrace(); mToast.show("粘贴功能不能使用！", 1); }
                 */
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_STAR);
                playTone(ToneGenerator.TONE_DTMF_S);

                break;
            case R.id.DigitDeleteButton:
                // if(KcUserConfig.getDataBoolean(mContext,
                // KcUserConfig.JKEY_FIRST_ENTER_KEYBORD_DELETE, true)){
                // KcUserConfig.setData(mContext,
                // KcUserConfig.JKEY_FIRST_ENTER_KEYBORD_DELETE, false);
                // mToast.show("长按删除可以清空号码", 1);
                // }
                // scannelNumber++;
                // if (scannelNumber == 3) {
                // Toast.makeText(mContext, "长按可以清空全部号码", Toast.LENGTH_LONG)
                // .show();
                // }
                // keyPressed(KeyEvent.KEYCODE_DEL);
                // playTone(ToneGenerator.TONE_DTMF_D);
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_POUND);
                playTone(ToneGenerator.TONE_DTMF_P);
                break;

            case R.id.DigitOneButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_1);
                playTone(ToneGenerator.TONE_DTMF_1);
                break;
            case R.id.DigitTwoButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_2);
                playTone(ToneGenerator.TONE_DTMF_2);
                break;
            case R.id.DigitThreeButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_3);
                playTone(ToneGenerator.TONE_DTMF_3);
                break;
            case R.id.DigitFourButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_4);
                playTone(ToneGenerator.TONE_DTMF_4);
                break;
            case R.id.DigitFiveButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_5);
                playTone(ToneGenerator.TONE_DTMF_5);
                break;
            case R.id.DigitSixButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_6);
                playTone(ToneGenerator.TONE_DTMF_6);
                break;
            case R.id.DigitSevenButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_7);
                playTone(ToneGenerator.TONE_DTMF_7);
                break;
            case R.id.DigitEightButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_8);
                playTone(ToneGenerator.TONE_DTMF_8);
                break;
            case R.id.DigitNineButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_9);
                playTone(ToneGenerator.TONE_DTMF_9);
                //  Log.d("fggre", "onClick:按了9 "+"按了9");
                break;
            case R.id.DigitZeroButton:
                mContext.sendBroadcast(open_call_btn);
                keyPressed(KeyEvent.KEYCODE_0);
                playTone(ToneGenerator.TONE_DTMF_2);
                break;
            case R.id.vs_calllog_btn:// 导入通话记录
                loadCallLog();
                break;
        }
    }

    private void loadCallLog() {
        // mCalllogNullPro.getVs_calllog_btn().setText("加载中...");
        VsUserConfig.setData(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, false);
        mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG_do);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInit) {
            return;
        }
        // 清除未接来电记录
        VsUserConfig.setData(mContext, VsUserConfig.JKey_missed_call, "");
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(861927);

        setTitleText();
        if (callNumber != null && !"".equals(callNumber)) {
            isCopy = true;
            CustomLog.i("vsdebug", "号码:" + callNumber);
            // 在粘贴板后面加一位
            mTitleTextView.setText(callNumber + "1");
            // 把光标移动到最后
            mTitleTextView.setSelection(callNumber.length() + 1);
            // 删除最后一位（这一步只是为了调用keyPressed()方法，进行T9搜索）
            keyPressed(KeyEvent.KEYCODE_DEL);
            // 显示拨号按钮
            mContext.sendBroadcast(open_call_btn);
            callNumber = "";
        }
        isCopy = false;
        ll_title_bar.setClickable(true);
        /*
         * isopen = KcUserConfig.getDataBoolean(mContext,
         * KcUserConfig.JKEY_KEYBORD_IS_SHOW, true);
         * openOrCloseKeyboard(isopen);
         */
        // if (!isLogin()) {
        // btn_invite_kc.setText(R.string.login_register);
        // tv_new_people.setText("40分钟免费话费");
        // setNewPeoPleVisibility(true);
        // is_show_people = true;
        // } else if (!KcUtil.checkHasBindPhone(mContext)) {
        // btn_invite_kc.setText(R.string.register_receive);
        // if (KcUtil.isNull(msg2)) {
        // msg2 = "验证手机有惊喜";
        // } else {
        // tv_new_people.setText(msg2);
        // }
        // setNewPeoPleVisibility(true);
        // is_show_people = true;
        // }
        // initUserLeadl();
        // countdown_time = Long.parseLong(KcUserConfig.getDataString(mContext,
        // KcUserConfig.JKey_RegSurplus,"0"));
        // if(countdown_time > 0 ){
        // CustomLog.i("beifen", "countdown_time === " + countdown_time);
        // mBaseHandler.sendEmptyMessageDelayed(MSG_COUNTDOWN_ID, 1000);
        // }

        if (ll_title_bar.getVisibility() == View.VISIBLE) {
            CustomLog.i(TAG, "title = " + ll_title_bar.getText().toString());
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
        }

        CustomLog.i(TAG, " onResume(), isNeedShowAd = " + isNeedShowAd + ",  mInputKeyboard.getVisibility() == View.VISIBLE is " + (mInputKeyboard.getVisibility() == View
                .VISIBLE) + ", mListView.getVisibility() != View.VISIBLE is " + (mListView.getVisibility() != View.VISIBLE));
        if (mInputKeyboard.getVisibility() == View.VISIBLE) {
            showAd();
        }

        CustomLog.i(TAG, "MainFragment------onResume(),...");
        position = 0;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        CustomLog.i("vsdebug", "vsDialFragment--onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("HomeExit", true);
    }

    private BroadcastReceiver diaOrShowCallLogBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(DfineAction.ACTION_DIAL_CALL)) {
                String phone = getPhone();
                if (phone.length() >= 3 && !phone.contains("*") && !phone.contains("#")) {
                    if (phone.contains("+") && phone.indexOf("+") > 0) {
                        Toast.makeText(mContext, mResource.getString(R.string.invalide_phone), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // boolean iscallsuc = false;
                    phone = phone.trim().replaceAll("-", "").replaceAll(" ", "");
                    if (VsUtil.checkPhone(phone)) {// 校验号码
                        VsContactItem item = null;
                        try {
                            item = (VsContactItem) mListView.getAdapter().getItem(0);
                            if (item.mIndex == -10) item = null;
                        } catch (Exception e) {
                            item = null;
                        } finally {
                            if (item != null && item.mContactPhoneNumber != null && item.mContactPhoneNumber.length() > 3 && item.mContactPhoneNumber.equals(phone)) {
                                // KcCommStaticFunction.callNumber(item.mContactName,
                                // item.mContactPhoneNumber,
                                // item.mContactBelongTo, mContext);
                            } else {
                                // KcCommStaticFunction.callNumber("", phone,
                                // "", mContext);
                            }
                        }
                    } else {
                        mToast.show(mResource.getString(R.string.invalide_phone), Toast.LENGTH_SHORT);
                    }
                    mContext.sendBroadcast(new Intent(RECEIVER_SET_EDITTECT_NULL));
                } else {
                    if (phone.length() > 0) {
                        Toast.makeText(mContext, mResource.getString(R.string.invalide_phone), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (action.equals(DfineAction.ACTION_SHOW_CALLLOG)) {
                CustomLog.d("calllog", "显示通话记录");
                mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG);
            } else if (action.equals(DfineAction.ACTION_UPDATE_CALLLOG)) {
                CustomLog.d("calllog", "更新通话记录");
                if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, true)) {
                    VsPhoneCallHistory.callLogs.clear();
                    VsPhoneCallHistory.callLogViewList.clear();
                    mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG_do);
                }
            } else if (VsUserConfig.VS_ACTION_LOGIN_LOADING_DIAL.equals(action)) {
                if (!VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_FRIST_LOAD_CALLLOG, true) && VsPhoneCallHistory.callLogs.size() == 0) {
                    mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG_do);
                    CustomLog.i("vsdebug", "收到广播了----");
                }
            } else if (VsUserConfig.JKey_GET_VSUSER_OK.equals(intent.getAction()) || VsUserConfig.JKey_GET_VSUSER_FAIL.equals(intent.getAction())) {
                new UpdateCalllogAsynTask().execute();
            }
        }
    };

    /**
     * @Title:Android客户端
     * @Description: 异步更新
     * @Copyright: Copyright (c) 2014
     * @author: 李志
     * @version: 1.0.0.0
     * @Date: 2014-11-3
     */
    class UpdateCalllogAsynTask extends AsyncTask<Void, Void, Void> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            VsContactItem newItem = null;
            if (GlobalVariables.SAVE_CALLLONG_NUMBER != null && !"".equals(GlobalVariables.SAVE_CALLLONG_NUMBER)) {
                newItem = VsUtil.getContactsItem(GlobalVariables.SAVE_CALLLONG_NUMBER);
                if (newItem != null) {
                    // 更新通话记录
                    if (newItem.phoneNumList.size() > 0) {
                        VsPhoneCallHistory.updateCallLog(mContext, newItem.mContactName, newItem.phoneNumList);
                    }
                }
                GlobalVariables.SAVE_CALLLONG_NUMBER = null;
            }
            return null;
        }
    }

    /**
     * @Title:Android客户端
     * @Description: 异步更新通话记录
     * @Copyright: Copyright (c) 2014
     * @author: 李志
     * @version: 1.0.0.0
     * @Date: 2014-11-3
     */
    class DisplayCallLogTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            CustomLog.d("calllog", "执行异步任务发送消息    ..............");
            mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG);
            return null;
        }
    }

    private BroadcastReceiver clear_edit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = mBaseHandler.obtainMessage();
            msg.what = VsDialFragment.MSG_CLOSEUSERLEAD;
            Bundle bundle = new Bundle();
            boolean isOpen = intent.getBooleanExtra("isOpen", true);
            int indicator = intent.getIntExtra("indicator", 1);
            bundle.putBoolean("isopen", isOpen);
            bundle.putInt("indicator", indicator);
            msg.setData(bundle);
            mBaseHandler.sendMessage(msg);
            // openOrCloseKeyboard(isOpen);
        }
    };

    private BroadcastReceiver inituserleadlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DfineAction.ACTION_REGSENDMONEY.equals(intent.getAction())) {
                initUserLeadl();
            } else if (GlobalVariables.action_loadcalllog_succ.equals(intent.getAction())) {
                CustomLog.d("calllog", "加载通话记录成功");
                Log.e("calllog", "加载通话记录成功");
                mBaseHandler.sendEmptyMessage(MSG_PROGRESS_QUERY_CALLLOG);
            }
        }
    };

    private BroadcastReceiver actionNetChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(VsUserConfig.JKey_GET_VSUSER_OK)) {// KC好友记载完成

            } else if (action.equals(GlobalVariables.action_dial_phone)) {
                mBaseHandler.sendEmptyMessage(MSG_DIAL_PHONE);
            } else {
                mBaseHandler.sendEmptyMessage(NETWORK_CHANGE);
            }
        }
    };

    private BroadcastReceiver SysMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jStr = intent.getStringExtra(VsCoreService.VS_KeyMsg);
            CustomLog.i(TAG, "系统公告json数据====" + jStr);
            JSONObject jObj;
            JSONObject jOb;
            String context_all = "";// 公告返回的信息
            String msg_id_all = "";// 公告ID
            String msgBody = null, msgID = null, msgTitle = null, msgSubTitle = null, redirect_btn_text = null, redirect_type = null, redirect_target = null, url = null,
                    button_text = null, title = null, content = null, v = null, status = null, id = null, pv = null, level = null, flag = null;
            try {
                jOb = new JSONObject(jStr);
                int gresult = Integer.valueOf(jOb.getString("code"));// 0:正常返回；
                jObj = jOb.getJSONObject("data");
                // 101:内容重复,为省流量不再返回内容

                if (gresult == 0) {
                    button_text = jObj.getString("button_text");
                    title = jObj.getString("title");
                    content = jObj.getString("content");
                    redirect_target = jObj.getString("redirect_target");
                    redirect_type = jObj.getString("redirect_type");
                    v = jObj.getString("v");
                    status = jObj.getString("status");
                    id = jObj.getString("id");
                    pv = jObj.getString("pv");
                    level = jObj.getString("level");
                    flag = jObj.getString("flag");

                    if (content != null && id != null && !checkMSGRead(id)) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        bundle.putString("content", content);
                        bundle.putString("title", title);
                        bundle.putString("v", v);
                        bundle.putString("status", status);
                        bundle.putString("pv", pv);
                        bundle.putString("level", level);
                        bundle.putString("flag", flag);
                        bundle.putString("button_text", button_text);
                        bundle.putString("redirect_target", redirect_target);
                        bundle.putString("redirect_type", redirect_type);

                        msg.setData(bundle);
                        msg.what = AUTOREGISTER_TOAST;
                        if (!VsUtil.isNull(content)) {
                            mBaseHandler.sendMessageDelayed(msg, 500);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 初始化音频振荡器
     */
    private void initTonePlayer() {
        int j;
        int i = 1;
        android.content.ContentResolver contentresolver = mContext.getContentResolver();
        Object obj = "dtmf_tone";
        j = android.provider.Settings.System.getInt(contentresolver, ((String) (obj)), i);
        if (j == i) j = i;
        else j = 0;
        // mDTMFToneEnabled = (j != 0) ? true : false;

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(3, 60);
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().setVolumeControlStream(3);
                } catch (RuntimeException e) {
                    mToneGenerator = null;
                }
            }
        }
        return;
    }

    /**
     * 播放音调
     *
     * @param tone
     */
    int position = 0;

    private void playTone(final int tone) {
        if (VsUserConfig.getDataBoolean(mContext, VsUserConfig.JKEY_SETTING_KEYPAD_TONE, true)) {
            if (VsUserConfig.getDataInt(mContext, VsUserConfig.JKEY_PIANO_ISCHECHED_ID) != 0) {
                if (position == VsCoreService.spMap.size() - 1) {
                    position = 0;
                }
                if (VsCoreService.spMap.get(position) != null) {
                    VsCoreService.playSounds(position, 0, getActivity());
                    position++;
                }
            } else {
                new ThreadPlayTone(tone).start();
            }
        }
    }

    class ThreadPlayTone extends Thread {
        int tone = -1;

        public ThreadPlayTone(int t) {
            tone = t;
        }

        @Override
        public void run() {
            if (tone > 0) {
                if (audioManager == null) {
                    audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                }
                int ringerMode = audioManager.getRingerMode();
                if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
                    return;
                }
                if (mToneGenerator == null) {
                    return;
                }
                // 设置拨号声音为100毫秒
                mToneGenerator.startTone(tone, 100);
            }
        }
    }

    /**
     * 输入电话号码
     */
    private void keyPressed(int keyCode) {
        if (mTitleTextView.getText().toString().contains(mResource.getString(R.string.title_dial))) {
            mTitleTextView.setText("");
        }

        calllog_listView.setVisibility(View.GONE);

        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mTitleTextView.onKeyDown(keyCode, event);
        ll_title_bar.setClickable(false);
        if (getPhone().length() == 0 && VsPhoneCallHistory.callLogs.size() == 0) {
            // mCalllogNullPro.setVisibility(View.VISIBLE);
            calllog_no_p.setVisibility(View.VISIBLE);
            setTitleText();
            ll_title_bar.setClickable(true);
        } else {
            // mCalllogNullPro.setVisibility(View.GONE);
            calllog_no_p.setVisibility(View.GONE);
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && getPhone().length() <= 0) {
            VsPhoneCallHistory.CONTACTIONFOLIST.clear();
            adapter.notifyDataSetChanged();
            mListView.setVisibility(View.GONE);
            if (mInputKeyboard.getVisibility() == View.VISIBLE) {
                showAd();
            }

            setTitleText();
            ll_title_bar.setClickable(true);
        } else {
            mListView.setVisibility(View.VISIBLE);
            closeAd();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        VsPhoneCallHistory.CONTACTIONFOLIST.clear();
        // 此activity 销毁时更新状态
        mActivityState = "invalid";
        // unregisterKcBroadcast();
        // 有时会注销未注册广播 - 一个个去try
        try {
            if (inituserleadlReceiver != null) {
                mContext.unregisterReceiver(inituserleadlReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (diaOrShowCallLogBr != null) {
                mContext.unregisterReceiver(diaOrShowCallLogBr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (setEdittectNullReceiver != null) {
                mContext.unregisterReceiver(setEdittectNullReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (SysMsgReceiver != null) {
                mContext.unregisterReceiver(SysMsgReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (actionNetChange != null) {
                mContext.unregisterReceiver(actionNetChange);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (clear_edit != null) {
                mContext.unregisterReceiver(clear_edit);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 过滤联系人
     */
    class PhoneNumberTextWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString();
            if (input.length() > 0) {
                setTtitlBar(false);
                searchInput = true;
            } else {
                setTtitlBar(true);
                searchInput = false;
                mTitleTextView.setTextSize(28f);
            }
            if (input.length() > 13) {
                mTitleTextView.removeTextChangedListener(phoneTextWatcher);
                mTitleTextView.setTextSize(25f);
                String textString = input.replaceAll("-", "");
                mTitleTextView.setText(textString);
                mTitleTextView.setSelection(textString.length());
                mTitleTextView.addTextChangedListener(phoneTextWatcher);
            }
            if (input.length() == 0) {
                scannelNumber = 0;
            }

            // 手机号码中间加-
            if (input.length() > 1 && input.startsWith("1")) {
                if (input.length() > 7) {
                    mBaseHandler.sendEmptyMessage(MSG_ID_SEARCH_LOCAL);// 查询归属地
                }
                if (oldLength == 0) {
                    oldLength = input.length();
                } else {
                    if ((oldLength > input.length())) {
                        flag = true;
                        // 判断是否为手机号码，是手机号码则3、4、4分隔
                        if (input.replaceAll("-", "").length() == 11) {
                            mTitleTextView.removeTextChangedListener(phoneTextWatcher);
                            mTitleTextView.setTextSize(28f);
                            String str = input.substring(0, 3) + "-" + input.subSequence(3, 7) + "-" + input.subSequence(7, 11);
                            mTitleTextView.setText(str);
                            mTitleTextView.setSelection(str.length());
                            mTitleTextView.addTextChangedListener(phoneTextWatcher);
                        }
                    } else {
                        flag = false;
                    }
                    oldLength = input.length();
                }
                if (!flag) {
                    if (input.length() == 3 || input.length() == 8) {
                        mTitleTextView.append("-");
                    }
                } else if (input.length() == 3 || input.length() == 8) {
                    mTitleTextView.setText(input.subSequence(0, input.length() - 1));
                    mTitleTextView.setSelection(input.length() - 1);
                }
            } else if (input.length() > 3 && input.startsWith("0")) {
                mBaseHandler.sendEmptyMessage(MSG_ID_SEARCH_LOCAL);// 查询归属地
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence message, int start, int before, int count) {
            String input = message.toString();

            if (input.length() > 10) {
                input = VsUtil.filterPhoneNumber(input);
            }

            if (input.length() > 7 && !input.startsWith("0") && !input.startsWith("1")) {
                mToast.show("固话前需加拨区号");
            } else if (input.length() > 13 && input.startsWith("1") && !isCopy && !VsUtil.checkPhone(input.replaceAll("-", ""))) {
                mToast.show("请输入正确手机号码");
            }
            if (input.length() > 0 && !input.contains(mResource.getString(R.string.title_dial))) {
                mBtnNavRight.setVisibility(View.GONE);
                ll_title_bar.setVisibility(View.GONE);
                calllog_listView.setVisibility(View.GONE);
                adapter.getFilter().filter(input.replaceAll("-", ""));
            } else {
                mBtnNavRight.setVisibility(View.VISIBLE);
                ll_title_bar.setVisibility(View.VISIBLE);
                // calllog_listView.setVisibility(View.VISIBLE);
                VsPhoneCallHistory.CONTACTIONFOLIST.clear();
                adapter.notifyDataSetChanged();
                localName.setText("");// 清空归属地
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        CustomLog.i(TAG, "Fragment 退出");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initTitleNavBar(View view) {
        mTitleTextView = (EditText) view.findViewById(R.id.sys_title_txt);
        mBtnNavLeft = (LinearLayout) view.findViewById(R.id.btn_nav_left);
        mBtnNavLeftTv = (TextView) view.findViewById(R.id.btn_nav_left_tv);
        mBtnNavRight = (LinearLayout) view.findViewById(R.id.btn_nav_right);
        mBtnNavRightTv = (TextView) view.findViewById(R.id.btn_nav_right_tv);
        mLeftLine = (ImageView) view.findViewById(R.id.title_line_left);
        mRightLine = (ImageView) view.findViewById(R.id.title_line_right);
        small_title = (RelativeLayout) view.findViewById(R.id.small_title);
        small_title.setVisibility(View.GONE);

        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                if (!isopen) {
                    openOrCloseKeyboard(true);
                }
            }
        });
    }

    private BroadcastReceiver setEdittectNullReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTitleText();
            keyPressed(KeyEvent.KEYCODE_DEL);
        }
    };

    @Override
    protected void HandleLeftNavBtn() {
        VsUtil.addContact(mContext, getPhone());
    }

    /**
     * 得到号码
     *
     * @return
     */
    private String getPhone() {
        String phone = mTitleTextView.getText().toString();
        if (phone.equals(mResource.getString(R.string.title_dial))) {
            mTitleTextView.setText("");
            return "";
        } else {
            // mCalllogNullPro.setVisibility(View.GONE);
            calllog_no_p.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            if (mInputKeyboard.getVisibility() == View.VISIBLE) {
                showAd();
            }
        }
        return phone.trim();
    }

    /**
     * 设置标题
     *
     * @return
     */
    public void setTitleText() {
        mTitleTextView.getText().clear();

        mTitleTextView.setText("");
        mContext.sendBroadcast(close_call_btn);
        if (VsPhoneCallHistory.callLogs.size() > 0) {
            // mCalllogNullPro.setVisibility(View.GONE);
            calllog_no_p.setVisibility(View.GONE);
        } else {
            // mCalllogNullPro.setVisibility(View.VISIBLE);
            calllog_no_p.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏输入法
     */
    private void hideDigitsIM() {
        mTitleTextView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // ctsKeywordEdt.setInputType(InputType.TYPE_NULL); // 关闭软键盘
                if (android.os.Build.VERSION.SDK_INT <= 10) {
                    mTitleTextView.setInputType(InputType.TYPE_NULL);
                } else {
                    if (getActivity() == null) {
                        return false;
                    }
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    try {
                        Class<EditText> cls = EditText.class;
                        Method setSoftInputShownOnFocus;
                        setSoftInputShownOnFocus = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                        setSoftInputShownOnFocus.setAccessible(true);
                        setSoftInputShownOnFocus.invoke(mTitleTextView, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Class<EditText> cls = EditText.class;
                        Method setShowSoftInputOnFocus;
                        setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                        setShowSoftInputOnFocus.setAccessible(true);
                        setShowSoftInputOnFocus.invoke(mTitleTextView, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTitleTextView.getWindowToken(), 0);
    }

    /**
     * 设置公告已读
     *
     * @author: 石云升
     */
    public void setMSGRead(String id) {
        VsUserConfig.setData(mContext, VsUserConfig.JKey_ReadSysMsgID, id);
    }

    public void setMSGReadTime(String str) {
        VsUserConfig.setData(mContext, VsUserConfig.JKey_ReadSysMsgTime, str);
    }

    /**
     * 判断公告是否已读
     */
    public boolean checkMSGRead(String gID) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        if (!VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ReadSysMsgID, "").equals(gID)) {
            VsUserConfig.setData(mContext, VsUserConfig.JKey_ReadSysMsgTime, str);
            return false;
        }

        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ReadSysMsgTime, "").equals(str)) {
            return true;
        }
        setMSGReadTime(str);
        return false;
    }

    private class UpgradeCancelBtnListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            setShowNewPeople();
            if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeMandatory).equals("force")) {
                VsApplication.getInstance().exit();
            }
        }
    }

    private class UpgradeCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialog) {
            setShowNewPeople();
            if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeMandatory).equals("force")) {
                VsApplication.getInstance().exit();
            }
        }
    }

    @Override
    public void onStop() {
        CustomLog.i("beifen", "stop");
        if (VsPhoneCallHistory.callLogViewList.size() > 0) {
            mListAdapter.notifyDataSetChanged();
        }
        if (VsPhoneCallHistory.CONTACTIONFOLIST.size() > 0) {
            adapter.notifyDataSetChanged();
        }
        super.onStop();
    }

    /**
     * 显示网络差状况
     */
    private void setNetWorkDlog() {
        if (GlobalVariables.netmode == 0) {
            tv_network_change.setText(R.string.call_log_text19);
            ll_title_bar_dlog.setVisibility(View.VISIBLE);
            ll_title_bar_dlog.setBackgroundColor(getResources().getColor(R.color.network_change_color));
            tv_network_change.setVisibility(View.VISIBLE);
            ll_title_bar_dlog_1.setVisibility(View.GONE);
            mBaseHandler.sendEmptyMessageDelayed(NETWORK_GONE, 3000);
        } else if (GlobalVariables.netmode != 1 && VsPhoneCallHistory.VSCONTACTLIST.size() > 0) {
            tv_network_change.setText(R.string.no_freedial_promit_network);
            ll_title_bar_dlog.setVisibility(View.VISIBLE);
            ll_title_bar_dlog.setBackgroundColor(getResources().getColor(R.color.network_change_color));
            tv_network_change.setVisibility(View.VISIBLE);
            ll_title_bar_dlog_1.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            mBaseHandler.sendEmptyMessageDelayed(NETWORK_GONE, 3000);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 关闭网络状态提醒
     */
    private void setNetWorkDlogGone() {
        ll_title_bar_dlog.setVisibility(View.GONE);
        tv_network_change.setVisibility(View.GONE);
        ll_title_bar_dlog_1.setVisibility(View.GONE);
        ll_title_bar_dlog.setBackgroundResource(R.drawable.title_bar_diog_bg);
    }

    /**
     * 判断是否登录
     *
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

    @Override
    protected void HandleRightNavBtn() {
        super.HandleRightNavBtn();
        initAnimation();
        showDialogCallSetting(true);
    }

    private void httpVerify(final String name, final String number, final String local, final Context mContext, final String dialType, final boolean checkFre) {
        VsUtil.callNumber(name, number, local, mContext, dialType, checkFre, getFragmentManager(), true);
    }

    /**
     * 打电话
     */
    private void callPhone() {
        String number = VsUtil.filterPhoneNumber(mTitleTextView.getText().toString().replaceAll("-", ""));
        if (number.length() > 7 && VsPhoneCallHistory.CONTACTIONFOLIST.size() > 0 && !"".equals(VsPhoneCallHistory.CONTACTIONFOLIST.get(0).mContactPhoneNumber)) {
            VsContactItem item = VsPhoneCallHistory.CONTACTIONFOLIST.get(0);
            if (item.phoneNumList.size() > 1) {
                number = VsUtil.getContactPhone(item, number);// 获取真实号码
            }
            httpVerify((item.mContactName == null || item.mContactName.equals("")) ? number : item.mContactName, number, item.mContactBelongTo, mContext, null, true);
            // if (VsUtil.checkPhoneNmbeIsVs(mContext, null, number, item)) {
//            ---VsUtil.callNumber((item.mContactName == null || item.mContactName.equals("")) ? number : item.mContactName, number, item.mContactBelongTo, mContext, null, true);
            // }
            return;
        } else if (number.length() > 7) {
            if (VsUtil.checkPhone(number)) {
                httpVerify("", number, VsLocalNameFinder.findLocalName(number, false, mContext), mContext, null, true);
                // if (VsUtil.checkPhoneNmbeIsVs(mContext, null, number, null))
                // {
//                ---VsUtil.callNumber("", number, VsLocalNameFinder.findLocalName(number, false, mContext), mContext, null, true);
                // }
            } else {
                mToast.show("请输入正确的手机号码");
            }
            return;
        } else if ((number.length() == 7 || number.length() == 8) && !number.startsWith("0") && !number.startsWith("1")) {
            mToast.show("固话前需加拨区号");
        } else if (number.length() < 7) {
            mToast.show("不支持拨打短号或特服号");
        } else {
            mToast.show("请输入正确的手机号码");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
        if (mDialogCallSetting != null) mDialogCallSetting.dismiss();
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

    /**
     * 判断是否需要弹出升级提醒
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public boolean IsNeedupgradeForTipsNumber(boolean isMandatory) {
        if (isMandatory) return true;
        int num = 0;
        int needTipsNum = Integer.parseInt(VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_UPGRADETIPSNUMBER, "3"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date());
        String upgrade_day = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UPGRADE_DAY);
        if (upgrade_day.length() == 0) {
            upgrade_day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            VsUserConfig.setData(mContext, VsUserConfig.JKey_UPGRADE_DAY, upgrade_day);
        }
        // 如果当前是同一天需要判断当天提醒了多少次。如果不是同一天。转变为同一天。从0开始算
        if (!date.equals(upgrade_day)) {
            upgrade_day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            VsUserConfig.setData(mContext, VsUserConfig.JKey_UPGRADE_DAY, upgrade_day);
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_UPGRADECURRENTTIPSNUMBER, 0);
        } else {
            num = VsUserConfig.getDataInt(mContext, VsUserConfig.JKEY_UPGRADECURRENTTIPSNUMBER);
        }
        if (num < needTipsNum) {
            VsUserConfig.setData(mContext, VsUserConfig.JKEY_UPGRADECURRENTTIPSNUMBER, num + 1);
            return true;
        }
        return false;
    }

    /**
     * 显示所有通话、未接来电选择项
     */
    private void showSelectDialog() {
        if (mDialogSelect != null && mDialogSelect.isShowing()) {
            mDialogSelect.dismiss();
        }
        if (null != mDialogSelect) {
            mDialogSelect.show();
        }
    }

    private void initCalllogDialog() {
        if (null == mDialogSelect) {
            mDialogSelect = new Dialog(mContext, R.style.CommonDialogStyle);
        }
        View v = View.inflate(mContext, R.layout.calllog_discuss_select_dialog, null);

        LinearLayout pri = (LinearLayout) v.findViewById(R.id.calllog_all);
        LinearLayout discuss = (LinearLayout) v.findViewById(R.id.calllog_miss);
        pri_image = (ImageView) v.findViewById(R.id.change_all);
        discuss_image = (ImageView) v.findViewById(R.id.change_miss);

        pri.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomLog.d("calllg", "pri.setOnClickListener...........");
                if (mDialogSelect != null && mDialogSelect.isShowing()) {
                    mDialogSelect.dismiss();
                }

                if (mTitleBarView.getTitleLeft().isEnabled()) {
                    // pri_image.setVisibility(View.VISIBLE);
                    // discuss_image.setVisibility(View.INVISIBLE);
                    mTitleBarView.getTitleLeft().setEnabled(false);
                    mTitleBarView.getTitleRight().setEnabled(true);
                    mTitleBarView.setIMG(R.drawable.vs_calllog_down);
                    mTitleBarView.setTxtDialSelect("全部通话");
                    isall = true;

                    isedit = false;
                    changEdit(true);
                    isup = true;
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsDialFragment
                     * newsFragment = new VsDialFragment();
                     * ft.replace(R.id.child_fragment, newsFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */
                    new DisplayCallLogTask().execute();
                }

            }
        });
        discuss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTitleBarView.getTitleRight().isEnabled()) {
                    // pri_image.setVisibility(View.INVISIBLE);
                    // discuss_image.setVisibility(View.VISIBLE);
                    mTitleBarView.getTitleLeft().setEnabled(true);
                    mTitleBarView.getTitleRight().setEnabled(false);
                    mTitleBarView.setIMG(R.drawable.vs_calllog_down);
                    isall = false;
                    mTitleBarView.setTxtDialSelect("未接来电");
                    isedit = false;
                    changEdit(true);
                    isup = false;
                    new DisplayCallLogTask().execute();
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsMyselfFragment
                     * callFragment = new VsMyselfFragment();
                     * ft.replace(R.id.child_fragment, callFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */
                    /*
                     * FragmentTransaction ft =
                     * getFragmentManager().beginTransaction(); VsDialFragment
                     * newsFragment = new VsDialFragment();
                     * ft.replace(R.id.child_fragment, newsFragment,
                     * VsDialBaseFragment.TAG); //ft.addToBackStack(TAG);
                     * ft.commit();
                     */

                }

                if (mDialogSelect != null && mDialogSelect.isShowing()) {
                    mDialogSelect.dismiss();
                }
            }
        });

        mDialogSelect.setContentView(v);
        mDialogSelect.setCanceledOnTouchOutside(true);
        mDialogSelect.setCancelable(true);

        mDialogSelect.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mTitleBarView.setIMG(R.drawable.vs_calllog_down);
            }
        });

        Window mDialogWindow = mDialogSelect.getWindow();
        mDialogWindow.setWindowAnimations(R.style.vs_calllog_select_dialog_show);
        WindowManager.LayoutParams lp = mDialogWindow.getAttributes();
        mDialogWindow.setGravity(Gravity.TOP);
        if (GlobalVariables.width == 0) {
            // 获取屏幕宽高与密度
            // VsUtil.setDensityWH(VsDialFragment.this);
        }
        lp.x = (int) (GlobalVariables.width); // 新位置X坐标
        lp.y = (int) (104 * GlobalVariables.density); // 新位置Y坐标
        lp.width = (int) mDialogWindow.getWindowManager().getDefaultDisplay().getWidth(); // 宽度
        lp.height = (int) (120 * GlobalVariables.density); // 高度
        lp.alpha = 1f; // 透明度

        mDialogWindow.setAttributes(lp);
    }

    /**
     * @return void 返回类型
     * @throws @Title: update
     * @Description: 升级
     */
    public void update() {
        //当前版本号与新版本号比较
        String appVersionName = VsUtil.getAppVersionName(mContext);
        String appVersionNewName = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version);
        if (appVersionName.equals(appVersionNewName)) {
            return;
        }
        String apkUrl = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl);
        String upMandatory = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeMandatory);
        //升级
        boolean isMandatory = "2".equals(upMandatory);
        if (apkUrl.length() > 5 && (IsNeedupgradeForTipsNumber(true))) {
            if (getFragmentManager() != null) {
                UpDataDialog
                        .getInstance()
                        .isUpMandatory(isMandatory)
                        .setVersion(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version))
                        .setContent(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeInfo))
                        .setUpDataListener(new UpDataDialog.UpDataListener() {
                            @Override
                            public void onUpDataClick() {
                                startUpdateApk();
                            }
                        })
                        .show(getFragmentManager(), "");
            } else {
                if (isMandatory) {
                    showYesNoDialog("检测到新版本" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version), VsUserConfig.getDataString(mContext, VsUserConfig
                            .JKey_UpgradeInfo, "发现新版本，是否更新?"), "暂不升级", "现在升级", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            startUpdateApk();
                        }
                    }, null, null);
                } else {
                    startUpdateApk();
                }
            }
        }
//        if (apkUrl.length() > 5 && "0".equals(upMandatory)) {
//
//            if (IsNeedupgradeForTipsNumber()) {
//                if(getFragmentManager() != null){
//                    UpDataDialog
//                            .getInstance()
//                            .setVersion(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version))
//                            .setContent(VsUserConfig.getDataString(mContext, VsUserConfig .JKey_UpgradeInfo))
//                            .setUpDataListener(new UpDataDialog.UpDataListener() {
//                                @Override
//                                public void onUpDataClick() {
//                                    startUpdateApk();
//                                }
//                            })
//                            .show(getFragmentManager(),"");
//                }else{
//                    showYesNoDialog("检测到新版本" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version), VsUserConfig.getDataString(mContext, VsUserConfig
//                            .JKey_UpgradeInfo, "发现新版本，是否更新?"), "暂不升级", "现在升级", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            startUpdateApk();
//                        }
//                    }, null, null);
//                }
//            }
//            //强制升级
//        } else if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5 && "2".equals(VsUserConfig.getDataString(mContext, VsUserConfig
//                .JKey_UpgradeMandatory))) {
//            startUpdateApk();
//        }

    }

    /**
     * @return void 返回类型
     * @throws @Title: update
     * @Description: fir升级
     */
    public void update_fir() {
        if (VsUserConfig.getDataString(mContext, VsUserConfig.JKey_UpgradeUrl).length() > 5) {

            if (IsNeedupgradeForTipsNumber(false)) {
                showYesNoDialog("检测到新版本" + VsUserConfig.getDataString(mContext, VsUserConfig.JKey_new_version), VsUserConfig.getDataString(mContext, VsUserConfig
                        .JKey_UpgradeInfo, "发现新版本，是否更新?"), "暂不升级", "现在升级", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        startUpdateApk();
                    }
                }, null, null);

            }
        }
        // else if (VsUserConfig.getDataString(mContext,
        // VsUserConfig.JKey_UpgradeUrl)
        // .length() > 5&&"2".equals(VsUserConfig.getDataString(mContext,
        // VsUserConfig.JKey_UpgradeMandatory))) {
        // startUpdateApk();
        // }

    }

    public void upTitleText() {
        scannelNumber++;
        if (scannelNumber == 3) {
            Toast.makeText(mContext, "长按可以清空全部号码", Toast.LENGTH_LONG).show();
        }
        keyPressed(KeyEvent.KEYCODE_DEL);
        playTone(ToneGenerator.TONE_DTMF_D);
    }
}