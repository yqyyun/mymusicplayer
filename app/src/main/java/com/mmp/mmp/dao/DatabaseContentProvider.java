package com.mmp.mmp.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

public class DatabaseContentProvider
        extends ContentProvider {

    private DatabaseOpener databaseOpener;
    private String tablename;

    @Override
    public boolean onCreate() {
        databaseOpener = new DatabaseOpener(getContext());
        tablename=new String("musiclist");
        Log.i("create contentprovider", "onCreate:open a database");
        return false;
    }


    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,   String[] selectionArgs,   String sortOrder) {
        SQLiteDatabase rdb = databaseOpener.getReadableDatabase();
        Cursor cursor = rdb.query(tablename, projection, selection, selectionArgs, null, null, sortOrder);
        Log.i("query contentprovider ",selection==null?"all columens":selection.toString());
        return cursor;
    }


    @Override
    public String getType( Uri uri) {
        Log.i("getType contentprovider", "getType() is invoked");
        return null;
    }


    @Override
    public Uri insert(Uri uri,   ContentValues values) {
        SQLiteDatabase wdb = databaseOpener.getWritableDatabase();
        wdb.insert(tablename,null,values);
        Log.i("insert contentprovider",values.toString());
        return null;
    }

    @Override
    public int delete( Uri uri,   String selection,   String[] selectionArgs) {
        SQLiteDatabase wdb = databaseOpener.getWritableDatabase();
        wdb.delete(tablename, selection, selectionArgs);
        Log.i("delete contentprovider", selection.toString() + "=" + selectionArgs);
        return 0;
    }

    @Override
    public int update(Uri uri,   ContentValues values,   String selection,   String[] selectionArgs) {
        SQLiteDatabase wdb = databaseOpener.getWritableDatabase();
        wdb.update(tablename, values, selection, selectionArgs);
        Log.i("updata contentprovider", values.toString());
        return 0;
    }
}
