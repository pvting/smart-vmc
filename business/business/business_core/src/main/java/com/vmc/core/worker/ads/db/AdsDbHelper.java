package com.vmc.core.worker.ads.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <b>Create Date:</b> 9/11/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 广告数据库帮助类
 * <br>
 */
public class AdsDbHelper extends SQLiteOpenHelper implements AdsContract {
    // TODO: 9/11/16 数据库实现

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Ads.db";

    public AdsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 9/11/16 数据库升级实现
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
