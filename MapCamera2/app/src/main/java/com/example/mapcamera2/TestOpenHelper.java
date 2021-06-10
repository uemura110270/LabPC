package com.example.mapcamera2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TestOpenHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;

    // データーベース名
    private static final String DATABASE_NAME = "picturesave.db";
    private static final String TABLE_NAME = "picture";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_TITLE = "name";
    private static final String COLUMN_NAME_SUBTITLE = "picname";
    private static final String COLUMN_NAME_SUBTITLE2 = "date";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_SUBTITLE + " VARCHAR," +
                    COLUMN_NAME_SUBTITLE2 + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    TestOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

        Log.d("debug", "onCreate(SQLiteDatabase db)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートの判別
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}