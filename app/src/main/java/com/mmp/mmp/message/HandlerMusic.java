package com.mmp.mmp.message;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mmp.mmp.bean.Music;
import com.mmp.mmp.service.PlayService;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理播放控制消息的类。
 */
public class HandlerMusic {

    public final String TAG = "HandlerMusic";
    /**
     * command = action + category + response + content;
     */
    //action
    public final static int ACTION_MUSIC_START = 1;
    public final static int ACTION_MUSIC_PAUSE = 2;
    public final static int ACTION_MUSIC_STOP = 4;
    public final static int ACTION_MUSIC_SEEKTO = 29;
    //category
    public final static int CATEGORY_MUSIC_ORDER =7;
    public final static int CATEGORY_MUSIC_REQUEST=9;
    //response
    public final static int RESPONSE_MUSIC_NULL = 0;
    public final static int RESPONSE_MUSIC_SELF = 11;
    public final static int RESPONSE_MUSIC_LIST = 13;
    public final static int RESPONSE_MUSIC_PROGRESS = 15;
    public final static int RESPONSE_MUSIC_DURATION =17;
    public final static int RESPONSE_MUSIC_STATE = 27;
    //content
    public final static int CONTENT_MUSIC_LAST = 19;
    public final static int CONTENT_MUSIC_CURRENT=21;
    public final static int CONTENT_MUSIC_NEXT = 23;
    public final static int CONTENT_MUSIC_SPECIFIED = 25;

    //code
    private List<Music> list;
    private Handler to_handler;
    private Handler from_handler;
    /**
     * context -> fron_handler,intent -> connection
     * connection -> to_handler 
     */
//    private final Map<Context,Sett<>> map = new HashMap<>();
    private final Map<Context,ServiceConnection> connectionMap = new HashMap<>();
    private final Map<Context,Integer> nm = new HashMap<>();

    private static HandlerMusic mHandlerMusic = null;

    private HandlerMusic() {

    }

    public static HandlerMusic getInstance() {
        synchronized (HandlerMusic.class)
        {
            if (mHandlerMusic == null) {
                mHandlerMusic = new HandlerMusic();
            }
        }
        return mHandlerMusic;
    }


    public static int action(int cm) {
        return (int)((cm & (0xff<<24))>>24);
    }

    public static int category(int cm) {
        return (int)((cm & (0xff<<16))>>16);
    }

    public static int response(int cm) {
        return (int) ((cm & (0xff << 8)) >> 8);
    }

    public static int content(int cm) {
        return (int)(cm & 0xff);
    }

    /**
     * command = action + category + response + content;
     * @param action
     * @return
     */
    private int encodeCommand(int action,int category,int response,int content ){
        int command = ((action & 0xff)<<24) |
                      ((category & 0xff)<<16) |
                      ((response & 0xff)<<8) |
                      (content & 0xff);
        return command;
    }

    public void playLast() {
        checkAcess();
        int cm = encodeCommand(ACTION_MUSIC_START,CATEGORY_MUSIC_ORDER,RESPONSE_MUSIC_NULL,CONTENT_MUSIC_LAST);
        Message message = prepareOrder(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "playLast: "+from_handler+" has send a message "+message+" with "+cm);
    }

    private Message prepareOrder(int cd) {
        Message message = Message.obtain();
        Bundle data=new Bundle();
        data.putInt("command",cd);
        message.setData(data);
        return message;
    }

    public void playNext() {
        checkAcess();
        int cm = encodeCommand(ACTION_MUSIC_START, CATEGORY_MUSIC_ORDER, RESPONSE_MUSIC_NULL, CONTENT_MUSIC_NEXT);
        Message message = prepareOrder(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "playNext: "+from_handler+" has send a message "+message+" with "+cm);
    }

    public void playOrPause() {
        checkAcess();
        int cm = encodeCommand(ACTION_MUSIC_PAUSE | ACTION_MUSIC_START, CATEGORY_MUSIC_ORDER, RESPONSE_MUSIC_NULL, CONTENT_MUSIC_CURRENT);
        Message message = prepareOrder(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "playOrPause(): "+from_handler+" has send a message "+message+" with "+cm);
    }

    public void stopPlay() {
        checkAcess();
      int cm = encodeCommand(ACTION_MUSIC_STOP,CATEGORY_MUSIC_ORDER,RESPONSE_MUSIC_NULL,CONTENT_MUSIC_CURRENT);
        Message message = prepareOrder(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "stopPlay: "+from_handler+" has send a message "+message+" with "+cm);
    }

    public void seekTo(int position) {
        checkAcess();
        int cm = encodeCommand(ACTION_MUSIC_SEEKTO,CATEGORY_MUSIC_ORDER,0,0);
        Message message = Message.obtain();
        Bundle data  = new Bundle();
        data.putInt("command",cm);
        data.putInt("position", position);
        message.setData(data);
        to_handler.sendMessage(message);
        Log.i(TAG, "seekTo: "+from_handler+" has send a message "+message+" with "+cm);
    }

    private void requestProgress() {
        checkAcess();
        int cm = encodeCommand(0, CATEGORY_MUSIC_REQUEST, RESPONSE_MUSIC_PROGRESS, CONTENT_MUSIC_CURRENT);
        Message message = prepareRequest(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "requestProgress: "+from_handler+" has send a message "+message+" with "+cm);
    }
    private void requestList() {
        checkAcess();
        int cm = encodeCommand(0,CATEGORY_MUSIC_REQUEST,RESPONSE_MUSIC_LIST,CONTENT_MUSIC_CURRENT);
        Message message = prepareRequest(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "requestList: "+from_handler+" has send a message "+message+" with "+cm);
    }

