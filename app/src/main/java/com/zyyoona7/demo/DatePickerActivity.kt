package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityDatePickerBinding
import com.zyyoona7.wheel.WheelViewKt
import com.zyyoona7.wheel.adapter.ArrayWheelAdapter
import com.zyyoona7.wheel.formatter.IntTextFormatter
import com.zyyoona7.wheel.listener.OnItemSelectedListener

class DatePickerActivity : BaseActivity<ActivityDatePickerBinding>() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DatePickerActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_date_picker
    }

    var dateStr="哈喽哈喽哈喽halo"

    override fun initVariables(savedInstanceState: Bundle?) {
        binding.wheelYear.setTextFormatter(IntTextFormatter("公元%d年"))
        binding.wheelMonth.setTextFormatter(IntTextFormatter("%d月"))
        binding.wheelDay.setTextFormatter(IntTextFormatter("%d日"))

        binding.wheelview.setData(arrayListOf(dateStr))
    }

    override fun initListeners(savedInstanceState: Bundle?) {
        binding.wheelYear.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(wheelView: WheelViewKt, adapter: ArrayWheelAdapter<*>, position: Int) {
                binding.wheelDay.year = adapter.getSelectedItem<Int>() ?: 2019

                dateStr+="哈喽"
                binding.wheelview.setData(arrayListOf(dateStr))
            }
        })
        binding.wheelMonth.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(wheelView: WheelViewKt, adapter: ArrayWheelAdapter<*>, position: Int) {
                binding.wheelDay.month = adapter.getSelectedItem<Int>() ?: 1
            }
        })
    }

}
