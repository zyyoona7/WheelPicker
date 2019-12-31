package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.interfaces.IndexConverter
import com.zyyoona7.wheel.WheelView

abstract class WheelIntView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexConverter {

    init {
        setItemIndexer { _, item -> if (item !is Int) -1 else indexFor(item) }
    }
}