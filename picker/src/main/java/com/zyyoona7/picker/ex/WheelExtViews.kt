package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import com.zyyoona7.picker.R
import com.zyyoona7.picker.listener.AmPmTextHandler
import com.zyyoona7.picker.listener.OnAmPmChangedListener
import com.zyyoona7.wheel.WheelView
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * 自定义选择年份的 WheelView
 *
 * @author zyyoona7
 */
class WheelYearView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    private var startYear = 1970
    private var endYear = 2100

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelYearView)
            startYear = typedArray.getInt(R.styleable.WheelYearView_wv_startYear, 1970)
            endYear = typedArray.getInt(R.styleable.WheelYearView_wv_endYear, 2100)
            val selectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_selectedYear, startYear)
            val maxSelectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_maxSelectedYear, -1)
            val minSelectedYear = typedArray.getInt(R.styleable.WheelYearView_wv_minSelectedYear, -1)
            typedArray.recycle()

            val selectedPosition = indexOf(selectedYear)
            val maxSelectedPosition = indexOf(maxSelectedYear)
            val minSelectedPosition = indexOf(minSelectedYear)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

        }
        updateYearData()
    }

    override fun indexOf(item: Any?, isCompareFormatText: Boolean): Int {
        return if (item !is Int) {
            -1
        } else {
            if (item in startYear..endYear) {
                item - startYear
            } else {
                -1
            }
        }
    }

    private fun updateYearData() {
        val data: MutableList<Int> = arrayListOf()
        for (i in startYear..endYear) {
            data.add(i)
        }
        setData(data)
    }

    /**
     * 设置年份取值范围
     */
    fun setYearRange(start: Int, end: Int) {
        require(start <= end) {
            "endYear must be greater than startYear in WheelYearView."
        }

        startYear = start
        endYear = end
        updateYearData()
    }

    /**
     * 设置当前选中的年份
     */
    @JvmOverloads
    fun setSelectedYear(year: Int, isSmoothScroll: Boolean = false,
                        smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(year), isSmoothScroll, smoothDuration)
    }

    /**
     * 设置可以选中的年份范围
     */
    @JvmOverloads
    fun setSelectedYearRange(minYear: Int = startYear, maxYear: Int) {
        setSelectableRange(indexOf(minYear), indexOf(maxYear))
    }

    /**
     * 设置可以选中的年份范围
     */
    @JvmOverloads
    fun setSelectedYearRange(minYear: Int = startYear, maxYear: Int, overRangeMode: OverRangeMode) {
        setSelectableRange(indexOf(minYear), indexOf(maxYear), overRangeMode)
    }
}


/**
 * 自定义选择月份的 WheelView
 *
 * @author zyyoona7
 */
class WheelMonthView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    companion object {
        const val MIN_MONTH = 1
        const val MAX_MONTH = 12
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelMonthView)
            val selectedMonth = typedArray.getInt(R.styleable.WheelMonthView_wv_selectedMonth, 1)
            val minSelectedMonth = typedArray.getInt(R.styleable.WheelYearView_wv_minSelectedYear, -1)
            val maxSelectedMonth = typedArray.getInt(R.styleable.WheelMonthView_wv_maxSelectedMonth, -1)
            typedArray.recycle()
            val selectedPosition = indexOf(selectedMonth)
            val maxSelectedPosition = indexOf(maxSelectedMonth)
            val minSelectedPosition = indexOf(minSelectedMonth)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)
        }
        updateMonthData()
    }

    override fun indexOf(item: Any?, isCompareFormatText: Boolean): Int {
        return if (item !is Int) {
            -1
        } else {
            if (item in MIN_MONTH..MAX_MONTH) {
                item - 1
            } else {
                -1
            }
        }
    }

    private fun updateMonthData() {
        val data = mutableListOf<Int>()
        for (i in MIN_MONTH..MAX_MONTH) {
            data.add(i)
        }
        setData(data)
    }

    @JvmOverloads
    fun setSelectedMonth(month: Int, isSmoothScroll: Boolean = false,
                         smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(month), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedMonthRange(minMonth: Int = MIN_MONTH, maxMonth: Int) {
        setSelectableRange(indexOf(minMonth), indexOf(maxMonth))
    }

    @JvmOverloads
    fun setSelectedMonthRange(minMonth: Int = MIN_MONTH, maxMonth: Int, overRangeMode: OverRangeMode) {
        setSelectableRange(indexOf(minMonth), indexOf(maxMonth), overRangeMode)
    }
}


