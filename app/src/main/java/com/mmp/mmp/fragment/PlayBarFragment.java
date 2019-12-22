package com.mmp.mmp.fragment;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mmp.mmp.PlayerActivity;
import com.mmp.mmp.R;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.service.PlayService;
import com.mmp.mmp.util.PlayTimer;

import java.util.List;

public class PlayBarFragment extends Fragment {

    private  int  MAX_PROGRESS = 1000;
    public final String TAG = "PlayBarFragment";

    private ImageView album;
    private TextView title;
    private TextView artist;
    private ImageView play;
    private ProgressBar progressbar;
    private HandlerMusic handlerMusic;
    private int duration;
    private int time_progress;
    private boolean isplaying=false;
    private boolean isstoped=true;

    private int[] dd ={R.drawable.akl,
            R.drawable.akm,
            R.drawable.akn,
            R.drawable.ako};
    private int di = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            Bundle data = msg.getData();
            switch (msg.what) {
                case HandlerMusic.RESPONSE_MUSIC_DURATION:
                    duration = data.getInt("duration");
                    Log.i(TAG, "handleMessage: duration "+ duration);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_PROGRESS:
                    int progress = data.getInt("progress");
                    if(duration !=0) {
                        time_progress =progress;
                        int p = (progress* MAX_PROGRESS)/ duration;
                        progressbar.setProgress(p);
                    }
                    Log.i(TAG, "handleMessage: progress "+progress);
                    break;
                case HandlerMusic.RESPONSE_MUSIC_SELF:
                    String path = (String)data.get("self");
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    System.out.println(path);
                    String  ti = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String al = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String ar = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    al = (al!=null)?al:"unknow";
                    ar = (ar!=null)?ar:"unknow";
                    if(ti==null) {
                        int f = path.lastIndexOf('/');
                        int l = path.lastIndexOf('.');
                        ti = path.substring(f != -1 ? f + 1 : 0, l);
                    }

                    title.setText(ti);
                    artist.setText((ar!=null)?ar:"");
                    byte[] embeddedPicture = retriever.getEmbeddedPicture();
                    if (embeddedPicture != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
                        album.setImageBitmap(bitmap);
                    }else{
                        album.setImageResource(R.drawable.z3);
                    }
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
                    Log.i(TAG, "handleMessage: music state! isplaying "+ isplaying +" isstoped "+ isstoped);
            }
            super.handleMessage(msg);
        }
    };
    private ProgressUpdateTask progressUpdateTask;
    private FragmentActivity activity;
    private PlaybarReciever reciever;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.palybar_fag, container, false);
        album = view.findViewById(R.id.playbar_album);
        title = view.findViewById(R.id.playbar_title);
        artist = view.findViewById(R.id.playbar_artist);
        play = view.findViewById(R.id.playbar_play);
        progressbar= view.findViewById(R.id.playbar_progress);
        progressbar.setMax(MAX_PROGRESS);

        handlerMusic = HandlerMusic.getInstance();


        activity = getActivity();
//        handlerMusic.setResultHandler(handler);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerMusic.bindPlayer(activity, PlayService.class);
                binded = true;
                handlerMusic.requestState(handler);
                handlerMusic.requestDuration(handler);
                handlerMusic.requestProgress(handler);
                handlerMusic.requestMusic(handler);

                IntentFilter intentFilter = new IntentFilter("com.mmp.music.status_changed");
                reciever = new PlaybarReciever();
                activity.registerReceiver(reciever,intentFilter);

            }
        }).start();

        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isplaying) {
                    di = 0;
                }else {
                    di = 2;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    play.setImageResource(dd[(di+1)]);
                    Log.i(TAG, "onTouch: down");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    play.setImageResource(dd[(di+2)%4]);
                    handlerMusic.playOrPause();
                    Log.i(TAG, "onTouch: up, and  has send a message to play or pause");
                }
                Log.i(TAG, "onTouch: ");

                return true;//此处应该返回true，否者up事件不会发生。准确的说，是up在别处被处理了。
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, PlayerActivity.class);
                startActivity(i);
            }
        });

        Log.i(TAG, "onCreateView: PlayBar view has been created !");
        return view;
    }

    boolean binded = false;

    @Override
    public void onStart() {
        progressUpdateTask = new ProgressUpdateTask();
        progressUpdateTask.execute();
        if(binded)
            handlerMusic.requestProgress(handler);
        super.onStart();
    }

    @Override
    public void onStop() {
        if (progressUpdateTask != null && progressUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            progressUpdateTask.cancel(true);
            progressUpdateTask = null;
        }
        handlerMusic.unBindPlayer(activity);
        Log.i(TAG, "onStop: update has canceled");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        activity.unregisterReceiver(reciever);

        Log.i(TAG, "onDestroyView: Playbar view has been destroyed !");
        super.onDestroyView();
    }


    private class ProgressUpdateTask extends AsyncTask<Void,Object,String> {

        @Override
        protected String doInBackground(Void[] objects) {
            while(true) {
                if (isCancelled()) {
                    break;
                }
                if (isplaying && duration != 0) {
                    time_progress += 1000;
                    int progress =  time_progress * MAX_PROGRESS / duration;
                    publishProgress(progress);
//                    Log.i(TAG, "run: progress has been set");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            progressbar.setProgress(progress);
        }
    }

    class PlaybarReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            handlerMusic.requestState(handler);
            handlerMusic.requestDuration(handler);
            handlerMusic.requestProgress(handler);
            handlerMusic.requestMusic(handler);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
