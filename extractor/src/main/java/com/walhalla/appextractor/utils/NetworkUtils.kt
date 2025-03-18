package com.walhalla.appextractor.utils

import okhttp3.OkHttpClient

import java.util.concurrent.TimeUnit

object NetworkUtils {
    @JvmStatic
    fun makeOkhttp(): OkHttpClient {
        val httpClientBuilder= OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        //.addInterceptor(new SimpleLoggingInterceptor())

        return httpClientBuilder.build()
    }
}