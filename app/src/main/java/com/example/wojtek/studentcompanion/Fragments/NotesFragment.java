package com.example.wojtek.studentcompanion.Fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wojtek.studentcompanion.DB.DatabaseContract;
import com.example.wojtek.studentcompanion.DB.DatabaseHandler;
import com.example.wojtek.studentcompanion.Adapters.NotesAdapter;
import com.example.wojtek.studentcompanion.R;

/**
 * Fragment For the Notes Section
 */
public class NotesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "NoteFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private DatabaseHandler dbHelper;
    private ListView taskListView;
    NotesAdapter notesAdapter;

    public static final int COL_TASK_ID = 0;
    public static final int COL_TASK_NAME = 1;


    //Method used for displaying the menu allowing the user to add new tasks to the list.
    public Dialog addItemMenu(){
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add New Task")
                .setView(taskEditText)
                .setPositiveButton(R.string.todoAcceptBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Gets the input from the text field
                        String userInput = taskEditText.getText().toString();

                        //Setting up the database to be written to
                        DatabaseHandler dbHelper = new DatabaseHandler(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        //Put the user input into ContentValues format
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(DatabaseContract.TaskEntry.COL_TASK_TITLE, userInput);

                        //Inserts the user input into the database
                        db.insertWithOnConflict(
                                DatabaseContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_IGNORE);

                        //New Query to get the updated values from the database
                        Cursor cursor = db.query(DatabaseContract.TaskEntry.TABLE,
                                new String[]{DatabaseContract.TaskEntry._ID, DatabaseContract.TaskEntry.COL_TASK_TITLE},
                                null, null, null, null, null);

                        //The old data is swapped for new so the new task can be seen in the list.
                        notesAdapter.swapCursor(cursor);
                        //Toast to confirm that the task has been added.
                        Toast.makeText(getActivity(),"Task " + taskEditText.getText() + " added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.todoCancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Does nothing, just closes the alert dialog
                    }
                });
        return builder.create();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);

        dbHelper = new DatabaseHandler(getActivity());
        taskListView = (ListView) rootView.findViewById(R.id.todoList);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemMenu().show();
            }
        });

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.TaskEntry.TABLE,
                new String[]{DatabaseContract.TaskEntry._ID, DatabaseContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(DatabaseContract.TaskEntry.COL_TASK_TITLE);
            Log.d(TAG, "Task: " + cursor.getString(idx));
        }

        notesAdapter = new NotesAdapter(getActivity(), cursor);
        taskListView.setAdapter(notesAdapter);

        return rootView;


    }
}
