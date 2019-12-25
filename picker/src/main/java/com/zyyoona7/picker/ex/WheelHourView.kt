package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.IntDef
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.IndexOfAction
import com.zyyoona7.picker.listener.OnAmPmChangedListener
import com.zyyoona7.wheel.WheelView

class WheelHourView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr), IndexOfAction<Int> {

    var is24Hour: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            updateHourData()
        }
    @HourType
    var hourType: Int = TYPE_NONE
    //所有数据的高度
    private var dataHeight: Int = 0
    //当前偏移除以dataHeight的结果
    private var currentOffsetCount: Int = 0
    //当前滚动偏移方向 1 scrollOffsetY>0 -1 scrollOffsetY<0
    private var currentOffsetYDirection: Int = 1
    private var hourRange: IntRange = 0 until 24

    private var amPmChangedListener: OnAmPmChangedListener? = null

    companion object {
        const val TYPE_NONE = 0
        const val TYPE_AM = 1
        const val TYPE_PM = 2
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelHourView)
            is24Hour = typedArray.getBoolean(R.styleable.WheelHourView_wv_is24Hour, true)
            val selectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_selectedHour, 1)
            val minSelectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_minSelectedHour, -1)
            val maxSelectedHour = typedArray.getInt(R.styleable.WheelHourView_wv_maxSelectedHour, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexOf(selectedHour),
                    indexOf(minSelectedHour), indexOf(maxSelectedHour))

        }
        updateHourData()
    }

    override fun indexOf(item: Int): Int {
        return if (!is24Hour && item == 12) 0 else
            if (item in hourRange) item else -1
    }

    override fun getItem(index: Int): Int {
        if (index !in 0 until getItemCount()) {
            return -1
        }
        return getAdapter()?.getItem<Int>(index) ?: -1
    }

    private fun updateHourData() {
        val dataList = mutableListOf<Int>()
        updateHourRange()
        for (hour in hourRange) {
            if (!is24Hour && hour == 0) {
                dataList.add(12)
            } else {
                dataList.add(hour)
            }
        }
        setData(dataList)
        dataHeight = calculateDataHeight()
    }

    private fun updateHourRange() {
        hourRange = if (is24Hour) 0 until 24 else 0..11
    }

    @JvmOverloads
    fun setSelectedHour(hour: Int, isSmoothScroll: Boolean = false,
                        smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(hour), isSmoothScroll, smoothDuration)
    }

    fun setSelectedHourRange(minHour: Int, maxHour: Int) {
        setSelectedRange(indexOf(minHour), indexOf(maxHour))
    }

    fun setOnAmPmChangedListener(amPmChangedListener: OnAmPmChangedListener?) {
        this.amPmChangedListener = amPmChangedListener
    }

    override fun onWheelScrollChanged(scrollOffsetY: Int) {
        super.onWheelScrollChanged(scrollOffsetY)
        if (is24Hour && !isCyclic) {
            return
        }
        //12时制，监听am/pm变化
        if (hourType != TYPE_AM && hourType != TYPE_PM) {
            hourType = TYPE_AM
        }
        if (dataHeight <= 0) {
            dataHeight = calculateDataHeight()
        }
        if (dataHeight == 0) {
            return
        }
        //scrollOffsetY 小于 0 后需要多加一个 itemHeight 才能保证切换正确
        val offsetCount = (if (scrollOffsetY < 0) scrollOffsetY + getItemHeight() else scrollOffsetY) / dataHeight
        val offsetYDirection = if (scrollOffsetY >= 0) 1 else -1
        if (currentOffsetCount != offsetCount || currentOffsetYDirection != offsetYDirection) {
            currentOffsetYDirection = offsetYDirection
            currentOffsetCount = offsetCount
            hourType = if (hourType == TYPE_AM) TYPE_PM else TYPE_AM
            amPmChangedListener?.onAmPmChanged(this, hourType == TYPE_AM)
        }
    }

    private fun calculateDataHeight(): Int {
        return getItemHeight() * getItemCount()
    }

    @IntDef(TYPE_NONE, TYPE_AM, TYPE_PM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class HourType
}