package com.weiwei.base.dataprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * 用户和系统一些配置信息
 *
 * @author Administrator
 */
public class VsUserConfig {
    /**
     * 手势密码
     */
    public final static String JKEY_SETTING_UNLOCKED_BTN = "unlocked_btn";
    public final static String JKey_RED_PAGE = "red_page";
    public final static String JKey_Money = "redbage_money";// 红包余额
    public final static String JKey_Status = "redbage_status";// 红包微信绑定状态
    public final static String JKey_new_version_pgyer = "JKey_new_version_pgyer";
    public final static String JKey_new_version_pgyer_url = "JKey_new_version_pgyer_url";
    public final static String JKEY_NUM_PEOPLE = "JKEY_NUM_PEOPLE";
    /**
     * 广告
     */
    public static final String JKEY_AD_CONFIG_300001 = "JKEY_AD_CONFIG_300001";// 发现顶部广告位
    public static final String JKEY_AD_CONFIG_000001 = "JKEY_AD_CONFIG_000001";// 通话记录顶部广告位
    public static final String JKEY_AD_CONFIG_300002 = "JKEY_AD_CONFIG_300002";// 更多界面的跑马灯
    public static final String JKEY_AD_CONFIG_400501 = "JKEY_AD_CONFIG_400501";// 启动界面图片拉去

    public static final String JKEY_AD_CONFIG_0001 = "JKEY_AD_CONFIG_0001";
    public static final String JKEY_AD_CONFIG_1001 = "JKEY_AD_CONFIG_1001";
    public static final String JKEY_AD_CONFIG_3001 = "JKEY_AD_CONFIG_3001";
    public static final String JKEY_AD_CONFIG_3002 = "JKEY_AD_CONFIG_3002";
    public static final String JKEY_AD_CONFIG_4001 = "JKEY_AD_CONFIG_4001";
    public static final String JKEY_AD_CONFIG_4002 = "JKEY_AD_CONFIG_4002";
    public static final String JKEY_AD_CONFIG_4003 = "JKEY_AD_CONFIG_4003";

    public final static String JKey_Driect_Call = "JKey_Driect_Call";
    public final static String JKey_dial_types = "JKey_dial_types";
    public final static String JKey_dial_swich = "JKey_dial_swich";

    public final static String JKey_Weixin = "jkey_weixin";
    public final static String JKey_Single_Init = "jkey_single_init";
    public final static String JKey_tiem_set = "jkey_tiem_set";
    public final static String JKey_Card_Direct = "jkey_card_direct";
    public final static String JKey_GET_MY_SHARE_BRO = "jkey_get_my_share_bro";
    /**
     * 设置钢琴音选中的Id
     */
    public static final String JKEY_PIANO_ISCHECHED_ID = "KETY_PIANO_ISCHECHED_IDS";
    /**
     * 广告位开关控制
     */
    public static String JKEY_AD_SWITCH = "jkey_ad_switch";
    /**
     * 广告位关闭时间
     */
    public static String JKEY_AD_CLOSE_TIME = "jkey_ad_close_time";
    /**
     * 通话记录分类显示
     */
    public static String JKEY_SHOW_HIDE_TBAR = "jkey_show_hide_bar";
    public static String JKEY_SHOW_HIDE_EDIT = "jkey_show_hide_edit";
    public static String JKey_Call_Number = "jkey_call_number";
    public static String JKey_Call_Name = "jkey_call_name";
    public static String JKey_Call_Local = "jkey_call_local";
    public static String JKey_Call_DialType = "jkey_call_dialtype";
    /**
     * 查询是老用户信息
     */
    public final static String VS_ACTION_CHECK_USER_INFO = "com.kc.logic.checkuser_INFO";
    /**
     * 邀请人
     */
    public final static String Jey_Invited_By = "Jey_invited_by";
    /**
     * 改绑手机2
     */
    public final static String VS_ACTION_CHANGE_PHONE_TWO = "com.vs.change_pohne_two";
    /**
     * 改绑手机1
     */
    public final static String VS_ACTION_CHANGE_PHONE = "com.vs.change_pohne";

