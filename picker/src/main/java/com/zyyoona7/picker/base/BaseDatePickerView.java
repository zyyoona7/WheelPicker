package com.zyyoona7.picker.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.zyyoona7.picker.ex.DayWheelView;
import com.zyyoona7.picker.ex.MonthWheelView;
import com.zyyoona7.picker.ex.YearWheelView;
import com.zyyoona7.picker.listener.OnDateSelectedListener;
import com.zyyoona7.picker.listener.OnPickerScrollStateChangedListener;
import com.zyyoona7.wheel.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseDatePickerView extends FrameLayout implements WheelView.OnItemSelectedListener<Integer>, WheelView.OnWheelChangedListener {

    private final SimpleDateFormat mYmdSdf;
    private final SimpleDateFormat mYmSdf;
    protected YearWheelView mYearWv;
    protected MonthWheelView mMonthWv;
    protected DayWheelView mDayWv;

    private OnDateSelectedListener mOnDateSelectedListener;
    private OnPickerScrollStateChangedListener mOnPickerScrollStateChangedListener;

    public BaseDatePickerView(@NonNull Context context) {
        this(context, null);
    }

    public BaseDatePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseDatePickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mYmdSdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        mYmSdf = new SimpleDateFormat("yyyy-M", Locale.getDefault());
        if (getDatePickerViewLayoutId() != 0) {
            LayoutInflater.from(context).inflate(getDatePickerViewLayoutId(), this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int yearId=getYearWheelViewId();
        if (!isNoId(yearId)) {
            mYearWv = findViewById(yearId);
        }
        int monthId=getMonthWheelViewId();
        if (!isNoId(monthId)) {
            mMonthWv = findViewById(monthId);
        }
        int dayId=getDayWheelViewId();
        if (!isNoId(dayId)) {
            mDayWv = findViewById(dayId);
        }
        if (mYearWv != null) {
            mYearWv.setOnItemSelectedListener(this);
            mYearWv.setOnWheelChangedListener(this);
        }
        if (mMonthWv != null) {
            mMonthWv.setOnItemSelectedListener(this);
            mMonthWv.setOnWheelChangedListener(this);
        }
        if (mDayWv != null) {
            mDayWv.setOnItemSelectedListener(this);
            mDayWv.setOnWheelChangedListener(this);
        }
    }

    /**
     * 获取 datePickerView 的布局资源id
     *
     * @return 布局资源id
     */
    @LayoutRes
    protected abstract int getDatePickerViewLayoutId();

    /**
     * 获取 YearWheelView 的 id
     *
     * @return YearWheelView id
     */
    @IdRes
    protected abstract int getYearWheelViewId();

    /**
     * 获取 MonthWheelView 的 id
     *
     * @return MonthWheelView id
     */
    @IdRes
    protected abstract int getMonthWheelViewId();

    /**
     * 获取 DayWheelView id
     *
     * @return DayWheelView id
     */
    @IdRes
    protected abstract int getDayWheelViewId();

    @Override
    public void onItemSelected(WheelView<Integer> wheelView, Integer data, int position) {
        if (wheelView.getId() == getYearWheelViewId()) {
            //年份选中
            if (mDayWv != null) {
                mDayWv.setYear(data);
            }
        } else if (wheelView.getId() == getMonthWheelViewId()) {
            //月份选中
            if (mDayWv != null) {
                mDayWv.setMonth(data);
            }
        }

        int year = mYearWv == null ? 1970 : mYearWv.getSelectedYear();
        int month = mMonthWv == null ? 1 : mMonthWv.getSelectedMonth();
        int day = mDayWv == null ? 1 : mDayWv.getSelectedDay();
        String date = year + "-" + month + "-" + day;
        if (mOnDateSelectedListener != null) {
            try {
                Date formatDate = null;
                if (isAllShown()) {
                    //全部显示的时候返回年月日转换后的日期
                    formatDate = mYmdSdf.parse(date);
                } else if (isYmShown()) {
                    //只有年月显示的时候返回年月转换后的日期
                    formatDate = mYmSdf.parse(date);
                }
                mOnDateSelectedListener.onDateSelected(this, year,
                        month, day, formatDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onWheelScroll(int scrollOffsetY) {

    }

    @Override
    public void onWheelItemChanged(int oldPosition, int newPosition) {

    }

    @Override
    public void onWheelSelected(int position) {

    }

    @Override
    public void onWheelScrollStateChanged(int state) {
        if (mOnPickerScrollStateChangedListener != null) {
            mOnPickerScrollStateChangedListener.onScrollStateChanged(state);
        }
    }

    private boolean isAllShown() {
        return isYmShown()
                && mDayWv != null && mDayWv.getVisibility() == VISIBLE;
    }

    private boolean isYmShown() {
        return mYearWv != null && mYearWv.getVisibility() == VISIBLE
                && mMonthWv != null && mMonthWv.getVisibility() == VISIBLE;
    }

    private boolean isNoId(@IdRes int idRes){
        return idRes==0 || idRes==NO_ID;
    }

    /**
     * 获取年份 WheelView
     *
     * @return 年份 WheelView
     */
    public YearWheelView getYearWv() {
        return mYearWv;
    }

    /**
     * 获取月份 WheelView
     *
     * @return 月份 WheelView
     */
    public MonthWheelView getMonthWv() {
        return mMonthWv;
    }

    /**
     * 获取日 WheelView
     *
     * @return 日 WheelView
     */
    public DayWheelView getDayWv() {
        return mDayWv;
    }


    /**
     * 设置日期回调监听器
     *
     * @param onDateSelectedListener 日期回调监听器
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    /**
     * 设置滚动状态变化监听
     *
     * @param listener 滚动状态变化监听器
     */
    public void setOnPickerScrollStateChangedListener(OnPickerScrollStateChangedListener listener) {
        mOnPickerScrollStateChangedListener = listener;
    }
}
