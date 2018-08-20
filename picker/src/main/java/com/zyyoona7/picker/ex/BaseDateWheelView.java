package com.zyyoona7.picker.ex;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zyyoona7.wheel.WheelView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zyyoona7
 * @version v1.0
 * @since 2018/8/20.
 */
public abstract class BaseDateWheelView extends WheelView<Date> {

    @NonNull
    protected SimpleDateFormat mSimpleDateFormat;
    protected Calendar mCalendar;

    public BaseDateWheelView(Context context) {
        super(context);
    }

    public BaseDateWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseDateWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCalendar=Calendar.getInstance();
    }

    @Override
    protected String getDataText(Date item) {
        if (item != null) {
            return mSimpleDateFormat.format(item);
        } else {
            return "";
        }
    }

    protected void resetUnderYear(){
        mCalendar.set(Calendar.MONTH,0);
        resetUnderMonth();
    }

    protected void resetUnderMonth(){
        mCalendar.set(Calendar.DATE,1);
        resetUnderDay();
    }

    protected void resetUnderDay(){
        mCalendar.set(Calendar.HOUR_OF_DAY,0);
        resetUnderHour();
    }

    protected void resetUnderHour(){
        mCalendar.set(Calendar.MINUTE,0);
        resetUnderMinute();
    }

    protected void resetUnderMinute(){
        mCalendar.set(Calendar.SECOND,0);
    }
}
