package com.zyyoona7.picker.helper

import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.WheelDayView
import com.zyyoona7.picker.ex.WheelMonthView
import com.zyyoona7.picker.ex.WheelYearView
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.WheelView
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
    private var selectedRangeMode: WheelView.SelectedRangeMode = WheelView.SelectedRangeMode.OVER_RANGE_SCROLL
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

    override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
        val yearId = wheelYearView?.id ?: -1
        val monthId = wheelMonthView?.id ?: -1
        when (wheelView.id) {
            yearId -> {
                val selectedYear = wheelYearView?.getItem(position) ?: DEFAULT_YEAR
                wheelDayView?.year = selectedYear
                when (selectedYear) {
                    minYear -> {
                        wheelMonthView?.setSelectedMonthRange(minMonth, WheelMonthView.MAX_MONTH, selectedRangeMode)
                        val selectedMonth = getSelectedMonth()
                        if (selectedMonth == minMonth) {
                            wheelDayView?.let {
                                it.setSelectedDayRange(minDay, it.getMaxDay(), selectedRangeMode)
                            }
                        } else {
                            wheelDayView?.setSelectedDayRange(-1, -1)
                        }
                    }
                    maxYear -> {
                        wheelMonthView?.setSelectedMonthRange(WheelMonthView.MIN_MONTH, maxMonth, selectedRangeMode)
                        val selectedMonth = getSelectedMonth()
                        if (selectedMonth == maxMonth) {
                            wheelDayView?.setSelectedDayRange(WheelDayView.MIN_DAY, maxDay, selectedRangeMode)
                        } else {
                            wheelDayView?.setSelectedDayRange(-1, -1)
                        }
                    }
                    else -> {
                        wheelMonthView?.setSelectedMonthRange(-1, -1)
                        wheelDayView?.setSelectedDayRange(-1, -1)
                    }
                }
            }
            monthId -> {
                wheelDayView?.month = wheelMonthView?.getItem(position)
                        ?: DEFAULT_MONTH
                val selectedYear = getSelectedYear()
                val selectedMonth = wheelMonthView?.getItem(position) ?: DEFAULT_MONTH
                //如果选中的年份是最小选中年或者最大选中年 并且 选中的月份是最小选中月或者最大选中月-
                //才限制选择 Day 的范围
                if (selectedMonth == minMonth && selectedYear == minYear) {
                    wheelDayView?.let {
                        it.setSelectedDayRange(minDay, it.getMaxDay(), selectedRangeMode)
                    }
                } else if (selectedMonth == maxMonth && selectedYear == maxYear) {
                    wheelDayView?.setSelectedDayRange(WheelDayView.MIN_DAY, maxDay, selectedRangeMode)
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

    override fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int) {
        scrollChangedListener?.onScrollChanged(wheelView, scrollOffsetY)
    }

    override fun onScrollStateChanged(wheelView: WheelView, state: Int) {
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

    override fun setYearRange(startYear: Int, endYear: Int) {
        wheelYearView?.setYearRange(startYear, endYear)
    }

    override fun setSelectedDate(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        setSelectedDate(calendar)
    }

    override fun setSelectedDate(calendar: Calendar) {
        setSelectedDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun setSelectedDate(year: Int, month: Int, day: Int) {
        wheelYearView?.setSelectedYear(year)
        wheelMonthView?.setSelectedMonth(month)
        wheelDayView?.setSelectedDay(day)
    }

    override fun setMaxSelectedDate(maxDate: Date) {
        val maxCalendar = Calendar.getInstance()
        maxCalendar.time = maxDate
        setMaxSelectedDate(maxCalendar, WheelView.SelectedRangeMode.OVER_RANGE_SCROLL)
    }

    override fun setMaxSelectedDate(maxDate: Date, selectedRangeMode: WheelView.SelectedRangeMode) {
        val maxCalendar = Calendar.getInstance()
        maxCalendar.time = maxDate
        setMaxSelectedDate(maxCalendar, selectedRangeMode)
    }

    override fun setMaxSelectedDate(maxCalendar: Calendar) {
        setMaxSelectedDate(maxCalendar, WheelView.SelectedRangeMode.OVER_RANGE_SCROLL)
    }

    override fun setMaxSelectedDate(maxCalendar: Calendar, selectedRangeMode: WheelView.SelectedRangeMode) {
        minYear = getWheelYearView().getItem(0) ?: 1970
        maxYear = maxCalendar.get(Calendar.YEAR)
        minMonth = WheelMonthView.MIN_MONTH
        maxMonth = maxCalendar.get(Calendar.MONTH) + 1
        minDay = WheelDayView.MIN_DAY
        maxDay = maxCalendar.get(Calendar.DAY_OF_MONTH)
        this.selectedRangeMode = selectedRangeMode
        wheelYearView?.setSelectedYearRange(maxYear = maxYear, selectedRangeMode = selectedRangeMode)
    }

    override fun setDateRange(minDate: Date, maxDate: Date) {
        val minCalendar = Calendar.getInstance()
        minCalendar.time = minDate
        val maxCalendar = Calendar.getInstance()
        maxCalendar.time = maxDate
        setDateRange(minCalendar, maxCalendar, WheelView.SelectedRangeMode.OVER_RANGE_SCROLL)
    }

    override fun setDateRange(minDate: Date, maxDate: Date, selectedRangeMode: WheelView.SelectedRangeMode) {
        val minCalendar = Calendar.getInstance()
        minCalendar.time = minDate
        val maxCalendar = Calendar.getInstance()
        maxCalendar.time = maxDate
        setDateRange(minCalendar, maxCalendar, selectedRangeMode)
    }

    override fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar) {
        setDateRange(minCalendar, maxCalendar, WheelView.SelectedRangeMode.OVER_RANGE_SCROLL)
    }

    override fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar,
                              selectedRangeMode: WheelView.SelectedRangeMode) {
        minYear = minCalendar.get(Calendar.YEAR)
        maxYear = maxCalendar.get(Calendar.YEAR)
        minMonth = minCalendar.get(Calendar.MONTH) + 1
        maxMonth = maxCalendar.get(Calendar.MONTH) + 1
        minDay = minCalendar.get(Calendar.DAY_OF_MONTH)
        maxDay = maxCalendar.get(Calendar.DAY_OF_MONTH)
        this.selectedRangeMode = selectedRangeMode
        wheelYearView?.setSelectedYearRange(minYear, maxYear, selectedRangeMode)
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

    override fun setShowYear(isShow: Boolean) {
        wheelYearView?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun setShowMonth(isShow: Boolean) {
        wheelMonthView?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun setShowDay(isShow: Boolean) {
        wheelDayView?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun setYearMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        wheelYearView?.maxTextWidthMeasureType = measureType
    }

    override fun setMonthMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        wheelMonthView?.maxTextWidthMeasureType = measureType
    }

    override fun setDayMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        wheelDayView?.maxTextWidthMeasureType = measureType
    }

    override fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        setMaxTextWidthMeasureType(measureType, measureType, measureType)
    }

    override fun setMaxTextWidthMeasureType(yearType: WheelView.MeasureType,
                                            monthType: WheelView.MeasureType,
                                            dayType: WheelView.MeasureType) {
        setYearMaxTextWidthMeasureType(yearType)
        setMonthMaxTextWidthMeasureType(monthType)
        setDayMaxTextWidthMeasureType(dayType)
    }

    override fun getSelectedDateStr(): String {
        return "${getSelectedYear()}-${getSelectedMonth()}-${getSelectedDay()}"
    }

    override fun getSelectedYear(): Int {
        return getWheelYearView().getSelectedItem<Int>() ?: DEFAULT_YEAR
    }

    override fun getSelectedMonth(): Int {
        return getWheelMonthView().getSelectedItem<Int>() ?: DEFAULT_MONTH
    }

    override fun getSelectedDay(): Int {
        return getWheelDayView().getSelectedItem<Int>() ?: DEFAULT_DAY
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

    override fun setLineSpacing(lineSpacingPx: Int) {
        wheelYearView?.lineSpacing = lineSpacingPx
        wheelMonthView?.lineSpacing = lineSpacingPx
        wheelDayView?.lineSpacing = lineSpacingPx
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        wheelYearView?.setLineSpacing(lineSpacingDp)
        wheelMonthView?.setLineSpacing(lineSpacingDp)
        wheelDayView?.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        wheelYearView?.isCyclic = isCyclic
        wheelMonthView?.isCyclic = isCyclic
        wheelDayView?.isCyclic = isCyclic
    }

    override fun setTextSize(textSizePx: Int) {
        wheelYearView?.textSize = textSizePx
        wheelMonthView?.textSize = textSizePx
        wheelDayView?.textSize = textSizePx
    }

    override fun setTextSize(textSizeSp: Float) {
        wheelYearView?.setTextSize(textSizeSp)
        wheelMonthView?.setTextSize(textSizeSp)
        wheelDayView?.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        wheelYearView?.isAutoFitTextSize = autoFit
        wheelMonthView?.isAutoFitTextSize = autoFit
        wheelDayView?.isAutoFitTextSize = autoFit
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        wheelYearView?.minTextSize = minTextSizePx
        wheelMonthView?.minTextSize = minTextSizePx
        wheelDayView?.minTextSize = minTextSizePx
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        wheelYearView?.setMinTextSize(minTextSizeSp)
        wheelMonthView?.setMinTextSize(minTextSizeSp)
        wheelDayView?.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(textAlign: Paint.Align) {
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

    override fun setTextPadding(paddingPx: Int) {
        wheelYearView?.textPaddingLeft = paddingPx
        wheelMonthView?.textPaddingLeft = paddingPx
        wheelDayView?.textPaddingLeft = paddingPx
        wheelYearView?.textPaddingRight = paddingPx
        wheelMonthView?.textPaddingRight = paddingPx
        wheelDayView?.textPaddingRight = paddingPx
    }

    override fun setTextPadding(paddingDp: Float) {
        wheelYearView?.setTextPadding(paddingDp)
        wheelMonthView?.setTextPadding(paddingDp)
        wheelDayView?.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        wheelYearView?.textPaddingLeft = textPaddingLeftPx
        wheelMonthView?.textPaddingLeft = textPaddingLeftPx
        wheelDayView?.textPaddingLeft = textPaddingLeftPx
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        wheelYearView?.setTextPaddingLeft(textPaddingLeftDp)
        wheelMonthView?.setTextPaddingLeft(textPaddingLeftDp)
        wheelDayView?.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        wheelYearView?.textPaddingRight = textPaddingRightPx
        wheelMonthView?.textPaddingRight = textPaddingRightPx
        wheelDayView?.textPaddingRight = textPaddingRightPx
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        wheelYearView?.setTextPaddingRight(textPaddingRightDp)
        wheelMonthView?.setTextPaddingRight(textPaddingRightDp)
        wheelDayView?.setTextPaddingRight(textPaddingRightDp)
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

    override fun setDividerHeight(dividerHeightPx: Int) {
        wheelYearView?.dividerHeight = dividerHeightPx
        wheelMonthView?.dividerHeight = dividerHeightPx
        wheelDayView?.dividerHeight = dividerHeightPx
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        wheelYearView?.setDividerHeight(dividerHeightDp)
        wheelMonthView?.setDividerHeight(dividerHeightDp)
        wheelDayView?.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(dividerType: WheelView.DividerType) {
        wheelYearView?.dividerType = dividerType
        wheelMonthView?.dividerType = dividerType
        wheelDayView?.dividerType = dividerType
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        wheelYearView?.dividerPadding = paddingPx
        wheelMonthView?.dividerPadding = paddingPx
        wheelDayView?.dividerPadding = paddingPx
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        wheelYearView?.setDividerPadding(paddingDp)
        wheelMonthView?.setDividerPadding(paddingDp)
        wheelDayView?.setDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        wheelYearView?.dividerCap = cap
        wheelMonthView?.dividerCap = cap
        wheelDayView?.dividerCap = cap
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        wheelYearView?.dividerOffsetY = offsetYPx
        wheelMonthView?.dividerOffsetY = offsetYPx
        wheelDayView?.dividerOffsetY = offsetYPx
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        wheelYearView?.setDividerOffsetY(offsetYDp)
        wheelMonthView?.setDividerOffsetY(offsetYDp)
        wheelDayView?.setDividerOffsetY(offsetYDp)
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

    override fun setCurvedArcDirection(direction: WheelView.CurvedArcDirection) {
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

    override fun setLeftText(text: CharSequence) {
        setLeftText(text, text, text)
    }

    override fun setLeftText(yearLeft: CharSequence, monthLeft: CharSequence, dayLeft: CharSequence) {
        wheelYearView?.leftText = yearLeft
        wheelMonthView?.leftText = monthLeft
        wheelDayView?.leftText = dayLeft
    }

    override fun setRightText(text: CharSequence) {
        setRightText(text, text, text)
    }

    override fun setRightText(yearRight: CharSequence, monthRight: CharSequence, dayRight: CharSequence) {
        wheelYearView?.rightText = yearRight
        wheelMonthView?.rightText = monthRight
        wheelDayView?.rightText = dayRight
    }

    override fun setLeftTextSize(textSizePx: Int) {
        wheelYearView?.leftTextSize = textSizePx
        wheelMonthView?.leftTextSize = textSizePx
        wheelDayView?.leftTextSize = textSizePx
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        wheelYearView?.setLeftTextSize(textSizeSp)
        wheelMonthView?.setLeftTextSize(textSizeSp)
        wheelDayView?.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        wheelYearView?.rightTextSize = textSizePx
        wheelMonthView?.rightTextSize = textSizePx
        wheelDayView?.rightTextSize = textSizePx
    }

    override fun setRightTextSize(textSizeSp: Float) {
        wheelYearView?.setRightTextSize(textSizeSp)
        wheelMonthView?.setRightTextSize(textSizeSp)
        wheelDayView?.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        wheelYearView?.leftTextColor = color
        wheelMonthView?.leftTextColor = color
        wheelDayView?.leftTextColor = color
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        wheelYearView?.setLeftTextColorRes(colorRes)
        wheelMonthView?.setLeftTextColorRes(colorRes)
        wheelDayView?.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        wheelYearView?.rightTextColor = color
        wheelMonthView?.rightTextColor = color
        wheelDayView?.rightTextColor = color
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        wheelYearView?.setRightTextColorRes(colorRes)
        wheelMonthView?.setRightTextColorRes(colorRes)
        wheelDayView?.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        wheelYearView?.leftTextMarginRight = marginRightPx
        wheelMonthView?.leftTextMarginRight = marginRightPx
        wheelDayView?.leftTextMarginRight = marginRightPx
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        wheelYearView?.setLeftTextMarginRight(marginRightDp)
        wheelMonthView?.setLeftTextMarginRight(marginRightDp)
        wheelDayView?.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        wheelYearView?.rightTextMarginLeft = marginLeftPx
        wheelMonthView?.rightTextMarginLeft = marginLeftPx
        wheelDayView?.rightTextMarginLeft = marginLeftPx
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        wheelYearView?.setRightTextMarginLeft(marginLeftDp)
        wheelMonthView?.setRightTextMarginLeft(marginLeftDp)
        wheelDayView?.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        wheelYearView?.leftTextGravity = gravity
        wheelMonthView?.leftTextGravity = gravity
        wheelDayView?.leftTextGravity = gravity
    }

    override fun setRightTextGravity(gravity: Int) {
        wheelYearView?.rightTextGravity = gravity
        wheelMonthView?.rightTextGravity = gravity
        wheelDayView?.rightTextGravity = gravity
    }
}