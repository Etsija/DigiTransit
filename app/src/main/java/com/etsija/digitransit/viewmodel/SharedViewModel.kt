package com.etsija.digitransit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.repository.SharedRepository
import com.etsija.digitransit.utils.Constants
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

    fun init() {
        getAlerts()
        getStops(60.2068726, 24.8939462, 500)
    }

    fun getAlerts() {
        viewModelScope.launch {
            val response = repository.getAlerts()
            _alerts.postValue(response)
        }
    }

    fun getStops(lat: Double, lon: Double, radius: Int) {
        viewModelScope.launch {
            val response = repository.getStops(lat, lon, radius)
            _stops.postValue(response)
        }
    }
}