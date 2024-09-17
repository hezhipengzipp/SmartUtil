package com.clife.smartutil.memory

import retrofit2.Call
import retrofit2.http.GET

interface NetApi {
    @GET("www.baidu.com")
    fun doneSth(): Call<String>
}