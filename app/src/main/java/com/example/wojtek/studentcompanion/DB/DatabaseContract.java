package com.example.wojtek.studentcompanion.DB;

import android.provider.BaseColumns;

/**
 * Created by Wojtek on 04/12/2016.
 */

public class DatabaseContract {
    public static final String DB_NAME = "com.example.wojtek.studentcompanion.DB";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }
}
