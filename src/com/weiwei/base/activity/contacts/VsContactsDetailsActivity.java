package com.weiwei.base.activity.contacts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.VsContactsSelectActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.ConverToPingying;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.util.CustomAlertDialog;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.base.widgets.DrawableCenterTextView;
import com.weiwei.home.utils.ArmsUtils;
import com.weiwei.home.utils.LogUtils;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.ArrayList;

/**
 * @Title:Android客户端
 * @Description: 联系人详情
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-14
 */
@SuppressLint("ResourceAsColor")
public class VsContactsDetailsActivity extends VsBaseActivity implements OnClickListener {
    private final String TAG = "VsContactsDetailsActivity";
    /**
     * 弹出选择分享方式
     */
    private Dialog dialog_share;
    /**
     * 移动动画
     */
    private Animation mTranslateAnimation;
    /**
     * 邀请移动动画
     */
//	private View v = null;
    /**
     * 选中的名片号码
     */
    private ArrayList<String> isCheckNum = new ArrayList<String>();
    /**
     * 加载listView视图信号
     */
    private boolean isSelectPhoneCard = false;

    private ImageButton vs_send_message;

    /**
     * 取消传名片按钮(TextView做的)
     */
    // private TextView vs_sendphonecard_cancel;

    /**
     * 免费拨打
     */
    private DrawableCenterTextView vs_contact_detail_freecall;
    /**
     * 免费信息
     */
    private DrawableCenterTextView vs_contact_detail_freemsg;
    // 免费这里的分隔线
    private View line_freemsg_layout;

    /**
     * 呼叫按钮
     */
    private LinearLayout vs_contact_detail_call;

    private LinearLayout vs_contact_detail_invitation;
    /**
     * 联系人名字
     */
    private TextView vs_contact_name;
    /**
     * 电话列表
     */
    private ListView vs_contact_detail_listview;
    /**
     * 联系人对象
     */
    private VsContactItem contactDetailsItem = null;
    /**
     * 通话记录条目
     */
    private VsCallLogListItem calllogListItem = null;

    ArrayList<Object> list = new ArrayList<Object>();
    /**
     * 联系人详情适配器
     */
    private VsContactDetailsAdapter adapter = null;
    /**
     * 邀请弹框点击事件
     */
    private ArrayList<View.OnClickListener> invite_click_listener;
    /**
     * 拨打弹框点击时间
     */
    private ArrayList<View.OnClickListener> dial_click_listener;
    /**
     * 免费拨打弹框点击时间
     */
    private ArrayList<View.OnClickListener> freeDial_click_listener;
    /**
     * 免费电话的号码
     */
    private ArrayList<String> freeNumber;
    /**
     * 多个联系人拨打和邀请弹框点击弹出框
     */
    private CustomAlertDialog dialog;
    /**
     * 联系人变化更新界面
     */
    private final char MSG_updateContact = 1000;

    private int listViewHeight = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_contacts_details);
        FitStateUtils.setImmersionStateMode(this, R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getString(R.string.vs_contact_detail_title));
        showLeftNavaBtn(R.drawable.icon_back);
        showRightImage();
//		showRightTxtBtn(getString(R.string.vs_contact_detail_edit_contacts));
        initData();
        initView();
        // initDialPhoneNumber();
