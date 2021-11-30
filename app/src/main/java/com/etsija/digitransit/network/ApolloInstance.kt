package com.etsija.digitransit.network

import com.apollographql.apollo.ApolloClient
import com.etsija.digitransit.utils.Constants.Companion.BASE_URL

object ApolloInstance {

    val apollo: ApolloClient by lazy {
        ApolloClient.builder()
            .serverUrl(BASE_URL)
            .build()
    }
}