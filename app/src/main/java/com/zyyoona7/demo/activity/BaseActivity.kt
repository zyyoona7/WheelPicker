package com.zyyoona7.demo.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, initLayoutId())
        initVariables(savedInstanceState)
        initListeners(savedInstanceState)
    }

    @LayoutRes
    abstract fun initLayoutId(): Int

    abstract fun initVariables(savedInstanceState: Bundle?)

    abstract fun initListeners(savedInstanceState: Bundle?)
}