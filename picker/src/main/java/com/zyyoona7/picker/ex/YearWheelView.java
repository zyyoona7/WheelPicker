package com.zyyoona7.picker.ex;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zyyoona7.picker.R;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 年 WheelView
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/20.
 */
public class YearWheelView extends WheelView<Integer> {

    private int mStartYear;
    private int mEndYear;
    private int mMaxYear = -1;
    private int mMinYear = -1;

    public YearWheelView(Context context) {
        this(context, null);
    }

    public YearWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.YearWheelView);
        mStartYear = typedArray.getInt(R.styleable.YearWheelView_wv_startYear, 1900);
        mEndYear = typedArray.getInt(R.styleable.YearWheelView_wv_endYear, 2100);
        int selectedYear = typedArray.getInt(R.styleable.YearWheelView_wv_selectedYear, Calendar.getInstance().get(Calendar.YEAR));
        typedArray.recycle();

        updateYear();
        setSelectedYear(selectedYear);
    }

    /**
     * 设置年份区间
     *
     * @param start 起始年份
     * @param end   结束年份
     */
    public void setYearRange(int start, int end) {
        mStartYear = start;
        mEndYear = end;
        updateYear();
        updateMaxAndMinYear();
    }

    /**
     * 最大选中年份 年份大于这个数字会选中到 maxYear
     *
     * @param maxYear 最大年份
     */
    public void setMaxYear(@IntRange(from = 0) int maxYear) {
        if (maxYear > mEndYear) {
            mMaxYear = mEndYear;
            return;
        }
        mMaxYear = maxYear;
        checkCurrentSelected(getSelectedItemData());
    }

    /**
     * 最小选中年份 年份低于这个数字会选中到 minYear
     *
     * @param minYear 最小年份
     */
    public void setMinYear(@IntRange(from = 0) int minYear) {
        if (minYear < mStartYear) {
            mMinYear = mStartYear;
            return;
        }
        mMinYear = minYear;
        checkCurrentSelected(getSelectedItemData());
    }

    private void updateMaxAndMinYear() {
        if (mMaxYear > mEndYear) {
            mMaxYear = mEndYear;
        }
        if (mMinYear < mStartYear) {
            mMinYear = mStartYear;
        }
        if (mMaxYear < mMinYear) {
            mMaxYear = mMinYear;
        }
    }

    /**
     * 更新年份数据
     */
    private void updateYear() {
        List<Integer> list = new ArrayList<>(1);
        for (int i = mStartYear; i <= mEndYear; i++) {
            list.add(i);
        }
        super.setData(list);
    }


    /**
     * 获取当前选中的年份
     *
     * @return 当前选中的年份
     */
    public int getSelectedYear() {
        return getSelectedItemData();
    }

    /**
     * 设置当前选中的年份
     *
     * @param selectedYear 选中的年份
     */
    public void setSelectedYear(int selectedYear) {
        setSelectedYear(selectedYear, false);
    }

    /**
     * 设置当前选中的年份
     *
     * @param selectedYear   选中的年份
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setSelectedYear(int selectedYear, boolean isSmoothScroll) {
        setSelectedYear(selectedYear, isSmoothScroll, 0);
    }

    /**
     * 设置当前选中的年份
     *
     * @param selectedYear   选中的年份
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    public void setSelectedYear(int selectedYear, boolean isSmoothScroll, int smoothDuration) {
        if (selectedYear >= mStartYear && selectedYear <= mEndYear) {
            int shouldSelected = selectedYear;
            if (isMoreThanMaxYear(selectedYear)) {
                shouldSelected = mMaxYear;
            } else if (isLessThanMinYear(selectedYear)) {
                shouldSelected = mMinYear;
            }
            updateSelectedYear(shouldSelected, isSmoothScroll, smoothDuration);
        }
    }

    /**
     * 更新选中的年份
     *
     * @param selectedYear   选中的年
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动持续时间
     */
    private void updateSelectedYear(int selectedYear, boolean isSmoothScroll, int smoothDuration) {
        setSelectedItemPosition(selectedYear - mStartYear, isSmoothScroll, smoothDuration);
    }

    @Override
    public void setData(List<Integer> dataList) {
        throw new UnsupportedOperationException("You can not invoke setData method in " + YearWheelView.class.getSimpleName() + ".");
    }

    @Override
    protected void onItemSelected(Integer data, int position) {
        checkCurrentSelected(data);
    }

    private void checkCurrentSelected(int data) {
        if (isMoreThanMaxYear(data)) {
            setSelectedYear(mMaxYear);
        } else if (isLessThanMinYear(data)) {
            setSelectedYear(mMinYear);
        }
    }

    private boolean isMoreThanMaxYear(int data) {
        return data > mMaxYear && mMaxYear > 0;
    }

    private boolean isLessThanMinYear(int data) {
        return data < mMinYear && mMinYear > 0;
    }
}
