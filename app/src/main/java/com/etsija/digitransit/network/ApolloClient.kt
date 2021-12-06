package com.etsija.digitransit.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.etsija.digitransit.AlertsQuery
import com.etsija.digitransit.StopsByRadiusQuery
import com.etsija.digitransit.utils.Constants.Companion.BASE_URL

// This class creates the ApolloClient object and does the network call to the API endpoint
object ApolloClient {

    val apollo: ApolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(BASE_URL)
            .build()
    }

    fun getAlerts(): ApolloQueryCall<AlertsQuery.Data> =
        apollo.query(AlertsQuery())

    fun getStops(lat: Double, lon: Double, radius: Int): ApolloQueryCall<StopsByRadiusQuery.Data> =
        apollo.query(StopsByRadiusQuery(lat, lon, radius))

    private inline fun <T> safeApiCall(apicall: () -> Response<T>): DigiTransitResponse<T> {
        return try {
            DigiTransitResponse.success(apicall.invoke())
        } catch(e: Exception) {
            DigiTransitResponse.failure(e)
        }
    }
}