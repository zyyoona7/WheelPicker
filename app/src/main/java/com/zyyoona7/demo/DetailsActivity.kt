package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityDetailsBinding
import com.zyyoona7.demo.dialogfragment.DatePickerDialogFragment
import com.zyyoona7.demo.dialogfragment.LinkagePickerDialogFragment
import com.zyyoona7.demo.dialogfragment.TimePickerDialogFragment
import com.zyyoona7.demo.entities.City
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.picker.listener.OnLinkageSelectedListener
import com.zyyoona7.picker.listener.OnTimeSelectedListener
import com.zyyoona7.wheel.WheelView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : BaseActivity<ActivityDetailsBinding>(),OnDateSelectedListener,
OnLinkageSelectedListener,OnTimeSelectedListener{

    private var currentYear=-1
    private var currentMonth=-1
    private var currentDay=-1


    private var p:String=""
    private var c:String=""
    private var a:String=""

    private var is24Hour:Boolean=false
    private var isAm:Boolean=false
    private var hour:Int=-1
    private var minute:Int=-1
    private var second:Int=-1

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DetailsActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_details
    }

    override fun initVariables(savedInstanceState: Bundle?) {
    }

    override fun initListeners(savedInstanceState: Bundle?) {
        binding.btnTimePicker.setOnClickListener {
            TimePickerActivity.start(this)
        }

        binding.btnDatePicker.setOnClickListener {
            DatePickerActivity.start(this)
        }

        binding.btnLinkagePicker.setOnClickListener {
            LinkagePickerActivity.start(this)
        }

        binding.btnDatePickerDf.setOnClickListener {
            DatePickerDialogFragment.newInstance(currentYear,currentMonth,currentDay)
                    .show(supportFragmentManager,"DatePicker")
        }

        binding.btnLinkagePickerDf.setOnClickListener {
            LinkagePickerDialogFragment.newInstance(p, c, a)
                    .show(supportFragmentManager,"LinkagePicker")
        }

        binding.btnTimePickerDf.setOnClickListener {
            TimePickerDialogFragment.newInstance(hour, minute, second)
                    .show(supportFragmentManager,"TimePicker")
        }
    }

    override fun onDateSelected(year: Int, month: Int, day: Int, date: Date) {
        currentYear=year
        currentMonth=month
        currentDay=day
        binding.btnDatePickerDf.text = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(date)
    }

    override fun onTimeSelected(is24Hour: Boolean, hour: Int, minute: Int, second: Int, isAm: Boolean) {
        this.is24Hour=is24Hour
        this.isAm=isAm
        this.hour=hour
        this.minute=minute
        this.second=second
        val amPm = if (!is24Hour) {
            if (isAm) "上午" else "下午"
        }else{
            ""
        }
        val time=String.format(if (is24Hour) "%02d:%02d:%02d" else "%d:%02d:%02d",hour,minute,second)
        binding.btnTimePickerDf.text="$amPm $time"
    }

    override fun onLinkageSelected(firstWv: WheelView, secondWv: WheelView?, thirdWv: WheelView?) {
        p=firstWv.getSelectedItem<City>()?.name?:""
        c=secondWv?.getSelectedItem<City>()?.name?:""
        a=thirdWv?.getSelectedItem<City>()?.name?:""

        binding.btnLinkagePickerDf.text="$p,$c,$a"
    }
}
