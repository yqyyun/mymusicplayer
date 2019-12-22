package com.mmp.mmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmp.mmp.fragment.LocallistFragment;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.service.PlayService;

public class BootActivity extends AppCompatActivity {

    public final String TAG = "BootActivity";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private SharedPreferences connfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connfig = getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean firstBoot = connfig.getBoolean("firstBooted",false);
        boolean booted = connfig.getBoolean("booted",false);

        Log.i(TAG, "onCreate: booted "+ booted);

        if(booted) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playbar",true);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.boot_layout);
            ImageView boot_view = findViewById(R.id.boot_view);
            final ImageView boot_btn = findViewById(R.id.boot_btn);
            Intent sintent = new Intent(this, PlayService.class);
            startService(sintent);
            final TextView countDown = findViewById(R.id.boot_count_down);
     /*       Thread cd = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });*/
            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countDown.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    countDown.setVisibility(View.INVISIBLE);
                    boot_btn.setVisibility(View.VISIBLE);
                }
            }.start();
            boot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(BootActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            });
        }
    }
}
