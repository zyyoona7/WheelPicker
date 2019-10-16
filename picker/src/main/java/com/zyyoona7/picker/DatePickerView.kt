package com.zyyoona7.picker

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
    val yearTextView: TextView
    val monthTextView: TextView
    val dayTextView: TextView

    companion object {
        private const val DEFAULT_TEXT_SIZE = 16f
        private const val DEFAULT_TEXT_COLOR = Color.BLACK
    }

    init {
        View.inflate(context, R.layout.layout_date_picker_view, this)
        yearTextView = findViewById(R.id.tv_year)
        monthTextView = findViewById(R.id.tv_month)
        dayTextView = findViewById(R.id.tv_day)
        val wheelYearView = findViewById<WheelYearView>(R.id.wv_year)
        val wheelMonthView = findViewById<WheelMonthView>(R.id.wv_month)
        val wheelDayView = findViewById<WheelDayView>(R.id.wv_day)
        datePickerHelper = DatePickerHelper(wheelYearView, wheelMonthView, wheelDayView)

        setLabelTextSize(DEFAULT_TEXT_SIZE)
        setLabelTextColor(DEFAULT_TEXT_COLOR)
        setTextSize(DEFAULT_TEXT_SIZE, true)
    }

    fun setShowLabelText(isShow: Boolean) {
        val visibility: Int = if (isShow) View.VISIBLE else View.GONE
        yearTextView.visibility = visibility
        monthTextView.visibility = visibility
        dayTextView.visibility = visibility
    }

    fun setLabelTextColor(@ColorInt textColor: Int) {
        yearTextView.setTextColor(textColor)
        monthTextView.setTextColor(textColor)
        dayTextView.setTextColor(textColor)
    }

    fun setLabelTextSize(textSize: Float) {
        yearTextView.textSize = textSize
        monthTextView.textSize = textSize
        dayTextView.textSize = textSize
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

    override fun setSelectedDate(date: Date) {
        datePickerHelper.setSelectedDate(date)
    }

    override fun setSelectedDate(year: Int, month: Int, day: Int) {
        datePickerHelper.setSelectedDate(year, month, day)
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

    override fun setLineSpacing(lineSpacing: Float) {
        datePickerHelper.setLineSpacing(lineSpacing)
    }

    override fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        datePickerHelper.setLineSpacing(lineSpacing, isDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        datePickerHelper.setCyclic(isCyclic)
    }

    override fun setTextSize(textSize: Float) {
        datePickerHelper.setTextSize(textSize)
    }

    override fun setTextSize(textSize: Float, isSp: Boolean) {
        datePickerHelper.setTextSize(textSize, isSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        datePickerHelper.setAutoFitTextSize(autoFit)
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

    override fun setTextMargins(margin: Float) {
        datePickerHelper.setTextMargins(margin)
    }

    override fun setTextMargins(margin: Float, isDp: Boolean) {
        datePickerHelper.setTextMargins(margin, isDp)
    }

    override fun setTextMarginLeft(marginLeft: Float) {
        datePickerHelper.setTextMarginLeft(marginLeft)
    }

    override fun setTextMarginLeft(marginLeft: Float, isDp: Boolean) {
        datePickerHelper.setTextMarginLeft(marginLeft, isDp)
    }

    override fun setTextMarginRight(marginRight: Float) {
        datePickerHelper.setTextMarginRight(marginRight)
    }

    override fun setTextMarginRight(marginRight: Float, isDp: Boolean) {
        datePickerHelper.setTextMarginRight(marginRight, isDp)
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

    override fun setDividerHeight(dividerHeight: Float) {
        datePickerHelper.setDividerHeight(dividerHeight)
    }

    override fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        datePickerHelper.setDividerHeight(dividerHeight, isDp)
    }

    override fun setDividerType(dividerType: Int) {
        datePickerHelper.setDividerType(dividerType)
    }

    override fun setDividerPaddingForWrap(padding: Float) {
        datePickerHelper.setDividerPaddingForWrap(padding)
    }

    override fun setDividerPaddingForWrap(padding: Float, isDp: Boolean) {
        datePickerHelper.setDividerPaddingForWrap(padding, isDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        datePickerHelper.setDividerCap(cap)
    }

    override fun setDividerOffsetY(offsetY: Float) {
        datePickerHelper.setDividerOffsetY(offsetY)
    }

    override fun setDividerOffsetY(offsetY: Float, isDp: Boolean) {
        datePickerHelper.setDividerOffsetY(offsetY, isDp)
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
}
