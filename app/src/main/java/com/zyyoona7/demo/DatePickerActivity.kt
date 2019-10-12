package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityDatePickerBinding
import com.zyyoona7.wheel.formatter.IntTextFormatter

class DatePickerActivity : BaseActivity<ActivityDatePickerBinding>() {

    companion object{

        fun start(context: Context){
            context.startActivity(Intent(context,DatePickerActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_date_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        binding.wheelYear.setTextFormatter(IntTextFormatter("公元%d年"))
    }

    override fun initListeners(savedInstanceState: Bundle?) {
    }

}
