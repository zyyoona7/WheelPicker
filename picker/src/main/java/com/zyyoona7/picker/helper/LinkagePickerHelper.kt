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

        setAutoFitTextSize(true)
    }


    override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
        val wheelView1Id = wheelView1?.id ?: -1
        val wheelView2Id = wheelView2?.id ?: -1

        when (wheelView.id) {
            wheelView1Id -> {
                wheelView2?.let {
                    val secondData: List<Any> = requestData2Listener?.convert(getLinkage1WheelView())
                            ?: emptyList()
                    it.setData(secondData)
                }
                wheelView3?.let {
                    val thirdData: List<Any> = requestData3Listener?.convert(getLinkage1WheelView(),
                            getLinkage2WheelView()) ?: emptyList()
                    it.setData(thirdData)
                }
            }
            wheelView2Id -> {
                wheelView3?.let {
                    val thirdData: List<Any> = requestData3Listener?.convert(getLinkage1WheelView(),
                            getLinkage2WheelView()) ?: emptyList()
                    it.setData(thirdData)
                }
            }
            else -> {

            }
        }

        linkageSelectedListener?.onLinkageSelected(getLinkage1WheelView(), wheelView2, wheelView3)
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

    override fun setLinkage1TextFormatter(textFormatter: TextFormatter) {
        wheelView1?.setTextFormatter(textFormatter)
    }

    override fun setLinkage2TextFormatter(textFormatter: TextFormatter) {
        wheelView2?.setTextFormatter(textFormatter)
    }

    override fun setLinkage3TextFormatter(textFormatter: TextFormatter) {
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
                val secondData: List<Any> = requestData2Listener?.convert(getLinkage1WheelView())
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
                val thirdData: List<Any> = requestData3Listener?.convert(getLinkage1WheelView(),
                        getLinkage2WheelView()) ?: emptyList()
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

    override fun setMaxTextWidthMeasureType(measureType: WheelView.MeasureType) {
        setMaxTextWidthMeasureType(measureType, measureType, measureType)
    }

    override fun setMaxTextWidthMeasureType(linkage1Type: WheelView.MeasureType,
                                            linkage2Type: WheelView.MeasureType,
                                            linkage3Type: WheelView.MeasureType) {
        wheelView1?.maxTextWidthMeasureType = linkage1Type
        wheelView2?.maxTextWidthMeasureType = linkage2Type
        wheelView3?.maxTextWidthMeasureType = linkage3Type
    }

    override fun getLinkage1WheelView(): WheelView {
        require(wheelView1 != null) {
            "First WheelView is null."
        }
        return wheelView1!!
    }

    override fun getLinkage2WheelView(): WheelView {
        require(wheelView2 != null) {
            "Second WheelView is null."
        }
        return wheelView2!!
    }

    override fun getLinkage3WheelView(): WheelView {
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

    override fun setLineSpacing(lineSpacingPx: Int) {
        wheelView1?.lineSpacing = lineSpacingPx
        wheelView2?.lineSpacing = lineSpacingPx
        wheelView3?.lineSpacing = lineSpacingPx
    }

    override fun setLineSpacing(lineSpacingDp: Float) {
        wheelView1?.setLineSpacing(lineSpacingDp)
        wheelView2?.setLineSpacing(lineSpacingDp)
        wheelView3?.setLineSpacing(lineSpacingDp)
    }

    override fun setCyclic(isCyclic: Boolean) {
        wheelView1?.isCyclic = isCyclic
        wheelView2?.isCyclic = isCyclic
        wheelView3?.isCyclic = isCyclic
    }

    override fun setTextSize(textSizePx: Int) {
        wheelView1?.textSize = textSizePx
        wheelView2?.textSize = textSizePx
        wheelView3?.textSize = textSizePx
    }

    override fun setTextSize(textSizeSp: Float) {
        wheelView1?.setTextSize(textSizeSp)
        wheelView2?.setTextSize(textSizeSp)
        wheelView3?.setTextSize(textSizeSp)
    }

    override fun setAutoFitTextSize(autoFit: Boolean) {
        wheelView1?.isAutoFitTextSize = autoFit
        wheelView2?.isAutoFitTextSize = autoFit
        wheelView3?.isAutoFitTextSize = autoFit
    }

    override fun setMinTextSize(minTextSizePx: Int) {
        wheelView1?.minTextSize = minTextSizePx
        wheelView2?.minTextSize = minTextSizePx
        wheelView3?.minTextSize = minTextSizePx
    }

    override fun setMinTextSize(minTextSizeSp: Float) {
        wheelView1?.setMinTextSize(minTextSizeSp)
        wheelView2?.setMinTextSize(minTextSizeSp)
        wheelView3?.setMinTextSize(minTextSizeSp)
    }

    override fun setTextAlign(textAlign: Paint.Align) {
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

    override fun setTextPadding(paddingPx: Int) {
        wheelView1?.textPaddingLeft = paddingPx
        wheelView2?.textPaddingLeft = paddingPx
        wheelView3?.textPaddingLeft = paddingPx
        wheelView1?.textPaddingRight = paddingPx
        wheelView2?.textPaddingRight = paddingPx
        wheelView3?.textPaddingRight = paddingPx
    }

    override fun setTextPadding(paddingDp: Float) {
        wheelView1?.setTextPadding(paddingDp)
        wheelView2?.setTextPadding(paddingDp)
        wheelView3?.setTextPadding(paddingDp)
    }

    override fun setTextPaddingLeft(textPaddingLeftPx: Int) {
        wheelView1?.textPaddingLeft = textPaddingLeftPx
        wheelView2?.textPaddingLeft = textPaddingLeftPx
        wheelView3?.textPaddingLeft = textPaddingLeftPx
    }

    override fun setTextPaddingLeft(textPaddingLeftDp: Float) {
        wheelView1?.setTextPaddingLeft(textPaddingLeftDp)
        wheelView2?.setTextPaddingLeft(textPaddingLeftDp)
        wheelView3?.setTextPaddingLeft(textPaddingLeftDp)
    }

    override fun setTextPaddingRight(textPaddingRightPx: Int) {
        wheelView1?.textPaddingRight = textPaddingRightPx
        wheelView2?.textPaddingRight = textPaddingRightPx
        wheelView3?.textPaddingRight = textPaddingRightPx
    }

    override fun setTextPaddingRight(textPaddingRightDp: Float) {
        wheelView1?.setTextPaddingRight(textPaddingRightDp)
        wheelView2?.setTextPaddingRight(textPaddingRightDp)
        wheelView3?.setTextPaddingRight(textPaddingRightDp)
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

    override fun setDividerHeight(dividerHeightPx: Int) {
        wheelView1?.dividerHeight = dividerHeightPx
        wheelView2?.dividerHeight = dividerHeightPx
        wheelView3?.dividerHeight = dividerHeightPx
    }

    override fun setDividerHeight(dividerHeightDp: Float) {
        wheelView1?.setDividerHeight(dividerHeightDp)
        wheelView2?.setDividerHeight(dividerHeightDp)
        wheelView3?.setDividerHeight(dividerHeightDp)
    }

    override fun setDividerType(dividerType: WheelView.DividerType) {
        wheelView1?.dividerType = dividerType
        wheelView2?.dividerType = dividerType
        wheelView3?.dividerType = dividerType
    }

    override fun setWheelDividerPadding(paddingPx: Int) {
        wheelView1?.dividerPadding = paddingPx
        wheelView2?.dividerPadding = paddingPx
        wheelView3?.dividerPadding = paddingPx
    }

    override fun setWheelDividerPadding(paddingDp: Float) {
        wheelView1?.setDividerPadding(paddingDp)
        wheelView2?.setDividerPadding(paddingDp)
        wheelView3?.setDividerPadding(paddingDp)
    }

    override fun setDividerCap(cap: Paint.Cap) {
        wheelView1?.dividerCap = cap
        wheelView2?.dividerCap = cap
        wheelView3?.dividerCap = cap
    }

    override fun setDividerOffsetY(offsetYPx: Int) {
        wheelView1?.dividerOffsetY = offsetYPx
        wheelView2?.dividerOffsetY = offsetYPx
        wheelView3?.dividerOffsetY = offsetYPx
    }

    override fun setDividerOffsetY(offsetYDp: Float) {
        wheelView1?.setDividerOffsetY(offsetYDp)
        wheelView2?.setDividerOffsetY(offsetYDp)
        wheelView3?.setDividerOffsetY(offsetYDp)
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

    override fun setCurvedArcDirection(direction: WheelView.CurvedArcDirection) {
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

    override fun setLeftText(text: CharSequence) {
        setLeftText(text, text, text)
    }

    override fun setLeftText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence) {
        wheelView1?.leftText = linkage1Text
        wheelView2?.leftText = linkage2Text
        wheelView3?.leftText = linkage3Text
    }

    override fun setRightText(text: CharSequence) {
        setRightText(text, text, text)
    }

    override fun setRightText(linkage1Text: CharSequence, linkage2Text: CharSequence, linkage3Text: CharSequence) {
        wheelView1?.rightText = linkage1Text
        wheelView2?.rightText = linkage2Text
        wheelView3?.rightText = linkage3Text
    }

    override fun setLeftTextSize(textSizePx: Int) {
        wheelView1?.leftTextSize = textSizePx
        wheelView2?.leftTextSize = textSizePx
        wheelView3?.leftTextSize = textSizePx
    }

    override fun setLeftTextSize(textSizeSp: Float) {
        wheelView1?.setLeftTextSize(textSizeSp)
        wheelView2?.setLeftTextSize(textSizeSp)
        wheelView3?.setLeftTextSize(textSizeSp)
    }

    override fun setRightTextSize(textSizePx: Int) {
        wheelView1?.rightTextSize = textSizePx
        wheelView2?.rightTextSize = textSizePx
        wheelView3?.rightTextSize = textSizePx
    }

    override fun setRightTextSize(textSizeSp: Float) {
        wheelView1?.setRightTextSize(textSizeSp)
        wheelView2?.setRightTextSize(textSizeSp)
        wheelView3?.setRightTextSize(textSizeSp)
    }

    override fun setLeftTextColor(@ColorInt color: Int) {
        wheelView1?.leftTextColor = color
        wheelView2?.leftTextColor = color
        wheelView3?.leftTextColor = color
    }

    override fun setLeftTextColorRes(@ColorRes colorRes: Int) {
        wheelView1?.setLeftTextColorRes(colorRes)
        wheelView2?.setLeftTextColorRes(colorRes)
        wheelView3?.setLeftTextColorRes(colorRes)
    }

    override fun setRightTextColor(@ColorInt color: Int) {
        wheelView1?.rightTextColor = color
        wheelView2?.rightTextColor = color
        wheelView3?.rightTextColor = color
    }

    override fun setRightTextColorRes(@ColorRes colorRes: Int) {
        wheelView1?.setRightTextColorRes(colorRes)
        wheelView2?.setRightTextColorRes(colorRes)
        wheelView3?.setRightTextColorRes(colorRes)
    }

    override fun setLeftTextMarginRight(marginRightPx: Int) {
        wheelView1?.leftTextMarginRight = marginRightPx
        wheelView2?.leftTextMarginRight = marginRightPx
        wheelView3?.leftTextMarginRight = marginRightPx
    }

    override fun setLeftTextMarginRight(marginRightDp: Float) {
        wheelView1?.setLeftTextMarginRight(marginRightDp)
        wheelView2?.setLeftTextMarginRight(marginRightDp)
        wheelView3?.setLeftTextMarginRight(marginRightDp)
    }

    override fun setRightTextMarginLeft(marginLeftPx: Int) {
        wheelView1?.rightTextMarginLeft = marginLeftPx
        wheelView2?.rightTextMarginLeft = marginLeftPx
        wheelView3?.rightTextMarginLeft = marginLeftPx
    }

    override fun setRightTextMarginLeft(marginLeftDp: Float) {
        wheelView1?.setRightTextMarginLeft(marginLeftDp)
        wheelView2?.setRightTextMarginLeft(marginLeftDp)
        wheelView3?.setRightTextMarginLeft(marginLeftDp)
    }

    override fun setLeftTextGravity(gravity: Int) {
        wheelView1?.leftTextGravity = gravity
        wheelView2?.leftTextGravity = gravity
        wheelView3?.leftTextGravity = gravity
    }

    override fun setRightTextGravity(gravity: Int) {
        wheelView1?.rightTextGravity = gravity
        wheelView2?.rightTextGravity = gravity
        wheelView3?.rightTextGravity = gravity
    }
}