package com.example.wojtek.studentcompanion.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.wojtek.studentcompanion.Activities.MainActivity;
import com.example.wojtek.studentcompanion.Activities.NewNoteActivity;
import com.example.wojtek.studentcompanion.R;

/**
 * Fragment For the Notes Section
 */
public class NotesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public void actionFab(){
        Intent myIntent = new Intent(getActivity(), NewNoteActivity.class);
        startActivity(myIntent);
    }

    public NotesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotesFragment newInstance(int sectionNumber) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionFab();
            }
        });

        return rootView;

    }
}
