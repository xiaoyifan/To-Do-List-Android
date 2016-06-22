package com.uchicago.yifan.todolist.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Yifan on 6/22/16.
 */
public class ToDoContract {

    public static final class ToDoEntry implements BaseColumns {

        public static final String TABLE_NAME = "todolist";

        // title is stored as a string format
        public static final String COLUMN_TITLE = "title";

        // Date, stored as long in milliseconds
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";

        // Priority is stored as priority in String format.
        public static final String COLUMN_PRIORITY = "priority";

    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
