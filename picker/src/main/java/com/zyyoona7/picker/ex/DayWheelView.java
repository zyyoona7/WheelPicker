package com.zyyoona7.picker.ex;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.zyyoona7.picker.R;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 日 WheelView
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/20.
 */
public class DayWheelView extends WheelView<Integer> {
    private static final SparseArray<List<Integer>> DAYS = new SparseArray<>(1);

    private int mYear;
    private int mMonth;
    private Calendar mCalendar;

    public DayWheelView(Context context) {
        this(context, null);
    }

    public DayWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCalendar = Calendar.getInstance();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DayWheelView);
        mYear = typedArray.getInt(R.styleable.DayWheelView_wv_year, mCalendar.get(Calendar.YEAR));
        mMonth = typedArray.getInt(R.styleable.DayWheelView_wv_month, mCalendar.get(Calendar.MONTH) + 1);
        int selectedDay = typedArray.getInt(R.styleable.DayWheelView_wv_selectedDay, 1);
        typedArray.recycle();
        updateDay();
        setSelectedDay(selectedDay);
    }

    public void setYearAndMonth(int year, int month) {
        mYear = year;
        mMonth = month;
        updateDay();
    }


    public void setYear(int year) {
        mYear = year;
        updateDay();
    }

    public int getYear() {
        return mYear;
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
        mCalendar.roll(Calendar.DATE, -1);

        int days = mCalendar.get(Calendar.DATE);
        List<Integer> data = DAYS.get(days);
        if (data == null) {
            data = new ArrayList<>(1);
            for (int i = 1; i <= days; i++) {
                data.add(i);
            }
            DAYS.put(days, data);
        }
        super.setData(data);
    }

    public int getSelectedDay() {
        return getItemData(getSelectedItemPosition());
    }

    public void setSelectedDay(int selectedDay) {
        setSelectedDay(selectedDay, false);
    }

    public void setSelectedDay(int selectedDay, boolean isSmoothScroll) {
        setSelectedDay(selectedDay, isSmoothScroll, 0);
    }

    public void setSelectedDay(int selectedDay, boolean isSmoothScroll, int smoothDuration) {
        int days = mCalendar.get(Calendar.DATE);
        if (selectedDay >= 1 && selectedDay <= days) {
            updateSelectedDay(selectedDay, isSmoothScroll, smoothDuration);
        }
    }

    private void updateSelectedDay(int selectedDay, boolean isSmoothScroll, int smoothDuration) {
        setSelectedItemPosition(selectedDay - 1, isSmoothScroll, smoothDuration);
    }

    @Override
    public void setData(List<Integer> dataList) {
        throw new UnsupportedOperationException("You can not invoke setData method in " + DayWheelView.class.getSimpleName() + ".");
    }
}
