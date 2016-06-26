package com.uchicago.yifan.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.uchicago.yifan.todolist.data.ToDoContract;

import java.util.Calendar;

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

        final Calendar myCalendar = Calendar.getInstance();



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTextView(year, monthOfYear, dayOfMonth);
            }

        };

        TextView dateTextView = (TextView)findViewById(R.id.label_date_edit);
        assert dateTextView != null;
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditItemActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        TextView timeTextView = (TextView)findViewById(R.id.label_time_edit);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {


            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTimeTextView(hourOfDay, minute);
            }
        };

        assert timeTextView != null;
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditItemActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(EditItemActivity.this)).show();
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


                dateTextView.setText(convertDateDataToStr(cursor.getString(2)));
                timeTextView.setText(convertTimeDataToStr(cursor.getString(3)));

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

    private String convertDateDataToStr(String date){
        //20160626
        int number  = Integer.parseInt(date);
        int year = number/10000;
        int res = number - year * 10000;
        int month = res/100;
        int day = res - month*100;
        return year + "-" + month + "-" + day;
    }

    private String convertTimeDataToStr(String time){
        //214013
        int number  = Integer.parseInt(time);
        int hour = number/10000;
        int res = number - hour * 10000;
        int min = res/100;
        return hour + ":" + min;
    }

    public void updateDateTextView(int year, int month, int day){
        TextView dateTextView = (TextView)findViewById(R.id.label_date_edit);
        dateTextView.setText(year + "-" + (month+1) + "-" + day);
    }

    public void updateTimeTextView(int hourOfDay, int minute){
        TextView timeTextView = (TextView)findViewById(R.id.label_time_edit);
        timeTextView.setText(hourOfDay + ":" + minute);
    }
}
