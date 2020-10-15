package com.weiwei.base.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hwtx.dududh.R;
import com.weiwei.base.adapter.VsContactListAdapter;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.json.me.JSONException;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.VsMainActivity;
import com.weiwei.netphone.data.process.CoreBusiness;

import java.util.List;

/**
 * @Title:Android客户端
 * @Description: 联系人界面
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsContactsListFragment extends VsBaseFragment implements OnClickListener {

    public static final String TAG = VsContactsListFragment.class.getSimpleName();

    // 企业号
    public static final char MSG_ID_SHOW_AGENT = 160;
    // 显示进度条
    private static final int MSG_SHOW_LOADING_CONTACT = 100;
    // 关闭进度条
    private static final int MSG_DISMISS_LOADING_CONTACT = 101;

    private View mParent;
    private InputMethodManager imm;
    // private static LinearLayout mInputKeyboard;// 搜索输入键盘
    public static boolean searchInput = false; // 是否输入搜索
    private WindowManager windowManager;
    private ListView mContactListView = null; // 联系人listview
    public VsContactListAdapter adapter = null;
    private ImageView deleteImage; // 删除
    private LinearLayout contacts_editext;
    private EditText ctsKeywordEdt;// 搜索输入框
    // private Button mFooterBtn;// 邀请好友按钮
    private LinearLayout mCurrentLetterView; // 用来放在WindowManager中显示提示字符
    private String mCurrentLetter = "A";
    private TextView tv_content1, tv_content2, tv_content3, tv_content4, tv_content5, tv_content6, tv_content7, tv_content8, tv_content9;

    /**
     * title小图标是否向下
     */
    private boolean title_icon = false;

    private TextView tv_network_change; // 网络状态切换
    private LinearLayout server_layout;

    /**
     * 通讯录人数 author:黄文武 修改时间:14/10/08
     */
    private TextView contact_num;
    /**
     * vs好友选择
     */
    // private RelativeLayout vsuserlayout;
    /**
     * VS好友数
     */
    private TextView groupSize;
    /**
     * vsUser_isshow
     */
    private boolean isShow = false;
    /**
     * 显示KC好友选择
     */
    private final char MSG_ID_SHOWKCUSER = 90;
    /**
     * 显示网络状态变化
     */
    public static final char NETWORK_CHANGE = 104;
    /**
     * 关系网络状态提醒
     */
    public static final char NETWORK_GONE = 105;
    /**
     * 显示分组变化
     */
    public static final char Group_CHANGE = 106;
    /**
     * 无联系人提示
     */
    public static final char MSG_ID_NO_CONTACTS = 107;
    /**
     * 搜索界面
     */
    private FrameLayout searchLayout;
    /**
     * 搜索子布局
     */
    private LinearLayout searchLayoutChild;

    /**
     * 主界面
     */
    private VsMainActivity vsMain;

    //
    public static final char MSG_ID_SHOW_CONTACTS_NUM = 161;

    private int scrollState; // 滚动的状态
    private ScrollView loadcontactLinearlayout;// 无联系人或者正在加载是显示
    private Button loadcontactButton;

    private int oldid = -100;
    public int now_index = 0;
    private final int mAzId[] = {R.id.az02, R.id.az03, R.id.az04, R.id.az05, R.id.az06, R.id.az07, R.id.az08, R.id.az09, R.id.az10, R.id.az11, R.id.az12, R.id.az13, R.id.az14, R
            .id.az15, R.id.az16, R.id.az17, R.id.az18, R.id.az19, R.id.az20, R.id.az21, R.id.az22, R.id.az23, R.id.az24, R.id.az25, R.id.az26, R.id.az27, R.id.az01};
    private final String mAzStr[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    public static final int OPERATION_NOTIFY_DATASET = 0; // 更新数据
    private static final int CURRENT_LOAD_CONTENT = 1; // 当前正在加载联系人
    private static final int OPERATION_RESET_CONTACTS = 3; // 重设数据

    public static Handler mBaseHandler;
    // listview 顶部布局
    private LinearLayout mHeaderLayout;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID_SHOW_AGENT:
                    String result = (String) msg.obj;
                    updateAgentInfo(result);
                    break;
                case MSG_SHOW_LOADING_CONTACT:
                    if (null != mLoadingDialog) {
                        // mLoadingDialog.setVisibility(View.VISIBLE);
                    }
                    if (null != ctsKeywordEdt) ctsKeywordEdt.setEnabled(false);
                    if (null != mAtoZView) mAtoZView.setVisibility(View.GONE);
                    break;
                case MSG_DISMISS_LOADING_CONTACT:
                    if (null != mLoadingDialog) {
                        mLoadingDialog.setVisibility(View.GONE);
                    }
                    if (null != mAtoZView) mAtoZView.setVisibility(View.VISIBLE);
                    if (null != ctsKeywordEdt) ctsKeywordEdt.setEnabled(true);
                    break;

                default:
                    break;
            }
        }
    };
    private LinearLayout mLoadingDialog;
    private boolean isInit = false;

    /**
     * Create a new instance of DetailsFragment, initialized to show the text at
     * 'index'.
     */
    public static VsContactsListFragment newInstance(int index) {
        VsContactsListFragment f = new VsContactsListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    // 接收广播并执行??
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (DfineAction.REFERSHLISTACTION.equals(action)) { // 更新数据
                mBaseHandler.sendEmptyMessage(OPERATION_NOTIFY_DATASET);
            } else if (DfineAction.CURRENT_LOGD_CONTACTLISTACTION.equals(action)) {

                mBaseHandler.sendEmptyMessage(CURRENT_LOAD_CONTENT);

            } else if (action.equals(VsUserConfig.JKey_GET_VSUSER_OK)) {

                mBaseHandler.sendEmptyMessage(MSG_ID_SHOWKCUSER);
            } else if (action.equals(GlobalVariables.action_net_change) || action.equals(GlobalVariables.action_no_network)) {
                mBaseHandler.sendEmptyMessage(NETWORK_CHANGE);
            } else if (action.equals(GlobalVariables.action_group_change)) {
                // mBaseHandler.sendEmptyMessage(Group_CHANGE);
                // mBaseHandler.sendEmptyMessageDelayed(Group_CHANGE, 500);
            } else if (action.equals(VsUserConfig.JKEY_NO_CONTACTS)) {
                mBaseHandler.sendEmptyMessage(MSG_ID_NO_CONTACTS);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CustomLog.i(TAG, "MainFragment------onCreateView(),...");
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        return view;
    }

    // 判断是否在加载联系人
    private void checkLoading() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 正在加载好友
                while (VsPhoneCallHistory.isloadContact || CoreBusiness.isLoadVsContact) {

                    try {
                        if (null != mLoadingDialog && mLoadingDialog.getVisibility() != View.VISIBLE && VsPhoneCallHistory.CONTACTLIST.size() <= 500) {
                            mHandle.sendEmptyMessage(MSG_SHOW_LOADING_CONTACT);
                            Thread.sleep(100);
                            break;
                        } else {
                            break;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandle.sendEmptyMessage(MSG_DISMISS_LOADING_CONTACT);
            }
        }).start();
    }

    @Override
    protected void HandleRightNavBtn() {
        super.HandleRightNavBtn();
        VsUtil.addContact(mContext, ctsKeywordEdt.getText().toString());
    }

    public void showLeftNavBtn() {
        super.showLeftNavaBtn(R.drawable.vs_title_back_selecter);
    }

    @Override
    protected void HandleLeftNavBtn() {
        super.HandleLeftNavBtn();
    }

    private void initView() {
        mParent.findViewById(R.id.title).setVisibility(View.GONE);
        mContactListView = (ListView) mParent.findViewById(R.id.contactlistview);
        contacts_editext = (LinearLayout) mParent.findViewById(R.id.contacts_editext);
        tv_network_change = (TextView) mParent.findViewById(R.id.tv_network_change);
        searchLayoutChild = (LinearLayout) mParent.findViewById(R.id.searchLayoutChild);
        // // 添加搜索输入框
        if (getActivity() == null) {
            return;
        }
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderLayout = (LinearLayout) inflater.inflate(R.layout.vs_contacts_friend, null);
        mContactListView.setFocusable(false);
        mContactListView.addHeaderView(mHeaderLayout);

        mLoadingDialog = (LinearLayout) mParent.findViewById(R.id.load_contact_ll);
        final String phone = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_ServicePhone, "");
        initAgentInfo(mHeaderLayout, null);
        server_layout = (LinearLayout) mParent.findViewById(R.id.server_layout);
        // 通讯录人数
        LinearLayout mFooterLayout = (LinearLayout) inflater.inflate(R.layout.fragment_contacts_list_contactnum, null);
        contact_num = (TextView) mFooterLayout.findViewById(R.id.contactnum);
        if (VsPhoneCallHistory.CONTACTLIST.size() > 0) {// 判断是否有联系人
            contact_num.setVisibility(View.VISIBLE);
            contact_num.setText("共有" + VsPhoneCallHistory.CONTACTLIST.size() + "位联系人");
            mContactListView.addFooterView(mFooterLayout);
        } else {
            contact_num.setVisibility(View.VISIBLE);
            contact_num.setText("没有联系人");
            mContactListView.addFooterView(mFooterLayout);
        }

        server_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (VsUtil.isFastDoubleClick()) {
                    return;
                }
                VsUtil.showDialog(DfineAction.RES.getString(R.string.product) + mContext.getResources().getString(R.string.prompt), "您将拨打: " + phone, "确定", "取消", new
                        DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VsUtil.LocalCallNumber(mContext, phone);

                    }
                }, null, mContext);

            }
        });

        // VS好友选择
        // vsuserlayout = (RelativeLayout)
        // mParent.findViewById(R.id.vs_contact_userlayout);
        isShow = true;
        mParent.findViewById(R.id.line).setVisibility(View.VISIBLE);
        // vsuserlayout.setVisibility(View.GONE);
        groupSize = (TextView) mParent.findViewById(R.id.groupSize);
        // vsuserlayout.setOnClickListener(this);
        if (VsPhoneCallHistory.VSCONTACTLIST.size() > 0) {// 判断是否有VS好友
            groupSize.setVisibility(View.VISIBLE);
            groupSize.setText("（" + VsPhoneCallHistory.VSCONTACTLIST.size() + "）");
        }
        // } else {
        // kcuserlayout.setVisibility(View.GONE);
        // }

        // 搜索输入框
        ctsKeywordEdt = (EditText) mParent.findViewById(R.id.vs_contact_cts_keyword);
        ctsKeywordEdt.addTextChangedListener(new textChangedListener());
        deleteImage = (ImageView) mParent.findViewById(R.id.vs_contact_deleteImage);
        searchLayout = (FrameLayout) mParent.findViewById(R.id.vs_contact_searchLayout);
        deleteImage.setOnClickListener(new deleteTextListener());
        // 无联系人或者正在加载时显示
        loadcontactLinearlayout = (ScrollView) mParent.findViewById(R.id.empty);
        loadcontactButton = (Button) mParent.findViewById(R.id.empty_btn);
        loadcontactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VsUtil.isFastDoubleClick()) {
                    VsPhoneCallHistory.loadContacts();
                }
            }
        });
        // 添加通讯录数据
        adapter = new VsContactListAdapter(mContext);
        if (VsPhoneCallHistory.CONTACTLIST.size() > 0) {
            adapter.setData(VsPhoneCallHistory.CONTACTLIST, VsPhoneCallHistory.CONTACTLIST.size());
            if (mContactListView.getAdapter() == null) {
                mContactListView.setAdapter(adapter);
            }
            mContactListView.setVisibility(View.VISIBLE);
            loadcontactLinearlayout.setVisibility(View.GONE);
            mHeaderLayout.setVisibility(View.VISIBLE);
        } else {
            mContactListView.setVisibility(View.GONE);
            // mHeaderLayout.setVisibility(View.GONE);
            // loadcontactLinearlayout.setVisibility(View.VISIBLE);
        }

        mContactListView.setOnScrollListener(new ListViewScrollListener());
        // mParent.findViewById(R.id.DigitHideButton).setOnClickListener(mDigintHindeButtonListener);//
        // 隐藏键盘按钮响应
        // 监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(DfineAction.REFERSHLISTACTION);
        filter.addAction(DfineAction.CURRENT_LOGD_CONTACTLISTACTION);
        filter.addAction(VsUserConfig.JKey_CLOSE_GROUP_TAB);
        filter.addAction(VsUserConfig.JKey_GET_VSUSER_OK);
        filter.addAction(GlobalVariables.action_net_change);
        filter.addAction(GlobalVariables.action_no_network);
        filter.addAction(GlobalVariables.action_group_change);
        filter.addAction(VsUserConfig.JKEY_NO_CONTACTS);
        getActivity().registerReceiver(mReceiver, filter);
        // hideDigitsIM();// 隐藏系统键盘，显示自定义键盘
        new GetContacts().execute();
        populateFastClick();// 首字母提示
        imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);

    }

    // 更新企业号信息
    public void updateAgentInfo(String result) {
        initAgentInfo(mHeaderLayout, result);
    }

    // 初始化企业号信息
    private void initAgentInfo(LinearLayout ll, String result) {
        ll.findViewById(R.id.agent_ll).setVisibility(View.GONE);
        ll.findViewById(R.id.line).setVisibility(View.GONE);
        CustomLog.i(TAG, "initAgentInfo(), result = " + result);
        if (!TextUtils.isEmpty(result)) {
            showAgent(ll, result);
        } else {
            if (getActivity() == null) {
                return;
            }
            String info = VsUserConfig.getDataString(getActivity(), VsUserConfig.JKEY_AGENT_INFO, "");
            showAgent(ll, info);
        }
    }

    private void showAgent(final LinearLayout ll, String result) {
        CustomLog.i(TAG, "showAgent(), result = " + result);
        ll.findViewById(R.id.agent_ll).setVisibility(View.GONE);
        ll.findViewById(R.id.line).setVisibility(View.GONE);
        String portrait = null;
        String agentName = null;
        String account = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("portait")) {
                portrait = jsonObject.getString("portait");
            }
            if (jsonObject.has("agent_name")) {
                agentName = jsonObject.getString("agent_name");
            }
            if (jsonObject.has("account")) {
                account = jsonObject.getString("account");
            }

        } catch (JSONException e) {
            CustomLog.i(TAG, "JSONException = " + e.getMessage());
        } finally {
            if (!TextUtils.isEmpty(portrait) && !TextUtils.isEmpty(agentName)) {
                CustomLog.i(TAG, "portait = " + portrait + ", agentName = " + agentName);
                ll.findViewById(R.id.agent_ll).setVisibility(View.VISIBLE);
                ll.findViewById(R.id.line).setVisibility(View.VISIBLE);
                // AsyncImageView mAsyncImageView = ((AsyncImageView)
                // ll.findViewById(R.id.agent_portrait_iv));
                // mAsyncImageView.setResource(new CompressedResource(new
                // Resource(Uri.parse(portrait)), 120, 120));

                // 显示图片的配置
                // final ImageView mImageView = (ImageView)
                // ll.findViewById(R.id.agent_portrait_iv);
                // DisplayImageOptions options = new
                // DisplayImageOptions.Builder().showImageOnLoading(R.drawable.rc_ic_portrait).showImageOnFail(R.drawable.rc_ic_portrait).cacheInMemory(true)
                // .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

                // ImageLoader.getInstance().displayImage(portrait, mImageView, options);

                ((TextView) ll.findViewById(R.id.agent_user_name)).setText(agentName);
                final String title = agentName;
                final String id = account;
                ll.findViewById(R.id.agent_ll).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ConnectionIm.getInstance().startPrivateChat(mContext, id, title);
                    }
                });
            }
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.vs_contact_userlayout:// VS好友
                // MobclickAgent.onEvent(mContext, "Con_WeiShuo");
                // startActivity(new Intent(mContext, VsFriendActivity.class));
                break;
        }
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
        if (!hidden) {
            init();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        if (isInit) {
            return;
        }
        isInit = true;

        setHasOptionsMenu(true);
        checkLoading();
        mParent = getView();
        initTitleNavBar(mParent);
        mTitleTextView.setText(R.string.title_contacts_list);
        showRightTxtBtn(getString(R.string.vs_contact_add_righttext));
        searchInput = false;

        mBaseHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                handleBaseMessage(msg);
                return false;
            }
        });
        initView();
        // 拉取企业号
        // VsBizUtil.getInstance().getAgentInfo(mContext, mHandle);

        if (VsPhoneCallHistory.CONTACTLIST.size() > 0) {
            contacts_editext.setVisibility(View.VISIBLE);
            if (VsPhoneCallHistory.VSCONTACTLIST != null && VsPhoneCallHistory.VSCONTACTLIST.size() <= 0) {
                // Runnable runnable = new Runnable() {
                // @Override
                // public void run() {
                // VsBizUtil.getInstance().getVsFriend(getActivity());
                // }
                // };
                // GlobalVariables.fixedThreadPool.execute(runnable);
            }
        } else {
            // 重新加载联系人
            if (!VsPhoneCallHistory.isloadContact) {
                VsPhoneCallHistory.loadContactData(mContext, 0);
            }
        }
    }

    /**
     * 搜索框内容改变监听
     *
     * @author dell
     */
    private class textChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                ctsKeywordEdt.setTextSize(13);
                deleteImage.setVisibility(View.GONE);
                // vsuserlayout.setVisibility(View.GONE);
            } else {
                ctsKeywordEdt.setTextSize(15);
                deleteImage.setVisibility(View.VISIBLE);
                // vsuserlayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString();
            DataSetChangedNotify(keyword);
        }
    }

    /**
     * 搜索框内容清除监听
     */
    private class deleteTextListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (ctsKeywordEdt.getText().length() > 0) {
                ctsKeywordEdt.setText("");
                ctsKeywordEdt.setSelection(0);
            }
        }
    }

    /**
     * 异步任务类，用于检索联系人，获取联系人
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mHandle.post(new UpdateUi());
        }
    }

    private class UpdateUi implements Runnable {
        public void run() {
            SetUPLetterNavio();// 字母导航
        }
    }

    /**
     * 首字母提示
     */
    private void populateFastClick() {
        if (mCurrentLetterView == null) {
            mCurrentLetterView = (LinearLayout) mParent.findViewById(R.id.pop_view);
            tv_content1 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num1);
            tv_content2 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num2);
            tv_content3 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num3);
            tv_content4 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num4);
            tv_content5 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num5);
            tv_content6 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num6);
            tv_content7 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num7);
            tv_content8 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num8);
            tv_content9 = (TextView) mCurrentLetterView.findViewById(R.id.tv_num9);
            // 默认设置为不可见
            mCurrentLetterView.setVisibility(View.INVISIBLE);
            // 设置WindowManager
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager
                    .LayoutParams.TYPE_APPLICATION,
                    // 设置为无焦点状态
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    // 半透明效果
                    PixelFormat.TRANSLUCENT);
            try {
                // windowManager.addView(mCurrentLetterView, lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View mAtoZView;
    // private View mAtoZLayout;
    private DisapearThread disapearThread = new DisapearThread();

    /**
     * 设置字母导航数据
     */
    public void SetUPLetterNavio() {
        mAtoZView = mParent.findViewById(R.id.aazz);// 父
        final int count = ((LinearLayout) mAtoZView).getChildCount();
        mAtoZView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x1 = (int) event.getX();
                int y1 = (int) event.getY();
                Rect frame = new Rect();
                v.getHitRect(frame);
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    for (int index = 0; index < count; index++) {
                        View view = ((ViewGroup) mAtoZView).getChildAt(index);
                        view.getHitRect(frame);
                        if (frame.contains(x1, y1)) {
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(mContext.getWindow().getDecorView().getWindowToken(), 0);
                            }
                            showUpLetter(view);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * 设置9种字体透明度
     *
     * @param view
     */
    private void showUpLetter(View view) {
        mCurrentLetter = (String) view.getTag();
        if ("#".equals(mCurrentLetter)) {
            mContactListView.setSelection(mContactListView.getCount() - 1);
            if (oldid != -100) {
                findView(oldid).setTextColor(this.getResources().getColor(R.color.vs_gray_deep));
            }
        } else {
            if (oldid == -100) {
                oldid = view.getId();
                ((TextView) mParent.findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.croci));
            } else {
                findView(oldid).setTextColor(this.getResources().getColor(R.color.vs_gray_deep));
                oldid = view.getId();
                ((TextView) mParent.findViewById(oldid)).setTextColor(this.getResources().getColor(R.color.croci));
            }
        }
        for (int i = 0; i < mAzStr.length; i++) {
            if (mCurrentLetter.equals(mAzStr[i])) {
                now_index = i;
            }
        }
        if (now_index == 2) {
            tv_content1.setVisibility(View.GONE);
            tv_content2.setVisibility(View.VISIBLE);
            tv_content3.setVisibility(View.VISIBLE);
            tv_content2.setText(mAzStr[now_index - 2]);
            tv_content2.setTextColor(VsUtil.setTransparency(3));
            tv_content3.setText(mAzStr[now_index - 1]);
            tv_content3.setTextColor(VsUtil.setTransparency(2));
            setAD();
        } else if (now_index == 1) {
            tv_content1.setVisibility(View.GONE);
            tv_content2.setVisibility(View.GONE);
            tv_content3.setVisibility(View.VISIBLE);
            tv_content3.setText(mAzStr[now_index - 1]);
            tv_content3.setTextColor(VsUtil.setTransparency(2));
            setAD();
        } else if (now_index == 0) {
            tv_content1.setVisibility(View.GONE);
            tv_content2.setVisibility(View.GONE);
            tv_content3.setVisibility(View.GONE);
            setAD();
        } else if (now_index == 26) {
            tv_content5.setVisibility(View.GONE);
            tv_content6.setVisibility(View.GONE);
            tv_content7.setVisibility(View.GONE);
            tv_content8.setVisibility(View.GONE);
            tv_content9.setVisibility(View.GONE);
            setSZ();
        } else if (now_index == 25) {
            tv_content5.setVisibility(View.VISIBLE);
            tv_content6.setVisibility(View.GONE);
            tv_content7.setVisibility(View.GONE);
            tv_content8.setVisibility(View.GONE);
            tv_content9.setVisibility(View.GONE);
            tv_content5.setText(mAzStr[now_index + 1]);
            tv_content5.setTextColor(VsUtil.setTransparency(2));
            setSZ();
        } else if (now_index == 24) {
            tv_content5.setVisibility(View.VISIBLE);
            tv_content6.setVisibility(View.VISIBLE);
            tv_content7.setVisibility(View.GONE);
            tv_content8.setVisibility(View.GONE);
            tv_content9.setVisibility(View.GONE);
            tv_content5.setText(mAzStr[now_index + 1]);
            tv_content5.setTextColor(VsUtil.setTransparency(2));
            tv_content6.setText(mAzStr[now_index + 2]);
            tv_content6.setTextColor(VsUtil.setTransparency(3));
            setSZ();
        } else if (now_index == 23) {
            tv_content5.setVisibility(View.VISIBLE);
            tv_content6.setVisibility(View.VISIBLE);
            tv_content7.setVisibility(View.VISIBLE);
            tv_content8.setVisibility(View.GONE);
            tv_content9.setVisibility(View.GONE);
            tv_content5.setText(mAzStr[now_index + 1]);
            tv_content5.setTextColor(VsUtil.setTransparency(2));
            tv_content6.setText(mAzStr[now_index + 2]);
            tv_content6.setTextColor(VsUtil.setTransparency(3));
            tv_content7.setText(mAzStr[now_index + 3]);
            tv_content7.setTextColor(VsUtil.setTransparency(4));
            setSZ();
        } else if (now_index == 22) {
            tv_content5.setVisibility(View.VISIBLE);
            tv_content6.setVisibility(View.VISIBLE);
            tv_content7.setVisibility(View.VISIBLE);
            tv_content8.setVisibility(View.VISIBLE);
            tv_content9.setVisibility(View.GONE);
            tv_content5.setText(mAzStr[now_index + 1]);
            tv_content5.setTextColor(VsUtil.setTransparency(2));
            tv_content6.setText(mAzStr[now_index + 2]);
            tv_content6.setTextColor(VsUtil.setTransparency(3));
            tv_content7.setText(mAzStr[now_index + 3]);
            tv_content7.setTextColor(VsUtil.setTransparency(4));
            tv_content8.setText(mAzStr[now_index + 4]);
            tv_content8.setTextColor(VsUtil.setTransparency(5));
            setSZ();
        } else {
            tv_content1.setVisibility(View.VISIBLE);
            tv_content2.setVisibility(View.VISIBLE);
            tv_content3.setVisibility(View.VISIBLE);
            tv_content1.setText(mAzStr[now_index - 3]);
            tv_content1.setTextColor(VsUtil.setTransparency(4));
            tv_content2.setText(mAzStr[now_index - 2]);
            tv_content2.setTextColor(VsUtil.setTransparency(3));
            tv_content3.setText(mAzStr[now_index - 1]);
            tv_content3.setTextColor(VsUtil.setTransparency(2));
            setAD();
        }
        mCurrentLetterView.setVisibility(View.VISIBLE);
        mHandle.removeCallbacks(disapearThread);
        mHandle.postDelayed(disapearThread, 700);
        int localPosition = binSearch(VsPhoneCallHistory.CONTACTLIST, mCurrentLetter); // 接收返回值
        if (localPosition != -1) {
            mContactListView.setSelection(localPosition + 1); // 让List指向对应位置的Item
        }
    }

    /**
     * S-Z处理
     */
    private void setSZ() {
        tv_content1.setVisibility(View.VISIBLE);
        tv_content2.setVisibility(View.VISIBLE);
        tv_content3.setVisibility(View.VISIBLE);
        tv_content4.setVisibility(View.VISIBLE);
        tv_content1.setText(mAzStr[now_index - 3]);
        tv_content1.setTextColor(VsUtil.setTransparency(4));
        tv_content2.setText(mAzStr[now_index - 2]);
        tv_content2.setTextColor(VsUtil.setTransparency(3));
        tv_content3.setText(mAzStr[now_index - 1]);
        tv_content3.setTextColor(VsUtil.setTransparency(2));
        tv_content4.setText(mAzStr[now_index]);
        tv_content4.setTextColor(getResources().getColor(R.color.vs_white));
        // setTextColor();
    }

    /**
     * A-D处理
     */
    private void setAD() {
        tv_content4.setVisibility(View.VISIBLE);
        tv_content5.setVisibility(View.VISIBLE);
        tv_content6.setVisibility(View.VISIBLE);
        tv_content7.setVisibility(View.VISIBLE);
        tv_content8.setVisibility(View.VISIBLE);
        tv_content9.setVisibility(View.VISIBLE);
        tv_content4.setText(mAzStr[now_index]);
        tv_content4.setTextColor(getResources().getColor(R.color.vs_white));
        tv_content5.setText(mAzStr[now_index + 1]);
        tv_content5.setTextColor(VsUtil.setTransparency(2));
        tv_content6.setText(mAzStr[now_index + 2]);
        tv_content6.setTextColor(VsUtil.setTransparency(3));
        tv_content7.setText(mAzStr[now_index + 3]);
        tv_content7.setTextColor(VsUtil.setTransparency(4));
        tv_content8.setText(mAzStr[now_index + 4]);
        tv_content8.setTextColor(VsUtil.setTransparency(5));
        tv_content9.setText(mAzStr[now_index + 5]);
        tv_content9.setTextColor(VsUtil.setTransparency(6));
        // setTextColor();
    }

    /**
     * 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
     *
     * @author dell
     */
    private class DisapearThread implements Runnable {
        public void run() {
            if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
                mCurrentLetterView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 将选中的py与stringArr的首字符进行匹配并返回对应字符串在数组中的位置
     *
     * @param list
     * @param s
     * @return
     */
    private int binSearch(List<VsContactItem> list, String s) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (s.equalsIgnoreCase("" + list.get(i).mContactFirstLetter.charAt(0))) { // 不区分大小写
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        super.handleBaseMessage(msg);
        switch (msg.what) {
            // 重新检索数据
            case OPERATION_RESET_CONTACTS:
                break;
            // 更新数据
            case OPERATION_NOTIFY_DATASET:
                DataSetChangedNotify("");
                updateAgentInfo("");
                if (VsPhoneCallHistory.CONTACTLIST.size() <= 0) {
                    contacts_editext.setVisibility(View.VISIBLE);
                    mContactListView.setVisibility(View.VISIBLE);
                    loadcontactButton.setVisibility(View.VISIBLE);
                    loadcontactLinearlayout.setVisibility(View.VISIBLE);
                    mHeaderLayout.setVisibility(View.VISIBLE);
                    contact_num.setText("没有联系人");
                    contact_num.setVisibility(View.VISIBLE);
                } else {
                    contacts_editext.setVisibility(View.VISIBLE);
                    mContactListView.setVisibility(View.VISIBLE);
                    loadcontactButton.setVisibility(View.GONE);
                    loadcontactLinearlayout.setVisibility(View.GONE);
                    mHeaderLayout.setVisibility(View.VISIBLE);
                    contact_num.setVisibility(View.VISIBLE);
                /*
                 * 设置通讯录人数 author:黄文武 修改时间:14/10/08
				 */
                    contact_num.setText("共有" + VsPhoneCallHistory.CONTACTLIST.size() + "位联系人");
                }
                ctsKeywordEdt.setText("");
                adapter.setData(VsPhoneCallHistory.CONTACTLIST, VsPhoneCallHistory.CONTACTLIST.size());
                adapter.notifyDataSetChanged();
                break;
            case CURRENT_LOAD_CONTENT:
                contacts_editext.setVisibility(View.VISIBLE);
                mContactListView.setVisibility(View.VISIBLE);
                // loadcontactButton.setVisibility(View.VISIBLE);
                // loadcontactLinearlayout.setVisibility(View.VISIBLE);
                // loadcontactButton.setText(R.string.contacts_loadingcontacts);
                break;
            case MSG_ID_SHOWKCUSER:
                if (VsPhoneCallHistory.VSCONTACTLIST.size() > 0) {
                    groupSize.setVisibility(View.VISIBLE);

                    groupSize.setText("（" + VsPhoneCallHistory.VSCONTACTLIST.size() + "）");
                    // vsuserlayout.setOnClickListener(this);
                    adapter.notifyDataSetChanged();
                } else {
                    groupSize.setVisibility(View.GONE);
                }
                break;
            case MSG_ID_NO_CONTACTS:
                break;
            default:
                break;
        }
    }

    /**
     * 搜索处理
     *
     * @param s
     */
    private void DataSetChangedNotify(CharSequence s) {
        if (s.length() > 0) {
            searchInput = true;
            adapter.getFilter().filter(s);
        } else {
            searchInput = false;
            // ArrayList<KcContactItem> items = new ArrayList<KcContactItem>();
            // if (KcCoreService.COMMON_CONTACTLIST.size() == 0)
            // KcCommonContactHistory.selectContact(mContext);
            // items.addAll(KcCoreService.COMMON_CONTACTLIST);
            // items.addAll(KcCoreService.CONTACTLIST);
            adapter.setData(VsPhoneCallHistory.CONTACTLIST, VsPhoneCallHistory.CONTACTLIST.size());
            if (mContactListView.getAdapter() == null) {
                mContactListView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 联系人ListView滑动监听
     *
     * @author dell
     */
    private class ListViewScrollListener implements OnScrollListener {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (VsPhoneCallHistory.CONTACTLIST != null) {
                int countSize = VsPhoneCallHistory.CONTACTLIST.size();
                // 以第一个ListItem为标准项来显示
                // if (firstVisibleItem < KcCoreService.COMMON_CONTACTLIST.size()) {
                // if (mCurrentLetterView != null) {
                // mCurrentLetterView.setVisibility(View.INVISIBLE);
                // }
                // if (oldid != -100) {
                // ((TextView)
                // mParent.findViewById(oldid)).setTextColor(mContext.getResources().getColor(R.color.Black));
                // }
                // } else
                if (VsPhoneCallHistory.CONTACTLIST != null && mCurrentLetterView != null && countSize > 0) {

                    // firstVisibleItem表示在现时屏幕第一个ListItem(部分显示的ListItem也算)在整个ListView的位置（下标从0开始）
                    // System.out.println("***firstParamInt:" + firstVisibleItem);
                    // // visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
                    // System.out.println("***visibleItemCount:" + visibleItemCount);
                    // // totalItemCount表示ListView的ListItem总数
                    // System.out.println("***totalItemCount:" + totalItemCount);
                    // //
                    // listView.getLastVisiblePosition()表示在现时屏幕最后一个ListItem(最后ListItem要完全显示出来才算)在整个ListView的位置（下标从0开始）
                    // System.out.println("****" + String.valueOf(view.getLastVisiblePosition()));
                    // try {
                    // String contactPinYin = KcCoreService.CONTACTLIST.get(firstVisibleItem - 1
                    // - KcCoreService.COMMON_CONTACTLIST.size()).mContactFirstLetter;
                    // mCurrentLetter = contactPinYin.substring(0, 1);
                    // CustomLog.i(TAG, "mCurrentLetter = " + mCurrentLetter);
                    // int i = 1;
                    // do {
                    // if (mCurrentLetter.equals(mAzStr[i]) && mAzId[i] != oldid) {
                    // if (oldid != -100) {
                    // ((TextView)
                    // findViewById(oldid)).setTextColor(mContext.getResources().getColor(
                    // R.color.Black));
                    // }
                    // oldid = mAzId[i];
                    // ((TextView)
                    // findViewById(oldid)).setTextColor(mContext.getResources().getColor(
                    // R.color.croci));
                    // break;
                    // }
                    // i++;
                    // } while (i < mAzStr.length);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    try {
                        System.err.println("****" + view.getFirstVisiblePosition());
                        String contactPinYin = null;
                        // if (view.getFirstVisiblePosition() == 0) {
                        // contactPinYin =
                        // KcPhoneCallHistory.CONTACTLIST.get(view.getFirstVisiblePosition()).mContactFirstLetter;
                        // }
                        // else {
                        contactPinYin = VsPhoneCallHistory.CONTACTLIST.get(view.getFirstVisiblePosition()).mContactFirstLetter;
                        // }
                        mCurrentLetter = contactPinYin.substring(0, 1);
                        CustomLog.i(TAG, "mCurrentLetter = " + mCurrentLetter);
                        int i = 1;
                        do {
                            if (mCurrentLetter.equals(mAzStr[i]) && mAzId[i] != oldid) {
                                if (oldid != -100) {
                                    findView(oldid).setTextColor(mContext.getResources().getColor(R.color.vs_gray_deep));
                                }
                                oldid = mAzId[i];
                                ((TextView) mParent.findViewById(oldid)).setTextColor(mContext.getResources().getColor(R.color.croci));
                                break;
                            }
                            i++;
                        } while (i < mAzStr.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    System.err.println("滚动中");
                    break;
                case OnScrollListener.SCROLL_STATE_FLING:
                    System.err.println("开始滚动");
                    break;
            }
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mContext.getWindow().getDecorView().getWindowToken(), 0);
                }
                // mInputKeyboard.setVisibility(View.GONE);
            }
        }
    }

    private TextView findView(int id) {
        return ((TextView) mParent.findViewById(id));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 禁止使用回车键
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isInit) {
            return;
        }
        if (windowManager != null) {
            if (mCurrentLetterView != null) {
                // windowManager.removeView(mCurrentLetterView);
            }
            windowManager = null;
        }
        if (mReceiver != null) {
            if (getActivity() == null) {
                return;
            }
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("HomeExit", true);
    }
}
