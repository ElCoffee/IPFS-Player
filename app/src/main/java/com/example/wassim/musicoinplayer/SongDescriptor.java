package com.example.wassim.musicoinplayer;

public class SongDescriptor {
    private String songname;
    private String artist;
    private String album;
    private String path;
    private long id;
    private String hash;

    public SongDescriptor() {

    }

    public SongDescriptor(String songname, String artist, String album) {
        this.songname = songname;
        this.artist = artist;
        this.album = album;
    }

    public SongDescriptor(long id, String songname, String artist, String album) {
        this.songname = songname;
        this.artist = artist;
        this.album = album;
        this.id = id;
    }

    public long getID(){ return id; }
    public void setID(long id){ this.id = id; }

    public String getSongname() {
        return songname;
    }
    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPath(String Path) { this.path = Path; }
    public String getPath() { return this.path; }

    public void setHash(String hash) { this.hash = hash; }
    public String getHash() { return this.hash; }
}
