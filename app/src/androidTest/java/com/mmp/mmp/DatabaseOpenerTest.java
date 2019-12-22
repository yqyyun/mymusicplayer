package com.mmp.mmp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mmp.mmp.dao.DatabaseOpener;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseOpenerTest {
    @Test
    public void openDB() {
        Context context = InstrumentationRegistry.getTargetContext();
        DatabaseOpener db = new DatabaseOpener(context);
        db.getReadableDatabase();
    }
    @Test
    public void insert() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver resolver =context.getContentResolver();
        ContentValues values=new ContentValues();
        values.put("name","zhangshan");
        resolver.insert(Uri.parse("content://com.yqy.musicdata"), values);
    }
    @Test
    public void select() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver resolver =context.getContentResolver();
        Cursor query = resolver.query(Uri.parse("content://com.yqy.musicdata"), null, null, null, null, null);
        while (query.moveToNext()) {
            String s0 = query.getString(0);
            String s1 = query.getString(1);
            String s2 = query.getString(2);
            String s3 = query.getString(3);
            String s4 = query.getString(4);
            System.out.println(s0 + s1 + s2 + s3 + s4);
        }
        query.close();
    }

    @Test
    public void update() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver resolver =context.getContentResolver();
        ContentValues values=new ContentValues();
        values.put("name","lishi");
        resolver.update(Uri.parse("content://com.yqy.musicdata"), values, "name=?", new String[]{"zhangshan"});
    }
    @Test
    public void delete() {
        Context context = InstrumentationRegistry.getTargetContext();
        ContentResolver resolver =context.getContentResolver();

        resolver.delete(Uri.parse("content://com.yqy.musicdata"),"name=?",new String[]{"lishi"});
    }
}
