package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet

open class Wheel60View @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0)
    : WheelIntView(context, attrs, defStyleAttr) {

    private val range: IntRange = 0 until 60

    override fun indexFor(item: Int): Int {
        return if (item in range) item else -1
    }

    protected fun updateData() {
        val dataList = mutableListOf<Int>()
        for (i in range) {
            dataList.add(i)
        }
        setData(dataList)
    }
}