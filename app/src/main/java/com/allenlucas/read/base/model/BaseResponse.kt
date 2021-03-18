package com.allenlucas.read.base.model

/**
 * Create by AllenLucas on 2021/13/14
 * 封装返回数据接口
 */
interface BaseResponse<T> {
    fun getServiceData(): T?
    fun getServiceCode(): Int
    fun getServiceMessage(): String?
}