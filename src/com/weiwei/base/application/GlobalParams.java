/**
 * Copyright 2014 eTao Tech. Inc. LTD. All rights reserved.
 * - Powered by Team GOF. -
 */

package com.weiwei.base.application;

import android.os.Build;

/**
 * @ClassName: GlobalParams
 * @Description: 全局静态变量存储
 * @author jinsongliu 
 * @date 2015-03-03 上午10:22:42
 * 
 */

public class GlobalParams {

	/**
	 * wap的代理ip
	 */
	public static String PROXY_IP = "";
	/**
	 * wap的代理端口
	 */
	public static int PROXY_PORT = 0;

	/** ——————————————————————————————————————推送  常量值——————————————————————————————————————————————— **/

	/**
	 * viewpage 重新加载的间隔数
	 */
	public static int VIEW_PAGE_CACHE_INTERVAL = 2;

	/**
	 * 1分钟
	 * */
	public static int MINUTE_INTERVAL = 1 * 60 * 1000;

	/**
	 * 广告轮播默认8秒
	 * */
	public static int ROTATE_INTERVAL = 7 * 1000;

	/** ——————————————————————————————————————日志开关  常量值——————————————————————————————————————————————— **/
	/**
	 * 开发阶段
	 */
	public static final int DEVELOP = 0;
	/**
	 * 内部测试阶段
	 */
	public static final int DEBUG = 1;
	/**
	 * 公开测试
	 */
	public static final int BATE = 2;
	/**
	 * 正式版
	 */
	public static final int RELEASE = 3;
	/**
	 * Log 处于什么阶段
	 */
	public static int currentStage = RELEASE;

	public static boolean REQUEST_PARAMETER_LOG = false;//是否开启请求网络参数日志

	/** ——————————————————————————————————————handler what 常量值——————————————————————————————————————————————— **/

	public static final int HANDLER_WHAT_0 = 0;
	public static final int HANDLER_WHAT_1 = 1;
	public static final int HANDLER_WHAT_2 = 2;
	public static final int HANDLER_WHAT_3 = 3;
	public static final int HANDLER_WHAT_4 = 4;
	public static final int HANDLER_WHAT_5 = 5;
	public static final int HANDLER_WHAT_6 = 6;

	/** ——————————————————————————————————————listView 常量值——————————————————————————————————————————————— **/

	public static final String OPERATESTATUS_DOWNLOAD = "0";//下载
	public static final String OPERATESTATUS_DOWNLOAD_SUCCESS = "1";//下载完成
	public static final String OPERATESTATUS_INSTALL = "2";//安装
	public static final String OPERATESTATUS_REMOVE = "3";//卸载

	public static final String OPERATETYPE_APP = "1";//代表app
	public static final String OPERATETYPE_WALLPAPER = "2";//代表壁纸
	public static final String OPERATETYPE_E_BOOK = "3";//代表电子书
	public static final String OPERATETYPE_VIDEO = "4";//代表视频

	/** ——————————————————————————————————————listView 常量值——————————————————————————————————————————————— **/

	/**---------------------------------------————————用户登录状态————————------------------------------*/
	/* 0为没有登录的状态，1为登录的状态 */
	public static int logined = 0;

	/*注销状态*/
	public static boolean logState = false;

	/*用户名*/
	public static String UserSAccount;
	/*加密后的密码*/
	public static String MDFiveUserPwd;

	/** ——————————————————————————————————————其它 常量值——————————————————————————————————————————————— **/

	/*----------------------------系统信息-----------------------------------*/

	public static int osVersion = Build.VERSION.SDK_INT;

	//自己的应用
	public static boolean WIFI_AUTO_UPDATE = true;

	/*存储的用户手机号*/
	public static String rememberedUserPhone = "";

	/*存储的用户名*/
	public static String rememberedUserName = "";

	/*存储的用户头像*/
	public static String rememberedUserImage = "";
	
	/*存储的用户头像url*/
	public static String headImageUrl = "";

	/*存储的用户昵称*/
	public static String rememberedNickName = "";

	/*存储的加密后的密码*/
	public static String rememberedUserPwd = "";

	/*是否记住密码*/
	public static int isRemberPassword;

	/*是否显示密码*/
	public static int isShowAccountAndPassword = 0;

	/*登录时得到的用户ID，用来判断是否是登录状态*/
	public static String userId;

	/*修改密码中的加密后的原密码*/
	public static String beforeModifyPassword;

