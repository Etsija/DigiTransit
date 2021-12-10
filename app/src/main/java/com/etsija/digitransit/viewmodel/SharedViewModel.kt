package com.etsija.digitransit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Departure
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.repository.SharedRepository
import com.etsija.digitransit.utils.prefs
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

    // Departures query response
    private val _departures = MutableLiveData<List<Departure>>()
    val departures: LiveData<List<Departure>> = _departures

    private var isActive: Boolean = false

    fun init() {
        getAlerts()
        // Get stops using last known location and search parameters. This is to make
        // user experience immediate
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
            val response = repository.getStops(lat, lon, radius)
            _stops.postValue(response)
        }
    }

    // Should be called from a coroutinescope in UI layer!
    suspend fun pollStops(lat: Double, lon: Double, radius: Int) {
        val response = repository.getStops(lat, lon, radius)
        _stops.postValue(response)
    }

    // Should be called from a coroutinescope in UI layer!
    suspend fun pollDepartures(gtfsId: String) {
        val response = repository.getDepartures(gtfsId)
        _departures.postValue(response)
    }
}