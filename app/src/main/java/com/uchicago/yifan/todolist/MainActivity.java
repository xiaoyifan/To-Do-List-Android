package com.uchicago.yifan.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.uchicago.yifan.todolist.data.ToDoContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class MainActivity extends ActionBarActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        //readItems();

    }

    @Override
    protected void onResume() {
        super.onResume();

        readFromDB();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

        itemsAdapter.notifyDataSetChanged();
    }

    public void onAddItem(View v){
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        //writeItems();
        insertRecordToDB(itemText);

        etNewItem.setText("");
    }

    public void insertRecordToDB(String newTitle){

        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.YEAR)*10000 + c.get(Calendar.MONTH)*100 + c.get(Calendar.DATE);
        int time = c.get(Calendar.HOUR_OF_DAY)*100 + c.get(Calendar.MINUTE);

        ContentValues todoValues = new ContentValues();
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_TITLE,  newTitle);
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_TIME, time);
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_DATE, date);
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_NOTE, "");
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_PRIORITY, "2");
        todoValues.put(ToDoContract.ToDoEntry.COLUMN_STATUS, "0");
        getContentResolver().insert(ToDoContract.ToDoEntry.CONTENT_URI, todoValues);
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                deleteItemFromDB(items.get(position));
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String todo_item = itemsAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, todo_item);
                startActivity(intent);
            }
        });
    }

    private void readFromDB(){

        Cursor cursor = getContentResolver().query(ToDoContract.ToDoEntry.CONTENT_URI, null, null, null, null);

        Vector<ContentValues> vector = new Vector<ContentValues>(cursor.getCount());
        if (cursor.moveToFirst()){
            do{
                ContentValues contentValue = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValue);
                vector.add(contentValue);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        items = convertContentValuesToStringFormat(vector);

    }

    private void deleteItemFromDB(String title){

        getContentResolver().delete(ToDoContract.ToDoEntry.CONTENT_URI, ToDoContract.ToDoEntry.COLUMN_TITLE + " = ?", new String[]{title});
    }

    private ArrayList<String> convertContentValuesToStringFormat(Vector<ContentValues> vector){

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < vector.size(); i++)
        {
          ContentValues todoValues = vector.elementAt(i);
          list.add(todoValues.getAsString(ToDoContract.ToDoEntry.COLUMN_TITLE));
        }

        return list;
    }

}
