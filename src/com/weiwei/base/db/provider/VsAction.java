package com.weiwei.base.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.weiwei.base.common.CustomLog;
import com.weiwei.base.dataprovider.DfineAction;
import com.weiwei.json.me.JSONObject;

public class VsAction {
	public static final String TABLE_NAME = "action";
	public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir";
	public static final String MIME_ITEM_PREFIX = "vnd.android.cursor.item";
	public static final String MIME_ITEM = "vnd.skype.action";

	public static final String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + "/" + MIME_ITEM;

	public static final String MEME_TYPE_MULTIPLE = "MIME_DIR_PREFIX" + "/" + MIME_ITEM;

	public static final String PATH_SINGLE = "action/#";
	public static final String PATH_MULTIPLE = "action";

	public static final String ID = "_id"; // 唯一标识主键
	public static final String ACTION_ID = "actionid"; // 动作id
	public static final String ACTION_ACTIVITY = "actionbody"; // 界面id
	public static final String ACTION_TYPE = "actiontype";// 界面动作类型
	public static final String ACTION_CTIME = "actionctime";// 动作点击时间
	public static final String ACTION_USERDTIME = "actionusertime";// 动作耗时时长
	public static final String ACTION_RESERVE = "actionreserve";// 扩展参数_预留

	/**
	 * 添加一条动作统计
	 * 
	 * @param name
	 * @param numbers
	 * @param mHandler
	 * @param context
	 */
	public static void insertAction(int action_activity, String action_type, String action_ctime,
			String action_userdtime, Context context) {
		try {
			Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + TABLE_NAME);
			ContentValues cv = new ContentValues();
			cv.put(ACTION_ACTIVITY, action_activity);
			cv.put(ACTION_TYPE, action_type);
			cv.put(ACTION_CTIME, action_ctime);
			cv.put(ACTION_USERDTIME, action_userdtime);
			context.getContentResolver().insert(contacturl, cv);
		} catch (Exception e) {
			CustomLog.i("GDK", e.toString());
		}
	}

	/**
	 * 删除动作统计信息
	 * 
	 * @param context
	 */
	public static void deleteActionInfo(Context context) {
		Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + TABLE_NAME);
		context.getContentResolver().delete(contacturl, null, null);
	}

	/**
	 * 查询保存的行为统计信息
	 * 
	 * @param context
	 */
	public static String selectActionList(Context context) {
		Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + TABLE_NAME);
		Cursor mCursor = context.getContentResolver().query(contacturl, null, null, null, null);
		if (mCursor == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		StringBuffer sb = new StringBuffer();
		try {
			for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
				if (sb != null && sb.length() > 0) {
					sb.append("|");
				}
				sb.append(mCursor.getString(mCursor.getColumnIndex(ACTION_ACTIVITY)) + ","
						+ mCursor.getString(mCursor.getColumnIndex(ACTION_TYPE)) + ","
						+ mCursor.getString(mCursor.getColumnIndex(ACTION_CTIME)) + ","
						+ mCursor.getString(mCursor.getColumnIndex(ACTION_USERDTIME)));
			}
			if (sb != null && sb.length() > 0) {
				json.put("keys", "adpid,adid,ctime,usedtime");
				json.put("values", sb.toString());
				return json.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
			}
			if (sb != null) {
				sb = null;
			}
		}
		return null;
	}
}
