package com.mmp.mmp.message;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mmp.mmp.exception.NotSupportHandlerMessageException;
import com.mmp.mmp.service.PlayService;
import com.mmp.mmp.util.MultiPlayer;
import com.mmp.mmp.util.Player;

public class HandlerMessage {
    public final String TAG = "HandlerMessage";

    private Handler to_handler;
    private Handler from_handler;
    private  Context context;
    private ServiceConnection conn;


    private void checkAcess() {
        if (to_handler == null || context == null) {
            throw new IllegalStateException(TAG+" handler and context is not set,please bind a player first !");
        }
    }

    public void bindPlayer(Context c,Player player) throws NotSupportHandlerMessageException {
        if (context == null || player == null) {
            throw new NullPointerException("context or player is null,bind player is faild");
        }
        if(!(player instanceof  Handlerable)){
            throw new NotSupportHandlerMessageException("the specified palyer is not support for handler message！");
        }
        if(player instanceof Service) {
            Intent intent = new Intent(c, player.getClass());
            context.startService(intent);
        }
        to_handler= ((Handlerable) player).getHandler();
    }

    public void unBindPlayer(Context context, Player player) {
        if(conn == null || this.context != context) {
            return;
        }
        if (context == null) {
            throw new NullPointerException("context is null ,unbindplayer is failed");
        }
        this.context.stopService(new Intent());
        this.conn =null;
        this.context = null;
    }
}
