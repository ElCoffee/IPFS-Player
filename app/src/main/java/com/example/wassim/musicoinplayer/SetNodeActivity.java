package com.example.wassim.musicoinplayer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class SetNodeActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Button btn_setNode;
    private TextView text_setnodelater;
    Context context;
    public static Intent ipfsService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setnode);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    20);
        }

        btn_setNode = (Button) findViewById(R.id.btn_setNode);
        text_setnodelater = (TextView) findViewById(R.id.text_setnodelater);

        btn_setNode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        text_setnodelater.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                // pio
                Intent launchMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(launchMain, 101);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        btn_setNode.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SetNodeActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Setting up the IPFS node...");
        progressDialog.show();

        setNode(); //SETTING THE NODE

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);

        Intent launchMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(launchMain, 101);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        btn_setNode.setEnabled(true);
        finish();
    }

    public void setNode() {

        //IPFS Node
        context = getApplicationContext();

        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.download(SetNodeActivity.this,true);

        ipfsService = new Intent(SetNodeActivity.this, IPFSDaemonService.class);
        startService(ipfsService);

    }
}