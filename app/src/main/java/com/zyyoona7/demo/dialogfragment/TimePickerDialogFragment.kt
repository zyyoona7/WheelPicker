package com.zyyoona7.demo.dialogfragment

import android.os.Bundle
import android.util.Log
import com.zyyoona7.demo.R
import com.zyyoona7.demo.databinding.DfTimePickerBinding
import com.zyyoona7.picker.helper.TimePickerHelper
import com.zyyoona7.picker.listener.OnTimeSelectedListener
import java.util.*

class TimePickerDialogFragment : BaseDialogFragment<DfTimePickerBinding>() {

    private var hour: Int = -1
    private var minute: Int = -1
    private var second: Int = -1
    private var is24Hour = true
    private var isAm = true

    companion object {

        private const val KEY_IS_24HOUR = "KEY_IS_24HOUR"
        private const val KEY_HOUR = "KEY_HOUR"
        private const val KEY_MINUTE = "KEY_MINUTE"
        private const val KEY_SECOND = "KEY_SECOND"
        private const val KEY_IS_AM = "KEY_IS_AM"

        fun newInstance(hour: Int, minute: Int, second: Int, is24Hour: Boolean, isAm: Boolean): TimePickerDialogFragment {
            return TimePickerDialogFragment()
                    .apply {
                        val bundle = Bundle()
                        bundle.putInt(KEY_HOUR, hour)
                        bundle.putInt(KEY_MINUTE, minute)
                        bundle.putInt(KEY_SECOND, second)
                        bundle.putBoolean(KEY_IS_24HOUR, is24Hour)
                        bundle.putBoolean(KEY_IS_AM, isAm)
                        arguments = bundle
                    }
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.df_time_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        arguments?.let {
            hour = it.getInt(KEY_HOUR, -1)
            minute = it.getInt(KEY_MINUTE, -1)
            second = it.getInt(KEY_SECOND, -1)
            is24Hour = it.getBoolean(KEY_IS_24HOUR, true)
            isAm = it.getBoolean(KEY_IS_AM, true)
        }

        if (hour==-1) {
            binding.timePicker.setTime(Calendar.getInstance(),TimePickerHelper.is24HourMode(activity!!))
        }else {
            binding.timePicker.setTime(hour, minute, second, is24Hour, isAm)
        }
    }

    override fun initListeners(savedInstanceState: Bundle?) {

        binding.timePicker.setOnTimeSelectedListener(object : OnTimeSelectedListener {
            override fun onTimeSelected(is24Hour: Boolean, hour: Int, minute: Int, second: Int, isAm: Boolean) {
                Log.d("TimePickerDF", "selectedTime:$hour-$minute-$second")
            }
        })

        binding.tvConfirm.setOnClickListener {
            getDialogListener(OnTimeSelectedListener::class.java)
                    ?.onTimeSelected(binding.timePicker.is24Hour(),
                            binding.timePicker.getSelectedHour(),
                            binding.timePicker.getSelectedMinute(),
                            binding.timePicker.getSelectedSecond(),
                            binding.timePicker.isAm())
            dismissAllowingStateLoss()
        }

        binding.tvCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}