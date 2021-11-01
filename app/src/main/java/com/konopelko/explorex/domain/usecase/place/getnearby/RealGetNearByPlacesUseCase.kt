package com.konopelko.explorex.domain.usecase.place.getnearby

import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.domain.repostory.PlaceRepository
import javax.inject.Inject

class RealGetNearByPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository
): GetNearByPlacesUseCase {

    override suspend fun invoke(latitude: Float, longitude: Float): GetNearbyPlacesResponse? =
        repository.getNearbyPlaces(latitude, longitude)
}