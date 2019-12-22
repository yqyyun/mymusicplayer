package com.mmp.mmp.util;

import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.os.Environment;
import android.util.Log;

import com.mmp.mmp.bean.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMusicPlayer implements MultiPlayer {


    public String TAG = "MyMusicPlayer";
    private boolean isStoped = true;


    private MediaPlayer mediaPlayer;

    private MusicList list;
    private String current;
    private int duration;

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        if (listener != null) {
            mediaPlayer.setOnCompletionListener(listener);
        }
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        if (listener != null) {
        mediaPlayer.setOnSeekCompleteListener(listener);
        }
    }



    public MyMusicPlayer() {
        mediaPlayer = new MediaPlayer();
        list = new MusicList();
    }


    public MyMusicPlayer(List<Music> list) {
        this();
        this.list.putAll(list);

    }

    @Override
    public List<String> getList() {
        return list.getList();
    }

    @Override
    public int getPosition() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void seekTo(int position) {
        if (mediaPlayer != null && (position >=0 || position < duration)) {
            mediaPlayer.seekTo(position);
        }
    }

    @Override
    public void playLast() {
        current = list.previous();
//        start();
    }

    @Override
    public void playNext() {
        current = list.next();
//        start();
    }

    @Override
    public String current() {
        current = list.current();
        return current;
    }


    @Override
    public void setSource(Object source) {
        list.putAll((List<Music>) source);
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null)
            return mediaPlayer.isPlaying();
        return false;
    }

    @Override
    public boolean isStoped() {
        return isStoped;
    }

    @Override
    public void start() {
        current = list.current();
        Log.i(TAG, "start: current = "+current);
        if (mediaPlayer != null && current != null) {
            try {
                mediaPlayer.reset();
          /*      try {
                    Log.i("sleep" ,"start:sleeping -------- ");
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                mediaPlayer.setDataSource(current);
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration();
                synchronized (this) {
                    mediaPlayer.start();
                    isStoped = false;
                }
                Log.i(TAG, "start: isplaying ---"+current);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "start: 播放出现异常。");
            }
        }

    }
    @Override
    public int currentIndex(){
        return list.currentIncex();
    };

    @Override
    public void play(int i) {
        current = list.get(i);
    }

    @Override
    public void startOrPause() {
        if (mediaPlayer == null) return;
        if(isStoped){
            this.start();
        }else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            synchronized (this) {
                mediaPlayer.stop();
                isStoped = true;
            }
            mediaPlayer.release();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            if (list != null) {
            list.clear();
            list=null;
            }
            current = null;
            isStoped = true;
        }
    }

    class MusicList {
        private final List<Music> musicList = new ArrayList<>();
        private int[] currList = new int[0];
        private final Map<Integer, String> map = new HashMap<>();
        private int size = 0;

        private int batch = 8;

        public MusicList() {

        }

        public MusicList(List<Music> list) {
            init(list);

        }

        public boolean put(Music music) {
            boolean a = musicList.add(music);
            if (a) {
                if (music != null) {
                    int id = music.getId();
                    currList[size++] = id;
                    map.put(id, music.getAbspath());
                    return true;
                }
            }
            return false;
        }


        public void trimToSize() {
            if (size > 0)
                currList = Arrays.copyOf(currList, size);
        }

        public boolean putAll(List<Music> list) {
            int s = size;
            init(list);
            if (s != size) {
                return true;
            }
            return false;
        }
        public List<String> getList() {
            int size = this.size;
            List<String> list = new ArrayList<>(size);
            int[] ml = this.currList;
            for (int i = 0; i < size; i++) {
                int id = ml[i];
                list.add(map.get(id));
            }
            return list;
        }

        private void init(List<Music> list) {
            musicList.addAll(list);

            System.out.println("musicList =====  "+musicList);

            int[] a = currList;

            int l = musicList.size();
            int al = a.length;
            int re = al - size;

            if (l >= re) {
                if ((l >= al + batch)) {
                    a = Arrays.copyOf(a, l);
                } else {
                    a = Arrays.copyOf(a, al + batch);
                }
            }

            for (int i = 0; i < l; i++) {
                Music m = musicList.get(i);
                if (m != null) {
                    int id = m.getId();
                    a[i] = id;
                    map.put(id, m.getAbspath());
                    size++;
                }
            }
            if(size>0)pos = 0;
            currList = a;
            System.out.println("map ==== "+map);
            System.out.println(" size ==== " + size+"  pos ===== "+pos);
        }


        private int pos = -1;

        public Music currentMusic() {
            return getMusic(pos);
        }

        public String get(int pos) {
            if (pos < 0 || pos > size-1) {
                throw new IndexOutOfBoundsException();
            }
            final int id = currList[pos];
            for (Music music : musicList) {
                if (music != null && music.getId() == id) {
                    this.pos = pos;
                    return map.get(id);
                }
            }
            return null;
        }

        public Music getMusic(int pos) {
            if (pos < 0 || pos > size) {
                throw new IndexOutOfBoundsException();
            }
            final int id = currList[pos];
            for (Music music : musicList) {
                if (music != null && music.getId() == id) {
                    return music;
                }
            }
            return null;
        }


        public String current() {
            if(pos!=-1)
                return map.get(currList[pos]);
            return null;
        }

        public String next() {
            if (size > 1) {
                pos = (pos + 1) % size;
                return map.get(currList[pos]);
            } else {
                return current();
            }
        }

        public int currentIncex() {
            return pos;
        }

        public Music nextMusic() {
            if (size > 1) {
                pos = (pos + 1) % size;
                return getMusic(pos);
            } else {
                return currentMusic();
            }
        }

        public String previous() {
            if (size > 1) {
                pos--;
                if (pos < 0) {
                    pos = size - 1;
                }
                return map.get(currList[pos]);
            } else {
                return current();
            }

        }

        public Music previousMusic() {
            if (size > 1) {
                pos--;
                if (pos < 0) {
                    pos = size - 1;
                }
                return getMusic(pos);
            } else {
                return currentMusic();
            }

        }

        public boolean remove(int i) {
            if (i < 0 || i > size) {
                return false;
            }
            int id = currList[i];
            for (Music music : musicList) {
                if (music != null && music.getId() == id) {
                    musicList.remove(music);
                    map.remove(id);
                    System.arraycopy(currList, i + 1, currList, i, size - i - 1);
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            pos = -1;
            size = 0;
            musicList.clear();
            map.clear();
            currList = new int[0];

        }


    }


}
