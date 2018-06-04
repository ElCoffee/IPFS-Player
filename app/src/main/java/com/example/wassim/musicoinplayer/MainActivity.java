package com.example.wassim.musicoinplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    //private static final String isFirstStart = "PREFS";
    //SharedPreferences sharedPreferences;
    private ImageButton btnServer;

    //SERVICE
    private Intent playerService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SERVICE
        playerService = new Intent(MainActivity.this, PlayerService.class);
        startService(playerService);

        //importé d'Aymeric
        fragmentManager = getSupportFragmentManager();
        displayedFragment = new DiscoverFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_container, displayedFragment);
        ft.commit();

        sm = new SongsManager();
        playlist = sm.getPlayList();

        fragments = new HashMap<String, Fragment>();
        fragments.put("discover", displayedFragment);

        //SERVER BUTTON
        btnServer = (ImageButton) findViewById(R.id.btn_server);
        btnServer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(serverIsRunning()){
                    startServer();
                    btnServer.setImageResource(R.drawable.btn_play);
                }else{
                    stopServer();
                    btnServer.setImageResource(R.drawable.btn_pause);
                    }
                }

        });

    }

    //SERVICE



/*    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            Toast ts = Toast.makeText(getApplication().getBaseContext(),"Working", Toast.LENGTH_SHORT);
            ts.show();
            playSong(currentSongIndex);
        }

    }*/




    //START IPFS SERVER
    public void startServer(){
        //IPFS Node
        context = getApplicationContext();

        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.download(MainActivity.this,true);

        startService(new Intent(MainActivity.this, IPFSDaemonService.class));
    }

    //STOP IPFS SERVER
    public void stopServer(){
        //A IMPLEMENTER
    }

    //SERVER RUNNING?
    public boolean serverIsRunning(){
        //A IMPLEMENTER
        return false;
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
                    settingsFragment = new SettingsFragment();
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

//PEUT ETRE CHANGER LA CLASSE DANS LA FONCTION
    public void playSong(int songIndex) {
        Toast ts2 = Toast.makeText(getApplication().getBaseContext(),"Working2", Toast.LENGTH_SHORT);
        ts2.show();
        //setContentView(R.layout.player3);
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(playlist.get(songIndex).get("songPath")));
        mp.start();

        Intent playSongIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        playSongIntent.putExtra("songIndex", songIndex);
        startActivityForResult(playSongIntent, 100);
    }


    public SongsManager getSongManager() {
        return sm;
    }


}
