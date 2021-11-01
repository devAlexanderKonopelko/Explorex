package com.konopelko.explorex.presentation.viewmodel.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.domain.usecase.place.getnearby.GetNearByPlacesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val getNearByPlacesUseCase: GetNearByPlacesUseCase
) : ViewModel() {

    private val uiEvent = MutableLiveData<Pair<String, Any?>>()

    var lastSavedLocation: Location? = null

    private var isDataLoading = false
    private var isMapReady = false

    private var userMarker: Marker? = null
    private var placesMarkers = listOf<Marker>()

    fun setLastUserLocation(location: Location?) {
        lastSavedLocation = location
    }

    fun setupUserMarker() {
        if(userMarker == null && lastSavedLocation != null) {
            uiEvent.value = Pair(EVENT_SETUP_USER_MARKER, lastSavedLocation)
        } else if(userMarker != null && lastSavedLocation != null) {
            updateUserPosition()
        }
    }

    fun setCameraPrimaryPosition() {
        if(lastSavedLocation != null) {
            uiEvent.value = Pair(EVENT_SETUP_PRIMARY_CAMERA, lastSavedLocation)
        }
    }

    fun loadNearByPlaces() {
        lastSavedLocation?.let {
            if (!isDataLoading && isMapReady) {
                isDataLoading = true
                viewModelScope.launch {
                    val placesResponse =
                        getNearByPlacesUseCase(it.latitude.toFloat(), it.longitude.toFloat())
                    isDataLoading = false
                    placesResponse?.let {
                        if(placesMarkers.isEmpty()) {
                            setupPlacesMarkers(it.results)
                        } else {
                            //todo: further implementation: realtime places update
//                            updatePlacesCoordinates(placesCoordinates)
                        }
                    }
                }
            }

        }
    }

    fun setUserMarkerValue(marker: Marker) {
        userMarker = marker
    }

    private fun updateUserPosition() {
        userMarker?.position = LatLng(lastSavedLocation!!.latitude, lastSavedLocation!!.longitude)
    }

    fun updateUserPosition(location: Location) {
        lastSavedLocation = location
        userMarker?.position = LatLng(location.latitude, location.longitude)
    }

    fun onUserLocationButtonClicked() {
        uiEvent.value = Pair(EVENT_UPDATE_USER_LOCATION, null)
    }

    private fun getPlacesCoordinates(response: GetNearbyPlacesResponse): List<LatLng> =
        response.results.map { pointOfInterest ->
            LatLng(
                pointOfInterest.position.lat.toDouble(),
                pointOfInterest.position.lon.toDouble()
            )
        }


    private fun setupPlacesMarkers(places: List<GetNearbyPlacesResponse.PointOfInterest>) {
        uiEvent.postValue(Pair(EVENT_PLACES_SETUP_MARKERS, places))
    }

    private fun updatePlacesCoordinates(coordinates: List<LatLng>) {
        //todo: further implementation: define update criteria (id's etc.)
        if(placesMarkers.size == coordinates.size) {
            for(i in coordinates.indices) {
                placesMarkers[i].position = coordinates[i]
            }
        }
    }

    fun setMapIsReady() {
        isMapReady = true
    }

    fun getUiEvent(): LiveData<Pair<String, Any?>> = uiEvent

    companion object {

        const val EVENT_SETUP_USER_MARKER = "event.user.marker.setup"
        const val EVENT_SETUP_PRIMARY_CAMERA = "event.camera.setup.primary"
        const val EVENT_PLACES_SETUP_MARKERS = "event.places.markers.setup"
        const val EVENT_UPDATE_USER_LOCATION = "event.user.location.update"
    }
}