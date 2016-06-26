package com.uchicago.yifan.todolist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yifan on 6/22/16.
 */
public class ToDoContract {

    public static final String CONTENT_AUTHORITY = "com.uchicago.yifan.todolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODOLIST = "todolist";

    public static final class ToDoEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODOLIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOLIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOLIST;

        public static final String TABLE_NAME = "todolist";

        // title is stored as a string format
        public static final String COLUMN_TITLE = "title";

        // Date, stored as long in milliseconds
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";

        // Priority is stored as priority in String format.
        public static final String COLUMN_PRIORITY = "priority";

        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_NOTE = "note";

        public static Uri buildToDoItemUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getTodoItemDateFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getTodoItemTimeFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getTodoItemTitleFromUri(Uri uri){
            return uri.getPathSegments().get(0);
        }
    }
}
