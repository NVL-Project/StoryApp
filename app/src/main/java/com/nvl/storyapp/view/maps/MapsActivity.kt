package com.nvl.storyapp.view.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.nvl.storyapp.R
import com.nvl.storyapp.data.server.response.StoryItem
import com.nvl.storyapp.databinding.ActivityMapsBinding
import com.nvl.storyapp.view.ViewModelFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var context: Context
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private var bearer: String = ""
    private lateinit var locationManager: LocationManager
    private var gpsStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.title_activity_maps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory.getInstance(this)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        val preferences = this.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        bearer = "Bearer " + preferences.getString("token", "").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        context = applicationContext
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mapsViewModel.getMaps(bearer).observe(this){
            it.listStory?.let { it1 -> addManyMarker(it1) }
        }
        getMyLocation()
        checkGpsStatus()
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker(stories : List<StoryItem>){
        stories.forEach {storyMap ->
            val latLng = LatLng(storyMap.lat, storyMap.lon)
            mMap.addMarker(MarkerOptions().position(latLng).title(storyMap.name))
            boundsBuilder.include(latLng)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                350
            )
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun checkGpsStatus() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS()
        } else {
            return
        }
    }
    private fun onGPS() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.enableGPS)).setCancelable(false).setPositiveButton(getString(
            R.string.yes)
        ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}