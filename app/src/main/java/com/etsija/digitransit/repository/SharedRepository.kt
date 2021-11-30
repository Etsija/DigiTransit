package com.etsija.digitransit.repository

import com.apollographql.apollo.coroutines.await
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.mapper.AlertMapper
import com.etsija.digitransit.model.Alert
import com.etsija.digitransit.network.ApolloInstance

class SharedRepository {

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

}