package com.example.wassim.musicoinplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // importé d'aymeric
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        displayedFragment = new DiscoverFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_container, displayedFragment);
        ft.commit();

        sm = new SongsManager();
        playlist = sm.getPlayList();

        fragments = new HashMap<String, Fragment>();
        fragments.put("discover", displayedFragment);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    20);
        }

        //IPFS Node
        context = getApplicationContext();

        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.download(MainActivity.this,true);

        startService(new Intent(MainActivity.this, IPFSDaemonService.class));

        /*
        IPFSGetTask getAsync = new IPFSGetTask();
        getAsync.execute(HASH);*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp != null) mp.release();
    }

    public void playSong(int songIndex) {
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(playlist.get(songIndex).get("songPath")));

        Intent playSongIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        playSongIntent.putExtra("songIndex", songIndex);
        startActivityForResult(playSongIntent, 100);
    }


    public SongsManager getSongManager() {
        return sm;
    }

    //Noeud IPFS
    private class IPFSGetTask extends AsyncTask<String, Integer, byte[]> {

        byte[] fileContents;

        @Override
        protected byte[] doInBackground(String... strings) {
/*
            context = getApplicationContext();
            IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

            ipfsDaemon.download(MainActivity.this,true);

            startService(new Intent(MainActivity.this, IPFSDaemonService.class));
*/
            IPFSConnection ipfs1 = new IPFS().getGet().getIpfs();

            VersionInfo version = null;
            version = new Info(ipfs1).version();



            Log.i("IPFS", "IPFS Base URL = " + ipfs1.getBase_url());
            ResponseBody rb = ipfs1.callCmd("cat/" + strings[0]);

            Log.i("IPFS", "essou ");

            try {
                fileContents = rb.bytes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream outputStream;
            try {
                //File file = createTempFile("song", ".mp3");
                File file = new File(getExternalFilesDir(null), "song.mp3");
                outputStream = new FileOutputStream(file);
                outputStream.write(fileContents);
                outputStream.close();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.fromFile(file));
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] fileContents) {

            FileOutputStream outputStream;
            try {
                //File file = createTempFile("song", ".mp3");
                File file = new File(getExternalFilesDir(null), "song.mp3");
                outputStream = new FileOutputStream(file);
                outputStream.write(fileContents);
                outputStream.close();
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.fromFile(file));
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
