package com.vmc.core.worker.ads.db;

import android.provider.BaseColumns;

/**
 * <b>Create Date:</b> 9/11/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public interface AdsContract {

    String TEXT_TYPE = " TEXT";
    String INTEGER_TYPE = " INTEGER";
    String COMMA_SEP = ",";
    String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
            Entry._ID + " INTEGER PRIMARY KEY," +
            Entry.COLUMN_NAME_ADS_ORDER + INTEGER_TYPE + COMMA_SEP +
            Entry.COLUMN_NAME_ADS_TYPE + TEXT_TYPE + COMMA_SEP +
            Entry.COLUMN_NAME_ADS_URL + TEXT_TYPE + COMMA_SEP +
            " )";
    String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    class Entry implements BaseColumns {
        public static final String TABLE_NAME = "ads";
        public static final String COLUMN_NAME_ADS_ID = "ad_id";
        public static final String COLUMN_NAME_ADS_ORDER = "order";
        public static final String COLUMN_NAME_ADS_TYPE = "type";
        public static final String COLUMN_NAME_ADS_URL = "url";
    }
}
