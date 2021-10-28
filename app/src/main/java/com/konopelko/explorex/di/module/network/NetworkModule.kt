package com.konopelko.explorex.di.module.network

import com.konopelko.explorex.BuildConfig
import com.konopelko.explorex.data.api.PlacesApi
import com.konopelko.explorex.data.api.http.createApi
import com.konopelko.explorex.data.api.http.createHttpClient
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = createHttpClient()

    @Singleton
    @Provides
    @Inject
    fun provideApi(httpClient: OkHttpClient): PlacesApi =
        createApi(
            httpClient,
            BuildConfig.PLACES_API_HOST
        )
}