    public final static String JKey_Invite_person = "JKey_Invite_person";
    public final static String JKey_GET_MY_SHARE = "Jkey_get_my_share";
    /**
     * 签到相关
     */
    public final static String JKey_sign_again = "sign_again";// 是否重复签到
    public final static String JKey_sign_success_header = "sign_success_header";// 签到成功信息保存
    public final static String JKey_sign_success_explain = "sign_success_explain";// 签到成功信息保存
    public final static String JKey_sign_btnresult = "sign_btnresult";// 签到分流返回结果
    public final static String JKey_sign_btnresult_target = "sign_btnresult_target";// 签到分流返回结果跳转说明
    public final static String JKey_sign_btntext = "sign_btntext";// 签到分流按钮文字
    public final static String JKey_sign_btntarget = "sign_btntarget";// 签到分流跳转地址\
    public final static String JKey_SIGNIN_SHARE = "signin_share";// 签到分享内容
    public final static String JKey_SigninSuccessTime = "SigninSuccessTime";// 当天进入签到时间

    public final static String JKey_sign_header = "sign_header";// 签到头部服务器返回信息
    public final static String JKey_sign_explain = "sign_explain";// 签到描述信息服务器返回信息
    public final static String JKey_sign_mode = "sign_mode";// 签到类型
    public static String is_show = "jkey_url_ishow";

    /**
     * 个人信息界面
     */

    public final static String JKey_MyInfo_Level = "jkey_myinfo_level";// 等级
    public final static String JKey_MyInfo_LevelName = "jkey_myinfo_levelname";// 等级名字
    public final static String JKey_MyInfo_Icon = "jkey_myinfo_icon";// 头像
    public final static String JKey_MyInfo_Nickname = "jkey_myinfo_nickname";// 昵称
    public final static String JKey_MyInfo_Mobile = "jkey_myinfo_mobile";// 手机
    public final static String JKey_MyInfo_Gender = "jkey_myinfo_gender";// 性别
    public final static String JKey_MyInfo_Birth = "jkey_myinfo_birth";// 生日
    public final static String JKey_MyInfo_Province = "jkey_myinfo_province";// 省份
    public final static String JKey_MyInfo_City = "jkey_myinfo_city";// 城市
    public final static String JKey_MyInfo_MailBox = "jkey_myinfo_mailbox";// 邮箱
    public final static String JKey_MyInfo_photo = "jkey_myinfo_photo";// 相片
    public final static String JKey_MyInfo_Rider = "jkey_myinfo_rider"; //骑手

    /**
     * 保存余额信息
     */
    public final static String JKey_Balance_Left = "jkey_balance_left";
    /**
     * 话单流水信息加载完成
     */
    public static final String JKey_GET_MY_CALL_LOG = "jkey_get_my_call_log";
    /**
     * 套餐信息加载完成
     */
    public static final String JKey_GET_MY_TAOCAN_LOG = "jkey_get_my_taocan_log";
    /**
     * 红包提取完成
     */
    public static final String JKey_GET_MY_REDMONEY = "jkey_get_my_red_money";

    public static final String JKey_GET_MY_BANK = "jkey_get_my_bank";
    /**
     * 红包流水信息加载完成
     */
    public static final String JKey_GET_MY_RED_LOG = "jkey_get_my_red_log";

    /**
     * 话费流水信息加载完成
     */
    public static final String JKey_GET_MY_CALL_MONEY = "jkey_get_my_call_money";

    /**
     * 存储企业号信息
     */
    public final static String JKEY_AGENT_INFO = "addr_ip_list";
    /**
     * 多点接入Ip地址列表
     */
    public final static String ADDR_IP_LIST = "addr_ip_list";

    /**
     * 服务条款URL
     */
    public static String JKEY_URL_SERVICE_TIAO = "jkey_url_service_tiao";
    /**
     * 商城URL
     */
    public static String JKEY_URL_MALL = "jkey_url_mal";
    /**
     * tab服务URL
     */
    public static String JKEY_URL_SERVICE = "jkey_url_service";
    // /**
    // * 第一次使用拨号盘删除键的时候弹出提示
    // */
    // public static final String JKEY_FIRST_ENTER_KEYBORD_DELETE =
    // "jkey_first_enter_keybord_delete";
    /**
     * 当前通话界面的clentId
     */
    public static String jkey_clentid_chatting = "jkey_clentid_chatting";
    /**
     * 只统计一次限制_启动
     */
    public static String JKEY_TJ_ONE_START = "jkey_tj_one_start";
    /**
     * 只统计一次限制
     */
    public static String JKEY_TJ_ONE = "jkey_tj_one";
    /**
     * 我界余额查询广播
     */
    public static String JKEY_SEARCH_BALANCE = "jkey_search_balance";

