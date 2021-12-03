package com.etsija.digitransit.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import androidx.lifecycle.LiveData
import com.etsija.digitransit.network.LocationDTO
import com.etsija.digitransit.utils.Constants.Companion.ONE_MINUTE
import com.etsija.digitransit.utils.Constants.Companion.ONE_SECOND
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationLiveData(context: Context): LiveData<LocationDTO>() {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location -> location.also {
                setLocationData(it)
            }
        }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private val locationCallback = object: LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            // If no location
            locationResult ?: return

            // Location found
            for (location in locationResult.locations) {
                setLocationData(location)
            }

        }
    }

    // If we have received a location update, this function will be called
    private fun setLocationData(location: Location) {
        value = LocationDTO(location.latitude.toString(), location.longitude.toString())
    }

    override fun onInactive() {
        super.onInactive()
        // Turn off location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = ONE_SECOND*20
            fastestInterval = interval/4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}