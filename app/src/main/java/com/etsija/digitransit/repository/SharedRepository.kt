package com.etsija.digitransit.repository

import com.apollographql.apollo.coroutines.await
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.mapper.AlertMapper
import com.etsija.digitransit.mapper.StopMapper
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.model.Stop
import com.etsija.digitransit.network.ApolloInstance

class SharedRepository() {

    suspend fun getAlerts(): List<Alert> {
        val response = ApolloInstance.apollo
            .query(AlertsQuery())
            .await()

        val alerts = mutableListOf<Alert>()

        for (alert in response.data?.alerts!!) {
            alerts.add(AlertMapper.buildFrom(alert!!))
        }
        return alerts
    }

    suspend fun getStops(lat: Double, lon: Double, radius: Int): List<Stop> {
        val response = ApolloInstance.apollo
            .query(StopsByRadiusQuery(lat, lon, radius))
            .await()

        val stops = mutableListOf<Stop>()

        for (stop in response.data?.stopsByRadius?.edges!!) {
            stops.add(StopMapper.buildFrom(stop!!))
        }
        return stops
    }
}