package com.weiwei.base.dataprovider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalVariables {

    /**
     * 广告
     */
    public final static String actionAdsConfig = "action.actionAdsConfig";
    public final static String actionSeaRchmoney = "action_search_money";
    public final static String actionSeaRchcard = "action_search_card";
    public final static String actionGetcard = "action_get_card";
    public final static String actionGetmoney = "action_getmoney";
    public static final String INTERFACE_SEACHE_MONEY = "/user/red_wallet";
    public static final String INTERFACE_SEACHE_CARD = "/red_wallet/bank_info";
    public static final String INTERFACE_GET_MONEY = "/fetch/red_packet";
    public static final String actionShareConfig = "/action_getsharecontent";
    /**
     * 个人信息
     */
    public static final String actionGetmyInfo = "action.getMyInfo";
    public static final String actionPushmyInfo = "action.pushMyInfo";
    public static final String GetMyInfo = "/user/info";
    public static final String PushMyInfo = "/user/renew_info";

    /**
     * 话费流水信息
     */
    public static final String actionGetMyCallMoney = "action.getMyCallMoney";
    public static final String GetMyCallMoney = "/user/charge_log";

    /**
     * 话单流水信息
     */
    public static final String actionGetMyCallLog = "action.getMyCallLog";
    public static final String GetMyCallLog = "/user/call_log";
    /**
     * 红包流水信息
     */
    public static final String actionGetMyRedLog = "action.getMyredLog";
    public static final String GetMyRedLog = "/user/split_log";
    /**
     * 红包提取信息
     */
    public static final String actionGetMyRedMoney = "action.getMyredMoney";
    public static final String GetMyRedMoney = "/user/drawing";


    public static final String actionGetMyBank = "action.getMyBank";
    public static final String GetMyBank = "/user/bank";


    /**
     * 广告
     */
    public static final String INTERFACE_AD = "/config/adver";
    public static final String INTERFACE_GetOK = "/action/call_ready";
    public static final String actionGetOK = "actionGetOK";


    /**
     * 分享内容
     */
    public static final String INTERFACE_SHARE = "/config/share";

    /**
     * 扫码绑定
     */
    public static final String GET_QRCODE = "/user/bind_invited";
    public static final String actionGetQrcode = "action.getqrcode";

    /**
     * 获取token的Action
     *
     * @deprecated
     */
    public final static String actionGetTokenYongYun = "action_gettoken_rongyun";

    /**
     * 签到
     */
    public final static String VS_ACTION_SIGNIN = "com.kc.action_sigin";
    /**
     * 签到提示
     */
    public static final String INTERFACE_SIGN = "/config/checkin";
    /**
     * 签到提示
     */
    public final static String actionSignConfig = "action.signconfig";

    /**
     * 查询服务器通讯录备份信息
     */
    public final static String VS_ACTION_CHECK_CONTACTS = "com.kc.logic.check_contacts";
    /**
     * 保存联系人添加的号码
     */
    public static String SAVE_CALLLONG_NUMBER = null;
    /**
     * Token失效连接失败-是否进行过重连
     */
    public static boolean RE_CONNECT_FLAG = false;

    /**
     * 联系人变化通知联系人详情更新
     */
    public final static String action_contact_detail_change = "action_contact_detail_change";
    // static final全局静态常量
    // final:一旦赋值常量，不能更改
    // static:全局只有一个存储区，使用"类名.变量名"访问
    // 通过static final修饰的容器类型变量中所“装”的对象是可改变的
    public static final String Action_GlobalFunService = "com.weiwei.dataprovider.GlobalFuntionService";
    /**
     * 是否进入拨打页面。用户回拨
     */
    public static boolean isEnterCallScreen = false;
    @SuppressWarnings("deprecation")
    public static int SDK_VERSON = Integer.parseInt(android.os.Build.VERSION.SDK);

    public static int netmode = 2; // 本地网络状态
    public static String dialPhoneNumber = ""; // 来电号码

    //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
    public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    //创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    /**
     * 图片下载独用线程池，以免影响其他业务请求
     */
    public static ExecutorService fixedThreadPoolImageDown = Executors.newFixedThreadPool(5);
    // ThreadPoolExecutor
    /**
     * 播放钢琴音
     */
    public static ExecutorService fixedThreadPoolPiano = Executors.newFixedThreadPool(5);
    /**
     * 获取发送电话号码类型 reg
     */
    public static final String getNumberType_reg = "reg";
    /**
     * 获取发送电话号码类型 bind
     */
    public static final String getNumberType_bind = "bind";
    // 函数处理完成后发送的广播action
    public static final String actionGetHttpData = "action.gethttpdata";
    public static final String actionCheckInternet = "action.checkinternet"; // 检查网络状态后发送的广播action
    public static final String actionLogin = "action.login";
    public static final String actionBalance = "action.balance";
    public final static String ACTION_DIAL_CALL = "com.bangbang.dial.call"; // 有拨号操作时通知DialActivity的广播
    /**
     * 提交余额查询
     */
    public static String action_search_balance_do = "action_search_balance_do";
    /**
     * 加载通话记录成功
     */
    public final static String action_loadcalllog_succ = "action_loadcalllog_succ";
    /**
     * 通知显示通话通知栏
     */
    public final static String action_show_notification = "action_show_notification";
    /**
     * 通知关闭通话通知栏
     */
    public final static String action_close_notification = "action_close_notification";
    /**
     * 显示通知栏后再次拨打电话
     */
    public final static String action_agin_notification = "action_agin_notification";
    /**
     * 通知拨打电话
     */
    public final static String action_dial_phone = "action_dial_phone";
    /**
     * 通知开始监听系统电话, 并且自动接通来电
     */
    public final static String action_start_listen_system_phone = "action_start_listen_system_phone";
    /**
     * 通知取消监听系统电话
     */
    public final static String action_stop_listen_system_phone = "action_stop_listen_system_phone";
    /**
     * 取消自动接听来电
     */
    public final static String action_stop_auto_answer = "action_stop_auto_answer";
    /**
     * 通知显示通话遮挡视图
     */
    public final static String action_show_calling_float_view = "action_show_calling_float_view";
    /**
     * 通知取消通话遮挡视图
     */
    public final static String action_dismiss_calling_float_view = "action_dismiss_calling_float_view";
    /**
     * 通话结束，修改遮挡浮框的状态
     */
    public final static String action_update_float_view_state = "action_update_float_view_state";
    /**
     * 通话结束，通知服务启动APP
     */
    public final static String action_start_app_after_call_end = "action_start_app_after_call_end";
    /**
     * 广播connect 融云结果
     */
    public final static String action_connect_rc_result = "action_connect_rc_result";
    /**
     * 广播未接来电
     */
    public final static String action_miss_incoming_check = "action_miss_incoming_check";
    /**
     * 赚话费分享的Action
     */
    public final static String action_makemoney_share = "action_makemoney_share";
    /**
     * 网络是否差
     */
    public static boolean netWorkBadFlag = false;
    /**
     * 赚话费任务
     */
    public static String action_invite_task = "action_invite_task";
    /**
     * 是否成功接通
     */
    public static boolean isStartConnect = false;
    /**
     * 保持通话号码
     */
    public static String saveCallName = null;
    /**
     * 通知我界面显示小广播红点
     */
    public final static String action_my_show_red = "action_my_show_red";
    /**
     * 通知主界面是否是两个按钮
     */
    public final static String action_is_double_btn = "action_is_double_btn";

    /**
     * 通知主界面关闭拨号按钮广播
     */
    public final static String action_close_call_btn = "action_close_call_btn";
    /**
     * 通知主界面打开拨号按钮广播
     */
    public final static String action_open_call_btn = "action_open_call_btn";
    /**
     * 通知UI网络较差，切换回拨的广播
     */
    public final static String action_net_change = "action_net_change";
    /**
     * 通知更新分组
     */
    public final static String action_group_change = "action_group_change";
    /**
     * 通知UI无网络
     */
    public final static String action_no_network = "action_no_network";
    /**
     * 下载完后启动第三方未安装apk广播
     */
    public final static String action_startplugin = "action_startplugin";
    /**
     * 备份联系人
     */
    public static final String actionBackUp = "action.backup_contact";
    /**
     * 还原联系人
     */
    public static final String actionRenew = "action.renew_contact";
    /**
     * mo绑定注册反馈广播
     */
    public static final String actionMORB = "action.mo_rb";
    /**
     * 查询服务器通讯录备份信息
     */
    public final static String actionCheckContacts = "com.kc.logic.check_contacts";
    /**
     * 动作统计
     */
    public static final String actionCount = "action.count";
    /**
     * 广告位动作统计
     */
    public static final String adActionCount = "ad.action.count";
    /**
     * 手机注册
     */
    public static final String actionRegister = "action.register";


    /**
     * 手机绑定
     */
    public static final String actionBind = "action.bind";
    /**
     * 始终呼叫号码
     */
    public final static String action_setcallphont = "action.setcallphone";

    /**
     * 提交mac地址
     */
    public final static String actionRecordInstall = "action.recordinstall";
    /**
     * 模版配置
     */
    public final static String actionTempLateConfig = "action.tempconfig";
    /**
     * 静态配置
     */
    public final static String actionDefaultConfig = "action.defaultconfig";
    public final static String actionFirUpdate = "action.firdate";
    /**
     * 升级
     */
    public final static String actionupdate = "action.update";
    /**
     * 充值套餐配置
     */
    public final static String actionGoodsConfig = "action.goodsconfig";
    public final static String actionGoodsConfigsec = "action.goodsconfigsec";
    /**
     * 得到发送短信的通道号码
     */
    public final static String actionGetNumber = "action.getnumber";
    /**
     * 第三方绑定
     */
    public final static String actionThirdBind = "action.third_bind";
    /**
     * 第三方登录
     */
    public final static String actionThirdLogin = "action_third_login";
    /**
     * 阅读反馈 促达push下来的消息点击后反馈给服务器
     */
    public final static String actionPushNotify = "action_push_notify";
    /**
     * 上报错误日志
     */
    public final static String actionReportList = "action_report_list";
    /**
     * 获取错误日志标准
     */
    public final static String actionReportControl = "action_report_control";

    /**
     * 消息略读反馈接口
     */
    public final static String actionFEEDBACK = "action.logic.feedback";

    /**
     * mo绑定
     */
    public final static String actionMoBind = "action_mo_bind";
    /**
     * mo注册
     */
    public final static String actionMoReg = "action_mo_reg";
    /**
     * 心跳上报，上报活跃
     */
    public final static String actionHeartBeat = "action_heart_beat";
    /**
     * 拉取系统公告消息
     */
    public final static String actionSysMsg = "action_sysmsg";
    /**
     * 注册、绑定获取验证码
     */
    public final static String actionRegBindGetVerify = "action_mo_regbind_getverify";
    /**
     * 回拨广播--界面反馈
     */
    public final static String actionCallBack = "action_call_back";
    /**
     * 回拨广播--界面反馈
     */
    public final static String actionCallBackF = "action_call_backF";
    /**
     * 执行回拨-拨打
     */
    public final static String actionCallBackDo = "action_call_back_do";
    /**
     * 执行回拨-拨打framgment
     */
    public final static String actionCallBackDoF = "action_call_back_doF";
    /**
     * 意见反馈
     */
    public final static String actionFeedBack = "action_feeb_back";
    /**
     * 支付宝充值
     */
    public final static String actionRechargeAlipay = "action_rechargealipay";
    /**
     * 支付宝网页充值
     */
    public final static String actionRechargeWapAlipay = "action_rechargewapalipay";
    /**
     * 网银充值/信用卡
     */
    public final static String actionRechargeOnline = "action_rechargeonline";
    /**
     * 银联全渠道控件支付
     */
    public final static String actionRechargeSDKPay = "action_recharge_sdk_pay";
    /**
     * 微信支付
     */
    public final static String actionRechargeWeiXin = "action_recharge_wei_xin";
    /**
     * 充值卡充值
     */
    public final static String actionMobileRecharge = "action_rechargemobile";
    /**
     * 查询余额
     */
    public final static String actionSeaRchbalance = "action_search_balance";
    /**
     * 查询套餐
     */
    public final static String actionSeaRchtaocan = "action_search_taocan";

    /**
     * 查询当月通话时间和节省
     */
    public final static String actionSearchCallTime = "action_search_calltime";
    /**
     * 来电显示
     */
    public final static String actionOPENSERVICE = "action_openservice";
    /**
     * 绑定赠送30分钟
     */
    public final static String actionGiftPkgTime = "action_gifpkgtime";
    /**
     * 获取token的Action
     */
    public final static String actionGetToken = "action_gettoken";
    /**
     * 获取vs好友的Action
     */
    public final static String actionGetVsFriend = "action_getkcfriend";
    /**
     * 上传push消息
     */
    public final static String actionReportPushInfo = "action_reportpushinfo";
    /**
     * 上传签名
     */
    public final static String actionReportSignature = "action_reportSignature";
    /**
     * 获取签名
     */
    public final static String actionGetSignature = "action_getSignature";
    /**
     * 获取自己签名
     */
    public final static String actionGetUsSignature = "action_getussignature";
    /**
     * 返回的JSON数据
     */
    public final static String VS_KeyMsg = "msg";
    /**
     * 手机型号
     */
    public static String PHONE_MODEL = null;

    // 检查apk是否安装后发送的广播action
    public static final String actionCheckApk = "action.checkapk";

    // http网关接口

    /**
     * 下载备份联系人
     */
    public static final String INTERFACE_CONTACT_DOWN = "/contacts/down";
    /**
     * 备份联系人
     */
    public static final String INTERFACE_CONTACT_BACKUP = "/contacts/backup";
    /**
     * 上传push消息
     */
    public static final String INTERFACE_REPORT_PUSHINFO = "/push/report_pushinfo";
    /**
     * 上传广告位统计
     */
    public static final String INTERFACE_STATISTIC_AD_UPLOAD = "/statistic/ad_upload";
    /**
     * 移动充值卡充值接口
     */
    public static final String INTERFACE_MOBILE_RECHARGE = "/card/pay";
    public static final String INTERFACE_ORDER_RECHARGE = "/order/pay";
    /**
     * 赚话费任务
     */
    public static final String INTERFACE_INVITE_TASK = "/user/unfinishedtask";
    /**
     * 意见反馈接口
     */
    public static final String INTERFACE_FEEDBACK = "/statistic/feedback";
    /**
     * 回拨接口
     */
    public static final String INTERFACE_CALLBACK = "/action/call_back";
    /**
     * 旧回拨接口
     */
    public static final String INTERFACE_CALLBACK_OLD = "/call";
    /**
     * 查询余额--我的界面
     */
    public static final String INTERFACE_BALANCE_MY = "/user/balance";
    /**
     * 查询余额
     */
    public static final String INTERFACE_BALANCE = "/user/wallet";
    /**
     * 来显
     */
    public static final String INTERFACE_CID_SERVER = "/user/show_num";
    /**
     * 注册
     */
    public static final String INTERFACE_REGISTER = "/account/vs_regsetpw";
    /**
     * 绑定
     */
    public static final String INTERFACE_BIND = "/user/bind_phone";
    /**
     * 查询用户信息
     */
    public static final String INTERFACE_QUERYUSER_info = "/user/query_user";
    /**
     * 查询当月通话时间和节省
     */
    public static final String INTERFACE_SEACHE_CALLTIME = "/user/month_calltime";
    /**
     * 手机注册获取验证码接口
     */
    public static final String INTERFACE_REG_GET_VERIFY = "/account/vsregcode.act";
    /**
     * 上报安装量
     */
    public static final String INTERFACE_INSTALL = "/statistic/install";
    /**
     * 模版配置
     */
    public static final String INTERFACE_TPL = "/config/tpl";
    /**
     * 静态配置
     */
    public static final String INTERFACE_CONFIG = "/config/app";
    public static final String FIR_UPDATE = "/com.hwtx.dududh";
    /**
     * 升级
     */
    public static final String UPDATE_CONFIG = "/version/update";
    /**
     * 充值套餐配置
     */
    public static final String INTERFACE_GOODSCONFIG = "/config/goods";
    /**
     * 联系人备份
     */
    public static final String INTERFACE_CONTACTINFO = "/contacts/info";
    /**
     * 阅读反馈接口
     */
    public static final String INTERFACE_PUSHNOTIFY = "/statistic/push_notify";
    /**
     * 得到注册用的通道号码
     */
    public static final String INTERFACE_GETNUMBER = "/autoreg/getnum";
    /**
     * 获取token
     */
    public final static String INTERFACE_GET_TOKEN = "/ott/gettoken";
    /**
     * 获取vs好友
     */
    public final static String INTERFACE_GET_VSFRIEND = "/user/iskc";
    /**
     * 获取短信验证码
     */
    public static final String INTRFACE_VERIFY_NUMBER = "/user/reset_pwd_apply";
    /**
     * 注册校验验证码
     */
    public static final String INTRFACE_VERIFY_MSG_REG = "/account/vsregchkcode.act";
    /**
     * 重置密码校验验证码
     */
    public static final String INTRFACE_VERIFY_MSG = "/user/reset_pwd_check";
    /**
     * 重置密码
     */
    public static final String INRFACE_REST_PWD = "/user/reset_pwd";
    /**
     * 修改密码
     */
    public static final String INRFACE_CHANGE_PWD = "/user/change_pwd";
    /**
     * 登录接口
     */
    public static final String INRFACE_LOGIN = "/account/login";
    /**
     * 心跳上报，上报活跃
     */
    public static final String INTERFACE_HEARTBEAT = "/statistic/heartbeat";
    /**
     * 拉取push消息/statistic/pull_msg2
     */
    public static final String INTERFACE_PULLMSG = "/push/pull";
    /**
     * 拉取系统公告
     */
    public static final String INTERFACE_SYSMSG = "/config/sys_msg";
    /**
     * 充值卡列表
     */
    public static final String INTERFACE_EXCHANGE_CARD = "/user/card_list";
    public final static String actionexchange = "action_exchange";

    /**
     * 拉取升级信息
     */
    public static final String INTERFACE_UPDATE = "/config/update";
    public final static String VS_ACTION_UPGRADE = "com.keepc.action.upgrade";
    /**
     * 把消息设置为已读
     */
    public static final String INTERFACE_PUSH_NOTIFY = "/statistic/push_notify";
    /**
     * 获取邀请人数
     */
    public final static String INTERFACE_BOUNTY_COUNT = "/bounty/count";

    /**
     * 检测是否为老用户
     */
    public static final String INTERFACE_QUERYUSER = "/account/reg";
    /**
     * 注册获取验证码
     */
    public static final String GET_REG_CODE = "/account/verify_code";
    /**
     * 重置密码
     */
    public static final String SET_NEW_PASSWORD = "/account/reset_pwd";
    /**
     * 获取token 融云
     */
    public final static String INTERFACE_GET_TOKEN_RONGYUN = "/im_gw/token";

    /**
     * 查询余额
     */
    public static final String INTERFACE_SIGIN = "/check_in";

    /**
     * 绑定验证码
     */
    public static final String INTERFACE_BIND_CODE = "/user/bind_req";
    /**
     * 绑定手机
     */
    public static final String INTERFACE_BIND_PHONE = "/user/bind_phone";
    /**
     * 始终呼叫号码
     */
    public final static String VS_ACTION_SETCALLPHONE = "com.sangcall.action.setcallphone";
    /**
     * 变量函数
     */
    public static Float density = 1.5F;
    public static int width = 0;
    public static int height = 0;
    public final static char IMAGE_TYPE_PNGORJPG = 1;// 其他格式
    public final static char IMAGE_TYPE_JIF = 0;// jif格式
    /**
     * 当前的fragment
     */
    public static int curIndicator = 0;

    // 通知栏
    public static int NotificationID = 1001;

    public static int TAG = 0;
    /**
     * 启动页广告
     */
    public static final String JKEY_AD_SPLASHIMAG_PATH = "JKEY_AD_SPLASHIMAG_PATH";
}