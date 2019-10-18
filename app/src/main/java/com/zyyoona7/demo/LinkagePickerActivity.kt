package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityLinkagePickerBinding
import com.zyyoona7.demo.entities.CityEntity
import com.zyyoona7.demo.utils.ParseHelper
import com.zyyoona7.picker.listener.OnRequestData2Listener
import com.zyyoona7.picker.listener.OnRequestData3Listener
import com.zyyoona7.wheel.WheelView
import com.zyyoona7.wheel.formatter.SimpleTextFormatter

class LinkagePickerActivity : BaseActivity<ActivityLinkagePickerBinding>() {


    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, LinkagePickerActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_linkage_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        initLinkagePicker()
    }

    override fun initListeners(savedInstanceState: Bundle?) {

    }

    private fun initLinkagePicker() {
        binding.linkagePicker.setTextFormatter(object : SimpleTextFormatter<CityEntity>() {
            override fun text(item: CityEntity): Any {
                return item.name
            }
        })
        binding.linkagePicker.setOnRequestData2Listener(object : OnRequestData2Listener {
            override fun convert(firstWv: WheelView): List<Any> {
                return firstWv.getSelectedItem<CityEntity>()?.districts ?: emptyList()
            }
        })

        binding.linkagePicker.setOnRequestData3Listener(object : OnRequestData3Listener {
            override fun convert(firstWv: WheelView, secondWv: WheelView): List<Any> {
                return secondWv.getSelectedItem<CityEntity>()?.districts ?: emptyList()
            }
        })
        val cityList = ParseHelper.parseThreeLevelCityList(this)
        binding.linkagePicker.setData(cityList, useSecond = true, useThird = true)
    }
}
