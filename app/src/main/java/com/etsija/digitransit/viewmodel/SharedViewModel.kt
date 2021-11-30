package com.etsija.digitransit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.repository.SharedRepository
import kotlinx.coroutines.launch

class SharedViewModel(): ViewModel() {

    // Repository is declared only here, not in View layer
    private val repository = SharedRepository()

    // Alerts query response
    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> = _alerts

    fun init() {
        getAlerts()
    }

    fun getAlerts() {
        viewModelScope.launch {
            val response = repository.getAlerts()
            _alerts.postValue(response)
        }
    }
}