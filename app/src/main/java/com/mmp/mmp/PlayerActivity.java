package com.mmp.mmp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.service.PlayService;
import com.mmp.mmp.util.PlayTimer;

import java.util.List;

public class PlayerActivity extends Activity {

    private PlayService.MyBinder myBinder;
    private HandlerMusic handlerMusic ;
    private ImageView playLast;
    private ImageView play;
    private ImageView playNext;
    private SeekBar seekBar;
    private ImageView album;
    private TextView timeRight;
    private TextView timeLeft;
    private ProgressUpdateTask progressUpdateTask;
    private Toolbar toolbar;

    private int duration=0;
    private int time_progress=0;

    private int MAX_PROGRESS = 1000;

    public final String TAG="PlayerActivity";


    private boolean isplaying=false;

    private boolean isstoped = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                case HandlerMusic.RESPONSE_MUSIC_DURATION:
                     duration = data.getInt("duration");
                     String tr = PlayTimer.timeTo(duration/1000);
                     timeRight.setText(tr);
                    Log.i(TAG, "handleMessage: duration "+duration);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_PROGRESS:
                    int progress = data.getInt("progress");
                    if(isplaying && duration!=0) {
                        time_progress=progress;
                        int p = (progress*MAX_PROGRESS)/duration;
                        String tl = PlayTimer.timeTo(progress/1000);
                        seekBar.setProgress(p);
                        timeLeft.setText(tl);
                    }
                    Log.i(TAG, "handleMessage: progress "+progress);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_LIST:
                    List<String> list = data.getStringArrayList("list");
                    Log.i(TAG, "handleMessage: list "+list);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_SELF:
                    String path = (String)data.get("self");
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    System.out.println(path);
                    String  title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String al = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String ar = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    al = (al!=null)?al:"unknow";
                    ar = (ar!=null)?ar:"unknow";
                    if(title==null) {
                        int f = path.lastIndexOf('/');
                        int l = path.lastIndexOf('.');
                        title = path.substring(f != -1 ? f + 1 : 0, l);
                    }
                    byte[] embeddedPicture = retriever.getEmbeddedPicture();
                    if (embeddedPicture != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
                        album.setImageBitmap(bitmap);
                        albumFg.setImageResource(R.drawable.ace);
                        album.setVisibility(View.VISIBLE);
                    }else{
                        album.setVisibility(View.INVISIBLE);
                        albumFg.setImageResource(R.drawable.beb);
                    }
                    toolbar.setTitle(title);
                    toolbar.setSubtitle(ar+" - "+al);
                    Log.i(TAG, "handleMessage: music self"+path);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_STATE:
                    isplaying = data.getBoolean("isplaying");
                    isstoped = data.getBoolean("isstoped");
                    if (isplaying) {
                        di = 0;
                    }else{
                        di = 2;
                    }
                    play.setImageResource(dd[(++di)%4]);
                    Log.i(TAG, "handleMessage: music state! isplaying "+isplaying+" isstoped "+isstoped);
            }
            super.handleMessage(msg);
        }
    };
    private ImageView albumFg;
    private Animation animation;
    private PlayReciever reciever;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        handlerMusic = HandlerMusic.getInstance();
        playLast = findViewById(R.id.imageButton);
        play = findViewById(R.id.imageButton2);
        playNext = findViewById(R.id.imageButton3);
        seekBar = findViewById(R.id.seekBar2);
        album = findViewById(R.id.player_layout_album);
        albumFg = findViewById(R.id.player_layout_album_fg);
        seekBar = findViewById(R.id.seekBar2);
        timeRight=findViewById(R.id.timer_right);
        timeLeft = findViewById(R.id.timer_left);

        animation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.album_rotate);

        album.startAnimation(animation);

        toolbar = (Toolbar)findViewById(R.id.toolbar_player_layout);
        toolbar.setNavigationIcon(R.drawable.z1);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     PlayerActivity.this.finish();
                                                 }
                                             });


        seekBar.setMax(MAX_PROGRESS);

        reciever = new PlayReciever();

        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerMusic.bindPlayer(PlayerActivity.this,PlayService.class);
                binded = true;
