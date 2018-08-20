package com.zyyoona7.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zyyoona7.picker.ex.DayWheelView;
import com.zyyoona7.picker.ex.MonthWheelView;
import com.zyyoona7.picker.ex.YearWheelView;
import com.zyyoona7.wheel.WheelView;

import java.util.Calendar;
import java.util.Date;

public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "Main3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        YearWheelView yearWv = findViewById(R.id.wv_year);
        MonthWheelView monthWv = findViewById(R.id.wv_month);
        final DayWheelView dayWv = findViewById(R.id.wv_day);
        yearWv.setYearRange(1900,2099);
        yearWv.setSelectedYear(2018);
        monthWv.setSelectedMonth(8);
        dayWv.setYearAndMonth(2018,8);

        yearWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Date>() {
            @Override
            public void onItemSelected(WheelView<Date> wheelView, Date data, int position) {
                dayWv.setYear(data);
            }
        });

        monthWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Date>() {
            @Override
            public void onItemSelected(WheelView<Date> wheelView, Date data, int position) {
                dayWv.setMonth(data);
            }
        });

        dayWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Date>() {
            @Override
            public void onItemSelected(WheelView<Date> wheelView, Date data, int position) {
                Log.d(TAG, "onItemSelected: date="+data.toString());
            }
        });
    }
}
