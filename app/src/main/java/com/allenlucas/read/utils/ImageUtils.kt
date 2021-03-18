package com.allenlucas.read.utils

import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Create by AllenLucas 2021/03/15
 * 图片加载类
 */
class ImageUtils private constructor() {

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ImageUtils() }
    }

    // 图片加载
    private fun imageLoad(url: String, imageView: ImageView, requestOptions: RequestOptions) {
        if (TextUtils.isEmpty(url)) return
        Glide.with(imageView).load(url)
            .apply(requestOptions).into(imageView)
    }

    /**
     * 加载图片，加载类型CenterCrop
     */
    fun imageLoad(url: String, imageView: ImageView) {
        imageLoad(
            url, imageView,
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.stat_notify_error)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(15)))
        )
    }

    /**
     * 默认加载图片，加载类型为默认
     */
    fun imageLoadDefault(url: String, imageView: ImageView) {
        imageLoad(
            url, imageView, RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.stat_notify_error)
                .transform(RoundedCorners(15))
        )
    }
}