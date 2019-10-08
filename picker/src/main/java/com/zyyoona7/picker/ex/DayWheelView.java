package com.zyyoona7.picker.ex;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
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
    private int mMaxYear = -1;
    private int mMinYear = -1;
    private int mMaxMonth = -1;
    private int mMinMonth = -1;
    private int mMaxDay = -1;
    private int mMinDay = -1;
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
        int selectedDay = typedArray.getInt(R.styleable.DayWheelView_wv_selectedDay, mCalendar.get(Calendar.DATE));
        typedArray.recycle();
        updateDay();
        setSelectedDay(selectedDay);
    }

    /**
     * 同时设置年份和月份
     *
     * @param year  年份
     * @param month 月份
     */
    public void setYearAndMonth(int year, int month) {
        mYear = year;
        mMonth = month;
        updateDay();
    }

    /**
     * 设置年份
     *
     * @param year 年份
     */
    public void setYear(int year) {
        mYear = year;
        updateDay();
    }

    /**
     * 获取年份
     *
     * @return 年份
     */
    public int getYear() {
        return mYear;
    }

    /**
     * 设置月份
     *
     * @param month 月份
     */
    public void setMonth(int month) {
        mMonth = month;
        updateDay();
    }

    /**
     * 获取月份
     *
     * @return 月份
     */
    public int getMonth() {
        return mMonth;
    }

    public void setMaxYearMonthAndDay(@IntRange(from = 0) int maxYear,
                                      @IntRange(from = 1, to = 12) int maxMonth,
                                      @IntRange(from = 1, to = 31) int maxDay) {
        mMaxYear = maxYear;
        mMaxMonth = maxMonth;
        mMaxDay = maxDay;
        checkCurrentSelected(getSelectedItemData());
    }

    public void setMinYearMonthAndDay(@IntRange(from = 0) int minYear,
                                      @IntRange(from = 1, to = 12) int minMonth,
                                      @IntRange(from = 1, to = 31) int minDay) {
        mMinYear = minYear;
        mMinMonth = minMonth;
        mMinDay = minDay;
        checkCurrentSelected(getSelectedItemData());
    }

    /**
     * 更新数据
     */
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
        checkCurrentSelected(getSelectedItemData());
    }

    /**
     * 获取选中的日
     *
     * @return 选中的日
     */
    public int getSelectedDay() {
        return getSelectedItemData();
    }

    /**
     * 设置选中的日
     *
     * @param selectedDay 选中的日
     */
    public void setSelectedDay(int selectedDay) {
        setSelectedDay(selectedDay, false);
    }

    /**
     * 设置选中的日
     *
     * @param selectedDay    选中的日
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setSelectedDay(int selectedDay, boolean isSmoothScroll) {
        setSelectedDay(selectedDay, isSmoothScroll, 0);
    }

    /**
     * 设置选中的日
     *
     * @param selectedDay    选中的日
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setSelectedDay(int selectedDay, boolean isSmoothScroll, int smoothDuration) {
        int days = mCalendar.get(Calendar.DATE);
        if (selectedDay >= 1 && selectedDay <= days) {
            int shouldSelected = selectedDay;
            if (isMoreThanMaxDay(selectedDay)) {
                shouldSelected = mMaxDay;
            } else if (isLessThanMinDay(selectedDay)) {
                shouldSelected = mMinDay;
            }
            updateSelectedDay(shouldSelected, isSmoothScroll, smoothDuration);
        }
    }

    /**
     * 更新选中的日
     *
     * @param selectedDay    选中的日
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    private void updateSelectedDay(int selectedDay, boolean isSmoothScroll, int smoothDuration) {
        setSelectedItemPosition(selectedDay - 1, isSmoothScroll, smoothDuration);
    }

    @Override
    public void setData(List<Integer> dataList) {
        throw new UnsupportedOperationException("You can not invoke setData method in " + DayWheelView.class.getSimpleName() + ".");
    }

    @Override
    protected void onItemSelected(Integer data, int position) {
        checkCurrentSelected(data);
    }

    private void checkCurrentSelected(int data) {
        if (isMoreThanMaxDay(data)) {
            setSelectedDay(mMaxDay);
        } else if (isLessThanMinDay(data)) {
            setSelectedDay(mMinDay);
        }
    }

    private boolean isMoreThanMaxDay(int data) {
        return isCurrentMaxYear() && isCurrentMaxMonth()
                && data > mMaxDay && mMaxDay > 0;
    }

    private boolean isLessThanMinDay(int data) {
        return isCurrentMinYear() && isCurrentMinMonth()
                && data < mMinDay && mMinDay > 0;
    }

    private boolean isCurrentMaxYear() {
        return (mMaxYear > 0 && mYear == mMaxYear)
                || (mYear < 0 && mMaxYear < 0 && mMinYear < 0);
    }

    private boolean isCurrentMinYear() {
        return (mYear == mMinYear && mMinYear > 0)
                || (mYear < 0 && mMaxYear < 0 && mMinYear < 0);
    }

    private boolean isCurrentMaxMonth() {
        return (mMaxMonth > 0 && mMonth == mMaxMonth)
                || (mMonth < 0 && mMaxMonth < 0 && mMinMonth < 0);
    }

    private boolean isCurrentMinMonth() {
        return (mMonth == mMinMonth && mMinMonth > 0)
                || (mMonth < 0 && mMaxMonth < 0 && mMinMonth < 0);
    }
}
