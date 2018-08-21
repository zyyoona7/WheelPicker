package com.zyyoona7.picker.ex;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
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

    public MonthWheelView(Context context) {
        this(context, null);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MonthWheelView);
        int selectedMonth = typedArray.getInt(R.styleable.MonthWheelView_wv_selectedMonth, Calendar.getInstance().get(Calendar.MONTH) + 1);
        typedArray.recycle();
        initData();
        setSelectedMonth(selectedMonth);
    }

    private void initData() {
        List<Integer> list = new ArrayList<>(1);
        for (int i = 1; i <= 12; i++) {
            list.add(i);
        }
        super.setData(list);
    }

    public int getSelectedMonth() {
        return getSelectedItemData();
    }

    public void setSelectedMonth(int selectedMonth) {
        setSelectedMonth(selectedMonth, false);
    }

    public void setSelectedMonth(int selectedMonth, boolean isSmoothScroll) {
        setSelectedMonth(selectedMonth, isSmoothScroll, 0);
    }

    public boolean setSelectedMonth(int selectedMonth, boolean isSmoothScroll, int smoothDuration) {
        if (selectedMonth >= 1 && selectedMonth <= 12) {
            updateSelectedMonth(selectedMonth, isSmoothScroll, smoothDuration);
            return true;
        }else {
            return false;
        }
    }

    private void updateSelectedMonth(int selectedMonth, boolean isSmoothScroll, int smoothDuration) {
        setSelectedItemPosition(selectedMonth - 1, isSmoothScroll, smoothDuration);
    }

    @Override
    public void setData(List<Integer> dataList) {
        throw new UnsupportedOperationException("You can not invoke setData method in " + MonthWheelView.class.getSimpleName() + ".");
    }
}
