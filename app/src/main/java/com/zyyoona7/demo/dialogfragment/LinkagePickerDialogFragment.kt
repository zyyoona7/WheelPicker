package com.zyyoona7.demo.dialogfragment

import android.os.Bundle
import android.util.Log
import com.zyyoona7.demo.CityFormatter
import com.zyyoona7.demo.DefaultTripleLoadDataListener
import com.zyyoona7.demo.R
import com.zyyoona7.demo.databinding.DfLinkagePickerBinding
import com.zyyoona7.demo.entities.City
import com.zyyoona7.demo.utils.ParseHelper
import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.wheel.WheelView

class LinkagePickerDialogFragment : BaseDialogFragment<DfLinkagePickerBinding>() {

    private var p:String=""
    private var c:String=""
    private var a:String=""

    companion object{

        private const val KEY_P="KEY_P"
        private const val KEY_C="KEY_C"
        private const val KEY_A="KEY_A"

        fun newInstance(p:String,c:String,a:String):LinkagePickerDialogFragment{
            return LinkagePickerDialogFragment()
                    .apply {
                        val bundle=Bundle()
                        bundle.putString(KEY_P,p)
                        bundle.putString(KEY_C,c)
                        bundle.putString(KEY_A,a)
                        arguments=bundle
                    }
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.df_linkage_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {

        arguments?.let {
            p=it.getString(KEY_P,"")
            c=it.getString(KEY_C,"")
            a=it.getString(KEY_A,"")
        }

        binding.linkagePicker.setTextFormatter(CityFormatter())

        val threeLevelList= ParseHelper.parseThreeLevelCityList(activity)
        binding.linkagePicker.setData(threeLevelList, DefaultTripleLoadDataListener())

        binding.linkagePicker.setSelectedItem(p,c,a,true)
    }

    override fun initListeners(savedInstanceState: Bundle?) {

        binding.linkagePicker.setOnLinkageSelectedListener(object :OnLinkageSelectedListener{
            override fun onLinkageSelected(firstWv: WheelView, secondWv: WheelView?, thirdWv: WheelView?) {
                val p=firstWv.getSelectedItem<City>()?.name?:""
                val c=secondWv?.getSelectedItem<City>()?.name?:""
                val a=secondWv?.getSelectedItem<City>()?.name?:""
                Log.d("LinkagePickerDF","选择区域：$p,$c,$a")
            }
        })

        binding.tvConfirm.setOnClickListener {
            getDialogListener(OnLinkageSelectedListener::class.java)
                    ?.onLinkageSelected(binding.linkagePicker.getLinkage1WheelView(),
                            binding.linkagePicker.getLinkage2WheelView(),
                            binding.linkagePicker.getLinkage3WheelView())
            dismissAllowingStateLoss()
        }

        binding.tvCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}