    /**
     * 余额提醒标记
     */
    public static String JKEY_BALANCE_HINT = "jkey_balance_hint";
    /**
     * 保存当前日期
     */
    public static String JKEY_DATE_NOW = "jkey_date_now";
    /**
     * 保存余额
     */
    public static String JKEY_BALANCE_SAVE = "jkey_balance_save";
    /**
     * 余额有效期
     */
    public static String JKEY_VALID_DATE = "jkey_valid_date";
    /**
     * 是否第一次加载通话记录
     */
    public static String JKEY_FRIST_LOAD_CALLLOG = "jkey_frist_load_calllog";
    /**
     * 对话框不在提醒
     */
    public static String JKEY_DIALOG_HINT = "jkey_dialog_hint";
    /**
     * 云之讯拨打电话服务器地址
     */
    public static String JKEY_UCPASS_URL = "jkey_ucpass_url";
    /**
     * 云之讯拨打电话服务器端口
     */
    public static String JKEY_UCPASS_PORT = "jkey_ucpass_port";
    /**
     * 拨打切换提醒
     */
    public static String JKEY_CHANGE_CALL = "jkey_change_call";
    /**
     * 没有联系人
     */
    public static String JKEY_NO_CONTACTS = "jkey_no_contacts";
    /**
     * 获取VS好友 flag
     */
    public final static String JKEY_GETVSUSERINFO_FLAG = "jkey_getkcuserinfo_flag";
    /**
     * 保存获取本地联系人是否是VS好友结果
     */
    public static final String JKEY_GETVSUSERINFO = "jkey_getkcuserinfo";
    /**
     * 拨打核心服务是否连接成功
     */
    public static final String JKEY_CALLSERVER_FLAG = "jkey_callserver_flag";
    /**
     * 查询是否为新注册Action
     */
    public final static String VS_ACTION_CHECK_USER = "com.kc.logic.checkuser";
    /**
     * 获取注册验证码
     */
    public final static String VS_ACTION_GET_CODE = "com.kc.get.code";
    public final static String VS_ACTION_RESETPWD_CODE = "com.kc.resetpwd.code";
    public final static String VS_ACTION_SET_PASSWORD = "com.kc.set.password";
    /**
     * 切换TAB改变清空编辑框数据广播
     */
    public static final String JKey_CLOSE_USER_LEAD = "jkey_close_user_lead";
    /**
     * 切换TAB收起分组选择
     */
    public static final String JKey_CLOSE_GROUP_TAB = "jkey_close_group_tab";
    /**
     * Kc好友加载完成
     */
    public static final String JKey_GET_VSUSER_OK = "jkey_get_vsuser_ok";
    /**
     * Kc好友加载失败
     */
    public static final String JKey_GET_VSUSER_FAIL = "jkey_get_vsuser_fail";
    /**
     * 第一次进入短信邀请分享界面速度
     */
    public static final String JKey_FIRST_ENTER_INVITE = "jkey_first_enter_invite";
    /**
     * 用户是否开通过来电显示-开通过：true 未开通过：false
     */
    public final static String JKey_IsHadOpenedCallDispaly = "IsHadOpenedCallDispaly";
    public final static String JKey_SHARE_SM = "share_sm";// 二维码分享

    /**
     * 去显
     */
    public final static String JKey_ValidityTime = "ValidityTime";// 去显有效期
    public final static String JKey_CallDispState = "CallDispState";// 去显状态
    /**
     * 玩家邀请数量
     */
    public static final String JKEY_INVITE_NUMBER = "jkey_invite_number";
    /**
     * 上次玩家邀请数量(用于做邀请页面动画)
     */
    public static final String JKEY_INVITE_OLD_NUMBER = "jkey_invite_old_number";
    /**
     * 键盘是否打开
     */
    public final static String JKEY_KEYBORD_IS_SHOW = "jkey_keybord_is_show";
    /**
     * 是否进行过其他有余额变化的操作
     */
    public static boolean isChangeBalance = false;
    /**
     * 用户是否切换过帐号
     */
    public final static String JKey_ActionSwitchAccount = "ActionSwitchAccount";
    /**
     * 是否进行过其他有余额变化的操作
     */
    public static long changeBalanceTime = 0;
    /**
     * 当天提醒了多少次了
     */
    public final static String JKEY_UPGRADECURRENTTIPSNUMBER = "jkey_upgradecurrenttipsnumber";
    public static final String JKey_UPGRADE_DAY = "jkey_upgrade_day";
    /**
     * 系统公告ID
     */
    public final static String JKey_ReadSysMsgID = "ReadSysMsgID";
    public final static String JKey_ReadSysMsgTime = "JKey_ReadSysMsgTime";
    /**
     * 回拨自动接听开关
     */
    public static final String JKey_CALL_ANSWER_SWITCH = "call_answer_switch";
    /**
     * 计算回拨费用开始时间
     */
    public static long CallBackStartTime = 0L;
    /**
     * 计算回拨费用结束时间
     */
    public static long CallBackEndTime = 0L;
    /**
     * 获取上报错误日志标准日期
     */
    public static final String JKey_GET_ERROR_LOG = "get_error_log";
    /**
     * 是否已经设置兼容模式
     */
    public final static String JKey_DIALTESTMODELINCALL = "jkey_dialtestmodelincall";
    /**
     * 保存用户手机的默认音频模式
     */
    public static final String JKEY_MEDIA_MODE = "jkey_media_mode";
    /**
     * 保存用户手机的默认免提开关
     */
    public static String JKEY_MEDIA_SPEAKERON = "jkey_media_speakerOn";
    /**
     * 用户第一次进入我的帐户标志
     */
    public static String Jkey_FIRST_ACCOUNT = "jkey_first_account";
    /**
     * 本地音频响铃模式
     */
    public static String JKEY_MEDIA_RINGERMODE = "jkey_media_RingerMode";
    /**
     * 直拨组件注册成功
     */
    public final static String JKEY_SPINITSUCC = "spinitsucc";
    /**
     * token
     */
    public final static String JKEY_TOKEN = "kc_token";

