package com.konopelko.explorex.domain.usecase.place.getnearby

import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse

interface GetNearbyPlacesUseCase {

    suspend operator fun invoke(latitude: Float, longitude: Float): GetNearbyPlacesResponse?
}