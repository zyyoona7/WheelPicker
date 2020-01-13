package com.zyyoona7.demo.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.zyyoona7.demo.R

abstract class BaseDialogFragment<VB : ViewDataBinding> : DialogFragment() {

    protected lateinit var binding: VB

    @Suppress("UNCHECKED_CAST")
    fun <T> getDialogListener(clazz: Class<T>): T? {
        return when {
            clazz.isInstance(activity) -> activity as T
            clazz.isInstance(context) -> context as T
            else -> null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog?.window?.attributes?.let {
            it.dimAmount = 0.6f
            it.gravity = Gravity.BOTTOM
            it.windowAnimations = R.style.DialogAnim
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,initLayoutId(),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables(savedInstanceState)
        initListeners(savedInstanceState)
    }

    @LayoutRes
    abstract fun initLayoutId(): Int

    abstract fun initVariables(savedInstanceState: Bundle?)

    abstract fun initListeners(savedInstanceState: Bundle?)

}