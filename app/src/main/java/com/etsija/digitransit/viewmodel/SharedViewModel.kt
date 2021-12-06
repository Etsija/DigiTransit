package com.etsija.digitransit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.repository.SharedRepository
import com.etsija.digitransit.utils.Constants
import com.etsija.digitransit.utils.Constants.Companion.ONE_SECOND
import com.etsija.digitransit.utils.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SharedViewModel(): ViewModel() {

    // Repository is declared only here, not in View layer
    private val repository = SharedRepository()

    // Alerts query response
    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> = _alerts

    // Stops query response
    private val _stops = MutableLiveData<List<Stop>>()
    val stops: LiveData<List<Stop>> = _stops

    private var isActive: Boolean = false

    fun init() {
        getAlerts()
        // Get stops using last known location and search parameters. This is to make
        // user experiance immediate
        getStops(
            prefs.lastLat!!.toDouble(),
            prefs.lastLon!!.toDouble(),
            prefs.searchRadius)
    }

    fun getAlerts() {
        viewModelScope.launch {
            val response = repository.getAlerts()
            _alerts.postValue(response)
        }
    }

    // Get the stops once every time this function is run
    fun getStops(lat: Double, lon: Double, radius: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getStops(lat, lon, radius)
                _stops.postValue(response)
            } catch (ae: ApolloException) {
                Log.d("ApolloException", "failure", ae)
            }
        }
    }

    // Should be called from a coroutinescope in UI layer!
    suspend fun pollStops(lat: Double, lon: Double, radius: Int) {
        try {
            val response = repository.getStops(lat, lon, radius)
            _stops.postValue(response)
        } catch (ae: ApolloException) {
            Log.d("ApolloException", "failure", ae)
        }
    }
}