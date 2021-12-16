package com.etsija.digitransit.viewmodel

import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class LocationViewModel(application: Application): AndroidViewModel(application) {

    // LiveData for location
    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData

    // LiveData for address
    private val _address = MutableLiveData<String?>()
    val address: LiveData<String?> = _address

    // Get address based on location and post it to the UI
    fun getAddress(context: Context, lat: Double, lon: Double) {
        viewModelScope.launch {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>
            var address: String? = ""
            try {
                addresses = geoCoder.getFromLocation(lat, lon, 1)
                address = addresses[0]
                    .getAddressLine(0)
                    .toString()
                    .substringBefore(", ")
            } catch (e: Exception) {
                Log.d("getAddress() exception", e.toString())
                address = "NO KNOWN ADDRESS FOR THIS LOCATION"
            }
            _address.postValue(address)
        }
    }
}