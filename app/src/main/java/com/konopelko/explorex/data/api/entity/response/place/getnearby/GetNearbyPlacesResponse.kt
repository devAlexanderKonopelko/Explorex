package com.konopelko.explorex.data.api.entity.response.place.getnearby

import com.google.gson.annotations.SerializedName

data class GetNearbyPlacesResponse(
    val results: List<PointOfInterest>
) {
    data class PointOfInterest(
        @SerializedName("poi")
        val info: PointOfInterestInfo,
        val position: PointPosition,
        @SerializedName("dist")
        val distance: Float
    ) {
        data class PointOfInterestInfo(
            val name: String,
            val url: String?,
            val phone: String?,
            val categories: List<String>,
        )

        data class PointPosition(
            val lat: Float,
            val lon: Float
        )
    }
}
