package com.example.wojtek.studentcompanion.Adapters;

/**
 * Created by Wojtek on 08/12/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wojtek.studentcompanion.DB.*;
import com.example.wojtek.studentcompanion.Fragments.NotesFragment;
import com.example.wojtek.studentcompanion.R;

import static android.R.attr.duration;

public class NotesAdapter extends CursorAdapter {

    private static Context context;
    DatabaseHandler helper;

    public NotesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
        helper = new DatabaseHandler(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find the components of the item_todo.xml
        TextView textView = (TextView) view.findViewById(R.id.task_title);
        Button taskDone = (Button) view.findViewById(R.id.task_delete);

        // Get the values from the cursor
        final String id = cursor.getString(NotesFragment.COL_TASK_ID);
        final String task = cursor.getString(NotesFragment.COL_TASK_NAME);

        //Populates the listview with the values and also give functionality to the button.
        textView.setText(task);
        taskDone.setOnClickListener(new View.OnClickListener() {
            //Sets the tick button with functionality to actually delete the chosen task.
            @Override
            public void onClick(View v) {
                //Set the SQL query to delete the task with a specific ID.
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        DatabaseContract.TaskEntry.TABLE,
                        DatabaseContract.TaskEntry._ID,
                        id);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                //The SQL query is executed here and the task is deleted from the DB.
                sqlDB.execSQL(sql);
                notifyDataSetChanged();

                /*Queries the database again for all the entries resulting in the new tasks
                * being displayed in the list view immediately after being added.*/
                Cursor cursor = sqlDB.query(DatabaseContract.TaskEntry.TABLE,
                        new String[]{DatabaseContract.TaskEntry._ID,
                                DatabaseContract.TaskEntry.COL_TASK_TITLE},
                        null,null,null,null,null);
                swapCursor(cursor);

                //Toast to confirm that the task has been removed from the list
                Toast.makeText(context,"Task Completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