    /**
     * token 融云
     */
    public final static String JKEY_TOKEN_RONGYUN = "kc_token_rongyun";

    /**
     * token 融云 返回结果
     */
    public final static String JKEY_TOKEN_RONGYUN_RESULT = "kc_token_rongyun_result";
    /**
     * 电话文件存放目录
     */
    public final static String mWldhFilePath = Environment.getExternalStorageDirectory() + File
            .separator + "data" + File.separator + "wldh" + File.separator;
    /**
     * 电话文件存放目录
     */
    public static String SavePath = mWldhFilePath;
    /**
     * 呼叫结束是否显示提示
     */
    public static final String JKey_IsSetCallTishi = "IsSetCallTishi";
    /**
     * 呼叫结束是否显示提示
     */
    public static final String JKey_CallEndShowTishi = "CallEndShowTishi";
    /**
     * 上报活跃日期
     */
    public static final String JKey_REPORT_ACTIVE_DAY = "report_on_active_day";
    /**
     * 充值套餐信息(套餐信息)
     */
    public final static String JKey_NewGoodsInfo = "RechargeGoodsInfo";
    /**
     * 充值任务
     */
    public final static String JKey_NewTaskInfo = "RechargeTaskInfo";
    /**
     * 微信分享图片地址
     */
    public final static String JKey_WEIXIN_SHARE_IMAGE_LOCAL_URL = "WEIXIN_SHARE_IMAGE_LOCAL_URL";
    /**
     * 二维码邀请
     */
    public final static String JKey_DIECODE_SHARE_CONTENT = "diecode_share_content";
    public final static String JKey_Code_Account = "code_account";// 扫描得到的id
    /**
     * QQ定向分享内容
     */
    public final static String JKey_QQDX_SHARE_CONTENT = "qqdx_share_content";
    /**
     * 微信分享内容
     */
    public final static String JKey_WEIXIN_SHARE_CONTENT = "weixin_share_content";
    /**
     * 微信圈分享内容
     */
    public final static String JKey_WEIXINQUAN_SHARE_CONTENT = "weixinquan_share_content";
    /**
     * 新浪微博分享
     */
    public final static String JKey_WEIBO_SHARE_CONTENT = "weibo_share_content";
    /**
     * vip会员中心地址
     */
    public final static String JKEY_VIP_CENTER_URL = "vip_center_url";
    /**
     * 收支明细和充值记录
     */
    public final static String JKey_ACCOUNT_DETAILS = "account_details";
    /**
     * 查询话单地址
     */
    public final static String JKey_CALL_LOG = "call_log";
    /**
     * 当天需要提醒升级次数
     */
    public final static String JKEY_UPGRADETIPSNUMBER = "jkey_upgradetipsnumber";
    /**
     * 在线升级URL
     */
    public final static String JKey_UpgradeUrl = "JKey_UpgradeUrl";
    public final static String JKey_FIRVERSION = "JKey_FirVersion";
    public final static String JKey_FIRUPDATEURL = "JKey_FirUpdateUrl";
    /**
     * 升级apk下载保存的路径
     */
    public final static String JKey_UPDATE_APK_FILE_PATH = "JKey_update_apk_file_path";
    /**
     * 在线升级内容
     */
    public final static String JKey_UpgradeInfo = "JKey_UpgradeInfo";
    /**
     * 在线升级方式
     */
    public final static String JKey_UpgradeMandatory = "JKey_UpgradeMandatory";
    public final static String JKey_new_version = "JKey_new_version";
    /**
     * MO发送短信的手机号
     */
    public final static String JKEY_SENDNOTESERVICESPHONE = "JKEY_SENDNOTESERVICESPHONE";
    /**
     * 短信的标识
     */
    public final static String JKEY_REG_BIND_IDENTIFY = "reg_bind_identify";
    /**
     * 错误日志大小开关
     */
    public static String JKEY_REPORT_CONFIGFLAG = "report_configflag";
    /**
     * 错误日志大小标准
     */
    public static String JKEY_REPORT_CONFIGSIZE = "report_configsize";
    /**
     * 获取短信验证码成功
     */
    public final static char MSG_ID_GET_MSG_SUCCESS = 32;
    /**
     * 加载图片成功
     */
    public final static char MSG_ID_GET_IMAGE_SUCCESS = 189;
    /**
     * 保存版本号。用于判断是否覆盖安装
     */
    public static final String JKey_V = "DfineV";
    /**
     * 网络质量很差
     */
    public static final char MSG_ID_NETWORK_BAD = 800;
    /**
     * 网络质量较差
     */
    public static final char MSG_ID_NETWORK_LittleBAD = 801;
    /**
     * 网络质良好
     */
    public static final char MSG_ID_NETWORK_GOOD = 802;
    /**
     * 网络质一般
     */
    public static final char MSG_ID_NETWORK_GENERA = 803;
    /**
     * 提交发送短信的号码
     */
    public final static String VS_ACTION_POSTSENDNOTE_NUMBER = "com.kc.postsendnote.number";
    /**
     * 分享手机号
     */
    public final static String VS_ACTION_SHAREPHONENUMBER = "com.kc.logic.share_phonenumber";
    /**
     * 推荐好友的开关和内容 短信分享
     */
    public final static String JKey_FRIEND_INVITE = "friend_invite";// 推荐好友内容
    public final static String JKey_FRIEND_TITLE = "friend_title";// 小标题
    public final static String JKey_FRIEND_PROMPT = "friend_promot";// 小标题下面内容
    public final static String JKey_FRIEND_HEADLINE = "friend_headline";// 大标题
    public final static String JKey_FRIEND_DETAIL = "friend_detail";// 大标题下面内容
    public final static String JKey_FRIEND_ONSHOW = "friend_onshow";// 推荐好友开关

