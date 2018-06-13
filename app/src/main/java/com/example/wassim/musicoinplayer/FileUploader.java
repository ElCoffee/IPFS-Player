package com.example.wassim.musicoinplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import io.ipfs.kotlin.IPFS;

/**
 * Created by Wassim on 13/06/18.
 */

public class FileUploader extends AsyncTask<String, Void, String> {
    private Exception exception;

    protected String doInBackground(String... res) {
        try {
            File tempf = new File(res[0]);
            IPFS ipfs = new IPFS();
            String fileHash = ipfs.getAdd().file(tempf).getHash();
            Log.d("IPFS", fileHash);
            return fileHash;
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(String hash) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
