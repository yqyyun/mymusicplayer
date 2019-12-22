package com.mmp.mmp;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.mmp.mmp.bean.Music;
import com.mmp.mmp.util.MyMusicPlayer;
import com.mmp.mmp.searcher.SearchForMusic;
import com.mmp.mmp.searcher.Searcher;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MyMusicPlayerTest {
    @Test
    public void start() {
        MyMusicPlayer mp = new MyMusicPlayer();
        Searcher s = new SearchForMusic(Environment.getExternalStorageDirectory());
        List<String> list = s.search();
        List<Music> ls = new ArrayList<>();
        for (int i = 0; i<list.size();i++) {
            Music m = new Music();
            m.setId(i);
            m.setAbspath(list.get(i));
            ls.add(m);
        }
        for (Music l : ls) {
            System.out.println(l);
        }
        mp.setSource(ls);
        System.out.println(mp.current());
        mp.start();
        mp.playLast();
        mp.playNext();
    }

}
