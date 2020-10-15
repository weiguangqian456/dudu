package com.weiwei.base.activity.calllog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwtx.dududh.R;
import com.weiwei.base.activity.VsBaseActivity;
import com.weiwei.base.activity.VsContactsSelectActivity;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.base.util.SendNoteObserver;
import com.weiwei.base.widgets.DrawableCenterTextView;
import com.weiwei.salemall.utils.FitStateUtils;

import java.util.ArrayList;

/**
 * @Title:Android客户端
 * @Description: 通话记录详情
 * @Copyright: Copyright (c) 2014
 * @author: 李志
 * @version: 1.0.0.0
 * @Date: 2014-9-23
 */
public class VsCallLogDetailsActivity extends VsBaseActivity implements OnClickListener {

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
     * 邀请按钮(用TextView做的)
     */
    // 免费电话
    private DrawableCenterTextView vs_contact_detail_freecall;
    // 免费信息
    private DrawableCenterTextView vs_contact_detail_freemsg;
    // 免费这里的分隔线
    private View line_freemsg_layout;

    /**
     * 呼叫按钮(用TextView做的)
     */
    private LinearLayout vs_contact_detail_call;

    /**
     * 联系人名字显示layout
     */
    private RelativeLayout vs_contact_detail_text_layout;
    /**
     * 通话记录进入显示的拨打图片
     */
    private ImageView vs_contact_callicon;
    private ImageView vs_contact_call;
    private ImageView vs_contact_freecall;
    private ImageView vs_contact_freecall_msg;

    private ImageButton vs_send_message;

    /**
     * 联系人姓名
     */
    private TextView vs_contact_name;
    /**
     * 创建新联系人
     */
    private LinearLayout vs_calllog_detail_createcontact_tv;
    /**
     * 添加到现有联系人
     */
    private LinearLayout vs_calllog_detail_addtocontact_tv;
    /**
     * 添加联系人和添加到现有联系人布局layout
     */
    private RelativeLayout vs_addcontact_layout;
    /**
     * 联系人listView;
     */
    private ListView vs_calllog_detail_listview;
    /**
     * 通话记录详情
     */
    private RelativeLayout vs_calllog_layout;
    /**
     * 当前通话记录电话号码
     */
    private TextView vs_calllog_details_phoneNumber;
    /**
     * 号码归属地
     */
    private TextView vs_calllog_details_localname;
    /**
     * 通话记录ListView
     */
    private ListView vs_calllog_listview;
    /**
     * 通话记录条目
     */
    private VsCallLogListItem calllogListItem = null;
    /**
     * 通话记录对应的联系人信息
     */
    private VsContactItem calllogOfContact = null;

    /**
     * 刷新界面
     */
    private final char MSG_ID_REFLSH = 301;

    /**
     * 名称
     */
    private String callName;
    // 联系人对应的通话记录
    private ArrayList<VsCallLogItem> contactOfCalllogArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vs_contacts_details);
        FitStateUtils.setImmersionStateMode(this,R.color.public_color_EC6941);
        initTitleNavBar();
        mTitleTextView.setText(getString(R.string.vs_calllog_detail_title));
        initData();
        initView();
