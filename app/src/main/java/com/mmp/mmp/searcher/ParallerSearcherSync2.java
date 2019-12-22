package com.mmp.mmp.searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 搜索器的并发版本,search方法会阻塞
 * 相比于parallerSearcherSync的改进是，不需要等待线程资
 * 源都释放，就可获取结果
 */
public class ParallerSearcherSync2 extends AbstractSearcher<String> {

    private  ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*30);
    public ParallerSearcherSync2() {
    }

    public ParallerSearcherSync2(boolean filterHidden, String[] filterDirs, String rootDir, String[] fileSuffixes) {
        super(filterHidden, filterDirs, rootDir, fileSuffixes);
    }

    public ParallerSearcherSync2(boolean filterHidden, String rootDir) {
        super(filterHidden, rootDir);
    }

    public ParallerSearcherSync2(boolean filterHidden, String rootDir, String[] fileSuffixes) {
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

        System.out.println("==============cpu核心数============="+Runtime.getRuntime().availableProcessors());
        List<String> list = new ArrayList<>();
        ((ThreadPoolExecutor) threadPool).prestartAllCoreThreads();
        doResearch(threadPool,root, list);//该方法阻塞，返回后，任务已经执行完毕，需要关闭线程
        closeThreadPool();
        System.out.println("================"+list);
        System.out.println("+++++++++++++++++++++"+threadPool.isTerminated()+","+threadPool.isShutdown());
        return list;
    }

    private void closeThreadPool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!threadPool.isShutdown()) {
                    threadPool.shutdown();
                    int count = 0;//重试阈值，超过此阈值，强制关闭
                    while(!threadPool.isTerminated() && count<10){
                        try {
                            count++;
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    threadPool.shutdownNow();
                }
            }
        }).start();
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
