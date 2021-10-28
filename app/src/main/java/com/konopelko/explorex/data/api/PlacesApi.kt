package com.konopelko.explorex.data.api

import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("nearbySearch/.json")
    suspend fun getNearbyPlaces(
        @Query("lat") latitude: Float,
        @Query("lon") longitude: Float,
        @Query("limit") limit: Int,
        @Query("radius") radius: Int
    ): Response<GetNearbyPlacesResponse>
}