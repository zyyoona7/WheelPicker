package com.zyyoona7.demo;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zyyoona7.picker.DatePickerView;
import com.zyyoona7.picker.base.BaseDatePickerView;
import com.zyyoona7.picker.ex.DayWheelView;
import com.zyyoona7.picker.ex.MonthWheelView;
import com.zyyoona7.picker.ex.YearWheelView;
import com.zyyoona7.picker.listener.OnDateSelectedListener;
import com.zyyoona7.wheel.WheelView;
import com.zyyoona7.wheel.formatter.IntegerItemTextFormatter;

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
        defaultDpv.setCurved(false);
        defaultDpv.setVisibleItems(3);
        defaultDpv.setSelectedMonth(2);
        //设置最大选择日期
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(Calendar.YEAR, 2018);
        defaultDpv.setMaxDate(maxCalendar);
        //设置最小选择日期
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(Calendar.YEAR, 2000);
        minCalendar.set(Calendar.MONTH, 5);
        minCalendar.set(Calendar.DAY_OF_MONTH, 15);
        defaultDpv.setMinDate(minCalendar);

        DatePickerView yearMonthDpv = findViewById(R.id.dpv_year_month);
        yearMonthDpv.setTextSize(24, true);
        yearMonthDpv.hideDayItem();
        defaultDpv.setLabelTextSize(20);

        yearMonthDpv.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(BaseDatePickerView datePickerView, int year, int month, int day, @Nullable Date date) {
                Toast.makeText(Main3Activity.this, "选中：" + year + "-" + month, Toast.LENGTH_SHORT).show();
            }
        });

        DatePickerView customDpv1 = findViewById(R.id.dpv_custom_1);
        customDpv1.setTextSize(24, true);
        customDpv1.setShowLabel(false);
        customDpv1.getYearWv().setTextBoundaryMargin(16, true);
        customDpv1.getMonthWv().setTextBoundaryMargin(16, true);
        customDpv1.getDayWv().setTextBoundaryMargin(16, true);
        customDpv1.setHasCurtain(true);
        customDpv1.setCurtainColor(Color.parseColor("#F5F5F5"));

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
//        monthWv2.setIntegerNeedFormat("%02d");
        monthWv2.setItemTextFormatter(new IntegerItemTextFormatter());
//        dayWv2.setIntegerNeedFormat("%02d");
        dayWv2.setItemTextFormatter(new IntegerItemTextFormatter());
        customDpv2.setResetSelectedPosition(true);

        final DatePickerView customDpv3 = findViewById(R.id.dpv_custom_3);
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
        yearWv3.setItemTextFormatter(new IntegerItemTextFormatter("%d年"));
        monthWv3.setItemTextFormatter(new IntegerItemTextFormatter("%d月"));
        dayWv3.setItemTextFormatter(new IntegerItemTextFormatter("%02d日"));
        yearWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_LEFT);
        yearWv3.setCurvedArcDirectionFactor(0.65f);
        dayWv3.setCurvedArcDirection(WheelView.CURVED_ARC_DIRECTION_RIGHT);
        dayWv3.setCurvedArcDirectionFactor(0.65f);

        customDpv3.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(BaseDatePickerView datePickerView, int year,
                                       int month, int day, @Nullable Date date) {
                //                Toast.makeText(Main3Activity.this,"选中的日期："+date.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(Main3Activity.this, "选中的日期：" + year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            }
        });

        final AppCompatCheckBox smoothCb = findViewById(R.id.cb_smooth);
        final AppCompatSeekBar smoothDurationSb = findViewById(R.id.sb_smooth_duration);
        smoothDurationSb.setMax(3000);
        smoothDurationSb.setProgress(250);
        final AppCompatEditText yearEt = findViewById(R.id.et_year);
        AppCompatButton setYearBtn = findViewById(R.id.btn_set_year);
        final AppCompatEditText monthEt = findViewById(R.id.et_month);
        AppCompatButton setMonthBtn = findViewById(R.id.btn_set_month);
        final AppCompatEditText dayEt = findViewById(R.id.et_day);
        AppCompatButton setDayBtn = findViewById(R.id.btn_set_day);

        setYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = yearEt.getText().toString();
                if (!TextUtils.isEmpty(year)) {
                    customDpv3.setSelectedYear(Integer.parseInt(year), smoothCb.isChecked(), smoothDurationSb.getProgress());
                }
            }
        });

        setMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = monthEt.getText().toString();
                if (!TextUtils.isEmpty(month)) {
                    customDpv3.setSelectedMonth(Integer.parseInt(month), smoothCb.isChecked(), smoothDurationSb.getProgress());
                }
            }
        });


        final NestedScrollView nestedScrollView = findViewById(R.id.nsv_main3);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(TAG, "onScrollChange: scrollY=" + scrollY);
            }
        });

        setDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String day = dayEt.getText().toString();
//                if (!TextUtils.isEmpty(day)) {
//                    customDpv3.setSelectedDay(Integer.parseInt(day), smoothCb.isChecked(), smoothDurationSb.getProgress());
//                }

                nestedScrollView.setScrollY(10);
            }
        });
    }
}