    /**
     * 安装统计 有UID时
     */
    public final static String JKey_RECORDINSTALL_WITH_UID = "recordinstall_with_uid";
    /**
     * 安装统计 无UID时
     */
    public final static String JKey_RECORDINSTALL_NO_UID = "recordinstall_no_uid";
    /**
     * 动态获取直拨地址
     */
    public final static String JKEY_DriectCallAddrAndPort = "JKEY_DRIECTCALLADDRANDPORT";
    /**
     * 动态获拨打服务域名
     */
    public final static String JKEY_DriectCallAddr = "JKEY_DRIECTCALLADDR";
    /**
     * 动态获拨打服务端口
     */
    public final static String JKEY_DriectCallPort = "JKEY_DRIECTCALLPORT";
    /**
     * 支付宝下载地址
     */
    public static final String JKey_AlipayDownUrl = "DfineAlipayDownUrl";
    /**
     * 银联说明网站
     */
    public final static String JKey_UpompWebsite = "UpompWebsite";
    /**
     * 充值套餐信息(充值类型)
     */
    public final static String JKey_PayTypes = "RechargePayTypesInfo";

    /**
     * 版本信息
     */
    public final static String JKey_cateTypes = "CateTypesInfo";

    public final static String JKey_kefuTypes = "KefuTypesInfo";

    /**
     * 系统公告
     */
    public final static String JKey_Announcement = "AnnouncementInfo";

    /**
     * 发现页配置信息
     */
    public final static String JKey_discoverTypes = "DiscoverTypesInfo";

    /**
     * 获取验证码
     */
    public final static String VS_ACTION_RESET_PWD_APPLY = "com.kc.logic.reset_pwd_apply";
    /**
     * 校验验证码
     */
    public final static String VS_ACTION_RESET_PWD_CHECK = "com.kc.logic.reset_pwd_check";
    /**
     * 重置密码
     */
    public final static String VS_ACTION_RESET_PWD = "com.kc.logic.reset_pwd";
    /**
     * 重置密码
     */
    public final static String VS_ACTION_CHANGE_PWD = "com.kc.logic.change_pwd";
    /**
     * 注册广播Action
     */
    public final static String VS_ACTION_REGISTER = "com.kc.logic.register";
    /**
     * QQ登录Action
     */
    public final static String VS_ACTION_TERM_CONF_THRID_LOGIN = "com.kc.action" + "" +
            ".term_conf_thrid_login";
    /**
     * QQ绑定Action
     */
    public final static String VS_ACTION_TERM_CONF_THRID_BIND = "com.kc.action" + "" +
            ".term_conf_thrid_bind";
    /**
     * 自动注册成功广播
     */
    public final static String VS_ACTION_AUTO_REGISTER_SUCCESS = "com.kc.succeed_register";

