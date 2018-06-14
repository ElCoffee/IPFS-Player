package com.example.wassim.musicoinplayer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wassim.musicoinplayer.Music;
import com.example.wassim.musicoinplayer.R;
import com.example.wassim.musicoinplayer.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    List<Music> lstMusic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discover_fragment, container, false);

        lstMusic=new ArrayList<>();
        lstMusic.add(new Music("Genius","LSD - Sia, Diplo, Labrinth",R.drawable.genius));
        lstMusic.add(new Music("Crazy Train","Ozzy Osbourne",R.drawable.crazy_train));
        lstMusic.add(new Music("Fear of the Dark","Iron Maiden",R.drawable.fear_of_the_dark));
        lstMusic.add(new Music("Lonely Nights","Scorpions",R.drawable.lonely_nights));
        lstMusic.add(new Music("The Trooper","Iron Maide",R.drawable.the_trooper));
        lstMusic.add(new Music("War Pigs","Black Sabbath",R.drawable.war_pigs));
        lstMusic.add(new Music("Fear of the Dark","Iron Maiden",R.drawable.fear_of_the_dark));
        lstMusic.add(new Music("Lonely Nights","Scorpions",R.drawable.lonely_nights));
        lstMusic.add(new Music("The Trooper","Iron Maide",R.drawable.the_trooper));
        lstMusic.add(new Music("War Pigs","Black Sabbath",R.drawable.war_pigs));

        RecyclerView myrv = (RecyclerView) rootView.findViewById(R.id.recyclerview_id);
        myrv.setHasFixedSize(true);
        myrv.setItemViewCacheSize(8);
        myrv.setDrawingCacheEnabled(true);
        myrv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this.getContext(),lstMusic);
        myrv.setLayoutManager(new GridLayoutManager(this.getContext(),2));
        myrv.setAdapter(myAdapter);
        // Inflate the layout for this fragment
        return rootView;
    }

}
