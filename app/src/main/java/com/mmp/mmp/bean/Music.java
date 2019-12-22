package com.mmp.mmp.bean;

import java.util.Objects;

public class Music {
    /**
     * 数据库返回的id
     */
    private int id;
    /**
     * 数据库返回的name
     */
    private String name;
    /**
     * 数据库返回的专辑名
     */
    private String album;
    /**
     * 数据库返回的歌手名
     */
    private String artist;
    /**
     * 数据库返回的绝对路径
     */
    private String abspath;



    public Music(){}

    public Music(int id, String name, String album, String artist, String abspath) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.abspath = abspath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAbspath() {
        return abspath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAbspath(String abspath) {
        this.abspath = abspath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return id == music.id &&
                Objects.equals(name, music.name) &&
                Objects.equals(album, music.album) &&
                Objects.equals(artist, music.artist) &&
                Objects.equals(abspath, music.abspath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, album, artist, abspath);
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", abspath='" + abspath + '\'' +
                '}';
    }
}
