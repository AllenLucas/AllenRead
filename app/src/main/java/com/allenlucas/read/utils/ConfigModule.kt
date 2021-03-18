package com.allenlucas.read.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Create by AllenLucas on 2021/03/17
 */
@GlideModule
class ConfigModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    }
}