package com.zyyoona7.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.zyyoona7.picker.DatePickerView;
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
//        yearWv.setYearRange(1900,2099);
//        yearWv.setSelectedYear(2018);
//        yearWv.setIntegerNeedFormat("%d年");
//        monthWv.setSelectedMonth(8);
//        monthWv.setIntegerNeedFormat("Mon %d");
//        dayWv.setYearAndMonth(2018,8);

        yearWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Integer>() {
            @Override
            public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
                dayWv.setYear(data);
            }
        });

        monthWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Integer>() {
            @Override
            public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
                dayWv.setMonth(data);
            }
        });

        dayWv.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<Integer>() {
            @Override
            public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
                Log.d(TAG, "onItemSelected: date=" + dayWv.getYear() + "-" + dayWv.getMonth() + "-" + dayWv.getSelectedDay());
            }
        });

        DatePickerView defaultDpv = findViewById(R.id.dpv_default);
        defaultDpv.setTextSize(24, true);
        defaultDpv.setLabelTextSize(20);

        DatePickerView customDpv1 = findViewById(R.id.dpv_custom_1);
        customDpv1.setTextSize(24, true);
        customDpv1.setShowLabel(false);
        customDpv1.getYearWv().setTextBoundaryMargin(16, true);
        customDpv1.getMonthWv().setTextBoundaryMargin(16, true);
        customDpv1.getDayWv().setTextBoundaryMargin(16, true);
        customDpv1.setDrawSelectedRect(true);
        customDpv1.setSelectedRectColor(Color.parseColor("#F5F5F5"));

        DatePickerView customDpv2 = findViewById(R.id.dpv_custom_2);
        customDpv2.setTextSize(24, true);
        customDpv2.setShowLabel(false);
        customDpv2.setTextBoundaryMargin(16, true);
        customDpv2.setShowDivider(true);
        customDpv2.setDividerType(WheelView.DIVIDER_TYPE_WRAP);
        customDpv2.setDividerColor(Color.parseColor("#9e9e9e"));
        customDpv2.setDividerPaddingForWrap(10, true);
        YearWheelView yearWv2 = customDpv2.getYearWv();
        MonthWheelView monthWv2 = customDpv2.getMonthWv();
        DayWheelView dayWv2 = customDpv2.getDayWv();
        monthWv2.setIntegerNeedFormat("%02d");
        dayWv2.setIntegerNeedFormat("%02d");

        DatePickerView customDpv3 = findViewById(R.id.dpv_custom_3);
        customDpv3.setTextSize(24, true);
        customDpv3.setShowLabel(false);
        customDpv3.setTextBoundaryMargin(16, true);
        customDpv3.setShowDivider(true);
        customDpv3.setDividerType(WheelView.DIVIDER_TYPE_FILL);
        customDpv3.setDividerColor(Color.parseColor("#9e9e9e"));
        customDpv3.setDividerPaddingForWrap(10, true);
        YearWheelView yearWv3 = customDpv3.getYearWv();
        MonthWheelView monthWv3 = customDpv3.getMonthWv();
        DayWheelView dayWv3 = customDpv3.getDayWv();
        yearWv3.setIntegerNeedFormat("%d年");
        monthWv3.setIntegerNeedFormat("%d月");
        dayWv3.setIntegerNeedFormat("%02d日");
        yearWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_LEFT);
        yearWv3.setCurvedArcDirectionFactor(0.65f);
        dayWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_RIGHT);
        dayWv3.setCurvedArcDirectionFactor(0.65f);

        customDpv3.setOnDateSelectedListener(new DatePickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(DatePickerView datePickerView, int year, int month, int day, @Nullable Date date) {
                //                Toast.makeText(Main3Activity.this,"选中的日期："+date.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(Main3Activity.this,"选中的日期："+year+"-"+month+"-"+day,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
