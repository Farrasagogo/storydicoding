package com.taqiyuddin.storyappdicoding.submissionakhir.view.maps

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.taqiyuddin.storyappdicoding.submissionakhir.R
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityMapsBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.displayToast

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel
    private var boundsBuilder: LatLngBounds.Builder? = null
    private var lastKnownCameraPosition: CameraPosition? = null
    private var lastKnownMapType: Int? = null

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_MAP_TYPE = "map_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            lastKnownCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            lastKnownMapType = savedInstanceState.getInt(KEY_MAP_TYPE)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::mMap.isInitialized) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.cameraPosition)
            outState.putInt(KEY_MAP_TYPE, mMap.mapType)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, MapsViewModelFactory(this))[MapsViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.stories.observe(this) { stories ->
            if (::mMap.isInitialized) {
                mMap.clear()
                stories?.let { addManyMarker(it) }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configureMapSettings()
        setMapStyle()

        lastKnownMapType?.let { mMap.mapType = it }
        lastKnownCameraPosition?.let {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(it))
        }

        if (viewModel.stories.value == null) {
            viewModel.getStoriestMap()
        } else {
            viewModel.stories.value?.let { addManyMarker(it) }
        }
    }

    private fun configureMapSettings() {
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                displayToast(this, getString(R.string.map_style_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            displayToast(this, getString(R.string.can_t_find_style_error, exception.message))
        }
    }

    private fun addManyMarker(stories: List<DetailedStory>) {
        boundsBuilder = LatLngBounds.Builder()
        var hasValidLocation = false

        stories.forEach { story ->
            val lat = story.lat
            val lon = story.lon
            if (lat != null && lon != null) {
                hasValidLocation = true
                val latLng = LatLng(lat, lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder?.include(latLng)
            }
        }

        if (hasValidLocation && lastKnownCameraPosition == null) {
            adjustCameraBounds()
        }
    }

    private fun adjustCameraBounds() {
        boundsBuilder?.let { builder ->
            val bounds = builder.build()
            mMap.setOnMapLoadedCallback {
                try {
                    val padding = 100
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    mMap.animateCamera(cameraUpdate)
                } catch (e: Exception) {
                    handleCameraUpdateException(bounds)
                }
            }
        }
    }

    private fun handleCameraUpdateException(bounds: LatLngBounds) {
        try {
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width.coerceAtMost(height) * 0.15).toInt()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            mMap.animateCamera(cameraUpdate)
        } catch (e: Exception) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bounds.center))
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10f))
        }
    }
}