package com.zyyoona7.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zyyoona7.picker.base.BaseDatePickerView;

public class CustomDatePickerView extends BaseDatePickerView {

    public CustomDatePickerView(@NonNull Context context) {
        super(context);
    }

    public CustomDatePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDatePickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getDatePickerViewLayoutId() {
        return R.layout.layout_custom_date_picker_view;
    }

    @Override
    protected int getYearWheelViewId() {
        return R.id.wv_custom_year;
    }

    @Override
    protected int getMonthWheelViewId() {
        return R.id.wv_custom_month;
    }

    @Override
    protected int getDayWheelViewId() {
        return 0;
    }
}
