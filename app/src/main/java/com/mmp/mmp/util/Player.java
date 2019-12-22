package com.mmp.mmp.util;

import android.media.MediaPlayer;

/**
 * 该接口是一个播放器接口，定义了播放器所必须有的行为和属性。
 */
public interface Player {

    /**
     *
     * @return 当前文件名。
     */
    String current();

    /**
     *
     * @return 媒体文件当前播放进度。
     */
    int getPosition();

    /**
     *
     * @return 媒体文件的时长
     */
    int getDuration();


    void seekTo(int position);

    /**
     *
     * @param source 指定的要播放的文件绝对路径。
     */

    void setSource(Object source);

    boolean isPlaying();

    boolean isStoped();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    void startOrPause();

    void release();

    void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener);

    void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);

}
