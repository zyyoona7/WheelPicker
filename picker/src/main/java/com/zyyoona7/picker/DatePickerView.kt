package com.zyyoona7.picker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.ex.WheelDayView
import com.zyyoona7.picker.ex.WheelMonthView
import com.zyyoona7.picker.ex.WheelYearView
import com.zyyoona7.picker.helper.DatePickerHelper
import com.zyyoona7.picker.interfaces.DatePicker
import com.zyyoona7.picker.interfaces.WheelPicker
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnScrollChangedListener
import java.util.*

/**
 * 日期 选择器View
 *
 * @author zyyoona7
 */
class DatePickerView @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr), DatePicker, WheelPicker {

    private val datePickerHelper: DatePickerHelper
    private var widthWeightMode = false
    private var yearWeight: Float = 1f
    private var monthWeight: Float = 1f
    private var dayWeight: Float = 1f

    init {
        val wheelYearView = WheelYearView(context)
        val wheelMonthView = WheelMonthView(context)
        val wheelDayView = WheelDayView(context)
        wheelYearView.id = R.id.wheel_year
        wheelMonthView.id = R.id.wheel_month
        wheelDayView.id = R.id.wheel_day
        datePickerHelper = DatePickerHelper(wheelYearView, wheelMonthView, wheelDayView)
        attrs?.let {
            initAttrs(context, it)
        }
        addViews(wheelYearView, wheelMonthView, wheelDayView)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DatePickerView)
        widthWeightMode = typedArray.getBoolean(R.styleable.DatePickerView_dpv_widthWeightMode, false)
        yearWeight = typedArray.getFloat(R.styleable.DatePickerView_dpv_yearWeight, 1f)
        monthWeight = typedArray.getFloat(R.styleable.DatePickerView_dpv_monthWeight, 1f)
        dayWeight = typedArray.getFloat(R.styleable.DatePickerView_dpv_dayWeight, 1f)
        val startYear = typedArray.getInt(R.styleable.DatePickerView_dpv_startYear, -1)
        val endYear = typedArray.getInt(R.styleable.DatePickerView_dpv_endYear, -1)
        if (startYear > 0 && endYear > 0 && endYear > startYear) {
            setYearRange(startYear, endYear)
        }
        val selectedYear = typedArray.getInt(R.styleable.DatePickerView_dpv_selectedYear, -1)
        val selectedMonth = typedArray.getInt(R.styleable.DatePickerView_dpv_selectedMonth, -1)
        val selectedDay = typedArray.getInt(R.styleable.DatePickerView_dpv_selectedDay, -1)
        if (selectedYear > 0 && selectedMonth > 0 && selectedDay > 0) {
            setSelectedDate(selectedYear, selectedMonth, selectedDay)
        }
        setVisibleItems(typedArray.getInt(R.styleable.DatePickerView_dpv_visibleItems,
                WheelView.DEFAULT_VISIBLE_ITEM))
        setLineSpacing(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_lineSpacing,
                WheelView.DEFAULT_LINE_SPACING))
        setCyclic(typedArray.getBoolean(R.styleable.DatePickerView_dpv_cyclic, false))
        setTextSize(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_textSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setTextAlign(typedArray.getInt(R.styleable.DatePickerView_dpv_textAlign,
                WheelView.TEXT_ALIGN_CENTER))
        setTextPadding(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_textPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        val yearLeftText = typedArray.getText(R.styleable.DatePickerView_dpv_yearLeftText) ?: ""
        val monthLeftText = typedArray.getText(R.styleable.DatePickerView_dpv_monthLeftText) ?: ""
        val dayLeftText = typedArray.getText(R.styleable.DatePickerView_dpv_dayLeftText) ?: ""
        setLeftText(yearLeftText, monthLeftText, dayLeftText)
        val yearRightText = typedArray.getText(R.styleable.DatePickerView_dpv_yearRightText) ?: ""
        val monthRightText = typedArray.getText(R.styleable.DatePickerView_dpv_monthRightText) ?: ""
        val dayRightText = typedArray.getText(R.styleable.DatePickerView_dpv_dayRightText) ?: ""
        setRightText(yearRightText, monthRightText, dayRightText)
        setLeftTextSize(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_leftTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setRightTextSize(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_rightTextSize,
                WheelView.DEFAULT_TEXT_SIZE))
        setLeftTextMarginRight(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_leftTextMarginRight,
                WheelView.DEFAULT_TEXT_PADDING))
        setRightTextMarginLeft(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_rightTextMarginLeft,
                WheelView.DEFAULT_TEXT_PADDING))
        setLeftTextColor(typedArray.getColor(R.styleable.DatePickerView_dpv_leftTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setRightTextColor(typedArray.getColor(R.styleable.DatePickerView_dpv_rightTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        val leftGravity = typedArray.getInt(R.styleable.DatePickerView_dpv_leftTextGravity, 0)
        setLeftTextGravity(WheelView.getExtraGravity(leftGravity))
        val rightGravity = typedArray.getInt(R.styleable.DatePickerView_dpv_rightTextGravity, 0)
        setRightTextGravity(WheelView.getExtraGravity(rightGravity))
        setNormalTextColor(typedArray.getColor(R.styleable.DatePickerView_dpv_normalTextColor,
                WheelView.DEFAULT_NORMAL_TEXT_COLOR))
        setSelectedTextColor(typedArray.getColor(R.styleable.DatePickerView_dpv_selectedTextColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setShowDivider(typedArray.getBoolean(R.styleable.DatePickerView_dpv_showDivider, false))
        setDividerType(typedArray.getInt(R.styleable.DatePickerView_dpv_dividerType, WheelView.DIVIDER_FILL))
        setDividerColor(typedArray.getColor(R.styleable.DatePickerView_dpv_dividerColor,
                WheelView.DEFAULT_SELECTED_TEXT_COLOR))
        setDividerHeight(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_dividerHeight,
                WheelView.DEFAULT_DIVIDER_HEIGHT))
        setWheelDividerPadding(typedArray.getDimensionPixelSize(R.styleable.DatePickerView_dpv_dividerPadding,
                WheelView.DEFAULT_TEXT_PADDING))
        setDividerOffsetY(typedArray.getDimensionPixelOffset(R.styleable.DatePickerView_dpv_dividerOffsetY, 0))
        setCurved(typedArray.getBoolean(R.styleable.DatePickerView_dpv_curved, true))
        setCurvedArcDirection(typedArray.getInt(R.styleable.DatePickerView_dpv_curvedArcDirection,
                WheelView.CURVED_ARC_DIRECTION_CENTER))
        setCurvedArcDirectionFactor(typedArray.getFloat(R.styleable.DatePickerView_dpv_curvedArcDirectionFactor,
                WheelView.DEFAULT_CURVED_FACTOR))
        setShowCurtain(typedArray.getBoolean(R.styleable.DatePickerView_dpv_showCurtain, false))
        setCurtainColor(typedArray.getColor(R.styleable.DatePickerView_dpv_curtainColor, Color.TRANSPARENT))
        typedArray.recycle()
    }

    private fun addViews(wheelYearView: WheelYearView, wheelMonthView: WheelMonthView,
                         wheelDayView: WheelDayView) {
        orientation = HORIZONTAL
        val width = if (widthWeightMode) 0 else ViewGroup.LayoutParams.WRAP_CONTENT
        val yearLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val monthLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dayLp = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        yearLp.gravity = Gravity.CENTER_VERTICAL
        monthLp.gravity = Gravity.CENTER_VERTICAL
        dayLp.gravity = Gravity.CENTER_VERTICAL
        if (widthWeightMode) {
            yearLp.weight = yearWeight
            monthLp.weight = monthWeight
            dayLp.weight = dayWeight
        }
        addView(wheelYearView, yearLp)
        addView(wheelMonthView, monthLp)
        addView(wheelDayView, dayLp)
    }

    override fun setYearTextFormatter(textFormatter: IntTextFormatter) {
        datePickerHelper.setYearTextFormatter(textFormatter)
    }

    override fun setMonthTextFormatter(textFormatter: IntTextFormatter) {
        datePickerHelper.setMonthTextFormatter(textFormatter)
    }

    override fun setDayTextFormatter(textFormatter: IntTextFormatter) {
        datePickerHelper.setDayTextFormatter(textFormatter)
    }

    override fun setOnDateSelectedListener(listener: OnDateSelectedListener?) {
        datePickerHelper.setOnDateSelectedListener(listener)
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        datePickerHelper.setOnScrollChangedListener(listener)
    }

    override fun setYearRange(startYear: Int, endYear: Int) {
        datePickerHelper.setYearRange(startYear, endYear)
    }

    override fun setSelectedDate(date: Date) {
        datePickerHelper.setSelectedDate(date)
    }

    override fun setSelectedDate(year: Int, month: Int, day: Int) {
        datePickerHelper.setSelectedDate(year, month, day)
    }

    override fun setMaxSelectedDate(maxDate: Date) {
        datePickerHelper.setMaxSelectedDate(maxDate)
    }

    override fun setMaxSelectedDate(maxCalendar: Calendar) {
        datePickerHelper.setMaxSelectedDate(maxCalendar)
    }

    override fun setDateRange(minDate: Date, maxDate: Date) {
        datePickerHelper.setDateRange(minDate, maxDate)
    }

    override fun setDateRange(minCalendar: Calendar, maxCalendar: Calendar) {
        datePickerHelper.setDateRange(minCalendar, maxCalendar)
    }

    override fun getSelectedDate(): Date {
        return datePickerHelper.getSelectedDate()
    }

    override fun getSelectedDateStr(): String {
        return datePickerHelper.getSelectedDateStr()
    }

    override fun getSelectedYear(): Int {
        return datePickerHelper.getSelectedYear()
    }

    override fun getSelectedMonth(): Int {
        return datePickerHelper.getSelectedMonth()
    }

    override fun getSelectedDay(): Int {
        return datePickerHelper.getSelectedDay()
    }

    override fun getWheelYearView(): WheelYearView {
        return datePickerHelper.getWheelYearView()
    }

    override fun getWheelMonthView(): WheelMonthView {
        return datePickerHelper.getWheelMonthView()
    }

    override fun getWheelDayView(): WheelDayView {
        return datePickerHelper.getWheelDayView()
    }

    override fun setVisibleItems(visibleItems: Int) {
        datePickerHelper.setVisibleItems(visibleItems)
    }

    override fun setLineSpacing(lineSpacingPx: Int) {
        datePickerHelper.setLineSpacing(lineSpacingPx)
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        datePickerHelper.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        datePickerHelper.setCyclic(isCyclic)
    }

    override fun setTextSize(textSizePx: Int) {
        datePickerHelper.setTextSize(textSizePx)
    }

    override fun setTextSize(textSizeSp: Float) {
        datePickerHelper.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        datePickerHelper.setAutoFitTextSize(autoFit)
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        datePickerHelper.setMinTextSize(minTextSizePx)
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        datePickerHelper.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(@WheelView.TextAlign textAlign: Int) {
        datePickerHelper.setTextAlign(textAlign)
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        datePickerHelper.setNormalTextColor(textColor)
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        datePickerHelper.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        datePickerHelper.setSelectedTextColor(textColor)
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        datePickerHelper.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextPadding(paddingPx: Int) {
        datePickerHelper.setTextPadding(paddingPx)
    }

    override fun setTextPadding(paddingDp: Float) {
        datePickerHelper.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        datePickerHelper.setTextPaddingLeft(textPaddingLeftPx)
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        datePickerHelper.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        datePickerHelper.setTextPaddingRight(textPaddingRightPx)
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        datePickerHelper.setTextPaddingRight(textPaddingRightDp)
    }

    override fun setTypeface(typeface: Typeface) {
        datePickerHelper.setTypeface(typeface)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        datePickerHelper.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        datePickerHelper.setShowDivider(showDivider)
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        datePickerHelper.setDividerColor(dividerColor)
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        datePickerHelper.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeightPx: Int) {
        datePickerHelper.setDividerHeight(dividerHeightPx)
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        datePickerHelper.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(dividerType: Int) {
        datePickerHelper.setDividerType(dividerType)
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        datePickerHelper.setWheelDividerPadding(paddingPx)
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        datePickerHelper.setWheelDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        datePickerHelper.setDividerCap(cap)
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        datePickerHelper.setDividerOffsetY(offsetYPx)
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        datePickerHelper.setDividerOffsetY(offsetYDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        datePickerHelper.setShowCurtain(showCurtain)
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        datePickerHelper.setCurtainColor(curtainColor)
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        datePickerHelper.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        datePickerHelper.setCurved(curved)
    }

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
        datePickerHelper.setCurvedArcDirection(direction)
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        datePickerHelper.setCurvedArcDirectionFactor(factor)
    }

    override fun setRefractRatio(ratio: Float) {
        datePickerHelper.setRefractRatio(ratio)
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        datePickerHelper.setSoundEffect(soundEffect)
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        datePickerHelper.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        datePickerHelper.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        datePickerHelper.setResetSelectedPosition(reset)
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        datePickerHelper.setCanOverRangeScroll(canOverRange)
    }

    override fun setLeftText(text: CharSequence) {
        datePickerHelper.setLeftText(text)
    }

    override fun setLeftText(yearLeft: CharSequence, monthLeft: CharSequence, dayLeft: CharSequence) {
        datePickerHelper.setLeftText(yearLeft, monthLeft, dayLeft)
    }

    override fun setRightText(text: CharSequence) {
        datePickerHelper.setRightText(text)
    }

    override fun setRightText(yearRight: CharSequence, monthRight: CharSequence, dayRight: CharSequence) {
        datePickerHelper.setRightText(yearRight, monthRight, dayRight)
    }

    override fun setLeftTextSize(textSizePx: Int) {
        datePickerHelper.setLeftTextSize(textSizePx)
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        datePickerHelper.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        datePickerHelper.setRightTextSize(textSizePx)
    }

    override fun setRightTextSize(textSizeSp: Float) {
        datePickerHelper.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        datePickerHelper.setLeftTextColor(color)
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        datePickerHelper.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        datePickerHelper.setRightTextColor(color)
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        datePickerHelper.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        datePickerHelper.setLeftTextMarginRight(marginRightPx)
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        datePickerHelper.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        datePickerHelper.setRightTextMarginLeft(marginLeftPx)
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        datePickerHelper.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        datePickerHelper.setLeftTextGravity(gravity)
    }

    override fun setRightTextGravity(gravity: Int) {
        datePickerHelper.setRightTextGravity(gravity)
    }
}
