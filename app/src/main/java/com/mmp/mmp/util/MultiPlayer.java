package com.mmp.mmp.util;

import java.util.List;

public interface MultiPlayer extends Player {
    /**
     *播放上一个
     */
    void playLast();

    /**
     * 播放下一个
     */
    void playNext();

    List<String> getList();

    /**
     * 指定位置。
     * @param i
     */
    void play(int i);

    int currentIndex();
}
