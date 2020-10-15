package com.weiwei.base.db.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.weiwei.base.application.VsApplication;
import com.weiwei.base.common.BaseRunable;
import com.weiwei.base.common.ConverToPingying;
import com.weiwei.base.common.CustomLog;
import com.weiwei.base.common.VsBizUtil;
import com.weiwei.base.common.VsLocalNameFinder;
import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.dataprovider.GlobalVariables;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.base.item.VsCallLogItem;
import com.weiwei.base.item.VsCallLogListItem;
import com.weiwei.base.item.VsContactItem;
import com.weiwei.json.me.JSONArray;
import com.weiwei.json.me.JSONObject;

public class VsPhoneCallHistory {
	public final static String TAG = "KcPhoneCallHistory";
	public final static String TABLE_NAME_PHONECALL_HISTORY = "phonecallhistory";
	public final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
	public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
	public static final String MIME_ITEM = "vnd.skype.callhistory";

	// 表示单一记录
	public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;

	// 所有记录
	public static final String MEME_TYPE_MULTIPLE = "MIME_DIR_PREFIX" + "/" + MIME_ITEM;

	// 授权者
	// public static final String AUTHORITY =
	// KcUserConfig.getDataString(context, key);

	public static final String PATH_SINGLE = "phonecallhistory/#";
	public static final String PATH_MULTIPLE = "phonecallhistory";

	// public static final String STR = "content://" + AUTHORITY + "/" +
	// PATH_MULTIPLE;
	// public static final Uri CONTENT_URI = Uri.parse(STR);

	// 表字段名
	public static final String ID = "_id"; // 唯一标识主键
	public static final String PHONECALLHISTORY_NAME = "callname";// 名称
	public static final String PHONECALLHISTORY_NUMBER = "callnumber";// 电话号码　
	public static final String PHOTOSHOPHISTORY_CALL_TYPE = "calltype"; // 呼叫类型(来电，去电，未接来电)
	public static final String PHONECALLHISTORY_TIME_STAMP = "calltimestamp"; // 时间
	public static final String PHONECALLHISTORY_TIME_LENGTH = "calltimelength";// 通话时长
	public static final String PHONECALLHISTORY_MONEY = "callmoney";// 通话费用
	public static final String PHONECALLHISTORY_LOCAL = "calllocal";// 归属地
	public static final String PHONECALLHISTORY_DIRECTCALL = "directCall";// 是否直拨
	// 没有boolean类型。所以用0表示直播
	// 2表示回拨，3是免费

	/**
	 * 所有的通话记录
	 */
	// public static ArrayList<KcCallLogListItem> callLogs = new ArrayList<KcCallLogListItem>();
	public static List<VsCallLogListItem> callLogs = (List<VsCallLogListItem>) Collections
			.synchronizedList(new ArrayList<VsCallLogListItem>(200));
	/**
	 * 显示在通话记录列表中的数据
	 */
	// public static ArrayList<KcCallLogListItem> callLogViewList = new ArrayList<KcCallLogListItem>();
	public static List<VsCallLogListItem> callLogViewList = (List<VsCallLogListItem>) Collections
			.synchronizedList(new ArrayList<VsCallLogListItem>(200));

	// public static ArrayList<KcContactItem> CONTACTIONFOLIST = new ArrayList<KcContactItem>();// 拨号盘联系人搜索显示数据
	public static List<VsContactItem> CONTACTIONFOLIST = (List<VsContactItem>) Collections
			.synchronizedList(new ArrayList<VsContactItem>(200));

	/**
	 * 所有的联系人
	 */
	// public static ArrayList<KcContactItem> CONTACTLIST = new ArrayList<KcContactItem>(500);
	public static List<VsContactItem> CONTACTLIST = (List<VsContactItem>) Collections
			.synchronizedList(new ArrayList<VsContactItem>(200));
	/**
	 * VS好友的联系人
	 */
	// public static ArrayList<KcContactItem> KCCONTACTLIST = new ArrayList<KcContactItem>(500);
	public static List<VsContactItem> VSCONTACTLIST = (List<VsContactItem>) Collections
			.synchronizedList(new ArrayList<VsContactItem>(100));
	/**
	 * IM好友的联系人
	 */
	public static List<VsContactItem> IMCONTACTLIST = (List<VsContactItem>) Collections
			.synchronizedList(new ArrayList<VsContactItem>(100));

