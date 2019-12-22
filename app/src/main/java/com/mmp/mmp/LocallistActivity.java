package com.mmp.mmp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mmp.mmp.fragment.LocallistFragment;
import com.mmp.mmp.fragment.PlayBarFragment;
import com.mmp.mmp.fragment.WaitProgressFragment;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.reciever.PlaybarReciever;

public class LocallistActivity extends FragmentActivity {

    public final String TAG = "LocallistActivity";
    private FragmentManager fm;
    private Handler handler;
    private LocallistFragment locallistFragment;

    private View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.local_btn_back:
                    finish();
                    break;
                case R.id.local_btn_menu:
                    handler.sendEmptyMessage(3);
                    break;
            }
        }
    };

    private SharedPreferences spf;
    private ImageView btn_back;
    private ImageView btn_menu;
    private PlayBarFragment playBarFragment;
    private LocalReciever reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locallist_layout);
        btn_back = findViewById(R.id.local_btn_back);
        btn_menu = findViewById(R.id.local_btn_menu);
        btn_back.setOnClickListener(listener);
        btn_menu.setOnClickListener(listener);

//        findViewById(R.id)

        locallistFragment = new LocallistFragment();
        playBarFragment = new PlayBarFragment();

        reciever = new LocalReciever();
        String action = "com.mmp.music.status_changed";
        IntentFilter filter = new IntentFilter(action);
        registerReceiver(reciever,filter);

        //SharedPreferences
        spf = getSharedPreferences("locallist", Context.MODE_PRIVATE);

        boolean canLoaded = spf.getBoolean("canLoaded",false);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(canLoaded){
            ft.add(R.id.local_list_linerl,locallistFragment);
        }else  {
            WaitProgressFragment waitProgressFragment = new WaitProgressFragment();
            ft.add(R.id.local_list_linerl, waitProgressFragment);
        }
        ft.commit();
        this.getHandler();
    }




    @Override
    protected void onStart() {
        Intent intent = getIntent();
        boolean playbar = intent.getBooleanExtra("playbar", false);
        if(playbar){
            handler.sendEmptyMessage(2);
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(reciever);
        super.onDestroy();
    }

    public Handler getHandler() {
        synchronized (this) {
            if(handler == null) {
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case 1://检索完毕
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.local_list_linerl, locallistFragment);
                                ft.commit();

                                SharedPreferences.Editor edit = spf.edit();
                                edit.putBoolean("canLoaded",true);
                                edit.commit();
                                edit.clear();

                                break;
                            case 2:
                                if (!addedbar) {
                                    Log.i(TAG, "handleMessage: beginTransaction to add playbar fragment");
                                    FragmentTransaction ft1 = fm.beginTransaction();
                                    ft1.add(R.id.local_list_fram, playBarFragment);
//                                ft1.replace(R.id.local_list_linerl,playBarFragment);

                                    ft1.commit();
                                    addedbar=true;
                                    setResult(1);
                                    Log.i(TAG, "handleMessage: successeed to add playbar fragment");
                                }
                                break;
                            case 3:
                                FragmentTransaction ft3 = fm.beginTransaction();
                                WaitProgressFragment waitProgressFragment = new WaitProgressFragment();
                                ft3.replace(R.id.local_list_linerl,waitProgressFragment);
                                ft3.commit();

                        }
                    }
                };
            }
        }
        return handler;
    }

    private boolean addedbar = false;

    //广播接收者
    class LocalReciever extends BroadcastReceiver {
        public final String TAG = "LocalReciever";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: has reciever a broadcast ,is doing ...");
            if (handler != null) {
                handler.sendEmptyMessage(2);
            }
            Log.i(TAG, "onReceive: broadcast has been donw");
        }
    }

}