/**
 * 自定义选择 日 的 WheelView
 *
 * @author zyyoona7
 */
class WheelDayView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    var year: Int = -1
        set(value) {
            if (value == field) {
                return
            }
            field = max(value, 0)
            updateDayData()
        }

    var month: Int = -1
        set(value) {
            if (value == field) {
                return
            }
            field = min(12, max(0, value))
            updateDayData()
        }
    private val daysArray: SparseArray<List<Int>> by lazy { SparseArray<List<Int>>() }

    companion object {
        const val MIN_DAY = 1
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelDayView)
            year = typedArray.getInt(R.styleable.WheelDayView_wv_year, 2019)
            month = typedArray.getInt(R.styleable.WheelDayView_wv_month, 1)
            val selectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_selectedDay, 1)
            val minSelectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_minSelectedDay, -1)
            val maxSelectedDay = typedArray.getInt(R.styleable.WheelDayView_wv_maxSelectedDay, -1)
            typedArray.recycle()

            val selectedPosition = indexOf(selectedDay)
            val maxSelectedPosition = indexOf(maxSelectedDay)
            val minSelectedPosition = indexOf(minSelectedDay)
            initSelectedPositionAndRange(selectedPosition, minSelectedPosition, maxSelectedPosition)

        }
        updateDayData()
    }

    override fun indexOf(item: Any?, isCompareFormatText: Boolean): Int {
        return if (item !is Int) {
            -1
        } else {
            val days = getDaysByCalendar()
            if (item in MIN_DAY..days) item - 1 else -1
        }
    }

    private fun updateDayData() {
        val days = getDaysByCalendar()
        //天数变化时才更新Adapter
        if (days == getItemCount()) {
            return
        }
        setData(daysArray.get(days) ?: generateDays(days))
    }

    private fun getDaysByCalendar(): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)

        return calendar.get(Calendar.DATE)
    }

    private fun generateDays(days: Int): List<Int> {
        val daysList = mutableListOf<Int>()
        for (i in MIN_DAY..days) {
            daysList.add(i)
        }
        daysArray.put(days, daysList)
        return daysList
    }

    fun setYearAndMonth(year: Int, month: Int) {
        this.year = year
        this.month = month

        updateDayData()
    }

    fun setDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        updateDayData()
        setSelectedDay(currentDay)
    }

    fun getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, getSelectedItem<Int>() ?: 1)
        return calendar.time
    }

    @JvmOverloads
    fun setSelectedDay(dayOfMonth: Int, isSmoothScroll: Boolean = false,
                       smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(dayOfMonth), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedDayRange(min: Int = MIN_DAY, max: Int) {
        setSelectableRange(indexOf(min), indexOf(max))
    }

    @JvmOverloads
    fun setSelectedDayRange(min: Int = MIN_DAY, max: Int, overRangeMode: OverRangeMode) {
        setSelectableRange(indexOf(min), indexOf(max), overRangeMode)
    }

    fun getMaxDay(): Int {
        return getAdapter()?.getItem(getItemCount() - 1) ?: -1
    }
}


/**
 * 自定义选择上午/下午的 WheelView
 *
 * @author zyyoona7
 */
class WheelAmPmView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    var amPmTextHandler: AmPmTextHandler = DefaultAmPmTextHandler()
        set(value) {
            if (value == field) {
                return
            }
            field = value
            updateData()
        }

    init {
        updateData()
    }

    private fun updateData() {
        val dataList = mutableListOf<String>()
        dataList.add(amPmTextHandler.amText(context))
        dataList.add(amPmTextHandler.pmText(context))
        setData(dataList)
    }

    fun isAm(): Boolean {
        return getSelectedPosition() == 0
    }

    fun isPm(): Boolean {
        return getSelectedPosition() == 1
    }

    class DefaultAmPmTextHandler : AmPmTextHandler {
        override fun amText(context: Context): String {
            return context.getString(R.string.time_picker_am)
        }

        override fun pmText(context: Context): String {
            return context.getString(R.string.time_picker_pm)
        }
    }
}


/**
 * 自定义选择小时的 WheelView
 *
 * @author zyyoona7
 */
