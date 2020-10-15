package com.weiwei.base.common;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hwtx.dududh.BuildConfig;
import com.zte.functions.HttpManager;
import com.zte.functions.agent.GetAgentInfo;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalFuntion;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.db.provider.VsPhoneCallHistory;
import com.weiwei.base.http.VsHttpClient;
import com.weiwei.base.http.VsHttpsClient;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONObject;
import com.weiwei.netphone.data.process.CoreBusiness;

@SuppressLint("SimpleDateFormat")
public class VsBizUtil {
	public static String TAG = "VsBizUtil";
	public static VsBizUtil preference;

	public static VsBizUtil getInstance() {
		if (preference == null) {
			preference = new VsBizUtil();
		}
		return preference;
	}

	/**
	 * 直接登录
	 */
	public JSONObject Longin(String acount, String pwd, Context context) {
		String netType = VsUtil.getNetTypeString();
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("account", acount);
		treeMap.put("passwd", VsMd5.md5(pwd));
		treeMap.put("netmode", netType);
		treeMap.put("ptype", android.os.Build.MODEL);

		return VsHttpsClient.GetLoginHttp(context, treeMap);
	}

	// /**
	// * 直接注册
	// */
	// public void Register(final String mPhoneNumber, final Context mContext) {
	// BaseRunable newRunable = new BaseRunable() {
	// public void run() {
	// // 线程运行主体
	// try {
	// String deciceid = KcUtil.getMacAddress(mContext);
	// Hashtable<String, String> bizparams = new Hashtable<String, String>();
	// bizparams.put("phone", mPhoneNumber);
	// bizparams.put("device_id", deciceid);
	// bizparams.put("ptype", android.os.Build.MODEL);
	// JSONObject json = (JSONObject)
	// CoreBusiness.getInstance().httpAccess(mContext,
	// KcUserConfig.KC_ACTION_REGISTER, GlobalVariables.INTERFACE_REGISTER,
	// DfineAction.authType_Key, bizparams);
	// String retStr = json.getString("result");
	// String uid = json.getString("uid");
	// String pwd = KcRc4.decry_RC4(json.getString("password"),
	// DfineAction.passwad_key);
	// if ("0".equals(retStr)) {
	// // 登录
	// JSONObject jData = Longin(uid, pwd, mContext);
	// retStr = jData.getString("result");
	// if (retStr.equals("0")) {
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_KcId,
	// jData.getString("uid"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_Password, pwd);
	// if ("true".equals(jData.getString("check"))) {
	// KcUserConfig
	// .setData(mContext, KcUserConfig.JKey_PhoneNumber, jData.getString("mobile"));
	// } else {
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_PBPhoneNumber,
	// jData.getString("mobile"));
	// KcUserConfig.setData(mContext, KcUserConfig.JKey_BindPhoneNumberHint,
	// jData.getString("msg2"));
	// }
	// Intent intent2 = new Intent(KcUserConfig.KC_ACTION_AUTO_REGISTER_SUCCESS);
	// intent2.putExtra("packname", mContext.getPackageName());
	// mContext.sendBroadcast(intent2);
	// }
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// };
	// // 使用线程池进行管理
	// GlobalVariables.fixedThreadPool.execute(newRunable);
	// }

