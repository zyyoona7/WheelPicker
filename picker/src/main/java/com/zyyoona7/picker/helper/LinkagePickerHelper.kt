package com.zyyoona7.picker.helper

import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import com.zyyoona7.picker.interfaces.LinkagePicker
import com.zyyoona7.picker.interfaces.WheelPicker
import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.picker.listener.OnRequestData2Listener
import com.zyyoona7.picker.listener.OnRequestData3Listener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener

class LinkagePickerHelper(private var wheelView1: WheelView?,
                          private var wheelView2: WheelView?,
                          private var wheelView3: WheelView?)
    : OnItemSelectedListener, OnScrollChangedListener, LinkagePicker, WheelPicker {

    private var requestData2Listener: OnRequestData2Listener? = null
    private var requestData3Listener: OnRequestData3Listener? = null
    private var scrollChangedListener: OnScrollChangedListener? = null
    private var linkageSelectedListener: OnLinkageSelectedListener? = null

    init {
        wheelView1?.setOnItemSelectedListener(this)
        wheelView2?.setOnItemSelectedListener(this)
        wheelView3?.setOnItemSelectedListener(this)
        wheelView1?.setOnScrollChangedListener(this)
        wheelView2?.setOnScrollChangedListener(this)
        wheelView3?.setOnScrollChangedListener(this)
    }


    override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
        val wheelView1Id = wheelView1?.id ?: -1
        val wheelView2Id = wheelView2?.id ?: -1

        when (wheelView.id) {
            wheelView1Id -> {
                wheelView2?.let {
                    val secondData: List<Any> = requestData2Listener?.convert(getFirstWheelView())
                            ?: emptyList()
                    it.setData(secondData)
                }
                wheelView3?.let {
                    val thirdData: List<Any> = requestData3Listener?.convert(getFirstWheelView(),
                            getSecondWheelView()) ?: emptyList()
                    it.setData(thirdData)
                }
            }
            wheelView2Id -> {
                wheelView3?.let {
                    val thirdData: List<Any> = requestData3Listener?.convert(getFirstWheelView(),
                            getSecondWheelView()) ?: emptyList()
                    it.setData(thirdData)
                }
            }
            else -> {

            }
        }

        linkageSelectedListener?.onLinkageSelected(getFirstWheelView(), wheelView2, wheelView3)
    }

    override fun onScrollChanged(wheelView: WheelView, scrollOffsetY: Int) {
        scrollChangedListener?.onScrollChanged(wheelView, scrollOffsetY)
    }

    override fun onScrollStateChanged(wheelView: WheelView, state: Int) {
        scrollChangedListener?.onScrollStateChanged(wheelView, state)
    }

    override fun setTextFormatter(textFormatter: TextFormatter) {
        wheelView1?.setTextFormatter(textFormatter)
        wheelView2?.setTextFormatter(textFormatter)
        wheelView3?.setTextFormatter(textFormatter)
    }

    override fun setFirstTextFormatter(textFormatter: TextFormatter) {
        wheelView1?.setTextFormatter(textFormatter)
    }

    override fun setSecondTextFormatter(textFormatter: TextFormatter) {
        wheelView2?.setTextFormatter(textFormatter)
    }

    override fun setThirdTextFormatter(textFormatter: TextFormatter) {
        wheelView3?.setTextFormatter(textFormatter)
    }

    override fun setOnRequestData2Listener(listener: OnRequestData2Listener?) {
        this.requestData2Listener = listener

    }

    override fun setOnRequestData3Listener(listener: OnRequestData3Listener?) {
        this.requestData3Listener = listener
    }

    override fun setData(firstData: List<Any>, useSecond: Boolean, useThird: Boolean) {
        wheelView1?.setData(firstData)
        if (useSecond) {
            require(requestData2Listener != null) {
                "use second WheelView must be execute setOnRequestData2Listener() before setData()."
            }
            wheelView2?.let {
                val secondData: List<Any> = requestData2Listener?.convert(getFirstWheelView())
                        ?: emptyList()
                it.setData(secondData)
            }
        } else {
            wheelView2?.visibility = View.GONE
            return
        }
        if (useThird) {
            require(requestData2Listener != null) {
                "use third WheelView must be execute setOnRequestData3Listener() before setData()."
            }
            wheelView3?.let {
                val thirdData: List<Any> = requestData3Listener?.convert(getFirstWheelView(),
                        getSecondWheelView()) ?: emptyList()
                it.setData(thirdData)
            }
        } else {
            wheelView3?.visibility = View.GONE
        }
    }

    override fun setData(firstData: List<Any>, useSecond: Boolean) {
        setData(firstData, useSecond, false)
    }

    override fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        this.scrollChangedListener = listener
    }

    override fun setOnLinkageSelectedListener(listener: OnLinkageSelectedListener?) {
        this.linkageSelectedListener = listener
    }

    override fun getFirstWheelView(): WheelView {
        require(wheelView1 != null) {
            "First WheelView is null."
        }
        return wheelView1!!
    }

    override fun getSecondWheelView(): WheelView {
        require(wheelView2 != null) {
            "Second WheelView is null."
        }
        return wheelView2!!
    }

    override fun getThirdWheelView(): WheelView {
        require(wheelView3 != null) {
            "Third WheelView is null."
        }
        return wheelView3!!
    }

    override fun setVisibleItems(visibleItems: Int) {
        wheelView1?.visibleItems = visibleItems
        wheelView2?.visibleItems = visibleItems
        wheelView3?.visibleItems = visibleItems
    }

    override fun setLineSpacing(lineSpacing: Float) {
        wheelView1?.lineSpacing = lineSpacing
        wheelView2?.lineSpacing = lineSpacing
        wheelView3?.lineSpacing = lineSpacing
    }

    override fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        wheelView1?.setLineSpacing(lineSpacing, isDp)
        wheelView2?.setLineSpacing(lineSpacing, isDp)
        wheelView3?.setLineSpacing(lineSpacing, isDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        wheelView1?.isCyclic = isCyclic
        wheelView2?.isCyclic = isCyclic
        wheelView3?.isCyclic = isCyclic
    }

    override fun setTextSize(textSize: Float) {
        wheelView1?.textSize = textSize
        wheelView2?.textSize = textSize
        wheelView3?.textSize = textSize
    }

    override fun setTextSize(textSize: Float, isSp: Boolean) {
        wheelView1?.setTextSize(textSize, isSp)
        wheelView2?.setTextSize(textSize, isSp)
        wheelView3?.setTextSize(textSize, isSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        wheelView1?.isAutoFitTextSize = autoFit
        wheelView2?.isAutoFitTextSize = autoFit
        wheelView3?.isAutoFitTextSize = autoFit
    }

    override fun setMinTextSize(minTextSize: Float) {
        wheelView1?.minTextSize = minTextSize
        wheelView2?.minTextSize = minTextSize
        wheelView3?.minTextSize = minTextSize
    }

    override fun setMinTextSize(minTextSize: Float, isSp: Boolean) {
        wheelView1?.setMinTextSize(minTextSize, isSp)
        wheelView2?.setMinTextSize(minTextSize, isSp)
        wheelView3?.setMinTextSize(minTextSize, isSp)
    }

    override fun setTextAlign(textAlign: Int) {
        wheelView1?.textAlign = textAlign
        wheelView2?.textAlign = textAlign
        wheelView3?.textAlign = textAlign
    }

    override fun setNormalTextColor(@ColorInt textColor: Int) {
        wheelView1?.normalTextColor = textColor
        wheelView2?.normalTextColor = textColor
        wheelView3?.normalTextColor = textColor
    }

    override fun setNormalTextColorRes(@ColorRes textColorRes: Int) {
        wheelView1?.setNormalTextColorRes(textColorRes)
        wheelView2?.setNormalTextColorRes(textColorRes)
        wheelView3?.setNormalTextColorRes(textColorRes)
    }

    override fun setSelectedTextColor(@ColorInt textColor: Int) {
        wheelView1?.selectedTextColor = textColor
        wheelView2?.selectedTextColor = textColor
        wheelView3?.selectedTextColor = textColor
    }

    override fun setSelectedTextColorRes(@ColorRes textColorRes: Int) {
        wheelView1?.setSelectedTextColorRes(textColorRes)
        wheelView2?.setSelectedTextColorRes(textColorRes)
        wheelView3?.setSelectedTextColorRes(textColorRes)
    }

    override fun setTextMargins(margin: Float) {
        wheelView1?.textPaddingLeft = margin
        wheelView2?.textPaddingLeft = margin
        wheelView3?.textPaddingLeft = margin
        wheelView1?.textPaddingRight = margin
        wheelView2?.textPaddingRight = margin
        wheelView3?.textPaddingRight = margin
    }

    override fun setTextMargins(margin: Float, isDp: Boolean) {
        wheelView1?.setTextPadding(margin, isDp)
        wheelView2?.setTextPadding(margin, isDp)
        wheelView3?.setTextPadding(margin, isDp)
    }

    override fun setTextMarginLeft(marginLeft: Float) {
        wheelView1?.textPaddingLeft = marginLeft
        wheelView2?.textPaddingLeft = marginLeft
        wheelView3?.textPaddingLeft = marginLeft
    }

    override fun setTextMarginLeft(marginLeft: Float, isDp: Boolean) {
        wheelView1?.setTextPaddingLeft(marginLeft, isDp)
        wheelView2?.setTextPaddingLeft(marginLeft, isDp)
        wheelView3?.setTextPaddingLeft(marginLeft, isDp)
    }

    override fun setTextMarginRight(marginRight: Float) {
        wheelView1?.textPaddingRight = marginRight
        wheelView2?.textPaddingRight = marginRight
        wheelView3?.textPaddingRight = marginRight
    }

    override fun setTextMarginRight(marginRight: Float, isDp: Boolean) {
        wheelView1?.setTextPaddingRight(marginRight, isDp)
        wheelView2?.setTextPaddingRight(marginRight, isDp)
        wheelView3?.setTextPaddingRight(marginRight, isDp)
    }

    override fun setTypeface(typeface: Typeface) {
        wheelView1?.setTypeface(typeface)
        wheelView2?.setTypeface(typeface)
        wheelView3?.setTypeface(typeface)
    }

    override fun setTypeface(typeface: Typeface, isBoldForSelectedItem: Boolean) {
        wheelView1?.setTypeface(typeface, isBoldForSelectedItem)
        wheelView2?.setTypeface(typeface, isBoldForSelectedItem)
        wheelView3?.setTypeface(typeface, isBoldForSelectedItem)
    }

    override fun setShowDivider(showDivider: Boolean) {
        wheelView1?.isShowDivider = showDivider
        wheelView2?.isShowDivider = showDivider
        wheelView3?.isShowDivider = showDivider
    }

    override fun setDividerColor(@ColorInt dividerColor: Int) {
        wheelView1?.dividerColor = dividerColor
        wheelView2?.dividerColor = dividerColor
        wheelView3?.dividerColor = dividerColor
    }

    override fun setDividerColorRes(@ColorRes dividerColorRes: Int) {
        wheelView1?.setDividerColorRes(dividerColorRes)
        wheelView2?.setDividerColorRes(dividerColorRes)
        wheelView3?.setDividerColorRes(dividerColorRes)
    }

    override fun setDividerHeight(dividerHeight: Float) {
        wheelView1?.dividerHeight = dividerHeight
        wheelView2?.dividerHeight = dividerHeight
        wheelView3?.dividerHeight = dividerHeight
    }

    override fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        wheelView1?.setDividerHeight(dividerHeight, isDp)
        wheelView2?.setDividerHeight(dividerHeight, isDp)
        wheelView3?.setDividerHeight(dividerHeight, isDp)
    }

    override fun setDividerType(@WheelView.DividerType dividerType: Int) {
        wheelView1?.dividerType = dividerType
        wheelView2?.dividerType = dividerType
        wheelView3?.dividerType = dividerType
    }

    override fun setDividerPadding(padding: Float) {
        wheelView1?.dividerPadding = padding
        wheelView2?.dividerPadding = padding
        wheelView3?.dividerPadding = padding
    }

    override fun setDividerPadding(padding: Float, isDp: Boolean) {
        wheelView1?.setDividerPadding(padding, isDp)
        wheelView2?.setDividerPadding(padding, isDp)
        wheelView3?.setDividerPadding(padding, isDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        wheelView1?.dividerCap = cap
        wheelView2?.dividerCap = cap
        wheelView3?.dividerCap = cap
    }

    override fun setDividerOffsetY(offsetY: Float) {
        wheelView1?.dividerOffsetY = offsetY
        wheelView2?.dividerOffsetY = offsetY
        wheelView3?.dividerOffsetY = offsetY
    }

    override fun setDividerOffsetY(offsetY: Float, isDp: Boolean) {
        wheelView1?.setDividerOffsetY(offsetY, isDp)
        wheelView2?.setDividerOffsetY(offsetY, isDp)
        wheelView3?.setDividerOffsetY(offsetY, isDp)
    }

    override fun setShowCurtain(showCurtain: Boolean) {
        wheelView1?.isShowCurtain = showCurtain
        wheelView2?.isShowCurtain = showCurtain
        wheelView3?.isShowCurtain = showCurtain
    }

    override fun setCurtainColor(@ColorInt curtainColor: Int) {
        wheelView1?.curtainColor = curtainColor
        wheelView2?.curtainColor = curtainColor
        wheelView3?.curtainColor = curtainColor
    }

    override fun setCurtainColorRes(@ColorRes curtainColorRes: Int) {
        wheelView1?.setCurtainColorRes(curtainColorRes)
        wheelView2?.setCurtainColorRes(curtainColorRes)
        wheelView3?.setCurtainColorRes(curtainColorRes)
    }

    override fun setCurved(curved: Boolean) {
        wheelView1?.isCurved = curved
        wheelView2?.isCurved = curved
        wheelView3?.isCurved = curved
    }

    override fun setCurvedArcDirection(@WheelView.CurvedArcDirection direction: Int) {
        wheelView1?.curvedArcDirection = direction
        wheelView2?.curvedArcDirection = direction
        wheelView3?.curvedArcDirection = direction
    }

    override fun setCurvedArcDirectionFactor(factor: Float) {
        wheelView1?.curvedArcDirectionFactor = factor
        wheelView2?.curvedArcDirectionFactor = factor
        wheelView3?.curvedArcDirectionFactor = factor
    }

    override fun setRefractRatio(ratio: Float) {
        wheelView1?.refractRatio = ratio
        wheelView2?.refractRatio = ratio
        wheelView3?.refractRatio = ratio
    }

    override fun setSoundEffect(soundEffect: Boolean) {
        wheelView1?.isSoundEffect = soundEffect
        wheelView2?.isSoundEffect = soundEffect
        wheelView3?.isSoundEffect = soundEffect
    }

    override fun setSoundResource(@RawRes soundRes: Int) {
        wheelView1?.setSoundResource(soundRes)
        wheelView2?.setSoundResource(soundRes)
        wheelView3?.setSoundResource(soundRes)
    }

    override fun setSoundVolume(playVolume: Float) {
        wheelView1?.setSoundVolume(playVolume)
        wheelView2?.setSoundVolume(playVolume)
        wheelView3?.setSoundVolume(playVolume)
    }

    override fun setResetSelectedPosition(reset: Boolean) {
        wheelView1?.isResetSelectedPosition = reset
        wheelView2?.isResetSelectedPosition = reset
        wheelView3?.isResetSelectedPosition = reset
    }

    override fun setCanOverRangeScroll(canOverRange: Boolean) {
        wheelView1?.canOverRangeScroll = canOverRange
        wheelView2?.canOverRangeScroll = canOverRange
        wheelView3?.canOverRangeScroll = canOverRange
    }
}