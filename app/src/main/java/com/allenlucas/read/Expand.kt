package com.allenlucas.read

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.allenlucas.read.base.ui.BaseViewHolder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 将base64 转为bitmap
 */
fun String.base642Bitmap(): Bitmap? {
    val base64String = this.split(",")
    if (base64String.size <= 1) return null
    val decodedString = Base64.decode(base64String[1], Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

/**
 * 获取baseViewHolder
 */
fun <T : ViewBinding> ViewGroup.getViewHolder(
    creator: (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> T
): BaseViewHolder<T> = BaseViewHolder(this, creator)

/**
 * 判断线程是否是在主线程
 */
fun Thread.isMainThread() = Looper.getMainLooper().thread.id == this.id

/**
 * mutableLiveData更新数据
 * 如果主线程使用setValue，其他线程使用postValue
 */
fun <T> MutableLiveData<T>.resetData(bean: T?) {
    if (Thread.currentThread().isMainThread()) {
        value = bean
        return
    }
    postValue(bean)
}

/**
 * ViewModel+协程
 */
fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit,
    onError: (e: Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    viewModelScope.launch(CoroutineExceptionHandler { _, e -> onError.invoke(e) }) {
        try {
            block.invoke(this)
        } finally {
            onComplete.invoke()
        }
    }
}

/**
 * 扩展函数，自定义点击事件，防止快速重复点击
 */
fun View.onSingleClick(onClick: (View) -> Unit, delayMillis: Long = 1000) {
    val lastClickTime = getTag(R.id.tag_click_time)
    val newClickTime = System.currentTimeMillis()
    if (null != lastClickTime) {
        val delayTime = (lastClickTime as Long) - newClickTime
        //两次点击间隔时间小于设置的间隔时间，拦截
        if (delayTime < delayMillis) {
            setTag(R.id.tag_click_time, newClickTime)
            return
        }
    }
    setOnClickListener {
        setTag(R.id.tag_click_time, newClickTime)
        onClick.invoke(this)
    }
}