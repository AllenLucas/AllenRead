package com.allenlucas.read

import android.app.Application
import android.content.ComponentCallbacks2
import com.bumptech.glide.Glide

class MyApp : Application() {

    companion object {
        private lateinit var instance: MyApp

        fun getInstance() = instance
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        //app 置换到后台 清空 glide缓存
        if (ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN == level){
            Glide.get(this).clearMemory()
        }
        //其他情况，glide自行处理内存
        Glide.get(this).trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // 低内存 情况glide 缓存
        Glide.get(this).clearMemory()
    }
}