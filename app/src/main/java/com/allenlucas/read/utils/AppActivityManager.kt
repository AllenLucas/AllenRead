package com.allenlucas.read.utils

import android.app.Activity
import java.util.*
import kotlin.system.exitProcess

/**
 * Create by AllenLucas  on  2021/03/14
 * activity任务栈管理类
 */
class AppActivityManager private constructor() {

    companion object {
        //双重校验锁式单例的kotlin实现
        val instance: AppActivityManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AppActivityManager() }
    }

    //任务栈
    private val activityStack by lazy { Stack<Activity>() }

    /**
     * activity进栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * activity出栈
     */
    fun removeActivity(activity: Activity) {
        if (!activityStack.contains(activity)) return
        activityStack.remove(activity)
    }

    /**
     * 销毁指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack.forEach {
            if (it.javaClass == cls) {
                activityStack.remove(it)
                return
            }
        }
    }

    /**
     * 清空Activity栈
     */
    fun clearActivity() {
        activityStack.forEach { it.finish() }
        activityStack.clear()
    }

    /**
     * 栈内是否存在指定类名的Activity
     */
    fun hasActivity(cls: Class<*>): Boolean {
        activityStack.forEach {
            if (it.javaClass == cls) return true
        }
        return false
    }

    /**
     * 退出程序
     */
    fun exitApp() {
        clearActivity()
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }
}