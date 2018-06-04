package com.example.wassim.musicoinplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {
    public static ImageButton btnPlay, btnStop, btnNext, btnPrevious, btnMenu, btnRepeat, btnShuffle;

    public static SeekBar songProgressBar;
    public int currentSongIndex = 0;
    public static TextView songTitleLabel;
    public static TextView songCurrentDurationLabel;
    public static TextView songTotalDurationLabel;
    public static ImageView coverImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player3);
        initView();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            Toast ts = Toast.makeText(getApplication().getBaseContext(), "Working", Toast.LENGTH_SHORT);
            ts.show();
            PlayerService.playSong(currentSongIndex);
        }
    }


        private void initView(){
            btnPlay = (ImageButton) findViewById(R.id.btnPlay);
            //btnStop = (ImageButton) findViewById(R.id.btnStop);

            //textViewSongTime = (TextView) findViewById(R.id.textViewSongTime);

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

            btnPlay.setOnClickListener(this);
            btnNext.setOnClickListener(this);
            btnPrevious.setOnClickListener(this);
            btnMenu.setOnClickListener(this);
            btnRepeat.setOnClickListener(this);
            btnShuffle.setOnClickListener(this);
            songProgressBar.setOnSeekBarChangeListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                PlayerService.actionPlay();
                break;

            case R.id.btnMenu:
                Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnNext:
                PlayerService.actionNext();
                break;

            case R.id.btnPrevious:
                PlayerService.actionPrevious();
                break;

            case R.id.btnRepeat:
                PlayerService.actionRepeat();
                break;

            case R.id.btnShuffle:
                PlayerService.actionShuffle();
                break;

        }
    }



    @Override
    protected void onDestroy() {
        if (!PlayerService.mp.isPlaying()) {
            PlayerService.mp.stop();
            //stopService(playerService);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
            if (!PlayerService.mp.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.play);

            } else {
                btnPlay.setBackgroundResource(R.drawable.pause);
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage() + e.getStackTrace() + e.getCause());
        }

        super.onResume();

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
