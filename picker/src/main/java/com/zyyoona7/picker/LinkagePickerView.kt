package com.zyyoona7.picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.zyyoona7.picker.helper.LinkagePickerHelper
import com.zyyoona7.picker.interfaces.LinkagePicker
import com.zyyoona7.picker.listener.OnRequestDataListener
import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.formatter.TextFormatter

/**
 * 联动选择器 View
 *
 * @author zyyoona7
 */
class LinkagePickerView @JvmOverloads constructor(context: Context,
                                                  attrs: AttributeSet? = null,
                                                  defStyleAttr: Int = 0)
    :LinearLayout(context, attrs, defStyleAttr),LinkagePicker {

    private val linkagePicker: LinkagePickerHelper

    init {
        View.inflate(context,R.layout.layout_linkage_picker_view,this)
        val wheelView1=findViewById<WheelViewKt>(R.id.wheel_view_1)
        val wheelView2=findViewById<WheelViewKt>(R.id.wheel_view_2)
        val wheelView3=findViewById<WheelViewKt>(R.id.wheel_view_3)
        linkagePicker= LinkagePickerHelper(wheelView1, wheelView2, wheelView3)
    }

    override fun setFirstData(firstData: List<Any>) {
        linkagePicker.setFirstData(firstData)
    }

    fun setOnRequestDataListener(listener: OnRequestDataListener) {
        linkagePicker.setOnRequestDataListener(listener)
    }

    override fun setFirstTextFormatter(textFormatter: TextFormatter) {
        linkagePicker.setFirstTextFormatter(textFormatter)
    }

    override fun setSecondTextFormatter(textFormatter: TextFormatter) {
        linkagePicker.setSecondTextFormatter(textFormatter)
    }

    override fun setThirdTextFormatter(textFormatter: TextFormatter) {
        linkagePicker.setThirdTextFormatter(textFormatter)
    }
}