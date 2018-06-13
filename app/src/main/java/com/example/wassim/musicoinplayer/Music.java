package com.example.wassim.musicoinplayer;

public class Music {
    private String Title;
    private String Artist;
    private int Thumbnail;
    private String Hash;

    public Music() {
    }

    public Music(String title, String artist, int thumbnail, String hash){
        Title=title;
        Artist=artist;
        Thumbnail=thumbnail;
        Hash=hash;
    }

    public String getTitle() {
        return Title;
    }

    public String getArtist() {
        return Artist;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public String getHash() {
        return Hash;
    }


    public void setTitle(String title) {
        Title = title;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

}
