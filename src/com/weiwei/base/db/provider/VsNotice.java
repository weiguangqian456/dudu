package com.weiwei.base.db.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.base.item.VsNoticeItem;
import com.hwtx.dududh.R;

public class VsNotice {
	public static final String TABLE_NAME_NOTICE = "notice";
	public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
	public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
	public static final String MIME_ITEM = "vnd.skype.notice";

	public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;

	public static final String MEME_TYPE_MULTIPLE = "MIME_DIR_PREFIX" + "/" + MIME_ITEM;
	public static final Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/"
			+ VsNotice.PATH_MULTIPLE);

	public static final String PATH_SINGLE = "notice/#";
	public static final String PATH_MULTIPLE = "notice";

	public static final String ID = "_id"; // 唯一标识主键
	public static final String NOTICE_ID = "messageid"; // 消息id
	public static final String NOTICE_BODY = "messagebody"; // 消息内容
	public static final String NOTICE_TYPE = "messagetype";// 消息状态
	public static final String NOTICE_TITLE = "messagetitle";// 消息标题
	public static final String NOTICE_TIME = "messagetime";// 时间信息
	public static final String NOTICE_LINK = "messagelink";// 跳转链接
	public static final String NOTICE_BUTTONTEXT = "messagebuttontext";// 按钮标题
	public static final String NOTICE_LINKTYPE = "messagelinktype";// 跳转类别

	/**
	 * 所有消息数据
	 */
	public static ArrayList<VsNoticeItem> NoticeList = new ArrayList<VsNoticeItem>();
	/**
	 * 显示的消息数据
	 */
	public static ArrayList<VsNoticeItem> noticeViewList = new ArrayList<VsNoticeItem>();
	/**
	 * 获取消息数据
	 */
	public final static String VS_ACTION_LOADNOTICE = "com.kc.logic.loadnotice";
	/**
	 * 消息略读反馈接口
	 */
	public final static String VS_ACTION_FEEDBACK = "com.kc.logic.feedback";

	/**
	 * 修改消息中心内容
	 * 
	 * @param Context
	 */
	private static void sendNoticeMsg(Context Context) {
		Intent intent = new Intent();
		intent.setAction(DfineAction.ACTION_SHOW_NOTICE);
		Context.sendBroadcast(intent);
		Intent updateNoticeNumIntent = new Intent();
		updateNoticeNumIntent.setAction(DfineAction.ACTION_UPDATENOTICENUM);
		Context.sendBroadcast(updateNoticeNumIntent);
	}

	/**
	 * 添加消息记录
	 */
	public static void addNoticeMsg(Context mContext, VsNoticeItem vsNoticeItem) {
		ContentValues cv = new ContentValues();
		cv.put(VsNotice.NOTICE_ID, vsNoticeItem.messageid);
		cv.put(VsNotice.NOTICE_BODY, vsNoticeItem.messagebody);
		cv.put(VsNotice.NOTICE_TYPE, vsNoticeItem.messagetype);
		cv.put(VsNotice.NOTICE_TITLE, vsNoticeItem.messagetitle);
		cv.put(VsNotice.NOTICE_TIME, vsNoticeItem.messagetime);
		cv.put(VsNotice.NOTICE_LINK, vsNoticeItem.messagelink);
		cv.put(VsNotice.NOTICE_BUTTONTEXT, vsNoticeItem.messagebuttontext);
		cv.put(VsNotice.NOTICE_LINKTYPE, vsNoticeItem.messagelinktype);
		mContext.getContentResolver().insert(contacturl, cv);
		vsNoticeItem.notice_id = loadNoticeDataID(mContext);

		NoticeList.add(vsNoticeItem);// 页面显示增加一条记录
		copyStaticNoticeToViewList();
		sendNoticeMsg(mContext);
	}

	/**
	 * 排序后显示的历史记录
	 */
	public static void copyStaticNoticeToViewList() {
		ArrayList<VsNoticeItem> tmpNotices = new ArrayList<VsNoticeItem>();
		tmpNotices.addAll(NoticeList);
		Collections.sort(tmpNotices, new Comparator<VsNoticeItem>() {
			@Override
			public int compare(VsNoticeItem object1, VsNoticeItem object2) {
				try {
					if (object1.messagetype.equals("0") && object2.messagetype.equals("0")) {
						if (Integer.parseInt(object1.notice_id) > Integer.parseInt(object2.notice_id)) {
							return -1;
						}
						if (Integer.parseInt(object1.notice_id) < Integer.parseInt(object2.notice_id)) {
							return 1;
						}
					} else if (object1.messagetype.equals("1") && object2.messagetype.equals("1")) {
						if (Integer.parseInt(object1.notice_id) > Integer.parseInt(object2.notice_id)) {
							return -1;
						}
						if (Integer.parseInt(object1.notice_id) < Integer.parseInt(object2.notice_id)) {
							return 1;
						}
					} else {
						if (object1.messagetype.equals("0")) {
							return -1;
						}
						if (object2.messagetype.equals("1")) {
							return 1;
						}
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return 0;
			}
		});
		noticeViewList.clear();
		noticeViewList.addAll(tmpNotices);
		if (noticeViewList.size() == 0) {
			VsNoticeItem item = new VsNoticeItem();
			item.messagebody = R.string.notice_hint + "";
			item.messagetime = VsUtil.getDate();
			noticeViewList.add(item);
		}
	}

	/**
	 * 删除所有消息
	 * 
	 * @param context
	 */
	public static void delAllNotice(Context context) {
		if (context == null)
			return;

		// 删除列表
		NoticeList.clear();

		// 删除数据库中所以通话记录
		// CursorHelper.deleteAllHistroy(context);
		context.getContentResolver().delete(contacturl, null, null);
		sendNoticeMsg(context);
		return;
	}

	/**
	 * 删除单个消息记录
	 * 
	 * @param context
	 * @param phonenum
	 */
	public static void delNotice(Context context, String notice_id) {
		if (context == null) {
			return;
		}
		// 删除列表
		for (int i = 0; i < NoticeList.size(); i++) {
			if (NoticeList.get(i).notice_id.equals(notice_id)) {
				NoticeList.remove(i);
			}
		}

		// 删除数据库中单个通话记录
		// CursorHelper.deleteByHistroy(context, phonenum);
		context.getContentResolver().delete(contacturl, VsNotice.ID + "=?", new String[] { notice_id });
		sendNoticeMsg(context);
		return;
	}

	/**
	 * 修改消息记录状态
	 * 
	 * @param context
	 * @param phonenum
	 */
	public static void updateNotice(Context context, String notice_id) {
		if (context == null) {
			return;
		}
		// 删除列表
		for (int i = 0; i < NoticeList.size(); i++) {
			if (NoticeList.get(i).notice_id.equals(notice_id)) {
				NoticeList.get(i).messagetype = "1";
			}
		}
		ContentValues args = new ContentValues();
		args.put(VsNotice.NOTICE_TYPE, "1");
		// 修改数据库中单个消息记录
		context.getContentResolver().update(contacturl, args, VsNotice.ID + "=?", new String[] { notice_id });
		sendNoticeMsg(context);
		return;
	}

	/**
	 * 读取消息的ID
	 */
	public static String loadNoticeDataID(Context context) {
		String noticeId = "";
		if (context == null) {
			return noticeId;
		}
		Cursor mCursor = null;
		try {
			mCursor = context.getContentResolver().query(contacturl, null, null, null, "_id desc limit 1");
			if (mCursor == null) {
				return noticeId;
			}
			Integer num = mCursor.getCount();
			mCursor.moveToFirst();
			while (mCursor.getPosition() != num) {
				// calldistnum
				noticeId = mCursor.getString(mCursor.getColumnIndex(VsNotice.ID));
				mCursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
		}
		return noticeId;
	}

	/**
	 * 读取消息
	 */
	public static void loadNoticeData(Context context) {
		if (context == null) {
			return;
		}
		NoticeList.clear();
		Intent intent = new Intent();
		intent.setAction(DfineAction.ACTION_LOAD_NOTICE);
		context.sendBroadcast(intent);
		Cursor mCursor = null;
		try {
			mCursor = context.getContentResolver().query(contacturl, null, null, null, "_id desc");
			if (mCursor == null) {
				return;
			}
			Integer num = mCursor.getCount();
			mCursor.moveToFirst();
			while (mCursor.getPosition() != num) {
				VsNoticeItem vsNoticeItem = new VsNoticeItem();
				// calldistnum
				vsNoticeItem.messageid = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_ID));
				vsNoticeItem.messagebody = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_BODY));
				vsNoticeItem.messagetype = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_TYPE));
				vsNoticeItem.messagebuttontext = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_BUTTONTEXT));
				vsNoticeItem.messagelink = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_LINK));
				vsNoticeItem.messagelinktype = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_LINKTYPE));
				vsNoticeItem.messagetitle = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_TITLE));
				vsNoticeItem.messagetime = mCursor.getString(mCursor.getColumnIndex(VsNotice.NOTICE_TIME));
				vsNoticeItem.notice_id = loadNoticeDataID(context);
				// 如果存储消息大于一个月就删除掉
				if (VsUtil.getDateMonth(vsNoticeItem.messagetime)) {
					delNotice(context, vsNoticeItem.messageid);
				} else {
					NoticeList.add(vsNoticeItem);
				}
				mCursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
		}
		// 如果没有消息 加一条默认消息
		if (NoticeList.size() == 0) {
			VsNoticeItem item = new VsNoticeItem();
			item.messagebody = R.string.notice_hint + "";
			item.messagetime = VsUtil.getDate();
			noticeViewList.add(item);
		}
		copyStaticNoticeToViewList();
		sendNoticeMsg(context);
	}

	/**
	 * 获取是否有未读消息
	 */
	public static boolean unreadMessages(Context context) {
		boolean istype = false;
		if (context == null) {
			return istype;
		}
		Cursor mCursor = null;
		try {
			mCursor = context.getContentResolver().query(contacturl, null, NOTICE_TYPE + "= 0", null, null);
			if (mCursor == null) {
				return istype;
			}
			// CustomLog.i("MESSAGE", "有没有新消息" + mCursor.getCount());
			if (mCursor.getCount() > 0) {
				istype = true;
				return istype;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
		}
		return istype;
	}
}
