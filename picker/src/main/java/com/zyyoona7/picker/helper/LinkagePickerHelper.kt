package com.zyyoona7.picker.helper

import android.view.View
import com.zyyoona7.picker.interfaces.LinkagePicker
import com.zyyoona7.picker.listener.OnRequestDataListener
import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.TextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import com.zyyoona7.wheel.listener.OnScrollChangedListener

class LinkagePickerHelper(private var wheelView1: WheelViewKt?,
                          private var wheelView2: WheelViewKt?,
                          private var wheelView3: WheelViewKt?)
    : OnItemSelectedListener, OnScrollChangedListener,LinkagePicker {

    private var requestDataListener: OnRequestDataListener? = null

    init {
        wheelView1?.setOnItemSelectedListener(this)
        wheelView2?.setOnItemSelectedListener(this)
        wheelView3?.setOnItemSelectedListener(this)
    }


    override fun onItemSelected(wheelView: WheelViewKt, adapter: ArrayWheelAdapter<*>, position: Int) {
        val wheelView1Id = wheelView1?.id ?: -1
        val wheelView2Id = wheelView2?.id ?: -1
        val wheelView3Id = wheelView3?.id ?: -1

        when (wheelView.id) {
            wheelView1Id -> {
                wheelView2?.let {
                    val secondData: List<Any> = requestDataListener?.convert(wheelView1!!, position)
                            ?: emptyList()
                    val thirdData: List<Any> = requestDataListener?.convert(wheelView1!!,
                            position, wheelView2!!, it.getSelectedPosition()) ?: arrayListOf()
                    it.setData(secondData)
                    wheelView3?.setData(thirdData)
                }
            }
            wheelView2Id -> {
                wheelView3?.let {
                    val thirdData: List<Any> = requestDataListener?.convert(wheelView1!!,
                            wheelView1!!.getSelectedPosition(), wheelView2!!, position) ?: arrayListOf()
                    it.setData(thirdData)
                }
            }
            else -> {

            }
        }
    }

    override fun onScrollChanged(wheelView: WheelViewKt, scrollOffsetY: Int) {
    }

    override fun onScrollStateChanged(wheelView: WheelViewKt, state: Int) {
    }

    override fun setFirstData(firstData: List<Any>) {
        wheelView1?.setData(firstData)
        wheelView2?.let {
            if (it.visibility==View.VISIBLE) {
                val secondData: List<Any> = requestDataListener?.convert(wheelView1!!,
                        wheelView1?.getSelectedPosition()?:0)
                        ?: arrayListOf()
                it.setData(secondData)
            }
        }

        wheelView3?.let {
            if (it.visibility==View.VISIBLE) {
                val thirdData: List<Any> = requestDataListener?.convert(wheelView1!!,
                        wheelView1?.getSelectedPosition()?:-1,
                        wheelView2!!,
                        it.getSelectedPosition()) ?: arrayListOf()
                it.setData(thirdData)
            }
        }
    }

    fun setOnRequestDataListener(listener: OnRequestDataListener) {
        this.requestDataListener=listener
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
}