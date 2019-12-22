package com.mmp.mmp;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.mmp.mmp.searcher.Searcher;
import com.mmp.mmp.searcher.SearcherFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchForMusicTest {

    @Test
    public void search() {
        File dir = Environment.getExternalStorageDirectory();
//        dir = "/mnt/sdcard/";
        Searcher  searcher = SearcherFactory.getDefaultMusicSyncSearcher(false);
        System.out.println(dir);
        List<String> list = searcher.search();
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String s : list) {
            System.out.println(s);
        }


    }
}
