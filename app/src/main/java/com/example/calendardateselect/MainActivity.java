package com.example.calendardateselect;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);

        //range date
        List<CalendarDay> calendarDays = new ArrayList<>();

        calendarDays.add(new CalendarDay(year, month, 1));
        calendarDays.add(new CalendarDay(year, month, 3));
        calendarDays.add(new CalendarDay(year, month, 4));
        calendarDays.add(new CalendarDay(year, month, 5));
        calendarDays.add(new CalendarDay(year, month, 6));
        calendarDays.add(new CalendarDay(year, month, 9));
        calendarDays.add(new CalendarDay(year, month, 12));
        calendarDays.add(new CalendarDay(year, month, 14));
        calendarDays.add(new CalendarDay(year, month, 15));
        calendarDays.add(new CalendarDay(year, month, 21));
        calendarDays.add(new CalendarDay(year, month, 22));

        findViewById(R.id.open_calendar).setOnClickListener(view -> new ModalCalendar.Builder()
                .setRangeDays(calendarDays)
                .setSelectionColor("#FF3700B3")
                .setColorDefaultRange("#CECECE")
                .onDateSelected(value -> Toast.makeText(MainActivity.this, String.valueOf(value.getDate()), Toast.LENGTH_SHORT).show())
                .show(getSupportFragmentManager()));
    }
}