    /**
     * 登录成功加载通话记录
     */
    public final static String VS_ACTION_LOGIN_LOADING_DIAL = "com.vs.login.loading.dial";
    /**
     * 保存QQ登录的token
     */
    public final static String JKEY_TENCENTLOGINTOKON = "jkey_tencentlogintokon";
    /**
     * 保存QQ登录的openid
     */
    public final static String JKEY_TENCENTLOGINOPENID = "jkey_tencentloginopenid";
    /**
     * 保存QQ登录的expires_in
     */
    public final static String JKEY_TENCENT_LOGIN_EXPIRES_IN = "jkey_tencentloginexpiresin";
    /**
     * 登录的QQ是否是VIP
     */
    public final static String JKEY_TENCENTLOGINOISVIP = "jkey_tencentloginisvip";

    /**
     * 登陆是否成功标识。每次登录都会变化
     */
    public final static String JKEY_NEWTENCENTLOGINTOKON = "jkey_newtencentlogintokon";

    /**
     * 存储的绑定号码
     */
    public final static String JKey_BindPhoneNumberHint = "JKey_BindPhoneNumberHint";

    /**
     *
     */
    public final static char MSG_ID_GET_SEND_SMS_SIGNAL = 101;
    /**
     * 卡密和状态 arry[0]卡号、array[1]密码、array[2]提交状态 (0默认、1成功、2失败);
     */
    public static ArrayList<String[]> cardList = new ArrayList<String[]>();
    /**
     * 发送短信生成的sid
     */
    public final static String JKey_MO_SID = "jkey_mo_sid";
    /**
     * 新用户注册30分钟内免费信息
     */
    public final static String JKey_RegAwardSwitch = "RegAwardSwitch";
    public final static String JKey_RegAwardTip = "RegAwardTip";
    public final static String JKey_RegSurplus = "RegSurplus";
    public final static String JKey_start_time = "JKey_start_time";
    public final static String PREFS_LAST_CALLRECORD = "PREFS_LAST_CALLRECOR";
    // /**
    // * 是否还验证登录
    // */
    // public static final String JKey_IS_NEED_VALIDATE_LOGIN =
    // "is_need_validate_login";
    public final static String JKey_UriAndPort = "DfineUriPrefixport";
    /**
     * 拨打设置的值
     */
    public final static String JKey_USERDIALVALUE = "userDialValue1";
    /**
     * 第三方拨打标示
     */
    public final static String JKey_THIRDCALLVALUE = "jkey_thirdcallvalue";
    public final static String JKey_THIRDCALLIP = "jkey_thirdcallip";
    public final static String JKey_THIRDCALLPORT = "jkey_thirdcallport";
    public final static String JKey_THIRDCALLACCOUNT = "jkey_thirdcallaccount";
    public final static String JKey_THIRDCALLPASSWORD = "jkey_thirdcallpassword";

    /**
     * 是否是手动拨打 0不是手动 1是手动
     */
    public final static String JKey_Hand_USERDIALVALUE = "hand_userdialvalue";
    /**
     * wifi下回拨
     */
    public final static String JKey_WIFI_CALLBACK = "wifi_callback";
    /**
     * 3g、4g下回拨
     */
    public final static String JKey_3G_4G_CALLBACK = "callback_3g_4g";
    /**
     * 用户验证绑定的手机号码
     */
    public final static String JKey_PhoneNumber = "PREFS_PHONE_NUMBER";
    public final static String JKey_LoginToken = "PREFS_PHONE_TOKEN";
    /**
     * 首次拨打标识
     */
    public final static String JKey_FirstCallState = "FirstCallState";// 首次拨打状态
    /**
     * 首次拨打后记住时间日期
     */
    public final static String JKey_FirstCallTime = "FirstCallTime";// 首次拨打后记下时间
    /**
     * QQ定向分享图片地址
     */
    public final static String JKey_QQDX_SHARE_IMAGE_LOCAL_URL = "qqdx_share_image_local_url";
    /**
     * 用户是否注销退出
     */
    public static String JKEY_ISLOGOUTBUTTON = "islogoutbutton";
    /**
     * 第一次进入应用
     */
    public final static String JKEY_FRIST_LOGIN_APP = "jkey_frist_login_app";

