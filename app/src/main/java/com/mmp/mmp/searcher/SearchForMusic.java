package com.mmp.mmp.searcher;

import com.mmp.mmp.searcher.Constants;
import com.mmp.mmp.searcher.Searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchForMusic implements Searcher {
    /**
     * 是否已经检索过
     */
    private boolean  flag = false;
    /**
     *返回结果列表
     */
    private ArrayList<String> arrayList;
    /**
     * 返回文件名列表
     */
    private String[] strArray;
    /**
     * 搜索的根目录
     */
    private File searforDir;
    /**
     * 搜索文件后缀
     */
    private String suffix;
    private String [] suffixs={Constants.MUSIC_TYPE_FLAC,
                            Constants.MUSIC_TYPE_MP3,
                            Constants.MUSIC_TYPE_WAV};
    /**
     * 文件的绝对路径列表
     */
    private String[] paths;

    public SearchForMusic(File dir) {
        arrayList = new ArrayList<String>();
        strArray = null;
        searforDir = dir;
        suffix = "";
        paths=null;
    }

    public SearchForMusic(String dir) {
        arrayList = new ArrayList<String>();
        strArray = null;
        searforDir = new File(dir);
        suffix = "";
        paths=null;
    }


    public SearchForMusic(String dir ,String type) {

        arrayList = new ArrayList<String>();
        strArray = null;
        searforDir = new File(dir);
        suffix =type;
        paths=null;
    }

    @Override
    public List search() {
        System.out.println("sarching .......");
        if(!flag){
            doRearch(searforDir);
            flag = true;
        }
        System.out.println("done ============");
        return arrayList;

    }

    private void doRearch(File file) {
        if(file.isFile()) {
            if(suffixs!=null) {
                for(String suffix:suffixs) {
                    if (file.getName().endsWith(suffix)) {
                        arrayList.add(file.getAbsolutePath());
                    }
                }
            }else {
                arrayList.add(file.getAbsolutePath());
            }
        }else {
            if(file.listFiles() != null) {
                for(File tempfile:file.listFiles()) {
                    doRearch(tempfile);
                }
            }
        }
    }
}
