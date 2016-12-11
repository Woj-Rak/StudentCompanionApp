package com.example.wojtek.studentcompanion.DB;

import android.provider.BaseColumns;

/**
 * Created by Wojtek on 04/12/2016.
 */

public class MarkerDBContract {
    public static final String DB_NAME = "com.example.wojtek.studentcompanion.markerDB";
    public static final int DB_VERSION = 4;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "markers";
        public static final String COL_MARKER_TITLE = "title";
        public static final String COL_MARKER_LATITUDE = "latitude";
        public static final String COL_MARKER_LONGITUDE = "longitude";
    }
}