package com.uchicago.yifan.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uchicago.yifan.todolist.data.ToDoContract.ToDoEntry;

/**
 * Created by Yifan on 6/22/16.
 */
public class ToDoDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "todo.db";

    public ToDoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + ToDoEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ToDoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ToDoEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                ToDoEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                ToDoEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                ToDoEntry.COLUMN_NOTE + " TEXT NOT NULL, " +
                ToDoEntry.COLUMN_PRIORITY + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ToDoEntry.TABLE_NAME);
        onCreate(db);
    }
}
