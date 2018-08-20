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
public class MonthWheelView extends BaseDateWheelView {

    private int mSelectedMonth;

    public MonthWheelView(Context context) {
        this(context,null);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSimpleDateFormat=new SimpleDateFormat("MMM",Locale.getDefault());
        initData();
    }

    private void initData(){
        resetUnderMonth();
        List<Date> list=new ArrayList<>(1);
        for (int i = 0; i < 12; i++) {
            mCalendar.set(Calendar.MONTH,i);
            list.add(mCalendar.getTime());
        }
        super.setData(list);
        mCalendar.setTime(getData().get(getCurrentItemPosition()));
        mSelectedMonth=mCalendar.get(Calendar.MONTH)+1;
    }

    public int getSelectedMonth(){
        return mSelectedMonth;
    }

    public void setSelectedMonth(int selectedMonth){
        if (selectedMonth>=1 && selectedMonth<=12) {
            mSelectedMonth=selectedMonth;
            updateSelectedMonth();
        }
    }

    private void updateSelectedMonth(){
        setCurrentItemPosition(mSelectedMonth-1);
    }
}
