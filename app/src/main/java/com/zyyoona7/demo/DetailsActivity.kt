package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityDetailsBinding
import com.zyyoona7.demo.dialogfragment.DatePickerDialogFragment

class DetailsActivity : BaseActivity<ActivityDetailsBinding>() {


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
            DatePickerDialogFragment.newInstance()
                    .show(supportFragmentManager,"DatePicker")
        }
    }
}
