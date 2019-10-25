package com.zyyoona7.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.zyyoona7.demo.activity.BaseActivity
import com.zyyoona7.demo.databinding.ActivityTimePickerBinding

class TimePickerActivity : BaseActivity<ActivityTimePickerBinding>() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, TimePickerActivity::class.java))
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.activity_time_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {

    }

    override fun initListeners(savedInstanceState: Bundle?) {
    }
}
