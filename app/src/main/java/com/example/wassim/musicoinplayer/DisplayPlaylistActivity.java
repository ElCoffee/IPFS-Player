package com.example.wassim.musicoinplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayPlaylistActivity extends Activity {
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songsinpl);

        ListView PlaylistList = (ListView) findViewById(R.id.songs_pl);
        PlaylistList.setClickable(true);

        final List<SongDescriptor> SongList = new ArrayList<SongDescriptor>();
        SongList.add(new SongDescriptor("Supélec est là", "Guillaume Debournoux", "SMS"));
        SongList.add(new SongDescriptor("Cloporte", "Damian Py", "Vrai Ingénieur"));
        SongList.add(new SongDescriptor("Go Top 1", "allez", "go top 1 srx"));

        SongDescriptorAdapter adapter = new SongDescriptorAdapter(this, SongList);


        TextView name1 = (TextView) findViewById(R.id.playlist_name);
        TextView name2 = (TextView) findViewById(R.id.playlist_name2);
        name1.setText(getIntent().getStringExtra("playlistTitle").toUpperCase());
        name2.setText(getIntent().getStringExtra("playlistTitle").toUpperCase());

        PlaylistList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                showToast(SongList.get(position).getSongname());
            }
        });

        PlaylistList.setAdapter(adapter);
    }

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //    if(resultCode == 100){
    //        String playlistTitle = data.getExtras().getString("playlistTitle");
    //        File playlistDir = this.getDir("Playlists", Context.MODE_PRIVATE); //Creating an internal dir;
    //        File fileWithinMyDir = new File(playlistDir, playlistTitle); //Getting a file within the dir.
    //    }
    //
    //}

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
