package com.example.wojtek.studentcompanion.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wojtek.studentcompanion.R;

/**
 * Fragment For the Audio Section
 */
public class AudioFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public AudioFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AudioFragment newInstance(int sectionNumber) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);
        return rootView;
    }
}
