package com.uchicago.yifan.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Yifan on 6/22/16.
 */
public class ToDoProvider extends ContentProvider {

    //private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ToDoDBHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sToDoListQueryBuilder;

    static final int TODO = 100;
    static final int TODO_WITH_DATE_TIME = 101;

    static {
        sToDoListQueryBuilder = new SQLiteQueryBuilder();

        sToDoListQueryBuilder.setTables(ToDoContract.ToDoEntry.TABLE_NAME);
    }


    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ToDoContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ToDoContract.PATH_TODOLIST, TODO);
        matcher.addURI(authority, ToDoContract.PATH_TODOLIST + "/#/#", TODO_WITH_DATE_TIME);

        return matcher;
    }

    private static final String sDateAndTimeSelection =
            ToDoContract.ToDoEntry.TABLE_NAME +
                    "." + ToDoContract.ToDoEntry.COLUMN_DATE + " = ? AND " +
                    ToDoContract.ToDoEntry.COLUMN_TIME + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new ToDoDBHelper(getContext());
        return true;
    }

    private Cursor getMovieWithDateAndTime(Uri uri, String[] projection, String sortOrder){
        String todo_date = ToDoContract.ToDoEntry.getTodoItemDateFromUri(uri);
        String todo_time = ToDoContract.ToDoEntry.getTodoItemTimeFromUri(uri);

        return sToDoListQueryBuilder.query(mOpenHelper.getWritableDatabase(),
                                           projection,
                                           sDateAndTimeSelection,
                                           new String[]{todo_date, todo_time},
                                           null,
                                           null,
                                           sortOrder);
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch ( sUriMatcher.match(uri)){
            case TODO:{
                retCursor = mOpenHelper.getReadableDatabase().query(ToDoContract.ToDoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case TODO_WITH_DATE_TIME:{
                retCursor = getMovieWithDateAndTime(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case TODO:
                return ToDoContract.ToDoEntry.CONTENT_TYPE;
            case TODO_WITH_DATE_TIME:
                return ToDoContract.ToDoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case TODO:{
                long _id = db.insert(ToDoContract.ToDoEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = ToDoContract.ToDoEntry.buildToDoItemUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into TODO_LIST TABLE: " + uri);
                }

                break;
            }
            default:
                throw new android.database.SQLException("Failed to insert row into: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri))
        {
            case TODO:{
                rowsDeleted = db.delete(
                        ToDoContract.ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TODO_WITH_DATE_TIME:{
                String todo_date = ToDoContract.ToDoEntry.getTodoItemDateFromUri(uri);
                String todo_time = ToDoContract.ToDoEntry.getTodoItemTimeFromUri(uri);
                rowsDeleted = db.delete(ToDoContract.ToDoEntry.TABLE_NAME,
                        ToDoContract.ToDoEntry.COLUMN_DATE + " = ?" + ToDoContract.ToDoEntry.COLUMN_TIME + " = ?", new String[]{todo_date,todo_time});

                Log.d("DELETE: ", "todo item " + String.valueOf(ContentUris.parseId(uri)) + " is deleted.");

                int fav = getContext().getContentResolver().query(
                        ToDoContract.ToDoEntry.CONTENT_URI,
                        null, null, null, null).getCount();

                Log.d("SUM: ", "There're " + fav + " todo items.");

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case TODO:
                rowsUpdated = db.update(ToDoContract.ToDoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
