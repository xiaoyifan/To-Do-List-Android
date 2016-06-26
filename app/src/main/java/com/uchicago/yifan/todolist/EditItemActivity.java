package com.uchicago.yifan.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uchicago.yifan.todolist.data.ToDoContract;

public class EditItemActivity extends AppCompatActivity {

    String itemTitle = "";
    ArrayAdapter<CharSequence> PriorityAdapter;
    ArrayAdapter<CharSequence> StatusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Button button = (Button) findViewById(R.id.button);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("dddddd");





            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            itemTitle = intent.getStringExtra(Intent.EXTRA_TEXT);

            Cursor cursor = getContentResolver().query(ToDoContract.ToDoEntry.CONTENT_URI,
                                                        new String[]{
                                                                ToDoContract.ToDoEntry._ID,
                                                                ToDoContract.ToDoEntry.COLUMN_TITLE,
                                                                ToDoContract.ToDoEntry.COLUMN_DATE,
                                                                ToDoContract.ToDoEntry.COLUMN_TIME,
                                                                ToDoContract.ToDoEntry.COLUMN_STATUS,
                                                                ToDoContract.ToDoEntry.COLUMN_PRIORITY,
                                                                ToDoContract.ToDoEntry.COLUMN_NOTE
                                                        },
                                                        ToDoContract.ToDoEntry.COLUMN_TITLE + " = ?",
                                                        new String[]{itemTitle},
                                                        null);


            assert cursor != null;
            if (cursor.moveToFirst()){

                EditText titleText = (EditText) findViewById(R.id.etTextEdit1);
                titleText.setText(cursor.getString(1));

                Spinner prioritySpinner = (Spinner)findViewById(R.id.spinnerPriority);
                PriorityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.priority_array, android.R.layout.simple_spinner_item);
                PriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                prioritySpinner.setAdapter(PriorityAdapter);

                TextView dateTextView = (TextView)findViewById(R.id.label_date_edit);
                dateTextView.setText(cursor.getString(2));

                TextView timeTextView = (TextView)findViewById(R.id.label_time_edit);
                timeTextView.setText(cursor.getString(3));

                Spinner statusSpinner = (Spinner)findViewById(R.id.spinnerStatus);
                StatusAdapter = ArrayAdapter.createFromResource(this,
                        R.array.status_array, android.R.layout.simple_spinner_item);
                StatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                statusSpinner.setAdapter(StatusAdapter);

                EditText noteText = (EditText)findViewById(R.id.text_note);
                noteText.setText(cursor.getString(6));
            }

        }


    }

}
