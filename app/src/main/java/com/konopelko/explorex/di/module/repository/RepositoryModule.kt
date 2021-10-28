package com.konopelko.explorex.di.module.repository

import com.konopelko.explorex.data.api.PlacesApi
import com.konopelko.explorex.data.repository.RealPlaceRepository
import com.konopelko.explorex.domain.repostory.PlaceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    @Inject
    fun provide(api: PlacesApi): PlaceRepository =
        RealPlaceRepository(api)
}