package com.zyyoona7.picker.ex;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author zyyoona7
 * @version v1.0.0
 * @since 2018/8/20.
 */
public class WeekWheelView extends BaseDateWheelView {


    public WeekWheelView(Context context) {
        this(context,null);
    }

    public WeekWheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WeekWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSimpleDateFormat=new SimpleDateFormat("E", Locale.getDefault());
    }


}
