package com.example.wassim.musicoinplayer.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.example.wassim.musicoinplayer.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        Toast ts = Toast.makeText(getActivity(),"Working", Toast.LENGTH_SHORT);
        ts.show();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
