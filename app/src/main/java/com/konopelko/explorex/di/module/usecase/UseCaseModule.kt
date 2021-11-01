package com.konopelko.explorex.di.module.usecase

import com.konopelko.explorex.domain.repostory.PlaceRepository
import com.konopelko.explorex.domain.usecase.place.getnearby.GetNearByPlacesUseCase
import com.konopelko.explorex.domain.usecase.place.getnearby.RealGetNearByPlacesUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class UseCaseModule {

    @Singleton
    @Provides
    @Inject
    fun provide(repository: PlaceRepository): GetNearByPlacesUseCase =
        RealGetNearByPlacesUseCase(repository)
}