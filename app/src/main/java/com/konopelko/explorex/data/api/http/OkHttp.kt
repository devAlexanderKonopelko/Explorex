package com.konopelko.explorex.data.api.http

import com.konopelko.explorex.BuildConfig
import com.konopelko.explorex.data.api.ApiConstants.API_KEY_QUERY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createHttpClient(): OkHttpClient =
    OkHttpClient
        .Builder()
        .addInterceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter(API_KEY_QUERY, BuildConfig.PLACES_API_KEY)
                .build()
            val request = chain.request().newBuilder().url(url).build()
            chain.proceed(request)
        }
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()