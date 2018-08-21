package com.zyyoona7.picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 选项选择器
 *
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/21.
 */
public class OptionsPickerView<T> extends LinearLayout {

    public OptionsPickerView(Context context) {
        this(context, null);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionsPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