//		v = initAnimation();
        IntentFilter netChangeFilter = new IntentFilter();
        netChangeFilter.addAction(VsUserConfig.JKey_GET_VSUSER_OK);
        netChangeFilter.addAction(VsUserConfig.JKey_GET_VSUSER_FAIL);
        mContext.registerReceiver(calllogDitailReceiver, netChangeFilter);
        VsApplication.getInstance().addActivity(this);// 保存所有添加的activity。倒是后退出的时候全部关闭
    }

    /**
     * 监听联系人数据变化
     */
    private BroadcastReceiver calllogDitailReceiver = new BroadcastReceiver() {
        /*
         * (non-Javadoc)
         *
         * @see
         * android.content.BroadcastReceiver#onReceive(android.content.Context,
         * android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (VsUserConfig.JKey_GET_VSUSER_OK.equals(intent.getAction()) || VsUserConfig.JKey_GET_VSUSER_FAIL.equals(intent.getAction())) {
                new CalllogAsynTask().execute();
            }
        }

    };

    /**
     * @Title:Android客户端
     * @Description: 异步执行更新操作
     * @Copyright: Copyright (c) 2014
     * @author: 李志
     * @version: 1.0.0.0
     * @Date: 2014-11-3
     */
    class CalllogAsynTask extends AsyncTask<String, Integer, String> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub]
            calllogOfContact = VsUtil.getContactsItem(calllogListItem.getFirst().callNumber);
            mBaseHandler.sendEmptyMessage(MSG_ID_REFLSH);
            return null;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = this.getIntent();
        // 通话记录进入
        calllogListItem = (VsCallLogListItem) intent.getSerializableExtra("CALLLOGDETAILSDATA");
        if (calllogListItem != null) {
            for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
                if (item != null) {
                    if (calllogListItem.getFirst().callName != null && !"".equals(calllogListItem.getFirst().callName)) {// 判断通话记录是否有名字
                        for (String phoneNumStr : item.phoneNumList) {
                            if (calllogListItem.getFirst().callNumber.equals(phoneNumStr) && calllogListItem.getFirst().callName.equals(item.mContactName)) {
                                CustomLog.i("vsdebug", "匹配到联系人");
                                calllogOfContact = item;
                                return;
                            }
                        }
                    } else {
                        for (String phoneNumStr : item.phoneNumList) {
                            if (calllogListItem.getFirst().callNumber.equals(phoneNumStr)) {
                                CustomLog.i("vsdebug", "匹配到联系人");
                                calllogOfContact = item;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化视图界面
     */
    private void initView() {
        showLeftNavaBtn(R.drawable.icon_back);

        // 获取控件对象
        vs_contact_detail_freecall = (DrawableCenterTextView) findViewById(R.id.vs_contact_detail_freecall);
        vs_contact_detail_freemsg = (DrawableCenterTextView) findViewById(R.id.vs_contact_detail_freemsg);
        line_freemsg_layout = findViewById(R.id.line_freemsg_layout);
        vs_calllog_detail_listview = (ListView) findViewById(R.id.vs_contact_detail_listview);
        vs_calllog_layout = (RelativeLayout) findViewById(R.id.vs_calllog_layout);
        vs_calllog_details_phoneNumber = (TextView) findViewById(R.id.vs_calllog_details_phoneNumber);
        vs_calllog_details_localname = (TextView) findViewById(R.id.vs_calllog_details_localname);
        vs_calllog_listview = (ListView) findViewById(R.id.vs_calllog_listview);
        vs_contact_detail_text_layout = (RelativeLayout) findViewById(R.id.vs_contact_detail_text_layout);
        vs_send_message = (ImageButton) findViewById(R.id.send_message);

        // TD电话（上边）
        vs_contact_callicon = (ImageView) findViewById(R.id.vs_contact_callicon);
        vs_contact_call = (ImageView) findViewById(R.id.vs_contact_call);
        // 免费电话、免费短信
        vs_contact_freecall = (ImageView) findViewById(R.id.vs_contact_freecall);
        vs_contact_freecall_msg = (ImageView) findViewById(R.id.vs_contact_freecall_msg);
        // TD电话（底部）
        vs_contact_detail_call = (LinearLayout) findViewById(R.id.vs_contact_detail_call);

        vs_contact_name = (TextView) findViewById(R.id.vs_contact_name);
        vs_calllog_detail_createcontact_tv = (LinearLayout) findViewById(R.id.vs_calllog_detail_createcontact_tv);
        vs_calllog_detail_addtocontact_tv = (LinearLayout) findViewById(R.id.vs_calllog_detail_addtocontact_tv);
        vs_addcontact_layout = (RelativeLayout) findViewById(R.id.vs_addcontact_layout);

        // 初始化控件状态 通话记录大多不需要显示的默认隐藏
        vs_calllog_detail_listview.setVisibility(View.GONE);
        vs_calllog_layout.setVisibility(View.VISIBLE);
        vs_contact_detail_freecall.setVisibility(View.GONE);
        vs_contact_detail_freemsg.setVisibility(View.GONE);
        line_freemsg_layout.setVisibility(View.GONE);

        vs_calllog_detail_createcontact_tv.setOnClickListener(this);
        vs_calllog_detail_addtocontact_tv.setOnClickListener(this);
        vs_calllog_layout.setOnClickListener(this);

        // 如果是联系人。显示编辑
        if (calllogOfContact != null) {
            showRightTxtBtn(getString(R.string.vs_contact_detail_edit_contacts));
            callName = calllogOfContact.mContactName;
            if (VsUtil.checheNumberIsVsUser(mContext, calllogListItem.getFirst().callNumber)) {
                vs_contact_callicon.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_call_icon));
                vs_contact_callicon.setVisibility(View.VISIBLE);
                vs_contact_call.setVisibility(View.GONE);
                // vs_contact_call.setOnClickListener(this);
                vs_contact_freecall.setVisibility(View.GONE);
                vs_contact_freecall.setOnClickListener(this);
                vs_contact_freecall_msg.setVisibility(View.GONE);
                vs_contact_freecall_msg.setOnClickListener(this);

                // vs_contact_detail_call.setVisibility(View.VISIBLE);
                // vs_contact_detail_call.setOnClickListener(this);

                vs_contact_detail_freecall.setVisibility(View.VISIBLE);
                vs_contact_detail_freecall.setOnClickListener(this);
                vs_contact_detail_freemsg.setVisibility(View.VISIBLE);
                vs_contact_detail_freemsg.setOnClickListener(this);
                line_freemsg_layout.setVisibility(View.VISIBLE);
            } else {
                vs_contact_callicon.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_contact_dial));
                vs_contact_callicon.setVisibility(View.GONE);
                // 始终显示
                vs_contact_call.setVisibility(View.VISIBLE);
                // vs_contact_call.setOnClickListener(this);
                vs_contact_freecall.setVisibility(View.GONE);
                vs_contact_freecall.setOnClickListener(this);
                vs_contact_freecall_msg.setVisibility(View.GONE);
                vs_contact_freecall_msg.setOnClickListener(this);

                // 始终显示TD电话 底部
                // vs_contact_detail_call.setVisibility(View.VISIBLE);
                // vs_contact_detail_call.setOnClickListener(this);

                vs_contact_detail_freecall.setVisibility(View.GONE);
                vs_contact_detail_freecall.setOnClickListener(this);
                vs_contact_detail_freemsg.setVisibility(View.GONE);
                vs_contact_detail_freemsg.setOnClickListener(this);
                line_freemsg_layout.setVisibility(View.GONE);
            }
        } else {
            // 显示添加联系人
            vs_calllog_details_phoneNumber.setTextSize((float) 23.0);
            vs_addcontact_layout.setVisibility(View.VISIBLE);
            // vs_contact_sendFrend_btn_layout2.setVisibility(View.VISIBLE);

            if (calllogListItem.getFirst().callName != null) {
                callName = calllogListItem.getFirst().callName;
            }
            vs_contact_detail_text_layout.setVisibility(View.GONE);
        }

        // 设置监听事件【只要在通话记录里面的人，都可以打TD电话】
        vs_contact_call.setOnClickListener(this);
        vs_contact_detail_call.setOnClickListener(this);
        vs_send_message.setOnClickListener(this);

        if (callName != null) {
            vs_contact_name.setText(callName);
        } else {
            callName = calllogListItem.getFirst().callNumber;
        }
        vs_calllog_details_phoneNumber.setText(calllogListItem.getFirst().callNumber);

        if ((calllogListItem.getLocalName() != null) && (calllogListItem.getLocalName().length() > 0)) {
            vs_calllog_details_localname.setText(calllogListItem.getLocalName());
            vs_calllog_details_localname.setVisibility(View.VISIBLE);
        } else {
            vs_calllog_details_localname.setVisibility(View.GONE);
        }

        if (calllogListItem.getChilds().get(0).calltimestamp != -999) {
            // kc_calllog_listview.setOnItemClickListener(new
            // ListViewItemClikcLisner());
        } else {
            vs_calllog_listview.setVisibility(View.GONE);
        }

        // 初始化ListView
        VsCallLogDetailsAdapter adapter = new VsCallLogDetailsAdapter(mContext, calllogListItem.getChilds());
        vs_calllog_listview.setAdapter(adapter);
        VsUtil.setListViewHeightBasedOnChildren(vs_calllog_detail_listview);
    }

    // /**
    // * 列表条目点击监听事件
    // *
    // * @author 李志
    // *
    // */
    // class ListViewItemClikcLisner implements OnItemClickListener {
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see
    // android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
    // * android.view.View, int, long)
    // */
    // @Override
    // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
    // arg3) {
    // // TODO Auto-generated method stub
    // MobclickAgent.onEvent(mContext, "CallD_DialClick");
    // if (calllogListItem.getLocalName() != null) {
    // KcUtil.callNumber(callName, calllogListItem.getFirst().callNumber,
    // calllogListItem.getLocalName(),
    // mContext, "", true);
    // } else {
    // KcUtil.callNumber(callName, calllogListItem.getFirst().callNumber, null,
    // mContext, "", true);
    // }
    // }
    // }

    @Override
    protected void HandleRightNavBtn() {
        // if (calllogOfContact != null) {
        // Intent intent = new Intent(this, KcContactsDetailsActivity.class);
        // intent.putExtra("contact_type", 2);
        // intent.putExtra("CONTACTDETAILSDATA", calllogOfContact);
        // startActivity(intent);
        // } else {
        // mToast.show("未获到联系人信息", Toast.LENGTH_SHORT);
        // }
        if (null != calllogOfContact && !"".equals(calllogOfContact.mContactId)) {
            VsUtil.updateContact(mContext, calllogOfContact.mContactId);
        } else {
            mToast.show("请先添加联系人", Toast.LENGTH_SHORT);
        }

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
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

    @Override
    public void onClick(View v) {
        if (VsUtil.isFastDoubleClick()) {
            return;
        }
        showDialogNow(false);

        switch (v.getId()) {
            case R.id.vs_contact_freecall:// 免费电话（上边）点击
            case R.id.vs_contact_detail_freecall:// 免费电话（底部）点击
                callNumber(true);
                break;

            case R.id.vs_contact_call:// TD电话（上边）点击
            case R.id.vs_contact_detail_call:// TD电话（底部）点击
                callNumber(true);
                break;

//		case R.id.vs_contact_freecall_msg:// 免费信息点击
//			VsUtil.sendFressMsg(mContext, calllogOfContact,
//					calllogOfContact.mContactUid.get(0));
//			break;

            case R.id.vs_calllog_detail_addtocontact_tv:
                VsUtil.addCurrentContact(mContext, calllogListItem.getFirst().callNumber);
                break;

            case R.id.vs_calllog_detail_createcontact_tv:
                VsUtil.addContact(mContext, calllogListItem.getFirst().callNumber);
                break;

            case R.id.vs_calllog_layout:
                if (calllogOfContact != null && calllogOfContact.isVsUser) {
                    callNumber(true);
                } else {
                    callNumber(false);
                }
                break;
            case R.id.tv_message:// 短信发送名片
                Intent intent = new Intent(mContext, VsContactsSelectActivity.class);
                intent.putExtra("SENDCARDCONTACTSENDSMS", true);// 添加标识
                intent.putExtra("phoneCardInfo", appendPhoneCardInfo());// 名片信息
                startActivity(intent);
                break;
            case R.id.send_message:// 发送消息

                vsContactInvite();

                break;
            case R.id.tv_weixin:// 微信发送名片
                VsUtil.weixinShare(mContext, appendPhoneCardInfo());
                break;

            default:
                break;
        }
    }

    /**
     * 拨打电话
     *
     * @param bol true为免费 false为TD电话
     */
    public void callNumber(boolean bol) {
        // 拨号
        if (calllogListItem.getLocalName() != null) {
            VsUtil.callNumber(callName, calllogListItem.getFirst().callNumber, calllogListItem.getLocalName(), mContext, "", bol,getFragmentManager());
        } else {
            VsUtil.callNumber(callName, calllogListItem.getFirst().callNumber, null, mContext, "", bol,getFragmentManager());
        }
    }

    public void vsContactInvite() {
        // 判断是否登录
        if (!VsUtil.isLogin(getResources().getString(R.string.recommend_friends_prompt), mContext)) return;
        if (calllogListItem == null || calllogListItem.getChilds().size() == 0) {
            return;
        }
        if (VsUtil.checkMobilePhone(calllogListItem.getFirst().callNumber)) {

            // 联系人号码
            String mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
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
                mRecommendInfo = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_FRIEND_INVITE);
            }

            SendNoteObserver.sendSendNoteNumber = 1;
            // 发送邀请短信
            VsUtil.smsShare(mContext, mRecommendInfo, calllogListItem.getFirst().callNumber);
                /*
                 * try { Intent intent = new Intent(Intent.ACTION_SENDTO,
				 * Uri.fromParts("smsto", calllogListItem.getFirst().callNumber,
				 * null)); intent.putExtra("sms_body", mRecommendInfo);
				 * startActivity(intent); } catch (Exception e) { // TODO
				 * Auto-generated catch block e.printStackTrace();
				 * mToast.show(getResources
				 * ().getString(R.string.sendmessage_fail), Toast.LENGTH_SHORT);
				 * }
				 */

        } else {
            mToast.show("号码格式不对！", Toast.LENGTH_SHORT);
        }
    }

    // HER

    /**
     * 拼接名片信息
     *
     * @return
     */
    private String appendPhoneCardInfo() {

        if (calllogOfContact != null) {// 存在则通话记录有名字
            StringBuilder builder = new StringBuilder("姓名:" + calllogOfContact.mContactName);// 拼接内容

            builder.append("\n号码:" + calllogOfContact.phoneNumList.get(0));
            return builder.toString();
        }

        return "号码:" + calllogListItem.getFirst().callNumber;
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
//		return v;
//	}
    @Override
    public void onStop() {
        if (calllogDitailReceiver != null) {
            unregisterReceiver(calllogDitailReceiver);
            calllogDitailReceiver = null;
        }
        super.onStop();
    }

    @Override
    protected void handleKcBroadcast(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.handleKcBroadcast(context, intent);
    }

    @Override
    protected void handleBaseMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleBaseMessage(msg);
        switch (msg.what) {
            case MSG_ID_REFLSH:
                refreshView();

                break;
            default:
                break;
        }
    }

    /**
     * 更新界面
     */
    public void refreshView() {
        if (calllogOfContact != null) {
            CustomLog.i("mydebug", "联系人名称：" + calllogOfContact.mContactName);
            showRightTxtBtn(getString(R.string.vs_contact_detail_edit_contacts));
            vs_addcontact_layout.setVisibility(View.GONE);
            vs_contact_detail_text_layout.setVisibility(View.VISIBLE);
            vs_contact_name.setText(calllogOfContact.mContactName);
            if (calllogOfContact.isVsUser) {
                vs_contact_callicon.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_call_icon));
                vs_contact_callicon.setVisibility(View.VISIBLE);
                vs_contact_call.setVisibility(View.GONE);
                vs_contact_call.setOnClickListener(this);
                vs_contact_freecall.setVisibility(View.VISIBLE);
                vs_contact_freecall.setOnClickListener(this);
                vs_contact_freecall_msg.setVisibility(View.VISIBLE);
                vs_contact_freecall_msg.setOnClickListener(this);

                vs_contact_detail_freecall.setVisibility(View.VISIBLE);
                vs_contact_detail_freecall.setOnClickListener(this);
                vs_contact_detail_freemsg.setVisibility(View.VISIBLE);
                vs_contact_detail_freemsg.setOnClickListener(this);
                line_freemsg_layout.setVisibility(View.VISIBLE);
            } else {

                vs_contact_callicon.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_contact_dial));
                vs_contact_callicon.setVisibility(View.GONE);
                vs_contact_call.setVisibility(View.VISIBLE);
                vs_contact_call.setOnClickListener(this);
                vs_contact_freecall.setVisibility(View.GONE);
                vs_contact_freecall.setOnClickListener(this);
                vs_contact_freecall_msg.setVisibility(View.GONE);
                vs_contact_freecall_msg.setOnClickListener(this);

                vs_contact_detail_freecall.setVisibility(View.GONE);
                vs_contact_detail_freecall.setOnClickListener(this);
                vs_contact_detail_freemsg.setVisibility(View.GONE);
                vs_contact_detail_freemsg.setOnClickListener(this);
                line_freemsg_layout.setVisibility(View.GONE);
            }
            // 更新通话记录
            if (calllogOfContact.phoneNumList.size() > 0) {
                VsPhoneCallHistory.updateCallLog(mContext, calllogOfContact.mContactName, calllogOfContact.phoneNumList);
            }
        } else {
            // 显示添加联系人
            mBtnNavRightTv.setVisibility(View.INVISIBLE);
            vs_calllog_details_phoneNumber.setTextSize((float) 23.0);
            vs_addcontact_layout.setVisibility(View.VISIBLE);
            // vs_contact_sendFrend_btn_layout2.setVisibility(View.VISIBLE);

            if (calllogListItem.getFirst().callName != null) {
                callName = calllogListItem.getFirst().callName;
            }
            vs_contact_detail_text_layout.setVisibility(View.GONE);
            vs_contact_detail_freecall.setVisibility(View.GONE);
            vs_contact_detail_freemsg.setVisibility(View.GONE);
            line_freemsg_layout.setVisibility(View.GONE);
            // vs_contact_callicon.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_contact_dial));
            // 更新通话记录
            ArrayList<String> callNumberList = new ArrayList<String>();
            callNumberList.add(calllogListItem.getFirst().callNumber);
            VsPhoneCallHistory.updateCallLog(mContext, calllogListItem.getFirst().callNumber, callNumberList);
        }
        vs_contact_detail_freecall.invalidate();
        vs_contact_detail_freemsg.invalidate();
        // vs_contact_detail_call.invalidate();
        vs_contact_call.invalidate();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // getContentResolver().unregisterContentObserver(noteObserver);
        if (calllogDitailReceiver != null) {
            unregisterReceiver(calllogDitailReceiver);
        }
    }
}
