package com.konopelko.explorex.data.api.http

import com.konopelko.explorex.data.api.PlacesApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createApi(
    httpClient: OkHttpClient,
    hostUrl: String
): PlacesApi =
    Retrofit
        .Builder()
        .baseUrl(hostUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(PlacesApi::class.java)