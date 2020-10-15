package com.weiwei.base.db.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.weiwei.base.dataprovider.DfineAction;

public class SkypeProvider extends ContentProvider {

	private static final String TAG = "SkypeProvider";
	private static final String DATABASE_NAME = "skype.db";
	// 数据库有变化版本号+1
	private static final int DATABASE_VERSION = 11;

	private Context mContext;
	private DBOpenHelper mDBOpenHelper;// 数据库维护类(表创建与维护)
	private static SQLiteDatabase mDb; // 数据库操作类(表数据的增,删,改,查)

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		// 执行删除操作，返回受影响行数
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		int count = mDb.delete(args.table, args.where, args.args);
		// 执行相应操作后，需要通知observer(观察者)，databases产生变化
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments arguments = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(arguments.where)) {
			return "vnd.android.cursor.dir/" + arguments.table;
		} else {
			return "vnd.android.cursor.item/" + arguments.table;
		}
	}

	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		// return super.applyBatch(operations);
		mDb.beginTransaction();// 开始事务
		try {
			ContentProviderResult[] results = super.applyBatch(operations);
			mDb.setTransactionSuccessful();// 设置事务标记为成功
			return results;
		} finally {
			mDb.endTransaction();
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		SqlArguments args = new SqlArguments(uri);
		// 返回新插入行的ID
		long rowId = mDb.replace(args.table, "", values);
		Log.i(TAG, "rowId------------->" + rowId);
		if (args.table.equals(VsNotice.PATH_MULTIPLE)) {
			Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsNotice.PATH_MULTIPLE);
			if (rowId > 0) {
				Uri newUri = ContentUris.withAppendedId(contacturl, rowId);
				// 执行相应操作后，需要通知observer(观察者)，databases产生变化
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		} else if (args.table.equals(VsPhoneCallHistory.PATH_MULTIPLE)) {
			Uri contacturl = Uri
					.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsPhoneCallHistory.PATH_MULTIPLE);
			Log.i(TAG, "=======phonecallhistory========");
			if (rowId > 0) {
				Uri newUri = ContentUris.withAppendedId(contacturl, rowId);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		} else if (args.table.equals(VsAction.TABLE_NAME)) {
			Uri contacturl = Uri.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsAction.TABLE_NAME);
			if (rowId > 0) {
				Uri newUri = ContentUris.withAppendedId(contacturl, rowId);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		} else if (args.table.equals(VsMessage.TABLE_NAME_MESSAGE)) {
			Uri contacturl = Uri
					.parse("content://" + DfineAction.projectAUTHORITY + "/" + VsMessage.TABLE_NAME_MESSAGE);
			if (rowId > 0) {
				Uri newUri = ContentUris.withAppendedId(contacturl, rowId);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mContext = getContext();
		mDBOpenHelper = new DBOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(TAG, "mDBOpenHelper:" + mDBOpenHelper);
		try {
			mDb = mDBOpenHelper.getWritableDatabase();
		} catch (Exception e) {
			mDb = mDBOpenHelper.getReadableDatabase();
		}
		if (mDb != null) {
			Log.i(TAG, "SkypeProvider were loaded...");
			return true;
		} else {
			Log.i(TAG, "SkypeProvider were loaded failed");
			return false;
		}

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.table);
		Cursor cursor = qb.query(mDb, projection, selection, args.args, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		// 返回受影响行数
		int count = mDb.update(args.table, values, args.where, args.args);
		// 执行相应操作后，需要通知observer(观察者)，databases产生变化
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private static class DBOpenHelper extends SQLiteOpenHelper {

		// 创建消息中心表
		String sql_notice = "CREATE TABLE " + VsNotice.TABLE_NAME_NOTICE + " (" + VsNotice.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + VsNotice.NOTICE_ID + " TEXT NOT NULL, "
				+ VsNotice.NOTICE_BODY + " TEXT NOT NULL, " + VsNotice.NOTICE_TYPE + " TEXT NOT NULL, "
				+ VsNotice.NOTICE_TITLE + " TEXT NOT NULL, " + VsNotice.NOTICE_TIME + " TEXT, " + VsNotice.NOTICE_LINK
				+ " TEXT, " + VsNotice.NOTICE_BUTTONTEXT + " TEXT, " + VsNotice.NOTICE_LINKTYPE + " TEXT);";

		String sql_phonecall_history = "CREATE TABLE " + VsPhoneCallHistory.TABLE_NAME_PHONECALL_HISTORY + " ("
				+ VsPhoneCallHistory.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_NAME + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_NUMBER + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHOTOSHOPHISTORY_CALL_TYPE + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_TIME_LENGTH + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_MONEY + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_TIME_STAMP + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_DIRECTCALL + " TEXT NOT NULL, "
				+ VsPhoneCallHistory.PHONECALLHISTORY_LOCAL + " TEXT);";

		String sql_action = "CREATE TABLE " + VsAction.TABLE_NAME + " (" + VsAction.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + VsAction.ACTION_ACTIVITY + " INTEGER NOT NULL, "
				+ VsAction.ACTION_TYPE + " TEXT NOT NULL, " + VsAction.ACTION_CTIME + " TEXT , "
				+ VsAction.ACTION_USERDTIME + " TEXT , " + VsAction.ACTION_RESERVE + " TEXT);";

		String sql_message = "CREATE TABLE " + VsMessage.TABLE_NAME_MESSAGE + " (" + VsMessage.ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + VsMessage.MESSAGE_ID + " TEXT NOT NULL, "
				+ VsMessage.MESSAGE_MSG + " TEXT , " + VsMessage.MESSAGE_FILEPATH + " TEXT , " + VsMessage.MESSAGE_TYPE
				+ " INTEGER , " + VsMessage.MESSAGE_FROMUID + " TEXT , " + VsMessage.MESSAGE_TOUID + " TEXT , "
				+ VsMessage.MESSAGE_SIZE + " TEXT , " + VsMessage.MESSAGE_NAME + " TEXT , " + VsMessage.MESSAGE_TIME
				+ " TEXT , " + VsMessage.MESSAGE_UID + " TEXT , " + VsMessage.MESSAGE_ISME + " INTEGER ,"
				+ VsMessage.MESSAGE_ISSENDSUC + " INTEGER ," + VsMessage.MESSAGE_READ + " INTEGER);";

		public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// @Override
		// public void onOpen(SQLiteDatabase db) {
		// super.onOpen(db);
		// if(!db.isReadOnly()) {
		// // Enable foreign key constraints
		// db.execSQL("PRAGMA foreign_keys=ON;");
		// }
		// }

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				Log.i(TAG, "ready to create table.");
				db.execSQL(sql_notice);
				db.execSQL(sql_phonecall_history);
				db.execSQL(sql_action);
				db.execSQL(sql_message);
				Log.i(TAG, "created table successful.");
			} catch (Exception e) {
				Log.i(TAG, "failed to create table.");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + VsNotice.TABLE_NAME_NOTICE);
			db.execSQL("DROP TABLE IF EXISTS " + VsPhoneCallHistory.TABLE_NAME_PHONECALL_HISTORY);
			db.execSQL("DROP TABLE IF EXISTS " + VsAction.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + VsMessage.TABLE_NAME_MESSAGE);
			onCreate(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + VsNotice.TABLE_NAME_NOTICE);
			db.execSQL("DROP TABLE IF EXISTS " + VsPhoneCallHistory.TABLE_NAME_PHONECALL_HISTORY);
			db.execSQL("DROP TABLE IF EXISTS " + VsAction.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + VsMessage.TABLE_NAME_MESSAGE);
			onCreate(db);
		}
	}

	/**
	 * 
	 * 拆分Uri
	 */
	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.table = url.getPathSegments().get(0);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException("WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(0);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				table = url.getPathSegments().get(0);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}
}
