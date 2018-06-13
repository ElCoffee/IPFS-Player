package com.example.wassim.musicoinplayer;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    public static boolean isPlay=false;
    // FAUT-IL APPELLER LE MEDIAPLAYER ICI ?
    public static MediaPlayer mp ;
    // FIN JSP

    private static int currentSongIndex = 0;
    public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    public static boolean mediaPlayerReady;
    public static MediaMetadataRetriever metaRetriever;
    public static String songTitle;
    public static byte[] art;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //mp = PlayerActivity.mp;
        if(mp == null) {
            mp = new MediaPlayer();
        }
        initUI();
        super.onStart(intent, startId);

        metaRetriever = new MediaMetadataRetriever();
        return START_STICKY;
    }

    public void initUI(){
        mediaPlayerReady=false;
        mp.setOnCompletionListener(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean getPlay(){
        return isPlay;
    }

    public static void playAction(){
        if(mp.isPlaying()){
            if(mp!=null){
                mp.pause();
                isPlay=false;
            }
        }else{
            // Resume song
            if(mp!=null){
                mp.start();
                isPlay=true;
            }
        }
    }
/*
    public static void nextAction(){
        if(currentSongIndex < (songsList.size() - 1)){
            currentSongIndex = currentSongIndex + 1;
            PlayerActivity.playSong(currentSongIndex);

        }else{
            PlayerActivity.playSong(0);
            currentSongIndex = 0;
        }
    }*/

    public static int nextAction(){
        return((currentSongIndex+1)%songsList.size());
    }

    public static int previousAction(){
        int newSongIndex;
        if(currentSongIndex == 0){
            newSongIndex = songsList.size()-1;
        }else{
            newSongIndex = (currentSongIndex - 1) % songsList.size();
        }
        return(newSongIndex);
    }

    public static int playSong(int songIndex){
        // Only restart mediaplayer if requested song is different from the one currently played
        if(songIndex != currentSongIndex) {
            currentSongIndex = songIndex;
            try {
                mp.reset();
                mp.setDataSource(songsList.get(songIndex).get("songPath"));
                mp.prepare();
                mp.start();
                mediaPlayerReady = true;

            } catch (IllegalArgumentException e) {

                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(PlayerService.songsList.get(songIndex).get("songPath"));
            art = metaRetriever.getEmbeddedPicture();

            if (art != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            }

            songTitle = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            return 0; // New song is being played
        }
        return -1; // Nothing happens because requested song is the same;
    }

    public void onCompletion(MediaPlayer arg0) {

        if(PlayerActivity.isRepeat){
            playSong(currentSongIndex);

        } else if(PlayerActivity.isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((PlayerService.songsList.size() - 1) - 0 + 1) + 0;
            PlayerActivity.playSong(currentSongIndex);

        } else{
            if(currentSongIndex < (PlayerService.songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                PlayerActivity.playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
