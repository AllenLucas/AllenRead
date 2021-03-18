package com.allenlucas.read.base.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allenlucas.read.launch
import com.allenlucas.read.resetData
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException

/**
 * Create by AllenLucas on 2021/03/14
 * 封装ViewModel的网络请求处理
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * 网络请求封装
     */
    fun <T> requestData(
        service: suspend CoroutineScope.() -> T,
        liveData: MutableLiveData<T>,
        success: () -> Unit = {},
        error: (String) -> Unit = {}
    ) {
        launch({
            val result = async { service.invoke(this) }
            getDataSuccess(liveData, result.await(), success)
        }, { error.invoke(getHttpError(it)) })
    }

    /**
     * 网络连接的错误码
     */
    protected fun getHttpError(e: Throwable) = when (e) {
        //Http错误
        is HttpException -> "Http请求错误"
        //解析错误
        is JsonParseException, is JSONException, is ParseException -> "解析错误"
        //网络错误
        is ConnectException -> "网络连接错误"
        //请求超时
        is SocketTimeoutException -> "请求超时"
        //未知错误
        else -> "未知错误"
    }

    /**
     * 请求成功，将数据添加到liveData中
     * 并修改加载状态
     */
    private fun <T> getDataSuccess(
        liveData: MutableLiveData<T>,
        bean: T?,
        success: () -> Unit = {}
    ) {
        liveData.resetData(bean)
        success.invoke()
    }
}