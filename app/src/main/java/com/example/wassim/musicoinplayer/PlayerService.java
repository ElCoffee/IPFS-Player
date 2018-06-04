package com.example.wassim.musicoinplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// import du MainActivity


public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    // private ImageButton btnPlus; //UNUSED

    private static WeakReference<ImageButton> btnPlay;
    private WeakReference<ImageButton> btnNext;
    private WeakReference<ImageButton> btnPrevious;
    private WeakReference<ImageButton> btnMenu;
    private static WeakReference<ImageButton> btnRepeat;
    private static WeakReference<ImageButton> btnShuffle;

    public static WeakReference<ImageView> coverImage;
    public static WeakReference<TextView> songTitleLabel;
    public static WeakReference<TextView> songCurrentDurationLabel;
    public static WeakReference<TextView> songTotalDurationLabel;
    public static WeakReference<SeekBar> songProgressBar;
    static Handler progressBarHandler = new Handler();

    public static MediaPlayer mp;
    private boolean isPause = false;

    private final IBinder musicBind = new MusicBinder();


    // Handler to update UI timer, progress bar etc,.

    private SongsManager songsManager;
    private static Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    public static int currentSongIndex = 0;
    private static boolean isShuffle = false;
    private static boolean isRepeat = false;
    private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private static MediaMetadataRetriever metaRetriever;
    private static boolean mediaPlayerReady;



    public void onCreate(Bundle savedInstanceState) {
        mp = new MediaPlayer();
        mp.reset();
        super.onCreate();

        //A VOIR SI ON GARDE : EST CE QUON MET DANS ONSTART ??????
        songsManager = new SongsManager();
        utils = new Utilities();
        songsList = songsManager.getPlayList();
        mp.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mp.setOnCompletionListener(this);

        //PAGE D'ACCUEIL
/*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player3);
        mediaPlayerReady = false;

        songProgressBar.setOnSeekBarChangeListener(this);
        // Mediaplayer

        mp = MainActivity.mp;


        if(mp == null) {
            mp = new MediaPlayer();
        }

        // Listener
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        playSong(getIntent().getIntExtra("songIndex", 0));*/


    } //FIN ONCREATE

    //binder
    public class MusicBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUI();
        super.onStart(intent, startId);
        return START_STICKY;

    }

    private void initUI() {
        btnPlay = new WeakReference<>(PlayerActivity.btnPlay);
        //btnPlay.get().setOnClickListener(this);
        btnNext = new WeakReference<>(PlayerActivity.btnNext);
        //btnNext.get().setOnClickListener(this);
        btnPrevious = new WeakReference<>(PlayerActivity.btnPrevious);
        //btnPrevious.get().setOnClickListener(this);
        btnMenu = new WeakReference<>(PlayerActivity.btnMenu);
        //btnMenu.get().setOnClickListener(this);
        btnRepeat = new WeakReference<>(PlayerActivity.btnRepeat);
        //btnRepeat.get().setOnClickListener(this);
        btnShuffle = new WeakReference<>(PlayerActivity.btnShuffle);
        //btnShuffle.get().setOnClickListener(this);
        songProgressBar = new WeakReference<>(PlayerActivity.songProgressBar);
        //songProgressBar.get().setOnSeekBarChangeListener(this);
        songTotalDurationLabel = new WeakReference<>(PlayerActivity.songTotalDurationLabel);

        coverImage = new WeakReference<>(PlayerActivity.coverImage);
        songTitleLabel = new WeakReference<>(PlayerActivity.songTitleLabel);
        songCurrentDurationLabel = new WeakReference<>(PlayerActivity.songCurrentDurationLabel);
        songTotalDurationLabel = new WeakReference<>(PlayerActivity.songTotalDurationLabel);
        songProgressBar = new WeakReference<>(PlayerActivity.songProgressBar);

        //mp.setOnCompletionListener(this);

    }

    public static void actionPlay(){
        if(mp.isPlaying()){
            if(mp!=null){
                mp.pause();
                // Changing button image to play button
                btnPlay.get().setImageResource(R.drawable.btn_play);
            }
        }else{
            // Resume song
            if(mp!=null){
                mp.start();
                // Changing button image to pause button
                btnPlay.get().setImageResource(R.drawable.btn_pause);
            }
        }
    }

    public static void actionNext(){
        if(currentSongIndex < (songsList.size() - 1)){
            playSong(currentSongIndex + 1);
            currentSongIndex = currentSongIndex + 1;
        }else{
            // play first song
            playSong(0);
            currentSongIndex = 0;
        }
    }

    public static void actionPrevious(){
        if(currentSongIndex > 0){
            playSong(currentSongIndex - 1);
            currentSongIndex = currentSongIndex - 1;
        }else{
            // play last song
            playSong(songsList.size() - 1);
            currentSongIndex = songsList.size() - 1;
        }
    }

    public static void actionRepeat(){
        if(isRepeat){
            isRepeat = false;
            //Toast.makeText(Intent.getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
            btnRepeat.get().setImageResource(R.drawable.btn_repeat);
        }else{
            // make repeat to true
            isRepeat = true;
            //Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isShuffle = false;
            btnRepeat.get().setImageResource(R.drawable.btn_repeat_focused);
            btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
        }
    }

    public static void actionShuffle(){
        if(isShuffle){
            isShuffle = false;
            //Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            btnShuffle.get().setImageResource(R.drawable.btn_shuffle);
        }else{
            // make repeat to true
            isShuffle= true;
            //Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isRepeat = false;
            btnShuffle.get().setImageResource(R.drawable.btn_shuffle_focused);
            btnRepeat.get().setImageResource(R.drawable.btn_repeat);
        }
    }






    // NEST PAS CENSE ETRE UTILISE


    public static void playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepareAsync();
            mp.start();
            mediaPlayerReady = true;
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.get().setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.get().setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.get().setProgress(0);
            songProgressBar.get().setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(songsList.get(songIndex).get("songPath"));
        try {
            byte[] art = metaRetriever.getEmbeddedPicture();

            if(art != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                coverImage.get().setImageBitmap(songImage);
            }
            else
            {
                coverImage.get().setImageResource(R.drawable.adele); //any default cover resourse folder
            }

            songTitleLabel.get().setText(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            /*album.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            genre.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));*/

        } catch (Exception e) {
            coverImage.get().setBackgroundColor(Color.GRAY);
            songTitleLabel.get().setText(songsList.get(songIndex).get("songTitle"));
            /*album.setText("Unknown Album");
            artist.setText("Unknown Artist");
            genre.setText("Unknown Genre");*/
        }
    }

    public static void updateProgressBar(){
        try{
            progressBarHandler.postDelayed(mUpdateTimeTask, 100);
        }catch(Exception e){

        }
    }

    private static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mediaPlayerReady) {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDurationLabel.get().setText("" + utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.get().setText("" + utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                songProgressBar.get().setProgress(progress);
            }
            // Running this thread after 100 milliseconds
            progressBarHandler.postDelayed(this, 100);
        }
    };




    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }



    // Implémentation du repeat et du shuffle
    @Override
    public void onCompletion(MediaPlayer arg0) {

        if(isRepeat){
            playSong(currentSongIndex);

        } else if(isShuffle){
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);

        } else{
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy(){
/*        super.onDestroy();
        mp.release();
        mediaPlayerReady = false;*/
    }
}