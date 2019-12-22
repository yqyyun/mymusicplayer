package com.mmp.mmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mmp.mmp.fragment.PlayBarFragment;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.service.PlayService;

import java.io.Serializable;

public class MainActivity extends FragmentActivity {

    final private String TAG="MainActivity";
    private PlayBarFragment myPlayBar;
    private FragmentManager fm;

    boolean addedbar = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HandlerMusic.RESPONSE_MUSIC_STATE) {
                Bundle data = msg.getData();
                boolean isstoped = data.getBoolean("isstoped");
                if(!addedbar && !isstoped){
                    myPlayBar = new PlayBarFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.main_playbar, myPlayBar);
                    ft.commit();
                    addedbar = true;
                }
            }
            super.handleMessage(msg);
        }
    };
    private HandlerMusic handlerMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        LinearLayout local = findViewById(R.id.layout_lcoal);
        LinearLayout recent = findViewById(R.id.layout_recent);
        LinearLayout download = findViewById(R.id.layout_download);

        handlerMusic = HandlerMusic.getInstance();
        fm = getSupportFragmentManager();


        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LocallistActivity.class);
                intent.putExtra("playbar",addedbar);
                startActivityForResult(intent,1);
                Log.i(TAG, "onClick: local has been clicked .");
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: recent has been clicked .");
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: download has been clicked .");

            }
        });
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();
        boolean playbar = intent.getBooleanExtra("playbar", false);
        if(playbar) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        handlerMusic.bindPlayer(MainActivity.this, PlayService.class);
                        handlerMusic.requestState(handler);
                    }
                };
                new Thread(r).start();
        }
        Log.i(TAG, "onStart: ");

    super.onStart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(resultCode == 1 ){
                    handlerMusic.requestState(handler);
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
