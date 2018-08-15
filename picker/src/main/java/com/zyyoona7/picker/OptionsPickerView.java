package com.zyyoona7.picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 选项PickerView
 * @param <T>
 */
public class OptionsPickerView<T> extends LinearLayout {

    // TODO: 2018/8/14 联动，不联动

    public OptionsPickerView(Context context) {
        super(context);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
