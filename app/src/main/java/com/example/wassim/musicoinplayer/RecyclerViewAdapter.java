package com.example.wassim.musicoinplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

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
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tv_music_title.setText(mData.get(position).getTitle());
        holder.tv_music_artist.setText(mData.get(position).getArtist());
        //holder.img_music_thumbnail.setImageResource(mData.get(position).getThumbnail());
        Picasso.get().load(mData.get(position).getThumbnail()).fit().into(holder.img_music_thumbnail);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_music_artist;
        TextView tv_music_title;
        ImageView img_music_thumbnail;

        public MyViewHolder(View itemView){
            super(itemView);

            tv_music_title  = (TextView) itemView.findViewById(R.id.music_title_id);
            tv_music_artist  = (TextView) itemView.findViewById(R.id.music_artist_id);
            img_music_thumbnail = (ImageView) itemView.findViewById(R.id.music_img_id);

        }

    }


}