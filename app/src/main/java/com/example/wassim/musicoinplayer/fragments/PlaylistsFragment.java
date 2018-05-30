package com.centralesupelec.ipfs.ipfsplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.centralesupelec.ipfs.ipfsplayer.MainActivity;
import com.centralesupelec.ipfs.ipfsplayer.R;
import com.centralesupelec.ipfs.ipfsplayer.DisplayPlaylistActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaylistsFragment extends Fragment {
    private ArrayList<HashMap<String, String>> playlist;
    private ListView mainList;
    private MainActivity parentActivity;

    private Button btnPlayer;
    private EditText playlistNameInput;
    private ImageButton btnAdd;
    private ListView lvItem;

    private ArrayList<String> playlistNames;
    private ArrayAdapter<String> playlistNamesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playlists_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

//        btnPlayer = (Button) parentActivity.findViewById(R.id.btnPlayer);
//
//        btnPlayer.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                Intent i = new Intent(parentActivity, PlayerActivity.class);
//                startActivityForResult(i, 100);
//            }
//        });

        playlistNameInput = (EditText) parentActivity.findViewById(R.id.playlistName_input);
        btnAdd = (ImageButton) parentActivity.findViewById(R.id.btn_addPlaylist);
        lvItem = (ListView) parentActivity.findViewById(R.id.playlists_list);

        playlistNames = new ArrayList<String>();

        playlistNamesAdapter = new ArrayAdapter<String>(parentActivity, android.R.layout.simple_list_item_1, playlistNames);
        lvItem.setAdapter(playlistNamesAdapter);


        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent in = new Intent(parentActivity, DisplayPlaylistActivity.class);
                in.putExtra("playlistTitle", playlistNames.get(position));
                startActivityForResult(in, 100);
            }

        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItemList();
            }
        });

        playlistNameInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    addItemList();
                }
                return true;
            }
        });
    }

    protected void addItemList() {
        if (isInputValid(playlistNameInput)) {
            playlistNames.add(0, playlistNameInput.getText().toString());
            playlistNameInput.setText("");
            playlistNamesAdapter.notifyDataSetChanged();
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