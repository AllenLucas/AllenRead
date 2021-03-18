package com.allenlucas.read.base.api

import retrofit2.http.GET

interface DataApi {

    @GET("blog/posts.json")
    suspend fun getNewList(): List<DataBean>
}