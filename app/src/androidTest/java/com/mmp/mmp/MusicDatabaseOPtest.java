package com.mmp.mmp;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mmp.mmp.dao.MusicDatabaseOperator;
import com.mmp.mmp.searcher.SearchForMusic;
import com.mmp.mmp.searcher.Searcher;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MusicDatabaseOPtest {
    @Test
    public void batchInsert() {
        Context context = InstrumentationRegistry.getTargetContext();
        MusicDatabaseOperator mo = new MusicDatabaseOperator(context);
        Searcher s = new SearchForMusic(Environment.getExternalStorageDirectory());
        List<String> list = s.search();
        mo.batchInsert(list);
    }
}
