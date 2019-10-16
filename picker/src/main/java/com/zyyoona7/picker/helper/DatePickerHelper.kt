package com.zyyoona7.picker.helper

import android.graphics.Paint
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.WheelDayView
import com.zyyoona7.picker.ex.WheelMonthView
import com.zyyoona7.picker.ex.WheelYearView
import com.zyyoona7.picker.interfaces.DatePicker
import com.zyyoona7.picker.interfaces.WheelPicker
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期选择辅助类
 *
 * @author zyyoona7
 */
class DatePickerHelper(private var wheelYearView: WheelYearView?,
                       private var wheelMonthView: WheelMonthView?,
                       private var wheelDayView: WheelDayView?)
    : OnItemSelectedListener, OnScrollChangedListener, DatePicker, WheelPicker {

    private var minYear: Int = -1
    private var maxYear: Int = -1
    private var minMonth: Int = -1
    private var maxMonth: Int = -1
    private var minDay: Int = -1
    private var maxDay: Int = -1
    private var dateSelectedListener: OnDateSelectedListener? = null
    private var scrollChangedListener: OnScrollChangedListener? = null

    companion object {
        private const val DEFAULT_YEAR = 1970
        private const val DEFAULT_MONTH = 1
        private const val DEFAULT_DAY = 1
    }

    init {
        wheelYearView?.setOnItemSelectedListener(this)
        wheelMonthView?.setOnItemSelectedListener(this)
        wheelDayView?.setOnItemSelectedListener(this)

        wheelYearView?.setOnScrollChangedListener(this)
        wheelMonthView?.setOnScrollChangedListener(this)
        wheelDayView?.setOnScrollChangedListener(this)
    }

    override fun onItemSelected(wheelView: WheelViewKt, adapter: ArrayWheelAdapter<*>, position: Int) {
        val yearId = wheelYearView?.id ?: -1
        val monthId = wheelMonthView?.id ?: -1
        when (wheelView.id) {
            yearId -> {
                val selectedYear = wheelYearView?.getItem(position) ?: DEFAULT_YEAR
                wheelDayView?.year = selectedYear
                if (selectedYear == minYear || selectedYear == maxYear) {
                    wheelMonthView?.setSelectedMonthRange(minMonth, maxMonth)
                    val selectedMonth = getSelectedMonth()
                    //如果当前选中的月是最小选中的月或者最大选中的月 则限制选择 Day 的范围
                    if (selectedMonth == minMonth || selectedMonth == maxMonth) {
                        wheelDayView?.setSelectedDayRange(minDay, maxDay)
                    }
                } else {
                    wheelMonthView?.setSelectedMonthRange(-1, -1)
                    wheelDayView?.setSelectedDayRange(-1, -1)
                }
            }
            monthId -> {
                wheelDayView?.month = wheelMonthView?.getItem(position) ?: DEFAULT_MONTH
                val selectedYear = getSelectedYear()
                val selectedMonth = wheelMonthView?.getItem(position) ?: DEFAULT_MONTH
                //如果选中的年份是最小选中年或者最大选中年 并且 选中的月份是最小选中月或者最大选中月-
                //才限制选择 Day 的范围
                if (((selectedYear == minYear || selectedYear == maxYear)
                                && (selectedMonth == minMonth || selectedMonth == maxMonth))) {
                    wheelDayView?.setSelectedDayRange(minDay, maxDay)
                } else {
                    wheelDayView?.setSelectedDayRange(-1, -1)
                }
            }
            else -> {
            }
        }
        dateSelectedListener?.onDateSelected(getSelectedYear(), getSelectedMonth(),
                getSelectedDay(), getSelectedDate())
    }

    override fun onScrollChanged(wheelView: WheelViewKt, scrollOffsetY: Int) {
        scrollChangedListener?.onScrollChanged(wheelView, scrollOffsetY)
    }

    override fun onScrollStateChanged(wheelView: WheelViewKt, state: Int) {
        scrollChangedListener?.onScrollStateChanged(wheelView, state)
    }

    override fun setYearTextFormatter(textFormatter: IntTextFormatter) {
        wheelYearView?.setTextFormatter(textFormatter)
    }

    override fun setMonthTextFormatter(textFormatter: IntTextFormatter) {
        wheelMonthView?.setTextFormatter(textFormatter)
    }

    override fun setDayTextFormatter(textFormatter: IntTextFormatter) {
        wheelDayView?.setTextFormatter(textFormatter)
    }

    override fun setOnDateSelectedListener(listener: OnDateSelectedListener?) {
        this.dateSelectedListener = listener
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        this.scrollChangedListener = listener
    }

    override fun setSelectedDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        setSelectedDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun setSelectedDate(year: Int, month: Int, day: Int) {
        wheelYearView?.setSelectedYear(year)
        wheelMonthView?.setSelectedMonth(month)
        wheelDayView?.setSelectedDay(day)
    }

    override fun setDateRange(minDate: Date, maxDate: Date) {
        val minCalendar = Calendar.getInstance()
        minCalendar.time = minDate
        val maxCalendar = Calendar.getInstance()
        maxCalendar.time = maxDate
        setDateRange(minCalendar, maxCalendar)
    }

    override fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar) {
        minYear = minCalendar.get(Calendar.YEAR)
        maxYear = maxCalendar.get(Calendar.YEAR)
        minMonth = minCalendar.get(Calendar.MONTH) + 1
        maxMonth = maxCalendar.get(Calendar.MONTH) + 1
        minDay = minCalendar.get(Calendar.DAY_OF_MONTH)
        maxDay = maxCalendar.get(Calendar.DAY_OF_MONTH)
        wheelYearView?.setSelectedYearRange(minYear, maxYear)
    }

    override fun getSelectedDate(): Date {
        val format = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        return try {
            format.parse(getSelectedDateStr())
                    ?: Date()
        } catch (e: ParseException) {
            Date()
        }
    }

    override fun getSelectedDateStr(): String {
        return "${getSelectedYear()}-${getSelectedMonth()}-${getSelectedDay()}"
    }

    override fun getSelectedYear(): Int {
        return wheelYearView?.getSelectedItem<Int>() ?: DEFAULT_YEAR
    }

    override fun getSelectedMonth(): Int {
        return wheelMonthView?.getSelectedItem<Int>() ?: DEFAULT_MONTH
    }

    override fun getSelectedDay(): Int {
        return wheelDayView?.getSelectedItem<Int>() ?: DEFAULT_DAY
    }

    override fun getWheelYearView(): WheelYearView {
        require(wheelYearView != null) {
            "WheelYearView is null."
        }
        return wheelYearView!!
    }

    override fun getWheelMonthView(): WheelMonthView {
        require(wheelYearView != null) {
            "WheelMonthView is null."
        }
        return wheelMonthView!!
    }

    override fun getWheelDayView(): WheelDayView {
        require(wheelYearView != null) {
            "WheelDayView is null."
        }
        return wheelDayView!!
    }

    override fun setVisibleItems(visibleItems: Int) {
        wheelYearView?.visibleItems = visibleItems
        wheelMonthView?.visibleItems = visibleItems
        wheelDayView?.visibleItems = visibleItems
    }

    override fun setLineSpacing(lineSpacing: Float) {
        wheelYearView?.lineSpacing = lineSpacing
        wheelMonthView?.lineSpacing = lineSpacing
        wheelDayView?.lineSpacing = lineSpacing
    }

    override fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        wheelYearView?.setLineSpacing(lineSpacing, isDp)
        wheelMonthView?.setLineSpacing(lineSpacing, isDp)
        wheelDayView?.setLineSpacing(lineSpacing, isDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        wheelYearView?.isCyclic = isCyclic
        wheelMonthView?.isCyclic = isCyclic
        wheelDayView?.isCyclic = isCyclic
    }

    override fun setTextSize(textSize: Float) {
        wheelYearView?.textSize = textSize
        wheelMonthView?.textSize = textSize
        wheelDayView?.textSize = textSize
    }

    override fun setTextSize(textSize: Float, isSp: Boolean) {
        wheelYearView?.setTextSize(textSize, isSp)
        wheelMonthView?.setTextSize(textSize, isSp)
        wheelDayView?.setTextSize(textSize, isSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        wheelYearView?.isAutoFitTextSize = autoFit
        wheelMonthView?.isAutoFitTextSize = autoFit
        wheelDayView?.isAutoFitTextSize = autoFit
    }

    override fun setTextAlign(@WheelView.TextAlign textAlign: Int) {
        wheelYearView?.textAlign = textAlign
        wheelMonthView?.textAlign = textAlign
        wheelDayView?.textAlign = textAlign
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        wheelYearView?.normalTextColor = textColor
        wheelMonthView?.normalTextColor = textColor
        wheelDayView?.normalTextColor = textColor
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        wheelYearView?.setNormalTextColorRes(textColorRes)
        wheelMonthView?.setNormalTextColorRes(textColorRes)
        wheelDayView?.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        wheelYearView?.selectedTextColor = textColor
        wheelMonthView?.selectedTextColor = textColor
        wheelDayView?.selectedTextColor = textColor
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        wheelYearView?.setSelectedTextColorRes(textColorRes)
        wheelMonthView?.setSelectedTextColorRes(textColorRes)
        wheelDayView?.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextMargins(margin: Float) {
        wheelYearView?.textMarginLeft = margin
        wheelMonthView?.textMarginLeft = margin
        wheelDayView?.textMarginLeft = margin
    }

    override fun setTextMargins(margin: Float, isDp: Boolean) {
        wheelYearView?.setTextMargins(margin, isDp)
        wheelMonthView?.setTextMargins(margin, isDp)
        wheelDayView?.setTextMargins(margin, isDp)
    }

    override fun setTextMarginLeft(marginLeft: Float) {
        wheelYearView?.textMarginLeft=marginLeft
        wheelMonthView?.textMarginLeft=marginLeft
        wheelDayView?.textMarginLeft=marginLeft
    }

    override fun setTextMarginLeft(marginLeft: Float, isDp: Boolean) {
        wheelYearView?.setTextMarginLeft(marginLeft,isDp)
        wheelMonthView?.setTextMarginLeft(marginLeft,isDp)
        wheelDayView?.setTextMarginLeft(marginLeft,isDp)
    }

    override fun setTextMarginRight(marginRight: Float) {
        wheelYearView?.textMarginRight=marginRight
        wheelMonthView?.textMarginRight=marginRight
        wheelDayView?.textMarginRight=marginRight
    }

    override fun setTextMarginRight(marginRight: Float, isDp: Boolean) {
        wheelYearView?.setTextMarginRight(marginRight,isDp)
        wheelMonthView?.setTextMarginRight(marginRight,isDp)
        wheelDayView?.setTextMarginRight(marginRight,isDp)
    }

    override fun setTypeface(typeface: Typeface) {
        wheelYearView?.setTypeface(typeface, false)
        wheelMonthView?.setTypeface(typeface, false)
        wheelDayView?.setTypeface(typeface, false)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        wheelYearView?.setTypeface(typeface, isBoldForSelectedItem)
        wheelMonthView?.setTypeface(typeface, isBoldForSelectedItem)
        wheelDayView?.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        wheelYearView?.isShowDivider = showDivider
        wheelMonthView?.isShowDivider = showDivider
        wheelDayView?.isShowDivider = showDivider
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        wheelYearView?.dividerColor = dividerColor
        wheelMonthView?.dividerColor = dividerColor
        wheelDayView?.dividerColor = dividerColor
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        wheelYearView?.setDividerColorRes(dividerColorRes)
        wheelMonthView?.setDividerColorRes(dividerColorRes)
        wheelDayView?.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeight: Float) {
        wheelYearView?.dividerHeight = dividerHeight
        wheelMonthView?.dividerHeight = dividerHeight
        wheelDayView?.dividerHeight = dividerHeight
    }

    override fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        wheelYearView?.setDividerHeight(dividerHeight, isDp)
        wheelMonthView?.setDividerHeight(dividerHeight, isDp)
        wheelDayView?.setDividerHeight(dividerHeight, isDp)
    }

    override fun setDividerType(dividerType: Int) {
        wheelYearView?.dividerType = dividerType
        wheelMonthView?.dividerType = dividerType
        wheelDayView?.dividerType = dividerType
    }

    override fun setDividerPaddingForWrap(padding: Float) {
        wheelYearView?.dividerPadding = padding
        wheelMonthView?.dividerPadding = padding
        wheelDayView?.dividerPadding = padding
    }

    override fun setDividerPaddingForWrap(padding: Float, isDp: Boolean) {
        wheelYearView?.setDividerPaddingForWrap(padding, isDp)
        wheelMonthView?.setDividerPaddingForWrap(padding, isDp)
        wheelDayView?.setDividerPaddingForWrap(padding, isDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        wheelYearView?.dividerCap = cap
        wheelMonthView?.dividerCap = cap
        wheelDayView?.dividerCap = cap
    }

    override fun setDividerOffsetY(offsetY: Float) {
        wheelYearView?.dividerOffsetY = offsetY
        wheelMonthView?.dividerOffsetY = offsetY
        wheelDayView?.dividerOffsetY = offsetY
    }

    override fun setDividerOffsetY(offsetY: Float, isDp: Boolean) {
        wheelYearView?.setDividerOffsetY(offsetY, isDp)
        wheelMonthView?.setDividerOffsetY(offsetY, isDp)
        wheelDayView?.setDividerOffsetY(offsetY, isDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        wheelYearView?.isShowCurtain = showCurtain
        wheelMonthView?.isShowCurtain = showCurtain
        wheelDayView?.isShowCurtain = showCurtain
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        wheelYearView?.curtainColor = curtainColor
        wheelMonthView?.curtainColor = curtainColor
        wheelDayView?.curtainColor = curtainColor
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        wheelYearView?.setCurtainColorRes(curtainColorRes)
        wheelMonthView?.setCurtainColorRes(curtainColorRes)
        wheelDayView?.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        wheelYearView?.isCurved = curved
        wheelMonthView?.isCurved = curved
        wheelDayView?.isCurved = curved
    }

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
        wheelYearView?.curvedArcDirection = direction
        wheelMonthView?.curvedArcDirection = direction
        wheelDayView?.curvedArcDirection = direction
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        wheelYearView?.curvedArcDirectionFactor = factor
        wheelMonthView?.curvedArcDirectionFactor = factor
        wheelDayView?.curvedArcDirectionFactor = factor
    }

    override fun setRefractRatio(ratio: Float) {
        wheelYearView?.refractRatio = ratio
        wheelMonthView?.refractRatio = ratio
        wheelDayView?.refractRatio = ratio
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        wheelYearView?.isSoundEffect = soundEffect
        wheelMonthView?.isSoundEffect = soundEffect
        wheelDayView?.isSoundEffect = soundEffect
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        wheelYearView?.setSoundResource(soundRes)
        wheelMonthView?.setSoundResource(soundRes)
        wheelDayView?.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        wheelYearView?.setSoundVolume(playVolume)
        wheelMonthView?.setSoundVolume(playVolume)
        wheelDayView?.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        wheelYearView?.isResetSelectedPosition = reset
        wheelMonthView?.isResetSelectedPosition = reset
        wheelDayView?.isResetSelectedPosition = reset
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        wheelYearView?.canOverRangeScroll = canOverRange
        wheelMonthView?.canOverRangeScroll = canOverRange
        wheelDayView?.canOverRangeScroll = canOverRange
    }
}