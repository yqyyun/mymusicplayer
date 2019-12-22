package com.mmp.mmp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 操作音乐文件的类，将文件信息提取出来，放入sqllite数据库中
 */
public class MusicDatabaseOperator {

        public final String TAG = "MusicDatabaseOperator";
        private DatabaseOpener opener ;
        public MusicDatabaseOperator(Context context) {
            opener = new DatabaseOpener(context);
        }

    public void batchInsert(List<String> list){
        clearLocalMusicDB();
        String sql = "insert into musiclist (name,album,artist,abspath) values (?,?,?,?)";
        SQLiteDatabase wdb = opener.getWritableDatabase();
        SQLiteStatement st = wdb.compileStatement(sql);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for (String s : list) {
            if (s == null) {
                continue;
            }
            File file = new File(s);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                FileDescriptor fd = fileInputStream.getFD();
                retriever.setDataSource(fd);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
//            retriever.setDataSource(s);
            String  title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if(title!=null){
                //如果音乐文件名不为空
                st.bindString(1, title);
            } else{
                //如果音乐文件名为空，则使用路径中的文件名来代替
                int f = s.lastIndexOf('/');
                int l = s.lastIndexOf('.');
                title = s.substring(f != -1 ? f+1 : 0, l!=-1?l:0);
                st.bindString(1,title);
            }
            if(album!=null){st.bindString(2, album);}
            if(artist!=null){st.bindString(3, artist);}
            st.bindString(4, s);
            st.execute();
            Log.i(TAG, "batchInsert: title="+title+"album="+album+"artist="+artist+"abspath="+s);
        }
        wdb.close();
    }

    public void clearLocalMusicDB(){
        String sql = "delete from musiclist";
        SQLiteDatabase wdb = opener.getWritableDatabase();
        wdb.execSQL(sql);
        wdb.close();
    }
}
