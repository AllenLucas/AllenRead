package com.allenlucas.read.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import com.allenlucas.read.MyApp

/**
 * Create by AllenLucas on 2021/03/15
 * 网络工具类
 */
object NetWorkUtils {

    /**
     * 网络是否可用
     */
    fun isNetWorkAvailable(): Boolean {
        val connectManager = MyApp.getInstance()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectManager.activeNetwork ?: return false
            val activeNetwork = connectManager.getNetworkCapabilities(network) ?: return false
            return when {
                //wifi网络
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or
                        //移动网络
                        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        val info = connectManager.activeNetworkInfo ?: return false
        if (info.isConnected) return info.state == NetworkInfo.State.CONNECTED
        return false

    }
}