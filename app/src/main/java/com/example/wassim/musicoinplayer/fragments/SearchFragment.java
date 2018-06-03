package com.example.wassim.musicoinplayer.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.wassim.musicoinplayer.MainActivity;
import com.example.wassim.musicoinplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.ipfs.kotlin.IPFS;
import io.ipfs.kotlin.IPFSConnection;
import io.ipfs.kotlin.commands.Add;

public class SearchFragment extends Fragment {
    private ArrayList<HashMap<String, String>> playlist;
    private ListView mainList;
    private MainActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.search_fragment, container, false);;

        final Button btn_import = view.findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,33);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        playlist = parentActivity.getSongManager().getPlayList();
        String[] listContent = new String[playlist.size()];
        for(int i = 0; i < playlist.size(); i++) {
            listContent[i] = playlist.get(i).get("songTitle");
        }
        mainList = (ListView) parentActivity.findViewById(R.id.songs_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listContent);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                parentActivity.playSong(i);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 33) {
            Uri currFileURI = data.getData();
            String path=getFileName(currFileURI);
            Log.d("OPENED_FILE", path);
        }}

    public String getFileName(Uri uri) {
        String result = null;

        if (result == null) {
            result = uri.getPath();
            File tempf = new File(result);
            IPFS ipfs = new IPFS();
            String fileHash = ipfs.getAdd().file(tempf).getHash();
            Log.d("IPFS", fileHash);
        }
        return result;
    }
}