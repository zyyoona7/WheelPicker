package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.wheel.WheelView

open class Wheel60View @JvmOverloads constructor(context: Context,
                                                          attrs: AttributeSet? = null,
                                                          defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexOfAction<Int> {

    private val range: IntRange = 0 until 60

    override fun indexOf(item: Int): Int {
        return if (item in range) item else -1
    }

    override fun getItem(index: Int): Int {
        if (index !in 0 until getItemCount()) {
            return -1
        }
        return getAdapter()?.getItem<Int>(index) ?: -1
    }

    protected fun updateData() {
        val dataList = mutableListOf<Int>()
        for (i in range) {
            dataList.add(i)
        }
        setData(dataList)
    }
}