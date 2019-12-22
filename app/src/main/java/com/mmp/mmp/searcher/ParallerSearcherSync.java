package com.mmp.mmp.searcher;

import com.mmp.mmp.searcher.AbstractSearcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件搜索器的多线程版本，会自动开启多个线程进行搜索，仍会阻塞
 */
public class ParallerSearcherSync extends AbstractSearcher<String> {
    public ParallerSearcherSync() {
    }

    public ParallerSearcherSync(boolean filterHidden, String[] filterDirs, String rootDir, String[] fileSuffixes) {
        super(filterHidden, filterDirs, rootDir, fileSuffixes);
    }

    public ParallerSearcherSync(boolean filterHidden, String rootDir) {
        super(filterHidden, rootDir);
    }

    public ParallerSearcherSync(boolean filterHidden, String rootDir, String[] fileSuffixes) {
        super(filterHidden, rootDir, fileSuffixes);
    }

    @Override
    public List<String> search() {
//        ArrayList<String> list = new ArrayList<String>();
        final File root = new File(this.rootDir);
        final boolean filterHidden = this.filterHidden;
        final String[] filterDirs = this.filterDirs;
        final String[] fileSuffixes = this.fileSuffixes;
        //此处参数调整不当，容易引发死锁，根据本次情况来说，线程数量，应该越多越好
//        ExecutorService threadPool = new ThreadPoolExecutor(20,20,0,
//                            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//        ExecutorService threadPool = Executors.newCachedThreadPool();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);
        List<String> list = new ArrayList<>();
        ((ThreadPoolExecutor) threadPool).prestartAllCoreThreads();
        doResearch(threadPool,root, list);
        if (!threadPool.isShutdown()) {
            threadPool.shutdown();
            while(!threadPool.isTerminated()){
                ;
            }
        }
        System.out.println("================"+list);
        System.out.println("+++++++++++++++++++++"+threadPool.isTerminated()+","+threadPool.isShutdown());
        return list;
    }
    private void doResearch(final ExecutorService executorService,final File file,final List<String> list) {
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (filterHidden && file.isHidden()) {
                    return null;
                } else {
                    if (file.isFile()) {
                        int len = fileSuffixes.length;
                        if (fileSuffixes != null && len > 0) {
                            String name = file.getName();
                            for (int i = 0; i < len; i++) {
                                if (name.endsWith(fileSuffixes[i])) {
//                                    list.add(file.getAbsolutePath());
                                    System.out.println("=============in the searching .....===="+list);
                                    return file.getAbsolutePath();
                                }
                            }
                        }
                    } else if (file.isDirectory()) {
                        if (filterDirs != null) {
                            int len = filterDirs.length;
                            if (len > 0) {
                                for (int i = 0; i < len; i++) {
                                    if (file.getAbsolutePath().matches(filterDirs[i])) {
                                        return null;
                                    }
                                }
                            }
                        }else {
                                File[] files = file.listFiles();
                                int n = files.length;
                                for (int i = 0; i < n; i++) {
                                    doResearch(executorService, files[i],list);
                                }
                            }
                        }
                    }
                return null;
            }
        });
        try {
            String res = future.get();
            if(res != null){
                list.add(res);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
