package com.zyyoona7.picker.ex

import android.content.Context
import android.util.AttributeSet
import com.zyyoona7.picker.R
import com.zyyoona7.picker.interfaces.AmPmTextHandler
import com.zyyoona7.wheel.WheelView

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