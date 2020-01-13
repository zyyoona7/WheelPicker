package com.zyyoona7.demo.dialogfragment

import android.os.Bundle
import com.zyyoona7.demo.R
import com.zyyoona7.demo.databinding.DfDatePickerBinding

class DatePickerDialogFragment : BaseDialogFragment<DfDatePickerBinding>() {

    companion object {

        fun newInstance(): DatePickerDialogFragment {
            return DatePickerDialogFragment()
        }
    }

    override fun initLayoutId(): Int {
        return R.layout.df_date_picker
    }

    override fun initVariables(savedInstanceState: Bundle?) {
    }

    override fun initListeners(savedInstanceState: Bundle?) {
    }
}