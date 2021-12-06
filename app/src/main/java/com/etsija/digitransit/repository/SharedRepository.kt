package com.etsija.digitransit.repository

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.mapper.AlertMapper
import com.etsija.digitransit.mapper.StopMapper
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.network.ApolloClient
import com.etsija.digitransit.network.ApolloClient.apollo

class SharedRepository() {

    suspend fun getAlerts(): List<Alert> {

        val response = ApolloClient.getAlerts().await().data
        val alerts = mutableListOf<Alert>()

        if (response != null) {
            for (alert in response.alerts!!) {
                alerts.add(AlertMapper.buildFrom(alert!!))
            }
            return alerts
        }
        return emptyList()
    }

    suspend fun getStops(lat: Double, lon: Double, radius: Int): List<Stop> {

        val response = ApolloClient.getStops(lat, lon, radius).await().data
        val stops = mutableListOf<Stop>()

        if (response != null) {
            for (stop in response.stopsByRadius?.edges!!) {
                stops.add(StopMapper.buildFrom(stop!!))
            }
            return stops
        }
        return emptyList()
    }
}