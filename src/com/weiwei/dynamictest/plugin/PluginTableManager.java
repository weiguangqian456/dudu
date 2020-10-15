package com.weiwei.dynamictest.plugin;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public abstract class PluginTableManager {
//	private static final String TAG = PluginTableManager.class.getName();
	public static PluginTableManager mInstance = null;
	@SuppressWarnings({ "rawtypes", "unused" })
	private HashMap mTableName2VersionMap = new HashMap();
	public int mTotalVersion = 100;

	public PluginTableManager() {
		loadTotalVersion();
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private HashMap loadHistoryTablesVersion(SQLiteDatabase paramSQLiteDatabase) {
//		return new hh().a(paramSQLiteDatabase);
		return null;
	}

	private void loadTotalVersion() {
//		ITableString[] arrayOfITableString = getAllTableStrings();
//		for (int i = 0; i < arrayOfITableString.length; i++) {
//			this.mTotalVersion += 1 + arrayOfITableString[i].getTableVersion();
//			this.mTableName2VersionMap.put(
//					arrayOfITableString[i].getTableName(),
//					Integer.valueOf(arrayOfITableString[i].getTableVersion()));
//		}
	}

	public void createAllTables(SQLiteDatabase paramSQLiteDatabase) {
//		//TLog.v(TAG, "createAllTables");
//		hh localhh = new hh();
//		paramSQLiteDatabase.execSQL(localhh.getCreateTableString());
//		localhh.b(paramSQLiteDatabase);
//		localhh.a(this.mTableName2VersionMap, paramSQLiteDatabase);
//		ITableString[] arrayOfITableString = getAllTableStrings();
//		for (int i = 0; i < arrayOfITableString.length; i++)
//			paramSQLiteDatabase.execSQL(arrayOfITableString[i]
//					.getCreateTableString());
	}

	public void deleteAllTables(SQLiteDatabase paramSQLiteDatabase) {
		//TLog.v(TAG, "deleteAllTables");
//		ITableString[] arrayOfITableString = getAllTableStrings();
//		for (int i = 0; i < arrayOfITableString.length; i++)
//			paramSQLiteDatabase.execSQL("drop table if exists "
//					+ arrayOfITableString[i].getTableName());
	}

//	protected abstract ITableString[] getAllTableStrings();

	public abstract String getDBName();

	public abstract int getDBVersion();

	public abstract String getSaveTabVerionName();

	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase) {
//		//TLog.v(TAG, "onUpgrade");
//		HashMap localHashMap = loadHistoryTablesVersion(paramSQLiteDatabase);
//		//TLog.v(TAG, "onUpgrade:" + localHashMap);
//		Iterator localIterator;
//		if ((localHashMap != null) && (localHashMap.size() > 0))
//			localIterator = localHashMap.keySet().iterator();
//		while (localIterator.hasNext()) {
//			String str = (String) localIterator.next();
//			int i = ((Integer) localHashMap.get(str)).intValue();
//			if (this.mTableName2VersionMap.containsKey(str)) {
//				if (i >= ((Integer) this.mTableName2VersionMap.get(str))
//						.intValue())
//					continue;
//				paramSQLiteDatabase.execSQL("drop table if exists " + str);
//				//TLog.v(TAG, "delete table:" + str);
//				continue;
//			}
//			paramSQLiteDatabase.execSQL("drop table if exists " + str);
//			//TLog.v(TAG, "delete table:" + str);
//			continue;
//			deleteAllTables(paramSQLiteDatabase);
//		}
//		hh localhh = new hh();
//		localhh.b(paramSQLiteDatabase);
//		localhh.a(this.mTableName2VersionMap, paramSQLiteDatabase);
	}
}
