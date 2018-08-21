package com.zyyoona7.picker;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.zyyoona7.picker.ex.DayWheelView;
import com.zyyoona7.picker.ex.MonthWheelView;
import com.zyyoona7.picker.ex.YearWheelView;
import com.zyyoona7.wheel.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期 选择View
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/21.
 */
public class DatePickerView extends LinearLayout implements WheelView.OnItemSelectedListener<Integer> {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    private YearWheelView mYearWv;
    private MonthWheelView mMonthWv;
    private DayWheelView mDayWv;
    private AppCompatTextView mYearTv;
    private AppCompatTextView mMonthTv;
    private AppCompatTextView mDayTv;

    private OnDateSelectedListener mOnDateSelectedListener;

    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_date_picker_view, this);

        mYearWv = findViewById(R.id.wv_year);
        mMonthWv = findViewById(R.id.wv_month);
        mDayWv = findViewById(R.id.wv_day);

        mYearTv = findViewById(R.id.tv_year);
        mMonthTv = findViewById(R.id.tv_month);
        mDayTv = findViewById(R.id.tv_day);
    }

    @Override
    public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
        if (wheelView.getId() == R.id.wv_year) {
            //年份选中
            mDayWv.setYear(data);
        } else if (wheelView.getId() == R.id.wv_month) {
            //月份选中
            mDayWv.setMonth(data);
        }

        int year = mYearWv.getSelectedYear();
        int month = mMonthWv.getSelectedMonth();
        int day = mDayWv.getSelectedDay();
        String date = year + "-" + month + "-" + day;
        if (mOnDateSelectedListener != null) {
            try {
                mOnDateSelectedListener.onDateSelected(this, year, month, day, SDF.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLabelTextSize(float textSize) {
        mYearTv.setTextSize(textSize);
        mMonthTv.setTextSize(textSize);
        mDayTv.setTextSize(textSize);
    }

    public void setLabelTextSize(int unit, float textSize) {
        mYearTv.setTextSize(unit, textSize);
        mMonthTv.setTextSize(unit, textSize);
        mDayTv.setTextSize(unit, textSize);
    }

    public void setLabelTextColorRes(@ColorRes int textColorRes) {
        setLabelTextColor(ContextCompat.getColor(getContext(), textColorRes));
    }

    public void setLabelTextColor(@ColorInt int textColor) {
        mYearTv.setTextColor(textColor);
        mMonthTv.setTextColor(textColor);
        mDayTv.setTextColor(textColor);
    }

    public void setShowLabel(boolean isShowLabel) {
        if (isShowLabel) {
            setLabelVisibility(VISIBLE);
        } else {
            setLabelVisibility(GONE);
        }
    }

    public void setLabelVisibility(int visibility) {
        mYearTv.setVisibility(visibility);
        mMonthTv.setVisibility(visibility);
        mDayTv.setVisibility(visibility);
    }

    public void hideDayItem() {
        setItemVisibility(GONE, mDayWv, mDayTv);
    }

    public void showDayItem() {
        setItemVisibility(VISIBLE, mDayWv, mDayTv);
    }

    public void hideMonthItem() {
        setItemVisibility(GONE, mMonthWv, mMonthTv);
    }

    public void showMonthItem() {
        setItemVisibility(VISIBLE, mMonthWv, mMonthTv);
    }

    public void hideYearItem() {
        setItemVisibility(GONE, mYearWv, mYearTv);
    }

    public void showYearItem() {
        setItemVisibility(VISIBLE, mYearWv, mYearTv);
    }

    private void setItemVisibility(int visibility, WheelView wheelView, AppCompatTextView textView) {
        wheelView.setVisibility(visibility);
        textView.setVisibility(visibility);
    }

    public void setYearRange(int start, int end) {
        mYearWv.setYearRange(start, end);
    }

    public void setSelectedYear(int year) {
        setSelectedYear(year, false);
    }

    public void setSelectedYear(int year, boolean isSmoothScroll) {
        setSelectedYear(year, isSmoothScroll, 0);
    }

    public void setSelectedYear(int year, boolean isSmoothScroll, int smoothDuration) {
        if (mYearWv.setSelectedYear(year, isSmoothScroll, smoothDuration)) {
            mDayWv.setYear(year);
        }
    }

    public void setSelectedMonth(int month) {
        mMonthWv.setSelectedMonth(month, false);
    }

    public void setSelectedMonth(int month, boolean isSmoothScroll) {
        mMonthWv.setSelectedMonth(month, isSmoothScroll, 0);
    }

    public void setSelectedMonth(int month, boolean isSmoothScroll, int smoothDuration) {
        if (mMonthWv.setSelectedMonth(month, isSmoothScroll, smoothDuration)) {
            mDayWv.setMonth(month);
        }
    }

    public void setSelectedDay(int day) {
        mDayWv.setSelectedDay(day, false);
    }

    public void setSelectedDay(int day, boolean isSmoothScroll) {
        mDayWv.setSelectedDay(day, isSmoothScroll, 0);
    }

    public void setSelectedDay(int day, boolean isSmoothScroll, int smoothDuration) {
        mDayWv.setSelectedDay(day, isSmoothScroll, smoothDuration);
    }

    public void setVisibleItems(int visibleItems) {
        mYearWv.setVisibleItems(visibleItems);
        mMonthWv.setVisibleItems(visibleItems);
        mDayWv.setVisibleItems(visibleItems);
    }

    public void setTextSize(float textSize) {
        setTextSize(textSize, false);
    }

    public void setTextSize(float textSize, boolean isDp) {
        mYearWv.setTextSize(textSize, isDp);
        mMonthWv.setTextSize(textSize, isDp);
        mDayWv.setTextSize(textSize, isDp);
    }

    public void setTypeface(Typeface tf) {
        mYearWv.setTypeface(tf);
        mMonthWv.setTypeface(tf);
        mDayWv.setTypeface(tf);
    }

    public void setNormalItemTextColor(int textColor) {
        mYearWv.setNormalItemTextColor(textColor);
        mMonthWv.setNormalItemTextColor(textColor);
        mDayWv.setNormalItemTextColor(textColor);
    }

    public void setSelectedItemTextColor(int textColor) {
        mYearWv.setSelectedItemTextColor(textColor);
        mMonthWv.setSelectedItemTextColor(textColor);
        mDayWv.setSelectedItemTextColor(textColor);
    }

    public void setCurved(boolean isCurved) {
        mYearWv.setCurved(isCurved);
        mMonthWv.setCurved(isCurved);
        mDayWv.setCurved(isCurved);
    }

    public void setCyclic(boolean isCyclic) {
        mYearWv.setCyclic(isCyclic);
        mMonthWv.setCyclic(isCyclic);
        mDayWv.setCyclic(isCyclic);
    }

    public void setLineSpacing(float lineSpacing) {
        setLineSpacing(lineSpacing, false);
    }

    public void setLineSpacing(float lineSpacing, boolean isDp) {
        mYearWv.setLineSpacing(lineSpacing, isDp);
        mMonthWv.setLineSpacing(lineSpacing, isDp);
        mDayWv.setLineSpacing(lineSpacing, isDp);
    }

    /**
     * 日期选中监听器
     */
    public interface OnDateSelectedListener {

        /**
         * @param datePickerView DatePickerView
         * @param year           选中的年份
         * @param month          选中的月份
         * @param day            选中的天
         * @param date           选中的日期
         */
        void onDateSelected(DatePickerView datePickerView, int year, int month, int day, Date date);
    }
}