	/**
	 * 读取SD卡账号信息并登录
	 *
	 * @param mContext
	 */
	public void redSDCardInfo(final Context mContext) {
		BaseRunable newRunable = new BaseRunable() {
			public void run() { // 线程运行主体
				// 记录线程id
				GlobalFuntion.PrintValue("启动新的一个线程读取sd卡帐号密码", "");
				try {
					final String fileContent = VsUtil.readFromFile(mContext, DfineAction.brandid);
					if (fileContent != null && fileContent.length() > 0) {
						String filevsid = "账号:";
						String filepwd = "密码:";
						int startid = fileContent.indexOf(filevsid);
						int startpwd = fileContent.indexOf(filepwd);
						String vsId = fileContent.substring(startid + 3, startpwd - 1);// 要去掉账号冒号3字
						String pwd = fileContent.substring(startpwd + 3, fileContent.length() - 1);
						JSONObject jData = VsBizUtil.getInstance().Longin(vsId, pwd, mContext);
						String retStr = jData.getString("result");
						if (retStr.equals("0")) {
							VsUserConfig.setData(mContext, VsUserConfig.JKey_KcId, jData.getString("uid"));
							VsUserConfig.setData(mContext, VsUserConfig.JKey_Password, pwd);
							Intent intent2 = new Intent(VsUserConfig.VS_ACTION_AUTO_REGISTER_SUCCESS);
							intent2.putExtra("packname", mContext.getPackageName());
							mContext.sendBroadcast(intent2);
							VsUserConfig.setData(mContext, VsUserConfig.JKey_PhoneNumber, jData.getString("mobile"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		// 使用线程池进行管理
		GlobalVariables.fixedThreadPool.execute(newRunable);
	}

	/**
	 * 模版配置，有帐号才请求
	 *
	 * @param mContext
	 */
	public void templateConfig(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_APPSERVER_TEMPLATE_CONFIG_FLAG));
		treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		treeMap.put("pwd", VsMd5.md5(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_Password)));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_TPL, DfineAction.authType_UID,
				treeMap, GlobalVariables.actionTempLateConfig);
	}

	/**
	 * 静态配置
	 *
	 * @param mContext
	 */
	public void defaultConfig(Context mContext) {
		CoreBusiness.getInstance().startTestPointThread(mContext);
	}

	/**
	 * 请求广告位接口 ，有帐号才请求
	 *
	 * @param mContext
	 */
	/*
	 * public void adConfig(Context mContext) { Hashtable<String, String> params =
	 * new Hashtable<String, String>(); params.put("flag",
	 * KcUserConfig.getDataString(mContext,
	 * KcUserConfig.JKEY_APPSERVER_AD_CONFIG_FLAG));
	 * CoreBusiness.getInstance().startThread(mContext,
	 * GlobalVariables.INTERFACE_ADCONFIG, DfineAction.authType_AUTO, params,
	 * GlobalVariables.actionADConfig); }
	 */

	/**
	 * 充值套餐请求，有帐号、无帐号都要请求
	 *
	 * @param mContext
	 */
	public void goodsConfig(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		// treeMap.put("flag", VsUserConfig.getDataString(mContext,
		// VsUserConfig.JKEY_APPSERVER_GOODS_CONFIG_FLAG));
		treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_GOODSCONFIG,
				DfineAction.authType_AUTO, treeMap, GlobalVariables.actionGoodsConfig);
	}

	/**
	 *
	 */
	public void contactbackinfo(Context mContext) {

		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_CONTACTINFO,
				DfineAction.authType_AUTO, null, GlobalVariables.VS_ACTION_CHECK_CONTACTS);
	}

	/**
	 * 是否是移动手机
	 *
	 * @return
	 */
	public String mobileType(Context context) {
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		String mobileType = "";
		if (operator != null) {
			if (operator.equals("46001") || operator.equals("46006")) {
				mobileType = "cu";
				return mobileType;
			} else if (operator.equals("46003") || operator.equals("46005")) {
				mobileType = "ct";
				return mobileType;
			} else {
				mobileType = "cmcc";
				return mobileType;
			}
		}
		return mobileType;
	}

	/**
	 * 获取错误日志上报标准
	 */
	public void reportControl(Context mContext) {
		String report_day = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_GET_ERROR_LOG);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		// 首次或者保存的时间不一致的时候上报活跃
		if (report_day.length() == 0 || (!report_day.equals(date))) {
			report_day = date;
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("bid", DfineAction.brandid);
			params.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
			params.put("pv", DfineAction.pv);
			params.put("v", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V));
			// params.put("sipv", SoftManager.getInstance().sm_getVersion());

			CoreBusiness.getInstance().startOtherHttpThread(mContext, "http://omp.weiwei.com/reportcontrol.action",
					CoreBusiness.getInstance().enmurParse(params), GlobalVariables.actionReportControl);
		}
	}

	/**
	 * 解决布局中ScrollView与ListView的冲突
	 *
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 上报错误日志
	 */
	@SuppressWarnings("deprecation")
	public void reportList(Context mContext, String errorContent, String errorType) {
		if (VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGFLAG).equals("0")
				&& (errorContent.getBytes().length <= Integer
						.parseInt(VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGSIZE))
						|| VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGSIZE).equals("0"))) {
		} else {
			return;
		}
		try {
			JSONObject contentJson = new JSONObject();
			contentJson.put("bid", DfineAction.brandid);
			contentJson.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
			contentJson.put("pv", "android");
			contentJson.put("v", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V));
			// contentJson.put("sipv", SoftManager.getInstance().sm_getVersion());
			StringBuffer sbf = new StringBuffer();
			sbf.append(DfineAction.brandid);
			sbf.append("_");
			sbf.append(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V));
			sbf.append("_");
			sbf.append(DfineAction.pv);
			sbf.append("_");
			sbf.append(mContext.getPackageName());

			JSONArray json = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("src", sbf.toString());
			obj.put("category", errorType);
			obj.put("summary", Build.VERSION.RELEASE);
			obj.put("detail", URLEncoder.encode(errorContent));
			json.put(obj);
			CustomLog.i("GDK", json.toString());
			// listBuffer.delete(listBuffer.length() - 1, listBuffer.length());
			// listBuffer.insert(0, "[").append("]");

			StringBuffer bodyBuffer = new StringBuffer();
			bodyBuffer.append("&content=").append(contentJson.toString()).append("&list=")
					.append(URLEncoder.encode(json.toString()));
			CoreBusiness.getInstance().startOtherHttpThread(mContext, "http://omp.weiwei.com/reportlist.action",
					bodyBuffer.toString(), GlobalVariables.actionReportList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 添加通话记录
	 */
	public void addCallLog(Context mContext, VsCallLogItem vsCallLogItem) {
		Uri contacturl = Uri
				.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
		ContentValues cv = new ContentValues();
		CustomLog.i(TAG, "vsCallLogItem.callName=" + vsCallLogItem.callName);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_TIME_STAMP, vsCallLogItem.calltimestamp);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_NAME, vsCallLogItem.callName);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_NUMBER, vsCallLogItem.callNumber);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_LOCAL, vsCallLogItem.local);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH, vsCallLogItem.calltimelength);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_MONEY, vsCallLogItem.callmoney);
		cv.put(VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE, vsCallLogItem.ctype);
		cv.put(VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL, vsCallLogItem.directCall);
		mContext.getContentResolver().insert(contacturl, cv);
		VsCallLogListItem existListItem = VsPhoneCallHistory.isInMyLogList(vsCallLogItem.callNumber);
		// 如果存在号码的通话记录。 就添加到已经存在的ListVIEW
		if (existListItem != null) {
			existListItem.getChilds().add(vsCallLogItem);
			Collections.sort(existListItem.getChilds(), new Comparator<VsCallLogItem>() {

				@Override
				public int compare(VsCallLogItem object1, VsCallLogItem object2) {
					if (object1.calltimestamp > object2.calltimestamp) {
						return -1;
					}
					if (object1.calltimestamp < object2.calltimestamp) {
						return 1;
					}
					return 0;
				}
			});
		} else {
			VsCallLogListItem callLogListItem = new VsCallLogListItem();
			callLogListItem.getChilds().add(vsCallLogItem);
			callLogListItem.setLocalName(VsLocalNameFinder.findLocalName(vsCallLogItem.callNumber, false, mContext));
			VsPhoneCallHistory.callLogs.add(callLogListItem);
		}
		Intent intent = new Intent();
		intent.setAction(DfineAction.ACTION_SHOW_CALLLOG);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 上传错误日志方法
	 *
	 * @param errorContent
	 */
	@SuppressWarnings("deprecation")
	public void ReportConfig(String errorContent, String errorType, Context mContext) {
		try {
			if (VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGFLAG).equals("0")
					&& (errorContent.getBytes().length <= Integer
							.parseInt(VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGSIZE))
							|| VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_REPORT_CONFIGSIZE).equals("0"))) {
			} else {
				return;
			}
			JSONObject contentJson = new JSONObject();
			contentJson.put("bid", DfineAction.brandid);
			contentJson.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
			contentJson.put("pv", "android");
			contentJson.put("v", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V));
			// contentJson.put("sipv",
			// SoftManager.getInstance().sm_getVersion());-----------待修改
			StringBuffer sbf = new StringBuffer();
			sbf.append(DfineAction.brandid);
			sbf.append("_");
			sbf.append(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_V));
			sbf.append("_");
			sbf.append(DfineAction.pv);
			sbf.append("_");
			sbf.append(mContext.getPackageName());

			JSONArray json = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("src", sbf.toString());
			obj.put("category", errorType);
			obj.put("summary", Build.VERSION.RELEASE);
			obj.put("detail", URLEncoder.encode(errorContent));
			json.put(obj);
			CustomLog.i("GDK", json.toString());
			// listBuffer.delete(listBuffer.length() - 1, listBuffer.length());
			// listBuffer.insert(0, "[").append("]");

			StringBuffer bodyBuffer = new StringBuffer();
			bodyBuffer.append("&content=").append(contentJson.toString()).append("&list=")
					.append(URLEncoder.encode(json.toString()));
			ReportConfig(bodyBuffer.toString(), mContext);
			//
			// StringBuffer bodyBuffer = new StringBuffer();
			// bodyBuffer.append("&content=").append(contentJson.toString()).append("&list=")
			// .append(listBuffer.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 错误日志上报
	 */
	public void ReportConfig(final String paramStr, final Context context) {

		BaseRunable newRunable = new BaseRunable() {
			@SuppressWarnings("deprecation")
			public void run() { // 线程运行主体
				String RealUrl = "http://omp.weiwei.com/reportlist.action";
				try {
					CustomLog.i("DGK", "ServiceConfigUrl=" + RealUrl + "?" + paramStr);
					byte[] jsonData = paramStr.getBytes("utf-8");
					String gProxyHost = android.net.Proxy.getDefaultHost();
					int gProxyPort = android.net.Proxy.getDefaultPort();
					VsHttpClient httpTools = new VsHttpClient(RealUrl);
					httpTools.setProxyHost(gProxyHost);
					httpTools.setProxyPort(gProxyPort);
					if (VsUtil.isWifi(context)) {
						httpTools.setProxyHost(null);
						httpTools.setProxyPort(-1);
					}
					String returnStr = httpTools.excutePost(jsonData);
					CustomLog.i("DGK", "ServiceConfigReturn=" + returnStr);
				} catch (Exception e) {
				}
			}
		};
		// 使用线程池进行管理
		GlobalVariables.fixedThreadPool.execute(newRunable);
	}

	/**
	 * 拉取系统公告
	 *
	 * @param mContext
	 */
	public void getSysMsg(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		treeMap.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_APPSERVER_SYSMSG__FLAG));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_SYSMSG, DfineAction.authType_AUTO,
				treeMap, GlobalVariables.actionSysMsg);
	}

	/**
	 * 上传广告位 统计
	 *
	 * @param mContext
	 */
	public static void PostADCountAction(Context mContext, String adpid, String adid) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("adpid", adpid);
		treeMap.put("adid", adid);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_STATISTIC_AD_UPLOAD,
				DfineAction.authType_AUTO, treeMap, GlobalVariables.adActionCount);
	}

	/**
	 * 获取token
	 */
	public void getToken(final Context mContext) {
		try {
			GlobalVariables.RE_CONNECT_FLAG = false;// 重连标识
			CustomLog.i("BaseCoreService", "gettoken--ruing");
			// 读取数据库保存的历史记录
			BaseRunable newRunable = new BaseRunable() {
				public void run() { // 线程运行主体
					String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
					if (uid != null && !"".equals(uid)) {// 判断是否有uid
						String token = VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_TOKEN);
						if (token == null || "".equals(token)) {
							CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_GET_TOKEN,
									DfineAction.authType_UID, null, GlobalVariables.actionGetToken);
						} else {// 直接连接服务
							// mContext.sendBroadcast(new
							// Intent(UIDfineAction.ACTION_LOGIN).putExtra("sid_pwd", token));
						}
					}
				}
			};
			// 使用线程池进行管理
			GlobalVariables.fixedThreadPool.execute(newRunable);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 获取融云token
	 *
	 * @param mContext
	 * @param mode_code
	 *            属于正常请求获取token，还是异常失效后重新获取token 0正常 1异常
	 * @instruction
	 * @author 黄发兴
	 * @version 创建时间：2014-12-15 下午07:07:26
	 */
	// public void getRYToken(final Context mContext, final String mode_code) {
	// CustomLog.i("ConnectionIm", "actionGetTokenYongYun gettoken--ruing");
	// ConnectionIm.getInstance().rongcloudConnection(mContext);
	// }

	/**
	 * 获取好友关系
	 *
	 * @param mContext
	 */
	public void getVsFriend(final Context mContext) {
		if (!VsPhoneCallHistory.isloadContact && VsPhoneCallHistory.CONTACTLIST != null
				&& VsPhoneCallHistory.CONTACTLIST.size() > 0) {
			StringBuffer buf = new StringBuffer();
			Hashtable<String, String> bizparams = new Hashtable<String, String>();
			for (VsContactItem item : VsPhoneCallHistory.CONTACTLIST) {
				if (item.phoneNumList != null && item.phoneNumList.size() > 0) {
					for (String num : item.phoneNumList) {
						if (VsUtil.checkMobilePhone(num)) {
							num = VsUtil.filterPhoneNumber(num);
							buf.append(num + ",");
						}
					}
				}
			}
			if (buf != null && buf.length() > 0) {
				/*
				 * if (isRongyuan) { bizparams.put("ryflag", "1"); }
				 */
				String muber = buf.toString().substring(0, buf.length() - 1);
				// VsRc4.encry_RC4_byte(muber, DfineAction.passwad_key);
				bizparams.put("ryflag", "1");// 融云 兼容老接口
				bizparams.put("saveflag", "1");// 360报毒问题，兼容老接口
				bizparams.put("numbers", VsRc4.encry_RC4_string(muber, DfineAction.passwad_key));
				// bizparams.put("numbers", muber);
				bizparams.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_GETVSUSERINFO_FLAG));
				// CoreBusiness.getInstance().startThread(mContext,
				// GlobalVariables.INTERFACE_GET_VSFRIEND, DfineAction.authType_AUTO, bizparams,
				// GlobalVariables.actionGetVsFriend);
			}
		}
	}

	/**
	 * 获取app静态配置
	 */
	public void getAppInfo(Context mContext) {
		// 请求静态配置信息
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_APPSERVER_DEFAULT_CONFIG_FLAG));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_CONFIG, DfineAction.authType_AUTO,
				treeMap, GlobalVariables.actionDefaultConfig);
	}

	/**
	 * fir升级
	 */
	public void getFirUpDate(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("api_token", DfineAction.FIR_API_TOKEN);
		treeMap.put("type", "android");
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.FIR_UPDATE, DfineAction.authType_AUTO, treeMap,
				GlobalVariables.actionFirUpdate);
	}

	/**
	 * 广告
	 * 
	 * @param mContext
	 */
	public void getAdInfos(Context mContext, String uid) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("uid", uid);
		treeMap.put("flag", VsUserConfig.getDataString(mContext, VsUserConfig.JKEY_APPSERVER_AD_CONFIG_FLAG));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_AD, DfineAction.authType_AUTO,
				treeMap, GlobalVariables.actionAdsConfig);
	}

	// 拨打前的鉴权请求
	public void getOK(Context mContext, String caller, String callee, String calltype, String uid) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("uid", uid);
		treeMap.put("caller", caller);
		treeMap.put("callee", callee);
		treeMap.put("call_type", calltype);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_GetOK, DfineAction.authType_AUTO,
				treeMap, GlobalVariables.actionGetOK);
	}

	public void getShareContent(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.INTERFACE_SHARE, DfineAction.authType_AUTO,
				treeMap, GlobalVariables.actionShareConfig);
	}

	/**
	 * 拉取个人信息
	 * 
	 */
	public void getMyInfoText(Context mContext) {
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		String uid = VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId);
		treeMap.put("uid", uid);
		CoreBusiness.getInstance().startThread(mContext, GlobalVariables.GetMyInfo, DfineAction.authType_AUTO, treeMap,
				GlobalVariables.actionGetmyInfo);

	}

	/**
	 * 拉取企业号
	 *
	 * @param context
	 */
	public void getAgentInfo(Context context, Handler handler) {
		String uid = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
		GetAgentInfo info = new GetAgentInfo(handler, DfineAction.brandid, uid);
		// HttpManager.getInstance().getAgentInfo(context,info);
	}

	/* public */ String getAgwParams(Context context, String authType, String dataParamas) {
		String vsid = VsUserConfig.getDataString(context, VsUserConfig.JKey_KcId);
		String pwd = VsMd5.md5(VsUserConfig.getDataString(context, VsUserConfig.JKey_Password));
		if (null == authType) {
			authType = "";
		}
		if (authType.equals("auto") && vsid.length() > 0 && pwd.length() > 0) {
			authType = DfineAction.authType_UID;
		} else if (authType.equals("auto")) {
			authType = DfineAction.authType_Key;
		}
		Long curTime = System.currentTimeMillis() / 1000;

		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.put("uid", vsid);
		treeMap.put("pv", DfineAction.pv);
		treeMap.put("v", BuildConfig.VERSION_NAME);
		treeMap.put("ts", curTime.toString());
		treeMap.put("invitedby", VsUserConfig.getDataString(context, VsUserConfig.JKEY_INVITEDBY, DfineAction.invite));
		treeMap.put("invitedway", VsUserConfig.getDataString(context, VsUserConfig.JKEY_INVITEDWAY, "ad"));
		treeMap.put("auth_type", authType);
		treeMap.put("nonce", VsMd5.md5(UUID.randomUUID().toString()).substring(8, 24));
		treeMap.put("data", dataParamas);
		String sign = "";
		if (authType.equals(DfineAction.authType_UID)) {
			// sign = getSign(treeMap, pwd, context);
		} else {
			// sign = getSign(treeMap, "", context);
		}
		treeMap.put("sign", sign);
		return null;
	}
}
