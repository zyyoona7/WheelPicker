package com.zyyoona7.picker.ex;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

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
public class YearWheelView extends BaseDateWheelView {

    private int mStartYear;
    private int mEndYear;
    private int mSelectedYear;

    public YearWheelView(Context context) {
        this(context, null);
    }

    public YearWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mSimpleDateFormat = new SimpleDateFormat("yyyy年", Locale.getDefault());
    }


    public void setYearRange(int start, int end) {
        mStartYear = start;
        mEndYear = end;
        updateYear();
    }

    public void updateYear() {
        resetUnderYear();
        List<Date> list = new ArrayList<>(1);
        for (int i = mStartYear; i < mEndYear; i++) {
            mCalendar.set(Calendar.YEAR, i);
            list.add(mCalendar.getTime());
        }
        super.setData(list);
        mCalendar.setTime(getData().get(getCurrentItemPosition()));
        mSelectedYear = mCalendar.get(Calendar.YEAR);
    }

    public void updateSelectedYear() {
        setCurrentItemPosition(mSelectedYear - mStartYear);
    }

    public int getSelectedYear() {
        return mSelectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        if (selectedYear >= mStartYear && selectedYear <= mEndYear) {
            this.mSelectedYear = selectedYear;
            updateSelectedYear();
        }
    }
}
