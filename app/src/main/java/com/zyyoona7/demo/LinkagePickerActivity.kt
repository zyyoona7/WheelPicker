package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityLinkagePickerBinding
import com.zyyoona7.demo.entities.City
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
        binding.linkagePicker1.setTextFormatter(CityFormatter())
        binding.linkagePicker2.setTextFormatter(CityFormatter())
        binding.linkagePicker3.setTextFormatter(CityFormatter())
        binding.linkagePicker4.setTextFormatter(CityFormatter())
        binding.linkagePicker5.setTextFormatter(CityFormatter())
        binding.linkagePicker6.setTextFormatter(CityFormatter())

        val cityList = ParseHelper.parseTwoLevelCityList(this)
        val compatCityList= arrayListOf<City>()
        val compatTwoLevelList= arrayListOf<List<City>>()
        ParseHelper.initTwoLevelCityList(this,compatCityList,compatTwoLevelList)

        //两级联动
        binding.linkagePicker1.setData(cityList,DefaultDoubleLoadDataListener())
        binding.linkagePicker2.setData(cityList,DefaultDoubleLoadDataListener())
        binding.linkagePicker3.setData(cityList,DefaultDoubleLoadDataListener())
        //两级联动,兼容1.x的数据类型
        binding.linkagePicker4.setData(compatCityList,DoubleLoadDataListenerCompat(compatTwoLevelList))

        val threeLevelList=ParseHelper.parseThreeLevelCityList(this)

        val compatThreeLevelList= arrayListOf<City>()
        val compatThreeLevelList1= arrayListOf<List<City>>()
        val compatThreeLevelList2= arrayListOf<List<List<City>>>()
        ParseHelper.initThreeLevelCityList(this,compatThreeLevelList,
                compatThreeLevelList1,compatThreeLevelList2)

        //三级联动
        binding.linkagePicker5.setData(threeLevelList,DefaultTripleLoadDataListener())
        //三级联动,兼容1.x的数据类型
        binding.linkagePicker6.setData(compatCityList,
                TripleLoadDataListenerCompat(compatThreeLevelList1,compatThreeLevelList2))


        //设置默认选中项
        binding.linkagePicker2.setSelectedItem("安徽省","马鞍山市",true)
        binding.linkagePicker5.setSelectedItem("内蒙古自治区",
                "包头市","九原区",true)
    }
}

class CityFormatter : SimpleTextFormatter<City>() {
    override fun text(item: City): Any {
        return item.name
    }
}

class DefaultDoubleLoadDataListener : OnDoubleLoadDataListener {
    override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
        return linkage1Wv.getSelectedItem<City>()?.districts ?: emptyList()
    }
}

class DoubleLoadDataListenerCompat(private val twoLevelList: List<List<City>>) : OnDoubleLoadDataListener {
    override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
        return twoLevelList[linkage1Wv.getSelectedPosition()]
    }
}

class DefaultTripleLoadDataListener : OnTripleLoadDataListener {
    override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
        return linkage1Wv.getSelectedItem<City>()?.districts ?: emptyList()
    }

    override fun onLoadData3(linkage1Wv: WheelView, linkage2Wv: WheelView): List<Any> {
        return linkage2Wv.getSelectedItem<City>()?.districts ?: emptyList()
    }

}

class TripleLoadDataListenerCompat(private val twoLevelList: List<List<City>>,
                                   private val threeLevelList: List<List<List<City>>>) : OnTripleLoadDataListener {
    override fun onLoadData2(linkage1Wv: WheelView): List<Any> {
        return twoLevelList[linkage1Wv.getSelectedPosition()]
    }

    override fun onLoadData3(linkage1Wv: WheelView, linkage2Wv: WheelView): List<Any> {
        return threeLevelList[linkage1Wv.getSelectedPosition()][linkage2Wv.getSelectedPosition()]
    }

}
