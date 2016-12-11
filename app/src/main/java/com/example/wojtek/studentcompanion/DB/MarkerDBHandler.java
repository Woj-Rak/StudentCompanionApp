package com.example.wojtek.studentcompanion.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

/**
 * Created by Wojtek on 01/12/2016.
 */

public class MarkerDBHandler extends SQLiteOpenHelper {

    public MarkerDBHandler(Context context){
        super(context, MarkerDBContract.DB_NAME, null, MarkerDBContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + MarkerDBContract.TaskEntry.TABLE + " ( " +
                MarkerDBContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MarkerDBContract.TaskEntry.COL_MARKER_TITLE + " TEXT NOT NULL," +
                MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE + " DOUBLE NOT NULL," +
                MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE + " DOUBLE NOT NULL);";


        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MarkerDBContract.TaskEntry.TABLE);
        onCreate(db);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }


}