class WheelHourView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    var is24Hour: Boolean = true
        set(value) {
            if (value == field) {
                return
            }
            field = value
            updateHourData()
        }

    var hourType: HourType = HourType.DEFAULT

    //所有数据的高度
    private var dataHeight: Int = 0

    //当前偏移除以dataHeight的结果
    private var currentOffsetCount: Int = 0

    //当前滚动偏移方向 1 scrollOffsetY>0 -1 scrollOffsetY<0
    private var currentOffsetYDirection: Int = 1
    private var hourRange: IntRange = 0 until 24

    private var amPmChangedListener: OnAmPmChangedListener? = null

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

    override fun indexOf(item: Any?, isCompareFormatText: Boolean): Int {
        if (item !is Int) {
            return -1
        }
        return if (!is24Hour && item == 12) 0 else
            if (item in hourRange) item else -1
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

    @JvmOverloads
    fun setSelectedHourRange(minHour: Int, maxHour: Int,
                             overRangeMode: OverRangeMode = OverRangeMode.NORMAL) {
        setSelectableRange(indexOf(minHour), indexOf(maxHour), overRangeMode)
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
        if (hourType == HourType.DEFAULT) {
            hourType = HourType.AM
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
            hourType = if (hourType == HourType.AM) HourType.PM else HourType.AM
            amPmChangedListener?.onAmPmChanged(this, hourType == HourType.AM)
        }
    }

    private fun calculateDataHeight(): Int {
        return getItemHeight() * getItemCount()
    }

    enum class HourType { DEFAULT, AM, PM }
}


/**
 * 自定义选择 range 的 WheelView
 *
 * @author zyyoona7
 */
open class Wheel60View @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0)
    : WheelView(context, attrs, defStyleAttr) {

    private val range: IntRange = 0 until 60

    override fun indexOf(item: Any?, isCompareFormatText: Boolean): Int {
        return if (item is Int && item in range) item else -1
    }

    protected fun updateData() {
        val dataList = mutableListOf<Int>()
        for (i in range) {
            dataList.add(i)
        }
        setData(dataList)
    }
}

/**
 * 自定义选择分钟的 WheelView
 *
 * @author zyyoona7
 */
class WheelMinuteView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0)
    : Wheel60View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelMinuteView)
            val selectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_selectedMinute, 0)
            val minSelectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_minSelectedMinute, -1)
            val maxSelectedMinute = typedArray.getInt(R.styleable.WheelMinuteView_wv_maxSelectedMinute, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexOf(selectedMinute),
                    indexOf(minSelectedMinute), indexOf(maxSelectedMinute))
        }
        updateData()
    }

    @JvmOverloads
    fun setSelectedMinute(minute: Int, isSmoothScroll: Boolean = false,
                          smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(minute), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedMinuteRange(minMinute: Int, maxMinute: Int,
                               overRangeMode: OverRangeMode = OverRangeMode.NORMAL) {
        setSelectableRange(indexOf(minMinute), indexOf(maxMinute), overRangeMode)
    }
}

/**
 * 自定义选择秒的 WheelView
 *
 * @author zyyoona7
 */
class WheelSecondView @JvmOverloads constructor(context: Context,
                                                attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0)
    : Wheel60View(context, attrs, defStyleAttr) {

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WheelSecondView)
            val selectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_selectedSecond, 0)
            val minSelectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_minSelectedSecond, -1)
            val maxSelectedSecond = typedArray.getInt(R.styleable.WheelSecondView_wv_maxSelectedSecond, -1)
            typedArray.recycle()

            initSelectedPositionAndRange(indexOf(selectedSecond),
                    indexOf(minSelectedSecond), indexOf(maxSelectedSecond))
        }
        updateData()
    }

    @JvmOverloads
    fun setSelectedSecond(second: Int, isSmoothScroll: Boolean = false,
                          smoothDuration: Int = DEFAULT_SCROLL_DURATION) {
        setSelectedPosition(indexOf(second), isSmoothScroll, smoothDuration)
    }

    @JvmOverloads
    fun setSelectedSecondRange(minSecond: Int, maxSecond: Int,
                               overRangeMode: OverRangeMode = OverRangeMode.NORMAL) {
        setSelectableRange(indexOf(minSecond), indexOf(maxSecond), overRangeMode)
    }
}