package com.weiwei.salemall.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hwtx.dududh.BuildConfig;

/**
 * author: Created by EDZ on 2018/7/5.
 * function:
 */

public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private static DaoSession daoSession;
    private static DaoMaster daoMaster;
    private static SQLiteDatabase db;

    private GreenDaoManager(Context context) {
        //通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper
        //注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "shoppingcart_db", null);
        //注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        db = devOpenHelper.getWritableDatabase();
//        devOpenHelper.onUpgrade(db, BuildConfig.VERSION_CODE - 1, BuildConfig.VERSION_CODE);

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static GreenDaoManager getmInstance(Context context) {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager(context);
                }
            }
        }
        return mInstance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }
}

