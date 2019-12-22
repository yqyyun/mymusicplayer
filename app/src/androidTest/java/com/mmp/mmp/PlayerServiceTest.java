package com.mmp.mmp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mmp.mmp.bean.Music;
import com.mmp.mmp.service.PlayService;
import com.mmp.mmp.util.MultiPlayer;
import com.mmp.mmp.searcher.SearchForMusic;
import com.mmp.mmp.searcher.Searcher;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PlayerServiceTest {
    @Test
    public  void test() {
        Context context = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(context, PlayService.class);
        intent.putExtra("from", "test");
        context.startService(intent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayService.MyBinder myBinder = ((PlayService.MyBinder) service);
                MultiPlayer p = myBinder.getMutiPlayer();
                Searcher s = new SearchForMusic(Environment.getExternalStorageDirectory());
                List<String> list = s.search();
                List<Music> ls = new ArrayList<>();
                for (int i = 0; i<list.size();i++) {
                    Music m = new Music();
                    m.setId(i);
                    m.setAbspath(list.get(i));
                    ls.add(m);
                }
                p.setSource(ls);
                p.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE);
    }
}