//                handlerMusic.setResultHandler(handler);
                handlerMusic.requestState(handler);
                handlerMusic.requestDuration(handler);
                handlerMusic.requestProgress(handler);
                handlerMusic.requestMusic(handler);

                IntentFilter intent = new IntentFilter("com.mmp.music.status_changed");
                PlayerActivity.this.registerReceiver(reciever,intent);
            }
        }).start();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
                if(handlerMusic != null && duration != 0) {

//                    handlerMusic.setResultHandler(handler);

                    int progress = s.getProgress();
                    System.out.println(" Seek progress " + progress + " duration "+ duration);
                    int p = progress *duration/MAX_PROGRESS;
                    handlerMusic.seekTo(p);
                    handlerMusic.requestProgress(handler);
                    Log.i(TAG, "onStopTrackingTouch: position "+p);
                }
            }
        });

        play.setOnTouchListener(touchListener);
        playLast.setOnTouchListener(touchListener);
        playNext.setOnTouchListener(touchListener);

        Log.i(TAG, "onCreate: ");
    }

    boolean binded = false;

    @Override
    protected void onStart() {
//        handlerMusic.requestMusic();
        if(binded)
            handlerMusic.requestProgress(handler);
        progressUpdateTask = new ProgressUpdateTask();

        progressUpdateTask.execute();
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (progressUpdateTask != null && progressUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            progressUpdateTask.cancel(true);
        }
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handlerMusic.unBindPlayer(this);
        unregisterReceiver(reciever);
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private int[] dd ={R.drawable.akl,
            R.drawable.akm,
            R.drawable.akn,
            R.drawable.ako};
    private int di = 0;
    private int lastdi=0;

    private int[] od = {R.drawable.ad9,R.drawable.ad_,R.drawable.adi,R.drawable.adj,R.drawable.adu,R.drawable.adv,
            R.drawable.adg,R.drawable.adh};//recycle,order,random,only;
    private int oi = 0;

    View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {


//            handlerMusic.setResultHandler(handler);
            int action = event.getAction();
            switch (v.getId()) {
                case R.id.imageButton:
                    if(action == MotionEvent.ACTION_DOWN){
                        playLast.setImageResource(R.drawable.akr);
                    }
                    if(action == MotionEvent.ACTION_UP){
                        playLast.setImageResource(R.drawable.akp);
                        play.setImageResource(dd[di=0]);
                        handlerMusic.playLast();
                        updateInfo();
                    }
                    break;
                case R.id.imageButton2:
                    if(isplaying){
                        di = 0;
                    }else {
                        di = 2;
                    }
                    if(action == MotionEvent.ACTION_DOWN){
                        play.setImageResource(dd[(di+1)%4]);
                    }
                    if(action == MotionEvent.ACTION_UP){
                        play.setImageResource(dd[(di+2)%4]);
                        handlerMusic.playOrPause();
                        isplaying=!isplaying;
                        isstoped=!isstoped;
                    }
                    break;
                case R.id.imageButton3:
                    if(action == MotionEvent.ACTION_DOWN){
                        playNext.setImageResource(R.drawable.akk);
                    }
                    if(action == MotionEvent.ACTION_UP){
                        playNext.setImageResource(R.drawable.akj);
                        play.setImageResource(dd[di=0]);
                        handlerMusic.playNext();
                        updateInfo();
                    }
            }
            return true;
        }
    };

    private void updateInfo() {
        handlerMusic.requestState(handler);
        handlerMusic.requestDuration(handler);
        handlerMusic.requestProgress(handler);
        handlerMusic.requestMusic(handler);
        isplaying = true;
        isstoped = false;
    }

    private class ProgressUpdateTask extends AsyncTask<Void,Object,String> {


        @Override
        protected String doInBackground(Void[] objects) {
            while(true) {
                    if (isCancelled()) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                if (isplaying && duration != 0) {
                    time_progress += 1000;
                    int progress =  time_progress * MAX_PROGRESS / duration;
                    String tl = PlayTimer.timeTo(time_progress/1000);
                    publishProgress(progress, tl);
//                    Log.i(TAG, "run: progress has been set");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            int progress = (Integer)values[0];
            String tl = (String)values[1];
            seekBar.setProgress(progress);
            timeLeft.setText(tl);
        }
    }

    class PlayReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateInfo();
        }
    }

}
