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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wojtek.studentcompanion.DB.DatabaseContract;
import com.example.wojtek.studentcompanion.DB.DatabaseHandler;
import com.example.wojtek.studentcompanion.R;

import java.util.ArrayList;

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

    private DatabaseHandler mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    //Method Used to display a menu for adding a new note/task
    public Dialog addItemMenu(){
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add New Task")
                .setView(taskEditText)
                .setPositiveButton(R.string.todoAcceptBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.TaskEntry.COL_TASK_TITLE, task);
                        db.insertWithOnConflict(DatabaseContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateList();
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

    private void updateList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.TaskEntry.TABLE,
                new String[]{DatabaseContract.TaskEntry._ID, DatabaseContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(DatabaseContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }


        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_todo,
                    R.id.task_title,
                    taskList);

            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }


    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DatabaseContract.TaskEntry.TABLE,
                DatabaseContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateList();
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

        mHelper = new DatabaseHandler(getActivity());
        mTaskListView = (ListView) rootView.findViewById(R.id.todoList);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemMenu().show();
            }
        });

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.TaskEntry.TABLE,
                new String[]{DatabaseContract.TaskEntry._ID, DatabaseContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(DatabaseContract.TaskEntry.COL_TASK_TITLE);
            Log.d(TAG, "Task: " + cursor.getString(idx));
        }

        cursor.close();
        db.close();
        updateList();

        return rootView;

    }
}
