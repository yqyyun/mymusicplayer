package com.mmp.mmp.searcher;

import com.mmp.mmp.bean.Music;

import java.util.List;

/**
 * 该类是搜索器
 * @param <T>
 */
public interface Searcher<T> {
    /**
     * 返回一个指定的列表。
     * 一般来讲，该方法属于耗时操作（具体取决于用户的实现）。
     * 因此，建议放在一个单独的线程中使用。
     * @return
     */
    public List<T> search();
}
