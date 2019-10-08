package com.zyyoona7.picker.base;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class BaseDatePickerView extends FrameLayout implements WheelView.OnItemSelectedListener<Integer>,
        WheelView.OnWheelChangedListener {

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

        int yearId = getYearWheelViewId();
        if (hasViewId(yearId)) {
            mYearWv = findViewById(yearId);
        }
        int monthId = getMonthWheelViewId();
        if (hasViewId(monthId)) {
            mMonthWv = findViewById(monthId);
        }
        int dayId = getDayWheelViewId();
        if (hasViewId(dayId)) {
            mDayWv = findViewById(dayId);
        }
        if (mYearWv != null) {
            mYearWv.setOnItemSelectedListener(this);
            mYearWv.setOnWheelChangedListener(this);
        }
        if (mMonthWv != null) {
            mMonthWv.setOnItemSelectedListener(this);
            mMonthWv.setOnWheelChangedListener(this);
            if (mYearWv != null) {
                mMonthWv.setCurrentSelectedYear(mYearWv.getSelectedYear());
            }
        }
        if (mDayWv != null) {
            mDayWv.setOnItemSelectedListener(this);
            mDayWv.setOnWheelChangedListener(this);
            if (mYearWv != null && mMonthWv != null) {
                mDayWv.setYearAndMonth(mYearWv.getSelectedYear(), mMonthWv.getSelectedMonth());
            }
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
            if (mMonthWv != null) {
                mMonthWv.setCurrentSelectedYear(data);
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

    private boolean hasViewId(@IdRes int idRes) {
        return idRes != 0 && idRes != NO_ID;
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

    /**
     * 设置最大选择的日期 日期大于此日期则会选中到此日期
     *
     * @param calendar Calendar
     */
    public void setMaxDate(@NonNull Calendar calendar) {
        setMaxDate(calendar.getTime());
    }

    /**
     * 设置最大选择的日期 日期大于此日期则会选中到此日期
     *
     * @param date 日期
     */
    public void setMaxDate(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH) + 1;
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (mYearWv != null) {
            mYearWv.setMaxYear(maxYear);
        }
        if (mMonthWv != null) {
            mMonthWv.setMaxYearAndMonth(maxYear, maxMonth);
        }
        if (mDayWv != null) {
            mDayWv.setMaxYearMonthAndDay(maxYear, maxMonth, maxDay);
        }
    }

    /**
     * 设置最小选择的日期 日期小于此日期则会选中到此日期
     *
     * @param calendar Calendar
     */
    public void setMinDate(@NonNull Calendar calendar) {
        setMinDate(calendar.getTime());
    }

    /**
     * 设置最小选择的日期 日期小于此日期则会选中到此日期
     *
     * @param date 日期
     */
    public void setMinDate(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minYear = calendar.get(Calendar.YEAR);
        int minMonth = calendar.get(Calendar.MONTH) + 1;
        int minDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (mYearWv != null) {
            mYearWv.setMinYear(minYear);
        }
        if (mMonthWv != null) {
            mMonthWv.setMinYearAndMonth(minYear, minMonth);
        }
        if (mDayWv != null) {
            mDayWv.setMinYearMonthAndDay(minYear, minMonth, minDay);
        }
    }
}
