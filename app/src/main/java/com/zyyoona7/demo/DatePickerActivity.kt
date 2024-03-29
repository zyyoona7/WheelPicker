package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityDatePickerBinding
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener
import java.util.*

class DatePickerActivity : BaseActivity<ActivityDatePickerBinding>() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DatePickerActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_date_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        binding.wheelYear.setTextFormatter(IntTextFormatter("公元%d年"))
        binding.wheelMonth.setTextFormatter(IntTextFormatter("%d月"))
        binding.wheelDay.setTextFormatter(IntTextFormatter("%d日"))

        binding.datePicker1.setRightText("年","月","日")
        binding.datePicker2.setRightTextMarginLeft(10f)

        binding.datePicker5.setMaxSelectedDate(Calendar.getInstance(),WheelView.OverRangeMode.HIDE_ITEM)
    }

    override fun initListeners(savedInstanceState: Bundle?) {
        binding.wheelYear.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
                binding.wheelDay.year = adapter.getSelectedItem<Int>() ?: 2019
            }
        })
        binding.wheelMonth.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(wheelView: WheelView, adapter: ArrayWheelAdapter<*>, position: Int) {
                binding.wheelDay.month = adapter.getSelectedItem<Int>() ?: 1
            }
        })
    }

}
