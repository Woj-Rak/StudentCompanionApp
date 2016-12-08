package com.example.wojtek.studentcompanion.Activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wojtek.studentcompanion.DB.DatabaseContract;
import com.example.wojtek.studentcompanion.DB.DatabaseHandler;
import com.example.wojtek.studentcompanion.R;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
    private static final String TAG = "New Note Activity";

    private DatabaseHandler mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    public Dialog addItemMenu(){
        final EditText taskEditText = new EditText(NotesActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
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
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHelper = new DatabaseHandler(this);
        mTaskListView = (ListView) findViewById(R.id.todoList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

}
