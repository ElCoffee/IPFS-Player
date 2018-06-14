package com.example.wassim.musicoinplayer;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import io.ipfs.kotlin.IPFS;

import static java.security.AccessController.getContext;

/**
 * Created by Wassim on 13/06/18.
 */

public class FileUploader extends AsyncTask<String, Void, String> {
    private Exception exception;
    private Process daemon = null;
    private Context context;

    public FileUploader(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... res) {
        try {
            File tempf = new File(res[0]);
            IPFS ipfs = new IPFS();
            //String fileHash = ipfs.getAdd().file(tempf).getHash();
            try {
                Log.d("Test","coucou   " + tempf.getAbsolutePath() + "   " + res);
                daemon = new IPFSDaemon(this.context).run("add "+tempf.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "pain";
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(String hash) {
        // TODO: check this.exception
        Log.d("IPFS", " "+hash);
    }
}
