package com.example.wassim.musicoinplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wassim.musicoinplayer.fragments.DiscoverFragment;
import com.example.wassim.musicoinplayer.fragments.PlaylistsFragment;
import com.example.wassim.musicoinplayer.fragments.SearchFragment;
import com.example.wassim.musicoinplayer.fragments.SettingsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import io.ipfs.kotlin.IPFS;
import io.ipfs.kotlin.IPFSConnection;
import io.ipfs.kotlin.commands.Info;
import io.ipfs.kotlin.model.VersionInfo;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {
    private ImageView currImv;
    private static final String HASH = "QmTfCkcHCNuTkLU5e54ofRnF17dzxxQmmuwgb86bsArqwv";
   // private IPFSDaemon ipfsDaemon = new IPFSDaemon(this);
    private FragmentManager fragmentManager;
    private Fragment displayedFragment;
    private HashMap<String, Fragment> fragments;
    private ArrayList<HashMap<String, String>> playlist;
    private SongsManager sm;
    public static MediaPlayer mp;
    public static String title;
    private int currentView;
    Context context;
    private Intent playerService;
    public static Intent ipfsService;
    public static TextView bar_songTitle;
    public static TextView bar_artist;
    public static ImageView bar_art;
    public static ImageButton bar_play;
    private static LinearLayout bar;
    private static ProgressBar songProgressBar;
    private ImageButton btnServer;

    private int currentSongIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // importé d'aymeric

        ipfsService = SetNodeActivity.ipfsService;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerService = new Intent(MainActivity.this, PlayerService.class);
        startService(playerService);


        fragmentManager = getSupportFragmentManager();
        displayedFragment = new DiscoverFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_container, displayedFragment);
        ft.commit();

        sm = new SongsManager();
        playlist = sm.getPlayList();

        fragments = new HashMap<String, Fragment>();
        fragments.put("discover", displayedFragment);

        mp = PlayerService.mp;

        bar_songTitle = (TextView) findViewById(R.id.titleplaying);
        bar_artist = (TextView) findViewById(R.id.artistplaying);
        bar_art = (ImageView) findViewById(R.id.coverplaying);

        bar_play = (ImageButton) findViewById(R.id.bar_btnPlay);

        bar = (LinearLayout) findViewById(R.id.playerBar);

        bar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSong(currentSongIndex);
            }
        });

        songProgressBar = (ProgressBar) findViewById(R.id.songProgressBar2);

        bar_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bar_playAction();
            }
        });

        btnServer = (ImageButton) findViewById(R.id.btn_server);
        btnServer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                serverBtnAction();
            }

        });

        if (SetNodeActivity.isIpfsRunning == true){
            btnServer.setImageResource(R.drawable.btn_pause);
        }

    }


    //START IPFS SERVER
    public void startServer(){
        //IPFS Node
        context = getApplicationContext();

        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.download(MainActivity.this,true);

        ipfsService = new Intent(MainActivity.this, IPFSDaemonService.class);
        startService(ipfsService);
        SetNodeActivity.isIpfsRunning = true;
    }

    //STOP IPFS SERVER
    public void stopServer(){
        stopService(ipfsService);
        SetNodeActivity.isIpfsRunning = false;
    }


    //SERVER RUNNING?
    public void serverBtnAction(){
        if (SetNodeActivity.isIpfsRunning == true){
            stopServer();
            Toast ts = Toast.makeText(MainActivity.this, "Server Stopped", Toast.LENGTH_SHORT);
            ts.show();
            btnServer.setImageResource(R.drawable.btn_play);
        }
        else{
            startServer();
            Toast ts = Toast.makeText(MainActivity.this, "Server Started", Toast.LENGTH_SHORT);
            ts.show();
            btnServer.setImageResource(R.drawable.btn_pause);
        }
    }

    public void bar_playAction(){
        PlayerService.playAction();
        if (PlayerService.getPlay()){
            bar_play.setImageResource(R.drawable.btn_pause);
        } else {
            bar_play.setImageResource(R.drawable.btn_play);
        }
    }
    public void onNavBarClicked(View view) {
        String tag = (String) view.getTag();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch(tag) {
            case "discover":
                Fragment discoverFragment;
                if(!fragments.containsKey("discover")) {
                    discoverFragment = new DiscoverFragment();
                    fragments.put("discover", discoverFragment);
                }
                else {
                    discoverFragment = fragments.get("discover");
                }
                ft.replace(R.id.main_container, discoverFragment);
                currentView = 0;
                break;
            case "search":
                Fragment searchFragment;
                if(!fragments.containsKey("search")) {
                    searchFragment = new SearchFragment();
                    fragments.put("search", searchFragment);
                }
                else {
                    searchFragment = fragments.get("search");
                }
                ft.replace(R.id.main_container, searchFragment);
                currentView = 1;
                break;
            case "playlists":
                Fragment playlistsFragment;
                if(!fragments.containsKey("playlists")) {
                    playlistsFragment = new PlaylistsFragment();
                    fragments.put("playlists", playlistsFragment);
                }
                else {
                    playlistsFragment = fragments.get("playlists");
                }
                ft.replace(R.id.main_container, playlistsFragment);
                currentView = 2;
                break;
            case "settings":
                Fragment settingsFragment;
                if(!fragments.containsKey("settings")) {
                    settingsFragment = new Fragment();
                    fragments.put("settings", settingsFragment);
                }
                else {
                    settingsFragment = fragments.get("settings");
                }
                ft.replace(R.id.main_container, settingsFragment);
                currentView = 3;
                break;
            default:
                // Stay on same fragment
                break;
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp != null) mp.release();
    }

    public void playSong(int songIndex) {
        if(songIndex != currentSongIndex) {
            currentSongIndex = songIndex;
        }
        Intent playSongIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        playSongIntent.putExtra("songIndex", songIndex);
        startActivityForResult(playSongIntent, 100);
        String songTitle = PlayerService.songTitle;
        bar_play.setImageResource(R.drawable.btn_pause);
    }

    void onPlayerBarClicked(View v) {
        //showPlayerFullscreen();
    }

    public SongsManager getSongManager() {
        return sm;
    }


}
