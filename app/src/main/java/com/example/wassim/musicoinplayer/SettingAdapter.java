package com.centralesupelec.ipfs.ipfsplayer;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.centralesupelec.ipfs.ipfsplayer.R;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout mConstraintLayout;
        public TextView mSettingNameTextView;
        public Switch mSwitch;
        public ViewHolder(ConstraintLayout cl) {
            super(cl);
            mConstraintLayout = cl;
            mSettingNameTextView = (TextView) cl.getChildAt(0);
            mSwitch = (Switch) cl.getChildAt(1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SettingAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SettingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        ConstraintLayout cl = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_element, parent, false);
        ViewHolder vh = new ViewHolder(cl);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mSettingNameTextView.setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}