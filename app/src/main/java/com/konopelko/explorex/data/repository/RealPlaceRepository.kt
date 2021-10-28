package com.konopelko.explorex.data.repository

import com.konopelko.explorex.data.api.ApiConstants.SEARCH_ITEM_LIMIT
import com.konopelko.explorex.data.api.ApiConstants.SEARCH_RADIUS_METERS
import com.konopelko.explorex.data.api.PlacesApi
import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.domain.repostory.PlaceRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealPlaceRepository @Inject constructor(
    private val api: PlacesApi
): PlaceRepository {

    override suspend fun getNearbyPlaces(
        latitude: Float,
        longitude: Float
    ): GetNearbyPlacesResponse? = withContext(IO) {
        val limit = SEARCH_ITEM_LIMIT
        val radius = SEARCH_RADIUS_METERS
        api.getNearbyPlaces(latitude, longitude, limit, radius).body()
    }
}