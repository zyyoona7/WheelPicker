package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityLinkagePickerBinding
import com.zyyoona7.demo.entities.CityEntity
import com.zyyoona7.demo.utils.ParseHelper
import com.zyyoona7.picker.listener.OnDoubleLoadDataListener
import com.zyyoona7.picker.listener.OnTripleLoadDataListener
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

        val cityList = ParseHelper.parseThreeLevelCityList(this)

        //两级联动
//        binding.linkagePicker.setData(cityList, object : OnDoubleLoadDataListener {
//            override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
//                return linkage1Wv.getSelectedItem<CityEntity>()?.districts ?: emptyList()
//            }
//
//        })

        //三级联动
        binding.linkagePicker.setData(cityList,object : OnTripleLoadDataListener {
            override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
                return linkage1Wv.getSelectedItem<CityEntity>()?.districts ?: emptyList()
            }

            override fun onLoadData3(linkage1Wv: WheelView, linkage2Wv: WheelView): List<Any> {
                return linkage2Wv.getSelectedItem<CityEntity>()?.districts ?: emptyList()
            }
        })

        binding.linkagePicker.setSelectedItem("河北省", "邯郸市",
                "北京市", true)
    }
}
