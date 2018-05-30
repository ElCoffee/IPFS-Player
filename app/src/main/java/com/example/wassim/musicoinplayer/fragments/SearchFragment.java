package com.centralesupelec.ipfs.ipfsplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.centralesupelec.ipfs.ipfsplayer.MainActivity;
import com.centralesupelec.ipfs.ipfsplayer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {
    private ArrayList<HashMap<String, String>> playlist;
    private ListView mainList;
    private MainActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        playlist = parentActivity.getSongManager().getPlayList();
        String[] listContent = new String[playlist.size()];
        for(int i = 0; i < playlist.size(); i++) {
            listContent[i] = playlist.get(i).get("songTitle");
        }
        mainList = (ListView) getActivity().findViewById(R.id.songs_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listContent);
        mainList.setAdapter(adapter);

        mainList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                parentActivity.playSong(i);
            }
        });
    }
}