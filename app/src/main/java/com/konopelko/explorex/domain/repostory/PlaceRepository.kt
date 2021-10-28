package com.konopelko.explorex.domain.repostory

import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse

interface PlaceRepository {

    suspend fun getNearbyPlaces(latitude: Float, longitude: Float): GetNearbyPlacesResponse?
}