package com.uchicago.yifan.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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

    long recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Button button = (Button) findViewById(R.id.button);
        final TextView titleTextView = (TextView)findViewById(R.id.etTextEdit1);
        final TextView dateTextView = (TextView)findViewById(R.id.label_date_edit);
        final TextView timeTextView = (TextView)findViewById(R.id.label_time_edit);

        final EditText noteText = (EditText)findViewById(R.id.text_note);

        final Spinner prioritySpinner = (Spinner)findViewById(R.id.spinnerPriority);
        final Spinner statusSpinner = (Spinner)findViewById(R.id.spinnerStatus);

        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues updateValues = new ContentValues();
                updateValues.put(ToDoContract.ToDoEntry._ID, recordId);
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_TITLE, titleTextView.getText().toString());
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_STATUS, statusSpinner.getSelectedItem().toString());
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_PRIORITY, prioritySpinner.getSelectedItem().toString());
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_DATE, String.valueOf(getIntValueOfDate(dateTextView.getText().toString())) );
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_TIME, String.valueOf(getIntValueOfTime(timeTextView.getText().toString())));
                updateValues.put(ToDoContract.ToDoEntry.COLUMN_NOTE,  noteText.getText().toString());

                getContentResolver().update(ToDoContract.ToDoEntry.CONTENT_URI, updateValues,
                        ToDoContract.ToDoEntry._ID + " = ?", new String[]{Long.toString(recordId)});

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

        assert dateTextView != null;
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditItemActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

                recordId = cursor.getLong(0);

                EditText titleText = (EditText) findViewById(R.id.etTextEdit1);
                titleText.setText(cursor.getString(1));

                PriorityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.priority_array, android.R.layout.simple_spinner_item);
                PriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                prioritySpinner.setAdapter(PriorityAdapter);


                dateTextView.setText(convertDateDataToStr(cursor.getString(2)));
                timeTextView.setText(convertTimeDataToStr(cursor.getString(3)));

                StatusAdapter = ArrayAdapter.createFromResource(this,
                        R.array.status_array, android.R.layout.simple_spinner_item);
                StatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                statusSpinner.setAdapter(StatusAdapter);

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
        //2140
        int number  = Integer.parseInt(time);
        int hour = number/100;
        int min = number - hour * 100;
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

    private int getIntValueOfDate(String dateText){
        String[] array = dateText.split("-");
        return Integer.parseInt(array[0])*10000 + Integer.parseInt(array[1])*100 + Integer.parseInt(array[2]);
    }

    private int getIntValueOfTime(String timeText){
        String[] array = timeText.split(":");
        return Integer.parseInt(array[0])*100 + Integer.parseInt(array[1]);
    }
}
