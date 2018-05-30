package com.centralesupelec.ipfs.ipfsplayer;

public class SongDescriptor {
    private String songname;
    private String artist;
    private String album;

    public SongDescriptor(String songname, String artist, String album) {
        this.songname = songname;
        this.artist = artist;
        this.album = album;
    }

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
}
