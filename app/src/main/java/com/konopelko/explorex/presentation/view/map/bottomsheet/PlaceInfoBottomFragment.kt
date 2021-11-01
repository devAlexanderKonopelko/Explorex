package com.konopelko.explorex.presentation.view.map.bottomsheet

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konopelko.explorex.R
import com.konopelko.explorex.data.api.entity.response.place.getnearby.GetNearbyPlacesResponse
import com.konopelko.explorex.databinding.BottomSheetPlaceInfoBinding

class PlaceInfoBottomFragment(
    private val place: GetNearbyPlacesResponse.PointOfInterest,
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPlaceInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPlaceInfoBinding.inflate(inflater, container, false)
        binding.place = place
        binding.lifecycleOwner = viewLifecycleOwner
        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.placeDetailsMoreButton.setOnClickListener {
            if(place.info.url != null) {
                openPlaceWebSite()
            } else {
                openPlaceGoogleSearch()
            }
        }
    }

    private fun openPlaceWebSite() {
        val validUrl = validatePlaceUrl()
        val uri = Uri.parse(validUrl)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun validatePlaceUrl(): String {
        var validUrl = place.info.url!!
        if(!place.info.url.startsWith("http://"))
            validUrl = "http://$validUrl"
        return validUrl
    }

    private fun openPlaceGoogleSearch() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, place.info.name)
        startActivity(intent)
    }

    override fun getTheme(): Int = R.style.BaseBottomSheetTheme

    companion object {

        const val TAG = "fragment.bottom.place.info"
    }
}