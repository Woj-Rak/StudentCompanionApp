package com.example.wojtek.studentcompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Wojtek on 01/12/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contactTable (colID, colName, colPhone)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public DatabaseHandler(Context context){
        super(context, "testDB", null, 1);
    }

}


