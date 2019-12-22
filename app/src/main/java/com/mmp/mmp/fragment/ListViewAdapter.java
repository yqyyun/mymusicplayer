package com.mmp.mmp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmp.mmp.R;
import com.mmp.mmp.bean.Music;
import com.mmp.mmp.message.HandlerMusic;

import java.util.List;


public class ListViewAdapter extends BaseAdapter {

    private List<Music> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ListViewAdapter(Context context, List<Music> data)
    {
        this.context=context;
        this.data=data;
        this.layoutInflater=layoutInflater.from(context);
    }
    public final class Items{
        public TextView music_name;
        public TextView music_album;
        public ImageView img_btn ;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Items items =  null;
        if(view == null)
        {
            items = new Items();
            view = layoutInflater.inflate(R.layout.listitems,null);
            items.music_name = (TextView)view.findViewById(R.id.music_name);
            items.music_album = (TextView)view.findViewById(R.id.music_album);
            items.img_btn = (ImageView)view.findViewById(R.id.img_btn);
            view.setTag(items);

        }
        else
        {
            items = (Items)view.getTag();
        }

        Music music = data.get(i);
        items.music_name.setText(music.getName());
        String artist = music.getArtist();
        String album = music.getAlbum();
        items.music_album.setText((artist==null?"unknow":artist)+" - "+ (album==null?"unknow":album));
        items.img_btn.setBackgroundResource(R.drawable.a3a);
        view.setOnTouchListener(listener);
        return view;
    }
    private View.OnTouchListener listener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
//                    v.setBackgroundColor(Color.DKGRAY);
//                    Log.i("Touch", "onTouch:down");
                }
                if(action == MotionEvent.ACTION_UP){
                    int rgb = Color.rgb(238, 240, 241);
//                    v.setBackgroundColor(rgb);
//                    Log.i("Touch", "onTouch:up ");
                    return false;
            }
            return false;
        }
    };
}