    /**
     * 下载归属地地址
     */
    public static final String JKey_PhoneNumberUrl = "DfinePhoneNumberUrl";
    /**
     * 是否加载过系统通话记录
     *
     * @deprecated
     */
    // public final static String JKey_LoadedSysCallLog = "LoadedSysCallLog";
    /**
     * 上次加载系统联系人，最新一条记录的时间
     */
    public final static String JKey_LoadedSysCallLogLastTime = "JKey_LoadedSysCallLogLastTime";
    /**
     * 上次读取未接来电的时间
     */
    public final static String JKey_CheckSysMissedCallLogLastTime =
            "JKey_CheckSysMissedCallLogLastTime";

    /**
     * 保存未接来电记录
     */
    public final static String JKey_missed_call = "JKey_missed_call";

    public final static String JKEY_APPSERVER_DEFAULT_CONFIG_FLAG = "APPSERVER_DEFAULT_CONFIG";
    public final static String JKey_APPSERVER_TEMPLATE_CONFIG_FLAG = "APPSERVER_TEMPLATE_CONFIG";
    public final static String JKEY_APPSERVER_GOODS_CONFIG_FLAG = "APPSERVER_GOODS_CONFIG_FLAG";
    // 商品配置flag
    public final static String JKEY_APPSERVER_AD_CONFIG_FLAG = "APPSERVER_AD_CONFIG_FLAG";//
    // 广告信息flag
    public final static String JKEY_APPSERVER_SYSMSG__FLAG = "appserver_sysmsg__flag";// 系统公告flag
    public final static String JKEY_APPSERVER_MAKEMOENYCONFIG_FLAG =
            "appserver_makemoenyconfig_flag";// 玩赚falg
    public final static String JKey_ShortCut = "ShortCut";
    /**
     * VS安装时间
     */
    public final static String JKey_InstallTime = "InstallTime";
    /**
     * 手机官网
     */
    public final static String JKey_phone_wap = "JKey_phone_wap";
    /**
     * 电脑官网
     */
    public final static String JKey_computer_wap = "JKey_computer_wap";
    /**
     * 客服电话
     */
    public final static String JKey_ServicePhone = "service_phone";
    /**
     * 客服QQ
     */
    public final static String JKey_ServiceQQ = "service_qq";

    public final static String JKey_ServiceTime = "service_time";

    public final static String JKey_CopyRight = "service_copyRight";
    public final static String JKey_WechatPublic = "service_WechatPublic";
    public final static String JKey_SinaWeibo = "service_SinaWeibo";
    public final static String JKey_LogoName = "service_LogoName";

    /**
     * 帮助中心URL
     */
    public static String JKEY_URL_HELP = "jkey_url_help";
    /**
     * 什么是回拨
     */
    public static String JKEY_URL_CALLBACK = "jkey_url_callback";
    /**
     * 充值说明URL
     */
    public static String JKEY_URL_CHARGE = "jkey_url_charge";
    /**
     * 资费说明URL
     */
    public static String JKEY_URL_TARIFF = "jkey_url_tariff";
    /**
     * TCP长连接保存的ip
     */
    public static final String JKEY_TCP_HOST = "jkey_tcp_host";
    /**
     * TCP长连接保存的端口
     */
    public static final String JKEY_TCP_PORT = "jkey_tcp_port";
    /**
     * 注意: 当invitedflag为1时,代表联盟渠道推广, 注册接口invite参数取channelid
     * 当invitedflag为kc时,代表好友推荐, 注册接口invite参数取invitedby
     * <p>
     * 另外注册接口cmwap/mo/mt 相应都增加invitedway和 invitedflag参数
     */
    public final static String JKEY_INVITEDWAY = "invitedway";// CPC返回的推荐方式
    public final static String JKEY_INVITEDBY = "invited";// cpc返回的推荐人uid
    public final static String JKEY_INVITED_BY = "invitedBy";

