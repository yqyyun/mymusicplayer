package com.mmp.mmp.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mmp.mmp.bean.Music;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.message.Handlerable;
import com.mmp.mmp.message.MusicMessageHandler;
import com.mmp.mmp.util.MultiPlayer;
import com.mmp.mmp.util.MyMusicPlayer;

import java.util.ArrayList;
import java.util.List;


public class PlayService extends Service implements Handlerable {

    private final  String TAG = "PlayService";

    /**
     * 播放器。
     */
    private MultiPlayer mplayer = new MyMusicPlayer();

    private MusicMessageHandler handler =MusicMessageHandler.getInstance();




    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Intent i = new Intent();
            i.setAction("com.mmp.music.status_changed");
            sendBroadcast(i);
            Log.i(TAG, "onCompletion: broadcast has been send out !");
            if (mp.isLooping()) {
                mp.seekTo(0);;
            }else {
                if (mplayer != null) {
                    mplayer.playNext();
                    mplayer.start();
                    if (handler != null) {
                        HandlerMusic handlerMusic = HandlerMusic.getInstance();
                        try{
//                            handlerMusic.requestMusic(handler);
//                            handlerMusic.requestDuration(handler);
//                            handlerMusic.requestProgress(handler);
//                            handlerMusic.requestState(handler);
                            Intent  intent = new Intent();
                            intent.setAction("com.mmp.music.status_changed");
                            sendBroadcast(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            // TODO: 2019-06-04
        }
    };


    @Override
    public Handler getHandler() {
        return handler;
    }

    // TODO: 2019-06-02 避免使用此接口
    public class MyBinder extends Binder {
        public MultiPlayer getMutiPlayer() {
            return mplayer;
        }
        public Handler getHandler() {
            return handler;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: 2019-06-02
        String name  = intent.getStringExtra("from");
        Log.i(TAG, "onBind: the service of player has been binded "+(name != null ?"by a activity called "+name:"!"));
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        handler.bindMultiPlayAndService(mplayer,this);
        List<Music> ls = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        Cursor query = resolver.query(Uri.parse("content://com.yqy.musicdata"), null, null, null, null, null);
        while (query.moveToNext()) {
            int id = query.getInt(0);
            String s1 = query.getString(1);
            String s2 = query.getString(2);
            String s3 = query.getString(3);
            String s4 = query.getString(4);
            Music m = new Music();
            m.setId(id);
            m.setName(s1);
            m.setAlbum(s2);
            m.setArtist(s3);
            m.setAbspath(s4);
            System.out.println(m);
            ls.add(m);
        }
        query.close();
        mplayer.setSource((List<Music>)ls);
        mplayer.setOnCompletionListener(completionListener);
        mplayer.setOnSeekCompleteListener(seekCompleteListener);

        HandlerMusic.getInstance();

        getSharedPreferences("config",Context.MODE_PRIVATE|Context.MODE_APPEND).edit().putBoolean("booted",true).commit();

        Log.i(TAG, "onCreate: the service of player has been initialized!");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = null ;
        if (intent != null) {
            name = intent.getStringExtra("from");
//        sendOrderedBroadcast();
        }
        List<Music> ls = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        Cursor query = resolver.query(Uri.parse("content://com.yqy.musicdata"), null, null, null, null, null);
        while (query.moveToNext()) {
            int id = query.getInt(0);
            String s1 = query.getString(1);
            String s2 = query.getString(2);
            String s3 = query.getString(3);
            String s4 = query.getString(4);
            Music m = new Music();
            m.setId(id);
            m.setName(s1);
            m.setAlbum(s2);
            m.setArtist(s3);
            m.setAbspath(s4);
            System.out.println(m);
            ls.add(m);
        }
        query.close();
        mplayer.setSource((List<Music>)ls);
        Log.i(TAG, "onStartCommand: the service of player has been started "+(name != null?"by a activity called "+name:"!"));
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if (mplayer != null) {
            mplayer.release();
            mplayer = null;
        }
        SharedPreferences.Editor configEdit = getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        configEdit.putBoolean("booted",false);
        configEdit.commit();
        Log.i(TAG, "onDestroy: the service of player has been destroyed ！");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String name = intent.getStringExtra("form");
        Log.i(TAG, "onUnbind: the service of player has been unbinded "+(name != null ?"by a activity called "+name:"!"));
        return super.onUnbind(intent);
    }
}
