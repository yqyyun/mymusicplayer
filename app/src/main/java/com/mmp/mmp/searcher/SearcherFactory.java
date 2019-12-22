package com.mmp.mmp.searcher;

import android.os.Environment;

import java.io.File;

public class SearcherFactory {

    /**
     * 返回一个搜索器对象
     * @param filterHidden 是否过滤隐藏目录
     * @param filterDirs 过滤目录的正则表达式
     * @param roodDir 搜索的根目录
     * @param fileSuffixes 文件后缀名
     * @param paralle 是否需要并发
     * @return
     */
    public static Searcher<String> getSyncSearcherInstance(boolean filterHidden, String [] filterDirs,
                                                           String roodDir, String[] fileSuffixes, boolean paralle){
        if(paralle){
            return new ParallerSearcherSync2(filterHidden,filterDirs,roodDir,fileSuffixes);
        }else{
        return new SearcherSync(filterHidden,filterDirs,roodDir,fileSuffixes);
        }
    }

    /**
     *返回一个默认的搜索器对象，
     * 搜索的音乐格式有Mp3,flac,wav
     * 过滤隐藏目录，从根目录开始搜索
     * @param paraller 是否需要并发
     * @return
     */
    public static Searcher<String> getDefaultMusicSyncSearcher(boolean paraller){
        File root = Environment.getExternalStorageDirectory();
        System.out.println(root+" ========================================");
        String rootDir = root.getAbsolutePath();
        String[] fileSuffixes = new String[]{Constants.MUSIC_TYPE_WAV,Constants.MUSIC_TYPE_MP3,Constants.MUSIC_TYPE_FLAC};
        if(paraller){
            return new ParallerSearcherSync2(true,null,rootDir,fileSuffixes);
        }else{
            return new SearcherSync(true,null,rootDir,fileSuffixes);
        }
    }
}
