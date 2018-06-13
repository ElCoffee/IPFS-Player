package com.example.wassim.musicoinplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// import du MainActivity


public class PlayerActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnMenu;
    // private ImageButton btnPlus; //UNUSED
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private static SeekBar songProgressBar;
    private static TextView songTitleLabel;
    private static TextView songCurrentDurationLabel;
    private static TextView songTotalDurationLabel;
    private static ImageView coverImage;


    // Media Player
    public static MediaPlayer mp;

    // Handler to update UI timer, progress bar etc,.
    private static Handler mHandler = new Handler();
    private SongsManager songsManager;
    private static Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    public static boolean isShuffle = false;
    public static boolean isRepeat = false;


    public static int currentSongIndex = PlayListActivity.songIndex;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //PAGE D'ACCUEIL

        super.onCreate(savedInstanceState);
        setContentView(R.layout.player3);
        //Mp


        // Boutons du player
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        //btnPlus = (ImageButton) findViewById(R.id.btnPlus);

        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);

        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        coverImage = (ImageView) findViewById(R.id.cover);



        // Mediaplayer

        mp = PlayerService.mp;
        songsManager = new SongsManager();
        utils = new Utilities();


        // Listener
        songProgressBar.setOnSeekBarChangeListener(this);


        PlayerService.songsList = songsManager.getPlayList();

        PlayerService.mediaPlayerReady = false;

        playSong(getIntent().getIntExtra("songIndex",2));

        // Implémentation des boutons

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                playAction();
            }
        });



        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
             nextAction();
            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
             previousAction();
            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                repeatAction();
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                shuffleAction();
            }
        });

/*        btnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });*/


    } //FIN ONCREATE

    public void playAction(){
        PlayerService.playAction();
        if (PlayerService.getPlay()){
            btnPlay.setImageResource(R.drawable.btn_pause);
        }else{
            btnPlay.setImageResource(R.drawable.btn_play);
        }
    }

    public void nextAction(){
        playSong(PlayerService.nextAction());
    }

    public void previousAction(){
        playSong(PlayerService.previousAction());
    }

    public void repeatAction(){
        if(isRepeat){
            isRepeat = false;
            Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
            btnRepeat.setImageResource(R.drawable.btn_repeat);
        }else{
            // make repeat to true
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isShuffle = false;
            btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    public void shuffleAction(){
        if(isShuffle){
            isShuffle = false;
            Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }else{
            // make repeat to true
            isShuffle= true;
            Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            // make shuffle to false
            isRepeat = false;
            btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
            btnRepeat.setImageResource(R.drawable.btn_repeat);
        }
    }







    // recevoir les chansons depuis l'index playlist

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            Log.d("Zoubi",String.valueOf(currentSongIndex));

            playSong(currentSongIndex);
        }

    }

    public static void playSong(int songIndex){
        // Play song
        Log.d("Zoubi",String.valueOf(PlayListActivity.songIndex));
        PlayerService.playSong(songIndex);
        btnPlay.setImageResource(R.drawable.btn_pause);
        try {
            String songTitle = PlayerService.songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            byte[] art = PlayerService.art;

            if(art != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                coverImage.setImageBitmap(songImage);
            }
            else
            {
                coverImage.setImageResource(R.drawable.adele); //any default cover resourse folder
            }

            songTitleLabel.setText(PlayerService.songTitle);


            /*album.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            genre.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));*/

        } catch (Exception e) {
            coverImage.setBackgroundColor(Color.GRAY);
            songTitleLabel.setText(PlayerService.songsList.get(songIndex).get("songTitle"));
            /*album.setText("Unknown Album");
            artist.setText("Unknown Artist");
            genre.setText("Unknown Genre");*/
        }
    }

    public static void updateProgressBar(){
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(PlayerService.mediaPlayerReady) {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                songProgressBar.setProgress(progress);
            }
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };




    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }



    // Implémentation du repeat et du shuffle


    @Override
    public void onDestroy(){
        super.onDestroy();
        //mp.release();
        //PlayerService.mediaPlayerReady = false; //ça a décancerisé
    }
}