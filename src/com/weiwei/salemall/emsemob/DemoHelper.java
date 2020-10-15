package com.weiwei.salemall.emsemob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hwtx.dududh.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.Notifier;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONObject;

import java.util.List;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_APPKEY;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TAGNAME;
import static com.weiwei.base.dataprovider.DfineAction.EASEMOB_TENANTID;

public class DemoHelper {

    private static final String TAG = "HuanXinDemoHelper";

    public static DemoHelper instance = new DemoHelper();

    /**
     * kefuChat.MessageListener  消息监听
     */
    protected ChatManager.MessageListener messageListener = null;

    /**
     * ChatClient.ConnectionListener   连接监听
     */
    private ChatClient.ConnectionListener connectionListener;

    private UIProvider _uiProvider;

    public boolean isVideoCalling;
    //    private CallReceiver callReceiver;
    private Context appContext;

    private DemoHelper() {
    }

    public synchronized static DemoHelper getInstance() {
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(final Context context) {
        Log.e(EASEMOB_TAGNAME, "sdk初始化 succedd");
        appContext = context;
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey(EASEMOB_APPKEY);
        options.setTenantId(EASEMOB_TENANTID);
        options.showAgentInputState().showVisitorWaitCount().showMessagePredict();

        //增加FCM推送，对于国外的APP可能比较需要
        options.setFCMNumber("570662061026");

        options.setUseFCM(true);
        //在小米手机上当app被kill时使用小米推送进行消息提示，SDK已支持，可选
        options.setMipushConfig("2882303761517507836", "5631750729836");

//        options.setKefuRestServer("https://sandbox.kefu.easemob.com");

        //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
        options.setConsoleLog(false);

        // 环信客服 SDK 初始化, 初始化成功后再调用环信下面的内容
        if (ChatClient.getInstance().init(context, options)) {
            _uiProvider = UIProvider.getInstance();
            //初始化EaseUI
            _uiProvider.init(context);
            //调用easeui的api设置providers
            setEaseUIProvider(context);
            //设置全局监听
//            setGlobalListeners();
        }
    }

    private void setEaseUIProvider(final Context context) {
        //设置头像和昵称 某些控件可能没有头像和昵称，需要注意
        UIProvider.getInstance().setUserProfileProvider(new UIProvider.UserProfileProvider() {
            @Override
            public void setNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView) {
                if (message.direct() == Message.Direct.RECEIVE) {
                    //设置接收方的昵称和头像
//                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                    AgentInfo agentInfo = MessageHelper.getAgentInfo(message);
                    if (usernickView != null) {
                        usernickView.setText(message.from());
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                                usernickView.setText(agentInfo.getNickname());
                            }
                        }
                    }
                    if (userAvatarView != null) {
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                                String strUrl = agentInfo.getAvatar();
                                // 设置客服头像
                                if (!TextUtils.isEmpty(strUrl)) {
                                    //方形 （圆形头像加载时闪动）
                                    userAvatarView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cs_sobot_face));
                                    return;
                                }
                            }
                        }
                        //方形头像
                        userAvatarView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cs_sobot_face));
                    }
                } else {
                    if (userAvatarView != null) {
                        userAvatarView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.visitor_sobot_face));
                    }
                }
            }
        });


        //设置通知栏样式
        _uiProvider.getNotifier().setNotificationInfoProvider(new Notifier.NotificationInfoProvider() {
            @Override
            public String getTitle(Message message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(Message message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(Message message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, context);
                if (message.getType() == Message.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", context.getString(R.string.app_name));
                }
                return message.from() + ": " + ticker;
            }

            @Override
            public String getLatestText(Message message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
            }

            @Override
            public Intent getLaunchIntent(Message message) {
                Intent intent = null;
                if (isVideoCalling) {
//                    intent = new Intent(context, CallActivity.class);
                } else {
                    //设置点击通知栏跳转事件
                    Conversation conversation = ChatClient.getInstance().chatManager().getConversation(message.from());
                    String titleName = null;
                    if (conversation.officialAccount() != null) {
                        titleName = conversation.officialAccount().getName();
                    }
                    intent = new IntentBuilder(context).setTargetClass(ChatActivity.class).setServiceIMNumber(conversation.conversationId()).setVisitorInfo(EaseMobMsgHelper
                            .createVisitorInfo("")).setTitleName(titleName).setShowUserNick(true).build();
                }
                return intent;
            }
        });

        //不设置,则使用默认, 声音和震动设置
//        _uiProvider.setSettingsProvider(new UIProvider.SettingsProvider() {
//            @Override
//            public boolean isMsgNotifyAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isMsgSoundAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isMsgVibrateAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isSpeakerOpened() {
//                return false;
//            }
//        });
//        ChatClient.getInstance().getChat().addMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(List<Message> msgs) {
//
//            }
//
//            @Override
//            public void onCmdMessage(List<Message> msgs) {
//
//            }
//
//            @Override
//            public void onMessageSent() {
//
//            }
//
//            @Override
//            public void onMessageStatusUpdate() {
//
//            }
//        });
    }


    private void setGlobalListeners() {
        //注册消息事件监听
        registerEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        messageListener = new ChatManager.MessageListener() {
            @Override
            public void onMessage(List<Message> msgs) {
                for (Message message : msgs) {
                    Log.d(TAG, "onMessageReceived id : " + message.messageId());
//
                    //这里全局监听通知类消息,通知类消息是通过普通消息的扩展实现
                    if (MessageHelper.isNotificationMessage(message)) {
                        // 检测是否为留言的通知消息
                        String eventName = getEventNameByNotification(message);
                        if (!TextUtils.isEmpty(eventName)) {
                            if (eventName.equals("TicketStatusChangedEvent") || eventName.equals("CommentCreatedEvent")) {
                                // 检测为留言部分的通知类消息,刷新留言列表
                                JSONObject jsonTicket = null;
                                try {
                                    jsonTicket = message.getJSONObjectAttribute("weichat").getJSONObject("event").getJSONObject("ticket");
                                } catch (Exception ignored) {
                                }
//                                ListenerManager.getInstance().sendBroadCast(eventName, jsonTicket);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCmdMessage(List<Message> msgs) {
                for (Message message : msgs) {
                    Log.d(TAG, "收到透传消息");
                    //获取消息body
                    EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) message.body();
                    String action = cmdMessageBody.action(); //获取自定义action
                    Log.d(TAG, String.format("透传消息: action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageStatusUpdate() {

            }

            @Override
            public void onMessageSent() {

            }
        };

        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
    }


    /**
     * 获取EventName
     *
     * @param message
     * @return
     */
    public String getEventNameByNotification(Message message) {

        try {
            JSONObject weichatJson = message.getJSONObjectAttribute("weichat");
            if (weichatJson != null && weichatJson.has("event")) {
                JSONObject eventJson = weichatJson.getJSONObject("event");
                if (eventJson != null && eventJson.has("eventName")) {
                    return eventJson.getString("eventName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pushActivity(Activity activity) {
        _uiProvider.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        _uiProvider.popActivity(activity);
    }

    public Notifier getNotifier() {
        return _uiProvider.getNotifier();
    }
}