//		v = initAnimation();
        IntentFilter netChangeFilter = new IntentFilter();
        // netChangeFilter.addAction(GlobalVariables.action_contact_detail_change);
        // mContext.registerReceiver(actionContactChange, netChangeFilter);
        netChangeFilter.addAction(VsUserConfig.JKey_GET_VSUSER_OK);
        netChangeFilter.addAction(VsUserConfig.JKey_GET_VSUSER_FAIL);
        mContext.registerReceiver(actionContactChange, netChangeFilter);
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    private BroadcastReceiver actionContactChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * String action = intent.getAction(); if
             * (action.equals(GlobalVariables.action_contact_detail_change)) {//
             * 加载完成 CustomLog.i(TAG,"VsContactsDetailsActivity" );
             * mBaseHandler.sendEmptyMessage(MSG_updateContact);}
             */
            String action = intent.getAction();
            if (action.equals(VsUserConfig.JKey_GET_VSUSER_OK) || action.equals(VsUserConfig.JKey_GET_VSUSER_FAIL)) {// 加载完成
                CustomLog.i(TAG, "VsContactsDetailsActivity接收广播");
                mBaseHandler.sendEmptyMessage(MSG_updateContact);
            }
        }
    };

    /**
     * 更新联系人
     */
    public void updateContact() {

        CustomLog.i(TAG, "进updateContact");
        Cursor cursor = VsPhoneCallHistory.getsignalContacts(mContext, contactDetailsItem.mContactId);
        try {
            if (cursor == null) {
                // 联系人取不到。表示联系人被删除？待验证

                return;
            }

            int beforePhoneSize = contactDetailsItem.phoneNumList.size();// 用于判断ListView更改之前的高度
            /**
             * 重新绑定事件监听
             */
            if (dial_click_listener == null) {
                dial_click_listener = new ArrayList<View.OnClickListener>();
            } else {
                dial_click_listener.clear();
            }
            if (invite_click_listener == null) {
                invite_click_listener = new ArrayList<View.OnClickListener>();
            } else {
                invite_click_listener.clear();
            }
            if (freeDial_click_listener == null) {
                freeDial_click_listener = new ArrayList<View.OnClickListener>();
            } else {
                freeDial_click_listener.clear();
            }
            if (freeNumber == null) {
                freeNumber = new ArrayList<String>(3);
            } else {
                freeNumber.clear();
            }
            contactDetailsItem.phoneNumList.clear();

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                String name = ConverToPingying.replaceString(cursor.getString(0));
                String number = "";
                try {
                    if (cursor.getColumnCount() > 1) {
                        number = cursor.getString(1).trim();

                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                number = VsPhoneCallHistory.removePrefix(number).replaceAll("-", "").replaceAll(" ", "");
                if (name != null && name.length() == 0) {// 姓名为空白时
                    name = number;
                }
                contactDetailsItem.mContactName = name;
                contactDetailsItem.mContactPhoneNumber = number;
                vs_contact_name.setText(contactDetailsItem.mContactName);
                contactDetailsItem.phoneNumList.add(number);

                /*
                 * 绑定事件监听
                 */
                if (VsUtil.checheNumberIsVsUser(mContext, number)) {// 检查是否是号码

                    // 免费拨打事件
                    freeNumber.add(number);
                    CustomLog.i(TAG, "免费拨打里面" + number);
                    freeDial_click_listener.add(new dialClickListen(number, "1"));

                    if (i == cursor.getCount() - 1) { // 添加一个 取消按钮 的监听
                        freeDial_click_listener.add(new CancelClickListener());
                    }

                }
                // 拨打事件
                dial_click_listener.add(new dialClickListen(number, ""));
                // 邀请事件
                invite_click_listener.add(new inviteClickListen(number));

                if (i == cursor.getCount() - 1) {// 添加一个 取消按钮 的监听
                    dial_click_listener.add(new CancelClickListener());
                    invite_click_listener.add(new CancelClickListener());
                }

            }
            if (contactDetailsItem.phoneNumList.size() <= 0) {
                contactDetailsItem.phoneNumList.add(contactDetailsItem.mContactPhoneNumber);
                contactDetailsItem.localNameList.add(contactDetailsItem.mContactBelongTo);
            }

            list.clear();
            list.add(contactDetailsItem);

            CustomLog.i(TAG, "freeNumber" + freeNumber.size());
            if (freeNumber.size() > 0) {// 更新控件显示
                contactDetailsItem.isVsUser = true;
                vs_contact_detail_freecall.setVisibility(View.VISIBLE);
                vs_contact_detail_freemsg.setVisibility(View.VISIBLE);
                line_freemsg_layout.setVisibility(View.VISIBLE);
            } else {
                contactDetailsItem.isVsUser = false;
                vs_contact_detail_freecall.setVisibility(View.GONE);
                vs_contact_detail_freemsg.setVisibility(View.GONE);
                line_freemsg_layout.setVisibility(View.GONE);
            }

            adapter.isSelectPhoneCard = isSelectPhoneCard;
            adapter.notifyDataSetChanged();// 更新ListView
            /**
             * 调整ListView的高度
             */
            if (beforePhoneSize < 2) {// 240
                if (contactDetailsItem.phoneNumList.size() > 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 2.5);// 600
                    vs_contact_detail_listview.setLayoutParams(lp);
                } else if (contactDetailsItem.phoneNumList.size() == 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 2);// 480
                    vs_contact_detail_listview.setLayoutParams(lp);
                }
            } else if (beforePhoneSize == 2) {// 480
                if (contactDetailsItem.phoneNumList.size() > 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 1.25);// 600
                    vs_contact_detail_listview.setLayoutParams(lp);
                } else if (contactDetailsItem.phoneNumList.size() < 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 0.5);// 240
                    vs_contact_detail_listview.setLayoutParams(lp);
                }
            } else {// 600
                if (contactDetailsItem.phoneNumList.size() == 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 0.8);// 480
                    vs_contact_detail_listview.setLayoutParams(lp);
                } else if (contactDetailsItem.phoneNumList.size() < 2) {
                    android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                    lp.height = (int) (lp.height * 0.4);// 240
                    vs_contact_detail_listview.setLayoutParams(lp);
                }
            }

            // 初始化自定义控件
            vs_contact_detail_freecall.invalidate();
            // vs_contact_detail_call.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        /*
         * //通知更新联系人 KcPhoneCallHistory.loadContactData(mContext, 1);
         *
         * if (mContactId != null && adapter != null) {// 判断是否是编辑联系人后重新进入 for
         * (KcContactItem item : KcPhoneCallHistory.CONTACTLIST) { if (item !=
         * null && item.mContactId.equals(mContactId)) { contactDetailsItem =
         * item; // 把数据添加到链表中 list.clear(); list.add(contactDetailsItem);
         * adapter.notifyDataSetChanged(); } } }
         */
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
        vs_contact_name = (TextView) findViewById(R.id.vs_contact_name);
        vs_contact_detail_freecall = (DrawableCenterTextView) findViewById(R.id.vs_contact_detail_freecall);
        vs_contact_detail_freemsg = (DrawableCenterTextView) findViewById(R.id.vs_contact_detail_freemsg);
        line_freemsg_layout = findViewById(R.id.line_freemsg_layout);
        vs_contact_detail_call = (LinearLayout) findViewById(R.id.vs_contact_detail_call);
        vs_contact_detail_invitation = (LinearLayout) findViewById(R.id.vs_contact_detail_invitation);
        vs_contact_detail_listview = (ListView) findViewById(R.id.vs_contact_detail_listview);
        vs_send_message = (ImageButton) findViewById(R.id.send_message);
        // vs_sendphonecard_cancel=(TextView)
        // findViewById(R.id.vs_sendphonecard_cancel);

        // 设置监听事件
        vs_contact_detail_freecall.setOnClickListener(this);
        vs_contact_detail_freemsg.setOnClickListener(this);
        vs_contact_detail_call.setOnClickListener(this);
        vs_contact_detail_invitation.setOnClickListener(this);
        vs_send_message.setOnClickListener(this);
        // vs_sendphonecard_cancel.setOnClickListener(this);

        if (contactDetailsItem == null) return;
        if (contactDetailsItem.mContactName != null) {
            vs_contact_name.setText(contactDetailsItem.mContactName);
        } else if (contactDetailsItem.phoneNumList != null && contactDetailsItem.phoneNumList.size() > 0) {
            vs_contact_name.setText(contactDetailsItem.phoneNumList.get(0));
        }
        // 如果是KC好友 需要显示签名。界面按钮文案和点击事件 需要保证Cliendid 不为空
        if (contactDetailsItem.isVsUser) {
            vs_contact_detail_call.setVisibility(View.VISIBLE);
            vs_contact_detail_invitation.setVisibility(View.GONE);
            // 不满足拨打条件
            /*
             * if (!(KcUserConfig.getDataBoolean(mContext,
             * KcUserConfig.JKey_Free_CallState, true) && KcUtil
             * .isWifi(mContext))) {
             * kc_contact_detail_invite.setBackgroundDrawable
             * (getResources().getDrawable(R.drawable.btn_notclick));
             * kc_contact_detail_invite.setOnClickListener(new OnClickListener()
             * {
             *
             * @Override public void onClick(View v) { // TODO Auto-generated
             * method stub if (KcUserConfig.getDataBoolean(mContext,
             * KcUserConfig.JKey_Free_CallState, true)) {
             * mToast.show(getResources
             * ().getString(R.string.no_freedial_promit_network), 2000); } else
             * { mToast.show(getResources().getString(R.string.
             * no_freedial_promit_close), 2000); } } }); }
             */
        } else {
            vs_contact_detail_call.setVisibility(View.GONE);
            vs_contact_detail_invitation.setVisibility(View.VISIBLE);
            vs_contact_detail_freecall.setVisibility(View.GONE);
            vs_contact_detail_freemsg.setVisibility(View.GONE);
            line_freemsg_layout.setVisibility(View.GONE);
            findViewById(R.id.line_freemsg_layout).setVisibility(View.GONE);
        }

        if (GlobalVariables.width == 0) {
            // 获取屏幕宽高与密度
            VsUtil.setDensityWH(this);
        }
        adapter = new VsContactDetailsAdapter(this, list, GlobalVariables.width, mBaseHandler);
        adapter.isSelectPhoneCard = isSelectPhoneCard;
        vs_contact_detail_listview.setAdapter(adapter);
        vs_contact_detail_listview.setDivider(null);
        // 联系人详情中的号码
        initDialogClick();

    }

    public void initDialogClick() {
        if (contactDetailsItem.phoneNumList.size() > 1) {
            if (contactDetailsItem.phoneNumList.size() > 2) {// 大于2个号码
                android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                listViewHeight = lp.height;
                lp.height = (int) (lp.height * 2.5);
                vs_contact_detail_listview.setLayoutParams(lp);

            } else {// 刚好2个号码
                android.view.ViewGroup.LayoutParams lp = vs_contact_detail_listview.getLayoutParams();
                lp.height = lp.height * 2;
                listViewHeight = lp.height;
                vs_contact_detail_listview.setLayoutParams(lp);

            }
            dial_click_listener = new ArrayList<View.OnClickListener>();
            invite_click_listener = new ArrayList<View.OnClickListener>();
            freeDial_click_listener = new ArrayList<View.OnClickListener>();
            for (int i = 0; i < contactDetailsItem.phoneNumList.size(); i++) {
                // 拨打事件
                dial_click_listener.add(new dialClickListen(contactDetailsItem.phoneNumList.get(i), ""));
                invite_click_listener.add(new inviteClickListen(contactDetailsItem.phoneNumList.get(i)));
                /*
                 * 添加一个 取消按钮 的监听 author:黄文武 时间:2014/10/11
                 */
                if (i == contactDetailsItem.phoneNumList.size() - 1) {
                    dial_click_listener.add(new CancelClickListener());
                    invite_click_listener.add(new CancelClickListener());
                }
                // 邀请事件

                // 免费拨打事件
                if (contactDetailsItem.mContactCliendid.size() > i && contactDetailsItem.mContactCliendid.get(i) != null && !"null".equals(contactDetailsItem.mContactCliendid
                        .get(i)) && !"".equals(contactDetailsItem.mContactCliendid.get(i)) && contactDetailsItem.mContactCliendid.get(i).length() > 2) {
                    if (freeNumber == null) {
                        freeNumber = new ArrayList<String>(3);
                    }
                    freeNumber.add(contactDetailsItem.phoneNumList.get(i));
                    freeDial_click_listener.add(new dialClickListen(contactDetailsItem.phoneNumList.get(i), "1"));
                    /*
                     * 添加一个 取消按钮 的监听 author:黄文武 时间:2014/10/11
                     */
                    if (i == contactDetailsItem.phoneNumList.size() - 1) {
                        freeDial_click_listener.add(new CancelClickListener());
                    }
                }
            }
        }
    }

    /**
     * 移除选中号码
     */
    public static final char MSG_ID_DELETESEND_CONTACT = 12;
    /**
     * 添加选中号码
     */
    public static final char MSG_ID_ADDSEND_CONTACT = 22;

    protected void handleBaseMessage(Message msg) {

        String contactPhoneNumber = msg.getData().getString("ContactPhoneNumber");// 获取号码
        switch (msg.what) {
            case MSG_updateContact:

                updateContact();
                // 初始化点击事件 initDialogClick();
                if (contactDetailsItem.phoneNumList.size() > 0) {// 通知更新通话记录-数据库中的联系人信息
                    VsPhoneCallHistory.updateCallLog(mContext, contactDetailsItem.mContactName, contactDetailsItem.phoneNumList);
                }

                break;
            case MSG_ID_ADDSEND_CONTACT:
                isCheckNum.add(contactPhoneNumber);

                break;
            case MSG_ID_DELETESEND_CONTACT:
                isCheckNum.remove(contactPhoneNumber);

                break;
            default:
                break;
        }
        if (isCheckNum.size() > 0) {
            mBtnNavRightTv.setVisibility(View.GONE);// 隐藏编辑按钮
        } else {
            mBtnNavRightTv.setVisibility(View.VISIBLE);// 显示编辑按钮
        }
    }

    private class dialClickListen implements View.OnClickListener {
        String callNumber;
        String dialType;

        public dialClickListen(String number, String dialType) {
            this.callNumber = number;
            this.dialType = dialType;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dialog.dismiss();
            vsCallBackChoice(dialType, callNumber, "");
            CustomLog.i(TAG, dialType + "拨打选中的号码" + callNumber);

        }
    }

    ;

    private class CancelClickListener implements View.OnClickListener {

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dialog.dismiss();
        }

    }

    private class inviteClickListen implements View.OnClickListener {
        String callNumber;

        public inviteClickListen(String number) {
            this.callNumber = number;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dialog.dismiss();
            vsContactInvite(callNumber);
            CustomLog.i(TAG, "邀请选中的号码" + callNumber);

        }
    }

    ;

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("contact_type", 0);
        if (type == 1) {// 从通话记录到联系人详情
            calllogListItem = (VsCallLogListItem) intent.getSerializableExtra("CALLLOGDETAILSDATA");
            if (calllogListItem != null) {
                CustomLog.i("vsdebug", "从通话记录到联系人-----");
                for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                    if (item != null && item.mContactName.equals(calllogListItem.getFirst().callName)) {
                        for (String phoneNumStr : item.phoneNumList) {
                            if (calllogListItem.getFirst().callNumber.equals(phoneNumStr)) {
                                contactDetailsItem = item;
                                CustomLog.i("vsdebug", "匹配到联系人-----" + phoneNumStr);
                                // 把数据添加到链表中
                                list.clear();
                                list.add(contactDetailsItem);
                                return;
                            }
                        }
                    }
                }
                if (contactDetailsItem == null) {
                    contactDetailsItem = new VsContactItem();
                    contactDetailsItem.mContactName = calllogListItem.getFirst().callName;
                    contactDetailsItem.mContactPhoneNumber = calllogListItem.getFirst().callNumber;
                    contactDetailsItem.mContactBelongTo = calllogListItem.getLocalName();
                    contactDetailsItem.phoneNumList.add(contactDetailsItem.mContactPhoneNumber);
                    contactDetailsItem.localNameList.add(contactDetailsItem.mContactBelongTo);
                } else if (contactDetailsItem.phoneNumList.size() <= 0) {
                    contactDetailsItem.phoneNumList.add(contactDetailsItem.mContactPhoneNumber);
                    contactDetailsItem.localNameList.add(contactDetailsItem.mContactBelongTo);
                }
                // 把数据添加到链表中
                list.clear();
                list.add(contactDetailsItem);
            } else {
                mToast.show("未获取到数据", Toast.LENGTH_SHORT);
            }

        } else if (type == 2) {// 从联系人到联系人详情
            contactDetailsItem = intent.getParcelableExtra("CONTACTDETAILSDATA");
            if (contactDetailsItem == null) {
                return;
            }
            if (contactDetailsItem.phoneNumList.size() <= 0) {
                contactDetailsItem.phoneNumList.add(contactDetailsItem.mContactPhoneNumber);
                contactDetailsItem.localNameList.add(contactDetailsItem.mContactBelongTo);
            }
            // 把数据添加到链表中
            list.clear();
            list.add(contactDetailsItem);

        } else {
            mToast.show("未获取到数据", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void HandleRightNavBtn() {
        if (null != contactDetailsItem.mContactId && !"".equals(contactDetailsItem.mContactId)) {

            VsUtil.updateContact(mContext, contactDetailsItem.mContactId);
        } else {
            mToast.show("请先添加联系人", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        showDialogNow(false);
        switch (v.getId()) {
            // case R.id.vs_calllong_invite_text:
            // MobclickAgent.onEvent(mContext, "Invite_ConDetails");
            // if (contactDetailsItem.phoneNumList.size() > 1) {
            // dialog = VsUtil.showChoose(mContext, contactDetailsItem.mContactName,
            // invite_click_listener,
            // contactDetailsItem.phoneNumList);
            // } else {
            // vsContactInvite(contactDetailsItem.mContactPhoneNumber);// 邀请好友
            // }
            // break;
            case R.id.vs_contact_detail_freecall:
                if (freeNumber != null && freeNumber.size() > 1) {
                    dialog = VsUtil.showChoose(mContext, contactDetailsItem.mContactName, freeDial_click_listener, freeNumber);

                } else if (freeNumber != null && freeNumber.size() == 1) {
                    vsCallBackChoice("1", freeNumber.get(0), contactDetailsItem.mContactBelongTo);

                } else {
                    vsCallBackChoice("1", contactDetailsItem.mContactPhoneNumber, contactDetailsItem.mContactBelongTo);

                }
                break;
            case R.id.vs_contact_detail_call:// 呼叫
                if (contactDetailsItem.phoneNumList.size() > 1) {
                    dialog = VsUtil.showChoose(mContext, contactDetailsItem.mContactName, dial_click_listener, contactDetailsItem.phoneNumList);
                } else {
                    // 回拨dialType
                    vsCallBackChoice("1", contactDetailsItem.mContactPhoneNumber, contactDetailsItem.mContactBelongTo);
                }
                break;
            case R.id.vs_contact_detail_invitation:// 发送消息
                vsContactInvite(contactDetailsItem.mContactPhoneNumber);// 邀请好友
                break;
            case R.id.send_message:// 发送消息
                if (contactDetailsItem.phoneNumList.size() > 1) {
                    dialog = VsUtil.showChoose(mContext, contactDetailsItem.mContactName, invite_click_listener, contactDetailsItem.phoneNumList);
                } else {
                    vsContactInvite(contactDetailsItem.mContactPhoneNumber);// 邀请好友
                }
                break;
            case R.id.vs_contact_detail_freemsg:
                /*
                 * Intent intent=new Intent(this,MessageListActivity.class); String
                 * phone=""; try { phone = new
                 * JSONObject(VsUserConfig.getDataString(mContext,
                 * VsUserConfig.JKEY_GETVSUSERINFO
                 * )).getString(contactDetailsItem.mContactPhoneNumber);
                 * CustomLog.i("GDK", "cliendid=" + phone); } catch (Exception e) {
                 * e.printStackTrace(); phone =
                 * contactDetailsItem.mContactPhoneNumber; }
                 */
                /*
                 * Bundle mBundle = new Bundle();
                 * intent.putExtra("freeMesNumber",phone);
                 * intent.putExtra("freename", contactDetailsItem.mContactName); //
                 * intent.putExtra("contactDetailsItem", contactDetailsItem);
                 * //intent.putExtras(extras)
                 * mBundle.putParcelable("contactDetailsItem", (Parcelable)
                 * contactDetailsItem); intent.putExtras(mBundle);
                 * startActivity(intent);
                 */
//			VsUtil.sendFressMsg(mContext, contactDetailsItem,
//					contactDetailsItem.mContactUid.get(0));
                break;
            // case R.id.vs_sendphonecard_cancel://取消发送名片按钮
            // loggle();
            // if(!isSelectPhoneCard){//为false时
            // adapter.isSelectPhoneCard=isSelectPhoneCard;
            // adapter.havaInit = -1 ;
            // isCheckNum.clear();
            // vs_sendphonecard_image.getBackground().setAlpha(255);
            // vs_sendphonecard_confirm.setText("传名片");
            // VsUtil.setTextViewColor(mContext, vs_sendphonecard_confirm,
            // R.color.vs_black_text_selecter);//设置点击效果
            // vs_sendphonecard_cancel.setVisibility(View.GONE);
            // adapter.notifyDataSetChanged();
            // mBtnNavRightTv.setVisibility(View.VISIBLE);//显示编辑按钮
            // }
            //
            // break;
            case R.id.tv_message:// 短信发送名片
            {

                Intent intent1 = new Intent(mContext, VsContactsSelectActivity.class);
                intent1.putExtra("SENDCARDCONTACTSENDSMS", true);// 添加标识
                intent1.putExtra("phoneCardInfo", appendPhoneCardInfo());// 名片信息
                startActivity(intent1);
            }
            break;
            case R.id.tv_weixin:// 微信发送名片
                VsUtil.weixinShare(mContext, appendPhoneCardInfo());
                break;

            default:
                break;
        }

    }

    String phoneNumber = null;
    String local = null;
    String KcPhoneNumber = null;
    String Kclocal = null;

    public void initDialPhoneNumber() {
        try {
            if (contactDetailsItem.phoneNumList.size() > 1) {// 判断是否有多个号码
                for (int i = contactDetailsItem.phoneNumList.size() - 1; i > 0; i--) {
                    if (VsUtil.checkMobilePhone(contactDetailsItem.phoneNumList.get(i))) {// 过滤固话
                        if (contactDetailsItem.isVsUser && contactDetailsItem.ISVsPhone.size() > i && contactDetailsItem.ISVsPhone.get(i)) {
                            if (contactDetailsItem.ISVsPhone.size() > i && contactDetailsItem.mContactCliendid.get(i) != null && !"null".equals(contactDetailsItem
                                    .mContactCliendid.get(i)) && !"".equals(contactDetailsItem.mContactCliendid.get(i)) && contactDetailsItem.mContactCliendid.get(i).length() >
                                    2) {
                                KcPhoneNumber = contactDetailsItem.phoneNumList.get(i);
                                phoneNumber = KcPhoneNumber;
                                if (contactDetailsItem.localNameList.size() > i) {
                                    Kclocal = contactDetailsItem.localNameList.get(i);
                                }
                                break;
                            }
                        } else {
                            phoneNumber = contactDetailsItem.phoneNumList.get(i);
                            if (contactDetailsItem.localNameList.size() > i) {
                                local = contactDetailsItem.localNameList.get(i);
                            }
                            break;
                        }
                    }
                }
            } else if (contactDetailsItem.phoneNumList.size() > 0) {// 当联系人有电话号码时才选第一个
                phoneNumber = contactDetailsItem.phoneNumList.get(0);
                if (contactDetailsItem.localNameList.size() > 0) {
                    local = contactDetailsItem.localNameList.get(0);
                }
                if (contactDetailsItem.isVsUser) {
                    KcPhoneNumber = contactDetailsItem.phoneNumList.get(0);
                    if (contactDetailsItem.localNameList.size() > 0) {
                        Kclocal = contactDetailsItem.localNameList.get(0);
                    }
                }
            }
            if (contactDetailsItem.phoneNumList.size() > 0) {// 当联系人有电话号码时才选第一个
                if (phoneNumber == null) {// 如果没有手机号默认选择第一个号码拨打
                    phoneNumber = contactDetailsItem.phoneNumList.get(0);
                    if (contactDetailsItem.localNameList.size() > 0) {
                        local = contactDetailsItem.localNameList.get(0);
                    }
                }
                if (KcPhoneNumber == null) {
                    KcPhoneNumber = contactDetailsItem.phoneNumList.get(0);
                    if (contactDetailsItem.localNameList.size() > 0) {
                        Kclocal = contactDetailsItem.localNameList.get(0);
                    }
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 拨打筛选
     */
    public void vsCallBackChoice(String dialType, String number, String locaString) {

        if (VsUtil.isLogin(getResources().getString(R.string.recommend_friends_prompt), mContext)) {

            if (VsUtil.checheNumberIsVsUser(mContext, number)) {
                // if (dialType.equals("1")) {
                // 免费电话
                VsUtil.callNumber(contactDetailsItem.mContactName, number, Kclocal, mContext, "1", true, getFragmentManager());
                // } else {
                // // 省钱拨打
                // VsUtil.callNumber(contactDetailsItem.mContactName, number,
                // locaString, mContext, dialType, false);
                // }
            } else {
                mToast.show("此号码不是好友！", Toast.LENGTH_SHORT);
            }

        } else {
            mToast.show("请先添加联系人号码！", Toast.LENGTH_SHORT);
        }

    }

    /**
     * 邀请筛选
     */
    public void vsContactInvite(String number) {
        if (VsUtil.isLogin(getResources().getString(R.string.recommend_friends_prompt), mContext)) {
            if (contactDetailsItem != null && contactDetailsItem.phoneNumList.size() > 0) {

                // String phoneNumber = null;
                // if (contactDetailsItem.phoneNumList.size() > 0) {//
                // 判断是否有多个号码
                // for (int i = contactDetailsItem.phoneNumList.size() - 1;
                // i > -1; i--) {
                // if
                // (KcUtil.checkMobilePhone(contactDetailsItem.phoneNumList.get(i)))
                // {// 过滤固话
                // phoneNumber = contactDetailsItem.phoneNumList.get(i);
                // break;
                // }
                // }
                // } else {
                // phoneNumber = contactDetailsItem.phoneNumList.get(0);
                // }
                if (number == null) {// 如果没有手机号提示反回
                    mToast.show("联系人无手机号！", Toast.LENGTH_SHORT);
                    return;
                }

                String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
                if ((mRecommendInfo == null) || "".equals(mRecommendInfo)) {
                    String InviteFriendInfo = DfineAction.InviteFriendInfo;
                    String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
                    if (uid != null && !"".equals(uid)) {
                        StringBuilder builder = new StringBuilder(InviteFriendInfo);
                        builder.append("a=").append(uid).append("&s=sm");
                        mRecommendInfo = new String(builder);
                    } else {
                        mRecommendInfo = DfineAction.InviteFriendInfo;
                    }
                } else {
                    mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_MY_SHARE);
                }
                SendNoteObserver.sendSendNoteNumber = 1;
                // 发送邀请短信
                VsUtil.smsShare(mContext, mRecommendInfo, number);
                /*
                 * try { Intent intent = new Intent(Intent.ACTION_SENDTO,
                 * Uri.fromParts("smsto", phoneNumber, null));
                 * intent.putExtra("sms_body", mRecommendInfo);
                 * startActivity(intent); } catch (Exception e) { // TODO
                 * Auto-generated catch block e.printStackTrace();
                 * mToast.show
                 * (getResources().getString(R.string.sendmessage_fail),
                 * Toast.LENGTH_SHORT); }
                 */

            } else {
                mToast.show("请先添加联系人号码！", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 拼接名片信息
     *
     * @return
     */
    private String appendPhoneCardInfo() {
        StringBuilder builder = new StringBuilder("姓名:" + contactDetailsItem.mContactName);// 拼接内容
        if (isCheckNum.size() == 0) {
            builder.append("\n号码:" + contactDetailsItem.phoneNumList.get(0));
        } else {
            for (String checkNum : isCheckNum) {
                builder = builder.append("\n").append("号码:").append(checkNum);
            }
        }
        return builder.toString();
    }

    /**
     * loggle赋值
     */
    private void loggle() {
        if (isSelectPhoneCard) {
            isSelectPhoneCard = false;
        } else {
            isSelectPhoneCard = true;
        }
    }

    /**
     * 显示分享方式
     *
     * @param show 是否显示
     */
    public void showDialogNow(boolean show) {
        if (show) {
//			v.startAnimation(mTranslateAnimation);
            dialog_share.show();
        } else {
            if (dialog_share != null && dialog_share.isShowing()) {
                dialog_share.dismiss();
            }

        }

    }

    /**
     * 显示要求对话框
     *
     * @return
     */
//	private View initAnimation() {
//		dialog_share = new Dialog(mContext, R.style.CommonDialogStyle);
//		final View v = View.inflate(mContext,
//				R.layout.vs_contactsdetail_sendfriendcard_dlog, null);
//		v.findViewById(R.id.tv_message).setOnClickListener(this);
//		v.findViewById(R.id.tv_weixin).setOnClickListener(this);
//		v.findViewById(R.id.btn_wait).setOnClickListener(this);
//		dialog_share.setContentView(v);
//		dialog_share.setCanceledOnTouchOutside(true);
//		dialog_share.setCancelable(true);
//		/*
//		 * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
//		 * 对象,这样这可以以同样的方式改变这个Activity的属性.
//		 */
//		Window dialogWindow = dialog_share.getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		dialogWindow.setGravity(Gravity.TOP);
//		/*
//		 * lp.x与lp.y表示相对于原始位置的偏移.
//		 * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
//		 * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
//		 * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
//		 * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
//		 * 当参数值包含Gravity.CENTER_HORIZONTAL时
//		 * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
//		 * 当参数值包含Gravity.CENTER_VERTICAL时
//		 * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
//		 * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
//		 * Gravity.CENTER_VERTICAL.
//		 * 
//		 * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
//		 * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
//		 * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
//		 */
//		if (GlobalVariables.width == 0) {
//			// 获取屏幕宽高与密度
//			VsUtil.setDensityWH(this);
//		}
//		lp.x = 0; // 新位置X坐标
//		lp.y = GlobalVariables.height - (int) (417.5 * GlobalVariables.density); // 新位置Y坐标
//		lp.width = GlobalVariables.width; // 宽度
//		lp.height = (int) (417.5 * GlobalVariables.density); // 高度
//		lp.alpha = 1f; // 透明度
//
//		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
//		// dialog.onWindowAttributesChanged(lp);
//		dialogWindow.setAttributes(lp);
//		mTranslateAnimation = new TranslateAnimation(0, 0,
//				GlobalVariables.height, 0);// 移动
//		mTranslateAnimation.setDuration(500);
//		dialog_share.setOnDismissListener(new OnDismissListener() {// 关闭监听
//
//					@Override
//					public void onDismiss(DialogInterface dialog) {
//
//						if (null != isCheckNum) {// 清除选中的号码
//							isCheckNum.clear();
//						}
//
//					}
//				});
//		return v;
//	}
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        /*
         * if (list.size() > 1) { list.remove(1);
         * adapter.notifyDataSetChanged(); }
         */
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (actionContactChange != null) {
            unregisterReceiver(actionContactChange);
        }
        super.onDestroy();

        VsUtil.unregisterCallNumberBC(this);
    }

}
