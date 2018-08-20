package com.zyyoona7.picker.ex;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/20.
 */
public class DayWheelView extends BaseDateWheelView {
    private static final SparseArray<List<Date>> DAYS = new SparseArray<>(1);

    private int mYear;
    private int mMonth;
    private int mSelectedDay;

    public DayWheelView(Context context) {
        this(context, null);
    }

    public DayWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSimpleDateFormat = new SimpleDateFormat("dd", Locale.getDefault());
    }

    public void setSelectedDay(int selectedDay) {
        int days = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (selectedDay >= 1 && selectedDay <= days) {
            mSelectedDay = selectedDay;
            updateSelectedDay();
        }
    }

    public int getSelectedDay() {
        return mSelectedDay;
    }

    public void setYearAndMonth(Date yearAndMonthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yearAndMonthDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        setYearAndMonth(year, month);
    }

    public void setYearAndMonth(int year, int month) {
        mYear = year;
        mMonth = month;
        updateDay();
    }

    public void setYear(Date yearDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yearDate);
        int year = calendar.get(Calendar.YEAR);
        setYear(year);
    }

    public void setYear(int year) {
        mYear = year;
        updateDay();
    }

    public int getYear() {
        return mYear;
    }

    public void setMonth(Date monthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthDate);
        int month = calendar.get(Calendar.MONTH) + 1;
        setMonth(month);
    }

    public void setMonth(int month) {
        mMonth = month;
        updateDay();
    }

    public int getMonth() {
        return mMonth;
    }

    private void updateDay() {
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.MONTH, mMonth - 1);
        mCalendar.set(Calendar.DATE, 1);
        resetUnderDay();
        mCalendar.roll(Calendar.DATE, -1);

        int days = mCalendar.get(Calendar.DATE);
        List<Date> data = DAYS.get(days);
        if (data == null) {
            data = new ArrayList<>(1);
            for (int i = 1; i <= days; i++) {
                mCalendar.set(Calendar.DAY_OF_MONTH, i);
                data.add(mCalendar.getTime());
            }
            DAYS.put(days, data);
        }
        super.setData(data);
    }

    private void updateSelectedDay() {
        setCurrentItemPosition(mSelectedDay - 1);
    }

    public Date getCurrentDate(){
        return getData().get(getCurrentItemPosition());
    }
}
