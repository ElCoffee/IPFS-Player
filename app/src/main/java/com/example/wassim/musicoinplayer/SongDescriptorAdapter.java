package com.centralesupelec.ipfs.ipfsplayer;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SongDescriptorAdapter extends BaseAdapter implements OnClickListener {

    private Context context;
    private List<SongDescriptor> songDescriptorList;

    public SongDescriptorAdapter(Context context, List<SongDescriptor> songDescriptorList) {
        this.context = context;
        this.songDescriptorList = songDescriptorList;
    }

    public int getCount() {
        return songDescriptorList.size();
    }

    public Object getItem(int position) {
        return songDescriptorList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        SongDescriptor entry = songDescriptorList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.phone_row, null);
        }
        TextView tvContact = (TextView) convertView.findViewById(R.id.tvContact);
        tvContact.setText(entry.getSongname());

        TextView tvPhone = (TextView) convertView.findViewById(R.id.tvMobile);
        tvPhone.setText(entry.getArtist());

        TextView tvMail = (TextView) convertView.findViewById(R.id.tvMail);
        tvMail.setText(entry.getAlbum());

        // Set the onClick Listener on this button
        Button btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
        btnRemove.setFocusableInTouchMode(false);
        btnRemove.setFocusable(false);
        btnRemove.setOnClickListener(this);
        // Set the entry, so that you can capture which item was clicked and
        // then remove it
        // As an alternative, you can use the id/position of the item to capture
        // the item
        // that was clicked.
        btnRemove.setTag(entry);

        // btnRemove.setId(position);


        return convertView;
    }

    @Override
    public void onClick(View view) {
        SongDescriptor entry = (SongDescriptor) view.getTag();
        songDescriptorList.remove(entry);
        notifyDataSetChanged();
    }
}
