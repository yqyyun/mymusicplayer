package com.mmp.mmp.searcher;

import com.mmp.mmp.searcher.Searcher;

/**
 *定义文件搜索器的抽象父类
 * @param <T>
 */
public abstract class AbstractSearcher<T> implements Searcher<T> {

    /**
     * 是否过滤隐藏目录，默认过滤
     */
     protected boolean filterHidden = true;

    /**
     * 过滤目录列表
     */
     protected String[] filterDirs ;

    /**
     * 搜索文件后缀名列表
     */
     protected String[] fileSuffixes ;

    /**
     * 搜索根目录
     */
     protected String rootDir;

    public AbstractSearcher(){
    }

    /**
     * 目录可以是正则表达式
     * @param filterHidden
     * @param filterDirs
     * @param rootDir
     * @param fileSuffixes
     */
    public AbstractSearcher(boolean filterHidden, String[] filterDirs,String rootDir,String[] fileSuffixes) {
        this.filterHidden = filterHidden;
        this.filterDirs = filterDirs;
        this.rootDir = rootDir;
        this.fileSuffixes = fileSuffixes;
    }

    public AbstractSearcher(boolean filterHidden,String rootDir) {
        this(filterHidden,null,rootDir,null);
    }
    public  AbstractSearcher(boolean filterHidden,String rootDir, String[] fileSuffixes){
        this(filterHidden,null,rootDir,fileSuffixes);
    }

}
