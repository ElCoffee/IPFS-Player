package com.example.wassim.musicoinplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.ipfs.kotlin.IPFS;
import io.ipfs.kotlin.IPFSConnection;
import okhttp3.ResponseBody;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Music> mData;

    public RecyclerViewAdapter(Context mContext, List<Music> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_book,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_music_title.setText(mData.get(position).getTitle());
        holder.tv_music_artist.setText(mData.get(position).getArtist());
        //holder.img_music_thumbnail.setImageResource(mData.get(position).getThumbnail());
        Picasso.get().load(mData.get(position).getThumbnail()).fit().into(holder.img_music_thumbnail);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            FileOutputStream outputStream;
            @Override
            public void onClick(View v){
                if (SetNodeActivity.isIpfsRunning == false){
                    Toast ts = Toast.makeText(mContext, "Service not launched", Toast.LENGTH_SHORT);
                    ts.show();
                }else{
                    IPFSGetTask getAsync = new IPFSGetTask();
                    getAsync.execute(mData.get(position).getHash(),mData.get(position).getTitle(),mData.get(position).getArtist());
                }

            }
        });
    }

    private class IPFSGetTask extends AsyncTask<String, Integer, byte[]>{
        byte[] fileContents;
        String MEDIA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/";
        private String title;
        private String artist;



        @Override
        protected byte[] doInBackground(String... strings) {
            IPFSConnection ipfs = new IPFS().getGet().getIpfs();
            Log.d("DoInBG","Zubi");
            ResponseBody rb = ipfs.callCmd("cat/" + strings[0]);
            title=strings[1];
            artist=strings[2];
            try {
                fileContents = rb.bytes();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return fileContents;
        }


        @Override
        protected void onPostExecute(byte[] fileContents) {

            FileOutputStream outputStream;
            try {
                // File file = createTempFile("song", ".mp3");
                Log.d("DoInBG", title);

                File file = new File(MEDIA_PATH, artist+" - "+title+".mp3");
                outputStream = new FileOutputStream(file);
                outputStream.write(fileContents);
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(mContext, "Music has been downloaded!", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_music_artist;
        TextView tv_music_title;
        ImageView img_music_thumbnail;
        CardView cardView;

        public MyViewHolder(View itemView){
            super(itemView);
            tv_music_title  = (TextView) itemView.findViewById(R.id.music_title_id);
            tv_music_artist  = (TextView) itemView.findViewById(R.id.music_artist_id);
            img_music_thumbnail = (ImageView) itemView.findViewById(R.id.music_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}