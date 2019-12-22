package com.mmp.mmp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mmp.mmp.LocallistActivity;
import com.mmp.mmp.R;
import com.mmp.mmp.dao.MusicDatabaseOperator;
import com.mmp.mmp.searcher.Searcher;
import com.mmp.mmp.searcher.SearcherFactory;

import java.util.List;

public class WaitProgressFragment extends Fragment {
    private Context context;
    private Searcher<String> searcher;
    private volatile Handler handler;
    private ProgressBar pro;
    private Button btn;
    private LocallistActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.waitprogress_item, container, false);
        pro = view.findViewById(R.id.progress_scan);
        btn = view.findViewById(R.id.btn_scan);
        context= getContext();
        activity = ((LocallistActivity) getActivity());
        handler = this.getHandler();

//        searcher= new SearchForMusic(Environment.getExternalStorageDirectory());
        //获取一个搜索器
        searcher= SearcherFactory.getDefaultMusicSyncSearcher(false);//在xiaomi4上测试,单线程版本的要快得多

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.INVISIBLE);
                pro.setVisibility(View.VISIBLE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        List<String> list = searcher.search();
                        long end = System.currentTimeMillis();
                        MusicDatabaseOperator mdo = new MusicDatabaseOperator(context);
                        System.out.println("1235465798=========="+Thread.currentThread().getName()+",size:"+list.size());
                        mdo.batchInsert(list);
                        WaitProgressFragment.this.getHandler().sendEmptyMessage(1);
                        System.out.println("================总计耗时============"+(end-start));
                    }
                });
                thread.start();
            }
        });

        return view;
    }

    /**
     * 单例的handler对象
     * @return
     */
    public Handler getHandler() {
        if(handler == null) {
            synchronized (Fragment.class) {
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Handler handler = activity.getHandler();
                            handler.sendEmptyMessage(1);
                        }
                    };
                }
            }
        }
        return handler;
    }
}
