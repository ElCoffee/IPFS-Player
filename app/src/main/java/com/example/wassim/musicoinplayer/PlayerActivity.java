package com.centralesupelec.ipfs.ipfsplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

// import du MainActivity
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class PlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnMenu;
    // private ImageButton btnPlus; //UNUSED
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private ImageView coverImage;


    // Media Player
    private MediaPlayer mp;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private SongsManager songsManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private MediaMetadataRetriever metaRetriever;
    private boolean mediaPlayerReady;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //PAGE D'ACCUEIL

        super.onCreate(savedInstanceState);
        setContentView(R.layout.player3);

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

        mediaPlayerReady = false;

        // Mediaplayer

        mp = MainActivity.mp;
        songsManager = new SongsManager();
        utils = new Utilities();

        if(mp == null) {
            mp = new MediaPlayer();
        }

        // Listener
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);

        songsList = songsManager.getPlayList();

        metaRetriever = new MediaMetadataRetriever();

        playSong(getIntent().getIntExtra("songIndex", 0));

        // Implémentation des boutons

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });


    } //FIN ONCREATE

    // recevoir les chansons depuis l'index playlist

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    public void playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            mediaPlayerReady = true;
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

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
                coverImage.setImageBitmap(songImage);
            }
            else
            {
                coverImage.setImageResource(R.drawable.adele); //any default cover resourse folder
            }

            songTitleLabel.setText(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            /*album.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            genre.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));*/

        } catch (Exception e) {
            coverImage.setBackgroundColor(Color.GRAY);
            songTitleLabel.setText(songsList.get(songIndex).get("songTitle"));
            /*album.setText("Unknown Album");
            artist.setText("Unknown Artist");
            genre.setText("Unknown Genre");*/
        }
    }

    public void updateProgressBar(){
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mediaPlayerReady) {
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
        super.onDestroy();
        mp.release();
        mediaPlayerReady = false;
    }
}