	/*修改密码中的加密后的新密码*/
	public static String newModifyPassword;

	/*--------------首页数据统计-------------------*/
	public static double totalsize;

	/*----------------------------设置信息-----------------------------------*/

	//测试用
	public static String APP_NAME = "biz.etao.lst";
//    易道通ID
	//public static String CHANNEL_CODE = "73C753CB-0549-43CA-8C4F-187685C24DAA";
//贝丰渠道ID
	//public static String CHANNEL_CODE = "28DE2BCF-F112-491E-A179-0F8EFE0969B8";
//    捷豹渠道ID
	//public static String CHANNEL_CODE = "E86729EE-5C29-478B-97E0-D526016D57E5";
//  江门工作室2
//	public static String CHANNEL_CODE = "C29E865B-0520-41FF-9EBA-B2F1D0C62638";
	//TODO 测试客户端更新用，后期删除
//	public static String CHANNEL_UPDATE_CODE = "2D2097A2-0AE8-4659-926A-C9642E9A2096";
	
	//易道应用市场01 
//	public static String CHANNEL_CODE = "C9A00442-B63E-4EEA-AF2F-87860F37A8E6";
	
	//易道应用市场02
//	public static String CHANNEL_CODE = "91F73F05-E86B-4DFD-B96E-B5791E77D5FA";
	
	//易道应用市场03
//	public static String CHANNEL_CODE = "E8A79C5D-D9B5-4A69-B86B-A9998231D3D7";
	
	//信太商店
//	public static String CHANNEL_CODE = "91F73F05-E86B-4DFD-B96E-B5791E77D5FA";
	//易道通2
//	public static String CHANNEL_CODE = "C9A00442-B63E-4EEA-AF2F-87860F37A8E6";
	
//分发渠道
	public static String CHANNEL_CODE = "951C218A-47AC-49C5-AFDA-B2C9323D043D";
	/***
	 * 搜索专栏
	 */
	public static String columId = "8AD73EBE-4FF8-4B4A-9CA2-41F328BB235B";
	//测试用
	public static String PACKAGE_VERSIONCODE = "253";

	public static String IMEI = "862482020047527";

	public static String MARKET_VERSION = "2.5.3";

	/*----------------------------设置信息-----------------------------------*/

	/**
	 * 安装状态
	 */
	// 未安装
	public static final int NOT_INSTALL = 1;

	// 已安装且为新版本
	public static final int INSTALLED_NEW = 4;

	// 已安装但为当前版本
	public static final int INSTALLED = 3;

	// 已安装但为旧版本
	public static final int OLD_VERSION = 2;

	public static final int GET_ERROR = 5;

	/**
	 * 热点分享 - 更新分享数量和分享列表
	 */
	public static final String HOTSPOT_UPDATE_SHARE_RECEIVER = "hotspot_update_share_receiver";

	/**
	 * 热点分享 - 传输文件[接收端]结束
	 */
	public static final String HOTSPOT_CLIENT_TRANSFER_FINISH = "hotspot_client_transfer_finish";

	/**
	 * 热点分享 - [HotspotShareApp activity oncreate]
	 */
	public static final String HOTSPOT_SHARE_APP_ON_CREATE = "hotspot_share_app_on_create";

	/**
	 * 热点分享 - [HotspotSharePhoto activity oncreate]
	 */
	public static final String HOTSPOT_SHARE_PHOTO_ON_CREATE = "hotspot_share_photo_on_create";

	/**
	 * 热点分享 - [HotspotShareAudio activity oncreate]
	 */
	public static final String HOTSPOT_SHARE_AUDIO_ON_CREATE = "hotspot_share_audio_on_create";

	/**
	 * 热点分享 - [HotspotShareVideo activity oncreate]
	 */
	public static final String HOTSPOT_SHARE_VIDEO_ON_CREATE = "hotspot_share_video_on_create";

	/**
	 * 消除 接收记录 广播
	 */
	public static final String CLEAR_RECEIVE_RECORD_ACTION = "clear_receive_record_action";

	/**
	 * 消除 分享记录 广播
	 */
	public static final String CLEAR_SEND_RECORD_ACTION = "clear_send_record_action";

	/**
	 * 取消 接收清除记录 广播
	 */
	public static final String CANCEL_RECEIVE_RECORD_ACTION = "cancel_receive_record_action";

	/**
	 * 取消 分享清除记录 广播
	 */
	public static final String CANCEL_SEND_RECORD_ACTION = "cancel_send_record_action";

	//========================================//

}
