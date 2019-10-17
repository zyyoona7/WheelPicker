package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityLinkagePickerBinding
import com.zyyoona7.demo.entities.CityEntity
import com.zyyoona7.demo.utils.ParseHelper
import com.zyyoona7.picker.listener.OnRequestDataListener
import com.zyyoona7.wheel.WheelViewKt
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
        val cityList = ParseHelper.parseThreeLevelCityList(this)
        binding.linkagePicker.setFirstTextFormatter(object :SimpleTextFormatter<CityEntity>(){
            override fun text(item: CityEntity): Any {
                return item.name
            }
        })
        binding.linkagePicker.setSecondTextFormatter(object :SimpleTextFormatter<CityEntity>(){
            override fun text(item: CityEntity): Any {
                return item.name
            }
        })
        binding.linkagePicker.setThirdTextFormatter(object :SimpleTextFormatter<CityEntity>(){
            override fun text(item: CityEntity): Any {
                return item.name
            }
        })
        binding.linkagePicker.setFirstData(cityList)
    }

    override fun initListeners(savedInstanceState: Bundle?) {
        binding.linkagePicker.setOnRequestDataListener(object : OnRequestDataListener {

            override fun convert(wheelView1: WheelViewKt, firstPosition: Int): List<Any> {
                val firstData: List<CityEntity> = wheelView1.getAdapter()?.getDataList()
                        ?: emptyList()
                return firstData[firstPosition].districts
            }

            override fun convert(wheelView1: WheelViewKt, firstPosition: Int,
                                 wheelView2: WheelViewKt, secondPosition: Int): List<Any> {
                val firstData: List<CityEntity> = wheelView1.getAdapter()?.getDataList()
                        ?: emptyList()
                return firstData[firstPosition].districts[secondPosition].districts
            }

        })
    }
}
