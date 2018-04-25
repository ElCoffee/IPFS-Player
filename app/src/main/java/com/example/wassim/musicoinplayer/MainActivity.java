package com.example.wassim.musicoinplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.ipfs.kotlin.IPFS;
import io.ipfs.kotlin.IPFSConnection;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private ImageView currImv;
    private static final String HASH = "QmTfCkcHCNuTkLU5e54ofRnF17dzxxQmmuwgb86bsArqwv";

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView discoverImageView = (ImageView) findViewById(R.id.imageViewDiscover);
        discoverImageView.getDrawable().setColorFilter(0xffffcc00, PorterDuff.Mode.SRC_ATOP);
        currImv = discoverImageView;


        context = getApplicationContext();
        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.calldownload(this,ipfsDaemon);

        Intent intent = new Intent(this, IPFSDaemonService.class);
        startService(intent);


        IPFSGetTask getAsync = new IPFSGetTask();
        getAsync.execute(HASH);
    }

    protected void onNavBarClicked(View v) {
        LinearLayout ll = (LinearLayout) v;
        ImageView iv = (ImageView) ll.getChildAt(0);
        currImv.getDrawable().setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
        iv.getDrawable().setColorFilter(0xffffcc00, PorterDuff.Mode.SRC_ATOP);
        currImv = iv;
    }

    private class IPFSGetTask extends AsyncTask<String, Integer, byte[]> {

        byte[] fileContents;

        @Override
        protected byte[] doInBackground(String... strings) {
            IPFSConnection ipfs = new IPFS().getGet().getIpfs();
            Log.i("IPFS", "IPFS Base URL = " + ipfs.getBase_url());
            ResponseBody rb = ipfs.callCmd("cat/" + strings[0]);
            try {
                fileContents = rb.bytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileContents;
        }

        @Override
        protected void onPostExecute(byte[] fileContents) {

            FileOutputStream outputStream;
            try {
                // File file = createTempFile("song", ".mp3");
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
