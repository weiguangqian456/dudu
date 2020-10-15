package com.weiwei.base.dataprovider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Resource {
	public static String needshownotices = "yes"; // 控制是否弹出系统公告
	public static String PREFS_NAME_FAVOURABLE_INFO = "PREFS_FAVOURABLE_INFO"; // 优惠活动

	public static String PREFS_NAME_PAY_INFO = "PREFS_PAY_INFO"; // 充值说明
	public static String PREFS_SUB_TITLE = "prefs_sub_title";// 充值界面副标题（截止时间）
	public static String order_list = "";// 保存临时订单
	public static String uid_list = "";// 保存临时账号
	public static String uid_pwd = "";// 保存临时密码
	// 查询订单
	public static int showOrderNum = 0; // 如果充值填出提示了。弹出公告就不在提示
	public static String legacyOrderId = "";// 保存所有不确定状态的订单号。下次启动在询问
	public static String legacyUid = "";// 保存所有不确定状态的卡号。下次启动在询问得到结果在给予提示
	public static String legacyPwd = "";// 保存所有不确定状态的密码。下次启动在询问得到结果在给予提示 此3个一一对应
	public static String return_UID_true = "";// 订单成功的卡号ID
	public static String return_PWD_true = "";// 订单成功的密码ID
	public static String return_UID_false = "";// 订单失败的卡号ID
	public static String return_PWD_false = "";// 订单失败的密码ID
	public static String return_UID_true_string = "";// 订单成功的卡号ID的文字
	public static String return_PWD_true_string = "";// 订单成功的密码ID的文字
	public static String return_UID_false_string = "";// 订单失败的卡号ID的文字
	public static String return_PWD_false_string = "";// 订单失败的密码ID的文字
	public static String dialPhoneNumber = ""; // 来电号码
	// public static ArrayList<KcContactItem> CONTACTIONFOLIST = new ArrayList<KcContactItem>();// 拨号盘联系人搜索显示数据
	public static String CREATE_CONTACT = "新建联系人";
	public static String ADD_CURRENT_CONTACT = "添加到已有联系人";
	public static int phonecalltype = 0;
	public static String moRegister = "no"; // mo自动发短信控制
	public static String relesase = "";// 系统版本
	public static boolean is3Gwap = false; // 是否是3G cmwap连接

	public static JSONArray allJsonArrayObject = new JSONArray();

	public static void appendJsonAction(int a, long startTime) {
		try {
			JSONObject object = new JSONObject();
			object.put("A", a);
			object.put("T", startTime);
			allJsonArrayObject.put(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public final static int activity_2001 = 2001; // 充值送手机
	public final static int activity_2000 = 2000; // 充值
	public final static int activity_3000 = 3000; // 玩赚
	public final static int activity_3003 = 3003; // 邀请好友
	public final static int activity_redpacket = 3034; // 红包分享
	public final static int activity_invite = 3002; // 邀请
	public final static int activity_jz = 3004; // 极智
	public final static int activity_ugame = 3035; // 手游
	public final static int activity_limei = 3036; // 力美
	public final static int activity_huodong = 4002; // 活动资讯 add张振威
	public final static int activity_ug_box = 4003; // 游戏盒子 add张振威

	public final static String activity_action_001 = "001"; // 事件id
	public final static String activity_action_002 = "002"; // 事件id
	public final static String activity_action_003 = "003"; // 事件id
	public final static String activity_action_004 = "004"; // 事件id
	public final static String activity_action_005 = "005"; // 事件id
	public final static String activity_action_006 = "006"; // 事件id
	public final static String activity_action_007 = "007"; // 事件id

}
