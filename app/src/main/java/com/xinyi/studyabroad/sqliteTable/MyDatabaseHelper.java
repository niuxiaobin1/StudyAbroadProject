package com.xinyi.studyabroad.sqliteTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Niu on 2017/3/31.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "liuxue_base";
    public static final String TABLE_NAME = "search_record";
    private static final int VERSION = 1;
    public static final String ID = "_id";
    public static final String KEY_NAME = "title";
    public static final String KEY_ID = "id";

    //建表语句


    public static final String CREATE_BOOK = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + KEY_NAME + " varchar," + KEY_ID + " varchar)";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
