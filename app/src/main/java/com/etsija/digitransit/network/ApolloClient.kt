package com.etsija.digitransit.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.StopArrDepQuery
import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.utils.Constants.Companion.BASE_URL
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

// This class creates the ApolloClient object and does the network call to the API endpoint
object ApolloClient {

    val apollo: ApolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(BASE_URL)
            .build()
    }

    suspend fun getStopsSafely(lat: Double, lon: Double, radius: Int): Response<StopsByRadiusQuery.Data>? {
        var response: Response<StopsByRadiusQuery.Data>?

        coroutineScope {
            response = try {
                apollo.query(StopsByRadiusQuery(lat, lon, radius)).await()
            } catch (e: ApolloException) {
                // Handle protocol errors
                Log.d("ApolloClient: getStopsSafely()", e.toString())
                response = null
                return@coroutineScope
            }
            if (response?.hasErrors() == true || response?.data == null) {
                // Handle other errors
                Log.d("ApolloClient: getStopsSafely()", "Other network error")
                response = null
                return@coroutineScope
            }
        }
        return response
    }

    suspend fun getDeparturesSafely(gtfsId: String): Response<StopArrDepQuery.Data>? {
        var response: Response<StopArrDepQuery.Data>?

        coroutineScope {
            response = try {
                apollo.query(StopArrDepQuery(gtfsId)).await()
            } catch (e: ApolloException) {
                // Handle protocol errors
                Log.d("ApolloClient: getDeparturesSafely()", e.toString())
                response = null
                return@coroutineScope
            }
            if (response?.hasErrors() == true || response?.data == null) {
                // Handle other errors
                Log.d("ApolloClient: getDeparturesSafely()", "Other network error")
                response = null
                return@coroutineScope
            }
        }
        return response
    }

    suspend fun getAlertsSafely(): Response<AlertsQuery.Data>? {
        var response: Response<AlertsQuery.Data>?

        coroutineScope {
            response = try {
                apollo.query(AlertsQuery()).await()
            } catch (e: ApolloException) {
                // Handle protocol errors
                Log.d("ApolloClient: getAlertsSafely()", e.toString())
                response = null
                return@coroutineScope
            }
            if (response?.hasErrors() == true || response?.data == null) {
                // Handle other errors
                Log.d("ApolloClient: getAlertsSafely()", "Other network error")
                response = null
                return@coroutineScope
            }
        }
        return response
    }

    fun getStops(lat: Double, lon: Double, radius: Int): ApolloQueryCall<StopsByRadiusQuery.Data> =
        apollo.query(StopsByRadiusQuery(lat, lon, radius))

    fun getDepartures(gtfsId: String): ApolloQueryCall<StopArrDepQuery.Data> =
        apollo.query(StopArrDepQuery(gtfsId))

    fun getAlerts(): ApolloQueryCall<AlertsQuery.Data> =
        apollo.query(AlertsQuery())
}