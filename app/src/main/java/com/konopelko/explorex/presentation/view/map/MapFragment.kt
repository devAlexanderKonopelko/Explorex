package com.konopelko.explorex.presentation.view.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.konopelko.explorex.ExplorexApplication
import com.konopelko.explorex.R
import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.databinding.BottomSheetPlaceInfoBinding
import com.konopelko.explorex.databinding.FragmentMapBinding
import com.konopelko.explorex.domain.constants.location.LocationConstants.LOCATION_FETCH_INTERVAL_MILLIS
import com.konopelko.explorex.domain.constants.location.LocationConstants.LOCATION_MIN_FETCH_INTERVAL_MILLIS
import com.konopelko.explorex.presentation.base.dialog.BaseDialog
import com.konopelko.explorex.presentation.view.map.bottomsheet.PlaceInfoBottomFragment
import com.konopelko.explorex.presentation.viewmodel.map.MapViewModel
import com.konopelko.explorex.presentation.viewmodel.map.MapViewModel.Companion.EVENT_PLACES_SETUP_MARKERS
import com.konopelko.explorex.presentation.viewmodel.map.MapViewModel.Companion.EVENT_SETUP_PRIMARY_CAMERA
import com.konopelko.explorex.presentation.viewmodel.map.MapViewModel.Companion.EVENT_SETUP_USER_MARKER
import com.konopelko.explorex.presentation.viewmodel.map.MapViewModel.Companion.EVENT_UPDATE_USER_LOCATION
import com.konopelko.explorex.utils.resources.getBitmapFromResource
import javax.inject.Inject

class MapFragment : Fragment() {

    @Inject
    lateinit var viewModel: MapViewModel

    private lateinit var binding: FragmentMapBinding

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            when (activityResult.resultCode) {
                Activity.RESULT_OK -> getUserLocation()
                Activity.RESULT_CANCELED -> checkLocationSettings()
            }
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_FETCH_INTERVAL_MILLIS
        fastestInterval = LOCATION_MIN_FETCH_INTERVAL_MILLIS
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult != null) {
                for (location in locationResult.locations) {
                    viewModel.updateUserPosition(location)
                }
            }
        }
    }

    private var map: GoogleMap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as ExplorexApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleUserLocation()
        setupMap()
        observeUiEvents()
    }

    private fun handleUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (areLocationPermissionsGranted()) {
            getUserLocation()
        } else {
            showPermissionDialog()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (it.result != null) {
                viewModel.setLastUserLocation(it.result)
                viewModel.setupUserMarker()
                viewModel.setCameraPrimaryPosition()
                viewModel.loadNearByPlaces()
            } else {
                checkLocationSettings()
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            updateMapUi()
            setOnMarkerClickListener()
            viewModel.setMapIsReady()
            viewModel.loadNearByPlaces()
        }
    }

    private fun updateMapUi() {
        map?.uiSettings?.isMapToolbarEnabled = false
    }

    private fun setOnMarkerClickListener() {
        map?.setOnMarkerClickListener {
            if (it.tag is GetNearbyPlacesResponse.PointOfInterest) {
                openPlaceInfoBottomFragment(it.tag as GetNearbyPlacesResponse.PointOfInterest)
            }
            false
        }
    }

    private fun openPlaceInfoBottomFragment(place: GetNearbyPlacesResponse.PointOfInterest) {
        PlaceInfoBottomFragment(place).show(childFragmentManager, PlaceInfoBottomFragment.TAG)
    }

    private fun observeUiEvents() {
        viewModel.getUiEvent().observe(viewLifecycleOwner) {
            when (it.first) {
                EVENT_PLACES_SETUP_MARKERS -> setupPlacesMarkers(it.second as List<GetNearbyPlacesResponse.PointOfInterest>)
                EVENT_UPDATE_USER_LOCATION -> checkLocationSettings()
                EVENT_SETUP_USER_MARKER -> setupUserMarker(it.second as Location)
                EVENT_SETUP_PRIMARY_CAMERA -> setCameraPrimaryPosition(it.second as Location)
            }
        }
    }

    private fun setupPlacesMarkers(places: List<GetNearbyPlacesResponse.PointOfInterest>) {
        places.forEach { place ->
            val placeMarkerBitmap = getBitmapFromResource(resources, R.drawable.ic_marker_place)
            val placeMarker = map?.addMarker(
                MarkerOptions()
                    .position(LatLng(place.position.lat.toDouble(), place.position.lon.toDouble()))
                    .icon(BitmapDescriptorFactory.fromBitmap(placeMarkerBitmap))
            )
            placeMarker?.tag = place
        }
    }

    private fun setupUserMarker(location: Location) {
        val userMarkerIconBitmap = getBitmapFromResource(resources, R.drawable.ic_marker_user)
        val userMarkerOptions = MarkerOptions()
            .position(LatLng(location.latitude, location.longitude))
            .icon(BitmapDescriptorFactory.fromBitmap(userMarkerIconBitmap))
        val userMarker = map?.addMarker(userMarkerOptions)
        viewModel.setUserMarkerValue(userMarker!!)
    }

    private fun setCameraPrimaryPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            getUserLocation()
        }.addOnFailureListener {
            if (it is ResolvableApiException) {
                showTurnOnGpsDialog(it)
            } else {
                it.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (areLocationPermissionsGranted()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun areLocationPermissionsGranted(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showPermissionDialog() {
        val dialog =
            BaseDialog(
                getString(R.string.dialog_permissions_location_title),
                getString(R.string.dialog_permissions_location_text),
                getString(R.string.dialog_permissions_location_btn)
            ) {
                it.dismiss()
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), CODE_REQUEST_PERMISSIONS
                )
            }
        dialog.isCancelable = false
        dialog.show(childFragmentManager, BaseDialog.TAG)
    }

    private fun showTurnOnGpsDialog(exception: ResolvableApiException) {
        val dialog =
            BaseDialog(
                getString(R.string.dialog_turn_on_location_title),
                getString(R.string.dialog_turn_on_location_text),
                getString(R.string.dialog_turn_on_location_btn)
            ) {
                it.dismiss()
                resolveLocationException(exception)
            }
        dialog.isCancelable = false
        dialog.show(childFragmentManager, BaseDialog.TAG)
    }

    private fun resolveLocationException(exception: ResolvableApiException) {
        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
        activityResultLauncher.launch(intentSenderRequest)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!areLocationPermissionsGranted()) {
            showPermissionDialog()
        } else {
            getUserLocation()
        }
    }

    companion object {

        const val CODE_REQUEST_PERMISSIONS = 1
    }
}