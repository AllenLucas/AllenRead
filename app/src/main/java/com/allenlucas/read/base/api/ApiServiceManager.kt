package com.allenlucas.read.base.api

import com.allenlucas.read.BuildConfig
import com.allenlucas.read.MyApp
import com.allenlucas.read.utils.NetWorkUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Create by AllenLucas on  2021/03/14
 * 网络请求管理
 */
object ApiServiceManager {

    const val BASE_URL = "https://arcblockio.cn/"

    val api by lazy { createService(DataApi::class.java) }

    private fun okHttpClick() = OkHttpClient.Builder()
        .cache(Cache(MyApp.getInstance().cacheDir, 1024 * 1024 * 10))  //缓存文件10MB
        .callTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(RequestCacheInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .addNetworkInterceptor(ResponseCacheInterceptor())
        .build()

    private fun retrofitClick(baseUrl: String) = Retrofit.Builder()
        .client(okHttpClick())
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(t: Class<T>, baseUrl: String = BASE_URL) =
        retrofitClick(baseUrl).create(t)
}

/**
 * 获取到数据后，头部添加缓存信息
 */
class ResponseCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request()).newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .addHeader("Cache-Control", CacheControl.FORCE_CACHE.toString())
            .build()
    }
}

/**
 * 请求处理缓存拦截器
 */
class RequestCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        if (!NetWorkUtils.isNetWorkAvailable()) {
            requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
        }
        return chain.proceed(requestBuilder.build())
    }
}