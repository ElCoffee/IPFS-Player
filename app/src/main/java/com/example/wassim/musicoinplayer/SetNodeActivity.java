package com.example.wassim.musicoinplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setnode);

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

        if (!setNode()) {
            onLoginFailed();
            return;
        }

        btn_setNode.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SetNodeActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Setting up the IPFS node...");
        progressDialog.show();

        setNode();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
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

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        btn_setNode.setEnabled(true);
    }

    public boolean setNode() {
        boolean valid = true;

        //IPFS Node
        context = getApplicationContext();

        IPFSDaemon ipfsDaemon = new IPFSDaemon(getApplicationContext());

        ipfsDaemon.download(SetNodeActivity.this,true);

        startService(new Intent(SetNodeActivity.this, IPFSDaemonService.class));
        Intent launchMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(launchMain, 101);
        return valid;
    }
}