    /**
     * 首次绑定标识符
     */
    public final static String JKey_FIRSTBIND_QQKJ = "firstbind_qqkj";
    public final static String JKey_FIRSTBIND_TENX = "firstbind_tenx";
    public final static String JKEY_FIRSTBIND_SINA = "firstbind_sina";
    /**
     * 首次充值标识
     */
    public final static String JKey_FirstRechargeState = "FirstRechargeState";
    /**
     * 首次银联充值标识
     */
    public final static String JKey_FirstUpompRechargeState = "FirstUpompRechargeState";
    /**
     * 多点接入状态
     */
    public final static String JKEY_TestAccessPointState = "jkey_testaccesspointstate";
    /**
     * 当前保存的tcp连接地址都连接不成功的时候 .在去拉取一次新的配置. 一天只拉取一次.此变量用来保存拉取配置的时间
     */
    public static final String JKEY_GETDEFAULTTIME = "jkey_getdefaulttime";
    /**
     * TCP长连接是否被踢掉
     */
    public static final String JKEY_IS_KICKOUT = "jkey_iskickout";
    public final static String PREFS_NAME = "PREFS_KC2011";
    public final static String JKey_KcId = "PREFS_ID_OF_KC";
    public final static String JKey_KcOldId = "PREFS_OLD_ID_OF_KC";
    public final static String JKey_Password = "PREFS_PASSWORD_OF_KC";
    public final static String JKey_tcpsid = "tcpsid";
    /**
     * 备份相关
     */
    public final static String JKey_ContactBakTime = "ContactBakTime";
    public final static String JKey_ContactRenewBakTime = "ContactRenewBakTime";
    public final static String JKey_ContactLocalNum = "ContactLocalNum";
    public final static String JKey_ContactServerNum = "ContactServerNum";

    /**
     * 设置按键音开关
     */
    public static String JKEY_SETTING_KEYPAD_TONE = "setting_keypad_tone";
    /**
     * 语音提醒
     */
    public static String JKEY_SETTING_HINT_VOICE = "setting_hint_voice";
    /**
     * 是否有新版本
     */
    public final static String HAS_NEW_VERSION = "has_new_version";
    /**
     * 查询余额相关
     */
    public final static String JKey_Balance = "total_balance";// 查询余额中的总余额
    public final static String JKey_BasicBalance = "BASICBALANCE";// 查询余额中的基本账户余额
    public final static String JKey_GiftBalance = "GIFTBALANCE";// 查询余额中的赠送账户余额
    public final static String JKey_VipValidtime = "VipValidtime";// 查询余额中的VIP有效期
    public final static String JKey_GiftExpireTime = "GiftExpireTime";
    public final static String JKey_ValidDate = "ValidDate";
    /**
     * 为绑定拨打限制次数
     */
    public final static String JKey_NOBind_CallNum = "NoBindCallNumber";

    public final static String JKey_GET_GOO = "NoBindCallNumber";

    public static String getDataString(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getString(key, "");
    }

    public static String getDataString(Context context, String key, String def) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getString(key, def);
    }

    public static int getDataInt(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getInt(key, 0);
    }

    public static int getDataInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getInt(key, value);
    }

    public static boolean getDataBoolean(Context context, String key, boolean defaltBol) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getBoolean(key, defaltBol);
    }

    public static long getDataLong(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        return settings.getLong(key, 0);
    }

    public static void setData(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setData(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setData(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setData(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(VsUserConfig.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 删除存储的优惠活动
     *
     * @param context
     */
    public static void removeFavourableInfo(Context context) {

        SharedPreferences settings = context.getSharedPreferences(Resource
                .PREFS_NAME_FAVOURABLE_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("content");
        editor.commit();
    }

    /**
     * 存储优惠活动
     *
     * @param context
     */
    public static void saveFavourableInfo(Context context, String content) {

        SharedPreferences settings = context.getSharedPreferences(Resource
                .PREFS_NAME_FAVOURABLE_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("content", content);
        editor.commit();
    }

    /**
     * 得到存储的优惠活动
     *
     * @param context
     */
    public static String getFavourableInfo(Context context) {

        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(Resource
                .PREFS_NAME_FAVOURABLE_INFO, 0);
        return settings.getString("content", "");
    }

    /**
     * 删除充值说明
     *
     * @param context
     */
    public static void removePayInfo(Context context) {

        SharedPreferences settings = context.getSharedPreferences(Resource.PREFS_NAME_PAY_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("content");
        editor.commit();
    }

    /**
     * 存储充值说明
     *
     * @param context
     */
    public static void savePayInfo(Context context, String content) {

        SharedPreferences settings = context.getSharedPreferences(Resource.PREFS_NAME_PAY_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("content", content);
        editor.commit();
    }

    /**
     * 获取充值说明
     *
     * @param context
     */
    public static String getPayInfo(Context context) {

        // Restore preferences
        SharedPreferences settings = context.getSharedPreferences(Resource.PREFS_NAME_PAY_INFO, 0);
        return settings.getString("content", "");
    }
}
