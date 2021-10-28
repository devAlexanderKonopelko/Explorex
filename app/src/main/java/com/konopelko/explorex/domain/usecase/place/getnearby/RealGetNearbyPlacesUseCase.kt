package com.konopelko.explorex.domain.usecase.place.getnearby

import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.domain.repostory.PlaceRepository
import javax.inject.Inject

class RealGetNearbyPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository
): GetNearbyPlacesUseCase {

    override suspend fun invoke(latitude: Float, longitude: Float): GetNearbyPlacesResponse? =
        repository.getNearbyPlaces(latitude, longitude)
}