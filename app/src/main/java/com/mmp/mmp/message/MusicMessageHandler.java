package com.mmp.mmp.message;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mmp.mmp.util.MultiPlayer;
import com.mmp.mmp.util.MyMusicPlayer;

import java.util.ArrayList;

/**
 * 处理音乐播放消息的类，用来解析消息，转成对应的播放行为
 * 或者返回对应的信息；
 * 比如，消息请求播放上一首，就播放上一首，
 *请求一个播放列表，就返回播放列表
 */
public class MusicMessageHandler extends Handler implements Handlerable {

    private final String TAG="MusciMessageHandler";

    private MultiPlayer mplayer ;

    private Service  service ;

    private static  MusicMessageHandler INSTANCE = new MusicMessageHandler();
    private  MusicMessageHandler() {
    }

    public static MusicMessageHandler getInstance(){
        return INSTANCE;
    }


    private void check(){
        if(this.mplayer == null || this.service == null){
            throw new IllegalStateException("you need to bind interface MultPlayer and class Service first!");
        }
    }

    public void bindMultiPlayAndService(MultiPlayer mplayer,Service service){
        this.mplayer = mplayer;
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg) {
        check();
        Log.i(TAG, "handleMessage: has recieved a message ,is dealing with it!");
        Bundle data = msg.getData();
        int command = data.getInt("command");
        int category = HandlerMusic.category(command);
        Log.i(TAG, "handleMessage: commad "+command+" category "+category);
        switch (category) {
            case HandlerMusic.CATEGORY_MUSIC_ORDER:
                categoryMusicOrder(data, command);
                break;
            case HandlerMusic.CATEGORY_MUSIC_REQUEST:
                categoryMusciRequest(msg, command);
                break;
        }
        Log.i(TAG, "handleMessage: message has been done");
        super.handleMessage(msg);
    }

    private void categoryMusciRequest(Message msg, int command) {
        Log.i(TAG, "categoryMusicRequest: message category request");
        Handler h =(Handler)msg.obj;
        if (h != null) {
            Message m1 = Message.obtain();
            Bundle d = new Bundle();
            m1.setData(d);
            int response = HandlerMusic.response(command);
            Log.i(TAG, "handleMessage: response "+ response);
            switch (response) {
                case HandlerMusic.RESPONSE_MUSIC_DURATION:
                    m1.what=HandlerMusic.RESPONSE_MUSIC_DURATION;
                    d.putInt("duration",mplayer.getDuration());
                    h.sendMessage(m1);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_PROGRESS:
                    m1.what=HandlerMusic.RESPONSE_MUSIC_PROGRESS;
                    d.putInt("progress",mplayer.getPosition());
                    h.sendMessage(m1);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_LIST:
                    m1.what=HandlerMusic.RESPONSE_MUSIC_LIST;
                    d.putStringArrayList("list",(ArrayList<String>)mplayer.getList());
                    h.sendMessage(m1);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_SELF:
                    m1.what=HandlerMusic.RESPONSE_MUSIC_SELF;
                    d.putString("self",mplayer.current());
                    h.sendMessage(m1);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_STATE:
                    m1.what=HandlerMusic.RESPONSE_MUSIC_STATE;
                    d.putBoolean("isplaying",mplayer.isPlaying());
                    d.putBoolean("isstoped", mplayer.isStoped());
                    h.sendMessage(m1);
                    break;
            }
        }
    }

    private void categoryMusicOrder(Bundle data, int command) {
        Log.i(TAG, "handleMessage: message category order");
        int specified = data.getInt("specified");
        int currentIndex = mplayer.currentIndex();
        int content = HandlerMusic.content(command);
        int action = HandlerMusic.action(command);

        Intent i = new Intent();
        i.setAction("com.mmp.music.status_changed");
        service.sendBroadcast(i);
        Log.i(TAG, "handleMessage: broadcast has been send out !");

        Log.i(TAG, "handleMessage: message content "+content+" action "+action+" specified "+specified);
        switch (content) {
            case HandlerMusic.CONTENT_MUSIC_LAST:
                mplayer.playLast();
                break;
            case HandlerMusic.CONTENT_MUSIC_CURRENT:
                specified = currentIndex;
                break;
            case HandlerMusic.CONTENT_MUSIC_NEXT:
                mplayer.playNext();
                break;
            case HandlerMusic.CONTENT_MUSIC_SPECIFIED:
                mplayer.play(specified);
                break;
        }
        switch (action) {
            case HandlerMusic.ACTION_MUSIC_SEEKTO:
                int position = data.getInt("position");
                mplayer.seekTo(position);
                Log.i(TAG, "handleMessage: message action seek to position "+position);
                break;
            case HandlerMusic.ACTION_MUSIC_START:
                mplayer.start();
                Log.i(TAG, "handleMessage: message action start");
                break;
            case HandlerMusic.ACTION_MUSIC_PAUSE:
                mplayer.pause();
                Log.i(TAG, "handleMessage: message action pause");
                break;
            case HandlerMusic.ACTION_MUSIC_STOP:
                mplayer.stop();
                Log.i(TAG, "handleMessage: message action stop");
                break;
            default:
                if (action == (HandlerMusic.ACTION_MUSIC_PAUSE | HandlerMusic.ACTION_MUSIC_START)) {
                    if(specified == currentIndex) {
                        mplayer.startOrPause();
                    }
                    else {
                        mplayer.start();
                    }
                    Log.i(TAG, "handleMessage: message action play or pause");
                }
        }
    }

    @Override
    public Handler getHandler() {
        return this;
    }
}
