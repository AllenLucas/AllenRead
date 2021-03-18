package com.allenlucas.read.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.allenlucas.read.utils.AppActivityManager

/**
 * Create by AllenLucas on 2021/03/14
 * 封装Activity基类
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    /**
     * 获取ViewBinding
     */
    abstract fun getViewBinding(): (inflater: LayoutInflater) -> VB

    /**
     * 初始化
     */
    abstract fun init(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor =
            ContextCompat.getColor(this@BaseActivity, android.R.color.darker_gray)
        AppActivityManager.instance.addActivity(this)
        binding = getViewBinding().invoke(layoutInflater)
        setContentView(binding.root)
        init(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppActivityManager.instance.removeActivity(this)
    }

    /**
     * 观察LiveData操作
     */
    protected fun <T> baseObserve(liveData: LiveData<T>, opera: (T) -> Unit) {
        liveData.observe(this, Observer(opera))
    }

    /**
     * 获取ViewModel
     */
    protected fun <T : ViewModel> getViewModel(clz: Class<T>) = ViewModelProvider(this).get(clz)

}