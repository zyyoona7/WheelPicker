package com.zyyoona7.picker.ex;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.zyyoona7.picker.R;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 月 WheelView
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/20.
 */
public class MonthWheelView extends WheelView<Integer> {

    private int mCurrentSelectedYear = -1;
    private int mMaxYear = -1;
    private int mMinYear = -1;
    private int mMaxMonth = -1;
    private int mMinMonth = -1;

    public MonthWheelView(Context context) {
        this(context, null);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonthWheelView);
        int selectedMonth = typedArray.getInt(R.styleable.MonthWheelView_wvt_selectedMonth,
                Calendar.getInstance().get(Calendar.MONTH) + 1);
        typedArray.recycle();
        initData();
        setSelectedMonth(selectedMonth);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<Integer> list = new ArrayList<>(1);
        for (int i = 1; i <= 12; i++) {
            list.add(i);
        }
        super.setData(list);
    }

    public int getCurrentSelectedYear() {
        return mCurrentSelectedYear;
    }

    public void setCurrentSelectedYear(@IntRange(from = 0) int currentSelectedYear) {
        mCurrentSelectedYear = currentSelectedYear;
        checkCurrentSelected(getSelectedItemData());
    }

    public void setMaxYearAndMonth(@IntRange(from = 0) int maxYear,
                                   @IntRange(from = 1, to = 12) int maxMonth) {
        mMaxYear = maxYear;
        mMaxMonth = maxMonth;
        checkCurrentSelected(getSelectedItemData());
    }

    public void setMaxMonth(@IntRange(from = 1, to = 12) int maxMonth) {
        mMaxMonth = maxMonth;
        checkCurrentSelected(getSelectedItemData());
    }

    public void setMinYearAndMonth(@IntRange(from = 0) int minYear,
                                   @IntRange(from = 1, to = 12) int minMonth) {
        mMinYear = minYear;
        mMinMonth = minMonth;
        checkCurrentSelected(getSelectedItemData());
    }

    public void setMinMonth(@IntRange(from = 1, to = 12) int minMonth) {
        mMinMonth = minMonth;
        checkCurrentSelected(getSelectedItemData());
    }

    /**
     * 获取选中的月
     *
     * @return 选中的月
     */
    public int getSelectedMonth() {
        return getSelectedItemData();
    }

    /**
     * 设置选中的月
     *
     * @param selectedMonth 选中的月
     */
    public void setSelectedMonth(int selectedMonth) {
        setSelectedMonth(selectedMonth, false);
    }

    /**
     * 设置选中的月
     *
     * @param selectedMonth  选中的月
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setSelectedMonth(int selectedMonth, boolean isSmoothScroll) {
        setSelectedMonth(selectedMonth, isSmoothScroll, 0);
    }

    /**
     * 设置选中的月
     *
     * @param selectedMonth  选中的月
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setSelectedMonth(int selectedMonth, boolean isSmoothScroll, int smoothDuration) {
        if (selectedMonth >= 1 && selectedMonth <= 12) {
            int shouldSelected = selectedMonth;
            if (isMoreThanMaxMonth(selectedMonth)) {
                shouldSelected = mMaxMonth;
            } else if (isLessThanMinMonth(selectedMonth)) {
                shouldSelected = mMinMonth;
            }
            updateSelectedMonth(shouldSelected, isSmoothScroll, smoothDuration);
        }
    }

    /**
     * 更新选中的月份
     *
     * @param selectedMonth  选中的月份
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    private void updateSelectedMonth(int selectedMonth, boolean isSmoothScroll, int smoothDuration) {
        setSelectedItemPosition(selectedMonth - 1, isSmoothScroll, smoothDuration);
    }

    @Override
    public void setData(List<Integer> dataList) {
        throw new UnsupportedOperationException("You can not invoke setData method in " + MonthWheelView.class.getSimpleName() + ".");
    }

    @Override
    protected void onItemSelected(Integer data, int position) {
        checkCurrentSelected(data);
    }

    private void checkCurrentSelected(int data) {
        if (isMoreThanMaxMonth(data)) {
            setSelectedMonth(mMaxMonth);
        } else if (isLessThanMinMonth(data)) {
            setSelectedMonth(mMinMonth);
        }
    }

    private boolean isMoreThanMaxMonth(int data) {
        return isCurrentMaxYear() && data > mMaxMonth
                && mMaxMonth > 0;
    }

    private boolean isLessThanMinMonth(int data) {
        return isCurrentMinYear() && data < mMinMonth
                && mMinMonth > 0;
    }

    private boolean isCurrentMaxYear() {
        return (mMaxYear > 0 && mCurrentSelectedYear == mMaxYear)
                || (mCurrentSelectedYear < 0 && mMaxYear < 0 && mMinYear < 0);
    }

    private boolean isCurrentMinYear() {
        return (mCurrentSelectedYear == mMinYear && mMinYear > 0)
                || (mCurrentSelectedYear < 0 && mMaxYear < 0 && mMinYear < 0);
    }
}
