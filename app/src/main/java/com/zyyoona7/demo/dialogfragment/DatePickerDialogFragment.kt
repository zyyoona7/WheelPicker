package com.zyyoona7.demo.dialogfragment

import android.os.Bundle
import android.util.Log
import com.zyyoona7.demo.R
import com.zyyoona7.demo.databinding.DfDatePickerBinding
import com.zyyoona7.picker.listener.OnDateSelectedListener
import java.util.*

class DatePickerDialogFragment : BaseDialogFragment<DfDatePickerBinding>() {

    private var year: Int = -1
    private var month: Int = -1
    private var day: Int = -1

    companion object {

        private const val KEY_YEAR = "KEY_YEAR"
        private const val KEY_MONTH = "KEY_MONTH"
        private const val KEY_DAY = "KEY_DAY"

        fun newInstance(year: Int, month: Int, day: Int): DatePickerDialogFragment {
            return DatePickerDialogFragment()
                    .apply {
                        val bundle = Bundle()
                        bundle.putInt(KEY_YEAR, year)
                        bundle.putInt(KEY_MONTH, month)
                        bundle.putInt(KEY_DAY, day)
                        arguments = bundle
                    }
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.df_date_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        arguments?.let {
            year = it.getInt(KEY_YEAR, -1)
            month = it.getInt(KEY_MONTH, -1)
            day = it.getInt(KEY_DAY, -1)
        }

        val calendar = Calendar.getInstance()
        if (year != -1) {
            calendar.set(Calendar.YEAR, year)
        }
        if (month != -1) {
            calendar.set(Calendar.MONTH, month - 1)
        }
        if (day != -1) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        binding.datePicker.setSelectedDate(calendar)
    }

    override fun initListeners(savedInstanceState: Bundle?) {

        binding.datePicker.setOnDateSelectedListener(object :OnDateSelectedListener{
            override fun onDateSelected(year: Int, month: Int, day: Int, date: Date) {
//                getDialogListener(OnDateSelectedListener::class.java)
//                        ?.onDateSelected(year, month, day, date)
                Log.d("DatePickerDF","selectedDate:$year-$month-$day")
            }
        })

        binding.tvConfirm.setOnClickListener {
            getDialogListener(OnDateSelectedListener::class.java)
                    ?.onDateSelected(binding.datePicker.getSelectedYear(),
                            binding.datePicker.getSelectedMonth(),
                            binding.datePicker.getSelectedDay(),
                            binding.datePicker.getSelectedDate())
            dismissAllowingStateLoss()
        }

        binding.tvCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}