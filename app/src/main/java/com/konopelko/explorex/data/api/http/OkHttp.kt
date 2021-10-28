package com.konopelko.explorex.data.api.http

import com.konopelko.explorex.BuildConfig
import com.konopelko.explorex.data.api.ApiConstants.API_KEY_QUERY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createHttpClient(): OkHttpClient =
    OkHttpClient
        .Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            request.url.newBuilder()
                .addQueryParameter(API_KEY_QUERY, BuildConfig.PLACES_API_KEY)
                .build()
            chain.proceed(request)
        }
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()