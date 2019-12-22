package com.mmp.mmp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class DatabaseOpener extends SQLiteOpenHelper {

    public DatabaseOpener(Context context) {
        super(context,"music.db",null,3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table musiclist(id integer primary key autoincrement," +
                "name varchar(50)," +//歌曲名
                "album varchar(50)," +//专辑
                "artist varchar(50)," +//歌手
                "abspath text)");//绝对路径
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("MESSAGE for database ","数据库的版本发生变化了!");
    }
}
