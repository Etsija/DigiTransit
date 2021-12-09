package com.etsija.digitransit.model

data class Departure(
    val scheduledDeparture: String = "",
    val realtimeDeparture: String = "",
    val realtime: Boolean = false,
    val serviceDay: String = "",
    val route: String? = "",
    val headsign: String? = null
)