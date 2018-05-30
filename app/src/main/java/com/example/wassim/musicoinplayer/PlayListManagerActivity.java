package com.centralesupelec.ipfs.ipfsplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayListManagerActivity extends Activity {
    private EditText etInput;
    private ImageButton btnAdd;
    private ListView lvItem;
    private ArrayList<String> itemArrey;
    private ArrayAdapter<String> itemAdapter;
    public static String title;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlists_fragment);
        setUpView();

    }

    private void setUpView() {
        etInput = (EditText) findViewById(R.id.playlistName_input);
        btnAdd = (ImageButton) findViewById(R.id.btn_addPlaylist);
        lvItem = (ListView) findViewById(R.id.playlists_list);

        itemArrey = new ArrayList<String>();
        itemArrey.clear();

        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,itemArrey);
        lvItem.setAdapter(itemAdapter);


        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                title = lvItem.getItemAtPosition(position).toString();
                Intent in = new Intent(getApplicationContext(), DisplayPlaylistActivity.class);
                startActivityForResult(in, 100);
            }

        });






        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItemList();
            }
        });

        etInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    addItemList();
                }
                return true;
            }
        });
    }

    protected void addItemList() {
        if (isInputValid(etInput)) {
            itemArrey.add(0,etInput.getText().toString());
            etInput.setText("");
            itemAdapter.notifyDataSetChanged();
        }
    }

    protected boolean isInputValid(EditText etInput2) {
        if (etInput2.getText().toString().trim().length()<1) {
            etInput2.setError("Please Enter Item");
            return false;
        } else {
            return true;
        }
    }
}