	// /**
	// * 常用联系人
	// */
	// public static ArrayList<KcContactItem> COMMON_CONTACTLIST = new ArrayList<KcContactItem>(); // 常用联系人

	/**
	 * 加载通话记录
	 * <p>1, 同步系统通话记录数据库最新数据到App本地数据库</p>
	 * <p>2, 确保{@link #callLogs}有最新数据</p>
	 * <p>3, 确保{@link #callLogs}的归属地信息有最新数据</p>
	 */
	public static void loadCallLog() {
		try {
			// 初始化通讯录历史记录
			BaseRunable newRunable = new BaseRunable() {
				public void run() {
					// 1, 更新系统通话记录到App通话记录表中
					boolean res = loadSysCallLogData();
					if (res || callLogs.size() == 0) {
						// 2, 更新通话记录列表
						callLogs.clear();
						Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
						loadCallLogData(VsApplication.getContext(), contacturl);
						// 3, 更新通话记录中号码的归属地
						loadSysCallLogLocal(VsApplication.getContext());
					}

					String mobilPhone = Build.MODEL;
					if (mobilPhone.startsWith("meizu") || mobilPhone.startsWith("M9") || mobilPhone.startsWith("MX") || mobilPhone.startsWith("M3") || mobilPhone.startsWith("M4")) {
					}
				}
			};
			// 使用线程池进行管理
			GlobalVariables.fixedThreadPool.execute(newRunable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载联系人
	 */
	public static void loadContacts() {
		try {
			// 读取数据库保存的历史记录
			BaseRunable newRunable = new BaseRunable() {
				public void run() { // 线程运行主体
					loadContactsAndLocal(VsApplication.getContext());
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
	 * 读取系统通话记录插入到自己的通话记录表中，
	 * <p> 更新系统通话记录到App通话记录表中</p>
	 * <p> 可执行多次</p>
	 *
	 * @return true 加载成功，有新数据加载， false 更新失败，无数据更新
	 */
	private static synchronized boolean loadSysCallLogData() {
		Uri contacturl = Uri
				.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
		Cursor mCursor = null;
		boolean result = false;
		try {
			ContentResolver resolver = VsApplication.getContext().getContentResolver();
			long lastTime =VsUserConfig.getDataLong(VsApplication.getContext(), VsUserConfig.JKey_LoadedSysCallLogLastTime);
			String selection;
			String sortOrder;

			if (0 == lastTime) {
				selection = null;
				sortOrder = CallLog.Calls.DATE + " desc limit 50";
			} else {
				selection = CallLog.Calls.DATE + " > '" + lastTime	+ "'";
				sortOrder = CallLog.Calls.DATE + " desc ";
			}

			mCursor = VsApplication.getContext().getContentResolver()
					.query(CallLog.Calls.CONTENT_URI, null, selection, null, sortOrder);
			VsCallLogItem mVsCallLogItem;
			ContentValues cv = new ContentValues();
			if (null != mCursor && mCursor.moveToFirst()) {
				int i = 0;
				int colName = mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
				int colDate = mCursor.getColumnIndex(CallLog.Calls.DATE);
				int colType = mCursor.getColumnIndex(CallLog.Calls.TYPE);
				int colNumber = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
				int colDuration = mCursor.getColumnIndex(CallLog.Calls.DURATION);
				result = mCursor.getCount() > 0;
				do {
					String callName = mCursor.getString(colName);// 姓名
					long  date = mCursor.getLong(colDate);// 呼叫时间点
					int type = mCursor.getInt(colType);// 电话类型(来电，拨打对方，未接电话)
					String cType = String.valueOf(type);
					String callNum = mCursor.getString(colNumber);// 电话号码
					long callDuration = mCursor.getLong(colDuration);// 电话号码
					if (i == 0) {
						VsUserConfig.setData(VsApplication.getContext(), VsUserConfig.JKey_LoadedSysCallLogLastTime, date);
						Log.i(TAG, "loadSysCallLogData(), i == 0 and callTimeL = " + date);
						i++;
					}

					if (callNum != null && callNum.length() > 0) {
						callNum = callNum.replaceAll("[^0-9]", "").replaceAll("-", "").replaceAll(" ", "");
					}
					if (callNum == null || callNum.length() < 3)
						continue;

					mVsCallLogItem = new VsCallLogItem();
					mVsCallLogItem.callNumber = callNum;
					mVsCallLogItem.callName = (TextUtils.isEmpty(callName) ? callNum: callName);
					mVsCallLogItem.calltimestamp = date;
					mVsCallLogItem.calltimelength = VsUtil.getFormatDuration(callDuration);
					mVsCallLogItem.callmoney = "22";//TODO 
					mVsCallLogItem.ctype = cType;
					mVsCallLogItem.directCall = 0;//系统通话记录默认为直播

					// 当没有读到系统联系人名称时获取联系人名称
					if (mVsCallLogItem.callNumber.equals(mVsCallLogItem.callName) && !isloadContact) {
						String callname = VsUtil.getContactsName(mVsCallLogItem.callNumber);
						if (callname != null) {
							mVsCallLogItem.callName = callname;
						}
					}
					// 获取V标
					if (mVsCallLogItem.callNumber != null && mVsCallLogItem.callNumber.length() > 10
							&& VsUtil.checheNumberIsVsUser(VsApplication.getContext(), mVsCallLogItem.callNumber)) {
						mVsCallLogItem.isVs = true;
					}
					// TODO 添加通话记录到我们自己的数据库中 添加完之后该对象变化不会印象数据看变化。所以创建对象在for循环外面
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_TIME_STAMP, mVsCallLogItem.calltimestamp);// 通话时间
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_NAME, mVsCallLogItem.callName);// 通话名称
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_NUMBER, mVsCallLogItem.callNumber);// 呼叫号码
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH, mVsCallLogItem.calltimelength);// 通话时长
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_MONEY, mVsCallLogItem.callmoney);// 通话费用
					cv.put(VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE, mVsCallLogItem.ctype);// 通话类型
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_LOCAL, mVsCallLogItem.local);// 归属地
					cv.put(VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL, mVsCallLogItem.directCall);// 是否直拨0是，回拨2,免费3
					resolver.insert(contacturl, cv);
				} while (mCursor.moveToNext());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
		return result;
	}

	/**
	 * 读取通话记录数据
	 */
	public static void loadCallLogData(Context context, Uri uri) {
		if (context == null) {
			return;
		}
		Cursor mCursor = null;
		try {
			mCursor = context.getContentResolver().query(uri, null, null, null, "calltimestamp desc");
			if (mCursor == null) {
				return;
			}
			Integer num = mCursor.getCount();
			mCursor.moveToFirst();
			VsCallLogItem vsCallLogItem;
			while (mCursor.getPosition() != num) {
				// calldistnum
				vsCallLogItem = new VsCallLogItem();
				vsCallLogItem.callName = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_NAME));
				vsCallLogItem.callNumber = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_NUMBER));
				vsCallLogItem.calltimelength = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH));
				vsCallLogItem.callmoney = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_MONEY));
				vsCallLogItem.calltimestamp = Long.parseLong(mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_TIME_STAMP)));
				vsCallLogItem.ctype = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE));
				vsCallLogItem.local = mCursor.getString(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_LOCAL));
				vsCallLogItem.directCall = mCursor.getInt(mCursor
						.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL));

				if (vsCallLogItem.callNumber != null && vsCallLogItem.callNumber.length() > 10
						&& VsUtil.checheNumberIsVsUser(context, vsCallLogItem.callNumber)) {
					vsCallLogItem.isVs = true;
				}
				VsCallLogListItem existListItem = isInMyLogList(vsCallLogItem.callNumber);
				// 如果存在号码的通话记录。 就添加到已经存在的ListVIEW
				if (existListItem != null) {
					existListItem.getChilds().add(vsCallLogItem);
					existListItem.getMissChilds().add(vsCallLogItem);
				} else {
					VsCallLogListItem callLogListItem = new VsCallLogListItem();
					callLogListItem.getChilds().add(vsCallLogItem);
					callLogListItem.getMissChilds().add(vsCallLogItem);
					callLogs.add(callLogListItem);
				}
				mCursor.moveToNext();
			}
			// 发送加载通话记录成功广播
			context.sendBroadcast(new Intent(GlobalVariables.action_loadcalllog_succ));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
		}
	}

	/**
	 * 联系人名称修改后同时修改联系人名称
	 *
	 * @param context
	 * @param name
	 */
	public static void updateCallLog(final Context context, final String name, final ArrayList<String> phone) {
		BaseRunable newRunable = new BaseRunable() {
			public void run() { // 线程运行主体
				Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/"
						+ VsPhoneCallHistory.PATH_MULTIPLE);
				if (context == null) {
					return;
				}
				try {
					ContentValues values = new ContentValues();
					values.put(VsPhoneCallHistory.PHONECALLHISTORY_NAME, name);
					for (int i = 0; i < phone.size(); i++) {
						context.getContentResolver().update(contacturl, values,
								VsPhoneCallHistory.PHONECALLHISTORY_NUMBER + "=?", new String[] { phone.get(i) });
					}
					// 发送更新联系人广播
					context.sendBroadcast(new Intent(DfineAction.ACTION_UPDATE_CALLLOG));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		// 使用线程池进行管理
		GlobalVariables.fixedThreadPool.execute(newRunable);
	}

	/**
	 * 加载系统归属地
	 *
	 * @param context
	 */
	public static void loadSysCallLogLocal(Context context) {
		if (callLogs != null && callLogs.size() > 0) {
			for (int i = 0; i < callLogs.size(); i++) {
				if (callLogs.get(i).getLocalName() == null || callLogs.get(i).getLocalName().length() == 0) {
					String localName = VsLocalNameFinder.findLocalName(callLogs.get(i).getFirst().callNumber, false, context);
					//可能会数组下标越界 也许是其他线程有改动 额外做一个判断
					if(callLogs.size() >= (i + 1) && callLogs.get(i) != null){
						callLogs.get(i).setLocalName(localName);
					}
				}
			}
		}
		context.sendBroadcast(new Intent(DfineAction.ACTION_SHOW_CALLLOG));
	}

	/**
	 * 是否存在号码的的通话记录
	 *
	 * @param phoneNum
	 * @return
	 */
	public static VsCallLogListItem isInMyLogList(String phoneNum) {
		for (VsCallLogListItem listItem : callLogs) {
			if (listItem.getFirst() != null && listItem.getFirst().callNumber.equals(phoneNum)) {
				return listItem;
			}
		}
		return null;
	}

	/**
	 * 按条件删除多个通话记录
	 *
	 * @param context
	 * @param
	 */
	public static void delCallLogBYtype(Context context, String number, String type) {
		Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
		if (context == null) {
			return;
		}
        /*    // 删除列表
           for (int i = 0; i < callLogs.size(); i++) {
                if (callLogs.get(i).getMissFirst().ctype.equals(type)) {
                    callLogs.remove(i);
                }
            }*/
		// VsCallLogManagement.delectMistcallcalllogs(number);

		// 删除数据库中单个通话记录
		// CursorHelper.deleteByHistroy(context, phonenum);
		if (VsUtil.isNull(number)) {//全删除
			CustomLog.i("callLogListItem", "全删除");
			context.getContentResolver().delete(contacturl, VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE + "=?", new String[] { type });

			context.getContentResolver().delete(contacturl, VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH + "=?", new String[] { "1", "3", "00分00秒" });
		}
		else {
			CustomLog.i("callLogListItem", "dange删除");
			context.getContentResolver().delete(contacturl, VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_NUMBER + "=?", new String[] { "3", number });
			context.getContentResolver().delete(contacturl, VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_NUMBER + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL + "=? and " + VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH + "=?", new String[] { "1", number, "3", "00分00秒" });
		}

		VsPhoneCallHistory.callLogs.clear();
		loadCallLog();
		Intent intent = new Intent();
		intent.setAction(DfineAction.ACTION_SHOW_CALLLOG);
		context.sendBroadcast(intent);
		return;
	}


	/**
	 * 删除单个通话记录
	 *
	 * @param context
	 * @param phonenum
	 */
	public static void delCallLog(Context context, String phonenum) {
		Uri contacturl = Uri
				.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
		if (context == null) {
			return;
		}
		// 删除列表
		for (int i = 0; i < callLogs.size(); i++) {
			if (callLogs.get(i).getFirst().callNumber.equals(phonenum)) {
				callLogs.remove(i);
			}
		}

		// 删除数据库中单个通话记录
		// CursorHelper.deleteByHistroy(context, phonenum);
		context.getContentResolver().delete(contacturl, VsPhoneCallHistory.PHONECALLHISTORY_NUMBER + "=?",
				new String[] { phonenum });

		Intent intent = new Intent();
		intent.setAction(DfineAction.ACTION_SHOW_CALLLOG);
		context.sendBroadcast(intent);
		return;
	}

	/**
	 * 删除所有通话记录
	 *
	 * @param context
	 */
	public static void delAllCallLog(Context context) {
		Uri contacturl = Uri
				.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);

		if (context == null)
			return;

		// 删除列表
		callLogs.clear();

		// 删除数据库中所以通话记录
		// CursorHelper.deleteAllHistroy(context);
		context.getContentResolver().delete(contacturl, null, null);
		context.sendBroadcast(new Intent(DfineAction.ACTION_SHOW_CALLLOG));
		return;
	}

	/**
	 * 加载联系人归属地
	 *
	 * @param mContext
	 */
	private static void loadContactsAndLocal(Context mContext) {
		try {
			if (CONTACTLIST == null) {
				return;
			}
			Log.i(TAG, "loadContactsAndLocal(), CONTACTLIST.size() = " + CONTACTLIST.size());
			if (CONTACTLIST.size() == 0) {
				if (!isloadContact) {
					loadContactData(mContext, 0);
				} else {
					return;
				}
			} else if (CONTACTLIST.size() > 0) {
				// 获取好友
//				VsBizUtil.getInstance().getVsFriend(mContext);
				if (CONTACTLIST.get(CONTACTLIST.size() - 1).mContactBelongTo.length() == 0) {
					// 可能存在最后一个查不到归属地的情况
					loadPhoneLocal(mContext);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isloadContact = false;// 是否正在加载联系人

	/**
	 * 读取联系人数据
	 */
	public static void loadContactData(final Context mContext, final int type) {
		isloadContact = true;
		BaseRunable newRunable = new BaseRunable() {
			@SuppressWarnings("deprecation")
			public void run() { // 线程运行主体
				// 用户于过虑一个人有多个号码的map
				HashMap<String, String> _id_map = new HashMap<String, String>();
				List<VsContactItem> list = (List<VsContactItem>) Collections.synchronizedList(new ArrayList<VsContactItem>(1000));
				CustomLog.i(TAG, "loadContactData(), start............ type = " + type);
				try {
					Cursor cursor = getallContacts(mContext);
					if (cursor == null) {
						return;
					}
					int count = cursor.getCount();
					if (count > 0 && type == 0) {// 通知UI正在加载数量据而不是显示暂无数据
						VsUtil.sendSpecialBroadcast(mContext, DfineAction.CURRENT_LOGD_CONTACTLISTACTION);
					} else {
						VsUtil.sendSpecialBroadcast(mContext, VsUserConfig.JKEY_NO_CONTACTS);
					}
					VsContactItem contactItem;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
						for (int i = 0; i < count; i++) {
							try {
								cursor.moveToPosition(i);
								if (cursor.getColumnCount() < 5) {// 判断是否有联系人ID
									continue;
								}
								String id = cursor.getString(4);
								String name = ConverToPingying.replaceString(cursor.getString(1));
								String number = "";
								try {
									number = cursor.getString(2).trim();
								} catch (Exception e1) {
									CustomLog.i(TAG, "loadContactData(), get contact number error:" + e1.getLocalizedMessage());
								}
								// 处理有联系人名字。但没有联系人号码的情况。部分手机在联系人详情会崩溃。所以统一获取的时候处理
								if (number == null || number.length() == 0) {
									continue;
								}
								number = removePrefix(number).replaceAll("-", "").replaceAll(" ", "");

								if (name != null && name.length() == 0) {
									name = number;
								}
								if (_id_map.get(id) == null) {
									contactItem = new VsContactItem();
									String[] PinYin = ConverToPingying.getInstance().converToPingYingAndNumber(name);

									contactItem.mContactName = name;
									contactItem.mContactId = id;
									contactItem.mContactPhoneNumber = number;
									contactItem.mContactPY = PinYin[2];
									contactItem.mContactPYToNumber = PinYin[3];
									contactItem.mContactFirstUpper = PinYin[0];
									contactItem.mContactFirstUpperToNumber = PinYin[1];
									contactItem.phoneNumList.add(number);
									contactItem.mContactFirstLetter = ConverToPingying.getInstance()
											.getAlpha(PinYin[2]);

									if (list != null
											&& list.size() > 0
											&& list.get(list.size() - 1).mContactId
											.equals(contactItem.mContactId)) {// 判断联系人是否已经存在
										list.get(list.size() - 1).phoneNumList
												.add(contactItem.mContactPhoneNumber);
									} else {
										list.add(contactItem);
									}
								}
							} catch (Exception e) {
								CustomLog.i(TAG, "loadContactData(), get contact error:" + e.getLocalizedMessage());
							}
						}
					} else {
						for (int i = 0; i < count; i++) {
							cursor.moveToPosition(i);
							if (cursor.getColumnCount() < 4) {// 判读是否有联系人ID
								continue;
							}
							String id = cursor.getString(3);
							String name = ConverToPingying.replaceString(cursor.getString(1));
							String number = cursor.getString(2).trim();

							number = removePrefix(number);
							// 处理有联系人名字。但没有联系人号码的情况。部分手机在联系人详情会崩溃。所以统一获取的时候处理
							if (number == null || number.length() == 0) {
								continue;
							}
							if (name != null && name.length() == 0) {
								name = number;
							}
							if (_id_map.get(id) == null) {
								contactItem = new VsContactItem();
								String[] PinYin = ConverToPingying.getInstance().converToPingYingAndNumber(name);
								contactItem.mContactName = name;
								contactItem.mContactId = id;
								contactItem.mContactPhoneNumber = number;
								contactItem.mContactPY = PinYin[2];
								contactItem.mContactPYToNumber = PinYin[3];
								contactItem.mContactFirstUpper = PinYin[0];
								contactItem.mContactFirstUpperToNumber = PinYin[1];
								contactItem.phoneNumList.add(number);
								contactItem.mContactFirstLetter = ConverToPingying.getInstance().getAlpha(PinYin[2]);

								/**
								 * 过滤重复的联系人
								 */
								if (list != null
										&& list.size() > 0
										&& list.get(list.size() - 1).mContactId
										.equals(contactItem.mContactId)) {
									list.get(list.size() - 1).phoneNumList
											.add(contactItem.mContactPhoneNumber);
								} else {
									list.add(contactItem);
								}
							}
						}
					}
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
					Collections.sort(list, new Comparator<VsContactItem>() {

						@Override
						public int compare(VsContactItem lhs, VsContactItem rhs) {
							if (rhs.mContactFirstLetter.equals("#")) {
								return -1;
							} else if (lhs.mContactFirstLetter.equals("#")) {
								return 1;
							} else {
								return lhs.mContactFirstLetter.compareTo(rhs.mContactFirstLetter);
							}
						}
					});

				} catch (Exception e) {
					CustomLog.i(TAG, "loadContactData(), get contact, sdk > 8. error:" + e.getLocalizedMessage());
				} finally {
					if (null != list && null != CONTACTLIST){
						CONTACTLIST.clear();
						CONTACTLIST.addAll(list);
					}
				}

				loadPhoneLocal(mContext);
				_id_map.clear();
				_id_map = null;
				isloadContact = false;
				if (type == 0) {
					// 查询VS好友
//					VsBizUtil.getInstance().getVsFriend(mContext);
				}
				ConverToPingying.getInstance().clearProperties();
				VsUtil.sendSpecialBroadcast(mContext, GlobalVariables.action_contact_detail_change);
			}
		};
		// 使用线程池进行管理
		GlobalVariables.fixedThreadPool.execute(newRunable);
	}

	/**
	 * 归属地下载和下载后要进行的操作
	 */
	public static void loadPhoneLocal(final Context mContext) {
		VsUtil.getDownLoadFile(DfineAction.mWldhFilePath,
				VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumberUrl, DfineAction.phoneNumberUrl),
				mContext);
		loadContactLocal(mContext);
	}

	/**
	 * 加载归属地
	 */
	public static void loadContactLocal(Context mContext) {
		CustomLog.i(TAG, "loadContactLocal(),...");
		if (CONTACTLIST == null) {
			return;
		}
		int size = CONTACTLIST.size();
		for (int i = 0; i < size; i++) {
			loadMoreContactLocal(mContext, i);
		}
		VsUtil.sendSpecialBroadcast(mContext, DfineAction.REFERSHLISTACTION);
	}

	/**
	 * 加载一个联系人里多个号码的归属地
	 *
	 * @param mContext
	 */
	public static void loadMoreContactLocal(Context mContext, int idx) {
		try {
			CONTACTLIST.get(idx).localNameList.clear();//确保phoneNumList与localNameList一一对应
			for (int i = 0; i < CONTACTLIST.get(idx).phoneNumList.size(); i++) {
				CONTACTLIST.get(idx).localNameList.add(VsLocalNameFinder.findLocalName(
						CONTACTLIST.get(idx).phoneNumList.get(i).toString(), false, mContext));
			}
			CONTACTLIST.get(idx).mContactBelongTo = CONTACTLIST.get(idx).localNameList.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有联系人
	 *
	 * @param context
	 * @return Cursor
	 */
	public static Cursor getallContacts(Context context) {
		String ver = "";
		if (GlobalVariables.SDK_VERSON >= 8) {
			ver = " sort_key COLLATE LOCALIZED asc";
			Uri uri = Uri.parse("content://com.android.contacts/data/phones");
			String[] projection = { "_id", "display_name", "data1", "sort_key", "contact_id", "photo_id" };
			ContentResolver contentresolver = context.getContentResolver();
			Cursor cur = contentresolver.query(uri, projection, null, null, "sort_key COLLATE LOCALIZED asc");
			return cur;
		} else {
			ver = " display_name COLLATE LOCALIZED asc";
			Uri uri = Uri.parse("content://com.android.contacts/data/phones");
			String[] projection = { "_id", "display_name", "data1", "contact_id", "photo_id" };
			ContentResolver contentresolver = context.getContentResolver();
			Cursor cur = contentresolver.query(uri, projection, null, null, ver);
			return cur;
		}
	}

	/**
	 * 获取单个联系人信息
	 *
	 * @param context
	 * @return Cursor
	 */
	public static Cursor getsignalContacts(Context context, String contact_id) {
		Cursor cur = null;
		if (contact_id != null && !"".equals(contact_id)) {
			Uri uri = Uri.parse("content://com.android.contacts/data/phones");
			String[] projection = { "display_name", "data1", };
			ContentResolver contentresolver = context.getContentResolver();
			cur = contentresolver.query(uri, projection, ContactsContract.Data.CONTACT_ID + "=?",
					new String[] { contact_id }, null);
		}
		return cur;
	}

	/**
	 * 去掉号码的前缀
	 *
	 * @param num
	 * @return
	 */
	public static String removePrefix(String num) {
		if (num != null) {
			try {
				num = num.replaceAll("-", "");
				num = num.replace("+", "");
				if (num.matches("^86.*"))
					num = num.substring("86".length());
				if (num.matches("^12593.*|17951.*|17909.*|17911.*")) {
					num = num.substring("12593".length());
				}
			} catch (Exception e) {
			}
		}
		return num;
	}

	/**
	 * 返回
	 *
	 * @param mContext
	 * @param num
	 *            需要返回多少个
	 */
	public static String loadHistoryContact(Context mContext, int num, boolean contactId) {
		if (mContext == null) {
			return null;
		}
		Cursor mCursor = null;
		int phoneNum = 0;
		JSONArray array = new JSONArray();
		try {
			mCursor = mContext.getContentResolver().query(
					Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE),
					new String[] { "DISTINCT callnumber,callname" }, null, null, "calltimestamp desc");
			if (mCursor == null) {
				return null;
			}
			Integer count = mCursor.getCount();
			mCursor.moveToFirst();
			while (mCursor.getPosition() != count) {
				String phone = mCursor.getString(mCursor.getColumnIndex(VsPhoneCallHistory.PHONECALLHISTORY_NUMBER));
				CustomLog.i("GDK", "通话记录号码:" + phone);
				Cursor cursor = getHistoryContacts(mContext, phone);
				if (cursor == null) {
					return null;
				}
				for (int i = 0; i < cursor.getCount(); i++) {
					JSONObject json = new JSONObject();
					cursor.moveToPosition(i);
					json.put("name", ConverToPingying.replaceString(cursor.getString(0)));
					json.put("number", cursor.getString(1).trim());
					CustomLog.i("GDK", cursor.getString(2).trim());
					if (contactId) {
						json.put("contact_id", cursor.getString(2).trim());
					}
					phoneNum++;
					array.put(json);
				}
				mCursor.moveToNext();
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
				if (phoneNum == num) {
					return array.toString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
		return array.toString();
	}

	/**
	 * 获取存在传入参数号码的联系人
	 *
	 * @param context
	 * @param phone
	 * @return
	 */
	public static Cursor getHistoryContacts(Context context, String phone) {
		String ver = " display_name COLLATE LOCALIZED asc";
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "display_name", "data1", ContactsContract.Data.CONTACT_ID };
		ContentResolver contentresolver = context.getContentResolver();
		Cursor cur = contentresolver.query(uri, projection, "data1=?", new String[] { phone }, ver);
		return cur;
	}
}
