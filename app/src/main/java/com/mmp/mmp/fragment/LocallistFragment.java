package com.mmp.mmp.fragment;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mmp.mmp.PlayerActivity;
import com.mmp.mmp.R;
import com.mmp.mmp.bean.Music;
import com.mmp.mmp.message.HandlerMusic;
import com.mmp.mmp.service.PlayService;

import java.util.ArrayList;
import java.util.List;

public class LocallistFragment extends Fragment {

    private final  String TAG = "LocalListFragment";

    private HandlerMusic handlerMusic = HandlerMusic.getInstance();
    private FragmentActivity activity;

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach: ");
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container,false);
        ListView lv = view.findViewById(R.id.list_frag);
        List<Music> ls = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
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

        activity =getActivity();

        ListViewAdapter ad = new ListViewAdapter(getContext(), ls);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(itemListener);
        System.out.println(getActivity());
        System.out.println(PlayService.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
        handlerMusic.bindPlayer(activity,PlayService.class);
        activity.startService(new Intent(activity,PlayService.class));
            }
        }).start();

        Log.i(TAG, "onCreateView: ");
        return view;
    }


    int lastposition = -1;
    private ListView.OnItemClickListener itemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            long time = System.currentTimeMillis();
            if (lastposition == position) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    startActivity(intent);
            }else {
                lastposition = position;
                handlerMusic.playOrPause(position);
            }
            /*MotionEvent motionEvent = MotionEvent.obtain(System.currentTimeMillis(),time,MotionEvent.ACTION_UP,0,0,0);
            view.dispatchTouchEvent(motionEvent);*/
            Log.i(TAG, "onItemClick: "+position+","+id);
        }
    };

    @Override
    public void onDestroyView() {
        handlerMusic.unBindPlayer(activity);
        Log.i(TAG, "onDestroyView: unBindPlayer ");
        super.onDestroyView();
    }
}