    private void requestDuration() {
        checkAcess();
        int cm = encodeCommand(0,CATEGORY_MUSIC_REQUEST,RESPONSE_MUSIC_DURATION,CONTENT_MUSIC_CURRENT);
        Message message = prepareRequest(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "requestDuration: "+from_handler+" has send a message "+message+" with "+cm);
    }

    private void requestMusic() {
        checkAcess();
        int cm = encodeCommand(0,CATEGORY_MUSIC_REQUEST,RESPONSE_MUSIC_SELF,CONTENT_MUSIC_CURRENT);
        Message message = prepareRequest(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "requestMusic: "+from_handler+" has send a message "+message+" with "+cm);
    }

    private void requestState() {
        checkAcess();;
        int cm = encodeCommand(0, CATEGORY_MUSIC_REQUEST, RESPONSE_MUSIC_STATE, CONTENT_MUSIC_CURRENT);
        Message message = prepareRequest(cm);
        to_handler.sendMessage(message);
        Log.i(TAG, "requestState: "+from_handler+" has send a message "+message+" with "+cm);
    }
    private Message prepareRequest(int cm) {
        Message message = Message.obtain();
        message.obj = from_handler;
        Bundle data = new Bundle();
        data.putInt("command", cm);
        message.setData(data);
        return message;

    }

    public synchronized void  requestMusic(Handler h){
            from_handler = h;
            requestMusic();
            from_handler = null;
    }

    public  synchronized void requestProgress(Handler h) {
        from_handler = h;
        requestProgress();
        from_handler = null;
    }

    public synchronized void requestDuration(Handler h) {
        from_handler = h;
        requestDuration();
        from_handler = null;
    }
    public synchronized void requestState(Handler h){
        from_handler = h;
        requestState();
        from_handler = null;
    }

    public synchronized void requestList(Handler h) {
        from_handler = h;
        requestList();
        from_handler = h;
    }

    public void playOrPause(int position) {
        checkAcess();
        int command = encodeCommand(ACTION_MUSIC_START | ACTION_MUSIC_PAUSE, CATEGORY_MUSIC_ORDER, 0, CONTENT_MUSIC_SPECIFIED);
        Message message = Message.obtain();
        Bundle data  = new Bundle();
        data.putInt("command",command);
        data.putInt("specified", position);
        message.setData(data);
        to_handler.sendMessage(message);
        Log.i(TAG, "playOrPause（position): "+from_handler+" has send a messge with "+message+"command"+command);
    }

    private int mcount = 0;

    private void checkAcess() {
        if (to_handler == null) {
            throw new IllegalStateException(TAG+" handler and context is not set,please bind a player first !");
        }
    }

    private void setResultHandler(Handler h) {
        this.from_handler = h;
    }

    private boolean tohandlerset = false;


    /**
     * 注意：该方法最好在子线程中调用，因为，该方法可能会阻塞，直到连接成功建立。
     * @param c
     * @param player
     */
    public void bindPlayer(Context c, Class<?> player) {
        if (c == null || player == null) {
            throw new NullPointerException("context or player is null,bind player is faild");
        }
        if (!Service.class.isAssignableFrom(player)) {
            throw new InvalidParameterException("the player param is not a subclass of Service !");
        }
//        String name = player.getName();
        Intent intent = new Intent(c, player);
        ServiceConnection conn = connectionMap.get(c);
        if(conn != null) {
            return;
        }
        conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    to_handler = ((PlayService.MyBinder) service).getHandler();
                    tohandlerset = true;
                    synchronized (HandlerMusic.this) {
                        Log.i(TAG, "onServiceConnected: syhronized in");
                        HandlerMusic.this.notify();
                        Log.i(TAG, "onServiceConnected: synchronized out");
                    }
                    Log.i(TAG, "onServiceConnected: succeed to bind to " + name);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.e(TAG, "onServiceDisconnected:unknow error! disconnected to player");
                }
            };
            connectionMap.put(c, conn);
            Integer count  = nm.get(c);
            if (count == null) {
                count = 0;
            }
            nm.put(c,++count);
        asynBind(c,intent,conn);
        try {
            synchronized (this) {
                Log.i(TAG, "bindPlayer: sycronized block in ");
                this.wait();
                Log.i(TAG, "bindPlayer: sychronized block out");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "bindPlayer: context "+c+" to_handler "+to_handler);
    }

    private void asynBind(Context c,Intent in,ServiceConnection cn) {
        final Context c1 = c;
        final Intent i = in;
        final ServiceConnection cn1 = cn;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            c1.bindService(i,cn1,Context.BIND_AUTO_CREATE);
            }
        };
        new Thread(runnable).start();
    }

    public void unBindPlayer(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null ,unbindplayer is failed");
        }
            ServiceConnection conn = connectionMap.get(context);
        if(conn == null)
            return;
        context.unbindService(conn);
        Integer count = nm.get(context);
        nm.put(context,--count);
        if(count == 0) {
            connectionMap.remove(context);
        }
        Log.i(TAG, "unBindPlayer: unbind !");
    }



}
